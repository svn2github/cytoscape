package Main;

import java.util.*;
import javax.swing.*;
import GuiInterception.*;
import Command.*;
import HandlerFactory.Handler;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import Props.*;

public class application{
	
	private static JFrame mainframe;
	public static JFrame inputframe;
	public static JFrame outputframe;
	private static JPanel pane;
	private static JMenuItem menuItem;
	public static Properties properties;
	public static command commander=new input();
	public static LinkedList<Handler> TunList = new LinkedList<Handler>();
	public static TunableInterceptor ti = null;
	public static TunableInterceptor pi = null;
	
		
	public static void main(String[] args){

		pi = new LoadPropsInterceptor(properties);
        CreateGUIandStart();
    }

	
	
	public static void CreateGUIandStart(){
		mainframe = new JFrame("TunableSampler");
		pane = new JPanel();
		JMenuBar MenuBar = new JMenuBar();
		mainframe.setJMenuBar(MenuBar);
		
		JMenu menu1 = new JMenu("Tunables");
		MenuBar.add(menu1);
		menuItem = new JMenuItem("CatchTunable");
		menuItem.addActionListener(new myActionListener1());
		menuItem.setActionCommand("catch");
		menu1.add(menuItem);
		
		JMenu menu2 = new JMenu("Display");
		MenuBar.add(menu2);
		menuItem = new JMenuItem("Input");
		menuItem.addActionListener(new myActionListener2());
		menuItem.setActionCommand("input");
		menu2.add(menuItem);

		menuItem = new JMenuItem("Output");
		menuItem.addActionListener(new myActionListener3());
		menuItem.setActionCommand("output");
		menu2.add(menuItem);

		
		JMenu menu3 = new JMenu("Action");
		MenuBar.add(menu3);
		menuItem = new JMenuItem("Save");
		menuItem.addActionListener(new myActionListener4());
		menuItem.setActionCommand("save");
		menu3.add(menuItem);
		
		menuItem = new JMenuItem("Cancel");
		menuItem.addActionListener(new myActionListener5());
		menuItem.setActionCommand("cancel");
		menu3.add(menuItem);
	
		

		mainframe.setContentPane(pane);
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainframe.setLocation(400, 300);
		mainframe.setSize(200, 200);
		mainframe.pack();
		mainframe.setVisible(true);	
	}


	private static class myActionListener5 implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			String command = ae.getActionCommand();
			if(command.equals("cancel")){
			ti.Cancel();
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
			}
		}
	}

	
	
	private static class myActionListener3 implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			String command = ae.getActionCommand();
			if(command.equals("output")){
				if(ti!=null){
					inputframe.dispose();
					ti.Display();
				}
				else System.out.println("no input");
			}
		}
	}
	
	
	private static class myActionListener2 implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			String command = ae.getActionCommand();
			if(command.equals("input")){
				if(ti!=null){
					ti.Process();
				}
				else System.out.println("No input");
			}
		}
	}

		
	private static class myActionListener1 implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			String command = ae.getActionCommand();
			if(command.equals("catch")){
				inputframe=new JFrame("InputParameters");
				outputframe=new JFrame("OutputParameters");
				ti = new GuiTunableInterceptor(inputframe,outputframe);
				ti.intercept(commander);
				
			}
		}
	}
}