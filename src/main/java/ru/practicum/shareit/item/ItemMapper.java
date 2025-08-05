package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request", ignore = true)
    Item toItem(NewItemRequest request);

    ItemDto toItemDto(Item item);

    void updateItemFromRequest(UpdateItemRequest request, @MappingTarget Item item);
}
// тут все сложнее чем с ConcurrentHashMap я тут реально седым стал пока информацию искал как правильно
// добавить зависимость в pom.xml и тд. Кстати pom.xml всетаки пришлось переделать чуть чуть.
