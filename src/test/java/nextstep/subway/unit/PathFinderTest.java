package nextstep.subway.unit;

import nextstep.subway.exception.SubwayException;
import nextstep.subway.line.Line;
import nextstep.subway.path.Path;
import nextstep.subway.path.PathFinder;
import nextstep.subway.section.Section;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PathFinderTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");

        이호선 = new Line("2호선", "green", 교대역, 강남역, 10L);
        신분당선 = new Line("신분당선", "red", 강남역, 양재역, 10L);
        삼호선 = new Line("3호선", "orange", 교대역, 남부터미널역, 2L);

        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3L));
    }

    @DisplayName("최단 경로를 조회한다")
    @Test
    void findPath() {
        PathFinder pathFinder = new PathFinder();
        Path path = pathFinder.findPath(Arrays.asList(이호선, 신분당선, 삼호선), 교대역, 양재역);

        assertThat(path.getStations()).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(path.getDistance()).isEqualTo(5L);
    }

    @DisplayName("출발역과 도착역이 같은 경우 에러를 반환한다.")
    @Test
    void validateEqualsStation() {
        PathFinder pathFinder = new PathFinder();

        assertThatThrownBy(() -> pathFinder.findPath(Arrays.asList(이호선, 신분당선, 삼호선), 교대역, 교대역))
                .isInstanceOf(SubwayException.class)
                .hasMessageContaining("출발역과 도착역이 같습니다.");
    }

}
