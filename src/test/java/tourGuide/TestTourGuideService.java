package tourGuide;

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
import tourGuide.dto.NearAttractionDTO;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.Attraction;
import tourGuide.model.VisitedLocation;
import tourGuide.proxy.GpsProxy;
import tourGuide.proxy.RewardProxy;
import tourGuide.service.UserService;
import tourGuide.user.User;
import tripPricer.Provider;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestTourGuideService {

    @Autowired
    RewardProxy rewardProxy;

    @Autowired
    GpsProxy gpsProxy;

	@BeforeClass
	public static void setup(){
		Locale.setDefault(Locale.US);
	}

	@Test
	public void getUserLocation() {
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(gpsProxy, rewardProxy);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = userService.trackUserLocation(user);
		userService.tracker.stopTracking();
		assertTrue(visitedLocation.getUserId().equals(user.getUserId()));
	}
	
	@Test
	public void addUser() {
        InternalTestHelper.setInternalUserNumber(0);
        UserService userService = new UserService(gpsProxy, rewardProxy);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		userService.addUser(user);
		userService.addUser(user2);
		
		User retrievedUser = userService.getUser(user.getUserName());
		User retrievedUser2 = userService.getUser(user2.getUserName());

		userService.tracker.stopTracking();
		
		assertEquals(user, retrievedUser);
		assertEquals(user2, retrievedUser2);
	}
	
	@Test
	public void getAllUsers() {
        InternalTestHelper.setInternalUserNumber(0);
        UserService userService = new UserService(gpsProxy, rewardProxy);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        userService.addUser(user);
        userService.addUser(user2);
		
		List<User> allUsers = userService.getAllUsers();

        userService.tracker.stopTracking();
		
		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}
	
	@Test
	public void trackUser() {
        InternalTestHelper.setInternalUserNumber(0);
        UserService userService = new UserService(gpsProxy, rewardProxy);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = userService.trackUserLocation(user);

        userService.tracker.stopTracking();
		
		assertEquals(user.getUserId(), visitedLocation.getUserId());
	}

	@Test
	public void getNearbyAttractions() {
        InternalTestHelper.setInternalUserNumber(0);
        UserService userService = new UserService(gpsProxy, rewardProxy);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = userService.trackUserLocation(user);
		
		List<NearAttractionDTO> attractions = userService.getNearByAttractions(user.getUserId(), 5);

        userService.tracker.stopTracking();
		
		assertEquals(5, attractions.size());
	}
	
	public void getTripDeals() {
        InternalTestHelper.setInternalUserNumber(0);
        UserService userService = new UserService(gpsProxy, rewardProxy);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = userService.getTripDeals(user);

        userService.tracker.stopTracking();
		
		assertEquals(10, providers.size());
	}
	
	
}
