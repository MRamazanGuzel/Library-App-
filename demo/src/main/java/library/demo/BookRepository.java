package library.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByUserIdAndIsDeleteFalse(Long userId);

    List<Book> findByUserIdAndIsDeleteFalseAndBooknameContainingIgnoreCase(Long userId, String bookname);

    List<Book> findByUserIdAndIsDeleteFalseAndAuthorContainingIgnoreCase(Long userId, String author);

    List<Book> findByUserIdAndIsDeleteFalseAndDateContainingIgnoreCase(Long userId, String date);

    List<Book> findByUserIdAndIsDeleteFalseAndBookTypeContainingIgnoreCase(Long userId, String bookType);

    List<Book> findByUserIdAndIsDeleteFalseAndPageCount(Long userId, Integer pageCount);

    List<Book> findByUserIdAndIsDeleteFalseAndIsbnContainingIgnoreCase(Long userId, String isbn);
}

