package io.github.thinwind.ebnas.provider2;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.client.utils.NetUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Provider2Application {

    public static void main(String[] args) {
        SpringApplication.run(Provider2Application.class, args);

    }

    @Bean
    public CommandLineRunner register(@Value("${ebnas.server-addr}") String serverAddr,
            @Value("${server.port}") int port, @Value("${ebnas.local-ip:}") String localIP) {
        return args -> {
            try {
                Properties properties = new Properties();
                properties.setProperty("serverAddr", serverAddr);
                // properties.setProperty("namespace", "ebnas");
                NamingService naming = NamingFactory.createNamingService(properties);
                String ip;
                if(StringUtils.isBlank(localIP)){
                    ip =  NetUtil.localIP();
                }else{
                    ip = localIP;
                }
                naming.registerInstance("loan-recommendation", ip, port);
                System.out.println(naming.getAllInstances("loan-recommendation"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
