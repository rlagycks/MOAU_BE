package com.moau.moau.user.application;

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

    @Transactional
    public UpsertResult upsertKakaoUser(long kakaoId, String email, String nickname) {
        String lowered = (email == null) ? null : email.toLowerCase();

        int affected = users.upsertKakao(kakaoId, lowered, nickname);

        User user = users.findByIdAndDeletedAtIsNull(kakaoId)
                .orElseThrow(() ->
                        new IllegalStateException(CommonError.USER_NOT_FOUND.getMessage())
                );

        boolean isNew = (affected == 1);
        return new UpsertResult(user, isNew);
    }

    public record UpsertResult(User user, boolean isNew) {}

    @Transactional
    public User findOrCreateTestUser(String email) {

        Optional<User> optionalUser = users.findByEmail(email);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        long testKakaoId = email.hashCode() & 0xFFFFFFFFL;
        String testNickname = "TestUser-" + email.split("@")[0];

        User newUser = User.builder()
                .id(testKakaoId) // 카카오 ID를 PK로 사용
                .email(email)
                .nickname(testNickname)
                // .role("USER") // User 엔티티의 @Builder에 따라 필요시 추가
                .build();

        // 4. save()를 통해 INSERT (JPA Auditing이 createdAt 등을 자동 처리)
        return users.save(newUser);
    }
}