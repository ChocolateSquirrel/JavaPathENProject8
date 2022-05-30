package tourGuide;

import java.util.*;
import java.util.concurrent.TimeUnit;


import org.apache.commons.lang3.builder.ToStringExclude;
import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tourGuide.dto.NearAttractionDTO;
import tourGuide.dto.UserPreferencesDTO;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.Attraction;
import tourGuide.model.VisitedLocation;
import tourGuide.proxy.GpsProxy;
import tourGuide.proxy.RewardProxy;
import tourGuide.service.UserService;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tourGuide.user.UserReward;
import tripPricer.Provider;

import javax.money.Monetary;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestUserService {

    @Autowired
    RewardProxy rewardProxy;

    @Autowired
    GpsProxy gpsProxy;

	@BeforeClass
	public static void setup(){
		Locale.setDefault(Locale.US);
	}

	private void waitVisitedLocation(User user, int size) {
		while(user.getVisitedLocations().size() <size) {
			try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
			}
		}
	}

	private void waitReward(User user, int size) {
		while(user.getUserRewards().size() <size) {
			try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
			}
		}
	}

	@Before
	public void setUp(){
		rewardProxy.setDefaultProximityBuffer();
	}

	@Test
	public void getUserLocation() {
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(gpsProxy, rewardProxy);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		userService.trackUserLocation(user);
		userService.tracker.stopTracking();
		waitVisitedLocation(user, 1);
		assertEquals(user.getVisitedLocations().size(), 1);
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
	public void getAllCurrentLocations(){
		InternalTestHelper.setInternalUserNumber(10);
		UserService userService = new UserService(gpsProxy, rewardProxy);
		assertEquals(10, userService.getAllCurrentLocations().size());
	}
	
	@Test
	public void trackUser() {
        InternalTestHelper.setInternalUserNumber(0);
        UserService userService = new UserService(gpsProxy, rewardProxy);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		userService.trackUserLocation(user);

        userService.tracker.stopTracking();

		waitVisitedLocation(user, 1);
		assertEquals(user.getVisitedLocations().size(), 1);
	}

	@Test
	public void getNearbyAttractions() {
        InternalTestHelper.setInternalUserNumber(0);
        UserService userService = new UserService(gpsProxy, rewardProxy);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		
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
		waitReward(user, 1);
		assertTrue(userRewards.size() == 1);
	}

	@Test
	public void updateUserPreferences(){
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(gpsProxy, rewardProxy);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO();
		userPreferencesDTO.setCurrency("EUR");
		userPreferencesDTO.setAttractionProximity(100);
		userPreferencesDTO.setHighPrice(1000);
		userPreferencesDTO.setLowerPrice(5);
		userPreferencesDTO.setTripDuration(4);
		userPreferencesDTO.setTicketQuantity(6);
		userPreferencesDTO.setNumberOfAdults(2);
		userPreferencesDTO.setNumberOfChildren(4);
		userService.addUser(user);

		userService.updateUserPreferences(user.getUserName(), userPreferencesDTO);
		UserPreferences userPreferences = user.getUserPreferences();

		userService.tracker.stopTracking();
		assertEquals(userPreferences.getAttractionProximity(), 100);
		assertEquals(userPreferences.getCurrency(), Monetary.getCurrency("EUR"));
		assertEquals(userPreferences.getHighPricePoint(), Money.of(userPreferencesDTO.getHighPrice(), Monetary.getCurrency("EUR")));
		assertEquals(userPreferences.getLowerPricePoint(), Money.of(userPreferencesDTO.getLowerPrice(), Monetary.getCurrency("EUR")));
		assertEquals(userPreferences.getTripDuration(), 4);
		assertEquals(userPreferences.getTicketQuantity(), 6);
		assertEquals(userPreferences.getNumberOfAdults(), 2);
		assertEquals(userPreferences.getNumberOfChildren(), 4);
	}

	@Test
	public void nearAllAttractions() {
		UserService userService = new UserService(gpsProxy, rewardProxy);
		rewardProxy.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(1);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		userService.addUser(user);

		userService.trackUserLocation(userService.getAllUsers().get(0));
		List<UserReward> userRewards = userService.getUserRewards(userService.getAllUsers().get(0));
		userService.tracker.stopTracking();

		waitReward(user, 26);
		assertEquals(gpsProxy.getAttractions().size(), userRewards.size());
	}
	
	
}
