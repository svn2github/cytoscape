package fgraph.plugin;
 
import javax.swing.*;          
import java.awt.*;
import java.awt.event.*;

import java.util.logging.LogManager;

import fgraph.MaxProduct;

public class RunDialog extends JDialog implements ActionListener
{
    private JLabel label;
    private static String labelPrefix = "numClicks: ";
    private int numClicks;

    private JFormattedTextField minKOField;
    private JFormattedTextField pathLenField;
    private JTextField sifField;
    private JTextField exprField;
    private JFormattedTextField exprThreshField;
    private JTextField edgeField;
    private JFormattedTextField edgeThreshField;
    private JTextField outDirField;
    private JTextField outFileField;
    
    public RunDialog(Frame parentFrame)
    {
        // create a modal dialog
        super(parentFrame, "Run", true);

        numClicks = 0;

        System.out.println("logging props: "
                           + System.getProperty("java.util.logging.config.file"));

        System.out.println("logging class: "
                           + System.getProperty("java.util.logging.config.class"));
        

        System.out.println("logging formatter: "
                           + LogManager.getLogManager().getProperty("java.util.logging.ConsoleHandler.formatter"));
        
        Component components = createComponents();
        
        this.getContentPane().add(components);
    }

    private JComponent createComponents()
    {
        JPanel pane = new JPanel(new GridLayout(0, 2));

        pathLenField = createIntField(pane, "Max Path Length", 10, 5);
        minKOField = createIntField(pane, "Min Num of KO per model", 10, 2);
        sifField = createField(pane, "Network SIF file", 10, "fgtest.sif");
        exprField = createField(pane, "Expression Data File", 10, "fg2.pvals");
        exprThreshField = createPvalField(pane, "Expression p-value cutoff",
                                          10, 0.01);
        edgeField = createField(pane, "Edge Data File", 10, "fgtest.eda");
        edgeThreshField = createPvalField(pane, "Protein-DNA p-value cutoff",
                                          10, 0.05);
        outDirField = createField(pane, "Output directory", 10, "/tmp");
        outFileField = createField(pane, "Base output file name", 10, "plugin_out");

        JButton button = new JButton("Run");
        button.setMnemonic(KeyEvent.VK_I); 
        button.addActionListener(this);

        label = new JLabel(labelPrefix + numClicks);
        label.setLabelFor(button);


        pane.add(button);
        pane.add(label);
        pane.setBorder(BorderFactory.createEmptyBorder(30, //top
                                                       30, //left
                                                       10, //bottom
                                                       30 //right 
                                                       )
                       );
        return pane;
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

    private JFormattedTextField createIntField(JComponent container, String label,
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

    private JFormattedTextField createPvalField(JComponent container, String label,
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

    
    
    public void actionPerformed(ActionEvent e)
    {
        numClicks++;
        label.setText(labelPrefix + numClicks);

        try
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
            mp.run(outDir, outFile);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
