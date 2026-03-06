# Team Board

팀별 업무를 게시판 단위로 관리하는 협업 툴입니다.
게시판 참가 요청 → 승인 → 업무 등록 → 검토 → 최종 승인의 워크플로우를 지원합니다.

## 기술 스택

| 구분 | 기술 |
|---|---|
| Backend | Spring Boot 3.3.3, Java 17 |
| ORM | MyBatis (Annotation 기반) |
| DB | MySQL 8.0 (Docker) |
| DB 마이그레이션 | Flyway |
| Template | Thymeleaf |
| Security | Spring Security 6 |
| Build | Gradle |
| Frontend | SCSS, Bootstrap 5 |

## 권한 체계

사용자는 **시스템 레벨**과 **게시판 레벨** 두 가지 권한을 동시에 가집니다.

### 시스템 레벨 (`members.role`)

| 역할 | 설명 |
|---|---|
| `ROLE_ADMIN` | 전체 회원/게시판 관리, 서비스 전체 설정 접근 |
| `ROLE_USER` | 게시판 생성 및 참가 요청 가능 |

### 게시판 레벨 (`board_members.board_role`)

| 역할 | 설명 |
|---|---|
| `MANAGER` | 멤버 관리, 게시글 승인/반려/수정, 담당자 변경 |
| `USER` | 게시판 내 게시글·댓글 작성 및 조회 |
| `REQUESTED` | 가입 요청 상태, 승인 전까지 접근 불가 |

## 업무 워크플로우

### 게시판 참가
1. 유저가 참가 요청 전송 → `board_role = REQUESTED`
2. 해당 게시판 MANAGER가 승인 → `board_role = USER`

### 게시글 처리

| 상태 | 의미 | 전환 주체 |
|---|---|---|
| `PROGRESS` | 진행 중 (기본값) | 작성자 자유 수정 가능 |
| `REQUESTED` | 승인 대기 | USER가 완료 요청 시, 수정 불가 |
| `APPROVED` | 승인 완료 | MANAGER 승인 시 |
| `REJECTED` | 반려 | MANAGER 반려 + 사유 작성, 재요청 가능 |

### 담당자(Assignee)
- 게시글 작성자는 게시판 소속 멤버를 담당자로 지정 가능 (작성자 자동 포함)
- 담당자는 해당 게시글 수정 및 완료 요청 가능
- 담당자 추가/제거는 작성자 또는 MANAGER만 가능

## 주요 기능

- 게시판 생성 / 참가 요청 및 승인 시스템
- 게시판·시스템 이중 권한 체계
- 업무 상태 관리 (PROGRESS → REQUESTED → APPROVED / REJECTED)
- 담당자 지정 시스템
- 반려 사유 기록 및 재요청
- 파일 첨부 및 다운로드 (최대 10MB)
- 마감일 설정 및 D-day 표시
- 댓글 기능

## DB 스키마

```
boards ──< board_members >── members
boards ──< posts ──< comments
                └─< post_files
                └─< post_members >── members
                └─< post_rejections
```

## 프로젝트 구조

```
src/main/
├── java/.../
│   ├── config/       # SecurityConfig
│   ├── controller/   # AdminController, BoardController, PostController 등
│   ├── model/        # Board, Post, Member, BoardMember, PostMember 등
│   │   └── dto/
│   ├── service/      # 비즈니스 로직
│   └── mapper/       # MyBatis 매퍼 인터페이스
└── resources/
    ├── db/migration/ # Flyway SQL 마이그레이션
    ├── templates/    # Thymeleaf 템플릿
    └── static/       # SCSS, JS
```