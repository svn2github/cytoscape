/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package csplugins.id.mapping.ui;

import java.awt.Component;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.DefaultListCellRenderer;

import java.util.List;
import java.util.Vector;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

/**
 * ComboBox containing checkbox
 * @author gjj
 */
class CheckComboBox extends JComboBox {
   private List<ObjCheckBox> cbs;
   private Map<Object, Boolean> mapObjSelected;

   public CheckComboBox(final Set objs) {
       resetObjs(objs);
   }

   public CheckComboBox(final Set objs, final Set selected) {
       mapObjSelected = new HashMap();
       for (Object obj : objs) {
           mapObjSelected.put(obj, selected.contains(obj));
       }

       reset();
   }


   public CheckComboBox(Map<Object, Boolean> mapObjSelected) {
       this.mapObjSelected = mapObjSelected;
       reset();
   }

   public void resetObjs(final Set objs) {
       mapObjSelected = new HashMap();
       for (Object obj : objs) {
           mapObjSelected.put(obj, false);
       }

       reset();
   }

   @Override
   public Set getSelectedItem() {
       Set ret = new HashSet();
       for (Map.Entry<Object,Boolean> entry : mapObjSelected.entrySet()) {
            Object obj = entry.getKey();
            Boolean selected = entry.getValue();

            if (selected) {
                ret.add(obj);
            }
        }
       
       return ret;
   }

   private void reset() {
       initCBs();

       this.addItem(new String());
       for (JCheckBox cb : cbs) {
           this.addItem(cb);
       }

       setRenderer(new CheckBoxRenderer(cbs));
       addActionListener(this);
   }

   private void initCBs() {
            cbs = new Vector<ObjCheckBox>();

            boolean selectedAll = false;
            boolean selectedNone = true;

            ObjCheckBox cb;
            for (Map.Entry<Object,Boolean> entry : mapObjSelected.entrySet()) {
                Object obj = entry.getKey();
                Boolean selected = entry.getValue();

                if (selected) {
                    selectedNone = false;
                } else {
                    selectedAll = false;
                }

                cb = new ObjCheckBox(obj);
                cb.setSelected(selected);
                cbs.add(cb);
            }

            cb = new ObjCheckBox("Select all");
            cb.setSelected(selectedAll);
            cbs.add(cb);

            cb = new ObjCheckBox("Select none");
            cb.setSelected(selectedNone);
            cbs.add(cb);
    }

    private void checkBoxSelectionChanged(int index) {
            int n = cbs.size();
            if (index<0 || index>=n) return;

            //Set selectedObj = getSelected();
            if (index<n-2) {
                ObjCheckBox cb = cbs.get(index);
                if (cb.isSelected()) {
                    cb.setSelected(false);
                    mapObjSelected.put(cb.getObj(), false);

                    cbs.get(n-2).setSelected(false); //Select all
                    cbs.get(n-1).setSelected(getSelectedItem().isEmpty());
                } else {
                    cb.setSelected(true);
                    mapObjSelected.put(cb.getObj(), true);

                    cbs.get(n-2).setSelected(getSelectedItem().size()==mapObjSelected.size()); // Select all
                    cbs.get(n-1).setSelected(false);
                }
            } else if (index==n-2) {
                for (Object obj : mapObjSelected.keySet()) {
                    mapObjSelected.put(obj, true);
                }

                for (int i=0; i<n-1; i++) {
                    cbs.get(i).setSelected(true);
                }
                cbs.get(n-1).setSelected(false);
            } else { // if (index==n-1)
                for (Object obj : mapObjSelected.keySet()) {
                    mapObjSelected.put(obj, false);
                }

                for (int i=0; i<n-1; i++) {
                        cbs.get(i).setSelected(false);
                }
                cbs.get(n-1).setSelected(true);
            }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
            int sel = getSelectedIndex();

            if (sel == 0) {
                    getUI().setPopupVisible(this, false);
            } else if (sel > 0) {
                    checkBoxSelectionChanged(sel-1);
            }

            this.setSelectedIndex(-1); // clear selection
    }

    @Override
    public void setPopupVisible(boolean flag)
    {
            //TODO this not work, fix it
            // Not code here prevents the populist from closing
    }

    // checkbox renderer for combobox
    class CheckBoxRenderer implements ListCellRenderer {
        private final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
        private final List<ObjCheckBox> cbs;
        //private final Set objs;

        public CheckBoxRenderer(final List<ObjCheckBox> cbs) {
            //setOpaque(true);
            this.cbs = cbs;
            //this.objs = objs;
        }

        //@Override
        public Component getListCellRendererComponent(
                                JList list,
                                Object value,
                                int index,
                                boolean isSelected,
                                boolean cellHasFocus) {
            if (index > 0) {
                    JCheckBox cb = cbs.get(index-1);
                    cb.setBackground(isSelected ? Color.blue : Color.white);
                    cb.setForeground(isSelected ? Color.white : Color.black);

                    return cb;
            }

            return defaultRenderer.getListCellRendererComponent(list, getSelectedItem().toString(), index, isSelected, cellHasFocus);
        }
    }

    class ObjCheckBox extends JCheckBox {
        private final Object obj;
        public ObjCheckBox(final Object obj) {
            super(obj.toString());
            this.obj = obj;
        }

        public Object getObj() {
            return obj;
        }
    }
}


