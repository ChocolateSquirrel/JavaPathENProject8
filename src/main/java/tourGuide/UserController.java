package tourGuide;

import com.jsoniter.output.JsonStream;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.dto.NearAttractionDTO;
import tourGuide.model.Attraction;
import tourGuide.model.VisitedLocation;
import tourGuide.proxy.GpsProxy;
import tourGuide.proxy.RewardProxy;
import tourGuide.service.UserService;
import tourGuide.user.User;
import tripPricer.Provider;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;
    private final GpsProxy gpsProxy;
    private final RewardProxy rewardProxy;

    public UserController(UserService userService, GpsProxy gpsProxy, RewardProxy rewardProxy) {
        this.userService = userService;
        this.gpsProxy = gpsProxy;
        this.rewardProxy = rewardProxy;
    }

    @RequestMapping("/getAttractions")
    public List<Attraction> getAttractions() {
        return gpsProxy.getAttractions();
    }

    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    @RequestMapping("/getLocation")
    public String getLocation(@RequestParam String userName) {
        VisitedLocation visitedLocation = userService.getUserLocation(userService.getUser(userName));
        return JsonStream.serialize(visitedLocation.getLocation());
    }

    @RequestMapping("/getNearestAttractions")
    public List<NearAttractionDTO> getNearestAttractions(@RequestParam String userName) {
        User user = userService.getUser(userName);
        return userService.getNearestAttractions(user.getUserId(), 5);
    }

/*    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) {
        VisitedLocation visitedLocation = userService.getLocation(userService.getUser(userName));
        return JsonStream.serialize(userService.getNearByAttractions(visitedLocation));
    }*/

    @RequestMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {
        return JsonStream.serialize(userService.getUserRewards(userService.getUser(userName)));
    }

    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
        return JsonStream.serialize(userService.getAllCurrentLocations());
    }

    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
        List<Provider> providers = userService.getTripDeals(userService.getUser(userName));
        return JsonStream.serialize(providers);
    }

}
