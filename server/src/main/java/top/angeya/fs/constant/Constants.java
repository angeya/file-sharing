package top.angeya.fs.constant;

/**
 * 常量
 * @author Angeya
 * @createTime 2023/1/14 22:36
 */
public class Constants {

    private Constants() {}

    /**
     * session判断是否登录的key
     */
    public static final String SESSION_LOGIN_KEY = "login";

    /**
     *
     */
    public static final String LOGIN_URI = "file-service/login";

    /**
     * 临时文本最大长度
     */
    public static final int TEMP_TEXT_MAX_LENGTH = 1000;

    /**
     * 文件重名副本后缀
     */
    public static final String DUPLICATE_FILE_NAME_SUFFIX = "(1)";



}
