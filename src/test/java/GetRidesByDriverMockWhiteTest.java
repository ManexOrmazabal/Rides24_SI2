import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

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

public class GetRidesByDriverMockWhiteTest {
	static DataAccess sut;
	
	protected MockedStatic<Persistence> persistenceMock;

	@Mock
	protected  EntityManagerFactory entityManagerFactory;
	@Mock
	protected  EntityManager db;
	@Mock
    protected  EntityTransaction  et;
	

	@Before
    public  void init() {
        MockitoAnnotations.openMocks(this);
        persistenceMock = Mockito.mockStatic(Persistence.class);
		persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
        .thenReturn(entityManagerFactory);
        
        Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
		Mockito.doReturn(et).when(db).getTransaction();
	    sut=new DataAccess(db);
    }
	@After
    public  void tearDown() {
		persistenceMock.close();
    }
	//Driver ez da existitzen datubasean
	@Test
	public void test1() {
        String driverUsername = "NonExistentDriver";

        // Configurando el mock para lanzar una excepción al intentar obtener el conductor
        when(db.createQuery("SELECT d FROM Driver d WHERE d.username = :username", Driver.class))
                .thenReturn(mock(TypedQuery.class));
        when(db.createQuery("SELECT d FROM Driver d WHERE d.username = :username", Driver.class).setParameter("username", driverUsername))
                .thenReturn(mock(TypedQuery.class));
        when(db.createQuery("SELECT d FROM Driver d WHERE d.username = :username", Driver.class).setParameter("username", driverUsername).getSingleResult())
                .thenThrow(new RuntimeException("Driver not found"));

        // Invoke SUT
        List<Ride> result = sut.getRidesByDriver(driverUsername);

        // Verify the results
        assertNull(result); // Non-existent driver should return null
	}
	//Driver- ak ez du ride-ik
	@Test
	public void test2() {
	       String driverUsername = "ValidDriverWithoutRides";
	        Driver driver = new Driver(driverUsername, "password");
	        driver.setCreatedRides(new ArrayList<>()); // No rides

	        // Configurar el mock para devolver el conductor
	        TypedQuery<Driver> mockedQuery = mock(TypedQuery.class);
	        when(db.createQuery("SELECT d FROM Driver d WHERE d.username = :username", Driver.class))
	                .thenReturn(mockedQuery);
	        when(mockedQuery.setParameter("username", driverUsername))
	                .thenReturn(mockedQuery);
	        when(mockedQuery.getSingleResult())
	                .thenReturn(driver);

	        // Ejecutar SUT
	        List<Ride> result = sut.getRidesByDriver(driverUsername);

	        // Verificar los resultados
	        assertNotNull(result);
	        assertEquals(0, result.size()); // No debería haber viajes, lista vacía
	}
	//Driver-ak ez du ride aktiborik
	@Test
	public void test3() {
	    
	        String driverUsername = "DriverWithNoActiveRides";
	        Driver driver = new Driver(driverUsername, "password");
	        ArrayList<Ride> rides = new ArrayList<>();
	        Ride r = new Ride("Origin2", "Destination2", new Date(), 2, 10, null);
	        if(!r.isActive()) {
	        	rides.add(r); // Inactive ride
	        }
	        
	        
	        driver.setCreatedRides(rides);

	        // Configurar el mock para devolver el conductor
	        TypedQuery<Driver> mockedQuery = mock(TypedQuery.class);
	        when(db.createQuery("SELECT d FROM Driver d WHERE d.username = :username", Driver.class))
	                .thenReturn(mockedQuery);
	        when(mockedQuery.setParameter("username", driverUsername))
	                .thenReturn(mockedQuery);
	        when(mockedQuery.getSingleResult())
	                .thenReturn(driver);

	        // Ejecutar SUT
	        List<Ride> result = sut.getRidesByDriver(driverUsername);

	        // Verificar los resultados
	        assertNotNull(result);
	        assertEquals(0, result.size()); // No debería haber viajes activos, por lo tanto
	}
	//Driver-ak ride aktiboa du
	@Test
	public void test4() {
        String driverUsername = "ValidDriver";
        Driver driver = new Driver(driverUsername, "password");
        ArrayList<Ride> rides = new ArrayList<>();
        rides.add(new Ride("Origin1", "Destination1", new Date(), 2, 10, driver)); // Active ride
        driver.setCreatedRides(rides);

        // Configurar el mock para devolver el conductor
        TypedQuery<Driver> mockedQuery = mock(TypedQuery.class);
        when(db.createQuery("SELECT d FROM Driver d WHERE d.username = :username", Driver.class))
                .thenReturn(mockedQuery);
        when(mockedQuery.setParameter("username", driverUsername))
                .thenReturn(mockedQuery);
        when(mockedQuery.getSingleResult())
                .thenReturn(driver);

        // Ejecutar SUT
        List<Ride> result = sut.getRidesByDriver(driverUsername);

        // Verificar los resultados
        assertNotNull(result);
        assertEquals(1, result.size()); // Asegurar que se devuelva el viaje activo
        assertTrue(result.get(0).isActive()); // Verificar que el viaje sea activo
	}

}
