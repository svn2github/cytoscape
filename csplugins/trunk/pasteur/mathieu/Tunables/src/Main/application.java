package Main;

import javax.swing.*;

import Props.*;
import GuiInterception.*;
import Command.*;
import HandlerFactory.Handler;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Properties;

public class application{
	
	private static JFrame mainframe = new JFrame("TunableSampler");;
	public static JFrame outputframe = new JFrame("Results");
	
	private static JMenuItem menuItem;
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
		
		JMenuBar MenuBar = new JMenuBar();
		mainframe.setJMenuBar(MenuBar);
		ti = new GuiTunableInterceptor(mainframe,outputframe);
		ti.intercept(commander);
		lp.intercept(commander);
	
		
		if(ti!=null){
			ti.Process();
			lp.addProperties();
			System.out.println("InputProperties = " + InputProperties);
		}
		else System.out.println("No input");
		
		
		JMenu menu3 = new JMenu("Values");
		MenuBar.add(menu3);
		menuItem = new JMenuItem("Save settings");
		menuItem.addActionListener(new myActionListener4());
		menuItem.setActionCommand("save");
		menu3.add(menuItem);
		
		menuItem = new JMenuItem("Cancel");
		menuItem.addActionListener(new myActionListener5());
		menuItem.setActionCommand("cancel");
		menu3.add(menuItem);

		menuItem = new JMenuItem("Done");
		menuItem.addActionListener(new myActionListener3());
		menuItem.setActionCommand("done");
		menu3.add(menuItem);

	
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		mainframe.setLocation(50,50);
//		mainframe.setSize(200, 200);
//		mainframe.pack();
//		mainframe.setVisible(true);
	}


	private static class myActionListener5 implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			String command = ae.getActionCommand();
			if(command.equals("cancel")){
				lp.ProcessProperties();
				sp.ProcessProperties();
				System.out.println("OutputCanceledProperties = " + store);			
			}
		}
	}
	
	
	private static class myActionListener4 implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			String command = ae.getActionCommand();
			if(command.equals("save")){
				if(ti!=null){
					outputframe.dispose();
					//inputframe.dispose();
					ti.Save();
				}
				else System.out.println("No input");
				sp.intercept(commander);
				sp.ProcessProperties();
				System.out.println("OutputSavedProperties = " + store);
			}
		}
	}

	
	
	private static class myActionListener3 implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			String command = ae.getActionCommand();
			if(command.equals("done")){
				if(ti!=null){
					ti.Display();
					ti.Process();
				}
				else System.out.println("no input");
			}
		}
	}
}