package io.github.thinwind.clusterhouse.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import io.github.thinwind.clusterhouse.aware.DataContainer;
import io.github.thinwind.clusterhouse.misc.Consts;
import io.github.thinwind.clusterhouse.misc.NetUtil;

@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Configuration
public class AppConfig {

    @Autowired
    private Environment env;

    @Value("${"+Consts.HOST_KEY+":null}")
    private String host;

    @Bean
    public EnvConfig configEvn() {
        EnvConfig config = new EnvConfig();
        String curHost = env.getProperty(Consts.HOST_KEY);
        if (StringUtils.isBlank(curHost) || "null".equalsIgnoreCase(curHost)) {
            curHost = host;
        }
        if (StringUtils.isBlank(curHost) || "null".equalsIgnoreCase(curHost)) {
            curHost = NetUtil.localIP();
        }
        config.setHost(curHost);
        DataContainer.setHost(curHost);
        return config;
    }
}
