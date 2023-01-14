package top.angeya.fs.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.angeya.fs.service.FileService;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Angeya
 * @createTime 2023/1/14 21:32
 */
@RestController
@RequestMapping("/file-service")
@AllArgsConstructor
public class FileController {

    /**
     * 文件服务
     */
    private final FileService fileService;

    @PostMapping("/login")
    public boolean login(ServletRequest request, @RequestParam String password) {
        return this.fileService.login((HttpServletRequest)request, password);
    }

    @PutMapping("/temp-text")
    public boolean saveTempText(String tempText) {
        return this.fileService.saveTempText(tempText);
    }

    @GetMapping("/temp-text")
    public String getTempText() {
        return this.fileService.getTempText();
    }

    @PostMapping("/upload")
    public String upload(MultipartFile file) throws IOException {
        String name = file.getOriginalFilename();
        File f = new File(name);
        try(OutputStream outputStream = new FileOutputStream(f)) {
            outputStream.write(file.getBytes());
            outputStream.flush();
        }
        f.createNewFile();
        System.out.println(name);
        return name;
    }

}
