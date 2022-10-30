package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;
    private final BookingDto bookingDto = BookingDto
            .builder()
            .id(1L)
            .start(LocalDateTime.now().withNano(0).plusHours(1L))
            .end(LocalDateTime.now().withNano(0).plusHours(2L))
            .status(BookingStatus.WAITING)
            .booker(new User())
            .item(new Item())
            .build();
    private final BookingDto bookingDtoApproved = BookingDto
            .builder()
            .id(1L)
            .start(LocalDateTime.now().withNano(0).plusHours(1L))
            .end(LocalDateTime.now().withNano(0).plusHours(2L))
            .status(BookingStatus.APPROVED)
            .booker(new User())
            .item(new Item())
            .build();
    private final List<BookingDto> bookingDtos = new ArrayList<>();

    @Test
    void testCreate() throws Exception {
        when(bookingService.create(any(), any()))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId())));

    }

    @Test
    void testApprove() throws Exception {
        when(bookingService.approve(any(), anyBoolean(), any()))
                .thenReturn(bookingDtoApproved);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoApproved.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoApproved.getStart().toString())))
                .andExpect(jsonPath("$.status", is(bookingDtoApproved.getStatus().name())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoApproved.getBooker().getId())))
                .andExpect(jsonPath("$.item.id", is(bookingDtoApproved.getItem().getId())));
    }

    @Test
    void testFindById() throws Exception {
        when(bookingService.findById(any(), any()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId())));
    }

    @Test
    void testFindAllByBooker() throws Exception {
        bookingDtos.add(bookingDto);
        when(bookingService.findAllByBooker(any(), any(), any(), any()))
                .thenReturn(bookingDtos);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().name())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId())));
    }

    @Test
    void testFindAllByOwner() throws Exception {
        bookingDtos.add(bookingDto);
        when(bookingService.findAllByOwner(any(), any(), any(), any()))
                .thenReturn(bookingDtos);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().name())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId())));
    }
}
