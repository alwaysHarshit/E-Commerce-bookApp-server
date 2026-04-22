package org.booknest.catelogservice.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collation = "books")
public class Book {
    @Id
    private int bookId;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private String publisher;
    private double price;
    private int stock;
    private double rating;
    private String description;
    private String coverImageUrl;
    private LocalDate publishedDate;
}
