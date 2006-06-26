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
import nct.networkblast.score.SimpleScoreModel;
import nct.networkblast.score.SimpleEdgeScoreModel;
import nct.networkblast.score.SimpleNodeScoreModel;

import cytoscape.Cytoscape;

import NetworkBLAST.panels.scoreModelPanels.LogLikelihoodPanel;

public class ScoreModelComboBox extends JComboBox
{
  public ScoreModelComboBox()
  {
    super();
    
    initializeItems();
    
    for (ScoreModelComboBoxItem item : this.items)
      this.addItem(item);
  }

  public ScoreModelComboBox(CardLayout _layout, JPanel _layoutParent)
  {
    super();
    
    initializeItems();

    final CardLayout cardLayout = _layout;
    final JPanel layoutParent = _layoutParent;

    for (ScoreModelComboBoxItem item : this.items)
      if (item.getSettingsPanel() != null)
      {
        this.addItem(item);
	layoutParent.add(item.getSettingsPanel(), item.toString());
      }

    this.addItemListener(new ItemListener()
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
    });
  }

  public ScoreModel<String,Double> getSelectedScoreModel()
  {
    if (this.getSelectedItem() == null)
    {
      System.err.println("ScoreModelComboBox: no item selected!");
      return null;
    }

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
    if (items != null)
      return;
    
    items = new ArrayList<ScoreModelComboBoxItem>();
    items.add(
      new ScoreModelComboBoxItem("Log Likelihood", new LogLikelihoodPanel())
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
	    	"The parameters for the Log Likelihood score model are " +
		"not specified correctly.\nGo back to the Score Model " +
		"Settings dialog\nand make sure each text field has been " +
		"entered correctly.", "Log Likelihood Score Model",
		JOptionPane.ERROR_MESSAGE);
	    return null;
	  }
	  System.out.println("Parsed parameters. " + truthFactor + ", " + truthModel + ", " + bkgndProb);

	  return new LogLikelihoodScoreModel<String>(truthFactor, truthModel,
	  	bkgndProb);
	}
      });
      
    items.add(new ScoreModelComboBoxItem("Simple Edge", null)
    {
      public ScoreModel<String,Double> getScoreModel()
        { return new SimpleEdgeScoreModel<String>(); }
    });
  }

  private List<ScoreModelComboBoxItem> items = null;

  protected int type;
}
