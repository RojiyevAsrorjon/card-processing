package uz.demo.card_procession_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CardProcessionAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardProcessionAppApplication.class, args);
    }

}
