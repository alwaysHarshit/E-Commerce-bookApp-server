package org.booknest.catelogservice.controllers;

import org.booknest.catelogservice.dto.BookRequestDTO;
import org.booknest.catelogservice.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/book")
public class BookController {
    @Autowired
    private BookService bookService;

    //adding the new book
    @PostMapping(value = "/add-book",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void addBook(@ModelAttribute BookRequestDTO bookRequestDTO) {
        bookService.addBook(bookRequestDTO);
    }


}
