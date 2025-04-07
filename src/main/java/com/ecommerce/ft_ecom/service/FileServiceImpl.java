package com.ecommerce.ft_ecom.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService{

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {

        //File names of current / original file
        String originalFileName = file.getOriginalFilename();

        // Generate a unique file name
        String random = UUID.randomUUID().toString();

        // give file name and path
        String fileName = random.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        String pathName = path+File.separator+fileName;

        //Check if path exist and create
        File folder = new File(path);
        if (!folder.exists()) folder.mkdir();

        //upload to server
        Files.copy(file.getInputStream(), Paths.get(pathName));

        //returning file name
        return fileName;
    }
}
