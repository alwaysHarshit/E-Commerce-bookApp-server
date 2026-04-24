package org.booknest.catelogservice.repo;

import org.booknest.catelogservice.entity.Book;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface BookRepo extends MongoRepository<Book, Integer> {
    boolean findByTitle(String title);

    boolean existsBookByTitle(String title);

    boolean existsByIsbn(String isbn);
}
