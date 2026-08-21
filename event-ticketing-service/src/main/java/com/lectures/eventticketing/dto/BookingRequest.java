package com.lectures.eventticketing.dto;

/*
    Bu servis tarafına gelen isteği tarifleyen bir nesne olduğundan Java
Persistence Api (JPA) tarafından yönetilmiyor. Dolayısıyla sabit ve değişmez
(immutable) olması tam olarak istediğimiz şey. Bu nedenle record olarak
tanımladık.
 */
public record BookingRequest(Long eventId, Long customerId, int seatCount) {

}
