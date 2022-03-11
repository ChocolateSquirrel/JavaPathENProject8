package tourGuide.dto;

import lombok.Data;
import tourGuide.model.Location;

@Data
public class AskNearAttractionsDTO {

    private Location location;
    private int nbAttractions;

    public AskNearAttractionsDTO() { }

    public AskNearAttractionsDTO(Location location, int nbAttractions) {
        this.location = location;
        this.nbAttractions = nbAttractions;
    }
}
