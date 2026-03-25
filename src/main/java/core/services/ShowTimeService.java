package core.services;


import core.dto.request.showtime.ShowTimeCreateRequest;
import core.dto.request.showtime.ShowTimeUpdateRequest;
import core.dto.response.ShowTimeResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ShowTimeService {
    ShowTimeResponse create(ShowTimeCreateRequest request);

    List<ShowTimeResponse> findShowTimeByDate(LocalDate date);

    ShowTimeResponse update(UUID id, ShowTimeUpdateRequest request);

    void delete(UUID id);
}
