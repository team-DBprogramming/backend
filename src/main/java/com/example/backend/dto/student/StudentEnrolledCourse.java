package com.example.backend.dto.student;

public class StudentEnrolledCourse {

  private String courseId;
  private String courseName;
  private Integer credit;
  private String schedule;
  private String registeredAt;
  private String professor;
  private String room;

  public StudentEnrolledCourse() {}

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

  public String getRegisteredAt() {
    return registeredAt;
  }

  public void setRegisteredAt(String registeredAt) {
    this.registeredAt = registeredAt;
  }

  public String getProfessor() {
    return professor;
  }

  public void setProfessor(String professor) {
    this.professor = professor;
  }

  public String getRoom() {
    return room;
  }

  public void setRoom(String room) {
    this.room = room;
  }
}
