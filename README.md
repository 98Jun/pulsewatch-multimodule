# PulseWatch Multi-Module

멀티모듈 기반의 Spring Boot 프로젝트 뼈대입니다.  
API 모듈에서 요청을 받고, Kafka 이벤트로 비동기 처리(Worker) 및 후속 알림(Notifier)으로 확장할 수 있는 구조로 설계했습니다.  
현재는 **API 접근 시 필터 단에서 요청/응답 로그를 남기고 DB에 저장하는 기능**이 구현되어 있습니다.

---

## 핵심 목표

- Gradle 멀티모듈 구조에서 역할 분리 연습
- API → Kafka Producer → Worker Consumer → 결과 발행 → Notifier Consumer 형태의 비동기 파이프라인 구축 기반 마련
- 모든 API 요청/응답에 대한 **공통 로깅/저장**을 필터 레벨에서 일괄 처리

---

## 모듈 구성

Repo 루트 기준 모듈은 다음과 같습니다.

```
pulsewatch-multimodule
├─ api        : REST API, Swagger, Logging Filter, (Producer 예정)
├─ worker     : Kafka Consumer 기반 처리 모듈(비즈니스/DB 적재 담당 예정)
├─ notifier   : Kafka Consumer 기반 알림/후처리 모듈 예정
└─ common     : api/worker/notifier가 공유하는 이벤트 DTO/상수 모듈
```

### 역할 요약

- **api**
  - 외부 요청 진입점
  - 공통 로깅 필터(ApiLoggingFilter)에서 요청/응답 로그 수집 및 DB 저장
  - 추후 Kafka Producer(요청 발행) 추가 예정

- **worker**
  - Kafka로 들어온 “작업 요청”을 소비(consume)해 실제 비즈니스 처리
  - 처리 결과를 다시 Kafka로 발행(produce)하는 구조로 확장 예정

- **notifier**
  - “처리 결과” 토픽을 구독해 알림(텔레그램/메일/슬랙 등) 및 후속 처리 담당 예정

- **common**
  - 모듈 간 메시지(이벤트 DTO) **공유 계약(Contract)**
  - 실행되는 앱이 아니라 “공용 클래스 라이브러리”

---

## 기술 스택

- Java 21
- Spring Boot 3.5.7
- Gradle 멀티모듈
- Spring Security + JWT 로그인(뼈대/의존성 포함)
- MyBatis
- Kafka
- Swagger(OpenAPI UI)
- MariaDB/MySQL + HikariCP
- Lombok

---

## 아키텍처 흐름(예정 포함)

```
[Client]
   |
   v
[API Module]
   | 1) 요청 수신 + 검증 + 로그 저장
   | 2) (예정) monitor.job.requested 토픽에 이벤트 발행
   v
[Kafka Topic: monitor.job.requested]
   |
   v
[Worker Module]
   | 3) 이벤트 소비 → 실제 처리/DB 적재
   | 4) monitor.job.result 토픽에 결과 발행
   v
[Kafka Topic: monitor.job.result]
   |
   v
[Notifier Module]
   | 5) 결과 소비 → 알림/후처리
```

---

## 실행 방법

### 1) 전체 빌드
```bash
./gradlew clean build
```

### 2) 모듈별 실행
```bash
./gradlew :api:bootRun
./gradlew :worker:bootRun
./gradlew :notifier:bootRun
```

---

## Swagger 확인

API 모듈 실행 후:

```
http://localhost:8080/swagger-ui.html
```

---

## 환경설정(application.yml)

각 모듈은 `src/main/resources/application.yml`을 따로 가집니다.

### Kafka 브로커
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
```

### DB 연결 (api/worker/notifier에 존재)
```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/pulsewatch
    username: root
    password: your_password
```

---

## API Logging Filter

`api` 모듈에서 모든 HTTP 요청/응답을 필터 레벨에서 수집합니다.

- 요청 URI / Method / Status
- Request Body / Response Body (텍스트/JSON만, 길이 제한)
- 특정 URL(스웨거/actuator 등)은 로깅 스킵 가능

현재 필터는:
1. 요청/응답을 ContentCaching Wrapper로 감싸고  
2. 체인 종료 후 바디를 꺼내 로그  
3. DB에 insert 하는 구조입니다.



---

## DB 테이블 예시 (로그 저장용)

```sql
CREATE TABLE api_access_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  method VARCHAR(10) NOT NULL,
  uri VARCHAR(255) NOT NULL,
  query TEXT NULL,
  status INT NOT NULL,
  reqBody MEDIUMTEXT NULL,
  resBody MEDIUMTEXT NULL,
  created_at Timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

---

## Roadmap

- [ ] API → Kafka Producer 이벤트 발행 뼈대 추가
- [ ] Worker / Notifier Consumer 실제 처리 로직 붙이기
- [ ] Access Log를 API에서 직접 저장하지 않고 Kafka로 넘겨 Worker에서 저장하도록 분리(성능/안정성 강화)
- [ ] traceId를 Kafka 헤더로 전파해 전체 파이프라인 로그 연동
- [ ] 예외/민감정보 마스킹 및 로깅 정책 고도화

---

