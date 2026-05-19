package ra.api.mini_project.entity;

import jakarta.persistence.*;

/**
 * Entity ánh xạ tới bảng "books" trong MySQL.
 *
 * @Table(name = "books") → tên bảng trong DB
 * @Id                    → khóa chính
 * @GeneratedValue        → tự tăng (AUTO_INCREMENT)
 */

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private Double price;

    // Constructor
    public Book() {
        // JPA yeu cau constructor khong tham so
    }

    public Book(String title , String author , Double price){
        this.title = title;
        this.author = author;
        this.price = price;
    }

    // Getter & setter

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Double getPrice() {
        return price;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    // toString (tien debug)
    @Override
    public String toString(){
        return "Book{id" + id
                + ", title=" + title + '\''
                + ", author=" + author + '\''
                + ", price=" + price + '}';
    }
}

