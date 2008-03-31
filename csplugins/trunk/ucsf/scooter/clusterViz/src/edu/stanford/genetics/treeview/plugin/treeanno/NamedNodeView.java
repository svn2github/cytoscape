/*
 * Created on Mar 13, 2005
 *
 * Copyright Alok Saldnaha, all rights reserved.
 */
package edu.stanford.genetics.treeview.plugin.treeanno;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.util.Observable;

import javax.swing.*;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.stanford.genetics.treeview.*;
import edu.stanford.genetics.treeview.ModelView;

/**
 * This class displays a selected list of all nodes that have annotations in the NAME column.
 */
public class NamedNodeView extends ModelView {
	/**
	 * This class represents the list of nodes with NAME annotations.
	 */
	public class AnnotatedListModel extends AbstractListModel {
		int [] annotated = new int[0];
		/* (non-Javadoc)
		 * @see javax.swing.ListModel#getSize()
		 */
		public int getSize() {
			return annotated.length;
		}

		/* (non-Javadoc)
		 * @see javax.swing.ListModel#getElementAt(int)
		 */
		public Object getElementAt(int index) {
			return headerInfo.getHeader(annotated[index], "NAME");
		}

		/**
		 * @param list indexes that have annotation
		 */
		public void setAnnotated(int[] list) {
			annotated = list;
			fireContentsChanged(this, 0, list.length);
		}

		/**
		 * @param selected
		 * @return
		 */
		public String getId(int listIndex) {
			return headerInfo.getHeader(annotated[listIndex], 0);
		}
		public int getListIndex(int headerIndex) {
			for (int i = 0; i < annotated.length; i++) {
				if (annotated[i] == headerIndex)
					return i;
			}
			return -1;
		}
	}
	private TreeSelectionI selection;
	private HeaderInfo headerInfo;
	private AnnotatedListModel nodeListModel = new AnnotatedListModel();
	private JList nodeList = new JList(nodeListModel);

	public void setSelection(TreeSelectionI sel) {
		if (selection != null) {
			selection.deleteObserver(this);	
		}
		selection = sel;
		selection.addObserver(this);
		if (selection != null) {
			update(selection, null);
		}
	}
	
	public String viewName() {
		return "Annotated Nodes";
	}

	protected void updateBuffer(Graphics g) {
		// no buffer here
	}
	/**
	 * @param nodeInfo
	 */
	public NamedNodeView(HeaderInfo nodeInfo) {
		headerInfo = nodeInfo;
		headerInfo.addObserver(this);
		setLayout(new BorderLayout());
		rebuildNodeList();
		nodeList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int selected = nodeList.getSelectedIndex();
				if (selected >= 0) {
					String id = nodeListModel.getId(selected);
					selection.setSelectedNode(id);
					selection.notifyObservers();
				}
			}
		});
		add(new JScrollPane(nodeList), BorderLayout.CENTER);
	}
	public void update(Observable o, Object arg) {
		update((Object) o, arg);
	}
	public void update(Object o, Object arg) {
		if (o == selection) {
			String id = selection.getSelectedNode();
			if (id != null) {
				int index = headerInfo.getHeaderIndex(id);
				if (index >= 0) {
					int listIndex = nodeListModel.getListIndex(index);
					if (listIndex >= 0) {
						nodeList.setSelectedIndex(listIndex);
						return;
					}
				}
			}
			nodeList.clearSelection();
		} else if (o == headerInfo) {
			rebuildNodeList();
		}
	}

	/**
	 * rebuilds node list
	 */
	private void rebuildNodeList() {
		int n = 0;
		int nameIndex = headerInfo.getIndex("NAME");
		if (nameIndex >= 0) {
			for (int i =0 ; i < headerInfo.getNumHeaders(); i++) {
				String header = headerInfo.getHeader(i, nameIndex);
				if (header == null) continue;
				if (header.equals("")) continue;
				n++;
			}
			int [] list = new int[n];
			n=0;
			for (int i =0 ; i < headerInfo.getNumHeaders(); i++) {
				String header = headerInfo.getHeader(i, nameIndex);
				if (header == null) continue;
				if (header.equals("")) continue;
				list[n++] = i;
			}
			nodeListModel.setAnnotated(list);
		} else {
			nodeListModel.setAnnotated(new int[0]);
		}
	}
}
