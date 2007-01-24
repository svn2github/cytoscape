package browser;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

public class SearchTablePanel extends JPanel {

	private final JTextField field;
	private final JComboBox combo = new JComboBox();  //  @jve:decl-index=0:visual-constraint="23,225"
	private final Vector model = new Vector();
	private int count = 0;
	public SearchTablePanel() {
		field = (JTextField) combo.getEditor().getEditorComponent();
		initModel();
		field.addKeyListener(new KeyAdapter() {
			public void keyReleased(final KeyEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						keyReleasedInCombo(e);
					}
				});
			}
		});
		Box box = Box.createVerticalBox();
		box.add(new JLabel("�"));
		box.add(new JLabel("aaa�"));
		box.add(new JLabel("�E�E�L�[�ŕ⊮"));
		box.add(new JLabel("�E���^�[���L�[�őI��or�ǉ�"));
		box.add(new JLabel("�E������͂Ō���|�b�v�A�b�v"));
		box.add(Box.createVerticalStrut(5));
		Box box2 = Box.createHorizontalBox();
		box2.add(box);
		box2.add(Box.createHorizontalStrut(100));
		setLayout(new BorderLayout());
		add(box2,   BorderLayout.CENTER);
		add(combo, BorderLayout.SOUTH);
		setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
	}

	private void keyReleasedInCombo(final KeyEvent ke) {
		String text = field.getText();
		int code = ke.getKeyCode();
		if(code==KeyEvent.VK_ENTER) {
			combo.hidePopup();
			if(!model.contains(text)) {
				model.addElement(text);
				Collections.sort(model);
				setModel(makeModel(text), text);
			}
		}else if(code==KeyEvent.VK_UP || code==KeyEvent.VK_DOWN) {
			combo.showPopup();
		}else if(code==KeyEvent.VK_RIGHT) {
			for(int i=0;i<model.size();i++) {
				String str = (String) model.elementAt(i);
				if(str.startsWith(text)) {
					combo.setSelectedIndex(-1);
					field.setText(str);
					return;
				}
			}
		}else if(code==KeyEvent.VK_ESCAPE) {
			combo.hidePopup();
		}else if(text.length()==0) {
			combo.hidePopup();
			setModel(new DefaultComboBoxModel(model), "");
			count = 0;
		}else{
			DefaultComboBoxModel mdl = makeModel(text);
			int tmpcount = mdl.getSize();
			if(tmpcount==0) {
				combo.hidePopup();
			}else if(tmpcount!=count) {
				setModel(mdl, text);
				combo.showPopup();
			}
			count = tmpcount;
		}
	}

	private void initModel() {
		model.addElement("aaaa");
		model.addElement("aaaabbb");
		model.addElement("aaaabbbcc");
		model.addElement("aaaabbbccddd");
		model.addElement("bbb1");
		model.addElement("bbb12");
		//Collections.sort(model);
		combo.setEditable(true);
		setModel(new DefaultComboBoxModel(model), "");
	}

	private void setModel(DefaultComboBoxModel mdl, String str) {
		combo.setModel(mdl);
		combo.setSelectedIndex(-1);
		field.setText(str);
	}

	private DefaultComboBoxModel makeModel(String text) {
		DefaultComboBoxModel mdl = new DefaultComboBoxModel();
		for(int i=0;i<model.size();i++) {
			String str = (String) model.elementAt(i);
			if(str.startsWith(text)) {
				mdl.addElement(str);
			}
		}
		return mdl;
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
	public static void createAndShowGUI() {
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch (Exception e) {
			throw new InternalError(e.toString());
		}
		final JFrame frame = new JFrame("@title@");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().add(new SearchTablePanel());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(300, 200);
	}

}
