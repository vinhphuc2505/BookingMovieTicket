package core.services;


import core.dto.request.showtime.ShowTimeCreateRequest;
import core.dto.request.showtime.ShowTimeUpdateRequest;
import core.dto.response.PageResponse;
import core.dto.response.ShowTimeResponse;
import core.entities.Movie;
import core.entities.Room;
import core.entities.ShowTime;
import core.exceptions.AppException;
import core.exceptions.ErrorCode;
import core.mapper.ShowTimeMapper;
import core.repositories.MovieRepository;
import core.repositories.RoomRepository;
import core.repositories.ShowTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShowTimeServiceImpl implements ShowTimeService{

    private final ShowTimeRepository showTimeRepository;

    private final ShowTimeMapper showTimeMapper;

    private final MovieRepository movieRepository;

    private final RoomRepository roomRepository;

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ShowTimeResponse create(ShowTimeCreateRequest request) {

        ShowTime showTime = showTimeMapper.toShowTime(request);

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED));

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_EXISTED));

        ZonedDateTime endTime = request.getStartTime().plusMinutes(movie.getDuration());

        boolean overlap = showTimeRepository.existsOverlap(request.getRoomId(), request.getStartTime(), endTime);

        if(overlap){
            throw new AppException(ErrorCode.ROOM_NOT_AVAILABLE);
        }

        showTime.setRoom(room);
        showTime.setMovie(movie);
        showTime.setEndTime(endTime);

        return showTimeMapper.toShowTimeResponse(showTimeRepository.save(showTime));
    }

    @Override
    public PageResponse<ShowTimeResponse> findShowTimeByDate(LocalDate date, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("startTime").descending());

        ZonedDateTime start = date.atStartOfDay(ZoneId.systemDefault());

        ZonedDateTime end = start.plusDays(1).minusNanos(1);

        Page<ShowTime> showTimePage = showTimeRepository.findByStartTimeBetween(start, end, pageable);

        List<ShowTimeResponse> showTimeResponses = showTimePage.getContent().stream()
                .map(showTimeMapper::toShowTimeResponse)
                .toList();

        return PageResponse.<ShowTimeResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(showTimePage.getTotalPages())
                .totalElements(showTimePage.getTotalElements())
                .data(showTimeResponses)
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ShowTimeResponse update(UUID id, ShowTimeUpdateRequest request) {
        ShowTime showTime = showTimeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHOW_TIME_NOT_EXISTED));

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_EXISTED));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED));

        ZonedDateTime endTime = request.getStartTime().plusMinutes(movie.getDuration());

        boolean overlap = showTimeRepository.existsOverlap(request.getRoomId(), request.getStartTime(), endTime, id);

        if(overlap){
            throw new AppException(ErrorCode.ROOM_NOT_AVAILABLE);
        }

        showTimeMapper.update(showTime, request);
        showTime.setMovie(movie);
        showTime.setRoom(room);
        showTime.setEndTime(endTime);

        return showTimeMapper.toShowTimeResponse(showTimeRepository.save(showTime));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(UUID id) {
        showTimeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHOW_TIME_NOT_EXISTED));
        showTimeRepository.deleteById(id);
    }

}






