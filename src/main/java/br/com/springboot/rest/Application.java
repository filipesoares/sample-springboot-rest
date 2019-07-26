package br.com.springboot.rest;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import br.com.springboot.rest.model.User;
import br.com.springboot.rest.repository.UserRepository;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(UserRepository repository) {
    	final String password = "123456";
        return args -> {
        	repository.save(new User("Jhon Doe", true, "jhon", password, "jhon@email.com", null, LocalDateTime.now()));
        	repository.save(new User("Sarah James", true, "sarah", password, "sarah@email.com", null, LocalDateTime.now()));
        	repository.save(new User("Aaron Field", true, "aaron", password, "aaron@email.com", null, LocalDateTime.now()));
        };
    }

}