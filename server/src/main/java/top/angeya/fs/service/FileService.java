package top.angeya.fs.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.angeya.fs.constant.Constants;
import top.angeya.fs.pojo.FileInfo;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
     * 临时文本
     */
    private String tempText;

    /**
     * 登录
     * 系统密码 = （月份 + 密码偏移） + （当月日期 + 密码偏移） + (小时 * 2)
     * @param password 密码
     * @return 是否登录成功
     */
    public boolean login(HttpServletRequest request, String password) {
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
        if (tempText.length() > Constants.TEMP_TEXT_MAX_LENGTH) {
            this.tempText = tempText.substring(0, Constants.TEMP_TEXT_MAX_LENGTH);
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
        String name = file.getOriginalFilename();
        assert name != null;
        try {
            if (!Files.isDirectory(directory)) {
                Files.createDirectory(directory);
            }
            Path filePath = Paths.get(this.fileDirectory, file.getOriginalFilename());
            Files.createFile(filePath);
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            log.error("Save file error", e);
            result = false;
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
        return fileInfoList.stream().sorted(Comparator.comparing(FileInfo::getDateTime)).collect(Collectors.toList());
    }

}
