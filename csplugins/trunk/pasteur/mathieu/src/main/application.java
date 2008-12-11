package main;

import javax.swing.*;
import Command.*;
import GuiInterception.*;
import HandlerFactory.Handler;
import Interceptors.*;
import java.awt.event.*;
import java.util.LinkedList;
import Process.*;


public class application{
	
	private static JFrame mainframe;
	private static JPanel pane;
	private static JMenuItem menuItem;
	public static Command commander = new Input();
	private static ProcessingTunableList tunablelist;
	public static TunableInterceptor<Handler> ti = new GuiTunableInterceptor();

	
	public static LinkedList<Handler> TunList = new LinkedList<Handler>();
	
	
		
	public static void main(String[] args){
            	CreateGUIandStart();
    }

	
	
	public static void CreateGUIandStart(){
		mainframe = new JFrame("TunableSampler");
		pane = new JPanel();
		JMenuBar MenuBar = new JMenuBar();
		mainframe.setJMenuBar(MenuBar);
		
		JMenu menu1 = new JMenu("Tunables");
		MenuBar.add(menu1);
		menuItem = new JMenuItem("Catch Tunables");
		menuItem.addActionListener(new myActionListener1());
		menuItem.setActionCommand("catch");
		menu1.add(menuItem);
		
		JMenu menu2 = new JMenu("Display");
		MenuBar.add(menu2);
		menuItem = new JMenuItem("Input");
		menuItem.addActionListener(new myActionListener2());
		menuItem.setActionCommand("input");
		menu2.add(menuItem);
		

		mainframe.setContentPane(pane);
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainframe.setLocation(400, 300);
		mainframe.setSize(200, 200);
		mainframe.pack();
		mainframe.setVisible(true);
		
	}

	private static class myActionListener2 implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			String command = ae.getActionCommand();
			if(command.equals("input")){
			pane.removeAll();
			tunablelist = new ProcessingTunableList(TunList);		
			tunablelist.process(mainframe,pane);
			}
		}
	}
		
	private static class myActionListener1 implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			String command = ae.getActionCommand();
			if(command.equals("catch"))TunList = ti.intercept(commander);
		}
	}
}