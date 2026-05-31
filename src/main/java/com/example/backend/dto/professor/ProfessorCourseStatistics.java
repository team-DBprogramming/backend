package com.example.backend.dto.professor;

public class ProfessorCourseStatistics {

  private Integer totalCourses;
  private Integer totalStudents;
  private Double avgSatisfaction;

  public ProfessorCourseStatistics() {}

  public ProfessorCourseStatistics(Integer totalCourses, Integer totalStudents, Double avgSatisfaction) {
    this.totalCourses = totalCourses;
    this.totalStudents = totalStudents;
    this.avgSatisfaction = avgSatisfaction;
  }

  public Integer getTotalCourses() {
    return totalCourses;
  }

  public void setTotalCourses(Integer totalCourses) {
    this.totalCourses = totalCourses;
  }

  public Integer getTotalStudents() {
    return totalStudents;
  }

  public void setTotalStudents(Integer totalStudents) {
    this.totalStudents = totalStudents;
  }

  public Double getAvgSatisfaction() {
    return avgSatisfaction;
  }

  public void setAvgSatisfaction(Double avgSatisfaction) {
    this.avgSatisfaction = avgSatisfaction;
  }
}
