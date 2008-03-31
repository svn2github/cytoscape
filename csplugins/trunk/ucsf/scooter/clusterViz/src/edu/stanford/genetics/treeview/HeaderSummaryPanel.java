/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: HeaderSummaryPanel.java,v $
 * $Revision: 1.9 $
 * $Date: 2005/12/05 05:27:53 $
 * $Name:  $
 *
 * This file is part of Java TreeView
 * Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
 *
 * This software is provided under the GNU GPL Version 2. In particular, 
 *
 * 1) If you modify a source file, make a comment in it containing your name and the date.
 * 2) If you distribute a modified version, you must do it under the GPL 2.
 * 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
 *
 * A full copy of the license can be found in gpl.txt or online at
 * http://www.gnu.org/licenses/gpl.txt
 *
 * END_HEADER 
 */
package edu.stanford.genetics.treeview;

import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/**
* enables editing of a headerSummary object.
*/
public class HeaderSummaryPanel extends JPanel  implements SettingsPanel,Observer {
		private HeaderInfo headerInfo;
	/** Setter for headerInfo */
	public void setHeaderInfo(HeaderInfo headerInfo) {
		if (this.headerInfo != null) this.headerInfo.deleteObserver(this);
		this.headerInfo = headerInfo;
		headerInfo.addObserver(this);
		synchronizeFrom();
	}
	/** Getter for headerInfo */
	public HeaderInfo getHeaderInfo() {
		return headerInfo;
	}
	
	
	
	private HeaderSummary headerSummary;
	/** Setter for headerSummary */
	public void setHeaderSummary(HeaderSummary headerSummary) {
		this.headerSummary = headerSummary;
		synchronizeFrom();
	}
	/** Getter for headerSummary */
	public HeaderSummary getHeaderSummary() {
		return headerSummary;
	}
	private JList headerList = new JList(new String [0]);
	/** Setter for headerList */
	public void setHeaderList(String [] headers) {
		if (headers == null) {
			headerList.setListData(new String [0]);
		} else {
			headerList.setListData(headers);
		}
	}
	/** Getter for headerList */
	public JList getHeaderList() {
		return headerList;
	}
	
	public void synchronizeFrom() {
			int [] included = getHeaderSummary().getIncluded();
			JList list = getHeaderList();
			if (list == null) return;
			list.clearSelection();
			for (int i = 0; i < included.length; i++) {
				int index = included[i];
				if ((index >=0) && (index < list.getModel().getSize())) {
					list.addSelectionInterval(index,index);
				}
			}
	}
	public void synchronizeTo() {
		getHeaderSummary().setIncluded(getHeaderList().getSelectedIndices());
	}
	
	public HeaderSummaryPanel(HeaderInfo headerInfo, HeaderSummary headerSummary) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.headerInfo = headerInfo;
		this.headerSummary = headerSummary;

		add(new JLabel("Headers to include"));
		setHeaderList(headerInfo.getNames());
		headerList.setVisibleRowCount(5);
		add(new JScrollPane(getHeaderList()));
		ListSelectionListener tmp = new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				synchronizeTo();
			}
		};
		getHeaderList().addListSelectionListener(tmp);
		synchronizeFrom();
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if (o == headerInfo) {
			setHeaderList(headerInfo.getNames());
			synchronizeFrom();
			repaint();
		} else {
			LogBuffer.println("HeaderSummaryPanel got update from unexpected observable " + o);
		}
	}
}