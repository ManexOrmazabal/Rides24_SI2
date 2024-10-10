
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import domain.User;

public class GauzatuEragiketaMockWhiteTest {
	
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
		persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
		.thenReturn(entityManagerFactory);
		
		Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
		Mockito.doReturn(et).when(db).getTransaction();
		sut = new DataAccess(db);
	}

	@After
	public void tearDown() {
		persistenceMock.close();
	}
	
	@Test
	//El usuario existe y se hace un depósito exitoso.
	public void test1_DepositSuccess() {
		String username = "TestUser";
		double amount = 100.0;

		User user = new User(username, "passwd", "mota");
		user.setMoney(50.0); 

		Mockito.when(db.find(User.class, username)).thenReturn(user);
 
		sut.open();
		boolean result = sut.gauzatuEragiketa(username, amount, true);
		sut.close();

		assertTrue(result);
		assertEquals(150.0, user.getMoney(), 0.01);
	}
	
	@Test
	//El usuario existe y se hace un retiro exitoso.
	public void test2_WithdrawSuccess() {
		String username = "TestUser";
		double amount = 30.0;

		User user = new User(username, "passwd", "mota");
		user.setMoney(100.0); // saldo inicial

		Mockito.when(db.find(User.class, username)).thenReturn(user);
 
		sut.open();
		boolean result = sut.gauzatuEragiketa(username, amount, false);
		sut.close();

		assertTrue(result);
		assertEquals(70.0, user.getMoney(), 0.01);
	}

	@Test
	//El usuario existe y se intenta retirar más dinero del que tiene.
	public void test3_WithdrawExceedsBalance() {
		String username = "TestUser";
		double amount = 150.0;

		User user = new User(username, "passwd", "mota");
		user.setMoney(100.0);

		Mockito.when(db.find(User.class, username)).thenReturn(user);

		sut.open();
		boolean result = sut.gauzatuEragiketa(username, amount, false);
		sut.close();

		assertTrue(result);
		assertEquals(0.0, user.getMoney(), 0.01);
	}
	
	@Test
	//El usuario no existe.
	public void test4_UserDoesNotExist() {
		String username = "NonExistentUser";
		double amount = 100.0;

		Mockito.when(db.find(User.class, username)).thenReturn(null);
 
		sut.open();
		boolean result = sut.gauzatuEragiketa(username, amount, true);
		sut.close();


		assertFalse(result);
	}

	@Test
    //Manejo de excepción al intentar acceder a la base de datos.
	public void test5_DatabaseException() {
		String username = "TestUser";
		double amount = 100.0;

		Mockito.when(db.find(User.class, username)).thenThrow(new RuntimeException("Database Error"));

		sut.open();
		boolean result = sut.gauzatuEragiketa(username, amount, true);
		sut.close();

		assertFalse(result);
	}
}

