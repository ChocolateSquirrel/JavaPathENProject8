package tourGuide.dto;

import gpsUtil.location.Location;
import lombok.Data;

@Data
public class NearAttractionDTO {
    private String attractionName;
    private Location attractionLocation;
    private Location userLocation;
    private double distanceMiles;
    private int rewardPts;

    public NearAttractionDTO() {}

    public NearAttractionDTO(String attractionName, Location attractionLocation, Location userLocation, double distanceMiles, int rewardPts) {
        this.attractionName = attractionName;
        this.attractionLocation = attractionLocation;
        this.userLocation = userLocation;
        this.distanceMiles = distanceMiles;
        this.rewardPts = rewardPts;
    }

    public String getAttractionName() {
        return attractionName;
    }

    public Location getAttractionLocation() {
        return attractionLocation;
    }

    public Location getUserLocation() {
        return userLocation;
    }

    public double getDistanceMiles() {
        return distanceMiles;
    }

    public int getRewardPts() {
        return rewardPts;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public void setAttractionLocation(Location attractionLocation) {
        this.attractionLocation = attractionLocation;
    }

    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
    }

    public void setDistanceMiles(double distanceMiles) {
        this.distanceMiles = distanceMiles;
    }

    public void setRewardPts(int rewardPts) {
        this.rewardPts = rewardPts;
    }
}
