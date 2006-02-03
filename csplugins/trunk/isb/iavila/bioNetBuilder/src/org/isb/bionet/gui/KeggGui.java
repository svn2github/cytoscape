/**
 * KeggGui.java
 */

package org.isb.bionet.gui;

import java.util.*;

import org.exolab.castor.jdo.conf.DataSource;
import org.isb.bionet.datasource.interactions.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.event.*;

/**
 * A modal dialog that displays KEGG parameters.
 * 
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 * 
 */
public class KeggGui extends JDialog implements InteractionsSourceGui {
    
    /**
     * The title of this JFrame
     */
    public static final String TITLE = "KEGG Settings";

    /**
     * The default threshold
     */
    protected JSlider thSlider;
    protected JRadioButton oneEdgeOption;
    
    protected InteractionDataClient interactionClient;
    
    /**
     * Constructor, sets title to TITLE 
     *
     * @param Hashtable from String to String
     * @param default_th the default threshold
     */
    public KeggGui (InteractionDataClient interaction_client, int default_th){
        this.interactionClient = interaction_client;
        setTitle(TITLE);
        setModal(true);
        createGUI(default_th);
    }//ProlinksGui
    
    /**
     * Gets a Hashtable of (key, value) entries that a Kegg interactions handler understands
     * @return a Hashtable
     * @see org.isb.bionet.datasource.interactions.KeggInteractionsSource
     */
    public Hashtable getArgsTable (){
        int threshold = getThreshold();
        boolean oneEdge = createOneEdgePerCompound();
        Hashtable args = new Hashtable();
        args.put(KeggInteractionsSource.THRESHOLD_KEY,new Integer(threshold));
        args.put(KeggInteractionsSource.EDGE_PER_CPD_KEY, new Boolean(oneEdge));
        return args;
    }
    
   
    /**
     * @return the slider threshold
     */
    public int getThreshold (){
        int value = this.thSlider.getValue();
        return value;
    }//getPval
    
    /**
     * @return whether one edge per shared compound between genes should be created
     */
    public boolean createOneEdgePerCompound (){
        return this.oneEdgeOption.isSelected();
    }//createOneEdgePerCompound
    
    /**
     * Creates the JFrame with Prolinks parameters
     */
    protected void createGUI (int default_th){
        JPanel thPanel = createThresholdPanel(default_th);
        JPanel edgeOptionPanel = createEdgeOptionPanel();
        JPanel buttons = createButtonsPanel();
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
        panel2.setBorder(BorderFactory.createEtchedBorder());
        
        panel2.add(thPanel);
        panel2.add(edgeOptionPanel);
        
        panel.add(panel2);
        panel.add(buttons);
        
        setContentPane(panel);
        
    }//createGUI
    
    
    /**
     * 
     * @return
     */
    protected JPanel createThresholdPanel (int default_th){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        
        JLabel thLabel = new JLabel("Select threshold for compound-gene interactions:");
        JPanel thLabelP = new JPanel();
        thLabelP.add(thLabel);
        panel.add(thLabelP);
        
        this.thSlider = new JSlider();
        Integer maxTh = null;
        final String keggClass = KeggInteractionsSource.class.getName();
        try{
            maxTh = (Integer)this.interactionClient.callSourceMethod(keggClass,"getMaxNumCompoundInteractions",new Vector());
        }catch(Exception ex){
            ex.printStackTrace();
            maxTh = new Integer(0);
        }
        this.thSlider.setMaximum(maxTh.intValue());
        this.thSlider.setMajorTickSpacing(50);
        this.thSlider.setSnapToTicks(true);
        this.thSlider.setPaintTicks(true);
        Dictionary labels = new Hashtable();
        labels.put(new Integer(0),new JLabel("0"));
        labels.put(maxTh,new JLabel(Integer.toString(maxTh.intValue())));
        
        this.thSlider.setLabelTable(labels);
        this.thSlider.setPaintLabels(true);
        panel.add(this.thSlider);
        
        final JLabel sliderLabel = new JLabel(Integer.toString(default_th));
        JPanel labelPanel = new JPanel();
        labelPanel.add(sliderLabel);
        panel.add(labelPanel);
        this.thSlider.addChangeListener(
        
                   new ChangeListener (){
                       public void stateChanged (ChangeEvent e){
                           JSlider source = (JSlider)e.getSource();
                           if (!source.getValueIsAdjusting()) {  
                               Hashtable cpds = new Hashtable();
                               try{
                                   Vector args = new Vector();
                                   args.add(new Integer(source.getValue()));
                                   args.add(new Integer(5));
                                   cpds = 
                                       (Hashtable)interactionClient.callSourceMethod(
                                               keggClass,
                                               "getCompoundsWithScoreClosestTo",
                                               args);
                               }catch(Exception ex){ ex.printStackTrace();}
                               Iterator it = cpds.values().iterator();
                               String compoundString = "";
                               while(it.hasNext()) {
                                   if(compoundString.length() > 0)
                                       compoundString += "<br>"+(String)it.next();
                                   else
                                       compoundString = (String)it.next();
                               }
                               if(compoundString.length() == 0){
                                   sliderLabel.setText(Integer.toString((int)source.getValue()));   
                               }else{
                                   sliderLabel.setText("<html><B>Threshold = </B>"+Integer.toString((int)source.getValue()) + "<br><B>Top 5 compounds:</B><br>" + compoundString + "</html>");
                               }
                               
                           }
                       }//stateChanged
                   }//ChangeListener
        );
        this.thSlider.setValue(default_th);
        
        return panel;
       
    }//createInteractionsPanel
    
    
    /**
     * 
     * @return
     */
    protected JPanel createEdgeOptionPanel (){
        
        JPanel panel = new JPanel();
        this.oneEdgeOption = new JRadioButton("<html>Create one edge per shared compound between genes<br>(adds large number of multiple edges between genes).</html>");
        this.oneEdgeOption.setSelected(false);
        panel.add(this.oneEdgeOption);
     
        return panel;
    }//createPvalPanel
    
    protected JPanel createButtonsPanel (){
        JButton OK = new JButton("OK");
        OK.addActionListener(new AbstractAction (){
           
            public void actionPerformed (ActionEvent e){
                KeggGui.this.dispose();
            }//actionPerformed
            
        });
        JPanel panel = new JPanel();
        panel.add(OK);
        
        JButton help = new JButton("Help");
        help.addActionListener(
                new AbstractAction (){
                    
                    public void actionPerformed (ActionEvent e){
                        JOptionPane.showMessageDialog(KeggGui.this,"HELP GOES HERE","Help",JOptionPane.INFORMATION_MESSAGE);
                    }//actionPerformed
                }
        );
        
        panel.add(help);
        return panel;
    }//createButtonsPanel
}//ProlinksGui