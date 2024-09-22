package library.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookChangeLogService bookChangeLogService;

    public List<Book> getAllBooks(Long userId) {
        return bookRepository.findByUserIdAndIsDeleteFalse(userId);
    }

    public List<Book> searchBooks(Long userId, String isbn, String bookName, String author, String date, String bookType, Integer pageCount) {
        if (isbn != null && !isbn.isEmpty()) {
            return bookRepository.findByUserIdAndIsDeleteFalseAndIsbnContainingIgnoreCase(userId, isbn);
        } else if (bookName != null && !bookName.isEmpty()) {
            return bookRepository.findByUserIdAndIsDeleteFalseAndBooknameContainingIgnoreCase(userId, bookName);
        } else if (author != null && !author.isEmpty()) {
            return bookRepository.findByUserIdAndIsDeleteFalseAndAuthorContainingIgnoreCase(userId, author);
        } else if (date != null && !date.isEmpty()) {
            return bookRepository.findByUserIdAndIsDeleteFalseAndDateContainingIgnoreCase(userId, date);
        } else if (bookType != null && !bookType.isEmpty()) {
            return bookRepository.findByUserIdAndIsDeleteFalseAndBookTypeContainingIgnoreCase(userId, bookType);
        } else if (pageCount != null) {
            return bookRepository.findByUserIdAndIsDeleteFalseAndPageCount(userId, pageCount);
        } else {
            return getAllBooks(userId);
        }
    }

    public Optional<Book> getBookById(Long bookId) {
        return bookRepository.findById(bookId);
    }

    public Book saveBook(Book book) {
        if (book.getCreateTime() == null) {
            book.setCreateTime(LocalDateTime.now());
        }
        return bookRepository.save(book);
    }

    public void deleteBook(Long bookId) {
        Optional<Book> book = bookRepository.findById(bookId);
        if (book.isPresent()) {
            Book bookToDelete = book.get();
            bookToDelete.setDelete(true); // Kitap silindi olarak işaretlenir
            bookToDelete.setIsDeleteTime(LocalDateTime.now()); // Silinme zamanı kaydedilir
            bookRepository.save(bookToDelete);
        }
    }

    public Book updateBook(Long bookId, Book updatedBook) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {
            Book existingBook = optionalBook.get();

            // Log eklemek için önceki değerleri saklayalım
            String oldBookname = existingBook.getBookname();
            String oldAuthor = existingBook.getAuthor();
            String oldDate = existingBook.getDate();
            String oldBookType = existingBook.getBookType();
            Integer oldPageCount = Integer.valueOf(existingBook.getPageCount());
            String oldIsbn = existingBook.getIsbn();

            existingBook.setBookname(updatedBook.getBookname());
            existingBook.setAuthor(updatedBook.getAuthor());
            existingBook.setDate(updatedBook.getDate());
            existingBook.setBookType(updatedBook.getBookType());
            existingBook.setPageCount(updatedBook.getPageCount());
            existingBook.setIsbn(updatedBook.getIsbn());

            Book updated = bookRepository.save(existingBook);

            // Her bir değişiklik için log ekleme
            if (oldBookname != null && !oldBookname.equals(updatedBook.getBookname())) {
                bookChangeLogService.logChange(bookId, updated.getUserId(), "bookname", oldBookname, updatedBook.getBookname());
            }
            if (oldAuthor != null && !oldAuthor.equals(updatedBook.getAuthor())) {
                bookChangeLogService.logChange(bookId, updated.getUserId(), "author", oldAuthor, updatedBook.getAuthor());
            }
            if (oldDate != null && !oldDate.equals(updatedBook.getDate())) {
                bookChangeLogService.logChange(bookId, updated.getUserId(), "date", oldDate, updatedBook.getDate());
            }
            if (oldBookType != null && !oldBookType.equals(updatedBook.getBookType())) {
                bookChangeLogService.logChange(bookId, updated.getUserId(), "bookType", oldBookType, updatedBook.getBookType());
            }
            if (oldPageCount != null && !oldPageCount.equals(updatedBook.getPageCount())) {
                bookChangeLogService.logChange(bookId, updated.getUserId(), "pageCount", oldPageCount.toString(), String.valueOf(updatedBook.getPageCount()));
            }
            if (oldIsbn != null && !oldIsbn.equals(updatedBook.getIsbn())) {
                bookChangeLogService.logChange(bookId, updated.getUserId(), "isbn", oldIsbn, updatedBook.getIsbn());
            }

            return updated;
        }
        return null;
    }
}
