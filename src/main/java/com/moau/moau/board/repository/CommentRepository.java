package com.moau.moau.board.repository;

import com.moau.moau.board.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 1. 특정 게시글의 모든 댓글 조회 (삭제 안 된 것만)
    List<Comment> findAllByPostIdAndDeletedAtIsNullOrderByCreatedAtAsc(Long postId);

    // 2. 댓글 수정/삭제 시 확인용
    Optional<Comment> findByIdAndDeletedAtIsNull(Long id);

    // 3. Lazy Loading 방지: Post와 함께 조회
    @Query("SELECT c FROM Comment c JOIN FETCH c.post WHERE c.id = :commentId AND c.deletedAt IS NULL")
    Optional<Comment> findByIdWithPost(@Param("commentId") Long commentId);
}