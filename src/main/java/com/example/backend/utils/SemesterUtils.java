package com.example.backend.utils;

import java.time.LocalDate;
public final class SemesterUtils {

  private SemesterUtils() {}

  public static Semester current(LocalDate date) {
    int month = date.getMonthValue();
    if (month <= 4) {
      return new Semester(date.getYear(), 1);
    }
    if (month <= 10) {
      return new Semester(date.getYear(), 2);
    }
    return new Semester(date.getYear() + 1, 1);
  }

  public static Semester parseOrCurrent(String value, LocalDate currentDate) {
    if (value == null || value.trim().isEmpty()) {
      return current(currentDate);
    }
    String normalized = value.trim().replaceAll("\\s+", "").replace("학기", "");
    String[] parts = normalized.split("-");
    if (parts.length != 2) {
      return current(currentDate);
    }
    try {
      int year = Integer.parseInt(parts[0]);
      int semester = Integer.parseInt(parts[1]);
      if (semester != 1 && semester != 2) {
        return current(currentDate);
      }
      return new Semester(year, semester);
    } catch (NumberFormatException e) {
      return current(currentDate);
    }
  }

  public static String format(Semester semester) {
    return semester.year() + "-" + semester.semester();
  }

  public record Semester(int year, int semester) {}
}
