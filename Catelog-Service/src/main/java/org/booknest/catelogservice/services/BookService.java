package org.booknest.catelogservice.services;

import lombok.extern.slf4j.Slf4j;
import org.booknest.catelogservice.dto.AdminBookResponse;
import org.booknest.catelogservice.dto.BookRequestDTO;
import org.booknest.catelogservice.dto.UserBookResponse;
import org.booknest.catelogservice.entity.Book;
import org.booknest.catelogservice.entity.Inventory;
import org.booknest.catelogservice.exceptions.BookAlreadyExistsException;
import org.booknest.catelogservice.exceptions.FileUploadException;
import org.booknest.catelogservice.exceptions.ResourceNotFoundException;
import org.booknest.catelogservice.mapper.BookMapper;
import org.booknest.catelogservice.repo.BookRepo;
import org.booknest.catelogservice.repo.InventoryRepo;
import org.booknest.catelogservice.utils.AwsUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class BookService {

    private final AwsUtils awsUtils;
    private final BookRepo bookRepo;
    private final InventoryRepo inventoryRepo;
    private final BookMapper bookMapper;

    public BookService(AwsUtils awsUtils, BookRepo bookRepo, InventoryRepo inventoryRepo, BookMapper bookMapper) {
        this.awsUtils = awsUtils;
        this.bookRepo = bookRepo;
        this.inventoryRepo = inventoryRepo;
        this.bookMapper = bookMapper;
    }

    /****************** Admin methods ***************************/

    public void addBook(BookRequestDTO bookRequestDTO) {

        String awsImageUrl;

        // 1. if book already no existed
        if (bookRepo.existsByIsbn(bookRequestDTO.getIsbn())) {
            throw new BookAlreadyExistsException("Book with Title: " + bookRequestDTO.getTitle() + " and Isbn: " + bookRequestDTO.getIsbn() + " already exists");
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
                .rating(bookRequestDTO.getRating())
                .description(bookRequestDTO.getDescription())
                .coverImageUrl(awsImageUrl)
                .coverImageKey(bookRequestDTO.getCoverImage().getOriginalFilename())
                .publishedDate(bookRequestDTO.getPublishedDate())
                .build();

        // 4. saving in db
        Book savedBook = bookRepo.save(book);

        // 5. Initialize inventory
        Inventory inventory = Inventory.builder()
                .book(savedBook)
                .stock(bookRequestDTO.getStocks() != null ? bookRequestDTO.getStocks() : 0)
                .status(bookRequestDTO.getStocks() != null && bookRequestDTO.getStocks() > 0 ? "AVAILABLE" : "OUT_OF_STOCK")
                .lowStockThreshold(10)
                .build();
        inventoryRepo.save(inventory);
    }

    public void updateBook(BookRequestDTO dto, Long id) {

        //get that book from db
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id: " + id + " not found"));

        if (dto.getTitle() != null) {
            book.setTitle(dto.getTitle());
        }
        if (dto.getPrice() != null) {
            book.setPrice(dto.getPrice());
        }
        if (dto.getStocks() != null) {
            Inventory inventory = inventoryRepo.findByBookId(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for book id: " + id));
            inventory.setStock(dto.getStocks());
            inventory.setStatus(dto.getStocks() > 0 ? "AVAILABLE" : "OUT_OF_STOCK");
            inventoryRepo.save(inventory);
        }
        if(dto.getCoverImage() != null) {

            //first remove the orginal image from s3
            awsUtils.deleteFromCloud(book.getCoverImageKey());

            //second upload the new image on s3
            String url;
            try {
                url = awsUtils.uploadOnCloud(dto.getCoverImage());
            } catch (IOException e) {
                throw new FileUploadException(e.getMessage());
            }
            // third update the key and image url
            book.setCoverImageKey(dto.getCoverImage().getOriginalFilename());
            book.setCoverImageUrl(url);

        }

        //save in db
        bookRepo.save(book);
    }

    public void deleteBook(Long id) {

        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id: " + id + " not found"));

            // 1. Delete image from S3
            if (book.getCoverImageKey() != null) {
                awsUtils.deleteFromCloud(book.getCoverImageKey());
            }
            // 2. Delete inventory
            inventoryRepo.findByBookId(id).ifPresent(inventoryRepo::delete);

            // 3. Delete from DB
            bookRepo.delete(book);
    }

    public List<AdminBookResponse> AllAdminBooks() {
        return bookRepo.findAll().stream()
                .map(book -> {
                    Integer stock = inventoryRepo.findByBookId(book.getId())
                            .map(Inventory::getStock)
                            .orElse(0);
                    return bookMapper.toAdminResponse(book, stock);
                })
                .toList();
    }


    /****************** public  methods ***************************/
    public List<UserBookResponse> getAllBooks() {
        return bookRepo.findAll().stream()
                .map(book -> {
                    Integer stock = inventoryRepo.findByBookId(book.getId())
                            .map(Inventory::getStock)
                            .orElse(0);
                    return bookMapper.toUserResponse(book, stock);
                })
                .toList();
    }

    public List<UserBookResponse> searchByTitle(String title) {
        return bookRepo.findByTitleContainingIgnoreCase(title).stream()
                .map(book -> {
                    Integer stock = inventoryRepo.findByBookId(book.getId())
                            .map(Inventory::getStock)
                            .orElse(0);
                    return bookMapper.toUserResponse(book, stock);
                })
                .toList();
    }

    public List<UserBookResponse> searchByAuthor(String author) {
        return bookRepo.findByAuthorContainingIgnoreCase(author).stream()
                .map(book -> {
                    Integer stock = inventoryRepo.findByBookId(book.getId())
                            .map(Inventory::getStock)
                            .orElse(0);
                    return bookMapper.toUserResponse(book, stock);
                })
                .toList();
    }

    public List<UserBookResponse> filterByGenre(String genre) {
        return bookRepo.findByGenreIgnoreCase(genre).stream()
                .map(book -> {
                    Integer stock = inventoryRepo.findByBookId(book.getId())
                            .map(Inventory::getStock)
                            .orElse(0);
                    return bookMapper.toUserResponse(book, stock);
                })
                .toList();
    }

    public UserBookResponse getBookById(Long id) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id: " + id + " not found"));
        Integer stock = inventoryRepo.findByBookId(id)
                .map(Inventory::getStock)
                .orElse(0);
        return bookMapper.toUserResponse(book, stock);
    }
}
