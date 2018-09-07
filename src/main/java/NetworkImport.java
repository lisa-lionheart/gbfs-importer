import de.westnordost.osmapi.map.MapDataDao;
import de.westnordost.osmapi.map.changes.MapDataChangesWriter;
import de.westnordost.osmapi.map.data.*;
import de.westnordost.osmapi.map.handler.MapDataHandler;
import domain.gbfs.Network;
import domain.gbfs.RootNode;
import domain.gbfs.Station;
import domain.gbfs.StationInformation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Generate a change set for the bike hire network
 *
 * @author Lisa Croxford <lisac@softwarerad.com>
 */
@AllArgsConstructor
@Slf4j
public class NetworkImport {

    final Network network;

    final MapDataDao data;

    final List<Element> newNodes = new LinkedList<>();

    final List<Element> modifiedNodes = new LinkedList<>();


    void proccessStations() {
        RootNode node = network.open();
        StationInformation stationInfo = node.getFeed("station_information", "en", StationInformation.class);
        List<Station> stations = stationInfo.getData().getStations();
        for (Station station : stations) {
            processStation(station);
        }
    }

    void processStation(Station station) {
        LatLon location = new OsmLatLon(station.getLat(),station.getLon());

        List<Node> existing = this.findExistingNode(location);
        if(existing.size() > 0) {

            if(existing.size() > 1) {
                log.warn("Too many existing nodes!!!");
                return;
            }

            Node node = existing.get(0);

            modifiedNodes.add(new OsmNode(
                    node.getId(),
                    node.getVersion()+1,
                    location,
                    generateNodeTags(station)
            ));
        } else {
          newNodes.add(new OsmNode(
              -(newNodes.size()+1),
              0,
              location,
              generateNodeTags(station)
          ));
        }

    }


    List<Node> findExistingNode(LatLon location) {

        List<Node> results = new LinkedList<>();

        // 10 arc seconds, about 300 meters at the equator, more in terms of longitude further
        // north, really rough estimate
        // TODO: Make this a better query
        double size =  10 / 360f;

        BoundingBox boundingBox = new BoundingBox(
                location.getLatitude() - size, location.getLongitude() - size,
                location.getLatitude() + size, location.getLongitude() + size
        );

        data.getMap(boundingBox, new MapDataHandler() {
            @Override
            public void handle(BoundingBox bounds) {
            }

            @Override
            public void handle(Node node) {
                String amenity = node.getTags().get("amenity");
                if(amenity.equals("bicycle_rental")) {
                    results.add(node);
                }
            }

            @Override
            public void handle(Way way) {

            }

            @Override
            public void handle(Relation relation) {
            }
        });

        return results;
    }

    Map<String,String> generateNodeTags(Station station){

        Map<String, String> tags = new HashMap<>();
        tags.put("name", station.getName());
        tags.put("amenity", "bicycle_rental");
        tags.put("ref", station.getStationId());
        tags.put("capacity", station.getCapacity() + "");
        tags.put("network", network.getSystemId());
        tags.put("operator", network.getName());
        tags.put("website", network.getWebsite());
        return tags;
    }


    public void dumpChangeSet(String filename) throws IOException {

        List<Element> allNodes = new LinkedList<>();
        allNodes.addAll(newNodes);
        allNodes.addAll(modifiedNodes);
        MapDataChangesWriter writer = new MapDataChangesWriter(0, allNodes);
        writer.write(new FileOutputStream(filename));
    }
}
