package org.booknest.catelogservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserBookResponse {
    private String title;
    private String author;

    private String isbn;

    private String genre;
    private String publisher;
    private double price;

    private String description;
    private String coverImageUrl;
    private LocalDate publishedDate;

}
