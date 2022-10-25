package com.viltgroup.filetransfer.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.viltgroup.filetransfer.model.FileInfo;
import com.viltgroup.filetransfer.service.FilesStorageService;

@Controller
@CrossOrigin
public class FileUploadController {

	@Autowired
	FilesStorageService storageService;

	@PostMapping("/upload")
	public String uploadFile(@RequestParam("file") MultipartFile file) {
		String message = "";
		try {
			storageService.save(file);

			message = "Uploaded the file successfully: " + file.getOriginalFilename();
			return "redirect:/files";
		} catch (Exception e) {
			message = "Could not upload the file: " + file.getOriginalFilename() + "!";
			return message;
		}
	}
	
	@DeleteMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<String> deleteFile(@PathVariable String filename) throws IOException {
		String file = storageService.deleteFile(filename);
		return new ResponseEntity<>(file,HttpStatus.OK);
	}
	

	@GetMapping("/getFiles")
	public ResponseEntity<List<FileInfo>> getListFiles() {
		List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
			String filename = path.getFileName().toString();
			String url = MvcUriComponentsBuilder
					.fromMethodName(FileUploadController.class, "getFile", path.getFileName().toString()).build()
					.toString();

			return new FileInfo(filename, url);
		}).collect(Collectors.toList());

		return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> getFile(@PathVariable String filename) {
		Resource file = storageService.load(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}
	
	@RequestMapping("/files")
	public String manageFiles(Model model){
		List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
			String filename = path.getFileName().toString();
			String url = MvcUriComponentsBuilder
					.fromMethodName(FileUploadController.class, "getFile", path.getFileName().toString()).build()
					.toString();

			return new FileInfo(filename, url);
		}).sorted((f1, f2) -> f1.getName().toUpperCase().compareTo(f2.getName().toUpperCase()))
		.collect(Collectors.toList());

		model.addAttribute("files", fileInfos);
		return "fileDisplayView";
	}
}
