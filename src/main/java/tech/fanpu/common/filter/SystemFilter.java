package tech.fanpu.common.filter;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.fanpu.common.config.ApplicationConfig;
import tech.fanpu.common.util.SystemUtil;

@Component
public class SystemFilter implements Filter{
  final Logger logger = LoggerFactory.getLogger(this.getClass());
  @Autowired
  ApplicationConfig config;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    logger.info("System init ....");
  }

  public void doFilter(ServletRequest request, ServletResponse response,
                       FilterChain chain) throws IOException, ServletException {
    Date beginDate = new Date();
    HttpServletRequest req = (HttpServletRequest)request;
    String url = req.getServletPath();

    chain.doFilter(request, response);
    StringBuffer stb = new StringBuffer();
    @SuppressWarnings("rawtypes")
    Enumeration enu=request.getParameterNames();
    while (enu.hasMoreElements()) {
      String key = (String) enu.nextElement();
      String value = request.getParameter(key);
      stb.append(key + "=" + value + "&");
    }
    logger.info(String.format("%s %s URL: %s %sms params:%s", SystemUtil.getClientIP(req), req.getMethod(), url, new Date().getTime() - beginDate.getTime(), stb.toString()));
  }

  @Override
  public void destroy() {
    logger.info("System destroy ....");
  }

}