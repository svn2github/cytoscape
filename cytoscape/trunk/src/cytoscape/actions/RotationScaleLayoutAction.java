package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.foo.GraphConverter;
import cytoscape.graph.layout.algorithm.MutablePolyEdgeGraphLayout;
import cytoscape.graph.layout.impl.RotationLayouter;
import cytoscape.graph.layout.impl.ScaleLayouter;
import cytoscape.util.CytoscapeAction;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class RotationScaleLayoutAction extends CytoscapeAction
{

  public RotationScaleLayoutAction()
  {
    super("Rotate/Scale Graph");
    setPreferredMenu("Layout");
  }

  public void actionPerformed(ActionEvent e)
  {
    final MutablePolyEdgeGraphLayout nativeGraph =
      GraphConverter.getGraphCopy(20.0d, true, true);
    final RotationLayouter rotation = new RotationLayouter(nativeGraph);
    final ScaleLayouter scale = new ScaleLayouter(nativeGraph);
    Frame cyFrame = Cytoscape.getDesktop();
    JDialog dialog = new JDialog(cyFrame, "Rotate/Scale", true);
    dialog.setResizable(false);
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    // Define the panel containing rotation widget.
    JPanel rotPanel = new JPanel(new BorderLayout());
    rotPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
    rotPanel.add(new JLabel("Degrees of Rotation:"), BorderLayout.NORTH);
    final JSlider rotSlider = new JSlider(0, 360, 0);
    rotSlider.setMajorTickSpacing(90);
    rotSlider.setMinorTickSpacing(15);
    rotSlider.setPaintTicks(true);
    rotSlider.setPaintLabels(true);
    rotSlider.addChangeListener(new ChangeListener() {
        private int prevValue = rotSlider.getValue();
        public void stateChanged(ChangeEvent e) {
          double radians = ((double) (rotSlider.getValue() - prevValue)) *
            2.0d * Math.PI / 360.0d;
          rotation.rotateGraph(radians);
          GraphConverter.updateCytoscapeLayout(nativeGraph);
          prevValue = rotSlider.getValue(); } });
    rotPanel.add(rotSlider, BorderLayout.CENTER);

    // Define the panel containing the scale widget.
    JPanel sclPanel = new JPanel(new BorderLayout());
    sclPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
    sclPanel.add(new JLabel("Scale Factor:"), BorderLayout.NORTH);
    final JSlider sclSlider = new JSlider(-300, 300, 0);
    sclSlider.setMajorTickSpacing(100);
    Hashtable labels = new Hashtable();
    labels.put(new Integer(-300), new JLabel("1/8"));
    labels.put(new Integer(-200), new JLabel("1/4"));
    labels.put(new Integer(-100), new JLabel("1/2"));
    labels.put(new Integer(0), new JLabel("1"));
    labels.put(new Integer(100), new JLabel("2"));
    labels.put(new Integer(200), new JLabel("4"));
    labels.put(new Integer(300), new JLabel("8"));
    sclSlider.setLabelTable(labels);
    sclSlider.setPaintTicks(true);
    sclSlider.setPaintLabels(true);
    sclSlider.addChangeListener(new ChangeListener() {
        private int prevValue = sclSlider.getValue();
        public void stateChanged(ChangeEvent e) {
          double prevAbsoluteScaleFactor =
            Math.pow(2, ((double) prevValue) / 100.0d);
          double currentAbsoluteScaleFactor =
            Math.pow(2, ((double) sclSlider.getValue()) / 100.0d);
          double neededIncrementalScaleFactor =
            currentAbsoluteScaleFactor / prevAbsoluteScaleFactor;
          scale.scaleGraph(neededIncrementalScaleFactor);
          GraphConverter.updateCytoscapeLayout(nativeGraph);
          prevValue = sclSlider.getValue(); } });
    sclPanel.add(sclSlider, BorderLayout.CENTER);

    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    mainPanel.add(sclPanel, BorderLayout.NORTH);
    mainPanel.add(rotPanel, BorderLayout.CENTER);

    dialog.getContentPane().add(mainPanel, BorderLayout.CENTER);
    dialog.pack();
    dialog.move((cyFrame.size().width - dialog.size().width) / 2 +
                cyFrame.location().x,
                (cyFrame.size().height - dialog.size().height) / 5 +
                cyFrame.location().y);
    dialog.show(); // This blocks until dialog is disposed of.
  }

}
