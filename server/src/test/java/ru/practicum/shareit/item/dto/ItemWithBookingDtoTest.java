package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemWithBookingDtoTest {

    @Autowired
    private JacksonTester<ItemWithBookingDto> json;

    @Test
    void testSerialize() throws IOException {
        ItemWithBookingDto dto = ItemWithBookingDto
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

        JsonContent<ItemWithBookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(dto.isAvailable());
        assertThat(result).extractingJsonPathValue("$.lastBooking").isEqualTo(dto.getLastBooking());
        assertThat(result).extractingJsonPathValue("$.nextBooking").isEqualTo(dto.getNextBooking());
        assertThat(result).extractingJsonPathValue("$.comments").isEqualTo(dto.getComments());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(dto.getRequestId().intValue());
    }
}