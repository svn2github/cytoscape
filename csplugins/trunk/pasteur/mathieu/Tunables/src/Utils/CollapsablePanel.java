package Utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JToggleButton;


public class CollapsablePanel extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private JToggleButton myExpandButton = null;
	private boolean expandPaneVisible=false;
	private static String ExpandName = "Expand>>";
	private static String CollapseName = "<<Collapse";
	private JPanel rightPanel = new JPanel();
	private JPanel leftPanel = new JPanel();
	ListSingleSelection<JPanel> listInPane;
	
	
	public CollapsablePanel(ListSingleSelection<JPanel> list){
		this.listInPane = list;
		setLayout(new BorderLayout());
		add(rightPanel,BorderLayout.EAST);
		rightPanel.add(myExpandButton = createButton(ExpandName));
		leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.LINE_AXIS));
		add(leftPanel,BorderLayout.WEST);
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
	
	
	
	
	private void collapsePanel(){
		leftPanel.removeAll();
	}
		
		
	private void expandPanel(){
			for(int i=0;i<listInPane.getPossibleValues().size();i++) leftPanel.add(listInPane.getPossibleValues().get(i));
		}
		
	
}