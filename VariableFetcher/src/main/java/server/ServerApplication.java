package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(scanBasePackages = {"server", "shared"})
@Configuration
@ImportResource({"classpath*:applicationContext.xml"})
@EnableAsync
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
