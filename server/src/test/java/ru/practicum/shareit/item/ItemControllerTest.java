package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    private final ItemDto itemDto = new ItemDto(
            1L,
            "name",
            "description",
            true,
            1L,
            1L);
    private final ItemDto updatedItemDto = new ItemDto(
            1L,
            "updatedMame",
            "UpdatedDescription",
            false,
            1L,
            1L);
    private final ItemWithBookingDto itemWithBookingDto = ItemWithBookingDto
            .builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(true)
            .lastBooking(null)
            .nextBooking(null)
            .comments(new ArrayList<>())
            .requestId(1L)
            .build();
    private final CommentDto commentDto = new CommentDto(1L, "text", "authorName", LocalDateTime.now().withNano(0));
    private final List<ItemDto> itemDtos = new ArrayList<>();
    private final List<ItemWithBookingDto> itemWithBookingDtos = new ArrayList<>();

    @Test
    void testAdd() throws Exception {
        when(itemService.add(any(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.owner", is(itemDto.getOwner()), Long.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void testUpdate() throws Exception {
        when(itemService.update(any(), any(), any()))
                .thenReturn(updatedItemDto);

        mockMvc.perform(patch("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedItemDto.getName())))
                .andExpect(jsonPath("$.description", is(updatedItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(updatedItemDto.getAvailable())))
                .andExpect(jsonPath("$.owner", is(updatedItemDto.getOwner()), Long.class))
                .andExpect(jsonPath("$.requestId", is(updatedItemDto.getRequestId()), Long.class));
    }

    @Test
    void testFindById() throws Exception {
        when(itemService.findById(any(), any()))
                .thenReturn(itemWithBookingDto);

        mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemWithBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemWithBookingDto.getName())))
                .andExpect(jsonPath("$.description", is(itemWithBookingDto.getDescription())))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.lastBooking", is(itemWithBookingDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemWithBookingDto.getNextBooking())))
                .andExpect(jsonPath("$.requestId", is(itemWithBookingDto.getRequestId()), Long.class));
    }

    @Test
    void testFindAllByOwner() throws Exception {
        itemWithBookingDtos.add(itemWithBookingDto);
        when(itemService.findAllByOwner(any(), any(), any()))
                .thenReturn(itemWithBookingDtos);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemWithBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemWithBookingDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemWithBookingDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[0].lastBooking", is(itemWithBookingDto.getLastBooking())))
                .andExpect(jsonPath("$[0].nextBooking", is(itemWithBookingDto.getNextBooking())))
                .andExpect(jsonPath("$[0].requestId", is(itemWithBookingDto.getRequestId()), Long.class));
    }

    @Test
    void testFindByText() throws Exception {
        itemDtos.add(itemDto);
        when(itemService.findByText(any(), any(), any()))
                .thenReturn(itemDtos);

        mockMvc.perform(get("/items/search")
                        .param("text", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].owner", is(itemDto.getOwner()), Long.class))
                .andExpect(jsonPath("$[0].requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void testAddComment() throws Exception {
        when(itemService.addComment(any(), any(), any()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().toString())));
    }
}
