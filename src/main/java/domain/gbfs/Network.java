package domain.gbfs;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.client.RestTemplate;
import util.CSVParserHttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lisa Croxford <lisac@softwarerad.com>
 */
@Data
@AllArgsConstructor
public class Network {


    private final String country;

    private final String name;

    private final String location;

    // System id of this network
    private final String systemId;

    // Website of this network
    private final String website;

    // The endpoint of the GBFS api for this network (gbfs.json)
    private final String url;

    /**
     * Fetch the list of networks from github
     */
    public static List<Network> getAllNetworks() {

        RestTemplate template = new RestTemplate();
        template.getMessageConverters().add(new CSVParserHttpMessageConverter(CSVFormat.DEFAULT.withFirstRecordAsHeader()));

        List<CSVRecord> csvData = template.getForObject("https://raw.githubusercontent.com/NABSA/gbfs/master/systems.csv", List.class);

        List<Network> result = new ArrayList<>(200);

        for(CSVRecord record : csvData) {

            result.add(new Network(
                    record.get("Country Code"),
                    record.get("Name"),
                    record.get("Location"),
                    record.get("System ID"),
                    record.get("URL"),
                    record.get("Auto-Discovery URL")
            ));
        }

        return result;
    }

    public RootNode open() {
        RestTemplate template = new RestTemplate();
        return template.getForObject(url, RootNode.class);
    }


}
