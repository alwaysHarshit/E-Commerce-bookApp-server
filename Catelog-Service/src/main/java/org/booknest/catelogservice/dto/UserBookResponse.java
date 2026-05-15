package org.booknest.catelogservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Response object representing book details for end users")
public class UserBookResponse {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private String publisher;
    private double price;
    private double rating;
    private String description;
    private String coverImageUrl;
    private LocalDate publishedDate;
    private Integer stock;
}
