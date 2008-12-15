package Process;


import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import GuiInterception.Guihandler;
import HandlerFactory.Handler;
import Properties.*;


public class ProcessingTunableList{
	

	LinkedList<Handler> list;
	PropertiesImpl properties = null;
	Properties prop = new Properties();
	
	public ProcessingTunableList(LinkedList<Handler> list){
		this.list=list;
	}
	
	
	public void process(Component frame,Container pane){
		
		properties = new PropertiesImpl("TunableSampler");
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		Border selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		
			for(Handler guihandler : list){
				
				//if(((Guihandler)guihandler).getClass()==GroupHandler.class)
					//continue;
				properties.setAll(((Guihandler)guihandler).getTunable(),((Guihandler)guihandler).getObject(),((Guihandler)guihandler).getField());
				properties.add();
				
				TitledBorder titleBorder = BorderFactory.createTitledBorder(selBorder,((Guihandler)guihandler).getField().getName());
				titleBorder.setTitlePosition(TitledBorder.LEFT);
				titleBorder.setTitlePosition(TitledBorder.TOP);
			
				JPanel tunpane = (((Guihandler)guihandler).getInputPanel());
				tunpane.setBorder(titleBorder);
				pane.add(tunpane);	
			}
		
		properties.initializeProperties(prop);
		

		// Create a panel for our button box
		JPanel buttonBox = new JPanel();

		JButton doneButton = new JButton("Done");
		doneButton.setActionCommand("done");
		doneButton.addActionListener(new myActionListener());

		JButton saveButton = new JButton("Save Settings");
		saveButton.setActionCommand("save");
		saveButton.addActionListener(new myActionListener());

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(new myActionListener());
		buttonBox.add(saveButton);
		buttonBox.add(cancelButton);
		buttonBox.add(doneButton);
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		pane.add(buttonBox);
		((JFrame)frame).setContentPane(pane);
		((Window) frame).pack();
	}


	
	JFrame resultframe;
	JPanel resultpane;
	JButton button;
	JPanel panes;
	
	public class myActionListener implements ActionListener{
		public void actionPerformed(ActionEvent event){
			String command = event.getActionCommand();
			
			if(command.equals("done")){
				resultframe = new JFrame("ResultFrame");
				resultpane = new JPanel();
				for(Handler guihandler : list){
					//if(((Guihandler)guihandler).getClass()==GroupHandler.class) continue;
					panes = ((Guihandler) guihandler).update();
					//((Guihandler) guihandler).handle();
					resultpane.add(panes);
				}
				button = new JButton("Close Results");
				button.setActionCommand("OK");
				button.addActionListener(this);
				resultpane.add(button);
				resultframe.setContentPane(resultpane);
				resultframe.pack();
				resultframe.setVisible(true);
			}
		
		
		
			if(command.equals("save")){
				for(Handler guihandler : list){
					//if(guihandler.getClass()!=GroupHandler.class){
						((Guihandler) guihandler).handle();
					//}
				}
			properties.saveProperties(prop);
			//resultframe = new JFrame("SavedValues");
			//resultframe.setContentPane(properties.getSavedValue());
			//resultframe.pack();
			//resultframe.setLocation(200, 300);
			//resultframe.setVisible(true);
			}
			
			
			if(command.equals("cancel")){
				for(Handler guihandler : list){
				//	if(guihandler.getClass()!=GroupHandler.class){
						((Guihandler) guihandler).cancel();
					//}
				}
				properties.revertProperties();
				//resultframe = new JFrame("DefaultValues = Values canceled");
				//resultframe.setContentPane(properties.getDefaultValue());
				//resultframe.pack();
				//resultframe.setLocation(200, 400);
				//resultframe.setVisible(true);
			}
			
			
			if(command.equals("OK")){
				resultframe.dispose();
			}
		}
	}

}