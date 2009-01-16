package GuiInterception;


import javax.swing.*;
import javax.swing.border.TitledBorder;
import Utils.ListSingleSelection;
import Utils.myButton;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.awt.MenuBar;
import java.awt.event.*;



public class GuiTunableInterceptor extends HiddenTunableInterceptor<Guihandler> {

	public JFrame inframe;
	public JFrame outframe;

	MenuBar menu;
	static JPanel tunPane = null;
	boolean processdone = false;
	myButton button;
	List<Guihandler> list;
	Guihandler guihandler;
	ListSingleSelection<JPanel> listPane;
	
	
	JPanel insidepane;	
	
	public GuiTunableInterceptor(JFrame inframe,JFrame outframe ,JPanel insidepane) {
		super( new GuiHandlerFactory<Guihandler>() );
		this.inframe = inframe;
		this.outframe = outframe;
		this.insidepane = insidepane;
	}


	protected void getInputPanes(List<Guihandler> list) {
			
			insidepane.removeAll();
			//insidepane.updateUI();
			this.list=list;
			//mainPanel = new JPanel();
			//mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));
			//insidepane.setLayout(new BoxLayout(insidepane,BoxLayout.PAGE_AXIS));
			
			TitledBorder titleBorder = null;

			java.util.List<JPanel> panes = new ArrayList<JPanel>();
			JPanel init = new JPanel();
			init.setName("init");
			panes.add(init);
			listPane = new ListSingleSelection<JPanel>(panes);
			String group=null;			

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
			

			insidepane.setLayout(new BoxLayout(insidepane,BoxLayout.PAGE_AXIS));
			
			for(int i=0;i<listPane.getPossibleValues().size();i++){
				insidepane.add(listPane.getPossibleValues().get(i));
			}
			
			System.out.println(insidepane.getComponentCount());
			
			inframe.pack();
			inframe.setLocation(50, 30);
			inframe.setVisible(true);
			processdone=true;
			
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