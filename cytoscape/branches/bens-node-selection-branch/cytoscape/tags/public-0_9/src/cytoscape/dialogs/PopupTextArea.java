// PopupTextArea
//----------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package cytoscape.dialogs;
//---------------------------------------------------------------------------------------
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.*;
import java.awt.event.*;
//-------------------------------------------------------------------------------------
public class PopupTextArea extends JDialog {
  PopupTextArea popupTextArea;
    Point location;
//-------------------------------------------------------------------------------------
public PopupTextArea (JDialog parent, String title, String text) {

  super (parent, false);
  location = parent.getLocationOnScreen();
  init (title, text);
}
//-------------------------------------------------------------------------------------
public PopupTextArea (Frame parent, String title, String text)
{
  super (parent, false);
  location = parent.getLocationOnScreen();
  init (title, text);

}
//-------------------------------------------------------------------------------------
private void init (String title, String text)
{
  setTitle (title);
  popupTextArea = this;

  JPanel panel = new JPanel ();
  panel.setLayout (new BorderLayout ());

  final JTextArea textArea = new JTextArea (text);
  textArea.setEditable (false);
  JScrollPane scrollPane = new JScrollPane (textArea);
  //textArea.setPreferredSize (new Dimension (600, 400));
  //scrollPane.setPreferredSize (new Dimension (600, 400));
  panel.setPreferredSize (new Dimension (600, 400));
  panel.add (scrollPane, BorderLayout.CENTER);


  JPanel buttonPanel = new JPanel ();
  JButton dismissButton = new JButton ("Dismiss");
  dismissButton.addActionListener (new DismissAction ());
  buttonPanel.add (dismissButton, BorderLayout.CENTER);
  panel.add (buttonPanel, BorderLayout.SOUTH);
  setContentPane (panel);
  setLocation(location);
  pack ();
  setVisible (true);

} // PopupTextArea ctor
//------------------------------------------------------------------------------------
public class DismissAction extends AbstractAction {

  DismissAction () {super ("");}

  public void actionPerformed (ActionEvent e) {
    popupTextArea.dispose ();
    }

} // QuitAction
//-----------------------------------------------------------------------------------
} // class PopupTextArea
