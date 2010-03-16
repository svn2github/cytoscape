package cytoscape.actions;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.visual.customgraphic.CustomGraphicsUtil;

public class ImportCustomGraphicsLocationListAction extends CytoscapeAction {
	
	private static final long serialVersionUID = -8744383285698760601L;

	/**
	 * Creates a new ListFromFileSelectionAction object.
	 */
	public ImportCustomGraphicsLocationListAction() {
		super("From File...");
		setPreferredMenu("File.Import.Custom Graphics");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		buildFromFile();
	}

	private void buildFromFile() {
		String fileName = null;

		try {
			fileName = FileUtil.getFile("Load Custom Graphics Location File", FileUtil.LOAD).toString();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		
		if(fileName == null) return;

		final Set<String> customGraphicsUrlList = new HashSet<String>();

		try {
			BufferedReader bin = null;

			try {
				bin = new BufferedReader(new FileReader(fileName));

				String s;

				while ((s = bin.readLine()) != null) {
					String trimName = s.trim();

					if (trimName.length() > 0)
						customGraphicsUrlList.add(trimName);
				}
			}
			finally {
				if (bin != null) {
					bin.close();
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.toString(), "Error Reading \"" + fileName + "\"",
			                              JOptionPane.ERROR_MESSAGE);
		}

		if (customGraphicsUrlList.size() == 0) {
			JOptionPane.showMessageDialog(null, "No image location was found in \"" + fileName + "\"!", "Warning!",
						      JOptionPane.WARNING_MESSAGE);
		}

		CustomGraphicsUtil.generateCustomGraphics(customGraphicsUrlList);
		
	}
}