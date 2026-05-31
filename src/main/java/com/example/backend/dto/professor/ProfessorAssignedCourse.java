package com.example.backend.dto.professor;

public class ProfessorAssignedCourse {

  private String courseId;
  private String courseName;
  private String division;
  private Integer studentCount;
  private Integer maxStudents;
  private Double satisfaction;

  public ProfessorAssignedCourse() {}

  public ProfessorAssignedCourse(
      String courseId,
      String courseName,
      String division,
      Integer studentCount,
      Integer maxStudents,
      Double satisfaction) {
    this.courseId = courseId;
    this.courseName = courseName;
    this.division = division;
    this.studentCount = studentCount;
    this.maxStudents = maxStudents;
    this.satisfaction = satisfaction;
  }

  public String getCourseId() {
    return courseId;
  }

  public void setCourseId(String courseId) {
    this.courseId = courseId;
  }

  public String getCourseName() {
    return courseName;
  }

  public void setCourseName(String courseName) {
    this.courseName = courseName;
  }

  public String getDivision() {
    return division;
  }

  public void setDivision(String division) {
    this.division = division;
  }

  public Integer getStudentCount() {
    return studentCount;
  }

  public void setStudentCount(Integer studentCount) {
    this.studentCount = studentCount;
  }

  public Integer getMaxStudents() {
    return maxStudents;
  }

  public void setMaxStudents(Integer maxStudents) {
    this.maxStudents = maxStudents;
  }

  public Double getSatisfaction() {
    return satisfaction;
  }

  public void setSatisfaction(Double satisfaction) {
    this.satisfaction = satisfaction;
  }
}
