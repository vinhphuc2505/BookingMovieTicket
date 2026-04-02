package core.strategy;


import core.enums.TicketType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ChildPriceStrategy implements TicketPriceStrategy{
    @Override
    public TicketType getType() {
        return TicketType.CHILD;
    }

    @Override
    public BigDecimal calculate(BigDecimal basePrice) {
        return basePrice.multiply(new BigDecimal("0.7"));
    }
}
