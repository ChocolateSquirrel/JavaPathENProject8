package tourGuide;

import com.jsoniter.output.JsonStream;
import gpsUtil.location.VisitedLocation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.model.VisitedLocation;
import tripPricer.Provider;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(TourGuideUserService
                                  userService) {
        this.userService = userService;
    }

    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    @RequestMapping("/getLocation")
    public String getLocation(@RequestParam String userName) {
        VisitedLocation visitedLocation = userService.getLocation(userService.getUser(userName));
        return JsonStream.serialize(visitedLocation.location);
    }

    @RequestMapping("/getNearestbyAttractions")
    public String getNearestbyAttractions(@RequestParam String userName) {
        VisitedLocation visitedLocation = userService.getLocation(userService.getUser(userName));
        return JsonStream.serialize(userService.getNearestAttractions(visitedLocation, 5, userService.getUser(userName)));
    }

    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) {
        VisitedLocation visitedLocation = userService.getLocation(userService.getUser(userName));
        return JsonStream.serialize(userService.getNearByAttractions(visitedLocation));
    }

    @RequestMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {
        return JsonStream.serialize(userService.getUserRewards(userName));
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
