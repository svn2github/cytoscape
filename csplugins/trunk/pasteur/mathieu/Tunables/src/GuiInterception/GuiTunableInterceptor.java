package GuiInterception;


import java.awt.Button;
import java.security.acl.Group;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import Utils.myButton;

import java.util.List;
import java.awt.event.*;



public class GuiTunableInterceptor extends HiddenTunableInterceptor<Guihandler> {


	public JFrame inframe;
	public JFrame outframe;
	static JPanel tunPane = null;
	boolean processdone = false;
	myButton test;
	List<Guihandler> list;
	//PropertiesImpl properties=null;
	//Properties prop = new Properties();
	Guihandler guihandler;
	
	public GuiTunableInterceptor(JFrame inframe,JFrame outframe) {
		super( new GuiHandlerFactory<Guihandler>() );
		this.inframe=inframe;
		this.outframe=outframe;
	}


	protected void process(List<Guihandler> list) {
			//properties = new PropertiesImpl("TunableSampler");
			this.list=list;
			JPanel mainPanel = new JPanel();
			//mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
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
			inframe.setLocation(100, 200);
			inframe.setVisible(true);
			processdone=true;
			for(Guihandler guihandler :list){
				if(guihandler.getclass()==Button.class){
					try{
					test = (myButton) guihandler.getField().get(guihandler.getObject());
					test.addActionListener(new myActionListener());
					this.guihandler=guihandler;
					test.setActionCommand("test");
					}catch(Exception e){e.printStackTrace();}
				}
			}
	}
	
	
	
	protected void display(List<Guihandler> list) {
		this.list=list;
		JPanel resultpane = new JPanel();
		Border selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleBorder = null;
		tunPane = new JPanel();
		if(processdone==true){
			for(Guihandler guihandler : list){
				if(guihandler.getclass()!=Group.class){
					titleBorder = BorderFactory.createTitledBorder(selBorder,guihandler.getTunable().description());
					titleBorder.setTitlePosition(TitledBorder.LEFT);
					titleBorder.setTitlePosition(TitledBorder.TOP);
					tunPane.add(guihandler.update());
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
			outframe.setLocation(100, 500);
			outframe.setVisible(true);
		}
		else System.out.println("No input displayed");
	}

	
	private class myActionListener implements ActionListener{
		public void actionPerformed(ActionEvent event){
			if(event.getActionCommand().equals("test")){
				test.setselected(true);
				try{
					guihandler.getField().set(guihandler.getObject(), test);
					myButton tt = (myButton) guihandler.getField().get(guihandler.getObject());
					System.out.println(tt.getselected());
				}catch(Exception e){e.printStackTrace();}
				display(list);
				
			}
		}
	}
	
	
	
	protected void save(List<Guihandler> list){
		for(Guihandler guihandler : list)	guihandler.handle();
		//properties.saveProperties(prop);
	}
	
	protected void cancel(List<Guihandler> list){
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