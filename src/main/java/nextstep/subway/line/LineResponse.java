package nextstep.subway.line;


import nextstep.subway.station.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {

    private Long id;
    private String name;
    private List<StationResponse> stations;

    public LineResponse(Line line) {
        this.id = line.getId();
        this.name = line.getName();
        this.stations = line.getOrderedStations()
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
