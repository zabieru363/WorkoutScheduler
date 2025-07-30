package com.workout.scheduler.app.workout_scheduler_app.utils;

import com.workout.scheduler.app.workout_scheduler_app.models.dtos.PageRequestDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtils {

    private PaginationUtils() {}

    public static Pageable buildPageable(PageRequestDTO request) {
        if(request == null)
            return PageRequest.of(0, 10);

        Sort sort = request.direction().equalsIgnoreCase("desc") ?
                Sort.by(request.orderField()).descending() :
                Sort.by(request.orderField()).ascending();

        return PageRequest.of(request.page(), request.size(), sort);
    }
}