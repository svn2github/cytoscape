package fgraph.plugin;
 
import javax.swing.*;
import javax.swing.border.*;          
import java.awt.*;
import java.awt.event.*;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Iterator;

import java.util.logging.LogManager;

import fgraph.AlgorithmException;
import fgraph.MaxProduct;
import fgraph.InteractionGraph;
import fgraph.SubmodelOutputFiles;

import phoebe.PGraphView;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;

import cytoscape.view.CyNetworkView;

import giny.util.SpringEmbeddedLayouter;
import giny.view.NodeView;

//import cytoscape.CytoscapeInit;

public class RunDialog extends JDialog implements ActionListener
{
    private static final int VIEW_THRESHOLD = 500;
    
    private JFormattedTextField minKOField;
    private JFormattedTextField pathLenField;
    private JTextField sifField;
    private JTextField exprField;
    private JFormattedTextField exprThreshField;
    private JTextField edgeField;
    private JFormattedTextField edgeThreshField;
    private JTextField outDirField;
    private JTextField outFileField;

    private JButton run;
    private JButton cancel;
    private JButton help;
    
    public RunDialog(Frame parentFrame)
    {
        // create a modal dialog
        super(parentFrame, "Explain knockout data", true);

        System.out.println("logging props: "
                           + System.getProperty("java.util.logging.config.file"));

        System.out.println("logging class: "
                           + System.getProperty("java.util.logging.config.class"));

        System.out.println("logging formatter: "
                           + LogManager.getLogManager().getProperty("java.util.logging.ConsoleHandler.formatter"));
        
        Component components = createComponents();

        this.getRootPane().setDefaultButton(run);
        
        this.getContentPane().add(components);
    }
    
    private JComponent createComponents()
    {
        JPanel topPane = new JPanel(new GridBagLayout());
        JPanel networkPane = createBorderedPanel("Physical network files");
        JPanel exprPane = createBorderedPanel("Expression data file");
        JPanel outputPane = createBorderedPanel("Output");
        JPanel paramPane = createBorderedPanel("Run parameters");

        JPanel advancedPane = createBorderedPanel(new GridLayout(0, 1),
                                                  "Advanced Options");
        
        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.LEFT));

        advancedPane.add(networkPane);
        advancedPane.add(paramPane);

        
        GridBagConstraints c = new GridBagConstraints();
        GridBagConstraints lc = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        lc.gridx = 0;
        lc.gridy = 0;
        lc.ipadx = 8;
        lc.ipady = 0;
        topPane.add(createStepLabel("Step 1"), lc);
        
        c.gridx = 1;
        c.gridy = 0;
        topPane.add(exprPane, c);

        lc.gridx = 0;
        lc.gridy = 1;
        topPane.add(createStepLabel("Step 2"), lc);

        c.gridx = 1;
        c.gridy = 1;
        topPane.add(outputPane, c);

        lc.gridx = 0;
        lc.gridy = 2;
        topPane.add(createStepLabel(""), lc);

        c.gridx = 1;
        c.gridy = 2;
        topPane.add(advancedPane, c);

        lc.gridx = 0;
        lc.gridy = 3;
        topPane.add(createStepLabel("Step 3"), lc);

        c.gridx = 1;
        c.gridy = 3;
        topPane.add(buttonPane, c);

        pathLenField = createIntField(paramPane, "Max path length", 10, 5);
        minKOField = createIntField(paramPane, "Min knockout's per model",
                                    10, 2);
        sifField = createField(networkPane, "Physical network SIF file", 10, "fgtest.sif");
        exprField = createField(exprPane, "Expression data file", 10, "fg2.pvals");
        exprThreshField = createPvalField(exprPane, "Expression p-value cutoff",
                                          10, 0.01);
        edgeField = createField(networkPane, "Interaction probabilties file", 10, "fgtest.eda");
        edgeThreshField = createPvalField(networkPane, "Protein-DNA edge p-value cutoff",
                                          10, 0.05);
        outDirField = createField(outputPane, "Output directory", 10, "/tmp");
        outFileField = createField(outputPane, "Output file name", 10, "plugin_out");

        run = new JButton("Run");
        run.setMnemonic(KeyEvent.VK_R); 
        run.addActionListener(this);

        cancel = new JButton("Cancel");
        cancel.setMnemonic(KeyEvent.VK_C); 
        cancel.addActionListener(this);

        help = new JButton("Help");
        help.setMnemonic(KeyEvent.VK_H); 
        help.addActionListener(this);

        
        buttonPane.add(run);
        buttonPane.add(cancel);
        buttonPane.add(help);

        topPane.setBorder(BorderFactory.createEmptyBorder(10, //top
                                                          10, //left
                                                          10, //bottom
                                                          10 //right 
                                                          )
                       );
        return topPane;
    }


    private JPanel createBorderedPanel(String title)
    {
        return createBorderedPanel(null, title);
    }
    
    private JPanel createBorderedPanel(LayoutManager layout, String title)
    {
        JPanel pane;
        if(layout == null)
        {
            pane = new JPanel(new GridLayout(0, 2));
        }
        else
        {
            pane = new JPanel(layout);
        }

        Border etched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        Border empty = BorderFactory.createEmptyBorder(10, //top
                                                       10, //left
                                                       10, //bottom
                                                       10 //right 
                                                       );

        TitledBorder tb = BorderFactory.createTitledBorder(etched, title);
        tb.setTitleJustification(TitledBorder.LEFT);
        
        pane.setBorder(BorderFactory.createCompoundBorder(tb, empty));

        return pane;
    }

    private JLabel createStepLabel(String label)
    {
        JLabel l = new JLabel(label);
        Font big = l.getFont().deriveFont(Font.BOLD, 36.0f);
        l.setForeground(new Color(0x666666));

        l.setFont(big);

        return l;
    }
    
    private JTextField createField(JComponent container, String label,
                                   int cols)
    {
        return createField(container, label, cols, null);
    }
    
    private JTextField createField(JComponent container, String label,
                                   int cols, String defval)
    {
        JTextField f = (JTextField) container.add(new JTextField(cols));
        f.setText(defval);
        f.setHorizontalAlignment(JTextField.TRAILING);
        container.add(new JLabel(label, SwingConstants.LEFT));
        return f;
    }

    private JFormattedTextField createIntField(JComponent container,
                                               String label,
                                               int cols, int defval)
    {
        JFormattedTextField f = new JFormattedTextField();
        f.setValue(new Integer(defval));
        f.setColumns(cols);
        f.setHorizontalAlignment(JTextField.TRAILING);
        
        container.add(f);
        container.add(new JLabel(label, SwingConstants.LEFT));
        return f;
    }

    private JFormattedTextField createPvalField(JComponent container,
                                                String label,
                                                int cols, double defval)
    {
        JFormattedTextField f = new JFormattedTextField();
        f.setValue(new Double(defval));
        f.setColumns(cols);
        f.setHorizontalAlignment(JTextField.TRAILING);
        
        container.add(f);
        container.add(new JLabel(label, SwingConstants.LEFT));

        return f;
    }

    
    /**
     * Listens for event from the Run button.
     * When triggered, run the max product algorithm, load the networks
     * from files, and load the edge and node attributes.
     */
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();

        if(source == cancel)
        {
            this.hide();
        }
        else if(source == help)
        {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                          "Not yet implemented",
                                          "Help",
                                          JOptionPane.INFORMATION_MESSAGE);
            
        }
        else if(source == run)
        {
            try
            {
                SubmodelOutputFiles output = runMaxProduct();
                
                loadModels(output);
                loadAttributes(output);
            }
            catch(AlgorithmException ae)
            {
                System.err.println("AlgorithmExeption while runnnig max product");
                ae.printStackTrace();
            }
            catch(IOException ioe)
            {
                System.err.println("IOExeption while runnnig max product");
                ioe.printStackTrace();
                
            }
            this.hide();
        }
    }

    private SubmodelOutputFiles runMaxProduct()
        throws AlgorithmException, IOException
    {
        String sif = sifField.getText();
        int pathLen = ((Number) pathLenField.getValue()).intValue();
        int minKO = ((Number) minKOField.getValue()).intValue();
        
        String expr = exprField.getText();
        double exprThresh = ((Number) exprThreshField.getValue()).doubleValue();
        
        String edge = edgeField.getText();
        double edgeThresh = ((Number) edgeThreshField.getValue()).doubleValue();
        
        String outDir = outDirField.getText();
        String outFile = outFileField.getText();
        
        MaxProduct mp = new MaxProduct();
        
        mp.setInteractionFile(sif);
        
        mp.setMaxPathLength(pathLen);
        mp.setKOExplainCutoff(minKO);
        mp.setExpressionFile(expr, exprThresh);
        mp.setEdgeFile(edge, edgeThresh);
        mp.run();
        
        InteractionGraph ig = mp.getInteractionGraph();

        String fname = outDir + File.separator + outFile;
        
        System.out.println("Writing interaction graph sif file: " + fname);
        System.out.println("Filtering submodels that explain fewer than: "
                           + minKO
                           + " KO experiments");
        
        SubmodelOutputFiles output = ig.writeGraphAsSubmodels(fname, minKO);
        
        return output;
    }

    private void loadModels(SubmodelOutputFiles output)
    {
        List models = output.getModels();
        
        CyNetwork[] networks = new CyNetwork[models.size()];
        for(int x=0; x < models.size(); x++)
        {
            File f = (File) models.get(x);
            CyNetwork cn = Cytoscape.createNetworkFromFile(f.getAbsolutePath());
            networks[x] = cn;
            if(cn.getNodeCount() < VIEW_THRESHOLD)
            {
                // code borrowed from csplugins.mcode.MCODEResultsDialog.java
                PGraphView view = (PGraphView) Cytoscape.createNetworkView(cn);
                //CyNetworkView view = Cytoscape.createNetworkView(cn);

                //layout new complex and fit it to window
                //randomize node positions before layout so that they
                //don't all layout in a line
                //(so they don't fall into a local minimum for the
                //SpringEmbedder)
                //If the SpringEmbedder implementation changes,
                //this code may need to be removed
                NodeView nv;
                double width = view.getCanvas().getLayer().getGlobalFullBounds().getWidth();
                double height = view.getCanvas().getLayer().getGlobalFullBounds().getHeight();
                for (Iterator in = view.getNodeViewsIterator(); in.hasNext();)
                {
                    nv = (NodeView) in.next();
                    nv.setXPosition(width * Math.random());
                    //height is small for many default drawn graphs, thus +100
                    nv.setYPosition((height + 100) * Math.random());
                }
                SpringEmbeddedLayouter lay = new SpringEmbeddedLayouter(view);
                lay.doLayout();
                view.fitContent();
                //Cytoscape.createNetworkView(cn);
            }
        }
    }
    
    private void loadAttributes(SubmodelOutputFiles output)
    {
        File nodeType = output.getNodeType();
        File edgeDir = output.getEdgeDir();
        File edgeSign = output.getEdgeSign();

        String[] noa;
        if(nodeType != null && nodeType.canRead())
        {
            noa = new String[1];
            noa[0] = nodeType.getAbsolutePath();
            System.out.println("loading node attribute file: " + noa[0]);
        }
        else
        {
            noa = new String[0];
        }

        String[] eda;
        if(edgeDir != null && edgeSign != null
           && edgeDir.canRead() && edgeSign.canRead())
        {
            eda = new String[2];
            eda[0] = edgeDir.getAbsolutePath();
            eda[1] = edgeSign.getAbsolutePath();
            System.out.println("loading edge attribute files: " + eda[0]
                               + ", " + eda[1]);
        }
        else
        {
            eda = new String[0];
        }

        Cytoscape.loadAttributes( noa, eda);
        
        /*
        Cytoscape.loadAttributes( noa,
                                  eda,
                                  !CytoscapeInit.noCanonicalization(),
                                  Cytoscape.getBioDataServer(),
                                  CytoscapeInit.getDefaultSpeciesName() ) ;
        */
    }
}
