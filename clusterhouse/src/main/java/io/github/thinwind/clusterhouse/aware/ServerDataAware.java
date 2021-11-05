package io.github.thinwind.clusterhouse.aware;

/**
 *
 * 获取服务的一些常用信息
 *
 * @author ShangYh <shangyehua@ebchinatech.com>
 * @since 2020-09-22 15:43
 *
 */
public interface ServerDataAware {

    default String getHost() {
        return DataContainer.host;
    }
}
