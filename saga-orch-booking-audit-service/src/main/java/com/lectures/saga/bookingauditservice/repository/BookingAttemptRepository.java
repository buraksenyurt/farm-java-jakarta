package com.lectures.saga.bookingauditservice.repository;

import com.lectures.saga.bookingauditservice.model.BookingAttempt;
import java.util.List;

public interface BookingAttemptRepository {

    List<BookingAttempt> findAll();

    void save(BookingAttempt attempt);
}
