package tourGuide;

import com.jsoniter.output.JsonStream;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.dto.CurrentLocationDTO;
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

    public UserController(UserService userService) {
        this.userService = userService;
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

    @RequestMapping("/getNearByAttractions")
    public List<NearAttractionDTO> getNearbyAttractions(@RequestParam String userName) {
        User user = userService.getUser(userName);
        return userService.getNearByAttractions(user.getUserId(), 5);
    }

    @RequestMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {
        User user = userService.getUser(userName);
        return JsonStream.serialize(userService.getUserRewards(user));
    }

    @RequestMapping("/getAllCurrentLocations")
    public List<CurrentLocationDTO> getAllCurrentLocations() {
        return userService.getAllCurrentLocations();
    }

    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
        List<Provider> providers = userService.getTripDeals(userService.getUser(userName));
        return JsonStream.serialize(providers);
    }

}
