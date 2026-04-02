package core.services;


import core.dto.request.showtime.ShowTimeCreateRequest;
import core.dto.request.showtime.ShowTimeUpdateRequest;
import core.dto.response.PageResponse;
import core.dto.response.ShowTimeResponse;

import java.time.LocalDate;
import java.util.UUID;

public interface ShowTimeService {
    ShowTimeResponse create(ShowTimeCreateRequest request);

    PageResponse<ShowTimeResponse> findShowTimeByDate(LocalDate date, int page, int size);

    ShowTimeResponse update(UUID id, ShowTimeUpdateRequest request);

    void delete(UUID id);
}
