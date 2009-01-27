package GuiInterception;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import Factory.GroupHandler;
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
	JPanel bigGroupPaneInput;
	JPanel bigGroupPaneOutput;
	myButton button;
	List<Guihandler> list;
	Guihandler guihandler;
	ListSingleSelection<JPanel> listInPane;
	ListSingleSelection<JPanel> listOutPane;
	
	
	/*------------------Constructor--------------------------*/	
	public GuiTunableInterceptor(JFrame inframe,JFrame outframe ,JPanel insidepane) {
		super( new GuiHandlerFactory<Guihandler>() );
		this.inframe = inframe;
		this.outframe = outframe;
		this.mainPanel = insidepane;
	}

	/*------------------Put the JPanels for each tunables into the Main JPanel of the JFrame--------------------------*/
	protected void getInputPanes(List<Guihandler> list) {
			this.list=list;
			mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));
			
			//List to get the JPanel for each Tunable
			java.util.List<JPanel> panesArray = new ArrayList<JPanel>();
			JPanel initPane = new JPanel();
			initPane.setName("init");
			panesArray.add(initPane);
			listInPane = new ListSingleSelection<JPanel>(panesArray);
			
			String group = null;
			Class<?> type;
			bigGroupPaneInput = new JPanel();
			bigGroupPaneInput.setLayout(new GridLayout());
			
			
			//Loop to get the JPanel for each tunable(except GroupTunable : use after)
			for(Guihandler guihandler : list){
				boolean exist=false;
				int nbpane=0;
				group = guihandler.getTunable().group();
				type = guihandler.getClass();

				if(type!=GroupHandler.class){
					for(int i=0;i<listInPane.getPossibleValues().size();i++){
						if(listInPane.getPossibleValues().get(i).getName().equals(group)){
							exist=true;
							nbpane=i;
						}
					}
					//if the Panel for this kind of Tunable exists, the JPanel for the tunable is added to this one(by using the name of the JPanel)
					if(exist==true){
						Param[] parameters = guihandler.getTunable().flag();
						for(int i=0;i<parameters.length;i++)
							if(parameters[i]==Param.Horizontal) horizontal = true;
						if(horizontal){
							listInPane.getPossibleValues().get(nbpane).setLayout(new GridLayout());
							listInPane.getPossibleValues().get(nbpane).add(guihandler.getPanel());
							horizontal=false;
						}
						else{
							listInPane.getPossibleValues().get(nbpane).setLayout(new BoxLayout(listInPane.getPossibleValues().get(nbpane),BoxLayout.PAGE_AXIS));
							listInPane.getPossibleValues().get(nbpane).add(Box.createRigidArea(new Dimension(0, 5)));
							listInPane.getPossibleValues().get(nbpane).add(guihandler.getPanel());
						}
					}
					//else(don't have the JPanel), it is created with the name of the group
					else{
						JPanel pane = new JPanel();
						pane.setLayout(new BoxLayout(pane,BoxLayout.PAGE_AXIS));					
						if(guihandler.getTunable().orientation()==Param.Horizontal) pane.setLayout(new BoxLayout(pane,BoxLayout.LINE_AXIS));
						pane.add(guihandler.getPanel());
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

			//List to make bigGroup with the JPanel that were previously sorted by GroupName.
			ArrayList<String> testname = null;
			panesArray.remove(0);
			listInPane = new ListSingleSelection<JPanel>(panesArray);
			
			for(Guihandler guihandler : list){
				if(guihandler.getClass()==GroupHandler.class){
					TitledBorder titleBorder2 = null;
					titleBorder2 = BorderFactory.createTitledBorder(guihandler.getTunable().description());
					titleBorder2.setTitleColor(Color.red);
					titleBorder2.setTitlePosition(TitledBorder.LEFT);
					titleBorder2.setTitlePosition(TitledBorder.TOP);

					//Get the Collapsable Parameter for the Group, and set it collapsed or not(true or false)
					Param[] parameters = guihandler.getTunable().flag();
					for(int i=0;i<parameters.length;i++) if(parameters[i]==Param.Collapsable) collapsable = true;
					Group test = (Group) guihandler.getObject();
					testname = test.getValue();
					
					if(collapsable){
						//add to the bigGroupPaneInput the group of JPanels whose name is in the inputlist of Group
						int	val = listInPane.getPossibleValues().size();
						bigGroupPaneInput = new JPanel();
						for(int i=0;i<testname.size();i++){
							for(int j=0;j<val;j++){
								if(listInPane.getPossibleValues().get(val-1-j-i).getName().equals(testname.get(i))){
									bigGroupPaneInput.add(listInPane.getPossibleValues().get(val-1-j-i));
									panesArray.remove(val-1-j-i);
									break;
								}
							}
						}
						java.util.List<JPanel> collapsableArray = new ArrayList<JPanel>();
						for(int i=0;i<bigGroupPaneInput.getComponentCount();i++){
							collapsableArray.add((JPanel)bigGroupPaneInput.getComponent(i));
						}
						//Set the bigGroupPaneInput as a collapsable JPanel
						ListSingleSelection<JPanel> collapsableList = new ListSingleSelection<JPanel>(collapsableArray);
						CollapsablePanel collapsepane = new CollapsablePanel(collapsableList,inframe,test.isCollapsed());
						collapsepane.setBorder(titleBorder2);
						//collapsepane.setPreferredSize(collapsepane.getPreferredSize());
						mainPanel.add(collapsepane);
						collapsable = false;
					}
					
					//if the group is not collapsable
					else{
						int	val2 = listInPane.getPossibleValues().size();
						bigGroupPaneInput = new JPanel();
						bigGroupPaneInput.setLayout(new BoxLayout(bigGroupPaneInput,BoxLayout.PAGE_AXIS));
						
						for(int i=0;i<testname.size();i++){
							for(int j=0;j<val2;j++){
								if(listInPane.getPossibleValues().get(val2-1-j-i).getName().equals(testname.get(i))){
									bigGroupPaneInput.add(listInPane.getPossibleValues().get(val2-1-j-i));
									panesArray.remove(val2-1-j-i);
									break;
								}
							}
						}
						//create the JPanel which contains the Group of JPanel of the inputList.
						bigGroupPaneInput.setBorder(titleBorder2);
						mainPanel.add(bigGroupPaneInput);
					}
				}
			}
			//Finally, mainPanel get the bigGroupPane(collapsable or not), the groups of JPanels, and the JPanels that are not sorted in groups.
			for(int i=0;i<listInPane.getPossibleValues().size();i++)	mainPanel.add(listInPane.getPossibleValues().get(i));
	}

	

	//Same process as before but to create the OutPutPanel to display the results
	protected void display(List<Guihandler> list) {
		inframe.repaint();
		this.list=list;
		java.util.List<JPanel> panesArray2 = new ArrayList<JPanel>();
		JPanel resultPanel = new JPanel();
		JPanel initPane = new JPanel();
		initPane.setName("init");
		panesArray2.add(initPane);
		listOutPane = new ListSingleSelection<JPanel>(panesArray2);	
		resultPanel.setLayout(new BoxLayout(resultPanel,BoxLayout.PAGE_AXIS));
		bigGroupPaneOutput = new JPanel();
		bigGroupPaneOutput.setLayout(new GridLayout());
		String group = null;
		Class<?> type = null;
		
		for(Guihandler guihandler : list){
			boolean exist=false;
			int nbpane=0;
			group = guihandler.getTunable().group();
			type = guihandler.getClass();
			
			
			if(type!=GroupHandler.class){
				for(int i=0;i<listOutPane.getPossibleValues().size();i++){
					if(listOutPane.getPossibleValues().get(i).getName().equals(group)){
						exist=true;
						nbpane=i;
					}
				}
				if(exist==true){
					Param[] parameters = guihandler.getTunable().flag();
					for(int i=0;i<parameters.length;i++)
						if(parameters[i]==Param.Horizontal) horizontal = true;
					if(horizontal){
						listOutPane.getPossibleValues().get(nbpane).setLayout(new GridLayout());
						listOutPane.getPossibleValues().get(nbpane).add(guihandler.getOutputPanel(guihandler.valueChanged()));
						horizontal=false;
					}
					else{
						listOutPane.getPossibleValues().get(nbpane).setLayout(new BoxLayout(listOutPane.getPossibleValues().get(nbpane),BoxLayout.PAGE_AXIS));
						listOutPane.getPossibleValues().get(nbpane).add(Box.createRigidArea(new Dimension(0, 5)));
						listOutPane.getPossibleValues().get(nbpane).add(guihandler.getOutputPanel(guihandler.valueChanged()));
					}
				}
				else{
					JPanel pane = new JPanel();
					pane.setLayout(new BoxLayout(pane,BoxLayout.PAGE_AXIS));					
					if(guihandler.getTunable().orientation()==Param.Horizontal) pane.setLayout(new BoxLayout(pane,BoxLayout.LINE_AXIS));
					pane.add(guihandler.getOutputPanel(guihandler.valueChanged()));
					TitledBorder titleBorder = BorderFactory.createTitledBorder(group);
					titleBorder.setTitleColor(Color.blue);
					titleBorder.setTitlePosition(TitledBorder.LEFT);
					titleBorder.setTitlePosition(TitledBorder.TOP);
					pane.setBorder(titleBorder);					
					pane.setName(group);	
					panesArray2.add(pane);
					listOutPane = new ListSingleSelection<JPanel>(panesArray2);		
				}
			}
		}
		
		ArrayList<String> testname = null;
		panesArray2.remove(0);
		listOutPane = new ListSingleSelection<JPanel>(panesArray2);

		for(Guihandler guihandler : list){
			if(guihandler.getClass()==GroupHandler.class){
				TitledBorder titleBorder2 = null;
				titleBorder2 = BorderFactory.createTitledBorder(guihandler.getTunable().description());
				titleBorder2.setTitleColor(Color.red);
				titleBorder2.setTitlePosition(TitledBorder.LEFT);
				titleBorder2.setTitlePosition(TitledBorder.TOP);
				Param[] parameters = guihandler.getTunable().flag();
				for(int i=0;i<parameters.length;i++) if(parameters[i]==Param.Collapsable) collapsable = true;
				Group test = (Group) guihandler.getObject();
				testname = test.getValue();
				
				if(collapsable){
					int	val = listOutPane.getPossibleValues().size();
					bigGroupPaneOutput = new JPanel();
					for(int i=0;i<testname.size();i++){
						for(int j=0;j<val;j++){
							if(listOutPane.getPossibleValues().get(val-1-j-i).getName().equals(testname.get(i))){
								bigGroupPaneOutput.add(listOutPane.getPossibleValues().get(val-1-j-i));
								panesArray2.remove(val-1-j-i);
								break;
							}
						}
					}
					java.util.List<JPanel> collapsableArray = new ArrayList<JPanel>();
					for(int i=0;i<bigGroupPaneOutput.getComponentCount();i++){
						collapsableArray.add((JPanel)bigGroupPaneOutput.getComponent(i));
					}
					ListSingleSelection<JPanel> collapsableList = new ListSingleSelection<JPanel>(collapsableArray);
					CollapsablePanel collapsepane = new CollapsablePanel(collapsableList,outframe,test.isCollapsed());
					collapsepane.setBorder(titleBorder2);
					resultPanel.add(collapsepane);
					collapsable = false;
				}
				
				else{
					int	val2 = listOutPane.getPossibleValues().size();
					bigGroupPaneOutput = new JPanel();
					for(int i=0;i<testname.size();i++){
						for(int j=0;j<val2;j++){
							if(listOutPane.getPossibleValues().get(val2-1-j-i).getName().equals(testname.get(i))){
								bigGroupPaneOutput.add(listOutPane.getPossibleValues().get(val2-1-j-i));
								panesArray2.remove(val2-1-j-i);
								break;
							}
						}
					}
					bigGroupPaneOutput.setBorder(titleBorder2);
					resultPanel.add(bigGroupPaneOutput);
				}
			}
		}
		//The resultPanel gets all the resultPanels, the groups of JPanel, and the groups of JPanels' groups.
		for(int i=0;i<listOutPane.getPossibleValues().size();i++)	resultPanel.add(listOutPane.getPossibleValues().get(i));
		outframe.setContentPane(resultPanel);
	}
	

	//for each tunable, get the new value that may have been modified, and set it to the objects
	protected void save(List<Guihandler> handlerlist){
		for(Guihandler guihandler : handlerlist)	guihandler.handle();
	}

	protected void addProps(List<Guihandler> handlerList){	
	}

	protected void processProps(List<Guihandler> handlerList){
	}
}