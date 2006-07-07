package NetworkBLAST;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.WindowConstants;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.GridLayout;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;

import NetworkBLAST.panels.AboutPanel;
import NetworkBLAST.panels.CompatGraphPanel;
import NetworkBLAST.panels.PathSearchPanel;
import NetworkBLAST.panels.ComplexSearchPanel;
import NetworkBLAST.panels.ScoreModelPanel;

public class NetworkBLASTDialog extends JDialog {

  public NetworkBLASTDialog(Frame _parent)
  {
    super(_parent, true);
    initialize();
  }

  private void initialize()
  {
    javax.swing.JPanel contentPane	= new JPanel();
    AboutPanel aboutPanel		= new AboutPanel();
    ScoreModelPanel scoreModelPanel	= new ScoreModelPanel(this);
    
    tabbedPane				= new JTabbedPane();
    compatGraphPanel			= new CompatGraphPanel(this);
    pathSearchPanel			= new PathSearchPanel(this);
    complexSearchPanel			= new ComplexSearchPanel(this);

    tabbedPane.addTab("About", null, aboutPanel, null);
    tabbedPane.addTab("Compatibility Graph", null, compatGraphPanel, null);
    tabbedPane.addTab("Path Search", null, pathSearchPanel, null);
    tabbedPane.addTab("Complex Search", null, complexSearchPanel, null);
    tabbedPane.addTab("Score Model", null, scoreModelPanel, null);
    
    this.setBounds(new Rectangle(0,0,462,254));
    this.setContentPane(contentPane);
    this.setTitle("NetworkBLAST");
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

    contentPane.setLayout(new GridLayout());
    contentPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    contentPane.add(tabbedPane);

    this.pack();
  }

  public void switchToTab(int _index)
  {
    tabbedPane.setSelectedIndex(_index);
  }

  public void setup()
  {
    compatGraphPanel.getGraph1ComboBox().setup();
    compatGraphPanel.getGraph2ComboBox().setup();
    compatGraphPanel.getHomgraphComboBox().setup();
    pathSearchPanel.getGraphComboBox().setup();
    complexSearchPanel.getGraphComboBox().setup();
  }

  public CompatGraphPanel getCompatGraphPanel()
  {
    return compatGraphPanel;
  }
  
  public PathSearchPanel getPathSearchPanel()
  {
    return pathSearchPanel;
  }
  
  public ComplexSearchPanel getComplexSearchPanel()
  {
    return complexSearchPanel;
  }
  private javax.swing.JTabbedPane	tabbedPane;
  private CompatGraphPanel		compatGraphPanel;
  private PathSearchPanel		pathSearchPanel;
  private ComplexSearchPanel		complexSearchPanel;
}
