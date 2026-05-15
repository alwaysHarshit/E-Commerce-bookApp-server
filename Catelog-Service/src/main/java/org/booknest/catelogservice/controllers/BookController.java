package org.booknest.catelogservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.booknest.catelogservice.dto.AdminBookResponse;
import org.booknest.catelogservice.dto.BookRequestDTO;
import org.booknest.catelogservice.model.ApiResponse;
import org.booknest.catelogservice.services.BookService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin Book Controller", description = "Endpoints for managing the book catalog (Admin only)")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Add a new book", description = "Allows administrators to add a new book listing with a cover image.")
    @PostMapping(value = "/books", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> addBook(@ModelAttribute BookRequestDTO bookRequestDTO) {
        bookService.addBook(bookRequestDTO);
        return ResponseEntity.ok(ApiResponse.success("Successfully added book", null));
    }

    @Operation(summary = "Get all books (Admin)", description = "Retrieves a list of all books in the catalog with full details.")
    @GetMapping("/books")
    public ResponseEntity<ApiResponse<List<AdminBookResponse>>> getAllAdminBooks() {
        List<AdminBookResponse> allBooks = bookService.AllAdminBooks();
        return ResponseEntity.ok(ApiResponse.success("Successfully retrieved all books", allBooks));
    }

    @Operation(summary = "Update an existing book", description = "Updates details of an existing book by its ID.")
    @PatchMapping(value = "/books/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> updateBook(@PathVariable Long id, @ModelAttribute BookRequestDTO bookRequestDTO) {
        bookService.updateBook(bookRequestDTO, id);
        return ResponseEntity.ok(ApiResponse.success("Successfully updated the book", null));
    }

    @Operation(summary = "Delete a book", description = "Deletes a book from the catalog and its cover image from S3.")
    @DeleteMapping("/books/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("Successfully deleted book", null));
    }
}
