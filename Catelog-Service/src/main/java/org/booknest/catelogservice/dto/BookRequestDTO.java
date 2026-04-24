package org.booknest.catelogservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequestDTO {
    private String title;
    private String author;
    private String isbn;
    private String description;
    private String publisher;
    private String category;
    private String genre;
    private double price;
    private int stocks;
    private double rating;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  // ← add this
    private LocalDate publishedDate;

    private MultipartFile coverImage;
}
