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
        boolean isLogin;

        if (uri.matches("^.*(.js|.css|.font|.svg|.woff|.ttf|.ico|.html)$") || uri.matches("/")) {
            filterChain.doFilter(servletRequest, servletResponse);
        }

        // 判断是否登录
        if (uri.contains(Constants.HAS_LOGIN_URI)) {
            Boolean hasLogin = hasSession(httpRequest)? Boolean.TRUE : Boolean.FALSE;
            servletResponse.getWriter().write(hasLogin.toString());
            return;
        }

        if (uri.contains(Constants.LOGIN_URI)) {
            isLogin = true;
        } else {
            isLogin = this.hasSession(httpRequest);
        }
        if (isLogin) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            servletResponse.getWriter().write(Constants.GO_LOGIN);
        }
    }

    private boolean hasSession(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession();
        Object object = session.getAttribute(Constants.SESSION_LOGIN_KEY);
        return object != null;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}
