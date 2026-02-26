# Team Board

팀별 업무를 칸반 스타일로 관리하는 협업 툴입니다.
게시판(팀) 단위로 업무를 등록·관리하고, 파일 첨부·댓글·마감일 설정 등의 기능을 지원합니다.

## 기술 스택

| 구분 | 기술 |
|---|---|
| Backend | Spring Boot 3.3.3, Java 17 |
| ORM | JPA + MyBatis |
| DB | MySQL 8.0 (Docker) |
| DB 마이그레이션 | Flyway |
| Template | Thymeleaf |
| Security | Spring Security 6 |
| Build | Gradle |
| Frontend | SCSS, Bootstrap 5 |

## 주요 기능

- 팀(게시판)별 칸반 보드로 업무 시각화
- 업무 상태 관리 (진행 중 → 승인 요청 → 완료)
- 파일 첨부 및 다운로드 (최대 10MB)
- 마감일 설정 및 D-day 표시
- 댓글(피드백) 기능
- 관리자 페이지 - 게시판 생성/수정/삭제, 회원 관리, 승인 요청 처리
- Spring Security 기반 로그인 / 역할 기반 접근 제어 (ROLE_USER, ROLE_ADMIN)

## DB 스키마

```
boards ──< posts ──< comments
                 └─< post_files
members ──< posts
members ──< comments
boards ──< members
```

## 프로젝트 구조

```
src/main/
├── java/.../
│   ├── config/       # SecurityConfig
│   ├── controller/   # AdminController, BoardController, PostController 등
│   ├── model/        # Board, Post, Member, Comment, PostFile
│   │   └── dto/
│   ├── service/      # 비즈니스 로직
│   └── mapper/       # MyBatis 매퍼 인터페이스
└── resources/
    ├── db/migration/ # Flyway SQL 마이그레이션
    ├── templates/    # Thymeleaf 템플릿
    └── static/       # CSS(SCSS), JS
```
