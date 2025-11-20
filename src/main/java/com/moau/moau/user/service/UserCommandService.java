package com.moau.moau.user.service;

import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.user.domain.User;
import com.moau.moau.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRepository users;

    /**
     * 카카오 프로필 기반 유저 upsert
     */
    @Transactional
    public UpsertResult upsertKakaoUser(long kakaoId, String email, String nickname) {
        String lowered = (email == null) ? null : email.toLowerCase();

        Optional<User> existingOpt = users.findByKakaoId(kakaoId);

        User user;
        boolean isNew;

        if (existingOpt.isPresent()) {
            // 기존 유저 업데이트
            user = existingOpt.get();
            user.changeEmail(lowered);
            user.changeNickname(nickname);
            isNew = false;
        } else {
            // 신규 유저 생성
            user = User.createWithKakao(kakaoId, lowered, nickname);
            user = users.save(user);
            isNew = true;
        }

        return new UpsertResult(user, isNew);
    }

    public record UpsertResult(User user, boolean isNew) {}

    /**
     * DEV 테스트 유저 조회/생성
     */
    @Transactional
    public User findOrCreateTestUser(String email) {
        Optional<User> optionalUser = users.findByEmail(email);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        long testKakaoId = email.hashCode() & 0xFFFFFFFFL;
        String testNickname = "TestUser-" + email.split("@")[0];

        User newUser = User.createWithKakao(testKakaoId, email, testNickname);
        return users.save(newUser);
    }

    /**
     * 내 닉네임 수정
     */
    @Transactional
    public User updateNickname(Long userId, String nickname) {

        User user = users.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(CommonError.USER_NOT_FOUND.getMessage())
                );

        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException(CommonError.USER_NICKNAME_EMPTY.getMessage());
        }

        user.changeNickname(nickname);
        return user;
    }

    /**
     * 내 정보 삭제
     */
    @Transactional
    public void deleteUser(Long userId) {

        User user = users.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(CommonError.USER_NOT_FOUND.getMessage())
                );

        users.delete(user);
    }
}
