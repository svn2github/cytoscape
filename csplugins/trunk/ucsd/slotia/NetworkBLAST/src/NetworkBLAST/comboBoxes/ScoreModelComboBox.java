package NetworkBLAST.comboBoxes;

import java.util.List;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.event.ItemListener;

import nct.networkblast.score.ScoreModel;
import nct.networkblast.score.LogLikelihoodScoreModel;
import nct.networkblast.score.SimpleEdgeScoreModel;

import cytoscape.Cytoscape;

import NetworkBLAST.NetworkBLASTDialog;
import NetworkBLAST.panels.scoreModelPanels.LogLikelihoodPanel;
import NetworkBLAST.panels.scoreModelPanels.SimpleEdgePanel;

public class ScoreModelComboBox extends JComboBox
{
  public ScoreModelComboBox(NetworkBLASTDialog _parentDialog)
  {
    super();
    initializeItems();
    
    parentDialog = _parentDialog;
    
    for (ScoreModelComboBoxItem item : this.items)
      this.addItem(item);
  }

  public ScoreModelComboBox(NetworkBLASTDialog _parentDialog,
  		CardLayout _layout, JPanel _layoutParent)
  {
    super();
    initializeItems();
    
    parentDialog = _parentDialog;

    final CardLayout cardLayout = _layout;
    final JPanel layoutParent = _layoutParent;

    for (ScoreModelComboBoxItem item : this.items)
      if (item.getSettingsPanel() != null)
      {
        this.addItem(item);
	layoutParent.add(item.getSettingsPanel(), item.toString());
      }
    ItemListener itemListener = new ItemListener()
    {
      public void itemStateChanged(java.awt.event.ItemEvent _e)
      {
        int switchState = getSelectedIndex();
	while (switchState != cardState)
	{
	  cardState = (cardState + 1) % getItemCount();
	  cardLayout.next(layoutParent);
        }
      }

      private int cardState = 0;
    };

    this.addItemListener(itemListener);
  }

  public ScoreModel<String,Double> getSelectedScoreModel()
  {
    if (this.getSelectedItem() == null)
      return null;
    else
      return ((ScoreModelComboBoxItem) this.getSelectedItem()).getScoreModel();
  }
  
  private abstract class ScoreModelComboBoxItem
  {
    public ScoreModelComboBoxItem(String _name, JPanel _settingsPanel)
    {
      this.name = _name;
      this.settingsPanel = _settingsPanel;
    }

    public String getName()
      { return this.name; }

    public String toString()
      { return this.getName(); }

    public JPanel getSettingsPanel()
      { return this.settingsPanel; }

    public abstract ScoreModel<String,Double> getScoreModel();

    protected String name;
    protected JPanel settingsPanel;
  }
  
  private void initializeItems()
  {
    if (items != null) return;
    
    ScoreModelComboBoxItem logLikelihoodItem =
    	new ScoreModelComboBoxItem("Log Likelihood", logLikelihoodPanel)
    {
      public ScoreModel<String,Double> getScoreModel()
      {
        LogLikelihoodPanel panel = (LogLikelihoodPanel) settingsPanel;
	double truthFactor, truthModel, bkgndProb;
	
	try
	{
	  truthFactor = Double.parseDouble(panel.
	    		getTruthFactorTextField().getText());
			
	  truthModel = Double.parseDouble(panel.
	    		getTruthModelTextField().getText());
			
	  if ( !( 0.0 <= truthModel && truthModel <= 1.0 ) )
	    throw new NumberFormatException();
	      
	  bkgndProb = Double.parseDouble(panel.
	   		getBkgndProbTextField().getText());
	}
	catch (NumberFormatException e)
	{
	  JOptionPane.showMessageDialog(null,
	    	"The parameters for the Log Likelihood\nscore model are " +
		"not specified correctly.\nPlease go back to the Score Model " +
		"Settings\ndialog and make sure each text field\nhas been " +
		"entered correctly.",
		"NetworkBLAST: Log Likelihood Score Model",
		JOptionPane.ERROR_MESSAGE);
	  return null;
	}

	return new LogLikelihoodScoreModel<String>(truthFactor, truthModel,
	  	bkgndProb);
      }
    };
      
    ScoreModelComboBoxItem simpleEdgeItem =
    	new ScoreModelComboBoxItem("Simple Edge", null)
    {
      public ScoreModel<String,Double> getScoreModel()
        { return new SimpleEdgeScoreModel<String>(); }
    };

    items = new ArrayList<ScoreModelComboBoxItem>();
    items.add(logLikelihoodItem);
    items.add(simpleEdgeItem);
  }

  private List<ScoreModelComboBoxItem> items = null;
  private NetworkBLASTDialog parentDialog;

  private static LogLikelihoodPanel logLikelihoodPanel =
  		new LogLikelihoodPanel();
}
