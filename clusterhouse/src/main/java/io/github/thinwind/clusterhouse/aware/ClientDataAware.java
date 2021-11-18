package io.github.thinwind.clusterhouse.aware;

/**
 *
 * 用于TraceId存储的接口
 * 
 * 继承/实现此接口，应该只调用<i>getTraceId</i>方法 <br>
 * 禁止对TRACEID_CONTAINER进行任何操作
 *
 * @author ShangYh <shangyehua@ebchinatech.com>
 * @since 2020-09-02 23:44
 *
 */
public interface ClientDataAware {

    /**
     * 获取当前请求的 TraceId
     * 
     * @return 如果请求携带请求id，则返回；<br />
     *         否则直接重新生成
     */
    default String getTraceId() {
        return DataContainer.CLIENT_DATA_CONTAINER.get()[0];
    }

    /**
     * 获取当前请求端的ip
     * 
     * @return 请求的客户端的ip地址<br>
     *         如果获取不到，返回 resolve_failed
     */
    default String getClientIp() {
        return DataContainer.CLIENT_DATA_CONTAINER.get()[1];
    }
}
