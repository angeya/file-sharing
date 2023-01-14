package top.angeya.fs.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.angeya.fs.constant.Constants;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

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
        if (tempText.length() > Constants.TEMP_TEXT_MAX_LENGTH) {
            this.tempText = tempText.substring(0, Constants.TEMP_TEXT_MAX_LENGTH);
        } else {
            this.tempText = tempText;
        }

        return true;
    }




}
