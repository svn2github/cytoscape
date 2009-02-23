
package org.cytoscape.work.util;

import cytoscape.CyOperatingContext;

import cytoscape.task.TaskMonitor;

import org.cytoscape.io.CyFileFilter;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;

import org.cytoscape.io.read.URLUtil;

import cytoscape.util.FileUtil;


class FileUtilImpl implements FileUtil {

	CyOperatingContext context;

	FileUtilImpl(CyOperatingContext context) {
		this.context = context;
	}

	/**
	 * {@inheritDoc}
	 */
	public File getFile(String title, int load_save_custom) {
		return getFile(title, load_save_custom, new CyFileFilter[] { }, null, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public File getFile(String title, int load_save_custom, CyFileFilter[] filters) {
		return getFile(title, load_save_custom, filters, null, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public File getFile(String title, int load_save_custom, CyFileFilter[] filters,
	                           String start_dir, String custom_approve_text) {
		File[] result = getFiles(title, load_save_custom, filters, start_dir, custom_approve_text,
		                         false);

		return ((result == null) || (result.length <= 0)) ? null : result[0];
	}


	/**
	 * {@inheritDoc}
	 */
    public File[] getFiles(Component parent, String title, int load_save_custom, CyFileFilter[] filters) {
        return getFiles(parent,title, load_save_custom, filters, null, null, true);
    }
  

	/**
	 * {@inheritDoc}
	 */
	public File[] getFiles(String title, int load_save_custom, CyFileFilter[] filters,
	                              String start_dir, String custom_approve_text) {
		 return getFiles(null, title, load_save_custom, filters, start_dir, custom_approve_text, true);
	}
	 
	/**
	 * {@inheritDoc}
	 */
	public File[] getFiles(String title, int load_save_custom, CyFileFilter[] filters,
          String start_dir, String custom_approve_text, boolean multiselect) {
		return getFiles(null, title, load_save_custom, filters, start_dir, custom_approve_text, multiselect);
	}

	/**
	 * {@inheritDoc}
	 */
	public File[] getFiles(Component parent, String title, int load_save_custom, CyFileFilter[] filters,
	                              String start_dir, String custom_approve_text, boolean multiselect) {

		if (parent == null) 
		 	throw new NullPointerException("Parent component is null");	

		File start = null;

		if (start_dir == null) {
			start = context.getMRUD();
		} else {
			start = new File(start_dir);
		}

		String osName = System.getProperty("os.name");

		//System.out.println( "Os name: "+osName );
		if (osName.startsWith("Mac")) {
			// this is a Macintosh, use the AWT style file dialog
			FileDialog chooser = new FileDialog((Frame)parent, title, load_save_custom);

			// we can only set the one filter; therefore, create a special
			// version of CyFileFilter that contains all extensions
			// TODO fix this so we actually use the filters we're given
//			CyFileFilter fileFilter = new CyFileFilter(new String[]{},new String[]{},"All network files");

//			chooser.setFilenameFilter(fileFilter);

			chooser.setVisible(true);

			if (chooser.getFile() != null) {
				File[] result = new File[1];
				result[0] = new File(chooser.getDirectory() + "/" + chooser.getFile());

				if (chooser.getDirectory() != null) {
					context.setMRUD(new File(chooser.getDirectory()));
				}

				return result;
			}

			return null;
		} else {
			// this is not a mac, use the Swing based file dialog
			JFileChooser chooser = new JFileChooser(start);

			// set multiple selection, if applicable
			chooser.setMultiSelectionEnabled(multiselect);

			// set the dialog title
			chooser.setDialogTitle(title);

			// add filters
			for (int i = 0; i < filters.length; ++i) {
				chooser.addChoosableFileFilter(filters[i]);
			}

			File[] result = null;
			File tmp = null;

			// set the dialog type
			if (load_save_custom == LOAD) {
				if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
					if (multiselect)
						result = chooser.getSelectedFiles();
					else if ((tmp = chooser.getSelectedFile()) != null) {
						result = new File[1];
						result[0] = tmp;
					}
				}
			} else if (load_save_custom == SAVE) {
				if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
					if (multiselect)
						result = chooser.getSelectedFiles();
					else if ((tmp = chooser.getSelectedFile()) != null) {
						result = new File[1];
						result[0] = tmp;
					}
					// FileDialog checks for overwrte, but JFileChooser does not, so we need to do
					// so ourselves
					for (int i = 0; i < result.length; i++) {
						if (result[i].exists()) {
							int answer = JOptionPane.showConfirmDialog(chooser, 
							   "The file '"+result[i].getName()+"' already exists, are you sure you want to overwrite it?",
							   "File exists", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
							if (answer == 1) 
								return null;
						}
					}
				}
			} else {
				if (chooser.showDialog(parent, custom_approve_text) == JFileChooser.APPROVE_OPTION) {
					if (multiselect)
						result = chooser.getSelectedFiles();
					else if ((tmp = chooser.getSelectedFile()) != null) {
						result = new File[1];
						result[0] = tmp;
					}
				}
			}

			if ((result != null) && (start_dir == null))
				context.setMRUD(chooser.getCurrentDirectory());

			return result;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public InputStream getInputStream(String name) {
		return getInputStream(name, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public InputStream getInputStream(String name, TaskMonitor monitor) {
		InputStream in = null;

		try {
			if (name.matches(urlPattern)) {
				URL u = new URL(name);
				// in = u.openStream();
                // Use URLUtil to get the InputStream since we might be using a proxy server 
				// and because pages may be cached:
				in = URLUtil.getBasicInputStream(u);
			} else
				in = new FileInputStream(name);
		} catch (IOException ioe) {
			ioe.printStackTrace();

			if (monitor != null)
				monitor.setException(ioe, ioe.getMessage());
		}

		return in;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getInputString(String filename) {
		try {
			InputStream stream = getInputStream(filename);
			return getInputString(stream);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		System.out.println("couldn't create string from '" + filename + "'");

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getInputString(InputStream inputStream) throws IOException {
		String lineSep = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();
		String line = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

		while ((line = br.readLine()) != null)
			sb.append(line + lineSep);

		return sb.toString();
	}
}
