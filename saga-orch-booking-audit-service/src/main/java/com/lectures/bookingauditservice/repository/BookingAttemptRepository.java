package com.lectures.bookingauditservice.repository;

import com.lectures.bookingauditservice.model.BookingAttempt;
import java.util.List;

public interface BookingAttemptRepository {

    List<BookingAttempt> findAll();

    void save(BookingAttempt attempt);
}
