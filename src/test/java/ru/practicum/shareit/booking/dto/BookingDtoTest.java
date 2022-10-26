package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws IOException {
        BookingDto dto = BookingDto
                .builder()
                .id(1L)
                .start(LocalDateTime.now().withNano(0))
                .end(LocalDateTime.now().withNano(0).plusHours(1L))
                .status(BookingStatus.WAITING)
                .booker(null)
                .item(null)
                .build();

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo(dto.getStart().toString());
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo(dto.getEnd().toString());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(dto.getStatus().name());
        assertThat(result).extractingJsonPathValue("$.booker").isEqualTo(dto.getBooker());
        assertThat(result).extractingJsonPathValue("$.item").isEqualTo(dto.getItem());
    }
}