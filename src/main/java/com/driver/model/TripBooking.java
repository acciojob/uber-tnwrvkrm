package com.driver.model;

import javax.persistence.*;

@Entity
public class TripBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tripBookingId;
    private String fromLocation;
    private String toLocation;
    private int distanceInKm;
    @Enumerated(EnumType.STRING)
    TripStatus status;
    private int bill;
}