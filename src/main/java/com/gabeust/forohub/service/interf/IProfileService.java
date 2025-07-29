package com.gabeust.forohub.service.interf;

import com.gabeust.forohub.dto.ProfileDTO;

import java.util.Optional;

public interface IProfileService {

    ProfileDTO save(ProfileDTO dto);

    Optional<ProfileDTO> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
