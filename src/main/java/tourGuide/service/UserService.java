package tourGuide.service;

import ch.qos.logback.core.CoreConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.dto.*;
import tourGuide.exception.UserNameNotFoundException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.Attraction;
import tourGuide.model.Location;
import tourGuide.model.VisitedLocation;
import tourGuide.proxy.GpsProxy;
import tourGuide.proxy.RewardProxy;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class UserService {

    private ExecutorService executor = Executors.newFixedThreadPool(10000);
    private Logger logger = LoggerFactory.getLogger(UserService.class);

    private final GpsProxy gpsProxy;
    private final RewardProxy rewardProxy;

    private final TripPricer tripPricer = new TripPricer();
    public final Tracker tracker;
    boolean testMode = true;

    public UserService(GpsProxy gpsProxy, RewardProxy rewardProxy) {
        this.gpsProxy = gpsProxy;
        this.rewardProxy = rewardProxy;

        if(testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            initializeInternalUsers();
            logger.debug("Finished initializing users");
        }
        tracker = new Tracker(this);
        addShutDownHook();
    }

    public List<UserReward> getUserRewards(User user) {
        return user.getUserRewards();
    }

    public List<Attraction> getAttractions() {
        return gpsProxy.getAttractions();
    }

    public VisitedLocation getUserLocation(User user) {
 /*       VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
                user.getLastVisitedLocation() :
                trackUserLocation(user);
        return visitedLocation;*/
        return user.getVisitedLocations().stream().findFirst().orElse(null);
    }

    public List<CurrentLocationDTO> getAllCurrentLocations(){
        List<CurrentLocationDTO> currentLocationDTOList = new ArrayList<>();
        for (User user : getAllUsers()) {
            CurrentLocationDTO currentLocationDTO = new CurrentLocationDTO();
            currentLocationDTO.setVisitedLocation(getUserLocation(user));
            currentLocationDTO.setUserName(user.getUserName());
            currentLocationDTOList.add(currentLocationDTO);
        }
        return currentLocationDTOList;
    }

    public User getUser(String userName) {
        if (!internalUserMap.containsKey(userName))
            throw new UserNameNotFoundException(userName);
        return internalUserMap.get(userName);
    }

    public List<User> getAllUsers() {
        return internalUserMap.values().stream().collect(Collectors.toList());
    }

    public void addUser(User user) {
        if(!internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.put(user.getUserName(), user);
        }
    }

    public UserPreferences updateUserPreferences(String userName, UserPreferencesDTO userPreferencesDTO){
        User user = getUser(userName);
        UserPreferences userPreferences = new UserPreferences(userPreferencesDTO);
        user.setUserPreferences(userPreferences);
        return userPreferences;
    }

    public List<Provider> getTripDeals(User user) {
        int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
        List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
        user.setTripDeals(providers);
        return providers;
    }

    public void trackUserLocation(User user) {
        CompletableFuture.supplyAsync(() -> {
            return gpsProxy.getUserLocation(user.getUserId().toString());
                }, executor)
                .thenAccept(visitedLocation -> {
            user.addToVisitedLocations(visitedLocation);
            calculateRewards(user);
        });

/*        Callable<VisitedLocation> visitedLocCallable = () -> gpsProxy.getUserLocation(user.getUserId().toString());
        FutureTask<VisitedLocation> futureTask = new FutureTask<>(visitedLocCallable);
        Thread t = new Thread(futureTask);
        t.start();
        try {
            VisitedLocation visitedLoc = futureTask.get();
            user.addToVisitedLocations(visitedLoc);
            calculateRewards(user);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/

        }

/*
    public VisitedLocation trackUserLocation(User user) {
        VisitedLocation visitedLocation = gpsProxy.getUserLocation(user.getUserId().toString());
        user.addToVisitedLocations(visitedLocation);
        calculateRewards(user);
        return visitedLocation;
    }
*/

    public void calculateRewards(User user){
        UserRewardDTO userRewardDTO = new UserRewardDTO(user.getVisitedLocations(), user.getUserId());
        rewardProxy.calculateRewards(userRewardDTO).forEach(user::addUserReward);
       /* UserRewardDTO userRewardDTO = new UserRewardDTO(user.getVisitedLocations(), user.getUserId());
        List<UserReward> userRewards = rewardProxy.calculateRewards(userRewardDTO);
        for (UserReward userReward : userRewards){
            user.addUserReward(userReward);
        }*/
    }

    public List<NearAttractionDTO> getNearByAttractions(UUID userId, int number) {
        List<NearAttractionDTO> attractionsDTOList = new ArrayList<>();
        Location userLoc = gpsProxy.getUserLocation(String.valueOf(userId)).getLocation();
        List<Attraction> attractions =  rewardProxy.getNearByAttractions(new AskNearAttractionsDTO(userLoc, number));
        for (Attraction attraction : attractions){
            NearAttractionDTO attractionDTO = new NearAttractionDTO();
            Location attLoc = new Location(attraction.getLatitude(), attraction.getLongitude());
            attractionDTO.setAttractionName(attraction.getAttractionName());
            attractionDTO.setAttractionLocation(attLoc);
            attractionDTO.setUserLocation(userLoc);
            attractionDTO.setDistanceMiles(rewardProxy.getDistance(new DistanceDTO(userLoc, attLoc)));
            attractionDTO.setRewardPts(rewardProxy.getRewardPoint(String.valueOf(attraction.getAttractionId()), String.valueOf(userId)));
            attractionsDTOList.add(attractionDTO);
        }
        return attractionsDTOList;
    }

    public List<NearAttractionDTO> getNearestAttractions(UUID userId, int number){
        List<NearAttractionDTO> attractionsDTOList = new ArrayList<>();
        Location userLoc = gpsProxy.getUserLocation(String.valueOf(userId)).getLocation();
        List<Attraction> attractions = rewardProxy.getNearestAttractions(new AskNearAttractionsDTO(userLoc, number));
        for (Attraction attraction : attractions){
            NearAttractionDTO attractionDTO = new NearAttractionDTO();
            Location attLoc = new Location(attraction.getLatitude(), attraction.getLongitude());
            attractionDTO.setAttractionName(attraction.getAttractionName());
            attractionDTO.setAttractionLocation(attLoc);
            attractionDTO.setUserLocation(userLoc);
            attractionDTO.setDistanceMiles(rewardProxy.getDistance(new DistanceDTO(userLoc, attLoc)));
            attractionDTO.setRewardPts(rewardProxy.getRewardPoint(String.valueOf(attraction.getAttractionId()), String.valueOf(userId)));
            attractionsDTOList.add(attractionDTO);
        }
        return attractionsDTOList;
    }

    public void setProximityBuffer(int proximityBuffer){

    }


    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tracker.stopTracking();
            }
        });
    }

    /**********************************************************************************
     *
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/
    private static final String tripPricerApiKey = "test-server-api-key";
    // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
    private Map<String, User> internalUserMap = new HashMap<>();
    private void initializeInternalUsers() {
        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            User user = new User(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(user);

            internalUserMap.put(userName, user);
        });
        logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
    }

    private void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i-> {
            user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
        });
    }

    private double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }


}

