package domain.gbfs;

import lombok.Data;

/**
 * @author Lisa Croxford <lisac@softwarerad.com>
 */
@Data
public class StationInformation {

    private long last_updated;

    private StationList data;
}
