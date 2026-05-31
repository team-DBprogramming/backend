package com.example.backend.config;

public final class OpenApiExamples {

  private OpenApiExamples() {}

  public static final String LOGIN_REQUEST =
      """
      {
        "userId": "2024123456",
        "password": "1234",
        "rememberMe": true
      }
      """;

  public static final String LOGIN_RESPONSE =
      """
      {
        "isSuccess": true,
        "code": "AUTH200",
        "message": "로그인 성공",
        "result": {
          "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
          "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
          "accessTokenExpiresAt": "2026-06-01T10:30:00Z",
          "refreshTokenExpiresAt": "2026-06-08T10:00:00Z",
          "user": {
            "id": "u_1",
            "userId": "2024123456",
            "name": "홍길동",
            "role": "student",
            "department": "computer-science"
          }
        }
      }
      """;

  public static final String LOGOUT_REQUEST =
      """
      {
        "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
      }
      """;

  public static final String LOGOUT_RESPONSE =
      """
      {
        "isSuccess": true,
        "code": "AUTH200",
        "message": "로그아웃 성공"
      }
      """;

  public static final String TOKEN_REFRESH_RESPONSE =
      """
      {
        "isSuccess": true,
        "code": "AUTH200",
        "message": "Access Token 재발급 성공",
        "result": {
          "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
          "accessTokenExpiresAt": "2026-06-01T10:30:00Z"
        }
      }
      """;

  public static final String PROFESSOR_DASHBOARD_RESPONSE =
      """
      {
        "isSuccess": true,
        "code": "PROFESSOR200",
        "message": "교수 대시보드 조회 성공",
        "result": {
          "courseCount": 3,
          "totalStudents": 73,
          "totalCapacity": 100,
          "avgSatisfaction": 4.5,
          "newReviewCount": 5,
          "todaySchedule": [
            {
              "courseId": "CSE301",
              "courseName": "데이터베이스개론",
              "studentCount": 28,
              "startTime": "10:30",
              "endTime": "12:00",
              "room": "A동 301호",
              "scheduleStatus": "SCHEDULED"
            }
          ],
          "assignedCourses": [
            {
              "courseId": "CSE301",
              "courseName": "데이터베이스개론",
              "division": "01분반",
              "studentCount": 28,
              "maxStudents": 35,
              "satisfaction": 4.5
            }
          ]
        }
      }
      """;

  public static final String PROFESSOR_COURSES_RESPONSE =
      """
      {
        "isSuccess": true,
        "code": "PROFESSOR200",
        "message": "담당 강의 목록 조회 성공",
        "result": {
          "courses": [
            {
              "courseId": "CSE301",
              "courseName": "데이터베이스개론",
              "division": "01분반",
              "credit": 3,
              "schedule": "월수 10:30-12:00",
              "room": "A동 301호",
              "capacity": 35,
              "enrolled": 28,
              "avgSatisfaction": 4.5
            }
          ],
          "statistics": {
            "totalCourses": 4,
            "totalStudents": 91,
            "avgSatisfaction": 4.5
          }
        }
      }
      """;

  public static final String COURSE_REQUESTS_RESPONSE =
      """
      {
        "isSuccess": true,
        "code": "PROFESSOR200",
        "message": "수강 요청 조회 성공",
        "result": {
          "summary": {
            "courseName": "데이터베이스개론",
            "courseId": "CSE301",
            "division": "01분반",
            "semester": "2026-1학기",
            "totalStudents": 6,
            "requestCount": 3
          },
          "requests": [
            {
              "requestId": "req-db-001",
              "studentId": "2024111111",
              "name": "홍길동",
              "grade": 3,
              "major": "컴퓨터공학과",
              "createdAt": "2026-05-15 14:30",
              "reason": "전공 필수 과목으로 수강이 필요합니다."
            }
          ]
        }
      }
      """;

  public static final String COURSE_REQUEST_DECISION_REQUEST =
      """
      {
        "status": "APPROVED"
      }
      """;

  public static final String COURSE_REQUEST_DECISION_RESPONSE =
      """
      {
        "isSuccess": true,
        "code": "PROFESSOR201",
        "message": "수강 요청 처리가 완료되었습니다.",
        "result": {
          "requestId": "req-db-001",
          "status": "APPROVED",
          "updatedAt": "2026-05-21T22:15:30Z"
        }
      }
      """;

  public static final String PROFESSOR_STUDENTS_RESPONSE =
      """
      {
        "isSuccess": true,
        "code": "PROFESSOR200",
        "message": "수강생 목록 조회 성공",
        "result": {
          "summary": {
            "courseName": "데이터베이스개론",
            "courseId": "CSE301",
            "division": "01분반",
            "semester": "2026-1학기",
            "totalStudents": 6,
            "requestCount": 3
          },
          "students": [
            {
              "studentId": "2024111111",
              "name": "홍길동",
              "grade": 3,
              "major": "컴퓨터공학과",
              "isRetake": false
            }
          ]
        }
      }
      """;

  public static final String ERROR_RESPONSE =
      """
      {
        "isSuccess": false,
        "code": "AUTH401",
        "message": "유효하지 않은 토큰입니다.",
        "result": null
      }
      """;
}
