package com.example.backend.dto.student;

import java.util.List;

public record StudentCartListResponse(List<StudentCartItem> items, Integer totalCredits, Integer totalCount) {}
