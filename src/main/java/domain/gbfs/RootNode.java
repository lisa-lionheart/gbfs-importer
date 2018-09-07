package domain.gbfs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * gbfs.json see docs
 *
 * @author Lisa Croxford <lisac@softwarerad.com>
 */
@Data
public class RootNode {


    @JsonProperty("last_updated")
    private long lastUpdated;

    private int ttl;


    private Map<String, FeedList> data;



    public <T> T getFeed(String name, String lang, Class<T> type) {
        FeedList feeds = data.get(lang);
        if(feeds == null) {
            feeds = data.get("en");
        }

        for(Feed feed : feeds.getFeeds()) {
            if(feed.getName().equals(name)) {
                RestTemplate t = new RestTemplate();
                return t.getForObject(feed.getUrl(),type);
            }
        }

        throw new RuntimeException("Feed not available");
    }

}
