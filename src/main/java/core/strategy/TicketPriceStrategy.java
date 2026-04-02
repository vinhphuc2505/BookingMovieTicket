package core.strategy;


import core.enums.TicketType;

import java.math.BigDecimal;

public interface TicketPriceStrategy {
    TicketType getType();

    BigDecimal calculate(BigDecimal basePrice);
}
