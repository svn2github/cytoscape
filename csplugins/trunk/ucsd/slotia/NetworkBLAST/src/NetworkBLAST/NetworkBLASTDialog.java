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
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowEvent;

import cytoscape.Cytoscape;

import NetworkBLAST.panels.AboutPanel;
import NetworkBLAST.panels.CompatGraphPanel;
import NetworkBLAST.panels.PathSearchPanel;
import NetworkBLAST.panels.ComplexSearchPanel;
import NetworkBLAST.panels.ScoreModelPanel;

public class NetworkBLASTDialog extends JDialog {

  public NetworkBLASTDialog(Frame _parent)
  {
    super(_parent, false);
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

    WindowFocusListener focusListener = new WindowFocusListener()
    {
      public void windowGainedFocus(WindowEvent e)
        { setup(); }

      public void windowLostFocus(WindowEvent e)
        { }
    };

    this.addWindowFocusListener(focusListener);
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

    if (Cytoscape.getNetworkSet().size() == 0)
    {
      compatGraphPanel.getGenerateButton().setEnabled(false);
      pathSearchPanel.getSearchButton().setEnabled(false);
      complexSearchPanel.getSearchButton().setEnabled(false);

      compatGraphPanel.getGenerateButton().setToolTipText("There are no "
      	+ "networks loaded to generate a compatibility graph.");
      pathSearchPanel.getSearchButton().setToolTipText("There are no "
      	+ "networks loaded to do a path search.");
      complexSearchPanel.getSearchButton().setToolTipText("There are no "
      	+ "networks loaded to do a complex search.");
    }
    else
    {
      compatGraphPanel.getGenerateButton().setEnabled(true);
      pathSearchPanel.getSearchButton().setEnabled(true);
      complexSearchPanel.getSearchButton().setEnabled(true);

      compatGraphPanel.getGenerateButton().setToolTipText("");
      pathSearchPanel.getSearchButton().setToolTipText("");
      complexSearchPanel.getSearchButton().setToolTipText("");
    }
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
