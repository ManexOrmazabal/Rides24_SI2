
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import dataAccess.DataAccess;
import domain.User;

public class GauzatuEragiketaMockBlackTest {

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
        persistenceMock = mockStatic(Persistence.class);
        persistenceMock.when(() -> Persistence.createEntityManagerFactory(any()))
            .thenReturn(entityManagerFactory);
        when(entityManagerFactory.createEntityManager()).thenReturn(db);
        when(db.getTransaction()).thenReturn(et);
        sut = new DataAccess(db);
    }

    @After
    public void tearDown() {
        persistenceMock.close();
    }

    @Test
    // Test para verificar el depósito de dinero
    public void testGauzatuEragiketaDeposit() {
        String username = "testUser";
        double initialAmount = 100.0;
        double depositAmount = 50.0;
        double expectedAmount = initialAmount + depositAmount;

        User user = new User(username, "password", "mota");
        user.setMoney(initialAmount);

        // Configuramos el estado mediante mocks
        when(db.find(User.class, username)).thenReturn(user);

        // Invocamos el método bajo prueba
        sut.gauzatuEragiketa(username, depositAmount, true);

        // Verificamos el resultado
        assertEquals(expectedAmount, user.getMoney(), 0.01);
        verify(db).merge(user);
    }

    @Test
    // Test para verificar el retiro de dinero con saldo suficiente
    public void testGauzatuEragiketaWithdraw() {
        String username = "testUser";
        double initialAmount = 100.0;
        double withdrawAmount = 30.0;
        double expectedAmount = initialAmount - withdrawAmount;

        User user = new User(username, "password", "mota");
        user.setMoney(initialAmount);

        when(db.find(User.class, username)).thenReturn(user);

        sut.gauzatuEragiketa(username, withdrawAmount, false);

        assertEquals(expectedAmount, user.getMoney(), 0.01);
        verify(db).merge(user);
    }

    @Test
    // Test para verificar el retiro de dinero con saldo insuficiente
    public void testGauzatuEragiketaWithdrawInsufficientBalance() {
        String username = "testUser";
        double initialAmount = 20.0;
        double withdrawAmount = 30.0;

        User user = new User(username, "password", "mota");
        user.setMoney(initialAmount);

        when(db.find(User.class, username)).thenReturn(user);

        sut.gauzatuEragiketa(username, withdrawAmount, false);

        assertEquals(0, user.getMoney(), 0.01);
        verify(db).merge(user);
    }

    @Test
    // Test para verificar el caso de usuario no encontrado
    public void testGauzatuEragiketaUserNotFound() {
        String username = "unknownUser";
        double amount = 50.0;

        when(db.find(User.class, username)).thenReturn(null);

        boolean result = sut.gauzatuEragiketa(username, amount, true);

        assertFalse(result);
        verify(db, never()).merge(any(User.class));
    }
}
