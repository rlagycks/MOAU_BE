package com.moau.moau.board.service;

import com.moau.moau.board.domain.Post;
import com.moau.moau.board.dto.request.PostRequestDto;
import com.moau.moau.global.exception.error.BoardError;
import com.moau.moau.board.repository.PostRepository;
import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.team.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostCommandService {

    private final PostRepository postRepository;
    private final TeamMemberRepository teamMemberRepository;

    public Long createPost(Long userId, Long teamId, PostRequestDto dto) {
        // Team 존재 + 멤버십 확인을 한 번에 (쿼리 1개로 통합)
        teamMemberRepository.findActiveMember(teamId, userId)
                .orElseThrow(() -> new BusinessException(CommonError.ACCESS_DENIED));

        Post post = Post.builder()
                .teamId(teamId)
                .authorUserId(userId)
                .title(dto.title())
                .content(dto.content())
                .isAnonymous(dto.isAnonymous())
                .build();

        return postRepository.save(post).getId();
    }

    // 게시글 수정
    public void updatePost(Long userId, Long postId, PostRequestDto dto) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new BusinessException(BoardError.POST_NOT_FOUND));

        // 작성자 본인 확인
        if (!post.getAuthorUserId().equals(userId)) {
            throw new BusinessException(BoardError.UNAUTHORIZED_ACCESS);
        }

        post.update(dto.title(), dto.content(), dto.isAnonymous());
    }

    // 게시글 삭제 (Soft Delete)
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new BusinessException(BoardError.POST_NOT_FOUND));

        if (!post.getAuthorUserId().equals(userId)) {
            throw new BusinessException(BoardError.UNAUTHORIZED_ACCESS);
        }

        post.delete();
    }
}