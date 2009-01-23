package Main;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import Props.*;
import GuiInterception.*;
import Command.*;
import HandlerFactory.Handler;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Properties;

public class application{	
	private static JFrame mainframe = new JFrame("TunableSampler");;
	private static JFrame outputframe = new JFrame("Results");
	private static JPanel mainpane;
	
	private static JPanel highpane;
	private static JPanel lowpane;
	private static Box buttonBox;
	
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
		
		mainframe.setResizable(false);
		outputframe.setResizable(false);
		mainpane.setLayout(new BoxLayout(mainpane,BoxLayout.PAGE_AXIS));
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainframe.setVisible(true);
		buttonBox = Box.createHorizontalBox ();
		lowpane.add (buttonBox);
		lowpane.setBorder (BorderFactory.createEmptyBorder (10, 10, 10, 10));
		
		mainpane.add(highpane);
		mainpane.add(lowpane);
	
		ti = new GuiTunableInterceptor(mainframe,outputframe,highpane);
		ti.intercept(commander);
		lp.intercept(commander);
		
		buttonBox.add(button = createButton("save settings","save",'s'));
		buttonBox.add (Box.createHorizontalGlue ());
		buttonBox.add (Box.createHorizontalStrut (4));
		buttonBox.add(button = createButton("cancel","cancel",'c'));
		buttonBox.add (Box.createHorizontalStrut (4));
		buttonBox.add(button = createButton("done","done",'d'));
		buttonBox.add (Box.createHorizontalStrut (10));
		
		if(ti!=null){
			ti.GetInputPanes();
			lp.addProperties();
			System.out.println("InputProperties = " + InputProperties);
		}
		else System.out.println("No input");
		mainframe.setContentPane(mainpane);
		mainframe.pack();
	}

	

	private static JButton createButton (String title,String command)  {
		return createButton (title,command, '\0');
	}
	
	private static JButton createButton(String title,String command,char mnemonic){
		JButton button = new JButton(title);
		button.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED,Color.gray.brighter(),Color.gray.darker()));
		button.addActionListener(new myActionListener());
		button.setActionCommand(command);
		if (mnemonic != '\0') button.setMnemonic (mnemonic);
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