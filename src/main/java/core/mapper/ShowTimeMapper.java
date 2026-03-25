package core.mapper;


import core.dto.request.showtime.ShowTimeCreateRequest;
import core.dto.request.showtime.ShowTimeUpdateRequest;
import core.dto.response.ShowTimeResponse;
import core.entities.ShowTime;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ShowTimeMapper {
    ShowTime toShowTime(ShowTimeCreateRequest request);

    ShowTimeResponse toShowTimeResponse(ShowTime showTime);

    List<ShowTimeResponse> toShowTimeResponse(List<ShowTime> showTimeList);

    void update(@MappingTarget ShowTime showTime, ShowTimeUpdateRequest request);
}
