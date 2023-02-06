package top.angeya.fs.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.angeya.fs.pojo.FileInfo;
import top.angeya.fs.pojo.TempText;
import top.angeya.fs.service.FileService;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Angeya
 * @createTime 2023/1/14 21:32
 */
@RestController
@RequestMapping("file-service")
@AllArgsConstructor
public class FileController {

    /**
     * 文件服务
     */
    private final FileService fileService;

    /**
     * 登录
     * @param request 请求
     * @param password 密码
     * @return 登录结果
     */
    @PostMapping("/login")
    public boolean login(ServletRequest request, @RequestBody String password) {
        // 使用post直接传字符串，参数会有多余的等号
        return this.fileService.login((HttpServletRequest)request, password.replace("=", ""));
    }

    /**
     * 保存临时文本
     * @param tempText 临时文本
     * @return 保存结果
     */
    @PutMapping("/temp-text")
    public boolean saveTempText(@RequestBody TempText tempText) {
        return this.fileService.saveTempText(tempText.getContent());
    }

    /**
     * 获取临时文本
     * @return 文本
     */
    @GetMapping("/temp-text")
    public String getTempText() {
        return this.fileService.getTempText();
    }

    /**
     * 上传文件
     * @param file 文件
     * @return 上传结果
     */
    @PostMapping("/upload")
    public boolean upload(MultipartFile file) {
        return fileService.saveFile(file);
    }


    @GetMapping("/download-file")
    public void downloadFile(String fileName, HttpServletResponse response) {
        this.fileService.downloadFile(fileName, response);
    }

    /**
     * 删除文件
     * @param fileName 文件名
     * @return 删除结果
     */
    @DeleteMapping("/delete-file")
    public boolean deleteFile(String fileName) {
        return this.fileService.deleteFile(fileName);
    }

    /**
     * 获取文件列表
     * @return 文件列表
     */
    @GetMapping("/file-list")
    public List<FileInfo> getFileList() {
        return this.fileService.getFileList();
    }

}
