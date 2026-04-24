package org.booknest.catelogservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "books", collation = "en")
public class Book {
    @Id
    private String id;

    private String title;
    private String author;

    @Indexed(unique = true)
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
