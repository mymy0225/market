package com.example.market;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MarketApplication {

	public static void main(String[] args) {
		Path path = Paths.get("java.txt");
		 
        //ファイルパスを取得する
        String str = path.toAbsolutePath().toString();
        
        System.out.println("pass : " + str);
		SpringApplication.run(MarketApplication.class, args);
	}

}
