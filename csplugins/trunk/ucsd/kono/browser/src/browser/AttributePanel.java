package browser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.SwingPropertyChangeSupport;

import cytoscape.data.CyAttributes;
import exesto.AttributeTags;

public class AttributePanel extends JPanel implements PropertyChangeListener,
		ListSelectionListener, ListDataListener, ActionListener {

	CyAttributes data;
	DataTableModel tableModel;

	// create new attribute
	JTextField newAttField;
	JButton newAttButton;
	JComboBox newAttType;

	// attributes
	JList attributeList;

	// tags
	JList tagList;
	JButton addToTag;
	JButton removeFromTag;
	JTextField newTag;
	JButton newTagButton;

	protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(
			this);

	public AttributePanel(CyAttributes data, AttributeModel a_model,
			LabelModel l_model) {

		this.data = data;

		setLayout(new BorderLayout());

		// new attribute
		JPanel new_att_panel = new JPanel();
		newAttField = new JTextField(10);
		newAttField.addActionListener(this);
		newAttButton = new JButton("Create");
		newAttButton.addActionListener(this);
		newAttType = new JComboBox(new String[] { "String", "Floating Point",
				"Integer", "Boolean" });
		new_att_panel.setLayout(new BorderLayout());
		new_att_panel.setBorder(new TitledBorder("Create New Attribute"));
		new_att_panel.add(newAttField, BorderLayout.WEST);
		new_att_panel.add(newAttType, BorderLayout.CENTER);
		JPanel bp = new JPanel();
		bp.add(newAttButton);
		new_att_panel.add(bp, BorderLayout.SOUTH);

		// attributes
		JPanel attPanel = new JPanel();
		attPanel.setBorder(new TitledBorder("Attributes"));
		attributeList = new JList(a_model);
		attributeList.addListSelectionListener(this);
		attributeList
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane a_scroll = new JScrollPane(attributeList);
		attPanel.add(a_scroll, BorderLayout.CENTER);
		a_scroll.setPreferredSize(new Dimension(200, 180));

		// tags
		JPanel labPanel = new JPanel();
		tagList = new JList(l_model);
		tagList.addListSelectionListener(this);
		tagList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane l_scroll = new JScrollPane(tagList);
		labPanel.add(l_scroll, BorderLayout.CENTER);
		l_scroll.setPreferredSize(new Dimension(200, 50));

		// tag control
		JPanel lcp = new JPanel();
		addToTag = new JButton("+");
		addToTag.addActionListener(this);
		removeFromTag = new JButton("-");
		removeFromTag.addActionListener(this);
		lcp.add(addToTag);
		lcp.add(removeFromTag);

		// new Tag
		newTag = new JTextField(10);
		newTag.addActionListener(this);
		newTagButton = new JButton("New");
		newTagButton.addActionListener(this);
		JPanel ln = new JPanel();
		ln.add(newTag);
		ln.add(newTagButton);

		JPanel one = new JPanel();
		one.setBorder(new TitledBorder("Tags"));
		one.setLayout(new BorderLayout());
		one.add(ln, BorderLayout.SOUTH);
		one.add(labPanel, BorderLayout.CENTER);
		one.add(lcp, BorderLayout.NORTH);

		setLayout(new BorderLayout());
		add(new_att_panel, BorderLayout.NORTH);
		add(attPanel, BorderLayout.CENTER);
		add(one, BorderLayout.SOUTH);

	}

	public void setTableModel(DataTableModel tableModel) {
		this.tableModel = tableModel;
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == newTag || e.getSource() == newTagButton) {
			// create a new tag, and add the attributes to it
			String tag_name = newTag.getText();
			Object[] atts = attributeList.getSelectedValues();
			for (int i = 0; i < atts.length; ++i) {
				AttributeTags.applyTag(data, (String) atts[i], tag_name);
			}

		} else if (e.getSource() == addToTag) {
			String tag = tagList.getSelectedValue().toString();
			Object[] atts = attributeList.getSelectedValues();
			for (int i = 0; i < atts.length; ++i) {
				AttributeTags.applyTag(data, (String) atts[i], tag);
			}

		} else if (e.getSource() == removeFromTag) {
			String tag = tagList.getSelectedValue().toString();
			Object[] atts = attributeList.getSelectedValues();
			for (int i = 0; i < atts.length; ++i) {
				AttributeTags.removeTag(data, (String) atts[i], tag);
			}

		} else if (e.getSource() == newAttButton
				|| e.getSource() == newAttField) {
			String name = newAttField.getText();
			if (name.length() < 1)
				return;

			String type = (String) newAttType.getSelectedItem();
			byte t;
			if (type.equals("String"))
				t = CyAttributes.TYPE_STRING;
			else if (type.equals("Floating Point"))
				t = CyAttributes.TYPE_FLOATING;
			else if (type.equals("Integer"))
				t = CyAttributes.TYPE_INTEGER;
			else if (type.equals("Boolean"))
				t = CyAttributes.TYPE_BOOLEAN;
			else
				t = CyAttributes.TYPE_STRING;

			// data.initializeAttributeType( name, t );

		}

	}

	public String getSelectedAttribute() {
		return attributeList.getSelectedValue().toString();
	}

	public void valueChanged(ListSelectionEvent e) {

		try {

			if (e.getSource() == attributeList) {
				Object[] atts = attributeList.getSelectedValues();
				tableModel.setTableDataAttributes(Arrays.asList(atts));
			}

			if (e.getSource() == tagList) {
				String tag = tagList.getSelectedValue().toString();
				Set atts = AttributeTags.getAttributesByTag(data, tag);
				int[] indices = new int[atts.size()];

				int count = 0;
				for (Iterator i = atts.iterator(); i.hasNext();) {
					int ind = attributeList.getNextMatch((String) i.next(), 0,
							javax.swing.text.Position.Bias.Forward);
					indices[count] = ind;
					count++;
				}

				attributeList.setSelectedIndices(indices);

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void contentsChanged(ListDataEvent e) {
	}

	public void intervalAdded(ListDataEvent e) {
		// handleEvent(e);
	}

	public void intervalRemoved(ListDataEvent e) {
		// handleEvent(e);
	}

	public void propertyChange(PropertyChangeEvent e) {
		// updateLists();
	}
}
