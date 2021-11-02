package io.github.thinwind.ebnas.consumer;

import java.util.Properties;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
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
    public NamingService initNaming(@Value("${ebnas.server-addr}")String serverAddr,@Value("${server.port}")int port) {
         try {
            Properties properties = new Properties();
            properties.setProperty("serverAddr", serverAddr);
            // properties.setProperty("namespace", "ebnas");
            NamingService naming = NamingFactory.createNamingService(properties);
            return naming;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
