package com.gabeust.forohub.mapper;


import com.gabeust.forohub.dto.ProfileDTO;
import com.gabeust.forohub.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(source = "user.id", target = "userId")
    ProfileDTO toDTO(Profile profile);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Profile toEntity(ProfileDTO dto);
}