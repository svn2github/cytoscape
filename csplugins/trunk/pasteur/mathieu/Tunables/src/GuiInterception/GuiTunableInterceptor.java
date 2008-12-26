package GuiInterception;


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
	myButton button;
	List<Guihandler> list;
	Guihandler guihandler;
	
	public GuiTunableInterceptor(JFrame inframe,JFrame outframe) {
		super( new GuiHandlerFactory<Guihandler>() );
		this.inframe=inframe;
		this.outframe=outframe;
	}


	protected void process(List<Guihandler> list) {
			this.list=list;
			JPanel mainPanel = new JPanel();
			Border selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			JPanel tunpane=new JPanel();
			TitledBorder titleBorder = null;
			for (Guihandler guihandler : list) {
				if(guihandler.getField().getType()!=Group.class){
					titleBorder = BorderFactory.createTitledBorder(selBorder,guihandler.getTunable().description());
					titleBorder.setTitlePosition(TitledBorder.LEFT);
					titleBorder.setTitlePosition(TitledBorder.TOP);
					tunpane.add(guihandler.getInputPanel());
				}
				if(guihandler.getField().getType()==Group.class){
					tunpane.setBorder(titleBorder);
					mainPanel.add(tunpane);
					tunpane=new JPanel();
					tunpane.removeAll();
				}
			}
			inframe.setContentPane(mainPanel);
			inframe.pack();
			inframe.setLocation(100, 200);
			inframe.setVisible(true);
			processdone=true;
			
			//Test to display the OutputFrame when Button is selected
			for(Guihandler guihandler :list){
				if(guihandler.getField().getType()==myButton.class){
					try{
						button = (myButton) guihandler.getField().get(guihandler.getObject());
						button.addActionListener(new myActionListener());
						this.guihandler=guihandler;
						button.setActionCommand(guihandler.getField().getName());
					}catch(Exception e){e.printStackTrace();}
				}
			}
	}
	
	private class myActionListener implements ActionListener{
		public void actionPerformed(ActionEvent event){
			if(event.getActionCommand().equals(guihandler.getField().getName())){
				button.setselected(true);
				try{
					guihandler.getField().set(guihandler.getObject(), button);
				}catch(Exception e){e.printStackTrace();}
				display(list);	
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
				if(guihandler.getField().getType()!=Group.class){
					titleBorder = BorderFactory.createTitledBorder(selBorder,guihandler.getTunable().description());
					titleBorder.setTitlePosition(TitledBorder.LEFT);
					titleBorder.setTitlePosition(TitledBorder.TOP);
					tunPane.add(guihandler.update());
				}
				if(guihandler.getField().getType()==Group.class){
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

		
	protected void save(List<Guihandler> list){
		for(Guihandler guihandler : list)	guihandler.handle();
	}

	protected void addProps(List<Guihandler> handlerList){	
	}

	protected void processProps(List<Guihandler> handlerList){
	}	
}