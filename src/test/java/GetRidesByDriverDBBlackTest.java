import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import dataAccess.DataAccess;
import domain.Driver;
import domain.Ride;
import testOperations.TestDataAccess;

public class GetRidesByDriverDBBlackTest {
	 //sut:system under test
	 static DataAccess sut=new DataAccess();
	 
	 //additional operations needed to execute the test 
	 static TestDataAccess testDA=new TestDataAccess();

	@SuppressWarnings("unused")
	private Driver driver; 
	
	//Driver ez da existitzen datubasean
	@Test
	public void test1() {
        sut.open();
        List<Ride> rides = sut.getRidesByDriver("nonExistentDriver");
        sut.close();
        assertNull(rides);
	}
	//Driver existitzen da baina ez du ride-ik
	@Test
	public void test2() {
        String driverUsername = "DriverTest";
        boolean driverCreated = false;

        try {
            testDA.open();
            if (!testDA.existDriver(driverUsername)) {
                testDA.createDriver(driverUsername, "1234");
                driverCreated = true;
            }
            testDA.close();

            // Ejecutar el método sut
            sut.open();
            List<Ride> rides = sut.getRidesByDriver(driverUsername);
            sut.close();

            assertNotNull(rides);
            assertTrue(rides.isEmpty());

        } finally {
            // Limpiar los datos
            testDA.open();
            if (driverCreated) {
                testDA.removeDriver(driverUsername);
            }
            testDA.close();
        }
	}
	//Driver-ak ride inaktiboak bakarrik ditu
	@Test
	public void test3()throws ParseException {
        String driverUsername = "DriverTest";
        boolean driverCreated = false;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date rideDate = sdf.parse("05/10/2026");

        try {
            // Añadir un conductor y un ride
            testDA.open();
            if (!testDA.existDriver(driverUsername)) {
                testDA.createDriver(driverUsername, "1234");
                driverCreated = true;
            }
            Driver d = new Driver (driverUsername,"password");
            Ride inactiveRide = new Ride( "Donostia", "Zarautz", rideDate, 2, 10, d); 
            inactiveRide.setActive(false); // Marcar el ride como inactivo manualmente
            testDA.close();

            // Ejecutar el método sut
            sut.open();
            List<Ride> rides = sut.getRidesByDriver(driverUsername);
            sut.close();

            assertNotNull(rides);
            assertTrue(rides.isEmpty());

        } finally {
            // Limpiar los datos
            testDA.open();
            if (driverCreated) {
                testDA.removeDriver(driverUsername);
            }
            testDA.close();
        }
	}
	
	//Driver-ak ride aktiboak ditu
	@Test
	public void test4()throws ParseException {
        String driverUsername = "DriverTest";
        boolean driverCreated = false;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date rideDate = sdf.parse("05/10/2026");

        try {
            // Añadir un conductor y un ride activo
            testDA.open();
            if (!testDA.existDriver(driverUsername)) {
                testDA.createDriver(driverUsername, "1234");
                driverCreated = true;
            }
            Driver d = new Driver (driverUsername,"password");
            Ride activeRide = new Ride( "Donostia", "Zarautz", rideDate, 2, 10, d);
            testDA.addDriverWithRide(driverUsername, "Donostia", "Zarautz", rideDate,2,10);
            testDA.close();
            
            
            
            // Ejecutar el método sut
            sut.open();
            List<Ride> rides = new ArrayList<>();
            rides = sut.getRidesByDriver(driverUsername);
            sut.close();

            assertNotNull(rides);
            assertEquals(1, rides.size());
            

        } finally {
            // Limpiar los datos
            testDA.open();
            if (driverCreated) {
                testDA.removeDriver(driverUsername);
            }
            testDA.close();
        }
	}
	
	//Driver-ak ride aktibo eta inaktiboak ditu
	@Test
	public void test5() throws ParseException {
        String driverUsername = "DriverTest";
        boolean driverCreated = false;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date rideDate = sdf.parse("05/10/2026");

        try {
            // Añadir un conductor y un ride activo
            testDA.open();
            if (!testDA.existDriver(driverUsername)) {
                testDA.createDriver(driverUsername, "1234");
                driverCreated = true;
            }
            Driver d = new Driver (driverUsername,"password");
            Ride activeRide = new Ride( "Donostia", "Zarautz", rideDate, 2, 10, d);
            testDA.addDriverWithRide(driverUsername, "Donostia", "Zarautz", rideDate,2,10);
            testDA.addDriverWithRide("", "", "", rideDate, 0, 0);
            testDA.close();
            
            
            
            // Ejecutar el método sut
            sut.open();
            List<Ride> rides = new ArrayList<>();
            rides = sut.getRidesByDriver(driverUsername);
            sut.close();

            assertNotNull(rides);
            assertEquals(1, rides.size());
            assertTrue(rides.get(0).isActive());
            

        } finally {
            // Limpiar los datos
            testDA.open();
            if (driverCreated) {
                testDA.removeDriver(driverUsername);
            }
            testDA.close();
        }
	}

}
