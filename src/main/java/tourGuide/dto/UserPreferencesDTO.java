package tourGuide.dto;

import lombok.Data;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

@Data
public class UserPreferencesDTO {

    private int attractionProximity;
    private String currency;
    private int lowerPrice;
    private int highPrice;
    private int tripDuration;
    private int ticketQuantity;
    private int numberOfAdults;
    private int numberOfChildren;

    public UserPreferencesDTO() { }
}
