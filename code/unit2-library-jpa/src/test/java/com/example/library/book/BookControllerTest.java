package com.example.library.book;

import com.example.library.common.BookNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper json;

    @MockitoBean
    private BookService service;

    @Test
    void list_returnsAllBooks() throws Exception {
        BookDTO dto = sample(1L, "Clean Code");
        when(service.listAll()).thenReturn(List.of(dto));

        mvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Code"));
    }

    @Test
    void getById_returns200WhenFound() throws Exception {
        when(service.get(1L)).thenReturn(sample(1L, "Clean Code"));

        mvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    void getById_returns404WhenMissing() throws Exception {
        when(service.get(eq(999L))).thenThrow(new BookNotFoundException(999L));

        mvc.perform(get("/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void create_returns400WhenValidationFails() throws Exception {
        BookDTO invalid = new BookDTO();
        invalid.setTitle("");
        invalid.setAuthor("Someone");
        invalid.setIsbn("9780132350884");
        invalid.setCopies(1);

        mvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("title"));
    }

    private static BookDTO sample(Long id, String title) {
        BookDTO dto = new BookDTO();
        dto.setId(id);
        dto.setTitle(title);
        dto.setAuthor("RCM");
        dto.setIsbn("9780132350884");
        dto.setCopies(1);
        return dto;
    }
}
