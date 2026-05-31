package com.example.backend.dto.professor;

public record ProfessorStudentExportFile(String filename, String contentType, byte[] bytes) {}
