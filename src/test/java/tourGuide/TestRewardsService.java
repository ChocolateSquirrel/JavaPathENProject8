package tourGuide;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.Attraction;
import tourGuide.model.VisitedLocation;
import tourGuide.proxy.GpsProxy;
import tourGuide.proxy.RewardProxy;
import tourGuide.service.UserService;
import tourGuide.user.User;
import tourGuide.user.UserReward;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRewardsService {

	@Autowired
	RewardProxy rewardProxy;

	@Autowired
	GpsProxy gpsProxy;

	@BeforeClass
	public static void setup(){
		Locale.setDefault(Locale.US);
	}

	@Test
	public void contextLoads(){}

	@Test
	public void userGetRewards() {
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(gpsProxy, rewardProxy);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsProxy.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		userService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();
		userService.tracker.stopTracking();
		assertTrue(userRewards.size() == 1);
	}

	@Test
	public void nearAllAttractions() {
		UserService userService = new UserService(gpsProxy, rewardProxy);
		rewardProxy.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(1);

		userService.calculateRewards(userService.getAllUsers().get(0));
		List<UserReward> userRewards = userService.getUserRewards(userService.getAllUsers().get(0));
		userService.tracker.stopTracking();

		assertEquals(gpsProxy.getAttractions().size(), userRewards.size());
	}
	
}
