package GuiInterception;

import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.event.*;
import Command.command;
import Tunable.*;
import HandlerFactory.*;
import java.util.List;


public class GuiTunableInterceptor extends HiddenTunableInterceptor<Guihandler> {

	private Component parent;
	public JFrame inframe;
	public JFrame outframe;
	
	public GuiTunableInterceptor(Component parent, JFrame inframe,JFrame outframe) {
		super( new GuiHandlerFactory() );
		this.parent = parent;
		this.inframe=inframe;
		this.outframe=outframe;
	}

	protected void process(List<Guihandler> list) {
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
			Border selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			for (Guihandler guihandler : list) {
				TitledBorder titleBorder = BorderFactory.createTitledBorder(selBorder,(guihandler.getField().getName()));
				titleBorder.setTitlePosition(TitledBorder.LEFT);
				titleBorder.setTitlePosition(TitledBorder.TOP);
				JPanel tunpane = guihandler.getInputPanel();
				tunpane.setBorder(titleBorder);
				mainPanel.add(tunpane);
			}
			inframe.setContentPane(mainPanel);
			inframe.pack();
			inframe.setLocation(500, 400);
			inframe.setVisible(true);

	}
	
	protected void display(List<Guihandler> list) {
		JPanel resultpane = new JPanel();
		JPanel tunpan = null;
		for(Guihandler guihandler : list){
			//if(((Guihandler)guihandler).getClass()==GroupHandler.class) continue;
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