package io.github.thinwind.clusterhouse.proxy;

import java.io.IOException;
import java.io.PrintWriter;
import org.apache.http.HttpHost;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import io.github.thinwind.clusterhouse.dto.HttpResult;

public class ProxyFilter implements Filter {

    private final ObjectMapper objectMapper;

    private final NamingService namingService;

    public ProxyFilter(ObjectMapper objectMapper, NamingService namingService) {
        this.objectMapper = objectMapper;
        this.namingService = namingService;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        //处理完成，判断是否需要添加注册的信息
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String serviceName = request.getServerName();
        //查找可用服务
        Instance instance;
        try {
            instance = namingService.selectOneHealthyInstance(serviceName);
        } catch (Exception e) {
            e.printStackTrace();
            instance = null;
        }
        
        if (instance == null) {
            writeError(response, HttpStatus.NOT_FOUND, "服务不存在或者无可用实例");
            return;
        }
        
        HttpHost host = new HttpHost(instance.getIp(),instance.getPort());
        request.setAttribute(ProxyServlet.ATTR_TARGET_HOST, host);
        chain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response, HttpStatus status, String msg)
            throws IOException {
        HttpResult result = new HttpResult();
        result.setSuccess(false);
        result.setErrorCode(String.valueOf(status.value()));
        result.setErrorMessage(msg);
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        objectMapper.writeValue(out, result);
    }
}
