package ra.api.mini_project.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import ra.api.mini_project.entity.Book;
import ra.api.mini_project.repository.BookRepository;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;

import java.util.Map;
/**
 * Integration Test – chạy toàn bộ Spring context với H2 in-memory DB.
 *
 * Cần thêm dependency H2 vào pom.xml khi test:
 *   <dependency>
 *       <groupId>com.h2database</groupId>
 *       <artifactId>h2</artifactId>
 *       <scope>test</scope>
 *   </dependency>
 *
 * Và tạo file src/test/resources/application.properties:
 *   spring.datasource.url=jdbc:h2:mem:testdb
 *   spring.datasource.driver-class-name=org.h2.Driver
 *   spring.jpa.hibernate.ddl-auto=create-drop
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // ID của sách tạo ở TC-02, dùng lại ở các TC sau
    private static Long createdBookId;

    // TC-01: GET /api/books → 200 OK, danh sách rỗng ban đầu
    @Test
    @Order(1)
    @DisplayName("TC-01: GET all books – trả về danh sách rỗng")
    void getAllBooks_shouldReturn200AndEmptyList() throws Exception {
        bookRepository.deleteAll(); // đảm bảo DB sạch

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // -------------------------------------------------------
    // TC-02: POST /api/books → 201 Created
    // -------------------------------------------------------
    @Test
    @Order(2)
    @DisplayName("TC-02: POST create book – trả về 201 và object vừa tạo")
    void createBook_shouldReturn201AndCreatedBook() throws Exception {
        Book newBook = new Book("Clean Code", "Robert C. Martin", 19.5);

        String responseBody = mockMvc.perform(
                        post("/api/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.author").value("Robert C. Martin"))
                .andExpect(jsonPath("$.price").value(19.5))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Lưu id để dùng ở các test sau
        createdBookId = objectMapper.readValue(responseBody, Book.class).getId();
    }

    // -------------------------------------------------------
    // TC-03: GET /api/books/{id} → 200 OK
    // -------------------------------------------------------
    @Test
    @Order(3)
    @DisplayName("TC-03: GET book by ID – trả về 200 và đúng sách")
    void getBookById_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/books/" + createdBookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdBookId))
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    // -------------------------------------------------------
    // TC-04: GET /api/books/{id} với ID không tồn tại → 404
    // -------------------------------------------------------
    @Test
    @Order(4)
    @DisplayName("TC-04: GET book by ID không tồn tại – trả về 404")
    void getBookById_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/books/99999"))
                .andExpect(status().isNotFound());
    }

    // -------------------------------------------------------
    // TC-05: PUT /api/books/{id} → 200 OK, dữ liệu đã thay đổi
    // -------------------------------------------------------
    @Test
    @Order(5)
    @DisplayName("TC-05: PUT update book – trả về 200 và dữ liệu mới")
    void updateBook_shouldReturn200AndUpdatedBook() throws Exception {
        Book updated = new Book("Clean Code 2nd Ed", "Robert C. Martin", 25.0);

        mockMvc.perform(
                        put("/api/books/" + createdBookId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdBookId))
                .andExpect(jsonPath("$.title").value("Clean Code 2nd Ed"))
                .andExpect(jsonPath("$.price").value(25.0));
    }

    // -------------------------------------------------------
    // TC-06: PATCH /api/books/{id} → chỉ cập nhật price
    // -------------------------------------------------------
    @Test
    @Order(6)
    @DisplayName("TC-06: PATCH book – chỉ cập nhật price, giữ nguyên title/author")
    void partialUpdateBook_shouldOnlyUpdatePrice() throws Exception {
        Map<String, Object> patch = Map.of("price", 30.0);

        mockMvc.perform(
                        patch("/api/books/" + createdBookId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(30.0))
                // title không được thay đổi
                .andExpect(jsonPath("$.title").value("Clean Code 2nd Ed"))
                .andExpect(jsonPath("$.author").value("Robert C. Martin"));
    }

    // -------------------------------------------------------
    // TC-07: DELETE /api/books/{id} → 204 No Content
    // -------------------------------------------------------
    @Test
    @Order(7)
    @DisplayName("TC-07: DELETE book – trả về 204")
    void deleteBook_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/books/" + createdBookId))
                .andExpect(status().isNoContent());
    }

    // -------------------------------------------------------
    // TC-08: DELETE /api/books/{id} lần 2 → 404 (đã xóa rồi)
    // -------------------------------------------------------
    @Test
    @Order(8)
    @DisplayName("TC-08: DELETE book đã xóa – trả về 404")
    void deleteBook_alreadyDeleted_shouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/books/" + createdBookId))
                .andExpect(status().isNotFound());
    }

    // -------------------------------------------------------
    // TC-09: GET /api/books → sau khi xóa, danh sách rỗng lại
    // -------------------------------------------------------
    @Test
    @Order(9)
    @DisplayName("TC-09: GET all books sau khi xóa – danh sách rỗng")
    void getAllBooks_afterDelete_shouldBeEmpty() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
