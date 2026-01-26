package com.moau.moau.board.service;

import com.moau.moau.board.domain.Comment;
import com.moau.moau.board.domain.Post;
import com.moau.moau.board.dto.response.CommentResponseDto;
import com.moau.moau.board.dto.response.PostDetailResponseDto;
import com.moau.moau.board.dto.response.PostResponseDto;
import com.moau.moau.global.exception.error.BoardError;
import com.moau.moau.board.repository.CommentRepository;
import com.moau.moau.board.repository.PostRepository;
import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.user.domain.User;
import com.moau.moau.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    // 목록 조회
    public Page<PostResponseDto> getPosts(Long currentUserId, Long teamId, Pageable pageable) {
        Page<Post> posts = postRepository.findAllByTeamIdAndDeletedAtIsNull(teamId, pageable);

        // N+1 쿼리 방지: 익명이 아닌 게시글의 작성자 ID 수집 후 일괄 조회
        Set<Long> authorIds = posts.stream()
                .filter(post -> !post.isAnonymous())
                .map(Post::getAuthorUserId)
                .collect(Collectors.toSet());

        Map<Long, String> userNicknameMap = userRepository.findAllByIdIn(authorIds).stream()
                .collect(Collectors.toMap(User::getId, User::getNickname));

        return posts.map(post -> {
            String displayName = resolveDisplayName(post.isAnonymous(), post.getAuthorUserId(), currentUserId, userNicknameMap);

            // 본문 미리보기 (50자 제한)
            String preview = post.getContent().length() > 50
                    ? post.getContent().substring(0, 50) + "..."
                    : post.getContent();

            return new PostResponseDto(
                    post.getId(),
                    post.getTitle(),
                    preview,
                    displayName,
                    post.getCommentCount(),
                    post.getCreatedAt()
            );
        });
    }

    // 상세 조회
    public PostDetailResponseDto getPostDetail(Long currentUserId, Long teamId, Long postId) {
        // 1. 게시글 조회
        Post post = postRepository.findByIdAndTeamIdAndDeletedAtIsNull(postId, teamId)
                .orElseThrow(() -> new BusinessException(BoardError.POST_NOT_FOUND));

        // 2. 댓글 조회
        List<Comment> comments = commentRepository.findAllByPostIdAndDeletedAtIsNullOrderByCreatedAtAsc(postId);

        // 3. N+1 쿼리 방지: 게시글 + 댓글 작성자 ID 수집 후 일괄 조회
        Set<Long> authorIds = Stream.concat(
                Stream.of(post).filter(p -> !p.isAnonymous()).map(Post::getAuthorUserId),
                comments.stream().filter(c -> !c.isAnonymous()).map(Comment::getAuthorUserId)
        ).collect(Collectors.toSet());

        Map<Long, String> userNicknameMap = userRepository.findAllByIdIn(authorIds).stream()
                .collect(Collectors.toMap(User::getId, User::getNickname));

        String postAuthorName = resolveDisplayName(post.isAnonymous(), post.getAuthorUserId(), currentUserId, userNicknameMap);
        boolean isMyPost = post.getAuthorUserId().equals(currentUserId);

        // 4. 댓글 변환
        List<CommentResponseDto> commentDtos = comments.stream().map(comment -> {
            String commentAuthorName = resolveDisplayName(comment.isAnonymous(), comment.getAuthorUserId(), currentUserId, userNicknameMap);
            boolean isMyComment = comment.getAuthorUserId().equals(currentUserId);

            return new CommentResponseDto(
                    comment.getId(),
                    comment.getParentId(),
                    comment.getContent(),
                    commentAuthorName,
                    isMyComment,
                    comment.getCreatedAt()
            );
        }).toList();

        return new PostDetailResponseDto(
                post.getId(),
                post.getAuthorUserId(),
                post.getTitle(),
                post.getContent(),
                postAuthorName,
                isMyPost,
                post.getCommentCount(),
                post.getCreatedAt(),
                commentDtos
        );
    }

    // [Helper] 익명 여부와 본인 여부에 따른 이름 결정 (일괄 조회 버전)
    private String resolveDisplayName(boolean isAnonymous, Long authorId, Long currentUserId, Map<Long, String> userNicknameMap) {
        if (isAnonymous) {
            return authorId.equals(currentUserId) ? "익명(나)" : "익명";
        } else {
            // 일괄 조회한 Map에서 닉네임 조회
            return userNicknameMap.getOrDefault(authorId, "(알수없음)");
        }
    }

    // [Helper] 익명 여부와 본인 여부에 따른 이름 결정 (단건 조회 버전 - 하위 호환성)
    private String resolveDisplayName(boolean isAnonymous, Long authorId, Long currentUserId) {
        if (isAnonymous) {
            return authorId.equals(currentUserId) ? "익명(나)" : "익명";
        } else {
            // 실명이면 DB에서 닉네임 조회 (캐싱 권장)
            return userRepository.findById(authorId)
                    .map(User::getNickname)
                    .orElse("(알수없음)");
        }
    }
}