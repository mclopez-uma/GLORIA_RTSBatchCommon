package eu.gloria.rt.worker.scheduler.test.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import eu.gloria.rt.worker.scheduler.core.Scheduler;
import eu.gloria.rt.worker.scheduler.test.window.Controller;

/**
 * @author Alfredo
 * 
 *         Class to launch the application in console mode.
 */
public class Main {
	public static final String WINDOWS_MODE = "-w";

	/**
	 * The launcher method.
	 * 
	 * @param args
	 *            The arguments, nothing are taken into account.
	 * @throws Exception
	 *             In error case
	 */
	public static void main(String[] args) throws Exception {
//		System.setProperty("http.proxyHost", "proxy.alu.uma.es");
//		System.setProperty("http.proxyPort", "3128");
		
		boolean withWindow = false;
		for (int i=0 ; !withWindow && i<args.length ; i++) {
			withWindow = args[i].equalsIgnoreCase(WINDOWS_MODE);
		}
		
		boolean needHelp = args.length > (
//				 (debugMode  ? 1 : 0) + 
//				 (withPass   ? 1 : 0) + 
				 (withWindow ? 1 : 0)
			);
		
		if (needHelp) {
			showHelp();
		}else if (withWindow) {
			new Controller();
		}else{
			Scheduler scheduler = new Scheduler();
			Thread thr = new Thread(scheduler);
			thr.start();
		}
	}
	
	/**
	 * Private method to show the help.
	 */
	private static void showHelp() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					Main.class.getResourceAsStream("/eu/gloria/rt/worker/scheduler/txt/help.txt")));
			
			while (br.ready()) {
				System.out.println(br.readLine());
			}
			
			br.close();
		} catch (Exception e) {
			System.out.println("Sorry, I can not show the help.\n");
			System.out.println("Exception: " + e.getClass().getCanonicalName() + " -> " + e.getLocalizedMessage() + "\n");
			e.printStackTrace();
		}
	}
}
