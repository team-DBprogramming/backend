package com.example.backend.service;

import com.example.backend.apiPayload.code.status.ErrorStatus;
import com.example.backend.apiPayload.exception.handler.ProfessorHandler;
import com.example.backend.dto.professor.ProfessorStudentExportCourse;
import com.example.backend.dto.professor.ProfessorStudentExportFile;
import com.example.backend.dto.professor.ProfessorStudentExportRow;
import com.example.backend.mapper.ProfessorStudentExportMapper;
import com.example.backend.security.AuthenticatedUser;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessorStudentExportService {

  private static final String XLSX_CONTENT_TYPE =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

  private final ProfessorStudentExportMapper exportMapper;
  private final Clock clock;

  public ProfessorStudentExportService(ProfessorStudentExportMapper exportMapper, Clock clock) {
    this.exportMapper = exportMapper;
    this.clock = clock;
  }

  @Transactional(readOnly = true)
  public ProfessorStudentExportFile exportStudents(
      AuthenticatedUser currentUser,
      String courseId,
      String division,
      String format,
      String keyword,
      Integer grade,
      String major) {
    Long professorUserId = currentUser.requireProfessorUserId();
    String normalizedDivision = normalizeRequiredDivision(division);
    String normalizedFormat = normalizeFormat(format);
    ProfessorStudentExportCourse course = getCourse(professorUserId, courseId, normalizedDivision);
    List<ProfessorStudentExportRow> rows =
        getStudents(professorUserId, courseId, normalizedDivision, normalize(keyword), grade, normalize(major));

    return new ProfessorStudentExportFile(
        filename(course, normalizedFormat), XLSX_CONTENT_TYPE, toXlsx(rows));
  }

  private ProfessorStudentExportCourse getCourse(
      Long professorUserId, String courseId, String division) {
    Map<String, Object> params = new HashMap<>();
    params.put("professorUserId", professorUserId);
    params.put("courseId", courseId);
    params.put("division", division);
    exportMapper.callGetExportCourse(params);
    handleResult(stringValue(params.get("result")));
    List<ProfessorStudentExportCourse> courses = listValue(params.get("course"));
    if (courses.isEmpty()) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_REQUEST_NOT_FOUND);
    }
    return courses.get(0);
  }

  private List<ProfessorStudentExportRow> getStudents(
      Long professorUserId,
      String courseId,
      String division,
      String keyword,
      Integer grade,
      String major) {
    Map<String, Object> params = new HashMap<>();
    params.put("professorUserId", professorUserId);
    params.put("courseId", courseId);
    params.put("division", division);
    params.put("keyword", keyword);
    params.put("grade", grade);
    params.put("major", major);
    exportMapper.callGetExportStudents(params);
    handleResult(stringValue(params.get("result")));
    return listValue(params.get("rows"));
  }

  private void handleResult(String result) {
    if (result == null || "SUCCESS".equals(result)) {
      return;
    }
    if ("DIVISION_REQUIRED".equals(result)) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_DIVISION_REQUIRED);
    }
    throw new ProfessorHandler(ErrorStatus.PROFESSOR_REQUEST_NOT_FOUND);
  }

  private String normalizeFormat(String format) {
    String normalized = normalize(format);
    if (!"xlsx".equals(normalized)) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_EXPORT_INVALID_FORMAT);
    }
    return normalized;
  }

  private String normalizeRequiredDivision(String division) {
    if (isBlank(division)) {
      throw new ProfessorHandler(ErrorStatus.PROFESSOR_DIVISION_REQUIRED);
    }
    return division.trim();
  }

  private String filename(ProfessorStudentExportCourse course, String format) {
    return "학생관리_"
        + sanitizeFilename(course.courseName())
        + "_"
        + sanitizeFilename(course.semester())
        + "_"
        + LocalDate.now(clock)
        + "."
        + format;
  }

  private byte[] toXlsx(List<ProfessorStudentExportRow> rows) {
    try {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      try (ZipOutputStream zip = new ZipOutputStream(output, StandardCharsets.UTF_8)) {
        put(zip, "[Content_Types].xml", contentTypesXml());
        put(zip, "_rels/.rels", relsXml());
        put(zip, "xl/workbook.xml", workbookXml());
        put(zip, "xl/_rels/workbook.xml.rels", workbookRelsXml());
        put(zip, "xl/worksheets/sheet1.xml", sheetXml(rows));
      }
      return output.toByteArray();
    } catch (Exception e) {
      throw new IllegalStateException("Failed to create xlsx file", e);
    }
  }

  private void put(ZipOutputStream zip, String name, String content) throws Exception {
    zip.putNextEntry(new ZipEntry(name));
    zip.write(content.getBytes(StandardCharsets.UTF_8));
    zip.closeEntry();
  }

  private String sheetXml(List<ProfessorStudentExportRow> rows) {
    StringBuilder sheet = new StringBuilder();
    sheet.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        .append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">")
        .append("<sheetData>");
    row(sheet, 1, List.of("studentId", "name", "grade", "major", "status", "enrolledAt", "note"));
    int rowIndex = 2;
    for (ProfessorStudentExportRow data : rows) {
      row(
          sheet,
          rowIndex++,
          Arrays.asList(
              data.studentId(),
              data.name(),
              String.valueOf(data.grade()),
              data.major(),
              data.status(),
              data.enrolledAt(),
              data.note()));
    }
    sheet.append("</sheetData></worksheet>");
    return sheet.toString();
  }

  private void row(StringBuilder sheet, int rowIndex, List<String> values) {
    sheet.append("<row r=\"").append(rowIndex).append("\">");
    for (int column = 0; column < values.size(); column++) {
      sheet
          .append("<c r=\"")
          .append((char) ('A' + column))
          .append(rowIndex)
          .append("\" t=\"inlineStr\"><is><t>")
          .append(xml(values.get(column)))
          .append("</t></is></c>");
    }
    sheet.append("</row>");
  }

  private String contentTypesXml() {
    return """
        <?xml version="1.0" encoding="UTF-8"?>
        <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
          <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
          <Default Extension="xml" ContentType="application/xml"/>
          <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
          <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
        </Types>
        """;
  }

  private String relsXml() {
    return """
        <?xml version="1.0" encoding="UTF-8"?>
        <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
          <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
        </Relationships>
        """;
  }

  private String workbookXml() {
    return """
        <?xml version="1.0" encoding="UTF-8"?>
        <workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
          <sheets>
            <sheet name="students" sheetId="1" r:id="rId1"/>
          </sheets>
        </workbook>
        """;
  }

  private String workbookRelsXml() {
    return """
        <?xml version="1.0" encoding="UTF-8"?>
        <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
          <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
        </Relationships>
        """;
  }

  private String xml(String value) {
    String safe = value == null ? "" : value;
    return safe
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;");
  }

  private String sanitizeFilename(String value) {
    return value == null ? "" : value.replaceAll("[\\\\/:*?\"<>|]", "_");
  }

  private String normalize(String value) {
    return isBlank(value) ? null : value.trim().toLowerCase();
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private String stringValue(Object value) {
    return value == null ? null : String.valueOf(value);
  }

  @SuppressWarnings("unchecked")
  private <T> List<T> listValue(Object value) {
    return value instanceof List<?> list ? (List<T>) list : List.of();
  }
}
