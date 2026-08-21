package com.lectures.eventticketing.service;

import com.lectures.eventticketing.model.Booking;
import com.lectures.eventticketing.model.Customer;
import com.lectures.eventticketing.model.Event;
import com.lectures.eventticketing.repository.BookingAttemptRepository;
import com.lectures.eventticketing.repository.BookingRepository;
import com.lectures.eventticketing.repository.CustomerRepository;
import com.lectures.eventticketing.repository.EventRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
    Projenin en önemli sınıfı diyebiliriz. Orkestrasyon işini üstlenen servis
sınıfımız. Dikkat edileceği üzere Transaction'a dahil olacak tüm bileşenler
doğrudan kullanılmak yerine Container üzerinden buraya enjekte edilirler. Zira
transaction istekleri uygulama servisinden, CDI üzerinden gelir. Hatta,
BookingAttemtLogger repository'sini kendi içerisinde kullanmakta ve REQUIRES_NEW
ile hep yeni ve bağımsız bir transaction istemektedir, hatırlayalım.

Bir diğer dikkat edilecek nokta bu sınıfta bir PersistenceContext 
belirtilmemiş olmasıdır. Nesne tam bir orkestrasyon görevi üstlenir. try bloğu
içerisindeki operasyonlar çağrılan repository nesnelerindeki persistence işlem
lerinin commit edilmesini sağlarken bir exception olup catch bloğuna
düşülmesi halinde o ana kadar ki işlemler rollback edilir. Bunları açıkça
belirtmemize gerek yoktur dikkat edileceği üzere.
 */
@ApplicationScoped
public class TicketBookingService {
    
    @Inject
    private EventRepository eventRepository;
    @Inject
    private CustomerRepository customerRepository;
    @Inject
    private BookingRepository bookingRepository;
    @Inject
    private BookingAttemptLogger bookingAttemptLogger;
    
    private static final Logger logger = LoggerFactory.getLogger(TicketBookingService.class);

    // Propogation varsayılan olarak REQUIRED. Buna göre aktif
    // bir transaction yoksa yenisi başlatılır varsa ona katılınır.
    // Burayı Unit of Work'un kapsamı olarak düşünebiliriz.
    @Transactional
    public Booking bookTickets(Long eventId, Long customerId, int seatCount) {
        try {
            Event event = eventRepository.findById(eventId);
            eventRepository.reserveSeats(event, seatCount);
            
            BigDecimal totalPrice = event.getTicketPrice().multiply(BigDecimal.valueOf(seatCount));
            
            Customer customer = customerRepository.findById(customerId);
            customerRepository.chargeWallet(customer, totalPrice);
            
            Booking booking = new Booking();
            booking.setEventId(eventId);
            booking.setCustomerId(customerId);
            booking.setSeatCount(seatCount);
            booking.setTotalPrice(totalPrice);
            booking.setBookingTime(LocalDateTime.now());
            
            Booking saved = bookingRepository.save(booking);
            bookingAttemptLogger.logAttempt(eventId, customerId, seatCount, "SUCCESS", null);
            
            logger.info("Event Id " + eventId + " Customer Id " + customerId + " Seat Count " + seatCount + " rezervasyon başarılı.");
            return saved;
            
        } catch (BookingException e) {
            bookingAttemptLogger.logAttempt(eventId, customerId, seatCount, "FAILURE", e.getMessage());
            
            logger.error("Beklenmedik bir hata oluştu ve transaction Rollback edildi.", e);
            throw e;
        }
    }
    
}
