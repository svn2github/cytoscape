/*
 * $Archive: SourceJammer$
 * $FileName: JSortTableTest.java$
 * $FileID: 3987$
 *
 * Last change:
 * $AuthorName: Timo Haberkern$
 * $Date: 2005/12/13 00:42:44 $
 * $Comment: $
 *
 * $KeyWordsOff: $
 */
/*
=====================================================================

  JSortTableTest.java

  Created by Claude Duguay
  Copyright (c) 2002

=====================================================================
*/
package browser;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class JSortTableTest
  extends JPanel
{
  public JSortTableTest()
  {
    setLayout(new GridLayout());
    setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    setPreferredSize(new Dimension(400, 400));
    add(new JScrollPane(new JSortTable(makeModel())));
  }

  protected SortTableModel makeModel()
  {
    Vector data = new Vector();
    for (int i = 0; i < 3; i++)
    {
      Vector row = new Vector();
      for (int j = 0; j < 5; j++)
      {
        row.add(new Integer((int)(Math.random() * 256)));
      }
      data.add(row);
    }

    Vector names = new Vector();
    names.add("One");
    names.add("Two");
    names.add("Three");
    names.add("Four");
    names.add("Five");

    return new DefaultSortTableModel(data, names);
  }

  public static void main(String[] args)
  {
    JFrame frame = new JFrame("JSortTable Test");
    frame.getContentPane().setLayout(new GridLayout());
    frame.getContentPane().add(new JSortTableTest());
    frame.pack();
    frame.show();
  }
}
