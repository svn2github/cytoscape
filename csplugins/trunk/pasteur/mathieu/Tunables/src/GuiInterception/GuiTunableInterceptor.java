package GuiInterception;


import java.security.acl.Group;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import Utils.Bounded;
import Utils.ListMultipleSelection;
import Utils.ListSingleSelection;
import Utils.myButton;

import java.util.ArrayList;
import java.util.List;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.*;



public class GuiTunableInterceptor extends HiddenTunableInterceptor<Guihandler> {


	public JFrame inframe;
	public JFrame outframe;
	JPanel mainPanel;
	static JPanel tunPane = null;
	boolean processdone = false;
	myButton button;
	List<Guihandler> list;
	Guihandler guihandler;
	ListSingleSelection<JPanel> listPane;
	Class<?> type = null;
	
	public GuiTunableInterceptor(JFrame inframe,JFrame outframe) {
		super( new GuiHandlerFactory<Guihandler>() );
		this.inframe = inframe;
		this.outframe = outframe;
	}


	protected void process(List<Guihandler> list) {
			this.list=list;
			mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));
			Border selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			TitledBorder titleBorder = null;
//			JPanel tunpane=new JPanel();
//			for (Guihandler guihandler : list) {
//				if(guihandler.getField().getType()!=Group.class){
//					titleBorder = BorderFactory.createTitledBorder(selBorder,guihandler.getTunable().description());
//					titleBorder.setTitlePosition(TitledBorder.LEFT);
//					titleBorder.setTitlePosition(TitledBorder.TOP);
//					tunpane.add(guihandler.getInputPanel());
//				}
//				if(guihandler.getField().getType()==Group.class){
//					tunpane.setBorder(titleBorder);
//					mainPanel.add(tunpane);
//					tunpane=new JPanel();
//					tunpane.removeAll();
//				}
//			}
			
			
//			JPanel intPane = new JPanel();
//			intPane.setName("Integer");
//			JPanel doubPane =  new JPanel();
//			doubPane.setName("Double");
//			JPanel stringPane =  new JPanel();
//			stringPane.setName("String");
//			JPanel boolPane = new JPanel();
//			boolPane.setName("Boolean");
//			JPanel boundPane = new JPanel();
//			boundPane.setName("Bounded");
//			JPanel lmsPane = new JPanel();
//			lmsPane.setName("ListMultipleSelection");
//			JPanel lssPane = new JPanel();
//			lssPane.setName("ListSingleSelection");
//			JPanel buttonPane = new JPanel();
//			buttonPane.setName("myButton");
//			
//			
//			java.util.List<JPanel> panes = new ArrayList<JPanel>();
//			panes.add(intPane);
//			panes.add(doubPane);
//			panes.add(stringPane);
//			panes.add(boolPane);
//			panes.add(boundPane);
//			panes.add(buttonPane);
//			panes.add(lmsPane);
//			panes.add(lssPane);
//			listPane = new ListSingleSelection<JPanel>(panes);
//			
//			
//			for (Guihandler guihandler : list){
//				type = guihandler.getField().getType();
//				String name = type.getSimpleName();
//				for(int i = 0;i<listPane.getPossibleValues().size();i++){
//					if(listPane.getPossibleValues().get(i).getName().equals(name)){
//						listPane.getPossibleValues().get(i).add(guihandler.getInputPanel());
//						titleBorder = BorderFactory.createTitledBorder(selBorder,guihandler.getTunable().description());
//						titleBorder.setTitlePosition(TitledBorder.LEFT);
//						titleBorder.setTitlePosition(TitledBorder.TOP);
//						listPane.getPossibleValues().get(i).setBorder(titleBorder);		
//					}
//				}
//			}
//			for(int i=0;i<listPane.getPossibleValues().size();i++){
//				mainPanel.add(listPane.getPossibleValues().get(i));
//			}

			java.util.List<JPanel> panes = new ArrayList<JPanel>();
			JPanel init = new JPanel();
			init.setName("init");
			panes.add(init);
			listPane = new ListSingleSelection<JPanel>(panes);
			String group=null;
			String type=null;
			
			for(Guihandler guihandler : list){
				boolean exist=false;
				int nbpane=0;
				group = guihandler.getTunable().group();
				type = guihandler.getField().getType().getSimpleName();
				for(int i=0;i<listPane.getPossibleValues().size();i++){
					if(listPane.getPossibleValues().get(i).getName().equals(group)){
						exist=true;
						nbpane=i;
					}
				}
				if(exist==true){
					listPane.getPossibleValues().get(nbpane).add(guihandler.getInputPanel());
				}
				else{
					JPanel pane = new JPanel();
					pane.setLayout(new BoxLayout(pane,BoxLayout.PAGE_AXIS));
					pane.add(guihandler.getInputPanel());

					titleBorder = BorderFactory.createTitledBorder(group);
					titleBorder.setTitleColor(Color.blue);
					titleBorder.setTitlePosition(TitledBorder.LEFT);
					titleBorder.setTitlePosition(TitledBorder.TOP);
					pane.setBorder(titleBorder);
					pane.setName(group);
					panes.add(pane);
					listPane=new ListSingleSelection<JPanel>(panes);		
				}
			}
			panes.remove(0);
			listPane=new ListSingleSelection<JPanel>(panes);
			
			for(int i=0;i<listPane.getPossibleValues().size();i++){
				mainPanel.add(listPane.getPossibleValues().get(i));
			}
			
			inframe.setContentPane(mainPanel);
			inframe.pack();
			inframe.setLocation(100, 100);
			inframe.setVisible(true);
			processdone=true;
			
			//Test to display the OutputFrame when Button is selected
			for(Guihandler guihandler :list){
				if(guihandler.getField().getType() == myButton.class){
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
				inframe.dispose();
				display(list);
				
			}
		}
	}
	
	
	
	
	protected void display(List<Guihandler> list) {
		this.list=list;
		JPanel resultpane = new JPanel();
		resultpane.setLayout(new BoxLayout(resultpane,BoxLayout.PAGE_AXIS));
		resultpane.removeAll();
		Border selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleBorder = null;
		
		for(int i = 0;i<listPane.getPossibleValues().size();i++){
			listPane.getPossibleValues().get(i).removeAll();
		}
		java.util.List<JPanel> panes = new ArrayList<JPanel>();
		JPanel init = new JPanel();
		init.setName("init");
		panes.add(init);
		listPane = new ListSingleSelection<JPanel>(panes);
		String group = null;
		String type = null;
		
		for(Guihandler guihandler : list){
			boolean exist=false;
			int nbpane=0;
			group = guihandler.getTunable().group();
			type = guihandler.getField().getType().getSimpleName();
			for(int i=0;i<listPane.getPossibleValues().size();i++){
				if(listPane.getPossibleValues().get(i).getName().equals(group)){
					exist=true;
					nbpane=i;
				}
			}
			if(exist==true){
				listPane.getPossibleValues().get(nbpane).add(guihandler.update());
			}
			else{
				JPanel pane = new JPanel();
				pane.setLayout(new BoxLayout(pane,BoxLayout.PAGE_AXIS));
				pane.add(guihandler.update());

				titleBorder = BorderFactory.createTitledBorder(group);
				titleBorder.setTitleColor(Color.blue);
				titleBorder.setTitlePosition(TitledBorder.LEFT);
				titleBorder.setTitlePosition(TitledBorder.TOP);
				pane.setBorder(titleBorder);
				pane.setName(group);
				panes.add(pane);
				listPane=new ListSingleSelection<JPanel>(panes);		
			}
		}
		panes.remove(0);
		listPane=new ListSingleSelection<JPanel>(panes);
		
		for(int i=0;i<listPane.getPossibleValues().size();i++){
			resultpane.add(listPane.getPossibleValues().get(i));
		}
		
		outframe.setContentPane(resultpane);
		outframe.pack();
		outframe.setLocation(500, 100);
		outframe.setVisible(true);
	}
			
			
			
			
			
			
			
			
//			for(Guihandler guihandler : list){
//				if(guihandler.getField().getType()!=Group.class){
//					titleBorder = BorderFactory.createTitledBorder(selBorder,guihandler.getTunable().description());
//					titleBorder.setTitlePosition(TitledBorder.LEFT);
//					titleBorder.setTitlePosition(TitledBorder.TOP);
//					tunPane.add(guihandler.update());
//				}
//				if(guihandler.getField().getType()==Group.class){
//					tunPane.setBorder(titleBorder);
//					resultpane.add(tunPane);
//					tunPane=new JPanel();
//					tunPane.removeAll();
//				}
//				resultpane.add(tunPane);
//			}

		
	protected void save(List<Guihandler> handlerlist){
		for(Guihandler guihandler : handlerlist)	guihandler.handle();
	}

	protected void addProps(List<Guihandler> handlerList){	
	}

	protected void processProps(List<Guihandler> handlerList){
	}	
}