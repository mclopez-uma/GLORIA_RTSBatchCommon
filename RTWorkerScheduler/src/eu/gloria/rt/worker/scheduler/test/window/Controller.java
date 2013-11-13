package eu.gloria.rt.worker.scheduler.test.window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import eu.gloria.rt.worker.scheduler.core.Scheduler;

/**
 * Controller class to launch the application in windows mode using MVC.
 * 
 * @author Alfredo
 */
public class Controller implements ActionListener {
	public static final String PAUSE    = "Pause";
	public static final String CONTINUE = "Continue";
	public static final String FINISH   = "Finish";
	public static final String UPDATE   = "Update times";
	private Scheduler scheduler;
	private Gui gui;
	
	/**
	 * Default constructor.
	 * 
	 * @throws Exception
	 */
	public Controller() throws Exception {
		gui = new Gui(this);
		scheduler = new Scheduler();
		Thread thr = new Thread(scheduler);
		thr.start();
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		try {
			String comando = actionEvent.getActionCommand();
			if (comando.equals(PAUSE)) {
				gui.setMensaje("Pausing...");
				scheduler.eventPauseScheduler();
				gui.setMensaje("Paused");
				
			}else if (comando.equals(CONTINUE)) {
				gui.setMensaje("Continuing...");
				scheduler.eventContinueScheduler();
				gui.setMensaje("Continued");
				
			}else if (comando.equals(FINISH)) {
				gui.setMensaje("Finishing...");
				scheduler.eventFinishScheduler();
				
			}else if (comando.equals(UPDATE)) {
				gui.setMensaje("Updating times...");
				scheduler.eventConfigScheduler();
				gui.setMensaje("Updated times");
				
			}else{
				gui.setMensaje("Action unknow: '" + comando + "'");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
