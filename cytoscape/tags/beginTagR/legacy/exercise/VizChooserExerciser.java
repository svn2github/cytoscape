// VizChooserExerciser.java
//------------------------------------------------------------------------------
// RCS   $Revision$   $Date$
//------------------------------------------------------------------------------
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JDialog;
import java.awt.*;
import java.awt.BorderLayout;
import java.awt.event.*;

import y.view.Graph2D;
//------------------------------------------------------------------------------
public class VizChooserExerciser extends JFrame implements VizChooserClient {
  JFrame self;
  private String geometryFilename;
  private NodeProperties nodeProps = new NodeProperties ();
  private EdgeProperties edgeProps = new EdgeProperties ();
  private ExpressionData expressionData = null;
  private String bioDataServerName;
  private static  IPBioDataServer bioDataServer;
  VizAttributes vizAttributes = new VizAttributes ();

//------------------------------------------------------------------------------
VizChooserExerciser (String [] args) throws Exception
{
  super ("VizChooser Exerciser");
  loadCommandLineData (args);

  addWindowListener (new WindowAdapter () {
    public void windowClosing (WindowEvent e) {
      System.out.println ("bye!");
      System.exit (0);
      }
      }
    );

  JButton vizChooserButton = new JButton ("VizChooser...");
  vizChooserButton.addActionListener (new RunVizChooser ());

  JButton quitButton = new JButton ("Quit");
  quitButton.addActionListener (new QuitAction ());

  JPanel buttonContainer = new JPanel ();
  buttonContainer.add (vizChooserButton);
  buttonContainer.add (quitButton);
  setContentPane (buttonContainer);

  pack ();
  setVisible (true);
  self = this;

} // ctor
//------------------------------------------------------------------------------
private void loadCommandLineData (String [] args) throws Exception
{
  LucaConfig config = new LucaConfig (args);

  if (config.helpRequested ())
    System.out.println (config.getUsage ());
  else if (config.inputsError ()) {
    System.out.println ("------------- Inputs Error");
    System.out.println (config.getUsage ());
    System.out.println (config);
    }
  else {
    System.out.println (config);
    String geometryFilename = config.getGeometryFilename ();
    String bioDataDirectory = config.getBioDataDirectory ();
    String interactionsFilename = config.getInteractionsFilename ();
    String expressionFilename = config.getExpressionFilename ();
    Graph2D graph = null; 
    String dataSourceName = null;
    if (geometryFilename != null) {
      System.out.print ("reading " + geometryFilename + "...");
      System.out.flush ();
      GMLReader gmlReader = new GMLReader (geometryFilename);
      graph = gmlReader.read ();
      System.out.println ("  done");
      dataSourceName = geometryFilename;
      }
    else if (interactionsFilename != null) {
      System.out.print ("reading " + interactionsFilename + "...");
      System.out.flush ();
      InteractionsReader reader = new InteractionsReader (interactionsFilename);
      reader.read ();
      edgeProps = reader.getEdgeProperties ();
      graph = reader.getGraph ();
      System.out.println ("  done");
      dataSourceName = interactionsFilename;
      }
    if (expressionFilename != null) {
      System.out.print ("reading " + expressionFilename + "...");
      System.out.flush ();
      expressionData = new ExpressionData (expressionFilename);
      System.out.println ("  done");
      }
    if (bioDataDirectory != null) {
      bioDataServer = new IPBioDataServer ();
      bioDataServer.load (bioDataDirectory);
      }

    String [] attributeFilenames = config.getAttributeFilenames ();

    if (attributeFilenames != null)
      for (int i=0; i < attributeFilenames.length; i++)
        nodeProps.readFloatAttributesFromFile (attributeFilenames [i]);
    } // else:  valid config, all arguments processed

} // loadCommandLineData
//------------------------------------------------------------------------------
public void setVizAttributes (VizAttributes vizAttributes)
{
  System.out.println (vizAttributes);

}
//------------------------------------------------------------------------------
public class RunVizChooser extends AbstractAction {

  RunVizChooser () {super ("");}

  public void actionPerformed (ActionEvent e) {
    VizChooserClient client = VizChooserExerciser.this;
    JFrame parentFrame = VizChooserExerciser.this;
    VizChooser chooser = 
       new VizChooser (client, parentFrame, vizAttributes, nodeProps, edgeProps,
                       expressionData);
    chooser.pack ();
    chooser.setLocationRelativeTo (parentFrame);
    chooser.setVisible (true);
    }

} // RunVizChooser
//------------------------------------------------------------------------------
public class QuitAction extends AbstractAction {

  QuitAction () {super ("");}

  public void actionPerformed (ActionEvent e) {
    System.exit (0);
    }

} // QuitAction
//------------------------------------------------------------------------------
public static void main (String [] args) throws Exception
{
  VizChooserExerciser app = new VizChooserExerciser (args);
}
//------------------------------------------------------------------------------
} // class VizChooserExerciser
