package com.moau.moau.global.security;

import com.moau.moau.team.domain.TeamMemberRole; // (import 경로 확인)

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckTeamRole {
    TeamMemberRole value() default TeamMemberRole.MEMBER;
}