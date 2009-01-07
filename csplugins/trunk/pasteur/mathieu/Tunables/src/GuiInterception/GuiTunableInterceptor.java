package GuiInterception;


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import Utils.ListSingleSelection;
import Utils.myButton;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
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
			mainPanel.setBorder(selBorder);
			TitledBorder titleBorder = null;

			java.util.List<JPanel> panes = new ArrayList<JPanel>();
			JPanel init = new JPanel();
			init.setName("init");
			panes.add(init);
			listPane = new ListSingleSelection<JPanel>(panes);
			String group=null;
			
			java.util.List<JPanel> inpanes = new ArrayList<JPanel>();
			inpanes.add(init);
			ListSingleSelection<JPanel> inlistPane = new ListSingleSelection<JPanel>(inpanes);

			
			String type = null;
			boolean exist;
			int nbpanesametype;
			boolean exist2;
			

			for(Guihandler guihandler : list){
				exist=false;
				int nbpane=0;
				exist2=false;
				nbpanesametype=0;
				
				type = guihandler.getTunable().description();	
				group = guihandler.getTunable().group();
				
				if(group.equals("")){
					JPanel pane = guihandler.getInputPanel();
					pane.setName(group);
					panes.add(pane);
				}
				else{
					for(int i=0;i<listPane.getPossibleValues().size();i++){
						if(listPane.getPossibleValues().get(i).getName().equals(group)){
							exist=true;
							nbpane=i;
						}
					}

					if(exist==true){
						for(int j=0;j<inlistPane.getPossibleValues().size();j++){
							if(inlistPane.getPossibleValues().get(j).getName().equals(group+type)){
								exist2=true;
								nbpanesametype=j;
							}
						}
						if(exist2==true){
							inlistPane.getPossibleValues().get(nbpanesametype).add(guihandler.getInputPanel());
						}
						else{
							JPanel inpane = new JPanel();
							inpane.setLayout(new BoxLayout(inpane,BoxLayout.PAGE_AXIS));
							inpane.setName(group+type);
							inpane.add(guihandler.getInputPanel());
							listPane.getPossibleValues().get(nbpane).add(inpane);

							titleBorder = BorderFactory.createTitledBorder(type);
							titleBorder.setTitleColor(Color.red);
							titleBorder.setTitlePosition(TitledBorder.LEFT);
							titleBorder.setTitlePosition(TitledBorder.TOP);
							inpane.setBorder(titleBorder);
							
							inpanes.add(inpane);
							inlistPane=new ListSingleSelection<JPanel>(inpanes);
							
						}
					}
					else{
						JPanel pane = new JPanel();
						JPanel inpane = new JPanel();
						
						inpane.setLayout(new BoxLayout(inpane,BoxLayout.PAGE_AXIS));
						pane.setLayout(new BoxLayout(pane,BoxLayout.PAGE_AXIS));
						
						pane.setName(group);
						inpane.setName(group+type);
						
						inpane.add(guihandler.getInputPanel());
						pane.add(inpane);
						
						titleBorder = BorderFactory.createTitledBorder(group);
						titleBorder.setTitleColor(Color.blue);
						titleBorder.setTitlePosition(TitledBorder.LEFT);
						titleBorder.setTitlePosition(TitledBorder.TOP);
						pane.setBorder(titleBorder);
						
						titleBorder = BorderFactory.createTitledBorder(type);
						titleBorder.setTitleColor(Color.red);
						titleBorder.setTitlePosition(TitledBorder.LEFT);
						titleBorder.setTitlePosition(TitledBorder.TOP);
						inpane.setBorder(titleBorder);
							
						inpanes.add(inpane);
						panes.add(pane);
						inlistPane = new ListSingleSelection<JPanel>(inpanes);
						listPane=new ListSingleSelection<JPanel>(panes);		
					}
				}
			}
			panes.remove(0);
			listPane=new ListSingleSelection<JPanel>(panes);
			inpanes.remove(0);
			inlistPane = new ListSingleSelection<JPanel>(inpanes);
			
			for(int i=0;i<listPane.getPossibleValues().size();i++){
				if(listPane.getPossibleValues().get(i).getName().equals("")){
					mainPanel.add(listPane.getPossibleValues().get(i));
				}
				else{
					for(int j=0;j<inlistPane.getPossibleValues().size();j++){
						if(inlistPane.getPossibleValues().get(j).getName().contains(listPane.getPossibleValues().get(i).getName()))
						listPane.getPossibleValues().get(i).add(inlistPane.getPossibleValues().get(j));
					}
					mainPanel.add(listPane.getPossibleValues().get(i));
				}
				
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
		
		for(Guihandler guihandler : list){
			boolean exist=false;
			int nbpane=0;
			group = guihandler.getTunable().group();
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
			


		
	protected void save(List<Guihandler> handlerlist){
		for(Guihandler guihandler : handlerlist)	guihandler.handle();
	}

	protected void addProps(List<Guihandler> handlerList){	
	}

	protected void processProps(List<Guihandler> handlerList){
	}	
}