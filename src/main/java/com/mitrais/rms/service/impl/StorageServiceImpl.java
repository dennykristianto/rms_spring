package com.mitrais.rms.service.impl;

import com.mitrais.rms.service.StorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class StorageServiceImpl implements StorageService {
    private Path rootLocation;
    @Autowired
    private ServletContext servletContext;

    @Override
    public String store(String subFolder, MultipartFile file) {
        String filename= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")).toString()+new Random().nextInt(999)+"."+ FilenameUtils.getExtension(file.getOriginalFilename());
//        String path=this.getClass().getClassLoader().getResource("").getPath();
        String path = servletContext.getRealPath("");

        this.rootLocation= Paths.get(path+File.separator+"uploads");

        File directory = new File(path+File.separator+"uploads"+File.separator+subFolder);

        if (! directory.exists()){
            directory.mkdirs();
        }

        try {
            if (filename.contains("..")) {
                // This is a security check
                throw new RuntimeException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, rootLocation.resolve((!subFolder.equals("")?subFolder+"/":"")+filename),
                        StandardCopyOption.REPLACE_EXISTING);
                return "uploads"+File.separator+subFolder+"/"+filename;
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to store file " + filename, e);
        }
    }

}
