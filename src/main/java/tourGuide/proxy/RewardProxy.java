package tourGuide.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import tourGuide.dto.DistanceDTO;
import tourGuide.dto.UserRewardDTO;
import tourGuide.model.Attraction;
import tourGuide.user.UserReward;

import java.util.List;

@FeignClient(name = "ms-rewards", url = "localhost:7070")
public interface RewardProxy {

    @GetMapping("getRewardPoints/{attractionId}/{userId}")
    public int getRewardPoint(@PathVariable("attractionId") String attractionId, @PathVariable("userId") String userId);

    @PostMapping("calculateRewards")
    public List<UserReward> calculateRewards(@RequestBody UserRewardDTO userRewardDTO);

    @PostMapping("getDistance")
    public double getDistance(@RequestBody DistanceDTO distanceDTO);

    @GetMapping("getNearAttractions/{userId}")
    public List<Attraction> getNearAttractions(@PathVariable("userId") String userId);

}