package cytoscape.genomespace.ui;


import cytoscape.Cytoscape;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;


public final class UserNamePasswordDialog extends JDialog {
	private String userName = null;
	private String password = null;
	private final JLabel userNameLabel;
	private final JLabel passwordLabel;
	private final JTextField userNameTextField;
	private final JPasswordField passwordField;
	private final JButton loginButton;
	private final JButton cancelButton;

	public UserNamePasswordDialog() {
		super(Cytoscape.getDesktop(), "Genome Space Authentication", /* modal = */ true);

		final JPanel userNameAndPasswordPanel = new JPanel(new GridBagLayout());
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;

		userNameLabel = new JLabel("GS User name: ");
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		userNameAndPasswordPanel.add(userNameLabel, constraints);

		userNameTextField = new JTextField(20);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		userNameAndPasswordPanel.add(userNameTextField, constraints);

		passwordLabel = new JLabel("GS Password: ");
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		userNameAndPasswordPanel.add(passwordLabel, constraints);

		passwordField = new JPasswordField(20);
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		userNameAndPasswordPanel.add(passwordField, constraints);

		userNameAndPasswordPanel.setBorder(new LineBorder(Color.GRAY));

		final JPanel buttonPanel = new JPanel();
		loginButton = new JButton("Login");
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (userNameTextField.getText().trim().isEmpty())
					JOptionPane.showMessageDialog(UserNamePasswordDialog.this,
								      "Please enter a non-empty user name!",
								      "Error",
								      JOptionPane.ERROR_MESSAGE);
				else if (passwordField.getPassword().length == 0)
                                        JOptionPane.showMessageDialog(UserNamePasswordDialog.this,
                                                                      "Please enter a non-empty password!",
                                                                      "Error",
                                                                      JOptionPane.ERROR_MESSAGE);
				else {
					userName = userNameTextField.getText().trim();
					password = new String(passwordField.getPassword());
					dispose();
				}
			}
			});
		buttonPanel.add(loginButton);
		getRootPane().setDefaultButton(loginButton);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
			});
		buttonPanel.add(cancelButton);

		getContentPane().add(userNameAndPasswordPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
		
		pack();
		setResizable(false);
		setLocationRelativeTo(Cytoscape.getDesktop());
	}

	public String getUserName() { return userName; }
	public String getPassword() { return password; }
}