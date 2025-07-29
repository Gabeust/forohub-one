package com.gabeust.forohub.service;

import com.gabeust.forohub.dto.ProfileDTO;
import com.gabeust.forohub.entity.Profile;
import com.gabeust.forohub.entity.User;
import com.gabeust.forohub.mapper.ProfileMapper;
import com.gabeust.forohub.repository.IProfileRepository;
import com.gabeust.forohub.repository.IUserRepository;
import com.gabeust.forohub.service.interf.IProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
/**
 * Implementación del servicio para gestionar los perfiles de usuario.
 *
 * Proporciona métodos para crear, actualizar, buscar y eliminar perfiles
 * asociados a usuarios en la base de datos.
 */
@Service
public class ProfileServiceImpl implements IProfileService {
    private final IProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final IUserRepository userRepository;

    public ProfileServiceImpl(IProfileRepository profileRepository, ProfileMapper profileMapper, IUserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.profileMapper = profileMapper;
        this.userRepository = userRepository;
    }
    /**
     * Guarda o actualiza un perfil de usuario.
     *
     * Busca el usuario asociado por ID y asigna el perfil a ese usuario.
     * Si ya existe un perfil para el usuario, actualiza los campos nick, imagen y bio.
     * Si no existe, crea uno nuevo.
     *
     * @param dto DTO con los datos del perfil
     * @return DTO del perfil guardado o actualizado
     * @throws RuntimeException si no se encuentra el usuario por ID
     */
    @Transactional
    @Override
    public ProfileDTO save(ProfileDTO dto) {
        Profile profile = profileMapper.toEntity(dto);
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.userId()));

        profile.setUser(user);
        Optional<Profile> existing = profileRepository.findByUser_Id(dto.userId());
        if (existing.isPresent()) {
            Profile existingProfile = existing.get();
            existingProfile.setNick(dto.nick());
            existingProfile.setImage(dto.image());
            existingProfile.setBio(dto.bio());
            return profileMapper.toDTO(profileRepository.save(existingProfile));
        }

        return profileMapper.toDTO(profileRepository.save(profile));
    }

    /**
     * Busca un perfil por el ID del usuario asociado.
     *
     * @param userId ID del usuario
     * @return Optional con el DTO del perfil si existe
     */
    @Override
    public Optional<ProfileDTO> findByUserId(Long userId) {
        return profileRepository.findByUser_Id(userId)
                .map(profileMapper::toDTO);
    }

    /**
     * Elimina un perfil asociado a un usuario por su ID.
     *
     * @param userId ID del usuario cuyo perfil será eliminado
     */
    @Transactional
    @Override
    public void deleteByUserId(Long userId) {
        profileRepository.deleteByUser_Id(userId);

    }

}

