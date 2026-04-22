package org.booknest.catelogservice.services;

import lombok.extern.slf4j.Slf4j;
import org.booknest.catelogservice.dto.BookRequestDTO;
import org.booknest.catelogservice.utils.Upload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class BookService {
    @Autowired
    private Upload upload;

    public void addBook(BookRequestDTO bookRequestDTO)  {
        log.info("this is addBook request {}", bookRequestDTO);
        log.info("this is addBook request image {}", bookRequestDTO.getCoverImage());

        //saving file
        String s = null;
        try {
            s = upload.uploadFile(bookRequestDTO.getCoverImage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("this is addBook response {}", s);
    }
}
