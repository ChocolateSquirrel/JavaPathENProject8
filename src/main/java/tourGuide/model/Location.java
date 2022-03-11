package tourGuide.model;

import lombok.Data;

@Data
public class Location {
    private  double longitude;
    private double latitude;

    public Location() {}

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
