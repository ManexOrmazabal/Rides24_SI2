package gui;

import java.util.Locale;
import javax.swing.UIManager;
import businessLogic.BLFacade;
import businessLogic.BLFacadeFactory;
import businessLogic.BLFacadeLocal;
import businessLogic.ExtendedIterator;
import configuration.ConfigXML;

public class ApplicationLauncher {

	public static void main(String[]	args)	{
		 ConfigXML config = ConfigXML.getInstance();
	        System.out.println(config.getLocale());
	        Locale.setDefault(new Locale(config.getLocale()));
	        System.out.println("Locale: " + Locale.getDefault());

	        try {
	            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
	            BLFacade appFacadeInterface = BLFacadeFactory.createBLFacade();

	            // Establecemos la lógica de negocio en la GUI principal
	            MainGUI.setBussinessLogic(appFacadeInterface);
	            MainGUI mainGui = new MainGUI();
	            mainGui.setVisible(true);

	        } catch (Exception e) {
	            System.out.println("Error in ApplicationLauncher: " + e.toString());
	        }
	}
}

