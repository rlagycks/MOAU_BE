// src/main/java/com/moau/moau/user/application/UserCommandService.java
package com.moau.moau.user.application;

import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.user.domain.User;
import com.moau.moau.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRepository users;

    @Transactional
    public UpsertResult upsertKakaoUser(long kakaoId, String email, String nickname) {
        String lowered = (email == null) ? null : email.toLowerCase();

        // 하이버네이트 경합을 우회하는 네이티브 UPSERT
        int affected = users.upsertKakao(kakaoId, lowered, nickname);

        User user = users.findByIdAndDeletedAtIsNull(kakaoId)
                .orElseThrow(() ->
                        new IllegalStateException(CommonError.USER_NOT_FOUND.getMessage())
                );

        boolean isNew = (affected == 1); // 일반적으로 insert=1, update=2
        return new UpsertResult(user, isNew);
    }

    public record UpsertResult(User user, boolean isNew) {}
}
