package eu.gloria.rt.worker.scheduler.test.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Human interface graphic class to launch the application in windows mode using MVC.
 * 
 * @author Alfredo
 */
public class Gui extends JFrame {
	private static final long serialVersionUID = 7028180386660859696L;
	private JButton jbPausar, jbContinuar, jbFinalizar, jbActTiempos;
	private JLabel jlMensaje;
	
	/**
	 * Default constructor.
	 * 
	 * @param ctrl Controller class in MVC.
	 */
	public Gui(Controller ctrl) {
		super("Scheduler GUI by ACJ");
		setLayout(new BorderLayout());
		
		/////////////////////// BOTONES, al norte
		JPanel jpBotones = new JPanel();
		jpBotones.setLayout(new FlowLayout());

		jbPausar     = new JButton(Controller.PAUSE);
		jbContinuar  = new JButton(Controller.CONTINUE);
		jbFinalizar  = new JButton(Controller.FINISH);
		jbActTiempos = new JButton(Controller.UPDATE);

		jbPausar.setActionCommand(Controller.PAUSE);
		jbContinuar.setActionCommand(Controller.CONTINUE);
		jbFinalizar.setActionCommand(Controller.FINISH);
		jbActTiempos.setActionCommand(Controller.UPDATE);

		jbPausar.addActionListener(ctrl);
		jbContinuar.addActionListener(ctrl);
		jbFinalizar.addActionListener(ctrl);
		jbActTiempos.addActionListener(ctrl);
		
		jpBotones.add(jbPausar);
		jpBotones.add(jbContinuar);
		jpBotones.add(jbFinalizar);
		jpBotones.add(jbActTiempos);

		add(jpBotones, BorderLayout.NORTH);
		
		
		/////////////////////// MENSAJES, al sur
		jlMensaje = new JLabel();
		jlMensaje.setText("Initialized");
		jlMensaje.setForeground(Color.BLUE);
		
		add(jlMensaje, BorderLayout.SOUTH);
		
		//setSize(850, 200);
		pack();
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	/**
	 * Method to set a string message in the GUI.
	 * 
	 * @param str The string message.
	 */
	public void setMensaje(String str) {
		jlMensaje.setText(str);
	}
}
