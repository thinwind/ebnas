package io.github.thinwind.ebnas.consumer;

import java.util.Properties;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.client.naming.NamingServiceWrapper;
import com.alibaba.nacos.client.utils.NetUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @Bean
    public CommandLineRunner register(@Value("${ebnas.local-cluster.address}") String localClusterAddr,@Value("${server.port}")int port) {
        return args -> {
            try {
                Properties properties = new Properties();
                properties.setProperty("serverAddr", localClusterAddr);
                // properties.setProperty("namespace", "ebnas");
                NamingService naming = NamingFactory.createNamingService(properties);
                naming.registerInstance("consumer1", NetUtil.localIP(), port);
                System.out.println(naming.getAllInstances("provider1"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
    
    @Bean
    public NamingService initNaming(@Value("${ebnas.local-cluster.name}") String localClusterName,
            @Value("${ebnas.local-cluster.address}") String localClusterAddr,
            @Value("${ebnas.clusterhouse}") String clusterhouseAddr) {
        return new NamingServiceWrapper(localClusterName, localClusterAddr, clusterhouseAddr);
    }
}
