package com.smoking_area.backend.repository;

import com.smoking_area.backend.entity.Bookmark;
import com.smoking_area.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUserId(Long userId);

    Optional<Bookmark> findByUserAndSmokingAreaId(User user, Long smokingAreaId);

    void deleteByUserAndSmokingAreaId(User user, Long smokingAreaId);
}
