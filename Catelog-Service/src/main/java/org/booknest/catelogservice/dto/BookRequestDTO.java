package org.booknest.catelogservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request object for creating or updating a book")
public class BookRequestDTO {
    @Schema(description = "Title of the book", example = "The Great Gatsby")
    private String title;
    
    @Schema(description = "Author of the book", example = "F. Scott Fitzgerald")
    private String author;
    
    @Schema(description = "Unique ISBN of the book", example = "978-0743273565")
    private String isbn;
    
    @Schema(description = "Detailed description of the book")
    private String description;
    
    @Schema(description = "Publisher name", example = "Scribner")
    private String publisher;
    
    @Schema(description = "Category of the book", example = "Fiction")
    private String category;
    
    @Schema(description = "Genre of the book", example = "Classic")
    private String genre;
    
    @Schema(description = "Price of the book", example = "15.99")
    private Double price;
    
    @Schema(description = "Available stock count", example = "100")
    private Integer stocks;
    
    @Schema(description = "Current rating (1-5)", example = "4.5")
    private Double rating;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "Publication date", example = "1925-04-10")
    private LocalDate publishedDate;

    private MultipartFile coverImage;
}
