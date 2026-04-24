package org.booknest.catelogservice.services;

import lombok.extern.slf4j.Slf4j;
import org.booknest.catelogservice.dto.BookRequestDTO;
import org.booknest.catelogservice.entity.Book;
import org.booknest.catelogservice.repo.BookRepo;
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

    @Autowired
    private BookRepo bookRepo;
    public ResponseEntity<String> addBook(BookRequestDTO bookRequestDTO) {
        log.info("addBook request {}", bookRequestDTO);

        String awsImageUrl = null;

        try {
            // 1. Upload image
            awsImageUrl = utils.uploadOnCloud(bookRequestDTO.getCoverImage());

            // 2. Build entity
            Book book = Book.builder()
                    .title(bookRequestDTO.getTitle())
                    .author(bookRequestDTO.getAuthor())
                    .isbn(bookRequestDTO.getIsbn())
                    .genre(bookRequestDTO.getGenre())
                    .publisher(bookRequestDTO.getPublisher())
                    .price(bookRequestDTO.getPrice())
                    .stock(bookRequestDTO.getStocks())
                    .rating(bookRequestDTO.getRating())
                    .description(bookRequestDTO.getDescription())
                    .coverImageUrl(awsImageUrl)
                    .publishedDate(bookRequestDTO.getPublishedDate())
                    .build();

            // 3. exist in DB if
            if (bookRepo.existsByIsbn(bookRequestDTO.getIsbn())){

                // delete uploaded image if DB failed

                if (awsImageUrl != null) {
                    try {
                        utils.deleteFromCloud(bookRequestDTO.getCoverImage().getOriginalFilename());
                    } catch (Exception ex) {
                        log.error("Failed to cleanup uploaded image", ex);
                    }
                }
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Book already exists");
            }

            bookRepo.save(book);

            return ResponseEntity.ok("success");

        } catch (Exception e) {
            log.error("Error while adding book", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add book");
        }
    }
}
