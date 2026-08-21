package com.lectures.eventticketing.repository;

import com.lectures.eventticketing.model.Booking;

public interface BookingRepository {

    Booking save(Booking booking);
}
