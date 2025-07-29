package com.gabeust.forohub.repository;

import com.gabeust.forohub.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface IProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUser_Id(Long userId);
    @Modifying
    @Query("DELETE FROM Profile p WHERE p.user.id = :userId")
    void deleteByUser_Id(Long userId);

}
