package org.vdt.qlch.documentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {"org.vdt.qlch.documentservice", "org.vdt.commonlib"}
)
public class DocumentServiceApplication{

    public static void main(String[] args) {
        SpringApplication.run(DocumentServiceApplication.class, args);
    }

}
