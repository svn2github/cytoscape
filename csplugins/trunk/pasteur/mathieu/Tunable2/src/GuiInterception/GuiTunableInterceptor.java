package GuiInterception;


import java.security.acl.Group;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import Factory.GroupHandler;
import java.util.List;


public class GuiTunableInterceptor extends HiddenTunableInterceptor<Guihandler> {


	public static JFrame inframe;
	public JFrame outframe;
	static JPanel tunpan = null;
	//static JPanel resultpane = new JPanel();
	
	public GuiTunableInterceptor(JFrame inframe,JFrame outframe) {
		super( new GuiHandlerFactory() );
		this.inframe=inframe;
		this.outframe=outframe;
	}


	protected void process(List<Guihandler> list) {
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
			Border selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			JPanel tunpane=new JPanel();
			TitledBorder titleBorder = null;
			for (Guihandler guihandler : list) {
				if(guihandler.getclass()!=Group.class){
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
			
		//	JButton button = new JButton("OK");
		//	mainPanel.add(button);
		//	button.addActionListener(new myActionListener(list));
		//	button.setActionCommand("OK");
			inframe.setContentPane(mainPanel);
			inframe.pack();
			inframe.setLocation(500, 400);
			inframe.setVisible(true);
	}
	
//	private static class myActionListener implements ActionListener{
//		
//		List<Guihandler>list;
//		public myActionListener(List<Guihandler> list){
//			this.list=list;
//		}
//		
//		public void actionPerformed(ActionEvent ae){
//			String command = ae.getActionCommand();
//			if(command.equals("OK")){
//				resultpane.removeAll();
//				for(Guihandler guihandler : list){
//					if(guihandler.getClass()==GroupHandler.class) continue;
//					tunpan = guihandler.update();
//					//((Guihandler) guihandler).handle(); NOT SURE
//					resultpane.add(tunpan);
//				}
//				inframe.dispose();
//			}		
//		}
//	}
	
	
	
	protected void display(List<Guihandler> list) {
		
		JPanel resultpane = new JPanel();
		for(Guihandler guihandler : list){
			if(guihandler.getClass()==GroupHandler.class) continue;
			tunpan = guihandler.update();
			//((Guihandler) guihandler).handle(); NOT SURE
			resultpane.add(tunpan);
		}
		outframe.setContentPane(resultpane);
		outframe.pack();
		outframe.setLocation(400, 600);
		outframe.setVisible(true);
	}
	
	protected void save(List<Guihandler> list){
		for(Guihandler guihandler : list) guihandler.handle();	
		//properties.saveProperties(prop);
	}
	
	protected void cancel(List<Guihandler> list){
		for(Guihandler guihandler : list) guihandler.cancel();	
		//properties.saveProperties(prop);
	}
	
	
	
}