package com.example.backend.dto.student;

import java.util.List;

public record StudentBulkEnrollRequest(List<Long> cartItemIds) {}
