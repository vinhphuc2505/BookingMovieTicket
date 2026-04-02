package core.strategy;


import core.enums.TicketType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AdultPriceStrategy implements TicketPriceStrategy{
    @Override
    public TicketType getType() {
        return TicketType.ADULT;
    }

    @Override
    public BigDecimal calculate(BigDecimal basePrice) {
        return basePrice;
    }
}
