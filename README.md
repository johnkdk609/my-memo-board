# MyMemo

개인 메모를 작성하고 관리할 수 있는 웹 애플리케이션입니다.

JWT 기반 인증을 통해 사용자별로 메모를 안전하게 구분하고, 카테고리 분류 및 고정 기능 등 실용적인 편의 요소도 함께 제공합니다.

<br><br>

## 프로젝트 소개

<b>MyMemo</b>는 간편하면서도 안전한 개인 메모장을 목표로 개발한 백엔드 중심의 웹 애플리케이션입니다.

사용자는 로그인 후 메모를 작성하고, 수정하거나 삭제하며, 고정 또는 분류할 수 있습니다.

또한 Swagger를 통해 직접 API 테스트가 가능하고, 인증 흐름도 명확히 분리되어 있습니다.

<br><br>

## 사용 기술 스택

- Java 17
- Spring Boot 3.2.5
- Spring Security
- MySQL
- JPA (Hibernate)
- JWT (Access Token 기반 인증 - Refresh Token 도입 예정)
- Swagger (springdoc-openapi)

<br><br>

## 기능 흐름도

아래는 MyMemo의 주요 기능 동작 흐름을 시각적으로 정리한 플로우 차트(flow-chart)입니다.

<details>
<summary>회원가입 플로우</summary>

![회원가입 플로우](./docs/signup-flow.png)
※ Whimsical에서 제작

</details>

<details>
<summary>로그인 플로우</summary>

![로그인 플로우](./docs/login-flow.png)
※ Whimsical에서 제작

</details>

<details>
<summary>메모 생성 플로우</summary>

![메모 생성 플로우](./docs/memo-create-flow.png)
※ Whimsical에서 제작

</details>