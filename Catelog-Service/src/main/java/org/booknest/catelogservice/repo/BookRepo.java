package org.booknest.catelogservice.repo;

import org.booknest.catelogservice.entity.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;


public interface BookRepo extends MongoRepository<Book, String> {

    boolean existsByIsbn(String isbn);
    Optional<Book> findBookById(String id);

    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByGenreIgnoreCase(String genre);

    @Query("{ '$or': [ { 'title': { '$regex': ?0, '$options': 'i' } }, { 'author': { '$regex': ?0, '$options': 'i' } }, { 'description': { '$regex': ?0, '$options': 'i' } } ] }")
    List<Book> searchByKeyword(String keyword);
}
