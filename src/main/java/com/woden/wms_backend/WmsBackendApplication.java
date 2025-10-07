package com.woden.wms_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.woden.wms_backend.models")
public class WmsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WmsBackendApplication.class, args);
	}


}
