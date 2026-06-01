package com.example.backend.dto.student;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StudentTodaySchedule {

  private String courseId;
  private String courseName;
  private String professor;
  private String startTime;
  private String endTime;
  private String room;
  private boolean isNext;

  public StudentTodaySchedule() {}

  public StudentTodaySchedule(
      String courseId,
      String courseName,
      String professor,
      String startTime,
      String endTime,
      String room,
      boolean isNext) {
    this.courseId = courseId;
    this.courseName = courseName;
    this.professor = professor;
    this.startTime = startTime;
    this.endTime = endTime;
    this.room = room;
    this.isNext = isNext;
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

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public String getRoom() {
    return room;
  }

  public void setRoom(String room) {
    this.room = room;
  }

  @JsonProperty("isNext")
  public boolean isNext() {
    return isNext;
  }

  public void setNext(boolean next) {
    isNext = next;
  }
}
