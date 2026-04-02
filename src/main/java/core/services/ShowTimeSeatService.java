package core.services;

import core.dto.request.showtimeseat.ShowTimeSeatCreateRequest;

import core.dto.request.showtimeseat.ShowTimeSeatHoldRequest;
import core.dto.response.PageResponse;
import core.dto.response.ShowTimeSeatResponse;

import java.util.List;
import java.util.UUID;

public interface ShowTimeSeatService {
    List<ShowTimeSeatResponse> create(ShowTimeSeatCreateRequest request);

    PageResponse<ShowTimeSeatResponse> findAllByShowTime(UUID id, int page, int size);

    List<ShowTimeSeatResponse> holdingSeat(ShowTimeSeatHoldRequest request);

    List<ShowTimeSeatResponse> holdingSeatV2(ShowTimeSeatHoldRequest request);

    void delete(UUID id);

    void handleSeatCleanup();
}
