/*
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import dataAccess.DataAccess; 
import domain.User; 
import testOperations.TestDataAccess;

public class GauzatuEragiketaBDWhiteTest {

    static DataAccess sut = new DataAccess(); 
    static TestDataAccess testDA = new TestDataAccess();

    @Test
    //Usuario existente
    public void test1() {
        boolean result;
        String username = "nonExistentUser";
        double amount = 100.0;

        try {
            sut.open();
            result = sut.gauzatuEragiketa(username, amount, true);

            assertFalse(result);

        } catch (Exception e) {
            fail("Se produjo una excepción: " + e.getMessage());
        } finally {
            sut.close();
        }
    }

    @Test
    //Usuario sin saldo(retiro)
    public void test2() {
        String username = "userWithNoBalance";
        double amount = 100.0;
        boolean result;

        testDA.open();
        User user = new User(username, "password", "mota");
        testDA.createUser(user);
        testDA.close();

        try {
            sut.open();
            result = sut.gauzatuEragiketa(username, amount, false);

            assertTrue(result); 

            User updatedUser = testDA.getUser(username);
            assertEquals(0.0, updatedUser.getMoney(), 0.01);

        } catch (Exception e) {
            fail("Se produjo una excepción: " + e.getMessage());
        } finally {
            sut.close();
            testDA.open();
            testDA.removeUser(username);
            testDA.close();
        }
    }

    @Test
    //Usuario con saldo(deposito)
    public void test3() {
        String username = "userWithBalance";
        double initialAmount = 100.0;
        double depositAmount = 50.0;

        testDA.open();
        User user = new User(username, "password", "mota");
        user.setMoney(initialAmount);
        testDA.createUser(user); 
        testDA.close();

        boolean result = false;

        try {
            sut.open(); 
            result = sut.gauzatuEragiketa(username, depositAmount, true); 

            assertTrue(result); 

            User updatedUser = testDA.getUser(username);
            assertEquals(initialAmount + depositAmount, updatedUser.getMoney(), 0.01);

        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        } finally {
          
            testDA.open();
            testDA.removeUser(username);
            testDA.close();
            sut.close(); 
        }
    }
}
**/