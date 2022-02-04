package tourGuide.dto;

import gpsUtil.location.Location;
import lombok.Data;

import java.util.UUID;

@Data
public class CurrentLocationDTO {
    private UUID userID;
    private Location location;

    public CurrentLocationDTO() {
    }

    public CurrentLocationDTO(UUID userID, Location location) {
        this.userID = userID;
        this.location = location;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
