package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.foo.GraphConverter;
import cytoscape.graph.layout.algorithm.MutablePolyEdgeGraphLayout;
import cytoscape.graph.layout.impl.ScaleLayouter;
import cytoscape.util.CytoscapeAction;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ScaleLayoutAction extends CytoscapeAction
{

  public ScaleLayoutAction()
  {
    super("Shrink/Expand Graph");
    setPreferredMenu("Layout");
  }

  public void actionPerformed(ActionEvent e)
  {
    final MutablePolyEdgeGraphLayout nativeGraph =
      GraphConverter.getGraphCopy(10.0d, true);
    final ScaleLayouter scale = new ScaleLayouter(nativeGraph);
    Frame cyFrame = Cytoscape.getDesktop();
    JDialog dialog = new JDialog(cyFrame, "Shrink/Expand", true);
    dialog.setResizable(false);
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(new EmptyBorder(20, 20, 20, 20));
    panel.add(new JLabel("Scale graph by factor of 2^(n/100):"),
              BorderLayout.CENTER);
    final JSlider slider = new JSlider(-300, 300, 0);
    slider.setMajorTickSpacing(100);
    slider.setMinorTickSpacing(25);
    slider.setPaintTicks(true);
    slider.setPaintLabels(true);
    slider.addChangeListener(new ChangeListener() {
        private int prevValue = slider.getValue();
        public void stateChanged(ChangeEvent e) {
          double prevAbsoluteScaleFactor =
            Math.pow(2, ((double) prevValue) / 100.0d);
          double currentAbsoluteScaleFactor =
            Math.pow(2, ((double) slider.getValue()) / 100.0d);
          double neededIncrementalScaleFactor =
            currentAbsoluteScaleFactor / prevAbsoluteScaleFactor;
          scale.scaleGraph(neededIncrementalScaleFactor);
          GraphConverter.updateCytoscapeLayout(nativeGraph);
          prevValue = slider.getValue(); } });
    panel.add(slider, BorderLayout.SOUTH);
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.pack();
    dialog.move((cyFrame.size().width - dialog.size().width) / 2 +
                cyFrame.location().x,
                (cyFrame.size().height - dialog.size().height) / 5 +
                cyFrame.location().y);
    dialog.show(); // This blocks until dialog is disposed of.
  }

}
