package cn.qfei.connect.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        res.addHeader("Access-Control-Allow-Credentials", "true");
        res.addHeader("Access-Control-Allow-Origin", "*");
        res.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        res.addHeader("Access-Control-Allow-Headers",
                "Content-Type,X-CAF-Authorization-Token,sessionToken,X-TOKEN, Origin, X-Requested-With, Accept");
        if (((HttpServletRequest) request).getMethod().equals("OPTIONS")) {
            response.getWriter().println("ok");
            return;
        }

        try {
            String connection = ((HttpServletRequest) request).getHeader("Connection");
            if (StringUtils.isNotBlank(connection) && connection.equalsIgnoreCase("keep-alive")) {
                String keepAliveTime = ((HttpServletRequest) request).getHeader("keepAliveTime");
                res.addHeader("keep-alive", StringUtils.isNotBlank(keepAliveTime) ? "timeout=" + keepAliveTime : "timeout=10");
                res.addHeader("test", "t");
            }
        } catch (Exception e) {
            log.error(String.format("设置response header 异常 : %s", ExceptionUtils.getStackTrace(e)));
        }


        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
