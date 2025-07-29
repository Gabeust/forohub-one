package com.gabeust.forohub.mapper;


import com.gabeust.forohub.dto.CommentDTO;
import com.gabeust.forohub.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { PostMapper.class })
public interface CommentMapper {

    @Mapping(source = "author.nick", target = "authorNick")
    @Mapping(source = "post.id", target = "postId")
    CommentDTO toDTO(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Comment toEntity(CommentDTO commentDTO);

}