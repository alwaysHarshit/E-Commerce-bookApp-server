package org.booknest.catelogservice.services;

import lombok.extern.slf4j.Slf4j;
import org.booknest.catelogservice.dto.BookRequestDTO;
import org.booknest.catelogservice.entity.Book;
import org.booknest.catelogservice.exceptions.BookAlreadyExistsException;
import org.booknest.catelogservice.exceptions.FileUploadException;
import org.booknest.catelogservice.exceptions.ResourceNotFoundException;
import org.booknest.catelogservice.model.UserBookResponse;
import org.booknest.catelogservice.repo.BookRepo;
import org.booknest.catelogservice.utils.AwsUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookService {

    private final AwsUtils awsUtils;
    private final BookRepo bookRepo;

    public BookService(AwsUtils awsUtils, BookRepo bookRepo) {
        this.awsUtils = awsUtils;
        this.bookRepo = bookRepo;
    }

    /****************** Admin methods ***************************/

    public void addBook(BookRequestDTO bookRequestDTO) {
        log.info("addBook request {}", bookRequestDTO);

        String awsImageUrl;

        // 1. if book already no existed
        if (bookRepo.existsByIsbn(bookRequestDTO.getIsbn())) {
            throw new BookAlreadyExistsException("Book with Title: " + bookRequestDTO.getTitle() + "and Isbn:" + bookRequestDTO.getIsbn() + " already exists");
        }

        // 2. Upload image
        try {
            awsImageUrl = awsUtils.uploadOnCloud(bookRequestDTO.getCoverImage());
        } catch (IOException e) {
            throw new FileUploadException("Failed to upload image to S3");
        }

        // 3. Build entity
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
                .coverImageKey(bookRequestDTO.getCoverImage().getOriginalFilename())
                .publishedDate(bookRequestDTO.getPublishedDate())
                .build();

        // 4. saving in db
        bookRepo.save(book);
    }

    public void updateBook(BookRequestDTO dto, String id) {

        log.info("updateBook request {}", dto);

        //get that book from db
        Book book = bookRepo.findBookById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id: " + id + " not found"));

        if (dto.getTitle() != null) {
            book.setTitle(dto.getTitle());
        }
        if (dto.getAuthor() != null) {
            book.setAuthor(dto.getAuthor());
        }
        if (dto.getIsbn() != null) {
            book.setIsbn(dto.getIsbn());
        }
        if (dto.getGenre() != null) {
            book.setGenre(dto.getGenre());
        }
        if (dto.getPublisher() != null) {
            book.setPublisher(dto.getPublisher());
        }
        if (dto.getPrice() != null) {
            book.setPrice(dto.getPrice());
        }
        if (dto.getStocks() != null) {
            book.setStock(dto.getStocks());
        }
        if (dto.getRating() != null) {
            book.setRating(dto.getRating());
        }
        if (dto.getDescription() != null) {
            book.setDescription(dto.getDescription());
        }
        if (dto.getPublishedDate() != null) {
            book.setPublishedDate(dto.getPublishedDate());
        }
        bookRepo.save(book);

        log.info("Successfully updated book");
    }

    public void deleteBook(String id) {

        Book book = bookRepo.findBookById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id: " + id + " not found"));

            // 1. Delete image from S3
            if (book.getCoverImageKey() != null) {
                awsUtils.deleteFromCloud(book.getCoverImageKey());
            }
            // 2. Delete from DB
            bookRepo.delete(book);
            log.info("Successfully delete book");

    }


    /****************** public  methods ***************************/
    public List<UserBookResponse> getAllBooks() {
        log.info("getAllBooks request");
        List<Book> books = bookRepo.findAll();
        return books.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<UserBookResponse> searchByTitle(String title) {
        return bookRepo.findByTitleContainingIgnoreCase(title).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<UserBookResponse> searchByAuthor(String author) {
        return bookRepo.findByAuthorContainingIgnoreCase(author).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<UserBookResponse> filterByGenre(String genre) {
        return bookRepo.findByGenreIgnoreCase(genre).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<UserBookResponse> searchByKeyword(String keyword) {
        return bookRepo.searchByKeyword(keyword).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UserBookResponse getBookById(String id) {
        Book book = bookRepo.findBookById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id: " + id + " not found"));
        return mapToResponse(book);
    }

    /****************** Utils methods ***************************/
    private UserBookResponse mapToResponse(Book book) {
        return UserBookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .genre(book.getGenre())
                .publisher(book.getPublisher())
                .price(book.getPrice())
                .description(book.getDescription())
                .coverImageUrl(book.getCoverImageUrl())
                .publishedDate(book.getPublishedDate())
                .build();
    }
}

