package fr.pasteur.sysbio.rdfscape.help;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import fr.pasteur.sysbio.rdfscape.InteractivePanel;
import fr.pasteur.sysbio.rdfscape.RDFScape;

public class HelpManager {
	public static int panelWidth=650;
	public static int panelHeight=700;
	public JFrame getHelpPanelForInteractivePanel(InteractivePanel panel) {
		JFrame helpWindow=new JFrame("help for "+panel.getPanelName());
		
		
		int status=panel.getStatusLevel();
		final String link=panel.getHelpLink();
		JPanel content=panel.getHelpPanel();
		
		helpWindow.getContentPane().add(new JLabel(panel.getStatusMessage(),panel.getStatusIcon(),SwingConstants.LEFT) ,BorderLayout.NORTH);
		JScrollPane jScroll=new JScrollPane(content,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		helpWindow.getContentPane().add(jScroll,BorderLayout.CENTER);
		
		JButton linkButton=new JButton("Help on the web");
		helpWindow.getContentPane().add(linkButton,BorderLayout.SOUTH);
		class LinkListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				String osName = System.getProperty("os.name"); 
				try { 
					if (osName.startsWith("Mac OS")) { 
						Class fileMgr = Class.forName("com.apple.eio.FileManager"); 
						Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] {String.class}); 
						openURL.invoke(null, new Object[] {link});
					} 
					else if (osName.startsWith("Windows")) Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + link); 
					else { //assume Unix or Linux 
						String[] browsers = { "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" }; 
						String browser = null; 
						for (int count = 0; count < browsers.length && browser == null; count++) 
							if (Runtime.getRuntime().exec( new String[] {"which", browsers[count]}).waitFor() == 0) browser = browsers[count]; 
							if (browser == null) throw new Exception("Could not find web browser"); 
							else Runtime.getRuntime().exec(new String[] {browser, link});
						} 
				} 
				 catch (Exception ex) {
						RDFScape.warn("Unable to open a browser for you. Have a look at this address: "+link);
				 } 
			}
			
		}
		linkButton.addActionListener(new LinkListener());
		
		helpWindow.setSize(new Dimension(panelHeight,panelWidth)); // TODO maybe should be get from a Default Object
		
		
		return helpWindow;
	}
	

}
