package core.strategy;


import core.enums.TicketType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StudentPriceStrategy implements TicketPriceStrategy{
    @Override
    public TicketType getType() {
        return TicketType.STUDENT;
    }

    @Override
    public BigDecimal calculate(BigDecimal basePrice) {
        return basePrice.multiply(new BigDecimal("0.9"));
    }
}
