package businessLogic;

import java.util.Date;
import java.util.List;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import domain.Alert;
import domain.Booking;
import domain.Car;
import domain.Complaint;
import domain.Discount;
import domain.Driver;
import domain.Movement;
import domain.Ride;
import domain.RideDetails;
import domain.Traveler;
import domain.User;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;

public class BLFacadeLocal extends BLFacadeImplementation {
	
	 public static BLFacade createBLFacade() throws Exception {
	            DataAccess da = new DataAccess();
	            return new BLFacadeImplementation(da);
	 }

}
