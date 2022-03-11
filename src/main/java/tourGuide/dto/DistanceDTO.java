package tourGuide.dto;

import lombok.Data;
import tourGuide.model.Location;

@Data
public class DistanceDTO {
    private Location loc1;
    private Location loc2;

    public DistanceDTO() {}

    public DistanceDTO(Location loc1, Location loc2) {
        this.loc1 = loc1;
        this.loc2 = loc2;
    }
}