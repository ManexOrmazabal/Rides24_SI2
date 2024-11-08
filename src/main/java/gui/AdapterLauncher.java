package gui;
import businessLogic.*;
import domain.Driver;


public class AdapterLauncher {

	public static void main(String[]	args)	{
//		the	BL	is	local
	boolean isLocal =	true;
	BLFacade	blFacade =	new BLFacadeLocal();
	Driver	d= blFacade. getDriver("Urtzi");
	DriverTable	dt=new	DriverTable(d);
	dt.setVisible(true);
	}

}
