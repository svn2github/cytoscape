package main.java.org.cytoscape.komal;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;

import cytoscape.plugin.CytoscapePlugin;

import cytoscape.data.writers.XGMMLWriter;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

import cytoscape.view.CyNetworkView;

import java.awt.event.ActionEvent;

import java.net.*;
import java.net.URL;
import java.io.*;
import java.io.File;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;

import java.net.URISyntaxException;

import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import javax.swing.*;

import java.lang.String;
import java.lang.Object;

import java.awt.event.ActionListener;

public class ExportToCWPlugin extends CytoscapePlugin {

    //Main class ExportToCytoscapeWeb extends the CytoscapePlugin class
    public static String titleofpage;   //Title input from user
    public static String descriptionofnetwork;  //Description input from user
    public static String urlofxgmml;    //URL of XGMML
    public static String exportlocation;    //export location
    public static String urloflogo = "img/cw.png";    //logo
    public static String what;
    public static String name; // XGMML file name
    public static String htmlname; // file name of web page to be saved
    public static String HtmlPath;

    //Constructor
    public ExportToCWPlugin() {
        MyPluginAction action = new MyPluginAction(this);
        Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) action);
    }

    protected boolean checkNetworkCount() {
        Set networks = Cytoscape.getNetworkSet();
        System.out.println(networks);

        if (networks.size() == 0) {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "No network in this session!",
                    "No network Error", JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else {
            return true;
        }
    }

    public class MyPluginAction extends CytoscapeAction {

        public MyPluginAction(ExportToCWPlugin myPlugin) {
            super("Network to Cytoscape Web");
            setPreferredMenu("File.Export");
        }

        public void actionPerformed(ActionEvent e) {

            final TextForm txtfrm = new TextForm();

        }

        public class TextForm extends JPanel {

            JFileChooser fc;

            public TextForm() {

                CyLogger.getLogger().info("Plugin GUI opened");

                JFrame frame = new JFrame("Export Current Network to Cytoscape Web");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setBounds(300, 200, 500, 300);
                frame.getContentPane().setLayout(null);

                JLabel location = new JLabel("Location:");
                frame.getContentPane().add(location);
                location.setBounds(20, 20, 80, 20);

                final JTextField urlField = new JTextField(" ");
                frame.getContentPane().add(urlField);
                urlField.setBounds(110, 20, 250, 20);

                JButton locationButton = new JButton("Choose");
                frame.getContentPane().add(locationButton);
                locationButton.setBounds(370, 20, 100, 20);

                locationButton.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent ev) {

                        if (checkNetworkCount() == false) {
                            return;
                        }
                        CyLogger.getLogger().info("Choosing location to save XGMML");
                        // Create FileFilters
                        CyFileFilter xgmmlFilter = new CyFileFilter();

                        // Add accepted File Extensions
                        xgmmlFilter.addExtension("xgmml");
                        xgmmlFilter.setDescription("XGMML files");


                        try {
                            name = FileUtil.getFile("Export Network and Attributes to Cytoscape Web", FileUtil.SAVE,
                                    new CyFileFilter[]{xgmmlFilter}).toString();
                        } catch (Exception exp) {
                            return;
                        }

                        urlField.setText(name);

                       // String thisstr = new File(name).toURI().toString();
                        String thisstr = name.replace("\\", "/");
                        CyLogger.getLogger().info("PathWithName:" + thisstr);
                        HtmlPath = thisstr + ".html";
                        CyLogger.getLogger().info("HtmlPath:" + HtmlPath);

                        htmlname = new File(name).getName();
                        CyLogger.getLogger().info("Name:" + htmlname);

                        exportlocation = new File(name).getParentFile().getPath();
                        CyLogger.getLogger().info("Path:" + exportlocation);

                        //    URI xuri = new File(name).toURI();
                        //   HtmlPath = "\"" + xuri.toString() + ".html" + "\"";
                        //   CyLogger.getLogger().info("HtmlPath:" + HtmlPath);

                        if (!name.endsWith(".xgmml")) {
                            name = name + ".xgmml";
                        }

                        String newstr = new File(name).toURI().toString();
                        urlofxgmml = newstr.replace("file:/", "file:///");

                        final CyNetwork network = Cytoscape.getCurrentNetwork();
                        final CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());

                        final ExportAsXGMMLTask task = new ExportAsXGMMLTask(name, network, view);

                        final JTaskConfig jTaskConfig = new JTaskConfig();
                        jTaskConfig.setOwner(Cytoscape.getDesktop());
                        jTaskConfig.displayCloseButton(true);
                        jTaskConfig.displayStatus(true);
                        jTaskConfig.setAutoDispose(false);

                        TaskManager.executeTask(task, jTaskConfig);

                    }
                });

                JCheckBox check = new JCheckBox("Show Advanced Options..");
                frame.getContentPane().add(check);
                check.setBounds(20, 50, 200, 20);

                JLabel title = new JLabel("Title of Web Page:");
                frame.getContentPane().add(title);
                title.setBounds(20, 80, 130, 20);

                final JTextField titleField = new JTextField(" ");
                frame.getContentPane().add(titleField);
                titleField.setBounds(150, 80, 300, 20);

                JLabel description = new JLabel("Network Description:");
                frame.getContentPane().add(description);
                description.setBounds(20, 110, 130, 20);

                final JTextArea descriptionField = new JTextArea(" ");
                frame.getContentPane().add(descriptionField);
                descriptionField.setBounds(150, 110, 300, 80);

                JLabel logo = new JLabel("Logo:");
                frame.getContentPane().add(logo);
                logo.setBounds(20, 200, 80, 20);

                final JTextField logoField = new JTextField(" ");
                frame.getContentPane().add(logoField);
                logoField.setBounds(150, 200, 190, 20);

                final JButton logoButton = new JButton("Choose..");
                frame.getContentPane().add(logoButton);
                logoButton.setBounds(370, 200, 100, 20);
                fc = new JFileChooser();

                logoButton.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent eve) {

                        CyLogger.getLogger().info("Choosing logo");

                        int returnVal = fc.showOpenDialog(TextForm.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            if (eve.getSource() == logoButton) {
                                File file = fc.getSelectedFile();
                                urloflogo = file.toString();
                                logoField.setText(file.toString());
                            }
                        }
                    }
                });

                JButton exportButton = new JButton("Export");
                frame.getContentPane().add(exportButton);
                exportButton.setBounds(190, 230, 100, 20);

                exportButton.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent ev) {

                        CyLogger.getLogger().info("Export Button Clicked");

                        titleofpage = titleField.getText();
                        descriptionofnetwork = descriptionField.getText();
                        //urlofxgmml = urlField.getText();
                        //urlofxgmml = urlofxgmml + ".xgmml";

                        ExportToCWPlugin p = new ExportToCWPlugin();

                        p.call();
                        CyLogger.getLogger().info("Calling function to call WebPage.java");
                    }
                });
                frame.setVisible(true);
            }
        }
    }

    public static void main(String[] args) {

        ExportToCWPlugin pebble = new ExportToCWPlugin();

    }

    public void call() {
        String why[] = {what};
        CopyDir.main(why);
        WebPage.main(why);
    }
}

class ExportAsXGMMLTask implements Task {

    public String fileName;
    private CyNetwork network;
    private CyNetworkView view;
    private TaskMonitor taskMonitor;

    public ExportAsXGMMLTask(String fileName, CyNetwork network, CyNetworkView view) {
        this.fileName = fileName;
        this.network = network;
        this.view = view;
    }

    public void run() {
        taskMonitor.setStatus("Exporting Network and Attributes...");
        taskMonitor.setPercentCompleted(-1);

        try {
            saveGraph();
        } catch (Exception e) {
            taskMonitor.setException(e, "Cannot export graph as XGMML.");
        }

        taskMonitor.setPercentCompleted(100);

        CyLogger.getLogger().info("Network and attributes are exported as an XGMML file: " + fileName);
    }

    public void halt() {
        // Task can not currently be halted.
    }

    public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    public String getTitle() {
        return new String("Exporting Network and Attributes");
    }

    private void saveGraph()
            throws IOException, JAXBException, URISyntaxException, XMLStreamException,
            FactoryConfigurationError {
        OutputStreamWriter fileWriter = null;

        try {
            fileWriter = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
            final XGMMLWriter writer = new XGMMLWriter(network, view);

            writer.write(fileWriter);
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }

        final Object[] ret_val = new Object[3];
        ret_val[0] = network;
        ret_val[1] = new File(fileName).toURI();
        ret_val[2] = new Integer(Cytoscape.FILE_XGMML);

        Cytoscape.firePropertyChange(Cytoscape.NETWORK_SAVED, null, ret_val);
    }
}
