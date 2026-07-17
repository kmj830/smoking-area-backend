# 1단계: 빌드 환경 구성
FROM gradle:8.5-jdk17 AS build
COPY --chown=gradle:gradle . /home/app
WORKDIR /home/app
RUN ./gradlew build -x test --no-daemon --refresh-dependencies

# 2단계: 실행 환경 구성 (공급 중단된 openjdk 대신 공식 지원되는 eclipse-temurin 사용)
FROM eclipse-temurin:17-jre
EXPOSE 8080
COPY --from=build /home/app/build/libs/*-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]