package org.booknest.catelogservice.model;

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
    @Schema(description = "Unique identifier of the book", example = "60d5ecb8b311f82d1c8e1a5f")
    private String id;
    
    @Schema(description = "Title of the book", example = "The Great Gatsby")
    private String title;
    
    @Schema(description = "Author of the book", example = "F. Scott Fitzgerald")
    private String author;

    @Schema(description = "ISBN of the book", example = "978-0743273565")
    private String isbn;

    @Schema(description = "Genre of the book", example = "Classic")
    private String genre;
    
    @Schema(description = "Publisher name", example = "Scribner")
    private String publisher;
    
    @Schema(description = "Price of the book", example = "15.99")
    private double price;

    @Schema(description = "Detailed description of the book")
    private String description;
    
    @Schema(description = "Public URL for the book cover image", example = "https://bucket.s3.amazonaws.com/covers/image.jpg")
    private String coverImageUrl;
    
    @Schema(description = "Publication date", example = "1925-04-10")
    private LocalDate publishedDate;
}
