package com.gabeust.forohub.mapper;

import com.gabeust.forohub.dto.PostDTO;
import com.gabeust.forohub.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(source = "author.nick", target = "authorNick")
    @Mapping(source = "author.profile.image", target = "authorImage")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(target = "reactions", ignore = true)
    PostDTO toDTO(Post post);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)    
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "reactions", ignore = true)
    Post toEntity(PostDTO postDTO);
}
