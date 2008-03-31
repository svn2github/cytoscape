package de.layclust.layout.acc;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import de.layclust.layout.data.ConnectedComponent;
import de.layclust.layout.data.CostMatrixReader;
import de.layclust.layout.geometric_clustering.SingleLinkageClusterer;
import de.layclust.taskmanaging.InvalidTypeException;

public class GUI extends JFrame implements ActionListener {

	private JPanel results;
	private JTextArea result;
	private JComboBox antSelection;
	private JTextField noOfIterations;
	private JTextField file;
	private JPanel playground;
	private ConnectedComponent cc;
	private Playground pg;
	private AntPanel plot;
	private JCheckBox spread;
	private JTextField spreadIterations;
	
	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Load Matrix-File")) {
			CostMatrixReader reader = new CostMatrixReader(new File(file.getText()));
			try {
				cc = reader.getConnectedComponent();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			pg = null;
			System.out.println("Matrix-file loaded!");
		}
		if (e.getActionCommand().equals("Browse for matrix-file")) {
//			plotpanel.setColorValues(-0.75,-0.1);
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Browse for matrix-file");
			chooser.setCurrentDirectory(new File("/homes/nkleinbo/workspace/ACC/de/layclust/data/cm/"));
			chooser.setFileFilter(new FileFilter() {
				public boolean accept(File f) {
					return f.getName().toLowerCase().endsWith(".cm")
							|| f.isDirectory();
				}

				public String getDescription() {
					return "Matrix-File(*.cm)";
				}
			});
			int returnVal = chooser.showOpenDialog(rootPane);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		}
		if (e.getActionCommand().equals("Start Clustering")) {
			if (cc != null) {
				if(pg == null) {
					pg = new Playground(cc);
				} else {
					pg.reset();
				}
				pg.setAntType((String)antSelection.getSelectedItem()); 
				pg.setNoOfIterations(Integer.parseInt(noOfIterations.getText()));
				pg.setSpreadMode(spread.isSelected());
				if (pg.isSpreadMode()) {
					pg.setSpreadIterations(Integer.parseInt(spreadIterations.getText()));
				}
				//System.out.println("Ant-Type: "+pg.getAntType()+" / Iterations: "+pg.getNoOfIterations());
				pg.runInPanel(playground, 500, this);

			} else {
				System.out.println("No Matrix-File loaded.");
			}
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new GUI();
			}
		});
	}
	
	public GUI() {
		setSize(1000, 1200);
		setLocation(
				(Toolkit.getDefaultToolkit().getScreenSize().width - getWidth()) / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height - getHeight()) / 2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		initMenu();
		initContent();
	}
	
	public void initMenu() {
		
	}
	
	public void initContent() {
		// building tabs
		getContentPane().setLayout(new BorderLayout());
		JTabbedPane tabbedPane = new JTabbedPane();
		JPanel matrix = new JPanel();
		
		//Matrix-pane:
		matrix.setLayout(new BorderLayout());
		JPanel file_choosing = new JPanel();
		file = new JTextField("/homes/nkleinbo/workspace/ACC/de/layclust/data/cm/cost_matrix_component_nr_1003_size_45_cutoff_20.0.cm");
		JButton load = new JButton("Load Matrix-File");
		load.addActionListener(this);
		JButton browse = new JButton("Browse for matrix-file");
		browse.addActionListener(this);
		file_choosing.add(file);
		file_choosing.add(browse);
		file_choosing.add(load);
		matrix.add(file_choosing, BorderLayout.NORTH);
		tabbedPane.addTab("Matrix", matrix);
		
		playground = new JPanel();
		playground.setLayout(new BorderLayout());
		JPanel playgroundMenu = new JPanel();
		playgroundMenu.setLayout(new GridLayout(4,2));
		playgroundMenu.add(new JLabel("Number of Iterations: "));
		noOfIterations = new JTextField("100000");
		playgroundMenu.add(noOfIterations);
		playgroundMenu.add(new JLabel("Ant-Type: "));
		antSelection = new JComboBox(new Object[] {"SimpleAnt", "JumpingAnt", "JumpingAntWithIncreasingViewField","MemoryAnt"});
		playgroundMenu.add(antSelection);
		spread = new JCheckBox("Spread-Mode", false);
		spreadIterations = new JTextField("50000");
		playgroundMenu.add(spread);
		playgroundMenu.add(spreadIterations);
		JButton start = new JButton("Start Clustering");
		start.addActionListener(this);
		playgroundMenu.add(start);
		playground.add(playgroundMenu, BorderLayout.NORTH);
		tabbedPane.addTab("Playground", playground);
		
		results = new JPanel();
		result = new JTextArea();
		result.setEditable(false);
		results.add(result, BorderLayout.CENTER);
		tabbedPane.addTab("Results", results);
		
		
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		
	}
	
	public void setResultText(String res) {
		result.setText(res);
	}

}
