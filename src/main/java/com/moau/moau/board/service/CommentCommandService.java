package com.moau.moau.board.service;

import com.moau.moau.board.domain.Comment;
import com.moau.moau.board.domain.Post;
import com.moau.moau.board.dto.request.CommentRequestDto;
import com.moau.moau.global.exception.error.BoardError;
import com.moau.moau.board.repository.CommentRepository;
import com.moau.moau.board.repository.PostRepository;
import com.moau.moau.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentCommandService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public Long createComment(Long userId, Long postId, CommentRequestDto dto) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new BusinessException(BoardError.POST_NOT_FOUND));

        Comment comment = Comment.builder()
                .post(post)
                .authorUserId(userId)
                .content(dto.content())
                .isAnonymous(dto.isAnonymous())
                .parentId(dto.parentId())
                .build();

        commentRepository.save(comment);

        post.increaseCommentCount();

        return comment.getId();
    }

    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new BusinessException(BoardError.COMMENT_NOT_FOUND));

        if (!comment.getAuthorUserId().equals(userId)) {
            throw new BusinessException(BoardError.UNAUTHORIZED_ACCESS);
        }

        comment.delete();

        comment.getPost().decreaseCommentCount();
    }

}