package tech.fanpu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableJpaAuditing
@EnableAutoConfiguration
@ComponentScan("tech.fanpu")
public class Server {

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }

}
