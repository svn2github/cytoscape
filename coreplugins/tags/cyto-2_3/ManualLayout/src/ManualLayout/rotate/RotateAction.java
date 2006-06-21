package ManualLayout.rotate;

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

//import LayoutCommon.MutablePolyEdgeGraphLayout;
import cytoscape.graph.layout.algorithm.MutablePolyEdgeGraphLayout;
import ManualLayout.common.GraphConverter2;

public class RotateAction extends AbstractAction
{
  public RotateAction()
  {
    super("Rotate");
  }

  public void actionPerformed(ActionEvent e)
  {
    // set up the user interface

    JLabel jLabel = new JLabel();
    jLabel.setText("Rotate in Degrees:");

    final JSlider jSlider = new JSlider();
    jSlider.setMaximum(360);
    jSlider.setMajorTickSpacing(90);
    jSlider.setPaintLabels(true);
    jSlider.setPaintTicks(true);
    jSlider.setMinorTickSpacing(15);
    jSlider.setValue(0);

    final JCheckBox jCheckBox = new JCheckBox();
    jCheckBox.setText("Rotate Selected Nodes Only");

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
    jDialog.setSize(new java.awt.Dimension(249,152));
    jDialog.setTitle("Rotate");
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
      
    final RotationLayouter[] rotation = new RotationLayouter[]
      { new RotationLayouter(nativeGraph[0]) };

    jSlider.addChangeListener(
      new ChangeListener()
      {
        int prevValue = jSlider.getValue();

	public void stateChanged(ChangeEvent e)
	{
	  if (jSlider.getValue() == prevValue) return;

	  double radians = ((double) (jSlider.getValue() - prevValue)) *
	                   2.0d * Math.PI / 360.0d;
          rotation[0].rotateGraph(radians);
	  Cytoscape.getCurrentNetworkView().updateView();

	  prevValue = jSlider.getValue();
	}
      }
    );

    jCheckBox.addActionListener(
      new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
	{
	  nativeGraph[0] = GraphConverter2.getGraphReference
	                     (128.0d, true, jCheckBox.isSelected());
	  rotation[0] = new RotationLayouter(nativeGraph[0]);
	}
      }
    );

    jDialog.show();
  }
}
