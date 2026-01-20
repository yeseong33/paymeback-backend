# PayMeBack Backend 🚀

Spring Boot 기반 REST API 서버

## 📋 프로젝트 개요

PayMeBack의 백엔드 API 서버입니다. Spring Boot를 기반으로 구축된 RESTful API를 제공합니다.

## 🛠️ 기술 스택

- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Database**: H2 (개발), PostgreSQL (운영)
- **Security**: Spring Security + JWT
- **Build Tool**: Gradle
- **Documentation**: OpenAPI 3.0 (Swagger)

## 🏗️ 프로젝트 구조

```
src/
├── main/
│   ├── java/com/paymeback/
│   │   ├── common/          # 공통 설정 및 유틸리티
│   │   ├── domain/
│   │   │   ├── gathering/   # 모임 관련 기능
│   │   │   ├── payment/     # 결제 관련 기능
│   │   │   ├── security/    # 보안 및 인증
│   │   │   └── user/        # 사용자 관련 기능
│   │   └── PayMeBackApplication.java
│   └── resources/
│       ├── application.properties
│       └── application-dev.properties
└── test/
```

## 🚀 실행 방법

### 개발 환경 실행

```bash
# 권한 부여 (필요시)
chmod +x gradlew

# 애플리케이션 실행
./gradlew bootRun

# 또는 개발 프로파일로 실행
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### 빌드

```bash
# 프로젝트 빌드
./gradlew build

# 테스트 실행
./gradlew test

# JAR 파일 생성
./gradlew bootJar
```

## 📚 API 문서

서버 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## 🔧 주요 설정

### 데이터베이스

#### 개발 환경 (H2)
- **URL**: `jdbc:h2:mem:testdb`
- **Console**: `http://localhost:8080/h2-console`
- **Username**: `sa`
- **Password**: (공백)

#### 운영 환경 (PostgreSQL)
환경변수를 통해 설정:
```bash
DB_URL=jdbc:postgresql://localhost:5432/paymeback
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

### JWT 설정

```bash
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000  # 24시간
```

## 🌟 주요 기능

### 인증 & 사용자 관리
- 사용자 회원가입/로그인
- JWT 토큰 기반 인증
- OTP 인증

### 모임 관리
- 모임 생성/조회/수정/삭제
- QR코드 생성
- 모임 참가자 관리

### 결제 관리
- 결제 요청 생성
- 결제 처리
- 결제 내역 조회

## 🔒 보안

- Spring Security를 통한 인증/인가
- JWT 토큰 기반 무상태 인증
- CORS 설정
- 입력값 검증

## 🧪 테스트

```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests "*AuthServiceTest"

# 테스트 커버리지 확인
./gradlew jacocoTestReport
```

## 🐳 Docker

```bash
# Docker 이미지 빌드
docker build -t paymeback-backend .

# Docker 컨테이너 실행
docker run -p 8080:8080 paymeback-backend

# Docker Compose 실행
docker-compose up
```

## 📝 API 엔드포인트

### 인증
- `POST /api/auth/signup` - 회원가입
- `POST /api/auth/signin` - 로그인
- `POST /api/auth/verify-otp` - OTP 인증

### 모임
- `GET /api/gatherings` - 모임 목록 조회
- `POST /api/gatherings` - 모임 생성
- `GET /api/gatherings/{id}` - 모임 상세 조회
- `POST /api/gatherings/{id}/join` - 모임 참가

### 결제
- `GET /api/payments` - 결제 내역 조회
- `POST /api/payment-requests` - 결제 요청 생성
- `POST /api/payments/process` - 결제 처리

## 🤝 기여하기

1. 새로운 기능 브랜치 생성
2. 기능 개발 및 테스트 작성
3. 코드 스타일 준수 (Google Java Style Guide)
4. PR 생성

## 📞 문의

- **개발자**: [yeseong33](https://github.com/yeseong33)
- **이슈**: [GitHub Issues](https://github.com/yeseong33/paymeback-backend/issues) 