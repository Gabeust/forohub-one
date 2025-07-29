package com.gabeust.forohub.mapper;

import com.gabeust.forohub.dto.ReactionDTO;
import com.gabeust.forohub.entity.Reaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReactionMapper {

    @Mapping(source = "user.nick", target = "nick")
    @Mapping(source = "post.id", target = "postId")
    ReactionDTO toDTO(Reaction reaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "post", ignore = true)
    Reaction toEntity(ReactionDTO reactionDTO);
}
