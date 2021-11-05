package io.github.thinwind.clusterhouse.web;

import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import io.github.thinwind.clusterhouse.aware.ClientDataMaintainer;
import io.github.thinwind.clusterhouse.config.EnvConfig;
import io.github.thinwind.clusterhouse.misc.Consts;
import io.github.thinwind.clusterhouse.misc.NetUtil;

/**
 *
 * TraceId过滤器
 * 
 * 自动从request查询参数/Header中获取traceId，并设置到当前线程 调用完成后，清理掉缓存的traceId
 * 
 * @author ShangYh <shangyehua@ebchinatech.com>
 * @since 2020-09-02 23:25
 *
 */
public class ClientDataFilter implements Filter, ClientDataMaintainer {

    private final EnvConfig envConfig;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String traceId = request.getHeader(Consts.TRACE_ID_HEADER_KEY);
        if (StringUtils.isBlank(traceId)) {
            traceId = request.getParameter(Consts.TRACE_ID_KEY);
        }
        if (StringUtils.isBlank(traceId)) {
            traceId = String.valueOf(envConfig.createTraceId());
        }
        String clientIp = NetUtil.getClientIp(request);
        String[] clientData = {traceId, clientIp};
        setClientData(clientData);
        request.setAttribute(Consts.TRACE_ID_KEY, traceId);
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader(Consts.TRACE_ID_HEADER_KEY, traceId);
        try {
            chain.doFilter(servletRequest, servletResponse);
        } finally {
            removeClientData();
        }
    }

    public ClientDataFilter(EnvConfig envConfig) {
        this.envConfig = envConfig;
    }

}
