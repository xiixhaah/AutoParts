package com.autopartsretrieval.autoparts;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 添加下面这行代码，指向你真正的 mapper 所在包！
@MapperScan("com.autopartsretrieval.autoparts.mapper")
public class AutopartsRetrievalApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutopartsRetrievalApplication.class, args);
    }

}
