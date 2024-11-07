package businessLogic;

public class BLFacadeFactory {
    private static boolean isLocal = true; // Obtener el valor de configuración aquí

    public static BLFacade createBLFacade() {
        if (isLocal) {
            return new BLFacadeLocal();
        } else {
            return new BLFacadeRemote();
        }
    }
}
