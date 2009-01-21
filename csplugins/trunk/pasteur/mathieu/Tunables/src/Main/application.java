package Main;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import Props.*;
import GuiInterception.*;
import Command.*;
import HandlerFactory.Handler;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Properties;

public class application{	
	private static JFrame mainframe = new JFrame("TunableSampler");;
	private static JFrame outputframe = new JFrame("Results");
	private static JPanel mainpane;
	
	private static JPanel highpane;
	private static JPanel lowpane;

	public static JButton button;
	public static command commander = new input();
	public static LinkedList<Handler> TunList = new LinkedList<Handler>();
	public static TunableInterceptor ti = null;

	static Properties InputProperties = new Properties();
	static TunableInterceptor lp = new LoadPropsInterceptor(InputProperties);
	static Properties store = new Properties();
	static TunableInterceptor sp = new StorePropsInterceptor(store);
	static TunableInterceptor canceled = new StorePropsInterceptor(InputProperties);

	
	public static void main(String[] args){
        CreateGUIandStart();
    }

	public static void CreateGUIandStart(){
		mainpane = new JPanel();
		highpane = new JPanel();
		lowpane = new JPanel();
		
		mainpane.setLayout(new BoxLayout(mainpane,BoxLayout.PAGE_AXIS));
		mainframe.setContentPane(mainpane);
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainframe.setVisible(true);
		lowpane.setLayout(new FlowLayout());
		mainpane.add(highpane);
		mainpane.add(lowpane);
	
		ti = new GuiTunableInterceptor(mainframe,outputframe,highpane);

		ti.intercept(commander);
		lp.intercept(commander);

		lowpane.add(button = createButton("save settings","save"));
		lowpane.add(button = createButton("cancel","cancel"));
		lowpane.add(button = createButton("done","done"));
		
		if(ti!=null){
			ti.GetInputPanes();
			lp.addProperties();
			System.out.println("InputProperties = " + InputProperties);
		}
		else System.out.println("No input");
		mainframe.pack();
	}

	
	private static JButton createButton(String title,String command){
		JButton button = new JButton(title);
		button.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED,Color.gray.brighter(),Color.gray.darker()));
		button.addActionListener(new myActionListener());
		button.setActionCommand(command);
		return button;
	}
	

	private static class myActionListener implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			String command = ae.getActionCommand();
			if(command.equals("done")){
				if(ti!=null){
					ti.Display();
					outputframe.pack();
					outputframe.setLocation(500, 100);
					outputframe.setVisible(true);
				}
				else System.out.println("no input");
			}
			else if(command.equals("save")){
				if(ti!=null){
					outputframe.dispose();
					ti.Save();
				}
				else System.out.println("No input");
				sp.intercept(commander);
				sp.ProcessProperties();
				System.out.println("OutputSavedProperties = " + store);
			}
			else if(command.equals("cancel")){
				lp.ProcessProperties();
				sp.ProcessProperties();
				System.out.println("OutputCanceledProperties = " + store);
				mainframe.dispose();
				outputframe.dispose();
			}
		}
	}
}