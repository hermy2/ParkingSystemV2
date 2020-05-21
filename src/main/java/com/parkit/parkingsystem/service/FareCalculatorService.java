package com.parkit.parkingsystem.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;


public class FareCalculatorService {
	
	private ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
	private TicketDAO ticketDAO = new TicketDAO(); 
	
	

	public void calculateFare(Ticket ticket) {
		System.out.println(ticket.getInTime());
		System.out.println(ticket.getOutTime());
		System.out.println(ticket.getOutTime().before(ticket.getInTime()));
		
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}
		
		long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();
		

		// TODO: Some tests are failing here. Need to check if this logic is correct
		double duration = (outHour - inHour) / 1000.0 / 60.0 / 60.0;
		
		BigDecimal bd = new BigDecimal(duration).setScale(2, RoundingMode.HALF_UP);
		 duration = bd.doubleValue();
		if (bd.doubleValue() <= 0.5) {
			ticket.setPrice(0.0);
		} else {
			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
				break;
			}
			case BIKE: {
				ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
			

		}
		

	}
	
	public boolean recurringClient(Ticket ticket) {
		Boolean existingClient = parkingSpotDAO.checkClientExist(ticket);
		return existingClient;
	}
	
    public double userDiscount(Ticket ticket) {
		
		if(!recurringClient(ticket)) {
			parkingSpotDAO.checkClientExist(ticket); 
			return ticket.getPrice();
		} else
				
		return (ticket.getPrice() - 0.05 * ticket.getPrice()); 
		
	}

	
}

