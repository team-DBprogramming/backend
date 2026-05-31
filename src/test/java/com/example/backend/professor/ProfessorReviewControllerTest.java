package com.example.backend.professor;

import static com.example.backend.support.TestAuthentications.professorUser;
import static com.example.backend.support.TestAuthentications.withProfessorAuthentication;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backend.controller.ProfessorReviewController;
import com.example.backend.dto.professor.ProfessorReviewResponse;
import com.example.backend.dto.professor.ProfessorReviewResponse.ItemAverages;
import com.example.backend.dto.professor.ProfessorReviewResponse.ReviewItem;
import com.example.backend.dto.professor.ProfessorReviewResponse.Summary;
import com.example.backend.service.ProfessorReviewService;
import com.example.backend.utils.JwtTokenProvider;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProfessorReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfessorReviewControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProfessorReviewService reviewService;
  @MockitoBean private JwtTokenProvider tokenProvider;

  @Test
  void getReviewsReturnsProjectApiResponseFormatWithoutRemovedFields() throws Exception {
    when(reviewService.getReviews(
            eq(professorUser()), eq("CSE301"), eq("01"), eq("2026-1"), eq("LATEST")))
        .thenReturn(
            new ProfessorReviewResponse(
                new Summary(4.4, 82, 44),
                new ItemAverages(4.6, 4.5, 3.8, 4.7),
                List.of(
                    new ReviewItem(
                        "REV_001",
                        5.0,
                        "2026.05.10",
                        "Good practice",
                        "Many assignments",
                        "Prepare SQL",
                        "익명"))));

    mockMvc
        .perform(
            get("/professors/me/courses/CSE301/reviews")
                .with(withProfessorAuthentication())
                .queryParam("division", "01")
                .queryParam("semester", "2026-1")
                .queryParam("sort", "LATEST"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isSuccess").value(true))
        .andExpect(jsonPath("$.code").value("PROFESSOR200"))
        .andExpect(jsonPath("$.result.summary.avgRating").value(4.4))
        .andExpect(jsonPath("$.result.summary.participationRate").value(82))
        .andExpect(jsonPath("$.result.summary.participantCount").value(44))
        .andExpect(jsonPath("$.result.summary.courseCount").doesNotExist())
        .andExpect(jsonPath("$.result.semesterTrends").doesNotExist())
        .andExpect(jsonPath("$.result.itemAverages.kindness").value(4.7))
        .andExpect(jsonPath("$.result.reviews[0].reviewId").value("REV_001"))
        .andExpect(jsonPath("$.result.reviews[0].writer").value("익명"));
  }
}
