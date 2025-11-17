// src/main/java/com/moau/moau/user/service/UserCommandService.java
package com.moau.moau.user.service;

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
     * - kakaoId로 먼저 조회
     * - 있으면 email/nickname 업데이트
     * - 없으면 새 User.createWithKakao(...) 저장
     */
    @Transactional
    public UpsertResult upsertKakaoUser(long kakaoId, String email, String nickname) {
        String lowered = (email == null) ? null : email.toLowerCase();

        Optional<User> existingOpt = users.findByKakaoId(kakaoId);

        User user;
        boolean isNew;

        if (existingOpt.isPresent()) {
            // 기존 유저: 프로필만 갱신
            user = existingOpt.get();
            user.changeEmail(lowered);
            user.changeNickname(nickname);
            isNew = false;
        } else {
            // 신규 유저 생성
            user = User.createWithKakao(kakaoId, lowered, nickname);
            user = users.save(user); // 여기서 id(AUTO_INCREMENT)가 채워짐
            isNew = true;
        }

        return new UpsertResult(user, isNew);
    }

    public record UpsertResult(User user, boolean isNew) {}

    /**
     * 테스트용 유저 조회/생성
     * - email로 먼저 찾고,
     * - 없으면 가짜 kakaoId 생성해서 User.createWithKakao로 생성
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
}
