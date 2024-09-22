package library.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String bookname,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String bookType,
            @RequestParam(required = false) Integer pageCount) {

        if (userId == null || userId <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Book> books = bookService.searchBooks(userId, isbn, bookname, author, date, bookType, pageCount);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book savedBook = bookService.saveBook(book);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<Void> updateBook(@PathVariable Long bookId, @RequestBody Book book) {
        if (bookService.getBookById(bookId).isPresent()) {
            book.setBookId(bookId);
            bookService.saveBook(book);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        if (bookService.getBookById(bookId).isPresent()) {
            bookService.deleteBook(bookId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{bookId}")
    public ResponseEntity<Book> UpdateBook(@PathVariable Long bookId, @RequestBody Book bookDetails) {
        Book updatedBook = bookService.updateBook(bookId, bookDetails);
        if (updatedBook != null) {
            return ResponseEntity.ok(updatedBook);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}