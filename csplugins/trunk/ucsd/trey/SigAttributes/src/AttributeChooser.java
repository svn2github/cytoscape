package ucsd.trey.SigAttributes;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class AttributeChooser extends JDialog implements ActionListener{

    Component parent;
    String [] attributes;
    String [] annotations;
    JTabbedPane tabbedPane;
    String chosenAttribute;
    String [] chosenNameAttrs;
    double chosenCutoff;
    int maxNumber;
    JComboBox attributeSelector;
    JComboBox annotationSelector;
    JList nameSelector;
    JTextField cutoffField;
    JTextField maxField;
    double DEFAULT_PVALUE = 0.001;
    int    DEFAULT_MAX    = 3;

    public AttributeChooser(String [] attributes, String [] annotations, Component parent){
	setTitle ("Significant Functions");
	this.parent = parent;
	this.attributes = attributes;
	this.annotations = annotations;
	JPanel mainPanel = new JPanel ();
	mainPanel.setLayout(new BorderLayout());

	// set up instructions
	JLabel instructions = new JLabel("Finds significant functions for selected nodes",
					 JLabel.LEFT);	
	
	// set up tabbed pane
	tabbedPane = new JTabbedPane();
	tabbedPane.addTab("By Attribute", makeAttributePanel(attributes) );
	tabbedPane.addTab("By Annotation", makeAnnotationPanel(annotations) );

	// set up cutoffs and protein names chooser
	JPanel cutoffPane = new JPanel(new GridLayout(3,2));
	cutoffField = new JTextField(Double.toString(DEFAULT_PVALUE));
	maxField = new JTextField(Integer.toString(DEFAULT_MAX));
	JLabel cutoffLabel = new JLabel("Pvalue Cutoff", JLabel.LEFT);
	JLabel maxLabel = new JLabel("Max # Attributes", JLabel.LEFT);
	JLabel nameLabel = new JLabel("Attribute(s)for Name Lookup  ");
	nameSelector = new JList(attributes);
	JScrollPane scrollPane = new JScrollPane(nameSelector, 
						 JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
						 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	nameSelector.setVisibleRowCount(0);
	cutoffPane.add(cutoffLabel);
	cutoffPane.add(cutoffField);
	cutoffPane.add(maxLabel);
	cutoffPane.add(maxField);
	cutoffPane.add(nameLabel);
	cutoffPane.add(scrollPane);

	// set up button
	JPanel buttonPane = new JPanel();
	buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
	buttonPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
	buttonPane.add(cutoffPane);
	buttonPane.add(Box.createHorizontalGlue());
	buttonPane.add(Box.createRigidArea(new Dimension(100,0)));
	JButton ok = new JButton("OK");
	ok.addActionListener(this);
	buttonPane.add(ok);

	// put everything together inside main panel
	mainPanel.add(instructions, BorderLayout.NORTH);
	mainPanel.add(tabbedPane, BorderLayout.CENTER);	
	mainPanel.add(buttonPane, BorderLayout.PAGE_END);
	setContentPane(mainPanel);
    }

    public void showDialog(){
	this.pack();
	this.setLocationRelativeTo(parent);
	this.setVisible(true);

	try{
	    synchronized (this){
		this.wait();
	    }
	}catch(Exception e){
	    e.printStackTrace();
	    System.exit(-1);
	}
    }
    
    public String getAttribute(){
	return chosenAttribute;
    }

    public String [] getNameAttribute() {
	return chosenNameAttrs;
    }
    
    public double getCutoff() {
	return chosenCutoff;
    }

    public int getMaxNumber() {
	return maxNumber;
    }

    public boolean useAttributes() {
	if (tabbedPane.getSelectedIndex() == 0) return true;
	else return false;
    }

    public boolean useAnnotations() {
	if (tabbedPane.getSelectedIndex() == 1) return true;
	else return false;
    }

    public void actionPerformed(ActionEvent e){
	
	if (tabbedPane.getSelectedIndex() == 0)  // attributes used
	    chosenAttribute = (String)attributeSelector.getSelectedItem();
	else                                     // annotations used
	    chosenAttribute = (String)annotationSelector.getSelectedItem();

	chosenCutoff    =  Double.parseDouble(cutoffField.getText());
	maxNumber       =  Integer.parseInt(maxField.getText());
	int [] indices  =  nameSelector.getSelectedIndices();
	chosenNameAttrs = new String [indices.length];
	for (int i=0; i<indices.length; i++) chosenNameAttrs[i] = attributes[indices[i]];
	synchronized (this){
	    notify();
	}
	this.dispose();
    }

    private JPanel makeAttributePanel (String [] attributes) {
	JPanel attributePanel = new JPanel();
	attributePanel.setLayout(new BoxLayout(attributePanel, BoxLayout.PAGE_AXIS));
	attributePanel.setBorder(BorderFactory.createEmptyBorder(0,100,0,100));
	attributePanel.setBackground(Color.LIGHT_GRAY);
	JLabel attributeLabel = new JLabel("Select Node Attribute:");
	attributeSelector = new JComboBox(attributes);
	attributePanel.add(attributeLabel);
	attributePanel.add(attributeSelector);
	attributePanel.add(Box.createVerticalGlue());
	//attributePanel.add(Box.createRigidArea(new Dimension(0,30)));


	return attributePanel;
    }

    private JPanel makeAnnotationPanel (String [] annotations) {
	JPanel annotationPanel = new JPanel();
	annotationPanel.setLayout(new BoxLayout(annotationPanel, BoxLayout.PAGE_AXIS));
	annotationPanel.setBorder(BorderFactory.createEmptyBorder(0,100,0,100));
	annotationPanel.setBackground(Color.LIGHT_GRAY);
	JLabel annotationLabel = new JLabel("Select Annotation:");
	annotationSelector = new JComboBox(annotations);
	annotationPanel.add(annotationLabel);
	annotationPanel.add(annotationSelector);
	annotationPanel.add(Box.createVerticalGlue());
	//annotationPanel.add(Box.createRigidArea(new Dimension(0,30)));


	return annotationPanel;
    }

}
