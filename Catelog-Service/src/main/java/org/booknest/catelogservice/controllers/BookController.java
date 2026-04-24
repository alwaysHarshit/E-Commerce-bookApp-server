package org.booknest.catelogservice.controllers;

import org.booknest.catelogservice.dto.BookRequestDTO;
import org.booknest.catelogservice.model.ApiResponse;
import org.booknest.catelogservice.model.UserBookResponse;
import org.booknest.catelogservice.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class BookController {
    @Autowired
    private BookService bookService;

    //adding the new book
    @PostMapping(value = "/books",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseEntity<String>> addBook(@ModelAttribute BookRequestDTO bookRequestDTO) {
        ResponseEntity<String> stringResponseEntity = bookService.addBook(bookRequestDTO);
        return ResponseEntity.ok(stringResponseEntity);
    }

    @GetMapping("/books")
    public ResponseEntity<ApiResponse<List<UserBookResponse>>> getAllBooks() {
        List<UserBookResponse> allBooks = bookService.getAllBooks();
        return ResponseEntity.ok(ApiResponse.success("SuccessFully get all books", allBooks));
    }


}
