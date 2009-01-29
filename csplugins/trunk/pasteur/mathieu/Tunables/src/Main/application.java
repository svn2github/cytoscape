package Main;

import GuiInterception.*;
import Props.LoadPropsInterceptor;
import Props.StorePropsInterceptor;
import Command.*;
import java.awt.event.*;
import java.util.Properties;

import javax.swing.*;

public class application {

	static Properties InitProps = new Properties();
	static Properties store = new Properties();

    static int action;
	
	public static void main(String[] args) {
                createAndShowGUI();
        }; 

    private static void createAndShowGUI() {
		JFrame frame = new JFrame("Tunable Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		TunableInterceptor ti = new GuiTunableInterceptor(frame);
		TunableInterceptor lpi = new LoadPropsInterceptor(InitProps);
		TunableInterceptor spi = new StorePropsInterceptor(store);
		

		JPanel p = new JPanel();
		p.add( new JButton(new MyAction("Print Something", new PrintSomething(), ti, lpi,spi)));
		p.add( new JButton(new MyAction("Abstract Active", new AbstractActive(), ti, lpi,spi)));
		p.add( new JButton(new MyAction("Input Test", new input(), ti,lpi,spi)));
		p.add( new JButton(new MyAction("Tunable Sampler", new TunableSampler(), ti,lpi,spi)));
        frame.setContentPane(p);
        frame.pack();
        frame.setVisible(true);
    }

	private static class MyAction extends AbstractAction {
		command com;
		TunableInterceptor ti;
		TunableInterceptor lpi;
		TunableInterceptor spi;
		
		MyAction(String title, command com, TunableInterceptor ti, TunableInterceptor lpi,TunableInterceptor spi) {
			super(title);
			this.com = com;
			this.ti = ti;
			this.lpi = lpi;
			this.spi = spi;
		}
		public void actionPerformed(ActionEvent a) {

			// set the initial properties
			lpi.intercept(com);
			System.out.println("InputProperties of "+com.getClass().getSimpleName()+ " = "+ InitProps);
			
			// intercept the command ,modify any tunable fields, and return the button clicked
			action = ti.intercept(com);
			

			switch(action){
				case 0: spi.intercept(com);break;		
				case 1: spi.intercept(com);lpi.processProperties(com);spi.processProperties(com);break;
				case 2: System.out.println("Done");break;//for the moment
			}
			System.out.println("OutputProperties of "+com.getClass().getSimpleName()+ " = "+ store);

			// execute the command
			com.execute();
		}
	}
}
