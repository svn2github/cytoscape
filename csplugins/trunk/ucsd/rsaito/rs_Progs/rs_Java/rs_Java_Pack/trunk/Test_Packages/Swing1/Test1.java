package Swing1;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JSlider;

public class Test1 extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JTextArea jTextArea1 = null;
	private JLabel jLabel1 = null;
	private JButton jButton1 = null;
	private JButton jButton2 = null;
	private JSlider jSlider1 = null;

	/**
	 * This is the default constructor
	 */
	public Test1() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		this.setTitle("JFrame");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("This-is-a-test");
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJTextArea1(), BorderLayout.CENTER);
			jContentPane.add(jLabel1, BorderLayout.NORTH);
			jContentPane.add(getJButton1(), BorderLayout.WEST);
			jContentPane.add(getJButton2(), BorderLayout.EAST);
			jContentPane.add(getJSlider1(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTextArea1	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea1() {
		if (jTextArea1 == null) {
			jTextArea1 = new JTextArea();
		}
		return jTextArea1;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
		}
		return jButton1;
	}

	/**
	 * This method initializes jButton2	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton();
		}
		return jButton2;
	}

	/**
	 * This method initializes jSlider1	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getJSlider1() {
		if (jSlider1 == null) {
			jSlider1 = new JSlider();
		}
		return jSlider1;
	}

}
