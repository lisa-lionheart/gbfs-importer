package domain.gbfs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author Lisa Croxford <lisac@softwarerad.com>
 */
@Data
@ToString
public class Station {

    @JsonProperty("station_id")
    String stationId;

    // Name of the station
    String name;

    @JsonProperty("short_name")
    String shortName;

    double lat;

    double lon;

    String address;

    String cross_street;

    String region_id;

    String post_code;

    List<String> rental_methods;

    int capacity;
}
