
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.Test;

import dataAccess.DataAccess;
import domain.Driver;
import domain.Ride;
import domain.Traveler;

import java.sql.Date;
import java.time.LocalDate;

public class BookRideMockBlackTest {

    static DataAccess sut;

    protected MockedStatic<Persistence> persistenceMock;

    @Mock
    protected EntityManagerFactory entityManagerFactory;
    @Mock
    protected EntityManager db;
    @Mock
    protected EntityTransaction et;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        persistenceMock = Mockito.mockStatic(Persistence.class);
        persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any())).thenReturn(entityManagerFactory);
        Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
        Mockito.doReturn(et).when(db).getTransaction();
        sut = new DataAccess(db);
    }

    @After
    public void tearDown() {
        persistenceMock.close();
    }

    // Test case (1): Everything is ok.
    @Test
    public void test1() {
        String driverUsername = "Driver Test";
        String rideFrom = "Donostia";
        String rideTo = "Zarautz";
        String passengerUsername = "Passenger Test";
        int seats = 8;
        float fare = 100;
        double travelerBalance = 150.0;
        double discount = 0.0;

        // Mock del viajero y conductor
        Traveler traveler = new Traveler(passengerUsername, "pass");
        traveler.setMoney(travelerBalance);
        Driver driver = new Driver(driverUsername, "pass");

        // Mock del viaje
        Ride ride = new Ride(rideFrom, rideTo, Date.valueOf(LocalDate.now().plusDays(1)), seats, fare, driver);

        // Configurar mocks del EntityManager
        when(db.find(Traveler.class, passengerUsername)).thenReturn(traveler);
        when(db.find(Driver.class, driverUsername)).thenReturn(driver);

        // Ejecutar la lógica
        boolean result = sut.bookRide(passengerUsername, ride, 2, discount);

        // Verificar el resultado
        assertTrue(result);
        assertEquals(50.0, traveler.getMoney(), 0.01); // Verificar que se ha actualizado correctamente el saldo del viajero
        verify(db, times(1)).merge(traveler); // Verificar que se ha persistido el viajero
    }

    // Test case (2): Traveler is null.
    @Test
    public void test2() {
        String driverUsername = "Driver Test";
        String rideFrom = "Donostia";
        String rideTo = "Zarautz";
        String passengerUsername = null; // Viajero nulo
        int seats = 8;
        float fare = 100;
        double discount = 0.0;

        // Mock del conductor y viaje
        Driver driver = new Driver(driverUsername, "pass");
        Ride ride = new Ride(rideFrom, rideTo, Date.valueOf(LocalDate.now().plusDays(1)), seats, fare, driver);

        // Configurar mocks del EntityManager
        when(db.find(Driver.class, driverUsername)).thenReturn(driver);

        // Ejecutar la lógica
        boolean result = sut.bookRide(null, ride, 2, discount);

        // Verificar el resultado
        assertFalse(result); // No debería permitir la reserva si el viajero es nulo
    }

    // Test case (3): Insufficient seats available.
    @Test
    public void test3() {
        String driverUsername = "Driver Test";
        String rideFrom = "Donostia";
        String rideTo = "Zarautz";
        String passengerUsername = "Passenger Test";
        int seats = 1; // Solo un asiento disponible
        float fare = 100;
        double travelerBalance = 150.0;
        double discount = 0.0;

        // Mock del viajero, conductor y viaje
        Traveler traveler = new Traveler(passengerUsername, "pass");
        traveler.setMoney(travelerBalance);
        Driver driver = new Driver(driverUsername, "pass");
        Ride ride = new Ride(rideFrom, rideTo, Date.valueOf(LocalDate.now().plusDays(1)), seats, fare, driver);

        // Configurar mocks del EntityManager
        when(db.find(Traveler.class, passengerUsername)).thenReturn(traveler);
        when(db.find(Driver.class, driverUsername)).thenReturn(driver);

        // Ejecutar la lógica
        boolean result = sut.bookRide(passengerUsername, ride, 2, discount); // Se solicita más asientos de los disponibles

        // Verificar el resultado
        assertFalse(result); // No debería permitir la reserva si no hay suficientes asientos
        verify(db, never()).merge(traveler); // No se debería haber persistido el viajero
    }

    // Test case (4): Insufficient funds.
    @Test
    public void test4() {
        String driverUsername = "Driver Test";
        String rideFrom = "Donostia";
        String rideTo = "Zarautz";
        String passengerUsername = "Passenger Test";
        int seats = 8;
        float fare = 200; // Tarifa que excede el saldo del viajero
        double travelerBalance = 150.0; // Fondos insuficientes
        double discount = 0.0;

        // Mock del viajero, conductor y viaje
        Traveler traveler = new Traveler(passengerUsername, "pass");
        traveler.setMoney(travelerBalance);
        Driver driver = new Driver(driverUsername, "pass");
        Ride ride = new Ride(rideFrom, rideTo, Date.valueOf(LocalDate.now().plusDays(1)), seats, fare, driver);

        // Configurar mocks del EntityManager
        when(db.find(Traveler.class, passengerUsername)).thenReturn(traveler);
        when(db.find(Driver.class, driverUsername)).thenReturn(driver);

        // Ejecutar la lógica
        boolean result = sut.bookRide(passengerUsername, ride, 2, discount); // Fondos insuficientes

        // Verificar el resultado
        assertFalse(result); // No debería permitir la reserva si no hay fondos suficientes
        verify(db, never()).merge(traveler); // No se debería haber persistido el viajero
    }

    // Test case (5): Invalid discount.
    @Test
    public void test5() {
        String driverUsername = "Driver Test";
        String rideFrom = "Donostia";
        String rideTo = "Zarautz";
        String passengerUsername = "Passenger Test";
        String passengerPassword = "pass";
        int seats = 8;
        float fare = 100;
        double travelerBalance = 150.0;
        double invalidDiscount = 180.0; // Descuento que excede la tarifa

        // Mock del viajero, conductor y viaje
        Traveler traveler = new Traveler(passengerUsername, passengerPassword);
        traveler.setMoney(travelerBalance);
        Driver driver = new Driver(driverUsername, "pass");
        Ride ride = new Ride(rideFrom, rideTo, Date.valueOf(LocalDate.now().plusDays(1)), seats, fare, driver);

        // Configurar mocks del EntityManager
        when(db.find(Traveler.class, passengerUsername)).thenReturn(traveler);
        when(db.find(Driver.class, driverUsername)).thenReturn(driver);

        // Ejecutar la lógica
        boolean result = sut.bookRide(passengerUsername, ride, 2, invalidDiscount); // Descuento inválido

        // Verificar el resultado
        assertFalse(result); // No debería permitir la reserva con un descuento inválido
        verify(db, never()).merge(traveler); // No se debería haber persistido el viajero
    }

    // Test case (6): The ride does not exist.
    @Test
    public void test6() {
        String driverUsername = "Driver Test";
        String rideFrom = "Donostia";
        String rideTo = "Zarautz";
        String passengerUsername = "Passenger Test";
        double discount = 0.0;

        // El viaje es nulo
        Ride ride = null;

        // Ejecutar la lógica
        boolean result = sut.bookRide(passengerUsername, ride, 2, discount);

        // Verificar el resultado
        assertFalse(result); // No debería permitir la reserva si el viaje no existe
    }
}

