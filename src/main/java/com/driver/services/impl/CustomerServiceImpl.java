package com.driver.services.impl;

import com.driver.model.TripBooking;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Customer;
import com.driver.model.Driver;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;
import com.driver.model.TripStatus;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		Customer customer = customerRepository2.findById(customerId).get();

		customerRepository2.delete(customer);
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
			List<Driver> driverList = driverRepository2.findAll();

			Driver freeDriver = null;
			for(Driver driver : driverList){
				if(driver.getCab().getAvailable()){
					freeDriver = driver;
					break;
				}
			}

			//not available
			if(freeDriver == null)
				throw new Exception("No cab available!");

			//cab things
			freeDriver.getCab().setAvailable(false);
			int perKmRate = freeDriver.getCab().getPerKmRate();
			int bill = distanceInKm * perKmRate;

			Customer customer = customerRepository2.findById(customerId).get();

			//tripbooking things
			TripBooking newTrip = new TripBooking();

			newTrip.setDriver(freeDriver);
			newTrip.setFromLocation(fromLocation);
			newTrip.setToLocation(toLocation);
			newTrip.setDistanceInKm(distanceInKm);
			newTrip.setStatus(TripStatus.CONFIRMED);
			newTrip.setBill(bill);
			newTrip.setCustomer(customer);
			newTrip.setDriver(freeDriver);

			//driver things
			freeDriver.getTripBookingList().add(newTrip);

			//customer things
			customer.getTripBookingList().add(newTrip);

			customerRepository2.save(customer);
			driverRepository2.save(freeDriver);

			return newTrip;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking bookedTrip = tripBookingRepository2.findById(tripId).get();

		//tripbooking things
		bookedTrip.setStatus(TripStatus.CANCELED);
		bookedTrip.setBill(0);

		//cab things
		Driver driver = bookedTrip.getDriver();
		driver.getCab().setAvailable(true);

		driverRepository2.save(driver);
		tripBookingRepository2.save(bookedTrip);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking bookedTrip = tripBookingRepository2.findById(tripId).get();

		//tripbooking things
		bookedTrip.setStatus(TripStatus.COMPLETED);

		//cab things
		Driver driver = bookedTrip.getDriver();
		driver.getCab().setAvailable(true);

		driverRepository2.save(driver);
		tripBookingRepository2.save(bookedTrip);
	}
}
