package top.angeya.fs.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.angeya.fs.constant.Constants;
import top.angeya.fs.pojo.FileInfo;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Angeya
 * @createTime 2023/1/14 21:32
 */
@Service
@Slf4j
public class FileService {

    /**
     * 密码偏移
     */
    @Value("${app.password.offset}")
    private int passwordOffset;

    /**
     * 文件目录
     */
    @Value("${app.file.dir}")
    private String fileDirectory;

    /**
     * 文件最大数量
     */
    @Value("${app.file.max-number}")
    public int fileMaxNumber;

    /**
     * 所有文件最大容量
     */
    @Value("${app.file.max-total-size}")
    public long fileMaxTotalSize;

    /**
     * 长期密码
     */
    @Value("${app.password.long-term}")
    private String longTermPassword;

    /**
     * 临时文件最大长度
     */
    @Value("${app.temp-text.max-length}")
    private Integer tempTextMaxLength;

    /**
     * 临时文本
     */
    private String tempText;

    /**
     * 登录，优先使用长期密码匹配
     * 系统密码 = （月份 + 密码偏移） + （当月日期 + 密码偏移） + (小时 * 2)
     * @param password 密码
     * @return 是否登录成功
     */
    public boolean login(HttpServletRequest request, String password) {
        if (longTermPassword.equals(password)) {
            request.getSession().setAttribute(Constants.SESSION_LOGIN_KEY, true);
            log.info("long-term user login ip: {}, result: {}", request.getLocalAddr(), true);
            return true;
        }
        LocalDateTime localDateTime = LocalDateTime.now();
        int month = localDateTime.getMonthValue();
        int day = localDateTime.getDayOfMonth();
        int hour = localDateTime.getHour();
        String todayPassword = "" + (month + passwordOffset) + (day + passwordOffset) + (hour * 2);
        boolean isOk = todayPassword.equals(password);
        if (isOk) {
            request.getSession().setAttribute(Constants.SESSION_LOGIN_KEY, true);
        }
        log.info("login ip: {}, password: {}, result: {}", request.getLocalAddr(), password, isOk);
        return isOk;
    }

    /**
     * 获取临时文本
     * @return 临时文本
     */
    public String getTempText() {
         return this.tempText;
    }

    /**
     * 保存临时文本
     * @param tempText 临时文本
     * @return 是否保存成功
     */
    public boolean saveTempText(String tempText) {
        if (tempText == null) {
            return false;
        }
        if (tempText.length() > this.tempTextMaxLength) {
            this.tempText = tempText.substring(0, this.tempTextMaxLength);
        } else {
            this.tempText = tempText;
        }

        return true;
    }

    /**
     * 保存文件
     * @param file 文件
     * @return 保存结果
     */
    public boolean saveFile(MultipartFile file) {
        boolean result = true;
        Path directory = Paths.get(this.fileDirectory);
        Path filePath = Paths.get(this.fileDirectory, file.getOriginalFilename());

        // 文件重名则加上后缀重命名
        while (Files.exists(filePath)) {
            String oldName = filePath.getFileName().toString();
            String newName = oldName.substring(0, oldName.lastIndexOf("."))
                    + Constants.DUPLICATE_FILE_NAME_SUFFIX + oldName.substring(oldName.lastIndexOf("."));
            filePath = Paths.get(filePath.getParent().toString(), newName);
        }
        try {
            if (!Files.isDirectory(directory)) {
                Files.createDirectory(directory);
            }
            // 判断文件个数是否超限
            if (Files.list(directory).count() >= this.fileMaxNumber) {
                log.info("File max number is : {}", this.fileMaxNumber);
                return false;
            }
            // 判断所有文件大小是否超限
            AtomicLong filesTotalSize = new AtomicLong(0);
            Files.list(directory).forEach(path -> {
                try {
                    filesTotalSize.accumulateAndGet(Files.size(path), Long::sum);
                } catch (IOException e) {
                    log.warn("Get file size error, path is {}", path, e);
                }
            });
            if (filesTotalSize.get() >= this.fileMaxTotalSize) {
                log.info("Files total size is  {}, now is {} ", this.fileMaxTotalSize, filesTotalSize);
                return false;
            }
            Files.createFile(filePath);
            Files.write(filePath, file.getBytes());
            log.info("Save file {} success", filePath.getFileName());
        } catch (IOException e) {
            log.error("Save file error", e);
            result = false;
        }
        return result;
    }

    public void downloadFile(String fileName, HttpServletResponse response) {
        Path path = Paths.get(this.fileDirectory, fileName);
        // 读到流中
        try (InputStream inputStream = new FileInputStream(path.toString())) {
            response.reset();
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] b = new byte[1024];
            int len;
            //从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
            while ((len = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, len);
            }
        } catch (Exception e) {
            log.error("Download file error", e);
        }
    }

    /**
     * 删除文件
     * @param fileName 文件名
     * @return 删除结果
     */
    public boolean deleteFile(String fileName) {
        Path path = Paths.get(this.fileDirectory, fileName);
        boolean result = true;
        try {
            Files.deleteIfExists(path);
            log.info("Delete file {} success", fileName);
        } catch (IOException e) {
            result = false;
            log.error("Delete file error", e);
        }
        return result;
    }

    /**
     * 获取文件信息列表
     * @return 时间降序列表数据
     */
    public List<FileInfo> getFileList() {
        List<FileInfo> fileInfoList = new ArrayList<>();
        Path fileDir = Paths.get(this.fileDirectory);

        try(Stream<Path> pathStream  = Files.list(fileDir)) {
            pathStream.filter(Files::isRegularFile).forEach(path -> {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setName(path.getFileName().toString());
                try {
                    fileInfo.setSize(Files.size(path));
                    fileInfo.setDateTime(LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneId.systemDefault()));
                } catch (IOException e) {
                    log.warn("Get file info error, path is: {}", path, e);
                }
                fileInfoList.add(fileInfo);
            });
        } catch (IOException e) {
            log.warn("Read file directory error, path is : {}", fileDir, e);
        }
        return fileInfoList.stream().
                sorted((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()))
                .collect(Collectors.toList());
    }
}
