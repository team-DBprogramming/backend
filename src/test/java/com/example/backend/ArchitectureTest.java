package com.example.backend;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class ArchitectureTest {

  @Test
  void authImplementationUsesLayeredMapperArchitecture() throws IOException {
    Path sourceRoot = Path.of("src/main/java/com/example/backend");

    try (Stream<Path> paths = Files.walk(sourceRoot)) {
      assertThat(paths.map(Path::toString).filter(path -> path.contains("\\auth\\repository\\")))
          .isEmpty();
    }

    assertThat(Files.exists(sourceRoot.resolve("controller/AuthController.java"))).isTrue();
    assertThat(Files.exists(sourceRoot.resolve("service/AuthService.java"))).isTrue();
    assertThat(Files.exists(sourceRoot.resolve("mapper/AuthMapper.java"))).isTrue();
    assertThat(Files.exists(sourceRoot.resolve("mapper/RefreshTokenMapper.java"))).isTrue();
    assertThat(Files.exists(Path.of("src/main/resources/mappers/auth/AuthMapper.xml"))).isTrue();
    assertThat(Files.exists(Path.of("src/main/resources/mappers/auth/RefreshTokenMapper.xml"))).isTrue();
    assertThat(Files.exists(sourceRoot.resolve("utils/JwtTokenProvider.java"))).isTrue();
    assertThat(Files.exists(sourceRoot.resolve("apiPayload/exception/handler/AuthHandler.java"))).isTrue();
  }

  @Test
  void mybatisSqlIsDefinedInXmlMapperFiles() throws IOException {
    Path sourceRoot = Path.of("src/main/java/com/example/backend");
    String authMapper = Files.readString(sourceRoot.resolve("mapper/AuthMapper.java"));
    String refreshTokenMapper = Files.readString(sourceRoot.resolve("mapper/RefreshTokenMapper.java"));

    assertThat(authMapper).doesNotContain("@Select", "@Insert", "@Update", "@Delete");
    assertThat(refreshTokenMapper).doesNotContain("@Select", "@Insert", "@Update", "@Delete");

    String authMapperXml = Files.readString(Path.of("src/main/resources/mappers/auth/AuthMapper.xml"));
    String refreshTokenMapperXml =
        Files.readString(Path.of("src/main/resources/mappers/auth/RefreshTokenMapper.xml"));

    assertThat(authMapperXml).contains("namespace=\"com.example.backend.mapper.AuthMapper\"");
    assertThat(refreshTokenMapperXml)
        .contains("namespace=\"com.example.backend.mapper.RefreshTokenMapper\"");
  }

  @Test
  void authCodeUsesAuthHandlerForAuthFailures() throws IOException {
    Path sourceRoot = Path.of("src/main/java/com/example/backend");

    String authService = Files.readString(sourceRoot.resolve("service/AuthService.java"));
    String jwtTokenProvider = Files.readString(sourceRoot.resolve("utils/JwtTokenProvider.java"));

    assertThat(authService).contains("new AuthHandler(");
    assertThat(jwtTokenProvider).contains("new AuthHandler(");
    assertThat(authService).doesNotContain("new GeneralException(");
    assertThat(jwtTokenProvider).doesNotContain("new GeneralException(");
  }
}
