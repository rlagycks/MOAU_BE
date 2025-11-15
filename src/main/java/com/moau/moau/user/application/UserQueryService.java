// src/main/java/com/moau/moau/user/application/UserQueryService.java
package com.moau.moau.user.application;

import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.user.domain.User;
import com.moau.moau.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserQueryService {
    private final UserRepository users;

    @Transactional(readOnly = true)
    public User getByIdOrThrow(Long id) {
        return users.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(CommonError.USER_NOT_FOUND.getMessage())
                );
    }
}
