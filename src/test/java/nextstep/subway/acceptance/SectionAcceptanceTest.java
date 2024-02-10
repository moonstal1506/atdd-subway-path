package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SectionAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setUp() {
        StationSteps.createStation("강남역");
        StationSteps.createStation("역삼역");
        StationSteps.createStation("선릉역");
        StationSteps.createStation("삼성역");
        LineSteps.createLine("2호선", "green", 1L, 2L, 10L);
    }

    /**
     * When 지하철 노선에 구간을 등록하면
     * Then 지하철 노선에 구간이 등록된다.
     */
    @DisplayName("지하철 노선에 구간을 등록한다.")
    @Test
    void createSection() {
        // when
        ExtractableResponse<Response> response = SectionSteps.createSection(1L, 2L, 3L, 10L);
        String locationHeader = response.header("Location");

        // then
        List<Long> lineStationIds = LineSteps.getLineStationIds(locationHeader);
        assertThat(lineStationIds).contains(2L, 3L);
    }

    /**
     * When 새로운 구간의 상행역이 해당 노선에 등록되어있는 하행 종점역이 아닌 구간을 등록하면
     * Then 에러를 반환한다.
     */
    @DisplayName("새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이 아니면 에러를 반환한다.")
    @Test
    void validateNextSection() {
        // when
        ExtractableResponse<Response> response = SectionSteps.createSection(1L, 3L, 4L, 10L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo("구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이 아닙니다.");
    }

    /**
     * When 이미 해당 노선에 등록되어있는 역을 등록하면
     * Then 에러를 반환한다.
     */
    @DisplayName("이미 해당 노선에 등록되어있는 역이면 에러를 반환한다.")
    @Test
    void validateDuplicateStation() {
        // when
        ExtractableResponse<Response> response = SectionSteps.createSection(1L, 2L, 1L, 10L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo("이미 등록되어있는 역입니다.");
    }

    /**
     * Given 구간을 생성하고
     * When 그 구간을 삭제하면
     * Then 노선 조회 시 등록한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 삭제하면 지하철역 목록 조회 시 생성한 역을 찾을 수 없다.")
    @Test
    void deleteSection() {
        // given
        ExtractableResponse<Response> response = SectionSteps.createSection(1L, 2L, 3L, 10L);
        String locationHeader = response.header("Location");

        // when
        SectionSteps.deleteSection(1L, 3L);

        // then
        List<Long> lineStationIds = LineSteps.getLineStationIds(locationHeader);
        assertThat(lineStationIds).doesNotContain(3L);
    }

    /**
     * Given 구간을 생성하고
     * When 마지막 구간이 아닌 구간을 삭제하면
     * Then 에러를 반환한다.
     */
    @DisplayName("마지막 구간이 아닌 구간을 삭제하면 에러를 반환한다.")
    @Test
    void validateEndSection() {
        // given
        SectionSteps.createSection(1L, 2L, 3L, 10L);

        // when
        ExtractableResponse<Response> response = SectionSteps.deleteSection(1L, 2L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo("마지막 구간만 제거할 수 있습니다.");
    }

    /**
     * When 구간이 1개인 경우 삭제하면
     * Then 에러를 반환한다.
     */
    @DisplayName("구간이 1개인 경우 삭제하면 에러를 반환한다.")
    @Test
    void validateLastSection() {
        // when
        ExtractableResponse<Response> response = SectionSteps.deleteSection(1L, 2L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo("구간이 1개인 경우 역을 삭제할 수 없습니다.");
    }
}
