package com.example.backend.dto.student;

public class StudentInfo {

  private String name;
  private String major;
  private Integer grade;
  private String currentSemester;

  public StudentInfo() {}

  public StudentInfo(String name, String major, Integer grade, String currentSemester) {
    this.name = name;
    this.major = major;
    this.grade = grade;
    this.currentSemester = currentSemester;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMajor() {
    return major;
  }

  public void setMajor(String major) {
    this.major = major;
  }

  public Integer getGrade() {
    return grade;
  }

  public void setGrade(Integer grade) {
    this.grade = grade;
  }

  public String getCurrentSemester() {
    return currentSemester;
  }

  public void setCurrentSemester(String currentSemester) {
    this.currentSemester = currentSemester;
  }
}
