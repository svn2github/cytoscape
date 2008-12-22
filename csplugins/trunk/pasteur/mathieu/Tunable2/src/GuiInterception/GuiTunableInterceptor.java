package GuiInterception;


import java.security.acl.Group;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.util.List;
import java.util.Properties;

import Factory.BoundedHandler;
import Properties.*;


public class GuiTunableInterceptor extends HiddenTunableInterceptor<Guihandler> {


	public JFrame inframe;
	public JFrame outframe;
	static JPanel tunPane = null;
	//PropertiesImpl properties=null;
	//Properties prop = new Properties();
	
	public GuiTunableInterceptor(JFrame inframe,JFrame outframe) {
		super( new GuiHandlerFactory<Guihandler>() );
		this.inframe=inframe;
		this.outframe=outframe;
	}


	protected void process(List<Guihandler> list) {
			//properties = new PropertiesImpl("TunableSampler");
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
			Border selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			JPanel tunpane=new JPanel();
			TitledBorder titleBorder = null;
			for (Guihandler guihandler : list) {
				if(guihandler.getclass()!=Group.class){
					//properties.setAll(guihandler.getTunable(),guihandler.getObject(),guihandler.getField());
					//properties.add();
					titleBorder = BorderFactory.createTitledBorder(selBorder,guihandler.getTunable().description());
					titleBorder.setTitlePosition(TitledBorder.LEFT);
					titleBorder.setTitlePosition(TitledBorder.TOP);
					tunpane.add(guihandler.getInputPanel());
				}
				if(guihandler.getclass()==Group.class){
					tunpane.setBorder(titleBorder);
					mainPanel.add(tunpane);
					tunpane=new JPanel();
					tunpane.removeAll();
				}
			}
			//properties.initializeProperties(prop);
			inframe.setContentPane(mainPanel);
			inframe.pack();
			inframe.setLocation(500, 400);
			inframe.setVisible(true);
	}
	
	
	
	protected void display(List<Guihandler> list) {
		JPanel resultpane = new JPanel();
		Border selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleBorder = null;
		tunPane = new JPanel();
		
		for(Guihandler guihandler : list){
			if(guihandler.getclass()!=Group.class){
				titleBorder = BorderFactory.createTitledBorder(selBorder,guihandler.getTunable().description());
				titleBorder.setTitlePosition(TitledBorder.LEFT);
				titleBorder.setTitlePosition(TitledBorder.TOP);
				tunPane.add(guihandler.update());
				//System.out.println(guihandler.getValue());
			}
			if(guihandler.getclass()==Group.class){
				tunPane.setBorder(titleBorder);
				resultpane.add(tunPane);
				tunPane=new JPanel();
				tunPane.removeAll();
			}
			resultpane.add(tunPane);
		}
		outframe.setContentPane(resultpane);
		outframe.pack();
		outframe.setLocation(400, 600);
		outframe.setVisible(true);
	}
	
	
	//A reprendre!
	protected void save(List<Guihandler> list){
		for(Guihandler guihandler : list)	guihandler.handle();
		//properties.saveProperties(prop);
	}
	
	protected void cancel(List<Guihandler> list){
		for(Guihandler guihandler : list) guihandler.cancel();
		//properties.revertProperties();
	}


	@Override
	protected void processProps(List<Guihandler> handlerList) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void addProps(List<Guihandler> handlerList) {
		// TODO Auto-generated method stub
		
	}
		
	
}