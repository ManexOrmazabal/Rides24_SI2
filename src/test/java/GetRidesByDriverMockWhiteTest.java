import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
	
	@Test
	public void test2() {
        String driverUsername = "ValidDriverWithoutRides";
        Driver driver = new Driver(driverUsername, "password");
        driver.setCreatedRides(new ArrayList<>()); // No rides

        // Configurando el mock para devolver el conductor
        when(db.createQuery("SELECT d FROM Driver d WHERE d.username = :username", Driver.class))
                .thenReturn(mock(TypedQuery.class));
        when(db.createQuery("SELECT d FROM Driver d WHERE d.username = :username", Driver.class).setParameter("username", driverUsername))
                .thenReturn(mock(TypedQuery.class));
        when(db.createQuery("SELECT d FROM Driver d WHERE d.username = :username", Driver.class).setParameter("username", driverUsername).getSingleResult())
                .thenReturn(driver);

        // Invoke SUT
        List<Ride> result = sut.getRidesByDriver(driverUsername);

        // Verify the results
        assertNotNull(result);
        assertEquals(0, result.size()); // Should return an empty list since the driver has no rides
	}

}
