package ManualLayout.scale;

import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.WindowConstants;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;

import cytoscape.graph.layout.algorithm.MutablePolyEdgeGraphLayout;
import ManualLayout.common.GraphConverter2;

public class ScaleAction extends AbstractAction
{
  public ScaleAction()
  {
    super("Scale");
  }

  public void actionPerformed(ActionEvent e)
  {
    // setup interface
    
    JLabel jLabel = new JLabel();
    jLabel.setText("Scale:");
    
    final JSlider jSlider = new JSlider();
    jSlider.setMaximum(300);
    jSlider.setMajorTickSpacing(100);
    jSlider.setPaintTicks(true);
    jSlider.setPaintLabels(true);
    jSlider.setValue(0);
    jSlider.setMinimum(-300);

    Hashtable labels = new Hashtable();
    labels.put(new Integer(-300), new JLabel("1/8"));
    labels.put(new Integer(-200), new JLabel("1/4"));
    labels.put(new Integer(-100), new JLabel("1/2"));
    labels.put(new Integer(0), new JLabel("1"));
    labels.put(new Integer(100), new JLabel("2"));
    labels.put(new Integer(200), new JLabel("4"));
    labels.put(new Integer(300), new JLabel("8"));

    jSlider.setLabelTable(labels);

    final JCheckBox jCheckBox = new JCheckBox();
    jCheckBox.setText("Scale Selected Nodes Only");

    GridLayout gridLayout = new GridLayout();
    gridLayout.setRows(3);

    JPanel jContentPane = new JPanel();
    jContentPane.setBorder(javax.swing.BorderFactory
                                .createEmptyBorder(0,10,0,10));
    jContentPane.setLayout(gridLayout);
    jContentPane.add(jLabel, null);
    jContentPane.add(jSlider, null);
    jContentPane.add(jCheckBox, null);

    JDialog jDialog = new JDialog();
    jDialog.setSize(new java.awt.Dimension(341,160));
    jDialog.setTitle("Scale");
    jDialog.setContentPane(jContentPane);
    jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jDialog.setResizable(false);
    jDialog.setModal(false);

    // set up interface logic

    if (Cytoscape.getCurrentNetworkView().getSelectedNodeIndices().length == 0)
    {
      jCheckBox.setEnabled(false);
    }

    final MutablePolyEdgeGraphLayout[] nativeGraph =
      new MutablePolyEdgeGraphLayout[]
      { GraphConverter2.getGraphReference(16.0d, true, false) };

    final ScaleLayouter[] scale = new ScaleLayouter[]
      { new ScaleLayouter(nativeGraph[0]) };

    jCheckBox.addActionListener(
      new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
	{
	  nativeGraph[0] = GraphConverter2.getGraphReference
	                     (128.0d, true, jCheckBox.isSelected());
          scale[0] = new ScaleLayouter(nativeGraph[0]);
	}
      }
    );

    jSlider.addChangeListener(
      new ChangeListener()
      {
        private int prevValue = jSlider.getValue();
	
	public void stateChanged(ChangeEvent e)
	{
          if (prevValue == jSlider.getValue()) return;

          double prevAbsoluteScaleFactor =
            Math.pow(2, ((double) prevValue) / 100.0d);

          double currentAbsoluteScaleFactor =
            Math.pow(2, ((double) jSlider.getValue()) / 100.0d);

          double neededIncrementalScaleFactor =
            currentAbsoluteScaleFactor / prevAbsoluteScaleFactor;
	    
          scale[0].scaleGraph(neededIncrementalScaleFactor);
          Cytoscape.getCurrentNetworkView().updateView();
          prevValue = jSlider.getValue();
        }
      }
    );
    
    jDialog.show();
  }
}
