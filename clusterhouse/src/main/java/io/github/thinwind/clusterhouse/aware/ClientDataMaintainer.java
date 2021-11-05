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
public interface ClientDataMaintainer {

    default void setClientData(String[] clientData){
        DataContainer.CLIENT_DATA_CONTAINER.set(clientData);
    }
    
    default void removeClientData(){
        DataContainer.CLIENT_DATA_CONTAINER.remove();
    }

}
