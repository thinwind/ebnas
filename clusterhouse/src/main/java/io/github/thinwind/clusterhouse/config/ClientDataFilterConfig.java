package io.github.thinwind.clusterhouse.config;

import java.util.Collections;
import javax.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 *
 * TraceId设置filter
 *
 * @author ShangYh <shangyehua@ebchinatech.com>
 * @since 2020-09-02 17:52
 *
 */
@Configuration
public class ClientDataFilterConfig {

    @Bean
    public ClientDataFilter clientDataFilter(EnvConfig envConfig) {
        return new ClientDataFilter(envConfig);
    }

    @Bean
    public FilterRegistrationBean<? extends Filter> traceIdRegistrationBean(
            ClientDataFilter clientDataFilter) {
        FilterRegistrationBean<ClientDataFilter> filterRegistrationBean =
                new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(clientDataFilter);
        filterRegistrationBean.setName("clientDataFilter");
        filterRegistrationBean.setUrlPatterns(Collections.singletonList("/*"));
        // 最高优先级，请求先添加客户端相关的信息
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegistrationBean;
    }
}
