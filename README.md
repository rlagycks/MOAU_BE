# MOAU Backend

MOAU(Make Our Association Unified) 백엔드 서버입니다.  
동아리/모임 운영에 필요한 팀 관리, 가입 요청, 공지, 게시판, 일정, 회계 기능을 Spring Boot 기반 REST API로 제공합니다.

## 상태

- 프로젝트 상태: 완료
- 현재 기준: 신규 기능 개발 종료, 유지보수/참고용 저장소
- 기준 브랜치: `develop`

## 핵심 기능

- 인증/인가: Kakao OAuth 로그인, JWT Access/Refresh Token
- 팀 관리: 팀 생성, 멤버 관리, 역할 기반 권한 제어
- 가입 요청: 가입 신청, 승인, 거절 워크플로우
- 게시판: 게시글/댓글, 익명 작성 지원
- 공지/투표: 공지 작성, 이미지 첨부, 투표 기능
- 일정: 팀 일정, 개인 일정 조회
- 회계: 카테고리, 계좌, 거래 내역, 회비 관리
- 영수증 처리: S3 업로드, OCR, AI 기반 정보 추출, 검토 프로세스

## 기술 스택

- Java 21
- Spring Boot 3.5.6
- Spring Web MVC, WebFlux
- Spring Security, JWT
- Spring Data JPA, Hibernate
- MySQL 8
- Springdoc OpenAPI (Swagger UI)
- AWS S3
- Naver Clova OCR
- OpenAI API
- GitHub Actions, Docker Compose

## 프로젝트 구조

```text
src/main/java/com/moau/moau
├── accounting    # 회계, 회비, 영수증, 계좌
├── auth          # Kakao OAuth
├── board         # 게시글, 댓글
├── global        # 공통 설정, 예외, 보안, 유틸
├── jwt           # JWT 인증 처리
├── notice        # 공지
├── poll          # 투표
├── request       # 가입 요청
├── schedule      # 일정
├── team          # 팀/멤버/권한
├── token         # Refresh Token
└── user          # 사용자
```

서비스 계층은 쓰기/읽기를 분리한 형태로 구성되어 있으며, 일부 도메인은 `CommandService` / `QueryService`로 나뉘어 있습니다.

## 실행 방법

### 1. 로컬 DB 실행

```bash
docker compose -f docker-compose.example.yml up -d
```

기본 포트는 `3307`입니다.

### 2. 개발 설정 파일 준비

예시 파일을 복사해 `src/main/resources/application-dev.yml`을 만든 뒤 값만 채우면 됩니다.

```bash
cp src/main/resources/application-dev.example.yml src/main/resources/application-dev.yml
```

### 3. 애플리케이션 실행

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

기본 애플리케이션 포트는 Spring Boot 기본값인 `8080`입니다.

## 주요 환경 설정

로컬/운영 공통으로 아래 값들이 필요합니다.

| 구분 | 변수 |
| --- | --- |
| DB | `RDS_ENDPOINT`, `RDS_DB`, `RDS_USER`, `RDS_PASS` |
| JWT | `SPRING_SECURITY_JWT_SECRET`, `SPRING_SECURITY_JWT_ACCESSTOKENEXPIRATIONMSEC`, `SPRING_SECURITY_JWT_REFRESHTOKENEXPIRATIONMSEC` |
| AWS S3 | `AWS_REGION`, `AWS_S3_BUCKET`, `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` |
| Kakao OAuth | `APP_KAKAO_REST_API_KEY`, `APP_KAKAO_TOKEN_URI`, `APP_KAKAO_USER_ME_URI`, `APP_KAKAO_REDIRECT_URIS_0` |
| OCR | `APP_NAVER_OCR_API_URL`, `APP_NAVER_OCR_SECRET_KEY` |
| OpenAI | `APP_OPENAI_API_KEY` |

## API 문서 및 헬스 체크

- Swagger UI: `/swagger-ui/index.html`
- OpenAPI JSON: `/v3/api-docs`
- Health Check: `/actuator/health`

## 테스트 및 CI

### 로컬 테스트

```bash
./gradlew test
```

CI에서는 `docker-compose.ci.yml`과 `application-ci.yml`을 사용합니다.  
로컬에서도 동일한 테스트를 재현하려면 CI용 환경 변수를 먼저 준비해야 합니다.

### GitHub Actions

- `ci.yml`: `main`, `develop` 및 기능 브랜치 대상 테스트/이미지 빌드
- `deploy.yml`: `main` 브랜치 push 시 AWS 배포

## 배포 개요

- 컨테이너 이미지: `ghcr.io/rlagycks/moau_be`
- 배포 대상: AWS EC2
- 배포 방식: GitHub Actions + AWS SSM + Docker Compose

## 저장소 정리 기준

- `build/`, `bin/`, `.gradle/` 등 생성 산출물은 커밋하지 않습니다.
- `.claude/`, `.serena/`, `.specify/` 등 로컬 AI 도구 폴더는 로컬 전용으로 취급합니다.
- 실제 비밀값은 절대 커밋하지 않고, 예시 파일만 유지합니다.

## 참고 파일

- [build.gradle](build.gradle)
- [docker-compose.example.yml](docker-compose.example.yml)
- [docker-compose.ci.yml](docker-compose.ci.yml)
- [src/main/resources/application.yml](src/main/resources/application.yml)
- [src/main/resources/application-dev.example.yml](src/main/resources/application-dev.example.yml)
- [.github/workflows/ci.yml](.github/workflows/ci.yml)
- [.github/workflows/deploy.yml](.github/workflows/deploy.yml)
