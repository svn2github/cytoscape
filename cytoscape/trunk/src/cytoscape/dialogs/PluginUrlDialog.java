/**
 * 
 */
package cytoscape.dialogs;

import cytoscape.*;
import cytoscape.plugin.PluginManager;
import cytoscape.plugin.PluginInfo;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import java.util.List;
import java.util.Map;
import java.util.Iterator;

/**
 * @author skillcoy
 * 
 */
public class PluginUrlDialog extends JDialog
	{
	private JComboBox Combo;
	private JPanel ButtonPanel;
	private JButton Ok;
	private JButton Cancel;

	public PluginUrlDialog()
		{
		this.setLocationRelativeTo(Cytoscape.getDesktop());
		this.initDialog();
		}

	private void initDialog()
		{
		setPreferredSize(new Dimension(325, 200));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new GridBagLayout());

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(10, 0, 10, 0);
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;

		JLabel Label = new JLabel("Find New Plugins");
		Label.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 24));
		getContentPane().add(Label, gridBagConstraints);
		}

	public void createComboBox(String[] Urls)
		{
		Combo = new JComboBox(Urls);
		Combo.setEditable(true);
		Combo.setPreferredSize(new Dimension(200, 25));
		Combo.setEnabled(true);
		initBox();
		initButtons();
		}

	private void initBox()
		{
		JPanel BoxPanel = new JPanel(new GridBagLayout());

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(10, 0, 10, 0);
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;

		JLabel Label = new JLabel("Choose a url from the list or enter your own");
		BoxPanel.add(Label, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(10, 0, 10, 0);
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;

		BoxPanel.add(Combo, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		// gridBagConstraints.insets = new Insets(10, 0, 10, 0);
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;

		getContentPane().add(BoxPanel, gridBagConstraints);
		}

	private void initButtons()
		{
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		// set up button panel
		ButtonPanel = new JPanel(new GridBagLayout());
		gridBagConstraints.insets = new Insets(5, 0, 0, 5);

		Ok = new JButton("Ok");
		Ok.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent Event)
					{
					PluginManager Mgr = CytoscapeInit.getPluginManager();
					// TODO bring up the install dialog, add entered url to list
					String Url = (String) Combo.getSelectedItem();
					PluginInstallDialog Install = new PluginInstallDialog();
					
					Map<String, List<PluginInfo>> Plugins = Mgr.getPluginsByCategory();
					Iterator<String> pI = Plugins.keySet().iterator();
					int index = 0;
					while(pI.hasNext())
						{
						String Category = pI.next();
						Install.addCategory(Category, Plugins.get(Category), index);
						if (index <= 0) index ++; // apparenlty just need 0/1
						}
					Install.pack();
					Install.setVisible(true);
					PluginUrlDialog.this.dispose();
					}
			});
		ButtonPanel.add(Ok, gridBagConstraints);

		Cancel = new JButton("Cancel");
		Cancel.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent Event)
					{
					PluginUrlDialog.this.setVisible(false);
					PluginUrlDialog.this.dispose();
					}
			});
		ButtonPanel.add(Cancel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		getContentPane().add(ButtonPanel, gridBagConstraints);
		}

	}
