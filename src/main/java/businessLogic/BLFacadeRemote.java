package businessLogic;

import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

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

public class BLFacadeRemote extends BLFacadeImplementation {
	
	  public static BLFacade createBLFacade() throws Exception {
	        ConfigXML config = ConfigXML.getInstance();
	            String serviceName = "http://" + config.getBusinessLogicNode() + ":" + config.getBusinessLogicPort() 
	                                + "/ws/" + config.getBusinessLogicName() + "?wsdl";
	            URL url = new URL(serviceName);
	            QName qname = new QName("http://businessLogic/", "BLFacadeImplementationService");
	            Service service = Service.create(url, qname);
	            return service.getPort(BLFacade.class);
	        
	    }
}
