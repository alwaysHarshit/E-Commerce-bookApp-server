//package org.booknest.catelogservice.services;
//
//import org.booknest.catelogservice.dto.BookRequestDTO;
//import org.booknest.catelogservice.entity.Book;
//import org.booknest.catelogservice.model.UserBookResponse;
//import org.booknest.catelogservice.repo.BookRepo;
//import org.booknest.catelogservice.utils.AwsUtils;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mock.web.MockMultipartFile;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class BookServiceTest {
//
//    @Mock
//    private AwsUtils awsUtils;
//
//    @Mock
//    private BookRepo bookRepo;
//
//    @InjectMocks
//    private BookService bookService;
//
//    private BookRequestDTO bookRequestDTO;
//
//    private Book book;
//
//    @BeforeEach
//    void setUp() {
//        MockMultipartFile mockFile = new MockMultipartFile(
//                "coverImage", "test.jpg", "image/jpeg", "test image content".getBytes());
//
//        bookRequestDTO = BookRequestDTO.builder()
//                .title("Test Book")
//                .author("Test Author")
//                .isbn("1234567890")
//                .genre("Fiction")
//                .publisher("Test Publisher")
//                .price(29.99)
//                .stocks(10)
//                .rating(4.5)
//                .description("Test Description")
//                .publishedDate(LocalDate.now())
//                .coverImage(mockFile)
//                .build();
//
//        book = Book.builder()
//                .id("1")
//                .title("Test Book")
//                .author("Test Author")
//                .isbn("1234567890")
//                .genre("Fiction")
//                .publisher("Test Publisher")
//                .price(29.99)
//                .stock(10)
//                .rating(4.5)
//                .description("Test Description")
//                .coverImageUrl("http://aws.s3/test.jpg")
//                .coverImageKey("test.jpg")
//                .publishedDate(LocalDate.now())
//                .build();
//    }
//
//    @Test
//    @DisplayName("Should successfully add a book")
//    void testAddBook_Success() throws IOException {
//        // Arrange
//        when(awsUtils.uploadOnCloud(any())).thenReturn("http://aws.s3/test.jpg");
//        when(bookRepo.existsByIsbn(anyString())).thenReturn(false);
//        when(bookRepo.save(any(Book.class))).thenReturn(book);
//
//        // Act
////        ResponseEntity<String> response = bookService.addBook(bookRequestDTO);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("success", response.getBody());
//        verify(bookRepo, times(1)).save(any(Book.class));
//    }
//
//    @Test
//    @DisplayName("Should return conflict when adding a book with existing ISBN")
//    void testAddBook_Conflict() throws IOException {
//        // Arrange
//        when(awsUtils.uploadOnCloud(any())).thenReturn("http://aws.s3/test.jpg");
//        when(bookRepo.existsByIsbn(anyString())).thenReturn(true);
//
//        // Act
//        ResponseEntity<String> response = bookService.addBook(bookRequestDTO);
//
//        // Assert
//        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
//        assertEquals("Book already exists", response.getBody());
//        verify(awsUtils, times(1)).deleteFromCloud(anyString());
//        verify(bookRepo, never()).save(any(Book.class));
//    }
//
//    @Test
//    @DisplayName("Should successfully update an existing book")
//    void testUpdateBook_Success() {
//        // Arrange
//        when(bookRepo.findBookById("1")).thenReturn(Optional.of(book));
//        BookRequestDTO updateDTO = BookRequestDTO.builder().title("Updated Title").build();
//
//        // Act
//        bookService.updateBook(updateDTO, "1");
//
//        // Assert
//        assertEquals("Updated Title", book.getTitle());
//        verify(bookRepo, times(1)).save(book);
//    }
//
//    @Test
//    @DisplayName("Should throw exception when updating non-existent book")
//    void testUpdateBook_NotFound() {
//        // Arrange
//        when(bookRepo.findBookById("non-existent")).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(RuntimeException.class, () -> bookService.updateBook(bookRequestDTO, "non-existent"));
//    }
//
//    @Test
//    @DisplayName("Should successfully delete a book")
//    void testDeleteBook_Success() {
//        // Arrange
//        when(bookRepo.findBookById("1")).thenReturn(Optional.of(book));
//
//        // Act
//        bookService.deleteBook("1");
//
//        // Assert
//        verify(awsUtils, times(1)).deleteFromCloud("test.jpg");
//        verify(bookRepo, times(1)).delete(book);
//    }
//
//    @Test
//    @DisplayName("Should return all books")
//    void testGetAllBooks() {
//        // Arrange
//        when(bookRepo.findAll()).thenReturn(Collections.singletonList(book));
//
//        // Act
//        List<UserBookResponse> result = bookService.getAllBooks();
//
//        // Assert
//        assertFalse(result.isEmpty());
//        assertEquals(1, result.size());
//        assertEquals("Test Book", result.get(0).getTitle());
//    }
//}
