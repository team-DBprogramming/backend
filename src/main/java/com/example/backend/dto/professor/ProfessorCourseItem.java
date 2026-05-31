package com.example.backend.dto.professor;

public class ProfessorCourseItem {

  private String courseId;
  private String courseName;
  private String division;
  private Integer credit;
  private String schedule;
  private String room;
  private Integer capacity;
  private Integer enrolled;
  private Double avgSatisfaction;

  public ProfessorCourseItem() {}

  public ProfessorCourseItem(
      String courseId,
      String courseName,
      String division,
      Integer credit,
      String schedule,
      String room,
      Integer capacity,
      Integer enrolled,
      Double avgSatisfaction) {
    this.courseId = courseId;
    this.courseName = courseName;
    this.division = division;
    this.credit = credit;
    this.schedule = schedule;
    this.room = room;
    this.capacity = capacity;
    this.enrolled = enrolled;
    this.avgSatisfaction = avgSatisfaction;
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

  public Integer getCapacity() {
    return capacity;
  }

  public void setCapacity(Integer capacity) {
    this.capacity = capacity;
  }

  public Integer getEnrolled() {
    return enrolled;
  }

  public void setEnrolled(Integer enrolled) {
    this.enrolled = enrolled;
  }

  public Double getAvgSatisfaction() {
    return avgSatisfaction;
  }

  public void setAvgSatisfaction(Double avgSatisfaction) {
    this.avgSatisfaction = avgSatisfaction;
  }
}
