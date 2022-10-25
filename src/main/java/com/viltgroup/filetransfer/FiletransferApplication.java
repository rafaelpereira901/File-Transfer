package com.viltgroup.filetransfer;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.viltgroup.filetransfer.service.FilesStorageService;

@SpringBootApplication
public class FiletransferApplication implements CommandLineRunner {

	@Resource
	FilesStorageService storageService;

	public static void main(String[] args) {
		SpringApplication.run(FiletransferApplication.class, args);
	}

	@Override
	public void run(String... arg) throws Exception {
		storageService.init();
	}

}
