package GuiInterception;


import javax.swing.*;
import javax.swing.border.TitledBorder;
import Factory.GroupHandler;
import Factory.IntegerHandler;
import Tunable.Tunable.Param;
import Utils.CollapsablePanel;
import Utils.Group;
import Utils.ListSingleSelection;
import Utils.myButton;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;




public class GuiTunableInterceptor extends HiddenTunableInterceptor<Guihandler> {

	public JFrame inframe;
	public JFrame outframe;
	public JPanel mainPanel;	
	
	public JPanel tunPane = null;

	boolean collapsable = false;
	boolean horizontal = false;
	JPanel bigGroupPane;
	myButton button;
	List<Guihandler> list;
	Guihandler guihandler;
	ListSingleSelection<JPanel> listInPane;
	ListSingleSelection<JPanel> listOutPane;
	
	
	public GuiTunableInterceptor(JFrame inframe,JFrame outframe ,JPanel insidepane) {
		super( new GuiHandlerFactory<Guihandler>() );
		this.inframe = inframe;
		this.outframe = outframe;
		this.mainPanel = insidepane;
		this.mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));
	}


	protected void getInputPanes(List<Guihandler> list) {
			this.list=list;
			java.util.List<JPanel> panesArray = new ArrayList<JPanel>();
			JPanel initPane = new JPanel();

			initPane.setName("init");
			panesArray.add(initPane);
			listInPane = new ListSingleSelection<JPanel>(panesArray);
			
			String group=null;
			Class<?> type;
			bigGroupPane = new JPanel();
			bigGroupPane.setLayout(new GridLayout());
			
			for(Guihandler guihandler : list){
				boolean exist=false;
				int nbpane=0;
				group = guihandler.getTunable().group();
				type = guihandler.getClass();
				
				if(type==GroupHandler.class){
					panesArray.remove(0);
			
					TitledBorder titleBorder2 = BorderFactory.createTitledBorder(guihandler.getTunable().description());
					titleBorder2.setTitleColor(Color.red);
					titleBorder2.setTitlePosition(TitledBorder.LEFT);
					titleBorder2.setTitlePosition(TitledBorder.TOP);
					bigGroupPane.setBorder(titleBorder2);
					Param[] parameters = guihandler.getTunable().flag();
					for(int i=0;i<parameters.length;i++) if(parameters[i]==Param.Collapsable) collapsable = true;
					
					if(collapsable){
						CollapsablePanel collapsepane = new CollapsablePanel(listInPane,inframe);
						collapsepane.setBorder(titleBorder2);
						mainPanel.add(collapsepane);
						collapsable = false;
					}	
					else{
						for(int i=0;i<listInPane.getPossibleValues().size();i++)	bigGroupPane.add(listInPane.getPossibleValues().get(i));
						mainPanel.add(bigGroupPane);
					}
					panesArray = new ArrayList<JPanel>();
					initPane.setName("init");
					panesArray.add(initPane);
					listInPane = new ListSingleSelection<JPanel>(panesArray);
					bigGroupPane = new JPanel();
					bigGroupPane.setLayout(new GridLayout());
				}
				
				
				else{
					for(int i=0;i<listInPane.getPossibleValues().size();i++){
						if(listInPane.getPossibleValues().get(i).getName().equals(group)){
							exist=true;
							nbpane=i;
						}
					}
					if(exist==true){
						Param[] parameters = guihandler.getTunable().flag();
						for(int i=0;i<parameters.length;i++)
							if(parameters[i]==Param.Horizontal) horizontal = true;
						if(horizontal){
							//listInPane.getPossibleValues().get(nbpane).setLayout(new BoxLayout(listInPane.getPossibleValues().get(nbpane),BoxLayout.LINE_AXIS));
							listInPane.getPossibleValues().get(nbpane).setLayout(new GridLayout());
							//listInPane.getPossibleValues().get(nbpane).add(Box.createRigidArea(new Dimension(0, 0)));
							listInPane.getPossibleValues().get(nbpane).add(guihandler.getInputPanel());
							horizontal=false;
						}
						else{
							listInPane.getPossibleValues().get(nbpane).setLayout(new BoxLayout(listInPane.getPossibleValues().get(nbpane),BoxLayout.PAGE_AXIS));
							listInPane.getPossibleValues().get(nbpane).add(Box.createRigidArea(new Dimension(0, 5)));
							listInPane.getPossibleValues().get(nbpane).add(guihandler.getInputPanel());
						}
					}
					else{
						JPanel pane = new JPanel();
						pane.setLayout(new BoxLayout(pane,BoxLayout.PAGE_AXIS));					
						if(guihandler.getTunable().orientation()==Param.Horizontal) pane.setLayout(new BoxLayout(pane,BoxLayout.LINE_AXIS));
						pane.add(guihandler.getInputPanel());		
						TitledBorder titleBorder = BorderFactory.createTitledBorder(group);
						titleBorder.setTitleColor(Color.blue);
						titleBorder.setTitlePosition(TitledBorder.LEFT);
						titleBorder.setTitlePosition(TitledBorder.TOP);
						pane.setBorder(titleBorder);
						
						pane.setName(group);	
						panesArray.add(pane);
						listInPane = new ListSingleSelection<JPanel>(panesArray);		
					}
				}
			}
		panesArray.remove(0);
		listInPane = new ListSingleSelection<JPanel>(panesArray);
		for(int i=0;i<listInPane.getPossibleValues().size();i++) mainPanel.add(listInPane.getPossibleValues().get(i));
//		inframe.pack();
	}
	

	
	
	protected void display(List<Guihandler> list) {
		this.list=list;
		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new BoxLayout(resultPanel,BoxLayout.PAGE_AXIS));
		resultPanel.removeAll();
		
		java.util.List<JPanel> panesArray2 = new ArrayList<JPanel>();
		JPanel initPane = new JPanel();
		initPane.setName("init");
		panesArray2.add(initPane);
		listOutPane = new ListSingleSelection<JPanel>(panesArray2);
		String group = null;
		Class<?> type = null;
		
		for(Guihandler guihandler : list){
			boolean exist=false;
			int nbpane=0;
			group = guihandler.getTunable().group();
			type = guihandler.getClass();
			
			
			if(type==GroupHandler.class){
				panesArray2.remove(0);
		
				TitledBorder titleBorder2 = BorderFactory.createTitledBorder(guihandler.getTunable().description());
				titleBorder2.setTitleColor(Color.red);
				titleBorder2.setTitlePosition(TitledBorder.LEFT);
				titleBorder2.setTitlePosition(TitledBorder.TOP);
				bigGroupPane.setBorder(titleBorder2);
			
				for(int i=0;i<listInPane.getPossibleValues().size();i++)	bigGroupPane.add(listInPane.getPossibleValues().get(i));
				resultPanel.add(bigGroupPane);
				
				
				panesArray2 = new ArrayList<JPanel>();
				initPane.setName("init");
				panesArray2.add(initPane);
				listInPane = new ListSingleSelection<JPanel>(panesArray2);
				bigGroupPane = new JPanel();
				bigGroupPane.setLayout(new GridLayout());
			}
			
			else{
				for(int i=0;i<listInPane.getPossibleValues().size();i++){
					if(listInPane.getPossibleValues().get(i).getName().equals(group)){
						exist=true;
						nbpane=i;
					}
				}
				if(exist==true){
					if(guihandler.getTunable().orientation()==Param.Horizontal){
						listInPane.getPossibleValues().get(nbpane).setLayout(new BoxLayout(listInPane.getPossibleValues().get(nbpane),BoxLayout.LINE_AXIS));
						//listInPane.getPossibleValues().get(nbpane).add(Box.createRigidArea(new Dimension(10, 0)));
						listInPane.getPossibleValues().get(nbpane).add(guihandler.getOutputPanel());
					}
					else{
						listInPane.getPossibleValues().get(nbpane).setLayout(new BoxLayout(listInPane.getPossibleValues().get(nbpane),BoxLayout.PAGE_AXIS));
						listInPane.getPossibleValues().get(nbpane).add(Box.createRigidArea(new Dimension(0, 5)));
						listInPane.getPossibleValues().get(nbpane).add(guihandler.getOutputPanel());
					}
				}
				else{
					JPanel pane = new JPanel();
					pane.setLayout(new BoxLayout(pane,BoxLayout.PAGE_AXIS));					
					if(guihandler.getTunable().orientation()==Param.Horizontal) pane.setLayout(new BoxLayout(pane,BoxLayout.LINE_AXIS));
					pane.add(guihandler.getOutputPanel());		
					TitledBorder titleBorder = BorderFactory.createTitledBorder(group);
					titleBorder.setTitleColor(Color.blue);
					titleBorder.setTitlePosition(TitledBorder.LEFT);
					titleBorder.setTitlePosition(TitledBorder.TOP);
					pane.setBorder(titleBorder);
					
					pane.setName(group);	
					panesArray2.add(pane);
					listInPane = new ListSingleSelection<JPanel>(panesArray2);		
				}

			}
		}
		panesArray2.remove(0);
		listOutPane = new ListSingleSelection<JPanel>(panesArray2);
		
		for(int i=0;i<listOutPane.getPossibleValues().size();i++){
			resultPanel.add(listOutPane.getPossibleValues().get(i));
		}
		outframe.setContentPane(resultPanel);
	}
	

		
	protected void save(List<Guihandler> handlerlist){
		for(Guihandler guihandler : handlerlist)	guihandler.handle();
	}

	protected void addProps(List<Guihandler> handlerList){	
	}

	protected void processProps(List<Guihandler> handlerList){
	}
}