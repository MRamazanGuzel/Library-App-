package library.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("book_id")
    private Long bookId;

    @JsonProperty("isbn")
    private String isbn;

    @JsonProperty("bookname")
    private String bookname;

    @JsonProperty("author")
    private String author;

    @JsonProperty("date")
    private String date;

    @JsonProperty("book_type")
    private String bookType;

    @JsonProperty("page_count")
    private int pageCount;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("is_delete")
    private boolean isDelete;

    @JsonProperty("is_delete_time")
    private LocalDateTime isDeleteTime; // Kitabın silinme zamanı

    @JsonProperty("create_time")
    private LocalDateTime CreateTime;

    // Getters and Setters
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public int getPageCount() {
        return Integer.parseInt(String.valueOf(pageCount));
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public LocalDateTime getIsDeleteTime() {
        return isDeleteTime;
    }

    public void setIsDeleteTime(LocalDateTime isDeleteTime) {
        this.isDeleteTime = isDeleteTime;
    }

    public LocalDateTime getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(LocalDateTime CreateTime) {
        this.CreateTime = CreateTime;
    }
}
