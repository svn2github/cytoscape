package ucsd.rmkelley.WithinPathway;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.ButtonGroup;
import ucsd.rmkelley.Util.DoubleVerification;
import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;

public class SearchOptionsPanel extends JPanel{

  JCheckBox selectedSearch, newScore, generateCutoff;
  JTextField cutoffField;

  public SearchOptionsPanel(){
    
    //create the display objects
    selectedSearch = new JCheckBox("Search from selected interactions",false);
    newScore = new JCheckBox("Use new scoring model?",true);
    newScore.setEnabled(false);
    generateCutoff = new JCheckBox("Generate score thresh-hold?",false);
    cutoffField = new JTextField("0.0",10);

    //set up the behaviors of the objects
    generateCutoff.addChangeListener(new ChangeListener(){
	public void stateChanged(ChangeEvent e){	
	  cutoffField.setEnabled(!generateCutoff.isSelected());	
	}});
    
    //set up the verification agents for the particular data types
    new DoubleVerification(cutoffField,0.0,Double.POSITIVE_INFINITY,0.0);
    
    //add into the layout
    setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
    add(selectedSearch);
    add(newScore);
    add(generateCutoff);
    JPanel cutoffPanel = new JPanel();
    cutoffPanel.setBorder(new TitledBorder("Manually specify scoring thresh-hold"));
    cutoffPanel.add(cutoffField);
    add(cutoffPanel);
  }


  public boolean selectedSearch(){
    return selectedSearch.isSelected();
  }

  public boolean newScore(){
    return newScore.isSelected();
  }

  public boolean generateCutoff(){
    return generateCutoff.isSelected();
  }

  public double getCutoff(){
    return (new Double(cutoffField.getText()).doubleValue());
  }

}




