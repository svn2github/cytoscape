package org.cytoscape;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.cytoscape.command.*;
import org.cytoscape.work.Handler;
import org.cytoscape.work.HandlerController;
import org.cytoscape.work.TunableInterceptor;
import org.cytoscape.work.internal.gui.GuiTunableInterceptor;
import org.cytoscape.work.internal.props.LoadPropsInterceptor;
import org.cytoscape.work.internal.props.StorePropsInterceptor;





public class applicationGUI <T extends Handler>{

	static Properties InitProps = new Properties();
	static Properties store = new Properties();

    static boolean action;
    
    static JFrame frame;
    static JPanel p;
	
    @SuppressWarnings("unchecked")
	static TunableInterceptor ti;
    @SuppressWarnings("unchecked")
	static TunableInterceptor lpi = null;
	@SuppressWarnings("unchecked")
	static TunableInterceptor spi = null;
	
	public static void main(String[] args) {
                createAndShowGUI();
        }; 

    private static void createAndShowGUI() {
		frame = new JFrame("Tunable Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		

		p = new JPanel(new BorderLayout());
		ti = new GuiTunableInterceptor();
		JPanel q = new JPanel();
		q.add( new JButton(new MyAction("Print Something", new PrintSomething(), ti, lpi,spi)));
		q.add( new JButton(new MyAction("Abstract Active", new AbstractActive(), ti, lpi,spi)));
		q.add( new JButton(new MyAction("Input Test", new input(), ti,lpi,spi)));
		q.add( new JButton(new MyAction("Tunable Sampler", new TunableSampler(), ti,lpi,spi)));
		q.add( new JButton(new MyAction("File Choose", new fileChoose(), ti,lpi,spi)));
		q.add( new JButton(new MyAction("URL Choose", new URLChoose(), ti,lpi,spi)));
		q.add( new JButton(new MyAction("InputStream Choose", new InputStreamChoose(), ti,lpi,spi)));
		q.add( new JButton(new MyAction("Proxy Test", new proxy(), ti,lpi,spi)));
		p.add(q,BorderLayout.NORTH);
        frame.setContentPane(p);
        frame.pack();
        frame.setVisible(true);

    }

	@SuppressWarnings("serial")
	private static class MyAction extends AbstractAction {
		Command com;
		@SuppressWarnings("unchecked")
		TunableInterceptor ti;
		@SuppressWarnings("unchecked")
		TunableInterceptor lpi;
		@SuppressWarnings("unchecked")
		TunableInterceptor spi;
		
		@SuppressWarnings("unchecked")
		MyAction(String title, Command com, TunableInterceptor ti, TunableInterceptor lpi,TunableInterceptor spi) {
			super(title);
			this.com = com;
			this.ti = ti;
			this.lpi = lpi;
			this.spi = spi;
		}
		@SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent a) {

			// set the initial properties
			InitProps = new Properties();
			store = new Properties();
			lpi = new LoadPropsInterceptor(InitProps);
			spi = new StorePropsInterceptor(store);
			
			lpi.loadTunables(com);
			lpi.createUI(com);
			spi.loadTunables(com);
			spi.createUI(com);
			System.out.println("InputProperties of "+com.getClass().getSimpleName()+ " = "+ InitProps);
			
			// intercept the command ,modify any tunable fields, and return the button clicked
			ti.loadTunables(com);
						
			if ( com instanceof HandlerController )
				((HandlerController)com).controlHandlers(ti.getHandlers(com));

			// create the UI based on the object
			action = ti.createUI(com);
			frame.pack();
			
			
			if(action==true){
				spi.loadTunables(com);spi.createUI(com);System.out.println("OutputProperties of "+com.getClass().getSimpleName()+ " = "+ store);
			}
			else if(action==false) {spi=lpi;System.out.println("OutputProperties of "+com.getClass().getSimpleName()+ " = "+ store);}//spi.createUI(com);break;
			frame.pack();
			
			com.execute();
		}
	}
}
