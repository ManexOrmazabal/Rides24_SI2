
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import dataAccess.DataAccess;
import domain.Driver;
import domain.Ride;
import testOperations.TestDataAccess;

public class BookRideBDBlackTest {

    // sut: system under test
    private DataAccess sut;
    private TestDataAccess testDA;

    @Before
    public void setUp() {
        sut = new DataAccess();
        testDA = new TestDataAccess();
        sut.open(); // Abre la conexión antes de cada prueba
        testDA.open(); // Abre la conexión antes de cada prueba
    }

    @Test
    // Test case (1): Everything is ok.
    public void test1(){
        String driverUsername = "Driver Test";
        String rideFrom = "Donostia";
        String rideTo = "Zarautz";
        String passengerUsername = "Passenger Test";
        int seats = 8;
        float fare = 100;
        double travelerBalance = 150.0;
        double discount = 0.0;

        // Crea un conductor con un viaje disponible
        testDA.addDriverWithRide(driverUsername, rideFrom, rideTo, null, seats, fare);
        sut.addTraveler(passengerUsername, "pass");
        sut.getTraveler(passengerUsername).setMoney(travelerBalance);

        try {
            // Crea un objeto Ride con la información necesaria
            Ride ride = new Ride(rideFrom, rideTo, Date.valueOf(LocalDate.now().plusDays(1)), seats, fare, sut.getDriver(driverUsername)); // Asegúrate de que el conductor se obtenga correctamente
            sut.bookRide(passengerUsername, ride, 10, discount); // La llamada debe ser correcta

            // Verifica que la reserva se haya realizado correctamente
            assertNotNull(ride);
            assertEquals(ride.getFrom(), rideFrom);
            assertEquals(ride.getTo(), rideTo);
            assertEquals(ride.getDriver().getUsername(), driverUsername);
  

            // Verifica que el saldo del viajero se haya actualizado correctamente
            double updatedBalance = sut.getTraveler(passengerUsername).getMoney();
            assertEquals(50.0, updatedBalance, 0.01); // Original balance - fare
        } finally {
            // Cleanup
            testDA.removeDriver(driverUsername);
        }
    }

    @Test
    // Test case (2): Traveler is null.
    public void test2(){
        String driverUsername = "Driver Test";
        String rideFrom = "Donostia";
        String rideTo = "Zarautz";
        String passengerUsername = null; // Viajero nulo
        int seats = 8;
        float fare = 100;
        double travelerBalance = 150.0;
        double discount = 0.0;

        // Crea un conductor con un viaje disponible
        testDA.addDriverWithRide(driverUsername, rideFrom, rideTo, null, seats, fare);

        try {
            // Invoca el método bajo prueba
            Ride ride = new Ride(rideFrom, rideTo, Date.valueOf(LocalDate.now().plusDays(1)), seats, fare, sut.getDriver(driverUsername));
            assertFalse(sut.bookRide(null, ride, 20, discount)); // Debe manejar el caso de viajero nulo
        } finally {
            testDA.removeDriver(driverUsername);
        }
    }

    @Test
    // Test case (3): Insufficient seats available.
    public void test3() {
        String driverUsername = "Driver Test";
        String rideFrom = "Donostia";
        String rideTo = "Zarautz";
        String passengerUsername = "Passenger Test";
        int seats = 1; // Solo un asiento disponible
        float fare = 100;
        double travelerBalance = 150.0;
        double discount = 0.0;

        // Crea un conductor con un viaje con solo un asiento
        testDA.addDriverWithRide(driverUsername, rideFrom, rideTo, Date.valueOf(LocalDate.now().plusDays(1)), seats, fare);
        sut.addTraveler(passengerUsername, "pass");

        try {
            // Invoca el método bajo prueba
            Ride ride = new Ride(rideFrom, rideTo, Date.valueOf(LocalDate.now().plusDays(1)), seats, fare, sut.getDriver(driverUsername));
            assertFalse(sut.bookRide(driverUsername, ride, 10, discount)); 
          
        } finally {
            testDA.removeDriver(driverUsername);
        }
    }

    @Test
    // Test case (4): Insufficient funds.
    public void test4(){
        String driverUsername = "Driver Test";
        String rideFrom = "Donostia";
        String rideTo = "Zarautz";
        String passengerUsername = "Passenger Test";
        int seats = 8;
        float fare = 200; // Tarifa que excede el saldo del viajero
        double travelerBalance = 150.0; // Fondos insuficientes
        double discount = 0.0;

        // Crea un conductor con un viaje disponible
        testDA.addDriverWithRide(driverUsername, rideFrom, rideTo,Date.valueOf(LocalDate.now().plusDays(1)), seats, fare);
        sut.addTraveler(passengerUsername, "pass");

        try {
            // Invoca el método bajo prueba
            Ride ride = new Ride(rideFrom, rideTo, Date.valueOf(LocalDate.now().plusDays(1)), seats, fare, sut.getDriver(driverUsername));
            assertFalse(sut.bookRide(driverUsername, ride, 20, discount)); // Debe manejar el caso de fondos insuficientes
        } finally {
            testDA.removeDriver(driverUsername);

        }
    }

    @Test
    // Test case (5): Invalid discount.
    public void test5(){
        String driverUsername = "Driver Test";
        String rideFrom = "Donostia";
        String rideTo = "Zarautz";
        String passengerUsername = "Passenger Test";
        String passengerPassword = "pass";
        int seats = 8;
        float fare = 100; 
        double travelerBalance = 150.0;
        double invalidDiscount = 180.0; // Descuento que excede la tarifa

        // Crea un conductor con un viaje disponible
        testDA.addDriverWithRide(driverUsername, rideFrom, rideTo,Date.valueOf(LocalDate.now()) , seats, fare);
        sut.addTraveler(passengerUsername,passengerPassword );

        try {
            // Invoca el método bajo prueba
            Ride ride = new Ride(rideFrom, rideTo, Date.valueOf(LocalDate.now().plusDays(1)), seats, fare, sut.getDriver(driverUsername));
            assertFalse(sut.bookRide(driverUsername, ride, 10, invalidDiscount)); // Debe manejar el caso de descuento inválido
        } finally {
            testDA.removeDriver(driverUsername);

        }
    }

    @Test
    // Test case (6): The ride does not exist.
    public void test6(){
        String driverUsername = "Driver Test";
        String rideFrom = "Donostia";
        String rideTo = "Zarautz";
        String passengerUsername = "Passenger Test";
        double discount = 0.0;

        try {
            // Invoca el método bajo prueba
            Ride ride = null; // La reserva no se creará porque el viaje no existe
            sut.bookRide(driverUsername, ride, 10, discount); // Debe manejar el caso de que el viaje no existe
            assertNull("Expected null because the ride does not exist.", ride); // Se espera null
        } finally {
            sut.close();
            testDA.close();
        }
    }
}

