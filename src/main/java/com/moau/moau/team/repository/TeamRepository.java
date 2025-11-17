// src/main/java/com/moau/moau/team/repository/TeamRepository.java
package com.moau.moau.team.repository;

import com.moau.moau.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    // 1) 오너가 가진 팀 목록 (삭제 안 된 것만)
    List<Team> findByOwnerIdAndDeletedAtIsNull(Long ownerId);

    // 2) 초대 코드로 팀 조회 (삭제 안 된 팀만)
    Optional<Team> findByInviteCodeAndDeletedAtIsNull(String inviteCode);

    // 3) 초대 코드 중복 체크 (삭제 안 된 팀만 기준)
    boolean existsByInviteCodeAndDeletedAtIsNull(String inviteCode);

    // 4) 단건 조회도 삭제 안 된 팀만
    Optional<Team> findByIdAndDeletedAtIsNull(Long id);

    @Modifying(clearAutomatically = true)
    @Query("update Team t set t.deletedAt = :when where t.id = :id")
    int softDeleteById(@Param("id") Long id, @Param("when") Instant when);
}
