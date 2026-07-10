package com.smoking_area.backend.service;

import com.smoking_area.backend.entity.Bookmark;
import com.smoking_area.backend.entity.User;
import com.smoking_area.backend.repository.BookmarkRepository;
import com.smoking_area.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addBookmark(Long userId, Long smokingAreaId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

        if (bookmarkRepository.findByUserAndSmokingAreaId(user, smokingAreaId).isPresent()) {
            throw new RuntimeException("이미 즐겨찾기에 등록된 흡연구역입니다.");
        }

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .smokingAreaId(smokingAreaId)
                .build();
        bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void removeBookmark(Long userId, Long smokingAreaId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));
        bookmarkRepository.deleteByUserAndSmokingAreaId(user, smokingAreaId);
    }

    public List<Long> getBookmarkSmokingAreaIds(Long userId) {
        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(userId);
        return bookmarks.stream()
                .map(Bookmark::getSmokingAreaId)
                .collect(Collectors.toList());
    }
}
