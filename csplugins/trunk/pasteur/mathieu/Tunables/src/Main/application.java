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
	
	private static JPanel inpane1;
	private static JPanel inpane2;
	
	
	private static JButton button;
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
		inpane1 = new JPanel();
		inpane2 = new JPanel();
		
		
		mainpane.setLayout(new BoxLayout(mainpane,BoxLayout.PAGE_AXIS));
		mainframe.setContentPane(mainpane);
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainframe.setVisible(true);
		inpane2.setLayout(new FlowLayout());
		
		mainpane.add(inpane1);
		mainpane.add(inpane2);
		
		
		ti = new GuiTunableInterceptor(mainframe,outputframe,inpane1);

		ti.intercept(commander);
		lp.intercept(commander);
		
		button = new JButton("save settings");
		button.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED,Color.gray.brighter(),Color.gray.darker()));
		button.setActionCommand("save");
		inpane2.add(button);
		button.addActionListener(new myActionListener4());		
		button = new JButton("cancel");
		button.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED,Color.gray.brighter(),Color.gray.darker()));		button.setActionCommand("cancel");
		inpane2.add(button);
		button.addActionListener(new myActionListener5());
		button = new JButton("done");
		button.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED,Color.gray.brighter(),Color.gray.darker()));		button.setActionCommand("done");
		inpane2.add(button);
		button.addActionListener(new myActionListener3());

		
		if(ti!=null){
			ti.GetInputPanes();
			lp.addProperties();
			System.out.println("InputProperties = " + InputProperties);
		}
		else System.out.println("No input");
		mainframe.pack();
	}


	private static class myActionListener5 implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			String command = ae.getActionCommand();
			if(command.equals("cancel")){
				lp.ProcessProperties();
				sp.ProcessProperties();
				System.out.println("OutputCanceledProperties = " + store);
				mainframe.dispose();
				outputframe.dispose();
			}
		}
	}
	
	
	private static class myActionListener4 implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			String command = ae.getActionCommand();
			if(command.equals("save")){
				if(ti!=null){
					outputframe.dispose();
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
					outputframe.pack();
					outputframe.setLocation(500, 100);
					outputframe.setVisible(true);
				}
				else System.out.println("no input");
			}
		}
	}
}