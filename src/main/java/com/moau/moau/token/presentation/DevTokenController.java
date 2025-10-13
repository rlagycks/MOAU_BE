package com.moau.moau.token.presentation;

import com.moau.moau.jwt.ports.JwtIssuerPort;
import com.moau.moau.token.domain.RefreshToken;
import com.moau.moau.token.domain.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional; // 또는 org.springframework.transaction.annotation.Transactional
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.moau.moau.jwt.infra.TokenHash.sha256;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dev/auth")
public class DevTokenController {

    private final JwtIssuerPort issuer;
    private final RefreshTokenRepository repo;

    public record IssueReq(Long userId) {}
    public record IssueRes(String accessToken, String refreshToken) {}

    @PostMapping("/issue")
    @Transactional
    public IssueRes issue(@RequestBody IssueReq req) {
        var userId = req.userId();

        var at = issuer.issueAccess(userId, "USER");
        var rt = issuer.issueRefresh(userId);

        // 1) 기존 RT 전부 삭제(누적/충돌 방지)
        repo.deleteAllByUserId(userId);

        // 2) 새 RT 1건 저장 (token_hash 타입 = DB와 일치해야 함)
        repo.save(RefreshToken.of(rt.jti(), userId, sha256(rt.token())));

        return new IssueRes(at.token(), rt.token());
    }
}
