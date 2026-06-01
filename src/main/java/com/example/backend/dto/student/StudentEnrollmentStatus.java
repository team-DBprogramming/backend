package com.example.backend.dto.student;

public class StudentEnrollmentStatus {

  private String status;
  private String deadline;
  private Integer daysLeft;

  public StudentEnrollmentStatus() {}

  public StudentEnrollmentStatus(String status, String deadline, Integer daysLeft) {
    this.status = status;
    this.deadline = deadline;
    this.daysLeft = daysLeft;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getDeadline() {
    return deadline;
  }

  public void setDeadline(String deadline) {
    this.deadline = deadline;
  }

  public Integer getDaysLeft() {
    return daysLeft;
  }

  public void setDaysLeft(Integer daysLeft) {
    this.daysLeft = daysLeft;
  }
}
