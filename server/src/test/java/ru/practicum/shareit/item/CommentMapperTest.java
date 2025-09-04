package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void toComment_shouldMapCommentDtoToCommentIgnoringCertainFields() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Отличная вещь, очень полезная!")
                .authorName("Джон Доу")
                .created(LocalDateTime.of(2024, 1, 15, 10, 30))
                .build();

        Comment comment = commentMapper.toComment(commentDto);

        assertNotNull(comment);
        assertEquals("Отличная вещь, очень полезная!", comment.getText());

        assertNull(comment.getId());
        assertNull(comment.getItem());
        assertNull(comment.getAuthor());
        assertNull(comment.getCreated());
    }

    @Test
    void toComment_shouldHandleNullCommentDto() {
        Comment comment = commentMapper.toComment(null);

        assertNull(comment);
    }

    @Test
    void toComment_shouldHandleCommentDtoWithNullFields() {
        CommentDto commentDto = CommentDto.builder().build();

        Comment comment = commentMapper.toComment(commentDto);

        assertNotNull(comment);
        assertNull(comment.getText());
        assertNull(comment.getId());
        assertNull(comment.getItem());
        assertNull(comment.getAuthor());
        assertNull(comment.getCreated());
    }

    @Test
    void toCommentDto_shouldMapCommentToCommentDtoWithAuthorName() {
        User author = User.builder()
                .id(1L)
                .name("Алиса Смит")
                .email("alice@example.com")
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .text("Отличное качество, рекомендую!")
                .author(author)
                .created(LocalDateTime.of(2024, 1, 16, 14, 45))
                .build();

        CommentDto commentDto = commentMapper.toCommentDto(comment);

        assertNotNull(commentDto);
        assertEquals(1L, commentDto.getId());
        assertEquals("Отличное качество, рекомендую!", commentDto.getText());
        assertEquals("Алиса Смит", commentDto.getAuthorName());
        assertEquals(LocalDateTime.of(2024, 1, 16, 14, 45), commentDto.getCreated());
    }

    @Test
    void toCommentDto_shouldHandleNullComment() {
        CommentDto commentDto = commentMapper.toCommentDto(null);

        assertNull(commentDto);
    }

    @Test
    void toCommentDto_shouldHandleCommentWithNullAuthor() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("Тестовый комментарий")
                .author(null)
                .created(LocalDateTime.now())
                .build();

        CommentDto commentDto = commentMapper.toCommentDto(comment);

        assertNotNull(commentDto);
        assertEquals(1L, commentDto.getId());
        assertEquals("Тестовый комментарий", commentDto.getText());
        assertNull(commentDto.getAuthorName());
        assertNotNull(commentDto.getCreated());
    }

    @Test
    void toCommentDto_shouldHandleCommentWithAuthorHavingNullName() {
        User author = User.builder()
                .id(1L)
                .name(null)
                .email("test@example.com")
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .text("Еще один тестовый комментарий")
                .author(author)
                .created(LocalDateTime.now())
                .build();

        CommentDto commentDto = commentMapper.toCommentDto(comment);

        assertNotNull(commentDto);
        assertEquals(1L, commentDto.getId());
        assertEquals("Еще один тестовый комментарий", commentDto.getText());
        assertNull(commentDto.getAuthorName());
        assertNotNull(commentDto.getCreated());
    }

    @Test
    void toCommentDto_shouldHandleCommentWithNullFields() {
        Comment comment = Comment.builder().build();

        CommentDto commentDto = commentMapper.toCommentDto(comment);

        assertNotNull(commentDto);
        assertNull(commentDto.getId());
        assertNull(commentDto.getText());
        assertNull(commentDto.getAuthorName());
        assertNull(commentDto.getCreated());
    }

    @Test
    void toCommentDto_shouldHandleCommentWithoutAuthorButWithOtherFields() {
        Comment comment = Comment.builder()
                .id(2L)
                .text("Комментарий без автора")
                .author(null)
                .created(LocalDateTime.of(2024, 1, 17, 9, 0))
                .build();

        CommentDto commentDto = commentMapper.toCommentDto(comment);

        assertNotNull(commentDto);
        assertEquals(2L, commentDto.getId());
        assertEquals("Комментарий без автора", commentDto.getText());
        assertNull(commentDto.getAuthorName());
        assertEquals(LocalDateTime.of(2024, 1, 17, 9, 0), commentDto.getCreated());
    }
}