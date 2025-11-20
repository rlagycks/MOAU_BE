package com.moau.moau.board.repository;

import com.moau.moau.board.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 1. 게시글 목록 조회 (삭제 안 된 것만)
    Page<Post> findAllByTeamIdAndDeletedAtIsNull(Long teamId, Pageable pageable);

    // 2. 게시글 상세 조회 (삭제 안 된 것만)
    Optional<Post> findByIdAndDeletedAtIsNull(Long id);

    // 3. 수정/삭제 시 권한 및 소유권 확인용 (삭제 안 된 것만)
    Optional<Post> findByIdAndTeamIdAndDeletedAtIsNull(Long id, Long teamId);
}