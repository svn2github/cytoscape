/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel;

import infovis.*;
import infovis.column.BooleanColumn;
import infovis.column.StringColumn;
import infovis.graph.DefaultGraph;
import infovis.graph.io.GraphReaderFactory;
import infovis.graph.io.HTMLGraphReader;
import infovis.graph.visualization.GraphVisualization;
import infovis.graph.visualization.MatrixVisualization;
import infovis.table.DefaultTable;
import infovis.table.io.TableReaderFactory;
import infovis.table.visualization.ScatterPlotVisualization;
import infovis.tree.DefaultTree;
import infovis.tree.io.TreeReaderFactory;
import infovis.tree.visualization.TreeVisualization;
import infovis.tree.visualization.TreemapVisualization;
import infovis.visualization.VisualizationFactory;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.apache.log4j.*;

//import agile2d.AgileJFrame;

/**
 * Component to create a visualization program as simply as possible.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.52 $
 */
public class MainFrameDecorator implements ActionListener {
    private static Logger       logger            = Logger.getLogger(MainFrameDecorator.class);
    protected JFrame            frame;
    protected JMenuBar          menuBar;
    protected JMenu             fileMenu;
    protected JMenu             viewMenu;
    protected JMenu             helpMenu;
    protected JFileChooser      fileChooser;
    protected JTextField        urlChooser;
    protected JDialog           urlDialog;
    protected JMenuItem         createImageItem;
    protected JFileChooser      createImageFileChooser;
    protected BoundedRangeModel scaleModel;
    protected JMenu             visualizationMenu;
    protected Object            table;
    protected Visualization     visualization     = null;
    protected JSplitPane        visualizationPanel;
    protected ControlPanel      controlPanel;
    protected JComponent        splashScreen;
    protected boolean           creatingWindow    = true;
    //boolean usingAgile = false;
    AbstractAction              fileOpenAction    = new DefaultAction(
                                                          "Open",
                                                          'O') {
                                                      public void actionPerformed(
                                                              ActionEvent e) {
                                                          chooseFile();
                                                      }
                                                  };
    AbstractAction              urlOpenAction     = new DefaultAction(
                                                          "Url Open",
                                                          'U') {
                                                      public void actionPerformed(
                                                              ActionEvent e) {
                                                          chooseURL();
                                                      }
                                                  };
    AbstractAction              quitAction        = new DefaultAction(
                                                          "Quit",
                                                          'Q') {
                                                      public void actionPerformed(
                                                              ActionEvent e) {
                                                          System.exit(0);
                                                      }
                                                  };
    AbstractAction              aboutAction       = new AbstractAction(
                                                          "About...") {
                                                      public void actionPerformed(
                                                              ActionEvent e) {
                                                          JOptionPane.showMessageDialog(
                                                                          null,
                                                                          "The Visualization Toolkit\n(C) Jean-Daniel Fekete and INRIA, France",
                                                                          "About the Visualization Toolkit",
                                                                          JOptionPane.INFORMATION_MESSAGE);
                                                      }
                                                  };
    //    AbstractAction toggleAgileAction =
    //        new AbstractAction("Toggle Agile2D") {
    //        public void actionPerformed(ActionEvent e) {
    //            usingAgile = !usingAgile;
    //        }
    //    };
    AbstractAction              createImageAction = new AbstractAction(
                                                          "Create Image") {
                                                      public void actionPerformed(
                                                              ActionEvent e) {
                                                          createImage();
                                                      }
                                                  };
    AbstractAction              editTableAction   = new AbstractAction(
                                                          "Edit Table") {
                                                      public void actionPerformed(
                                                              ActionEvent e) {
                                                          editTable();
                                                      }
                                                  };

    /**
     * Creates a new MainFrameDecorator object.
     * 
     * @param frame
     *            the JFrame to decorate.
     */
    public MainFrameDecorator(JFrame frame) {
        this.frame = frame;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        urlDialog = new JDialog(frame, "Open URL ...", true);
        urlChooser = new JTextField(30);
        urlChooser.setAutoscrolls(true);
        urlChooser.addActionListener(this);
        urlDialog.getContentPane().add(urlChooser);
        urlDialog.pack();
        fileChooser = new JFileChooser(".");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setDoubleBuffered(false);
        createImageFileChooser = new JFileChooser(".");
        createImageFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        createImageFileChooser.setDoubleBuffered(false);
        JSlider scaleSlider = new JSlider(JSlider.VERTICAL, 0, 1600, 100);
        scaleModel = scaleSlider.getModel();
        scaleSlider.setMajorTickSpacing(100);
        scaleSlider.setMinorTickSpacing(10);
        scaleSlider.setPaintTicks(true);
        scaleSlider.setPaintLabels(true);
        scaleSlider.setBorder(BorderFactory.createTitledBorder("Scale"));
        createImageFileChooser.setAccessory(scaleSlider);
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        createMenus();
        frame.getContentPane().add(createSplashScreen());
    }

    /**
     * Creates the menus.
     */
    protected void createMenus() {
        fileMenu = createFileMenu();
        menuBar.add(fileMenu);
        viewMenu = createViewMenu();
        menuBar.add(viewMenu);
        visualizationMenu = new JMenu("Visualization");
        visualizationMenu.setEnabled(false);
        menuBar.add(visualizationMenu);
        helpMenu = createHelpMenu();
        menuBar.add(helpMenu);
    }

    /**
     * Creates the file menu.
     * 
     * @return the file menu.
     */
    public JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");

        fileMenu.add(fileOpenAction);
        fileMenu.add(urlOpenAction);
        createImageItem = fileMenu.add(createImageAction);
        createImageItem.setEnabled(false);
        fileMenu.addSeparator();
        fileMenu.add(quitAction);
        return fileMenu;
    }

    public JMenu createViewMenu() {
        JMenu viewMenu = new JMenu("View");

        //JCheckBoxMenuItem cb = new JCheckBoxMenuItem(toggleAgileAction);
        //        cb.setArmed(usingAgile);
        //        viewMenu.add(cb);
        viewMenu.add(editTableAction);
        editTableAction.setEnabled(false);
        return viewMenu;
    }

    /**
     * Creates the help menu.
     * 
     * @return the help menu.
     */
    public JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");

        helpMenu.add(aboutAction);
        return helpMenu;
    }

    /**
     * Create the splash screen.
     * 
     * @return the splash screen.
     */
    public JComponent createSplashScreen() {
        splashScreen = new SplashPanel();
        return splashScreen;
    }

    /**
     * shows a file chooser and wait for an selection.
     */
    protected void chooseFile() {
        int ret = fileChooser.showOpenDialog(null);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String error = openFile(file);
            if (error != null) {
                JOptionPane.showMessageDialog(
                        null,
                        "Error",
                        "Couldn't read file " + file.getAbsoluteFile(),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    protected void chooseURL() {
        urlDialog.setVisible(true);
        String url = urlChooser.getText();
        if (url.length() != 0) {
            Graph graph = new DefaultGraph();
            HTMLGraphReader reader = new HTMLGraphReader(url, graph);
            reader.add(url);
            if (reader.load()) {
                visualization = createGraphVisualization(url, graph);
            }
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param file
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public String openFile(File file) {
        String name = file.getAbsolutePath();

        Graph graph = new DefaultGraph();
        boolean loaded = GraphReaderFactory.readGraph(name, graph);

        if (loaded) {
            visualization = createGraphVisualization(name, graph);
            return null;
        }

        Tree tree = new DefaultTree();
        loaded = TreeReaderFactory.readTree(name, tree);
        if (loaded) {
            visualization = createTreeVisualization(name, tree);
            return null;
        }

        Table table = new DefaultTable();
        loaded = TableReaderFactory.readTable(name, table);
        if (loaded) {
            visualization = createTableVisualization(name, table);
            return null;
        }

        return "invalid format";
    }

    /**
     * DOCUMENT ME!
     * 
     * @param t
     *            DOCUMENT ME!
     * @param index
     *            DOCUMENT ME!
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

    public static JSplitPane createScrollVisualization(ControlPanel cp) {
//        Visualization vis = cp.getVisualization();
//        InteractorFactory.installInteractor(vis);
//        VisualizationPanel vp = new VisualizationPanel(vis);
//        cp.setVisualizationPanel(vp);
//        JScrollPane scroll = new JScrollPane(vp);
//        if (vis.getRulerTable() != null) {
//            DefaultAxisVisualization column = new DefaultAxisVisualization(
//                    vis,
//                    Orientable.ORIENTATION_SOUTH);
//            InteractorFactory.installInteractor(column);
//            VisualLabel vl = VisualLabel.get(column);
//            vl.setOrientation(Orientable.ORIENTATION_SOUTH);
//            scroll.setColumnHeaderView(new VisualizationPanel(column));
//            DefaultAxisVisualization row = new DefaultAxisVisualization(
//                    vis,
//                    Orientable.ORIENTATION_WEST);
//            InteractorFactory.installInteractor(row);
//            row.setOrientation(Orientable.ORIENTATION_EAST);
//            scroll.setRowHeaderView(new VisualizationPanel(row));
//            
//            scroll.setCorner(
//                    JScrollPane.UPPER_LEFT_CORNER,
//                    new JPanner(scroll));
//        }
//        scroll.setDoubleBuffered(false);
//        scroll.setWheelScrollingEnabled(false);
//        JSplitPane split = new JSplitPane(
//                JSplitPane.HORIZONTAL_SPLIT,
//                scroll,
//                cp);
//        split.setResizeWeight(1.0);
//        return split;
        return ControlPanelFactory.createScrollVisualization(cp);
    }

    /**
     * Create the control panel associated with a specified visualization with a name
     * 
     * @param name
     *            The name (file name) of the visualization
     * @param visualization
     *            The Visualization 
     * 
     * @return the Visualization
     */
    public Visualization createControls(
            String name,
            final Visualization visualization) {
        final ControlPanel control = ControlPanelFactory
                .createControlPanel(visualization);

        if (splashScreen != null) {
            frame.getContentPane().remove(splashScreen);
            splashScreen = null;
        }
        if (visualization != null && !creatingWindow) {
            frame.getContentPane().remove(visualizationPanel);
            controlPanel.dispose();
            controlPanel = null;
        }
        else if (controlPanel != null && creatingWindow) {
            JFrame window = new JFrame(name);

            window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            window.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent e) {
                    control.dispose();
                }
            });
            JComponent visualizationPanel = createScrollVisualization(control);
            window.getContentPane().add(visualizationPanel);
            window.setVisible(true);
            window.pack();
            return visualization;
        }
        controlPanel = control;
        visualizationPanel = createScrollVisualization(control);
        frame.getContentPane().add(visualizationPanel);
        frame.setTitle("Infovis Toolkit: "+name);
        frame.validate();
        System.gc();
        System.runFinalization();
        VisualizationFactory.Creator[] creators = VisualizationFactory
                .getInstance().getCompatibleCreators(table);
        visualizationMenu.removeAll();
        for (int i = 0; i < creators.length; i++) {
            addVisualizationMenu(creators[i], i);
        }
        visualizationMenu.setEnabled(creators.length != 0);
        createImageItem.setEnabled(visualization != null);
        editTableAction.setEnabled(visualization != null);

        return visualization;
    }

    public void addVisualizationMenu(
            final VisualizationFactory.Creator creator,
            int i) {
        final String name = creator.getName();
        visualizationMenu.add(new DefaultAction(name, '1' + i) {
            public void actionPerformed(ActionEvent e) {
                Visualization visualization = creator.create(table);
                if (visualization != null)
                    createControls(name, visualization);
            }
        });
    }

    /**
     * DOCUMENT ME!
     * 
     * @param graph
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public GraphVisualization createGraphVisualization(String name, Graph graph) {
        table = graph;
        MatrixVisualization visualization = new MatrixVisualization(graph);
        createControls(name, visualization);
        return visualization;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param tree
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public TreeVisualization createTreeVisualization(String name, Tree tree) {
        table = tree;
        TreemapVisualization visualization = new TreemapVisualization(
                tree,
                null);
        createControls(name, visualization);
        return visualization;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param table
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Visualization createTableVisualization(String name, Table table) {
        this.table = table;
        Visualization visualization = new ScatterPlotVisualization(table);
        createControls(name, visualization);
        return visualization;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == urlChooser) {
            urlDialog.setVisible(false);
        }
    }

    public void createImage() {
        int ret = createImageFileChooser.showOpenDialog(null);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = createImageFileChooser.getSelectedFile();
            createImage(file, scaleModel.getValue() / 100.0f);
        }
    }

    public void createImage(File file, float scale) {
        Component comp = visualization.getComponent();
        while (comp != null && ! (comp instanceof JScrollPane)) {
            comp = comp.getParent();
        }
        if (comp == null) {
            logger.error("Visualization not in a scroll pane");
            return;
        }
        JScrollPane scroll = (JScrollPane) comp;
        Dimension savedSize = scroll.getSize();
        Rectangle savedBounds = scroll.getBounds();
        Rectangle bounds = new Rectangle(savedBounds);
        Dimension d = scroll.getPreferredSize();
        scroll.setSize(d);
        scroll.validate();
        
        bounds.x = 0;
        bounds.y = 0;
        bounds.width = (int) (d.width * scale);
        bounds.height = (int) (d.height * scale);
        BufferedImage image = null;
        try {
            image = new BufferedImage(
                    bounds.width,
                    bounds.height,
                    BufferedImage.TYPE_INT_RGB);
        }
        catch(OutOfMemoryError e) {
            logger.error("Out of memory creating image", e);
            JOptionPane.showMessageDialog(
                    null,
                    "Error",
                    "Out of memory creating image",
                    JOptionPane.ERROR_MESSAGE);
            //parent.add(comp);
            scroll.setSize(savedSize);
            scroll.validate();
            return;
        }
        Graphics2D g2d = (Graphics2D) image.getGraphics();
//        g2d.setColor(comp.getBackground());
//        g2d.fill(bounds);
//        visualization.paint(g2d, bounds);
        comp.paint(g2d);

        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            logger.error("Cannot write png file " + file, e);
        }
        image.flush();
        image = null;
        scroll.setSize(savedSize);
        scroll.validate();
    }

    public JComponent createJTable(Table table) {
        JTable jtable = new JTable(table);
        BooleanColumn sel = BooleanColumn.findColumn(
                table,
                Table.SELECTION_COLUMN);
        jtable.setSelectionModel(sel);
        jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane pane = new JScrollPane(
                jtable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return pane;
    }

    public void editTable() {
        if (table == null)
            return;
        JFrame window = new JFrame("Table");
        JComponent pane;
        if (table instanceof Table) {
            Table table = (Table) this.table;
            pane = createJTable(table);
        }
        else if (table instanceof Graph) {
            Graph graph = (Graph) table;
            Table vertices = graph.getVertexTable();
            Table edges = graph.getEdgeTable();

            JComponent v = createJTable(vertices);
            JComponent e = createJTable(edges);
            JTabbedPane tab = new JTabbedPane();
            tab.add("Vertices", v);
            tab.add("Edges", e);
            pane = tab;

        }
        else {
            logger.error("Unexpected table");
            return;
        }
        window.getContentPane().add(pane);
        //window.setSize(500, 500);
        window.pack();
        window.setVisible(true);
    }

    /**
     * Main program.
     * 
     * @param args
     *            args.
     */
    public static void main(String[] args) {
        File loggerConfig = new File("properties/log4j.properties");
        if (loggerConfig.exists()) {
            PropertyConfigurator.configure(loggerConfig.toString());
        }
        else {
            BasicConfigurator.configure();
        }
        JFrame frame = new JFrame("InfoVis Toolkit");
        new MainFrameDecorator(frame);
        frame.setSize(1024, 768);
        frame.setVisible(true);
    }
}
