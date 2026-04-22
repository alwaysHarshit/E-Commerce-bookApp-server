package org.booknest.catelogservice.services;

import lombok.extern.slf4j.Slf4j;
import org.booknest.catelogservice.dto.BookRequestDTO;
import org.booknest.catelogservice.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class BookService {
    @Autowired
    private Utils utils;

    public ResponseEntity<String> addBook(BookRequestDTO bookRequestDTO)  {
        log.info("this is addBook request {}", bookRequestDTO);
        log.info("this is addBook request image {}", bookRequestDTO.getCoverImage());

        //saving file
        String s = null;
        try {
            s = utils.uploadOnCloud(bookRequestDTO.getCoverImage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("this is addBook response {}", s);
        return new ResponseEntity<>(s, HttpStatus.OK);
    }
}
