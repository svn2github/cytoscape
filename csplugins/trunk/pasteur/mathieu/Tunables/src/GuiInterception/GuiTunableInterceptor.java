package GuiInterception;


import javax.swing.*;
import javax.swing.border.TitledBorder;
import Tunable.Tunable.Param;
import Utils.ListSingleSelection;
import Utils.myButton;
import java.util.ArrayList;
import java.util.List;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;



public class GuiTunableInterceptor extends HiddenTunableInterceptor<Guihandler> {

	public JFrame inframe;
	public JFrame outframe;
	public JPanel insidepane;	
	

	public JPanel tunPane = null;

	myButton button;
	List<Guihandler> list;
	Guihandler guihandler;
	ListSingleSelection<JPanel> listInPane;
	ListSingleSelection<JPanel> listOutPane;
	
	
	public GuiTunableInterceptor(JFrame inframe,JFrame outframe ,JPanel insidepane) {
		super( new GuiHandlerFactory<Guihandler>() );
		this.inframe = inframe;
		this.outframe = outframe;
		this.insidepane = insidepane;
		this.insidepane.setLayout(new BoxLayout(insidepane,BoxLayout.PAGE_AXIS));

	}


	protected void getInputPanes(List<Guihandler> list) {
			this.list=list;
			java.util.List<JPanel> panes = new ArrayList<JPanel>();
			JPanel init = new JPanel();

			init.setName("init");
			panes.add(init);
			listInPane = new ListSingleSelection<JPanel>(panes);
			String group=null;			

			for(Guihandler guihandler : list){
				boolean exist=false;
				int nbpane=0;
				group = guihandler.getTunable().group();	
				for(int i=0;i<listInPane.getPossibleValues().size();i++){
						if(listInPane.getPossibleValues().get(i).getName().equals(group)){
							exist=true;
							nbpane=i;
						}
				}
				if(exist==true){
					if(guihandler.getTunable().orientation()==Param.Horizontal){
						listInPane.getPossibleValues().get(nbpane).setLayout(new BoxLayout(listInPane.getPossibleValues().get(nbpane),BoxLayout.LINE_AXIS));
						listInPane.getPossibleValues().get(nbpane).add(Box.createRigidArea(new Dimension(10, 0)));
						listInPane.getPossibleValues().get(nbpane).add(guihandler.getInputPanel());
					}
					else{
						listInPane.getPossibleValues().get(nbpane).setLayout(new BoxLayout(listInPane.getPossibleValues().get(nbpane),BoxLayout.PAGE_AXIS));
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
					panes.add(pane);
					listInPane=new ListSingleSelection<JPanel>(panes);		
				}
			}
			panes.remove(0);
			listInPane=new ListSingleSelection<JPanel>(panes);
	
			for(int i=0;i<listInPane.getPossibleValues().size();i++){
				insidepane.add(listInPane.getPossibleValues().get(i));
			}
	}
	

	
	
	protected void display(List<Guihandler> list) {
		this.list=list;
		JPanel resultpane = new JPanel();
		resultpane.setLayout(new BoxLayout(resultpane,BoxLayout.PAGE_AXIS));
		resultpane.removeAll();
		
		java.util.List<JPanel> panes = new ArrayList<JPanel>();
		JPanel init = new JPanel();
		init.setName("init");
		panes.add(init);
		listOutPane = new ListSingleSelection<JPanel>(panes);
		String group = null;
		
		for(Guihandler guihandler : list){
			boolean exist=false;
			int nbpane=0;
			group = guihandler.getTunable().group();
			for(int i=0;i<listOutPane.getPossibleValues().size();i++){
				if(listOutPane.getPossibleValues().get(i).getName().equals(group)){
					exist=true;
					nbpane=i;
				}
			}
			if(exist==true){
				listOutPane.getPossibleValues().get(nbpane).add(guihandler.getOutputPanel());
			}
			else{
				JPanel pane = new JPanel();
				pane.setLayout(new BoxLayout(pane,BoxLayout.PAGE_AXIS));
				pane.add(guihandler.getOutputPanel());

				TitledBorder titleBorder = BorderFactory.createTitledBorder(group);
				titleBorder.setTitleColor(Color.blue);
				titleBorder.setTitlePosition(TitledBorder.LEFT);
				titleBorder.setTitlePosition(TitledBorder.TOP);
				pane.setBorder(titleBorder);
				pane.setName(group);
				panes.add(pane);
				listOutPane=new ListSingleSelection<JPanel>(panes);		
			}
		}
		panes.remove(0);
		listOutPane=new ListSingleSelection<JPanel>(panes);
		
		for(int i=0;i<listOutPane.getPossibleValues().size();i++){
			resultpane.add(listOutPane.getPossibleValues().get(i));
		}
		outframe.setContentPane(resultpane);
	}
			


		
	protected void save(List<Guihandler> handlerlist){
		for(Guihandler guihandler : handlerlist)	guihandler.handle();
	}

	protected void addProps(List<Guihandler> handlerList){	
	}

	protected void processProps(List<Guihandler> handlerList){
	}
}