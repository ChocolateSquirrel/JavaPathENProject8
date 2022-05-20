package tourGuide.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import tourGuide.model.Attraction;
import tourGuide.model.VisitedLocation;

import java.util.List;

@FeignClient(name = "ms-gps", url = "ms-gps:6060")
public interface GpsProxy {

    @RequestMapping("getAttractions")
    public List<Attraction> getAttractions();

    @GetMapping("getLocation/{userId}")
    public VisitedLocation getUserLocation(@PathVariable String userId);

}
