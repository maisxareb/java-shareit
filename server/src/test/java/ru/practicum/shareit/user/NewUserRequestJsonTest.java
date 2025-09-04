package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.NewUserRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class NewUserRequestJsonTest {

    @Autowired
    private JacksonTester<NewUserRequest> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialize() throws Exception {
        NewUserRequest dto = new NewUserRequest();
        dto.setName("Иван Иванов");
        dto.setEmail("ivan@example.com");

        var result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Иван Иванов");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("ivan@example.com");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"name\":\"Иван Иванов\",\"email\":\"ivan@example.com\"}";

        NewUserRequest result = objectMapper.readValue(content, NewUserRequest.class);

        assertThat(result.getName()).isEqualTo("Иван Иванов");
        assertThat(result.getEmail()).isEqualTo("ivan@example.com");
    }
}