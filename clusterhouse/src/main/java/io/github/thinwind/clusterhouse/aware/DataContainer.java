package io.github.thinwind.clusterhouse.aware;

import io.github.thinwind.clusterhouse.config.EnvConfig;

public class DataContainer {

    public static final String NOT_SET = "NOT-SET";

    private DataContainer() {
    }

    /**
     * 存储客户端相关的数据
     * 
     * 一般情况，请勿直接使用此对象
     * <p>
     * 目前存储的信息有 traceId和clientIp <br>
     * 分布如下:<br>
     * [traceId][clientIp]
     */
    @SuppressWarnings("all")
    final static ThreadLocal<String[]> CLIENT_DATA_CONTAINER = new ThreadLocal() {
        @Override
        protected String[] initialValue() {
            return new String[] {NOT_SET, NOT_SET};
        }
    };

    /**
     * 当前服务的host值
     * 
     * @see EnvConfig#host
     */
    static String host;

    public static void setHost(String host) {
        DataContainer.host = host;
    }
}
