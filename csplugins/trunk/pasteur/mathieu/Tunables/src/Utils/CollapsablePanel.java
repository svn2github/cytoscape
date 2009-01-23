package Utils;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;


public class CollapsablePanel extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private JToggleButton myExpandButton = null;
	private boolean expandPaneVisible;
	private static String ExpandName = "Expand>>";
	private static String CollapseName = "<<Collapse";
	private JPanel rightPanel = new JPanel();
	private JPanel leftPanel = new JPanel();
	
	ListSingleSelection<JPanel> listInPane;
	public JFrame frame;
	Dimension initPaneSize;
	
	
	public CollapsablePanel(ListSingleSelection<JPanel> list,JFrame frame){
		this.listInPane = list;
		this.frame=frame;
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		leftPanel.setLayout(new GridLayout());
		add(leftPanel);
		leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.PAGE_AXIS));
		add(rightPanel);
		rightPanel.add(myExpandButton = createButton(ExpandName));
		initPaneSize = getPreferredSize();
//		this.expandPaneVisible=!collapse;
		//setCollapsed(expandPaneVisible);
	}

	
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == myExpandButton){
			if(expandPaneVisible){
				collapsePanel();
				myExpandButton.setText (ExpandName);
				expandPaneVisible = false;
			}
			else{
				expandPanel();
				myExpandButton.setText (CollapseName);
				expandPaneVisible = true;
			}
		}
	}

	
	private JToggleButton createButton(String name){
		JToggleButton button = new JToggleButton(name);		
		button.setPreferredSize (new Dimension (90, 20));
		button.setMargin (new Insets (2, 2, 2, 2));
		button.addActionListener(this);
		return button;
	}
	
	
	public void setCollapsed(boolean collapsed){
		if(collapsed){
			myExpandButton.setSelected(true);
			expandPanel();
			myExpandButton.setText(CollapseName);
			System.out.println("panel affiche");
		}
		else {
			myExpandButton.setSelected(false);
			collapsePanel();
			myExpandButton.setText(ExpandName);
			System.out.println("panel collapse");
		}
	}
	
	public void setButtonChanges(boolean value){
		myExpandButton.setSelected(value);
		if(value)myExpandButton.setText(CollapseName);
		else myExpandButton.setText(ExpandName);
		
	}
	
	
	
	public boolean isCollapsed(){
		return expandPaneVisible;
	}
	
	private void collapsePanel(){
		leftPanel.removeAll();
		resize(initPaneSize);
//		repaint();
//		frame.resize(initFrameSize);
		frame.pack();
	}
		
		
	private void expandPanel(){
			for(int i=0;i<listInPane.getPossibleValues().size();i++)	leftPanel.add(listInPane.getPossibleValues().get(i));
			this.repaint();
			frame.pack();
		}
}