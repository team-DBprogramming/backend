package com.example.backend.dto.professor;

public class ProfessorTodaySchedule {

  private String courseId;
  private String courseName;
  private String division;
  private Integer studentCount;
  private String startTime;
  private String endTime;
  private String room;

  public ProfessorTodaySchedule() {}

  public ProfessorTodaySchedule(
      String courseId,
      String courseName,
      String division,
      Integer studentCount,
      String startTime,
      String endTime,
      String room) {
    this.courseId = courseId;
    this.courseName = courseName;
    this.division = division;
    this.studentCount = studentCount;
    this.startTime = startTime;
    this.endTime = endTime;
    this.room = room;
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
}
