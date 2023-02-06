package top.angeya.fs.filter;

import org.springframework.stereotype.Component;
import top.angeya.fs.constant.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 判断是否登录过滤器
 * @author Angeya
 * @createTime 2023/1/14 22:45
 */
@Component
public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)servletRequest;
        String uri = httpRequest.getRequestURI();
        boolean isLogin = false;
        if (uri.contains(Constants.LOGIN_URI)) {
            isLogin = true;
        } else {
            HttpSession session = httpRequest.getSession();
            Object object = session.getAttribute(Constants.SESSION_LOGIN_KEY);
            if (object != null) {
                isLogin = true;
            }
        }
        if (isLogin) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            servletResponse.getWriter().write(Constants.GO_LOGIN);
        }
    }
}
