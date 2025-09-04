package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestRequestJsonTest {

    @Autowired
    private JacksonTester<ItemRequestRequest> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialize() throws Exception {
        ItemRequestRequest dto = new ItemRequestRequest();
        dto.setDescription("Need a drill for home repairs");

        var result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Need a drill for home repairs");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"description\":\"Need a drill for home repairs\"}";

        ItemRequestRequest result = objectMapper.readValue(content, ItemRequestRequest.class);

        assertThat(result.getDescription()).isEqualTo("Need a drill for home repairs");
    }
}