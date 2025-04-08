# Java 17 기반 이미지 사용
FROM eclipse-temurin:17-jdk

# 작업 디렉토리 설정
WORKDIR /app

# 소스 복사
COPY . .

# 실행 권한 부여 (윈도우에서 올라온 gradlew는 권한 필요함)
RUN chmod +x ./gradlew

# 빌드
RUN ./gradlew build -x test

# 실행
CMD ["java", "-jar", "build/libs/example-0.0.1-SNAPSHOT.jar"]
