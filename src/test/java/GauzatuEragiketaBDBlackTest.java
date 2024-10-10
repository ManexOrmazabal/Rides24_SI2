/*
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import dataAccess.DataAccess;
import domain.User;
import testOperations.TestDataAccess;

public class GauzatuEragiketaBDBlackTest {

    static DataAccess sut = new DataAccess();

    static TestDataAccess testDA = new TestDataAccess();

    @Test
    // Existe user y se deposita correctamente
    public void test1() {
        String username = "User Test";
        double initialMoney = 100.0;
        double depositAmount = 50.0;
        boolean deposit = true;

        User user = new User(username, "password", "normal");
        user.setMoney(initialMoney);

        testDA.open();
        testDA.createUser(user);
        testDA.close();

        try {
            sut.open();
            boolean result = sut.gauzatuEragiketa(username, depositAmount, deposit);
            sut.close();

            assertTrue(result);

            testDA.open();
            User updatedUser = testDA.getUser(username);
            assertNotNull(updatedUser);
            assertEquals(initialMoney + depositAmount, updatedUser.getMoney(), 0.01);
            testDA.close();

        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        } finally {
            testDA.open();
            testDA.removeUser(username);
            testDA.close();
        }
    }

    @Test
    //Retiro de dinero exitoso
    public void test2() {
        String username = "User Test";
        double initialMoney = 100.0;
        double withdrawAmount = 50.0;
        boolean deposit = false;

        User user = new User(username, "password", "normal");
        user.setMoney(initialMoney);

        testDA.open();
        testDA.createUser(user);
        testDA.close();

        try {
            sut.open();
            boolean result = sut.gauzatuEragiketa(username, withdrawAmount, deposit);
            sut.close();

            assertTrue(result);

            testDA.open();
            User updatedUser = testDA.getUser(username);
            assertNotNull(updatedUser);
            assertEquals(initialMoney - withdrawAmount, updatedUser.getMoney(), 0.01);
            testDA.close();

        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        } finally {
            testDA.open();
            testDA.removeUser(username);
            testDA.close();
        }
    }

    @Test
    //Usuario no existente
    public void test3() {
        String username = "NonExistentUser";
        double amount = 50.0;
        boolean deposit = true;

        try {
            sut.open();
            boolean result = sut.gauzatuEragiketa(username, amount, deposit);
            sut.close();
            
            assertTrue(!result);

            testDA.open();
            User user = testDA.getUser(username);
            assertNull(user);
            testDA.close();

        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    //Intento de retirar más dinero del disponible
    public void test4() {
        String username = "User Test";
        double initialMoney = 50.0;
        double withdrawAmount = 100.0;
        boolean deposit = false;

        User user = new User(username, "password", "normal");
        user.setMoney(initialMoney);

        testDA.open();
        testDA.createUser(user);
        testDA.close();

        try {
            sut.open();
            boolean result = sut.gauzatuEragiketa(username, withdrawAmount, deposit);
            sut.close();

            assertTrue(result);

            testDA.open();
            User updatedUser = testDA.getUser(username);
            assertNotNull(updatedUser);
            assertEquals(0.0, updatedUser.getMoney(), 0.01);
            testDA.close();

        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        } finally {
            testDA.open();
            testDA.removeUser(username);
            testDA.close();
        }
    }
}

**/
