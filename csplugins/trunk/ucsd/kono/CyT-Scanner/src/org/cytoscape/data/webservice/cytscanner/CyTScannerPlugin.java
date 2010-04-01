package org.cytoscape.data.webservice.cytscanner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.jdesktop.swingx.JXLoginDialog;
import org.jdesktop.swingx.auth.LoginService;

import twitter4j.TwitterException;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;

public class CyTScannerPlugin extends CytoscapePlugin {

	private CyTScanner scanner;
	
	private String id;
	private String pw;


	public CyTScannerPlugin() {
		final JMenu menu = new JMenu("CyT Scanner");
		final JMenuItem login = new JMenuItem(
				"Login to your Twitter account...");
		final JMenuItem start = new JMenuItem("Start");
		login.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				login();
			}

		});
		
		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				start();
			}

		});

		menu.add(login);
		menu.add(start);

		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins")
				.add(menu);
	}

	private void login() {
		// TODO Auto-generated method stub
		JXLoginDialog dialog = new JXLoginDialog(new LoginService() {
			public boolean authenticate(String name, char[] password,
					String server) throws Exception {
				System.out.println("NAME = " + name);
				System.out.println("PW = " + String.valueOf(password));
				id = name;
				pw = String.valueOf(password);
				try {
				scanner = new CyTScanner(id, pw);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		}, null, null);
		
		dialog.setTitle("Login to your Twitter Account");
		dialog.setVisible(true);
	}
	
	private  void start() {
		try {
			scanner.start();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
