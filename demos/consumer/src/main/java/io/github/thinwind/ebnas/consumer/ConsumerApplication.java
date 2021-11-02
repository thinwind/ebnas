package io.github.thinwind.ebnas.consumer;

import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.client.naming.MultiClusterNamingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @Bean
    public NamingService initNaming(@Value("${ebnas.local-server-addr}") String serverAddr,
            @Value("${ebnas.remote-server-addr}") String remoteServerAddr) {
        return new MultiClusterNamingService(serverAddr, remoteServerAddr);
    }
}
