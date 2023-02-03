package top.angeya.fs.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件信息
 * @author Angeya
 * @createTime 2023/2/3 21:46
 */
@Data
public class FileInfo {

    /**
     * 文件名
     */
    private String name;

    /**
     * 文件大小
     */
    private long size;

    /**
     * 上传时间
     */
    private LocalDateTime dateTime;

}
