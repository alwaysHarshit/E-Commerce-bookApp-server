package org.booknest.catelogservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.booknest.catelogservice.dto.UserBookResponse;
import org.booknest.catelogservice.model.ApiResponse;
import org.booknest.catelogservice.services.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Public Book Controller", description = "Public endpoints for browsing and searching the book catalog")
public class PublicBookController {

    private final BookService bookService;

    public PublicBookController(BookService bookService) {
        this.bookService = bookService;
    }


    @Operation(summary = "Get all books", description = "Retrieves a list of all books available in the catalog.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserBookResponse>>> getAllPublicBooks() {
        return ResponseEntity.ok(ApiResponse.success("Successfully retrieved all books", bookService.getAllBooks()));
    }

    @Operation(summary = "Get book by ID", description = "Retrieves detailed information about a specific book.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserBookResponse>> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Successfully retrieved book", bookService.getBookById(id)));
    }

    @Operation(summary = "Search books by title", description = "Finds books whose titles contain the given search string.")
    @GetMapping("/search/title")
    public ResponseEntity<ApiResponse<List<UserBookResponse>>> searchByTitle(@RequestParam String title) {
        return ResponseEntity.ok(ApiResponse.success("Search results for title: " + title, bookService.searchByTitle(title)));
    }

    @Operation(summary = "Search books by author", description = "Finds books written by a specific author.")
    @GetMapping("/search/author")
    public ResponseEntity<ApiResponse<List<UserBookResponse>>> searchByAuthor(@RequestParam String author) {
        return ResponseEntity.ok(ApiResponse.success("Search results for author: " + author, bookService.searchByAuthor(author)));
    }

    @Operation(summary = "Filter books by genre", description = "Retrieves all books belonging to a specific genre.")
    @GetMapping("/filter/genre")
    public ResponseEntity<ApiResponse<List<UserBookResponse>>> filterByGenre(@RequestParam String genre) {
        return ResponseEntity.ok(ApiResponse.success("Filter results for genre: " + genre, bookService.filterByGenre(genre)));
    }
}
