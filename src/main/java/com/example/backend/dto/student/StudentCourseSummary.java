package com.example.backend.dto.student;

import java.util.List;

public class StudentCourseSummary {

  private String courseId;
  private String courseName;
  private String professor;
  private String division;
  private String courseType;
  private Integer credit;
  private List<StudentLectureTime> lectureTimes;
  private String room;
  private Integer capacity;
  private Integer enrolled;
  private Integer cartCount;
  private Double rating;
  private Integer reviewCount;
  private Boolean isEnglish;
  private String seatStatus;
  private String year;

  public StudentCourseSummary() {}

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

  public String getCourseType() {
    return courseType;
  }

  public void setCourseType(String courseType) {
    this.courseType = courseType;
  }

  public Integer getCredit() {
    return credit;
  }

  public void setCredit(Integer credit) {
    this.credit = credit;
  }

  public List<StudentLectureTime> getLectureTimes() {
    return lectureTimes;
  }

  public void setLectureTimes(List<StudentLectureTime> lectureTimes) {
    this.lectureTimes = lectureTimes;
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

  public Integer getCartCount() {
    return cartCount;
  }

  public void setCartCount(Integer cartCount) {
    this.cartCount = cartCount;
  }

  public Double getRating() {
    return rating;
  }

  public void setRating(Double rating) {
    this.rating = rating;
  }

  public Integer getReviewCount() {
    return reviewCount;
  }

  public void setReviewCount(Integer reviewCount) {
    this.reviewCount = reviewCount;
  }

  public Boolean getIsEnglish() {
    return isEnglish;
  }

  public void setIsEnglish(Boolean isEnglish) {
    this.isEnglish = isEnglish;
  }

  public String getSeatStatus() {
    return seatStatus;
  }

  public void setSeatStatus(String seatStatus) {
    this.seatStatus = seatStatus;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }
}
