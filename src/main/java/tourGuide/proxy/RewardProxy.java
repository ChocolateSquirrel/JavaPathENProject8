package tourGuide.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import tourGuide.dto.AskNearAttractionsDTO;
import tourGuide.dto.DistanceDTO;
import tourGuide.dto.UserRewardDTO;
import tourGuide.model.Attraction;
import tourGuide.user.UserReward;

import java.util.List;

@FeignClient(name = "ms-rewards", url = "ms-rewards:7070")
public interface RewardProxy {

    @GetMapping("getRewardPoints/{attractionId}/{userId}")
    public int getRewardPoint(@PathVariable("attractionId") String attractionId, @PathVariable("userId") String userId);

    @PostMapping("calculateRewards")
    public List<UserReward> calculateRewards(@RequestBody UserRewardDTO userRewardDTO);

    @PostMapping("getDistance")
    public double getDistance(@RequestBody DistanceDTO distanceDTO);

    @PostMapping("getNearByAttractions")
    public List<Attraction> getNearByAttractions(@RequestBody AskNearAttractionsDTO askNearAttractionsDTO);

    @PostMapping("getNearestAttractions")
    public List<Attraction> getNearestAttractions(@RequestBody AskNearAttractionsDTO askNearAttractionsDTO);

    @PostMapping("setProximityBuffer")
    public void setProximityBuffer(@RequestBody int proximityBuffer);

    @PostMapping("setDefaultProximityBuffer")
    public void setDefaultProximityBuffer();
}