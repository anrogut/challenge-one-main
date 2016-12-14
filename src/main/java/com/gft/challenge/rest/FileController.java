package com.gft.challenge.rest;

import com.gft.challenge.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping(value = "/addFile")
    public ResponseEntity<Void> addFile(@RequestParam(value = "name") String name) throws IOException {
        fileService.createFile(name);
        return ResponseEntity.ok().build();
    }
}
