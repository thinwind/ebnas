package io.github.thinwind.clusterhouse.config;

import java.util.concurrent.atomic.AtomicLong;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
/**
 *
 * 服务器的一些环境设置
 *
 * @author ShangYh <shangyehua@ebchinatech.com>
 * @since 2020-09-22 16:25
 *
 */
public class EnvConfig {

    /**
     * 当前服务的host值
     * 
     * <p>
     * 
     * 用于表示当前的响应结果来自哪个服务，在服务多节点部署时方便跟踪。<br />
     * 可以通过在<code>application.yml</code>中添加<code>pms.host</code>来设置，或者通过环境变量 <br />
     * <code>pms.host</code>方式指定。如果同时指定，会优先使用环境变量的值，在<code>docker</code>方式部署时，<br />
     * 方便通过<code>Dockerfile</code>或者脚本的方式进行区分。<br />
     * 如果没有指定，会默认使用当前服务所在机器的<code>IPv4</code>值。
     */
    private String host;

    private static final AtomicLong FAKE_TRACE_ID_GENERATOR =
            new AtomicLong(System.currentTimeMillis());

    public String createTraceId() {
        return String.valueOf(FAKE_TRACE_ID_GENERATOR.getAndIncrement()) + "@" + host;
    }
}
