import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.junit.Test;

import dataAccess.DataAccess;
import domain.Driver;
import domain.Ride;
import testOperations.TestDataAccess;

public class GetRidesByDriverDBWhiteTest {
	 //sut:system under test
	 static DataAccess sut=new DataAccess();
	 
	 //additional operations needed to execute the test 
	 static TestDataAccess testDA=new TestDataAccess();

	@SuppressWarnings("unused")
	private Driver driver;
	

	
	@Test
	public void test1() {
	    String driverUsername = "NonExistentDriver";

	    sut.open();
	    List<Ride> rides = sut.getRidesByDriver(driverUsername);
	    sut.close();

	    // Emaitza null dela ziurtatu, driver ez bai da existitzen
	    assertNull(rides);
	}
	
	@Test
	public void test2() {
	    String driverUsername = "DriverNoRides";
	    
	    // Rides gabeko driver-a sortu
	    testDA.open();
	    testDA.createDriver(driverUsername, "password");
	    testDA.close();

	    sut.open();
	    List<Ride> rides = sut.getRidesByDriver(driverUsername);
	    sut.close();

	    // Rides lista hutsik
	    assertNotNull(rides);
	    assertTrue(rides.isEmpty());

	    // garbiketa
	    testDA.open();
	    testDA.removeDriver(driverUsername);
	    testDA.close();
	}
	
	@Test
	public void test3() {
        String driverUsername = "Driver No Active Rides";
        testDA.open();
        testDA.createDriver(driverUsername, "password");
        testDA.close();

        sut.open();
        List<Ride> rides = sut.getRidesByDriver(driverUsername);
        sut.close();

        assertNotNull(rides);
        assertEquals(0, rides.size()); // No active rides should be returned
	}
	
	@Test
	public void test4() {
        String driverUsername = "ValidDriver";
        Driver driver = new Driver(driverUsername, "password");
        ArrayList<Ride> rides = new ArrayList<>();
        // Add active rides
        rides.add(new Ride("Origin1", "Destination1", new Date(), 2, 10, driver)); // Active ride
        driver.setCreatedRides(rides); // Assuming you have a setter in Driver

        // Persist the driver
        testDA.open();
        testDA.createDriver(driverUsername, "password"); // Create the driver in the DB
        testDA.close();

        // Invoke SUT
        sut.open();
        List<Ride> result = sut.getRidesByDriver(driverUsername);
        sut.close();

        // Verify the results
        assertNotNull(result);
        assertEquals(1, result.size()); // Ensure that we get the active ride back
        assertTrue(result.get(0).isActive()); // Verify that the ride is active
	}

}
