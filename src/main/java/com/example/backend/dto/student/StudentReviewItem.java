package com.example.backend.dto.student;

public class StudentReviewItem {

  private String courseId;
  private String courseName;
  private String professor;
  private String semester;
  private Integer credit;
  private String status;
  private Integer ratingOverall;
  private String createdAt;

  public StudentReviewItem() {}

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

  public String getProfessor() {
    return professor;
  }

  public void setProfessor(String professor) {
    this.professor = professor;
  }

  public String getSemester() {
    return semester;
  }

  public void setSemester(String semester) {
    this.semester = semester;
  }

  public Integer getCredit() {
    return credit;
  }

  public void setCredit(Integer credit) {
    this.credit = credit;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Integer getRatingOverall() {
    return ratingOverall;
  }

  public void setRatingOverall(Integer ratingOverall) {
    this.ratingOverall = ratingOverall;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }
}
