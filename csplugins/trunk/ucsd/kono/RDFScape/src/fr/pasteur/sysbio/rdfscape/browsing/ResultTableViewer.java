/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.browsing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import fr.pasteur.sysbio.rdfscape.query.AbstractQueryResultTable;

public class ResultTableViewer extends JTable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResultTableViewer() {
		super();
		
		setDefaultRenderer(String.class,new MyTableCellRenderer());
		
	}
	
	public ResultTableViewer(AbstractQueryResultTable queryResult) {
		super(queryResult);
		
		setDefaultRenderer(String.class,new MyTableCellRenderer());
	}

	public class MyTableCellRenderer  implements TableCellRenderer {

		public Component getTableCellRendererComponent(JTable table, Object arg1, boolean isSelected, boolean hasFocus, int x, int y) {
			//System.out.println("Rendering "+x+","+y+":"+isSelected);
			JLabel rended=new JLabel();
			rended.setOpaque(true);
			if(isSelected==false) rended.setBackground(Color.WHITE);
			else rended.setBackground(Color.BLUE);
			Color colorToSet=((AbstractQueryResultTable)(table.getModel())).getColor(x,y);
			if(colorToSet.equals(Color.BLUE) && isSelected==true) rended.setForeground(Color.WHITE);
			else rended.setForeground(colorToSet);
			if(((AbstractQueryResultTable)(table.getModel())).isURI(x,y)) {
				rended.setText(((AbstractQueryResultTable)(table.getModel())).getLabel(x,y));
			}
			if(((AbstractQueryResultTable)(table.getModel())).isLiteral(x,y)) {
				rended.setText(((AbstractQueryResultTable)(table.getModel())).getLabel(x,y));
			}
			if(((AbstractQueryResultTable)(table.getModel())).isBlank(x,y)) {
				rended.setText(((AbstractQueryResultTable)(table.getModel())).getLabel(x,y));
			}
			//System.out.println("F :"+((AbstractQueryResultTable)(table.getModel())).getColor(x,y)+" - "+((AbstractQueryResultTable)(table.getModel())).getLabel(x,y));
			return rended;
		}
		
	}

}
