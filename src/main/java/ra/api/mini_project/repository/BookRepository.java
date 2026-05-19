package ra.api.mini_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.api.mini_project.entity.Book;

/**
 * Repository cung cấp sẵn các method CRUD:
 *   findAll()       → SELECT * FROM books
 *   findById(id)    → SELECT * FROM books WHERE id = ?
 *   save(book)      → INSERT / UPDATE
 *   deleteById(id)  → DELETE FROM books WHERE id = ?
 *   existsById(id)  → kiểm tra tồn tại
 *
 * Không cần viết thêm SQL – Spring Data JPA tự sinh.
 */

@Repository
public interface BookRepository extends JpaRepository<Book , Long> {
    // Khong can method
    // Neu muon tim theo title
    // List<Book> findByTitleContainingIgnoreCase(String keyword);
}
