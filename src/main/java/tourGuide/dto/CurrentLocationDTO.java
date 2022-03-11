package tourGuide.dto;

import lombok.Data;
import tourGuide.model.Location;
import tourGuide.model.VisitedLocation;

import java.util.UUID;

@Data
public class CurrentLocationDTO {
    private String userName;
    private VisitedLocation visitedLocation;

    public CurrentLocationDTO() {
    }

    public CurrentLocationDTO(String userName, VisitedLocation visitedLocation) {
        this.userName = userName;
        this.visitedLocation = visitedLocation;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public VisitedLocation getVisitedLocation() {
        return visitedLocation;
    }

    public void setVisitedLocation(VisitedLocation visitedLocation) {
        this.visitedLocation = visitedLocation;
    }
}
