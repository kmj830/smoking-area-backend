package com.smoking_area.backend.config;

import com.smoking_area.backend.entity.SmokingArea;
import com.smoking_area.backend.repository.SmokingAreaRepository;
import com.smoking_area.backend.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SmokingAreaRepository smokingAreaRepository;
    private final GeocodingService geocodingService;

    @Override
    public void run(String... args) throws Exception {
        if (smokingAreaRepository.count() > 0) {
            log.info("▶️ 이미 데이터베이스에 흡연구역 데이터가 존재하므로 CSV 임포트를 건너뜁니다.");
            return;
        }

        log.info("▶️ CSV 파일로부터 주소 기반 데이터 초기화를 시작합니다...");

        ClassPathResource resource = new ClassPathResource("smoking_areas.csv");
        List<SmokingArea> smokingAreas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), Charset.forName("EUC-KR")))) {

            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] row = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                if (row.length < 4) continue;

                try {
                    String name = row[1].replace("\"", "").trim();
                    String fullAddress = row[2].replace("\"", "").trim();
                    String type = row[3].replace("\"", "").trim();

                    String cleanAddress = fullAddress;
                    if (fullAddress.contains(",")) {
                        cleanAddress = fullAddress.split(",")[0].trim();
                    }

                    log.info("📍 주소 변환 시도 중: {}", cleanAddress);

                    // 💡 수정된 GeocodingService 메서드를 호출하여 Map 반환값을 받습니다.
                    Map<String, Double> coordinateMap = geocodingService.getCoordinates(cleanAddress);

                    if (coordinateMap != null && coordinateMap.containsKey("latitude") && coordinateMap.containsKey("longitude")) {
                        SmokingArea area = new SmokingArea();
                        area.setName(name);
                        area.setAddress(fullAddress);
                        area.setLatitude(coordinateMap.get("latitude"));
                        area.setLongitude(coordinateMap.get("longitude"));
                        area.setType(type);
                        area.setDescription(fullAddress + "에 위치한 흡연시설입니다.");

                        smokingAreas.add(area);

                        Thread.sleep(100);
                    } else {
                        log.warn("⚠️ 위경도 변환 실패 (주소 불명확): {}", cleanAddress);
                    }

                } catch (Exception e) {
                    log.warn("❌ 행 파싱 에러 건너뛰기: {} | 원인: {}", line, e.getMessage());
                }
            }

            if (!smokingAreas.isEmpty()) {
                smokingAreaRepository.saveAll(smokingAreas);
                log.info("✅ 총 {}개의 흡연구역 데이터가 위경도 변환 후 DB에 저장되었습니다!", smokingAreas.size());
            }

        } catch (Exception e) {
            log.error("❌ CSV 처리 중 치명적 오류 발생: ", e);
        }
    }
}