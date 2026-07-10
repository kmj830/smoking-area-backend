package com.smoking_area.backend.controller;

import com.smoking_area.backend.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping("/{smokingAreaId}")
    public ResponseEntity<Map<String, String>> addBookmark(
            @AuthenticationPrincipal String userId,
            @PathVariable Long smokingAreaId) {
        bookmarkService.addBookmark(Long.valueOf(userId), smokingAreaId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "즐겨찾기에 추가되었습니다.");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{smokingAreaId}")
    public ResponseEntity<Map<String, String>> removeBookmark(
            @AuthenticationPrincipal String userId,
            @PathVariable Long smokingAreaId) {
        bookmarkService.removeBookmark(Long.valueOf(userId), smokingAreaId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "즐겨찾기에서 삭제되었습니다");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyBookmarks(@AuthenticationPrincipal String userId) {
        List<Long> smokingAreaIds = bookmarkService.getBookmarkSmokingAreaIds(Long.valueOf(userId));

        Map<String, Object> response = new HashMap<>();
        response.put("smokingAreaIds", smokingAreaIds);
        return ResponseEntity.ok(response);
    }
}
