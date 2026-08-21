package com.lectures.eventticketing.repository;

import com.lectures.eventticketing.model.BookingAttempt;
import java.util.List;

public interface BookingAttemptRepository {

    List<BookingAttempt> findAll();
}
