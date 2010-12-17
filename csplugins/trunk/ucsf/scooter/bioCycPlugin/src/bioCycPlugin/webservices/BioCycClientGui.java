// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package bioCycPlugin.webservices;

import bioCycPlugin.BioCycPlugin;
import bioCycPlugin.model.Database;
import bioCycPlugin.model.Pathway;
import bioCycPlugin.webservices.ResultProperty;

import bioCycPlugin.webservices.BioCycClient.FindPathwaysByTextParameters;
import bioCycPlugin.webservices.BioCycClient.GetPathwayParameters;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import cytoscape.logger.CyLogger;
import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.data.webservice.CyWebServiceException;
import cytoscape.data.webservice.WebServiceClientManager;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/**
 * GUI for accessing the WikiPathways webservice,
 * lets the user query for a list of Pathways
 */
public class BioCycClientGui extends JPanel implements ActionListener {
	final BioCycClient client;
	private CyLogger logger;
	private boolean initialized = false;

	Database defaultDatabase = null;
	JComboBox databaseCombo;
	JTextField searchText;
	JTextField webServiceText;
	JTable resultTable;
	ListWithPropertiesTableModel<ResultProperty, ResultRow> tableModel;

	public BioCycClientGui(BioCycClient c, CyLogger logger) {
		client = c;
		this.logger = logger;

		databaseCombo = new JComboBox();
		databaseCombo.addActionListener(this);
		databaseCombo.setActionCommand(ACTION_SET_DATABASE);
		Object dbArray[] = new Object[1];
		dbArray[0] = "Initializing...";
		databaseCombo.setModel(new DefaultComboBoxModel(dbArray));

		searchText = new JTextField();
		searchText.setActionCommand(ACTION_SEARCH);
		searchText.addActionListener(this);

		webServiceText = new JTextField();
		webServiceText.setActionCommand(ACTION_SET_WEBSERVICE);
		webServiceText.addActionListener(this);
		webServiceText.setText(BioCycPlugin.getBaseUrl());

		JButton searchBtn = new JButton("Search");
		searchBtn.setActionCommand(ACTION_SEARCH);
		searchBtn.addActionListener(this);

		resultTable = new JTable();
		resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = resultTable.getSelectedRow();
					ResultRow selected = tableModel.getRow(row);
					openNetwork(selected);
				}
			}
		});

		setLayout(new FormLayout(
				"4dlu, pref, 2dlu, fill:pref:grow, 4dlu, pref, 4dlu, pref, 4dlu",
				"4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, fill:pref:grow, 4dlu"
		));
		CellConstraints cc = new CellConstraints();
		add(new JLabel("Web Server:"), cc.xy(2, 2));
		add(webServiceText, cc.xy(4, 2));
		add(new JLabel("Database:"), cc.xy(2, 4));
		add(databaseCombo, cc.xy(4, 4));
		add(new JLabel("Search:"), cc.xy(2, 6));
		add(searchText, cc.xy(4, 6));
		add(searchBtn, cc.xy(8, 6));
		add(new JScrollPane(resultTable), cc.xyw(2, 8, 7));
	}

	protected void resetDatabases() {
		List<Database> databases = new ArrayList<Database>();
		try {
			if (client.listDatabases() != null)
				databases.addAll(client.listDatabases());
		} catch (Exception e) {
			logger.error("Unable to get databases for BioCyc client", e);
		}

		Object dbArray[] = databases.toArray();
		Arrays.sort(dbArray);
		databaseCombo.setModel(new DefaultComboBoxModel(dbArray));
		defaultDatabase = Database.getDefaultDatabase(databases);
		if (defaultDatabase != null) 
			databaseCombo.setSelectedItem(defaultDatabase);
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if(ACTION_SEARCH.equals(action)) {
			FindPathwaysByTextParameters request = new FindPathwaysByTextParameters();
			request.query = searchText.getText();
			request.db = ((Database)databaseCombo.getSelectedItem()).getOrgID();
			try {
				WebServiceClientManager.getCyWebServiceEventSupport().fireCyWebServiceEvent(
					new CyWebServiceEvent<FindPathwaysByTextParameters>(
						client.getClientID(),
						WSEventType.SEARCH_DATABASE,
						request
					)
				);
			} catch (CyWebServiceException ex) {
				switch(ex.getErrorCode()) {
				case NO_RESULT:
					JOptionPane.showMessageDialog(
							this, "The search didn't return any results",
							"No results", JOptionPane.INFORMATION_MESSAGE
					);
					break;
				case OPERATION_NOT_SUPPORTED:
				case REMOTE_EXEC_FAILED:
					JOptionPane.showMessageDialog(
						this, "Error: " + ex.getErrorCode() + ". See log for details",
						"Error", JOptionPane.ERROR_MESSAGE
					);
					break;
				}
				ex.printStackTrace();
			}
		} else if (ACTION_SET_DATABASE.equals(action)) {
			defaultDatabase = (Database) databaseCombo.getSelectedItem();
			Database.setDefaultDatabase(defaultDatabase);
		} else if (ACTION_SET_WEBSERVICE.equals(action)) {
			String url = webServiceText.getText();
			if (url == null || url.length() == 0)
				url = BioCycPlugin.DEFAULT_URL;
			BioCycPlugin.setProp(BioCycPlugin.WEBSERVICE_URL, url);
			client.getStub();
			// Now, update the text again
			webServiceText.setText(url);
			resetDatabases();
		}
	}

	private void openNetwork(ResultRow selected) {
		try {
			GetPathwayParameters request = new GetPathwayParameters();
			Pathway result = selected.getResult();
			request.id = result.getFrameID();
			request.db = result.getOrgID();
			WebServiceClientManager.getCyWebServiceEventSupport().fireCyWebServiceEvent(
				new CyWebServiceEvent(
						client.getClientID(), WSEventType.IMPORT_NETWORK,
						request
				)
			);
		} catch (CyWebServiceException ex) {
			JOptionPane.showMessageDialog(
				BioCycClientGui.this, "Error: " + ex.getErrorCode() + ". See error log for details",
				"Error", JOptionPane.ERROR_MESSAGE
			);
		}
	}

	public void setResults(List<Pathway> results) {
		tableModel =
			new ListWithPropertiesTableModel<ResultProperty, ResultRow>();
		if(results != null) {
			tableModel.setColumns(new ResultProperty[] {
					ResultProperty.ID,
					ResultProperty.NAME,
					ResultProperty.SPECIES,
			});
			resultTable.setModel(tableModel);
			for(Pathway p : results) {
				tableModel.addRow(new ResultRow(p));
			}
		}
		resultTable.setModel(tableModel);
	}

	/**
	 * Represents a hit, a single row in the query results table.
	 */
	class ResultRow implements RowWithProperties<ResultProperty> {
		Pathway result;

		public ResultRow(Pathway result) {
			this.result = result;
		}

		public Pathway getResult() {
			return result;
		}

		public String getProperty(ResultProperty prop) {
			switch(prop) {
			case ID: return result.getFrameID();
			case NAME: return result.getCommonName();
			case SPECIES: return result.getOrgID();
			}
			return null;
		}
	}

	// We override the paint method for the sole purpose of figuring out when we're
	// actually being painted.  We don't want to start going over the net unless
	// we're really going to need to
	public void paint(Graphics g) {
		if (!initialized) {
			initialized = true;

			ResetDatabaseTask getDbTask = new ResetDatabaseTask();
			getDbTask.start();
		}
		super.paint(g);
	}

	private static final String ACTION_SEARCH = "Search";
	private static final String ACTION_SET_DATABASE = "Set Database";
	private static final String ACTION_SET_WEBSERVICE = "Set Web Service URL";

	class ResetDatabaseTask extends Thread {
		public void run() {
			resetDatabases();
		}
	}
}
