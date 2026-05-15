package org.booknest.catelogservice.mapper;

import org.booknest.catelogservice.dto.AdminBookResponse;
import org.booknest.catelogservice.dto.UserBookResponse;
import org.booknest.catelogservice.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public UserBookResponse toUserResponse(Book book, Integer stock) {
        if (book == null) return null;
        return UserBookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .genre(book.getGenre())
                .publisher(book.getPublisher())
                .price(book.getPrice())
                .rating(book.getRating())
                .description(book.getDescription())
                .coverImageUrl(book.getCoverImageUrl())
                .publishedDate(book.getPublishedDate())
                .stock(stock)
                .build();
    }

    public AdminBookResponse toAdminResponse(Book book, Integer stock) {
        if (book == null) return null;
        return AdminBookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .genre(book.getGenre())
                .publisher(book.getPublisher())
                .price(book.getPrice())
                .rating(book.getRating())
                .description(book.getDescription())
                .coverImageUrl(book.getCoverImageUrl())
                .coverImageKey(book.getCoverImageKey())
                .publishedDate(book.getPublishedDate())
                .stock(stock)
                .build();
    }
}
