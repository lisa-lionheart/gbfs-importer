import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.map.MapDataDao;
import de.westnordost.osmapi.map.data.Element;
import domain.gbfs.Network;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author Lisa Croxford <lisac@softwarerad.com>
 */
@Slf4j
public class Importer {


    public static void main(String[] args) throws IOException {

        OsmConnection osm = new OsmConnection(
                "http://api.openstreetmap.org/api/0.6/",
                "gbfs-importer", null);

        MapDataDao mapDataDao = new MapDataDao(osm);

        List<Element> nodes = new ArrayList<>();

        for(Network network : Network.getAllNetworks()) {
            try {
                NetworkImport networkImport = new NetworkImport(network, mapDataDao);
                networkImport.proccessStations();
                networkImport.dumpChangeSet(network.getName() + ".osc");

            }catch (Exception e) {
                log.error("Failed parsing network {}", network.getName(), e);
            }
        }



    }
}
