import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dataAccess.DataAccess;
import domain.Driver;
import domain.Ride;
import domain.Traveler;
import testOperations.TestDataAccess;

public class BookRideBDWhiteTest {

    // sut: system under test
    static DataAccess sut;

    // additional operations needed to execute the test
    static TestDataAccess testDA;

    private Traveler traveler;
    private Driver driver;
    private Ride ride;

    @Before
    public void setUp() {
        // Inicialización del sistema bajo prueba (SUT) y TestDataAccess
        sut = new DataAccess();
        testDA = new TestDataAccess();

        // Abrir conexiones
        sut.open();
        testDA.open();

        // Crear datos comunes para todas las pruebas
        traveler = new Traveler("testUser", "aa");  // Inicializamos un viajero
        driver = new Driver("driverUser", "aa");  // Inicializamos un conductor
        ride = new Ride("Origin", "Destination", Date.valueOf(LocalDate.now()), 10, 50.0, driver);  // Creamos un viaje
        ride.setRideNumber(1);
        // Añadir a la base de datos usando el TestDataAccess
        sut.addTraveler(traveler.getUsername(), traveler.getPassword());
        driver.addRide(ride.getFrom(), ride.getTo(), ride.getDate(), ride.getnPlaces(), ride.getRideNumber());
    }

    @After
    public void tearDown() {
        // Cerrar conexiones después de cada prueba
        testDA.close();
        sut.close();
    }

    @Test
    public void testBookRideSuccess() {
        // Act
        boolean result = sut.bookRide("testUser", ride, 2, 10.0);

        // Assert
        assertTrue(result); // La reserva debería ser exitosa
        assertEquals(8, ride.getnPlaces()); // Verifica que los lugares se hayan actualizado
        assertEquals(80.0, traveler.getMoney()); // Verifica que el dinero del viajero se haya actualizado
    }

    @Test
    public void testBookRideInsufficientFunds() {
        // Arrange (modificando fondos del viajero)
        traveler.setMoney(100.0);
        sut.updateTraveler(traveler);

        // Act
        boolean result = sut.bookRide("testUser", ride, 3, 0.0); // Precio total sería 150 > 100

        // Assert
        assertFalse(result); // No debería permitir la reserva por falta de fondos
    }

    @Test
    public void testBookRideNotEnoughSeats() {
        // Act
        boolean result = sut.bookRide("testUser", ride, 11, 0.0); // Intento de reservar más asientos de los disponibles

        // Assert
        assertFalse(result); // No debería permitir la reserva por falta de asientos
    }

    @Test
    public void testBookRideInvalidTraveler() {
        // Act
        boolean result = sut.bookRide(null, ride, 2, 0.0); // Usuario no válido

        // Assert
        assertFalse(result); // La reserva no debería ser exitosa
    }
}
