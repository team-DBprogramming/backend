package com.example.backend.notification;

import static com.example.backend.support.TestAuthentications.withProfessorAuthentication;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backend.controller.NotificationController;
import com.example.backend.dto.notification.NotificationDetailResponse;
import com.example.backend.dto.notification.NotificationItem;
import com.example.backend.dto.notification.NotificationListResponse;
import com.example.backend.service.NotificationService;
import com.example.backend.utils.JwtTokenProvider;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private NotificationService notificationService;
  @MockitoBean private JwtTokenProvider tokenProvider;

  @Test
  void getNotificationsReturnsProjectApiResponseFormat() throws Exception {
    when(notificationService.getNotifications(eq(10L)))
        .thenReturn(
            new NotificationListResponse(
                List.of(
                    new NotificationItem(
                        "1",
                        "새 강의 평가",
                        "데이터베이스개론에 새 강의 평가가 등록되었습니다.",
                        "COURSE_REVIEW",
                        false,
                        "2026-06-01 10:15",
                        "101",
                        null))));

    mockMvc
        .perform(get("/notifications").with(withProfessorAuthentication()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isSuccess").value(true))
        .andExpect(jsonPath("$.code").value("NOTIFICATION200"))
        .andExpect(jsonPath("$.result.notifications[0].notificationId").value("1"))
        .andExpect(jsonPath("$.result.notifications[0].title").value("새 강의 평가"))
        .andExpect(jsonPath("$.result.notifications[0].isRead").value(false))
        .andExpect(jsonPath("$.result.notifications[0].targetSectionId").value("101"));
  }

  @Test
  void getNotificationReturnsDetailForPopup() throws Exception {
    when(notificationService.getNotification(eq(10L), eq("7")))
        .thenReturn(
            new NotificationDetailResponse(
                "7",
                "수강 요청",
                "학생으로부터 새 메시지가 도착했습니다.",
                "COURSE_REQUEST",
                false,
                "2026-06-01 09:30",
                "홍길동",
                "CSE301",
                "데이터베이스개론",
                "01분반",
                "201",
                "301",
                "전공 필수 과목으로 수강이 필요합니다."));

    mockMvc
        .perform(get("/notifications/7").with(withProfessorAuthentication()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isSuccess").value(true))
        .andExpect(jsonPath("$.code").value("NOTIFICATION200"))
        .andExpect(jsonPath("$.result.notificationId").value("7"))
        .andExpect(jsonPath("$.result.senderName").value("홍길동"))
        .andExpect(jsonPath("$.result.courseName").value("데이터베이스개론"))
        .andExpect(jsonPath("$.result.requestReason").value("전공 필수 과목으로 수강이 필요합니다."));
  }

  @Test
  void markNotificationAsReadReturnsProjectApiResponseFormat() throws Exception {
    mockMvc
        .perform(patch("/notifications/7/read").with(withProfessorAuthentication()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isSuccess").value(true))
        .andExpect(jsonPath("$.code").value("NOTIFICATION200"))
        .andExpect(jsonPath("$.result").doesNotExist());

    verify(notificationService).markAsRead(eq(10L), eq("7"));
  }
}
