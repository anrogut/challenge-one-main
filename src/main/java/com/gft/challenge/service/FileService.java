package com.gft.challenge.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
class FileService {

    private static final Logger LOG = LoggerFactory.getLogger(FileService.class);

    File createFile(String fullPath) {
        File newFile = new File(fullPath);
        try {
            newFile.createNewFile();
        } catch (IOException e) {
            LOG.warn(e.getMessage());
            LOG.trace("",e);
        }
        return newFile;
    }
}
