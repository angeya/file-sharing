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
     * 登录接口
     */
    public static final String LOGIN_URI = "file-service/login";

    /**
     * 判断是否登录接口
     */
    public static final String HAS_LOGIN_URI = "file-service/has-login";

    /**
     * 需要登录时的返回值
     */
    public static final String GO_LOGIN = "goLogin";

    /**
     * 文件重名副本后缀
     */
    public static final String DUPLICATE_FILE_NAME_SUFFIX = "(1)";



}
