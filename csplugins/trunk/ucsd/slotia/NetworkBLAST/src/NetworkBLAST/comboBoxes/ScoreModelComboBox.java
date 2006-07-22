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

/**
 * Used by NetworkBLAST's panels for selecting a score model.
 * Score model combo boxes have their own class because having one class
 * that manages score model combo boxes for all panels is easier to
 * maintain than having many combo boxes scattered throughout the code.
 *
 * There are two types of ScoreModelComboBoxes:
 *  1. A combo box that gives the option to the user of which score model
 *     to use in an algorithm. (Like in the Compatibility Graph Panel.)
 *  2. A combo box that gives the option to the user of which score model
 *     to modify its settings. (Like in the Score Model Settings Panel.)
 *     This type of combo box requires a CardLayout and the JPanel that holds 
 *     it. Upon construction, this class will add all the settings panels
 *     to the CardLayout. When the user selects a score model, it will
 *     flip through the CardLayout for the desired settings panel. This
 *     type of combo box does not list score models that do not have
 *     a settings panel.
 *
 * Choosing the type of the combo box is done by selecting the necessary
 * constructor. See constructor comments for more information.
 *
 * See comments for the initializeItems() private method for information
 * on adding, modifying, or removing a score model from the combo boxes.
 */

public class ScoreModelComboBox extends JComboBox
{
  /**
   * This constructor is for combo boxes that are used for selecting
   * a score model for an algorithm.
   */
  public ScoreModelComboBox(NetworkBLASTDialog _parentDialog)
  {
    super();
    initializeItems();
    
    parentDialog = _parentDialog;
    
    for (ScoreModelComboBoxItem item : this.items)
      this.addItem(item);
  }

  /**
   * This constructor is for combo boxes that are used for selecting
   * which score model's settings to modify.
   * @param _layout		The CardLayout that holds the selected
   *				score model's settings panel. This class
   *				will handle flipping the cards of _layout when
   *				the user has selected a score model.
   *				_layout must never add or remove cards. This
   *				is explicitly handled by this class.
   * @param _layoutParent	The JPanel that holds the CardLayout
   */
  public ScoreModelComboBox(NetworkBLASTDialog _parentDialog,
  		CardLayout _layout, JPanel _layoutParent)
  {
    super();
    parentDialog = _parentDialog;
    initializeItems();

    final CardLayout cardLayout = _layout;
    final JPanel layoutParent = _layoutParent;

    for (ScoreModelComboBoxItem item : this.items)
    {
      if (item.getSettingsPanel() != null)
      {
        this.addItem(item);
	layoutParent.add(item.getSettingsPanel(), item.toString());
      }
    }
    
    ItemListener itemListener = new ItemListener()
    {
      public void itemStateChanged(java.awt.event.ItemEvent _e)
      {
	while (getSelectedIndex() != cardState)
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

  /**
   * This sets up the <i>items</i> private class member.
   * The <i>items</i> stores information about each available score model.
   * Each score model is stored by using the ScoreModelComboBoxItem class.
   * Each has:
   *  1. A name.
   *  2. A way to obtain a new ScoreModel instance. This is done by
   *     implementing the abstract method <i>getScoreModel()</i>.
   *  3. (Optional) The score model's settings panel. If the score model
   *     does not have a settings panel, it will be null. If the score model
   *     combo box is used for selecting which score model to modify its
   *     settings (Type 2 in the class's comments above), it will not list
   *     the score model if it does not have a settings panel.
   *
   * To add another score model:
   *  1. Create a new instance of ScoreModelComboBoxItem.
   *  2. Add the instance to the <i>items</i> member.
   * Adding score models must be done here. When a ScoreModelComboBox is
   * constructed, it will read <i>items</i> to set up its interface.
   */
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
