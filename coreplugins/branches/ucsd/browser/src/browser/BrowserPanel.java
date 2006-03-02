package browser;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;

public class BrowserPanel extends JPanel implements ActionListener,
		MouseListener {

	private JToolBar jJToolBarBar = null;
	private JScrollPane jScrollPane = null;
	private JPopupMenu popup = null;

	private JMenuItem advancedMenuItem = null;
	private JMenu newAttributeSubMenu = null;
	private JMenuItem jMenuItem1 = null;
	private JMenuItem jMenuItem2 = null;
	private JMenuItem jMenuItem3 = null;
	private JMenuItem jMenuItem4 = null;
	private JPopupMenu attributePopupMenu = null;  //  @jve:decl-index=0:visual-constraint="108,273"
	private JButton jButton = null;
	private JList jList = null;
	private JScrollPane jScrollPane1 = null;
	
	private JSortTable attrTable;
	
	
	public BrowserPanel() {
		super();
		// TODO Auto-generated constructor stub
		initialize();
	}

	public BrowserPanel( JSortTable jst ) {
		super();
		
		this.attrTable = jst;
		// TODO Auto-generated constructor stub
		initialize();
	}
	
	public BrowserPanel(boolean arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
		initialize();
	}

	public BrowserPanel(LayoutManager arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
		initialize();
	}

	public BrowserPanel(LayoutManager arg0, boolean arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {

		addMouseListener(this);

		this.setLayout(new BorderLayout());
		this.setSize(600, 200);
		this.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
				"Node Attribute Browser",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
		this.add(getJJToolBarBar(), java.awt.BorderLayout.NORTH);
		this.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
		popup = getJPopupMenu();
		attributePopupMenu = getJPopupMenu2();
		
	}

	/**
	 * This method initializes jJToolBarBar
	 * 
	 * @return javax.swing.JToolBar
	 */
	private JToolBar getJJToolBarBar() {
		if (jJToolBarBar == null) {
			jJToolBarBar = new JToolBar();
			jJToolBarBar.setFloatable(false);
			jJToolBarBar.setOrientation(javax.swing.JToolBar.HORIZONTAL);
			jJToolBarBar.setPreferredSize(new java.awt.Dimension(200,22));
			jJToolBarBar.add(getJButton());
		}
		return jJToolBarBar;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(
					5, 5, 5, 5));
			jScrollPane.setPreferredSize(new java.awt.Dimension(500, 100));
			jScrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					// TODO
					// Auto-generated
					// Event stub
					// mouseClicked()

					System.out.println("X = " + e.getX() + ", Y = " + e.getY());
					if (javax.swing.SwingUtilities.isRightMouseButton(e)) {
						System.out.println("RIGHT");
						popup.show(e.getComponent(), e.getX(), e.getY());
					} 
					

				}
			});
			
			jScrollPane.add(attrTable);
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jPopupMenu
	 * 
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getJPopupMenu() {
		if (popup == null) {
			popup = new JPopupMenu();
			popup.add(getJMenu());
			popup.add(getJMenuItem1());
		}
		return popup;
	}

	/**
	 * This method initializes jMenuItem1
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItem1() {
		if (advancedMenuItem == null) {
			advancedMenuItem = new JMenuItem();
			advancedMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// actionPerformed()"); // TODO Auto-generated Event stub
					// actionPerformed()

					System.out.println(e.getActionCommand());
				}
			});

			advancedMenuItem.setActionCommand("New Attribute...");

			advancedMenuItem.setText("Open Advanced Function Window...");
		}
		return advancedMenuItem;
	}

	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

		if (javax.swing.SwingUtilities.isRightMouseButton(arg0)) {
			System.out.println("RIGHT");

			// 右クリック時の処理
		} else if (javax.swing.SwingUtilities.isMiddleMouseButton(arg0)) {
			// 中ボタンクリック時の処理
		} else if (javax.swing.SwingUtilities.isLeftMouseButton(arg0)) {
			System.out.println("LEFT");
			// 左クリック時の処理
		}

	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenu() {
		if (newAttributeSubMenu == null) {
			newAttributeSubMenu = new JMenu();
			newAttributeSubMenu.setText("Create New Attributes");
			newAttributeSubMenu.add(getJMenuItem12());
			newAttributeSubMenu.add(getJMenuItem22());
			newAttributeSubMenu.add(getJMenuItem3());
			newAttributeSubMenu.add(getJMenuItem4());
		}
		return newAttributeSubMenu;
	}

	/**
	 * This method initializes jMenuItem1	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem12() {
		if (jMenuItem1 == null) {
			jMenuItem1 = new JMenuItem();
			jMenuItem1.setText("New String Attribute");
		}
		return jMenuItem1;
	}

	/**
	 * This method initializes jMenuItem2	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem22() {
		if (jMenuItem2 == null) {
			jMenuItem2 = new JMenuItem();
			jMenuItem2.setText("New Integer Attribute");
		}
		return jMenuItem2;
	}

	/**
	 * This method initializes jMenuItem3	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem3() {
		if (jMenuItem3 == null) {
			jMenuItem3 = new JMenuItem();
			jMenuItem3.setText("New Floating Point Attribute");
		}
		return jMenuItem3;
	}

	/**
	 * This method initializes jMenuItem4	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem4() {
		if (jMenuItem4 == null) {
			jMenuItem4 = new JMenuItem();
			jMenuItem4.setText("New Boolean Attribute");
		}
		return jMenuItem4;
	}

	/**
	 * This method initializes jPopupMenu	
	 * 	
	 * @return javax.swing.JPopupMenu	
	 */
	private JPopupMenu getJPopupMenu2() {
		if (attributePopupMenu == null) {
			attributePopupMenu = new JPopupMenu();
			//attributePopupMenu.setPopupSize(new java.awt.Dimension(100,40));
			attributePopupMenu.add(getJScrollPane1());
		}
		return attributePopupMenu;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Select Attributes");
			jButton.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
			jButton.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					 // TODO Auto-generated Event stub mouseClicked()
					attributePopupMenu.show(e.getComponent(), e.getX(), e.getY());
				
					//System.out.println("mouseClicked()"); // TODO Auto-generated Event stub mouseClicked()
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJList() {
		if (jList == null) {
//			String[] testitems = new String[5];
//			testitems[0] = "Attribute A";
//			testitems[1] = "Attribute B";
//			testitems[2] = "Attribute C";
//			testitems[3] = "Attribute D";
//			testitems[4] = "Attribute E";
			
			String[] testitems = new String[100];
			for(int i = 0; i<100; i++) {
				testitems[i] = "Attribute " + i;
			}
			
			
			jList = new JList(testitems);
			jList.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
			jList.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			jList.setEnabled(true);
			//jList.setVisibleRowCount(3);
			jList.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
			jList.setVisible(true);
		}
		return jList;
	}

	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
			jScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			jScrollPane1.setPreferredSize(new java.awt.Dimension(100,200));
			jScrollPane1.setViewportView(getJList());
		}
		return jScrollPane1;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
