package org.booknest.catelogservice.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class Upload {

    public String uploadFile(MultipartFile file) throws IOException {

        //String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String fileName = file.getOriginalFilename();
        Path uploadDir = Paths.get("Catelog-Service/src/main/resources/uploads/covers");

        // Create directory if it doesn't exist
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path filePath = uploadDir.resolve(fileName);
        file.transferTo(filePath);  // ← this is transferTo() you asked about

        return fileName;
    }

}
