package ra.api.mini_project.controller;

import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.api.mini_project.entity.Book;
import ra.api.mini_project.repository.BookRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller xử lý toàn bộ request liên quan đến Book.
 *
 * @RestController  = @Controller + @ResponseBody
 *                   → tự chuyển return value thành JSON
 * @RequestMapping  → tiền tố chung cho mọi endpoint trong class
 */

@RestController
@RequestMapping("/api/books")
public class Bookcontroller {
    // Spring tự inject BookRepository qua constructor
    private final BookRepository bookRepository;

    public Bookcontroller(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    // Get : /api/books -> lay tat ca sach
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks(){
        List<Book> books = bookRepository.findAll();
        return ResponseEntity.ok(books); // 200 ok
    }

    // Get : /api/books/{id} -> lay sach theo id
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id){
        Optional<Book> optional = bookRepository.findById(id);

        if(optional.isEmpty()){
            return ResponseEntity.notFound().build(); //404 not found
        }

        return ResponseEntity.ok(optional.get()); // 200 ok
    }

    // Post : /api/books -> Them moi sach
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book){
        // dam bao client khong truyen id -> de DB tu sinh
        book.setId(null);

        Book saved = bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved); //201 created
    }

    // Put : /api/books/{id} -> cap nhat toan bo sach
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id,
                                           @RequestBody Book bookRequest){
        Optional<Book> optional = bookRepository.findById(id);

        if(optional.isEmpty()){
            return ResponseEntity.notFound().build(); // 404 not found
        }

        Book existing = optional.get();

        // Ghi de toan bo field ( Put = replace )
        existing.setTitle(bookRequest.getTitle());
        existing.setAuthor(bookRequest.getAuthor());
        existing.setPrice(bookRequest.getPrice());

        Book updated = bookRepository.save(existing);
        return ResponseEntity.ok(updated); // 200 ok
    }

    // Patch : /api/books/{id} -> cap nhat mot phan cua sach
    @PatchMapping("/{id}")
    public ResponseEntity<Book> partialUpdateBook(@PathVariable Long id,
                                                  @RequestBody Map<String , Object> fields){
        /*
         * Dùng Map<String, Object> thay vì Book để phân biệt:
         *   - field KHÔNG có trong request  → giữ nguyên
         *   - field CÓ trong request        → cập nhật
         *
         * Nếu dùng @RequestBody Book thì các field bị bỏ qua sẽ
         * tự động thành null → sẽ xóa dữ liệu không mong muốn.
         */

        Optional<Book> optional = bookRepository.findById(id);

        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

        Book existing = optional.get();

        // Chỉ cập nhật field nào client gửi lên
        if (fields.containsKey("title")) {
            existing.setTitle((String) fields.get("title"));
        }
        if (fields.containsKey("author")) {
            existing.setAuthor((String) fields.get("author"));
        }
        if (fields.containsKey("price")) {
            // JSON number → Jackson parse thành Double hoặc Integer tuỳ giá trị
            existing.setPrice(((Number) fields.get("price")).doubleValue());
        }

        Book updated = bookRepository.save(existing);
        return ResponseEntity.ok(updated); // 200 OK
    }

    // Delete : /api/books/{id} -> Xoa sach
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
