package io.github.thinwind.ebnas.provider1;

import java.util.Properties;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.client.utils.NetUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);

    }

    @Bean
    public CommandLineRunner register(@Value("${ebnas.server-addr}")String serverAddr,@Value("${server.port}")int port) {
        return args -> {
            try {
                Properties properties = new Properties();
                properties.setProperty("serverAddr", serverAddr);
                // properties.setProperty("namespace", "ebnas");
                NamingService naming = NamingFactory.createNamingService(properties);
                naming.registerInstance("provider1", NetUtil.localIP(), port);
                System.out.println(naming.getAllInstances("provider1"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
