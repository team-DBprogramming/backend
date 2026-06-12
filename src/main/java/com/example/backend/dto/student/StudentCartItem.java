package com.example.backend.dto.student;

public class StudentCartItem {

  private Long cartItemId;
  private String courseId;
  private String courseName;
  private String professor;
  private String division;
  private Integer credit;
  private String schedule;
  private String room;
  private String seatStatus;
  private String conflictWith;
  private Boolean isApplicable;

  public StudentCartItem() {}

  public Long getCartItemId() {
    return cartItemId;
  }

  public void setCartItemId(Long cartItemId) {
    this.cartItemId = cartItemId;
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

  public String getProfessor() {
    return professor;
  }

  public void setProfessor(String professor) {
    this.professor = professor;
  }

  public String getDivision() {
    return division;
  }

  public void setDivision(String division) {
    this.division = division;
  }

  public Integer getCredit() {
    return credit;
  }

  public void setCredit(Integer credit) {
    this.credit = credit;
  }

  public String getSchedule() {
    return schedule;
  }

  public void setSchedule(String schedule) {
    this.schedule = schedule;
  }

  public String getRoom() {
    return room;
  }

  public void setRoom(String room) {
    this.room = room;
  }

  public String getSeatStatus() {
    return seatStatus;
  }

  public void setSeatStatus(String seatStatus) {
    this.seatStatus = seatStatus;
  }

  public String getConflictWith() {
    return conflictWith;
  }

  public void setConflictWith(String conflictWith) {
    this.conflictWith = conflictWith;
  }

  public Boolean getIsApplicable() {
    return isApplicable;
  }

  public void setIsApplicable(Boolean applicable) {
    isApplicable = applicable;
  }
}
