package cn.lang.spring.framework;

import cn.lang.spring.framework.context.MyApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MySpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyApplicationContext.class, args);
    }

}
