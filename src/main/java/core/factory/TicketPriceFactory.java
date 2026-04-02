package core.factory;


import core.enums.TicketType;
import core.strategy.TicketPriceStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TicketPriceFactory {
    private final Map<TicketType, TicketPriceStrategy> strategyMap;

//    public TicketPriceFactory(Map<TicketType, TicketPriceStrategy> strategyMap) {
//        this.strategyMap = strategyMap;
//    }

    public TicketPriceFactory(List<TicketPriceStrategy> strategies) {
        strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        TicketPriceStrategy::getType,
                        Function.identity()
                ));
    }

    public TicketPriceStrategy getStrategy(TicketType type) {
        return Optional.ofNullable(strategyMap.get(type))
                .orElseThrow(() -> new IllegalArgumentException("Chưa cấu hình tính giá cho loại vé: " + type));
    }
}
