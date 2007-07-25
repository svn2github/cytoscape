package infovis.example;

import infovis.Table;
import infovis.Visualization;
import infovis.column.StringColumn;
import infovis.panel.ControlPanel;
import infovis.panel.ControlPanelFactory;

import java.io.File;

import javax.swing.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

/**
 * Class ExampleRunner
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 */
public class ExampleRunner {
    //protected boolean useAgile;
    protected String name;
    protected String args[];
    protected int firstFile;
    
    
    public ExampleRunner(String args[], String name) {
        this.name = name;
        this.args = args;
        firstFile = skipOptions();

        File loggerConfig = new File("properties/log4j.properties");
        if (loggerConfig.exists()) {
            PropertyConfigurator.configure(loggerConfig.toString());
        } else {
            BasicConfigurator.configure();
        }        
    }

    public int skipOptions() {
        for (int i = 0; i < args.length; i++) {
            if (args[i].charAt(0) != '-') {
                return i;
            }
//            else if (args[i].charAt(1) == 'a'){
//                useAgile = true;
//            }
            else {
                System.err.println("use -a to use Agile2D");
            }
        }
        return 0;
    }
    
    public int fileCount() {
        return args.length - firstFile;
    }
    
    public String getArg(int index) {
        if (args.length <= (firstFile+index)) {
            JFileChooser fchooser = new JFileChooser(".");
            if (fchooser.showDialog(null, "Select a data file to visualize") !=
                JFileChooser.APPROVE_OPTION) {
                System.exit(1);
            }
            String file = fchooser.getSelectedFile().getAbsolutePath();
            String[] nargs = new String[firstFile+index+1];
            System.arraycopy(args, 0, nargs, 0, firstFile+index);
            args = nargs;
            nargs[firstFile+index] = file;
        }
        return args[firstFile+index];
    }
    
    public ControlPanel createFrame(Visualization visualization) {
        JFrame frame;
        frame = new JFrame(name);
        
        ControlPanel control = create(frame, visualization);
        frame.setVisible(true);
        frame.pack();
        return control;
    }
    
    /**
     * Create a the visualization control panel in a specified JFrame.
     *
     * @param frame the JFrame
     * @param t the Tree.
     */
    public ControlPanel create(JFrame frame, Visualization visualization) {
        ControlPanel control = ControlPanelFactory.createControlPanel(visualization);
        if (control == null) {
            return control;
        }
        JSplitPane split = ControlPanelFactory.createScrollVisualization(control);
        split.setResizeWeight(1.0);
        frame.getContentPane().add(split);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return control;
    }

    /**
     * DOCUMENT ME!
     *
     * @param t DOCUMENT ME!
     * @param index DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static StringColumn getStringColumn(Table t, int index) {
        StringColumn ret = null;
        for (int i = 0; i < t.getColumnCount(); i++) {
            ret = StringColumn.getColumn(t, i);
            if (ret != null && !ret.isInternal() && index-- == 0)
                return ret;
        }
        return null;
    }
}
