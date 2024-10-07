
import static org.mockito.Mockito.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import dataAccess.DataAccess;
import domain.Driver;
import domain.Ride;
import domain.Traveler;

public class BookRideMockWhiteTest {

    static DataAccess sut;
    protected MockedStatic<Persistence> persistenceMock;

    @Mock
    protected EntityManagerFactory entityManagerFactory;
    @Mock
    protected EntityManager db;
    @Mock
    protected EntityTransaction et;

    private Traveler traveler;
    private Driver driver;
    private Ride ride;

    @Before 
    public void init() {
        MockitoAnnotations.openMocks(this);
        persistenceMock = Mockito.mockStatic(Persistence.class);
        persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
                .thenReturn(entityManagerFactory);

        Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
        Mockito.doReturn(et).when(db).getTransaction();

        sut = new DataAccess(db); // Inicializar sut correctamente

        // Set up test data
        traveler = new Traveler("testUser", "aa");
        driver = new Driver("driverUser", "aa");
        ride = new Ride("Origin", "Destination", Date.valueOf(LocalDate.now()), 10, 50.0, driver);
        ride.setRideNumber(1);

        // Mock the database interactions for traveler and ride
        when(db.find(Traveler.class, "testUser")).thenReturn(traveler);
        when(db.find(Ride.class, ride.getRideNumber())).thenReturn(ride);
    }

    @After
    public void tearDown() {
        persistenceMock.close();
    }

    @Test
    public void testBookRideSuccess() {
        // Arrange: the traveler has enough money to book the ride
        traveler.setMoney(100.0);

        // Act: simulate booking the ride
        boolean result = sut.bookRide("testUser", ride, 2, 10.0);

        // Assert: verify the ride is successfully booked
        assertTrue(result); // The booking should succeed
        assertEquals(8, ride.getnPlaces()); // 10 places -> 8 places after booking 2 seats
        assertEquals(80.0, traveler.getMoney()); // 100 - (2 * 10) = 80
        verify(db, times(1)).merge(traveler); // Check if traveler is updated in the database
    }

    @Test
    public void testBookRideInsufficientFunds() {
        // Arrange: the traveler has insufficient funds to book the ride
        traveler.setMoney(20.0);

        // Act: try to book a ride that costs more than the traveler's funds
        boolean result = sut.bookRide("testUser", ride, 3, 50.0); // 3 seats * 50 = 150 > 20

        // Assert: the booking should fail due to insufficient funds
        assertFalse(result);
        verify(db, never()).merge(traveler); // Ensure that the traveler was not updated
    }

    @Test
    public void testBookRideNotEnoughSeats() {
        // Arrange: there are only 10 seats available in the ride

        // Act: try to book more seats than are available
        boolean result = sut.bookRide("testUser", ride, 11, 10.0); // 11 seats but only 10 available

        // Assert: the booking should fail due to not enough seats
        assertFalse(result);
        verify(db, never()).merge(traveler); // Ensure no update to the traveler or ride
    }

    @Test
    public void testBookRideInvalidTraveler() {
        // Arrange: simulate an invalid (non-existent) traveler
        when(db.find(Traveler.class, "invalidUser")).thenReturn(null);

        // Act: attempt to book a ride for a non-existent user
        boolean result = sut.bookRide("invalidUser", ride, 2, 10.0);

        // Assert: the booking should fail due to invalid traveler
        assertFalse(result);
        verify(db, never()).merge(any(Traveler.class)); // Ensure no update to any traveler
    }
}

