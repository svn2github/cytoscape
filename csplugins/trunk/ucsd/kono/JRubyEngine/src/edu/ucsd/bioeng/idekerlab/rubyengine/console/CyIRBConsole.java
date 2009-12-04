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
package edu.ucsd.bioeng.idekerlab.rubyengine.console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.internal.runtime.ValueAccessor;

import cytoscape.Cytoscape;
import cytoscape.view.cytopanels.CytoPanelState;
import edu.ucsd.bioeng.idekerlab.rubyengine.RubyEnginePlugin;

/**
 * Ruby Console for Cytoscape based on demo code in JRuby distribution.
 * 
 * @author Keiichiro Ono
 */
public class CyIRBConsole extends JPanel {
	private static CyIRBConsole console = null;
	private static Ruby runtime = null;
	private static final ExecutorService consoleService = Executors
			.newCachedThreadPool();
	private static final String BIORUBY_SCRIPT_LOCATION = "/utilscripts/biorubyshell.rb";
	private static String startScript;

	private static final ImageIcon tabIcon = new ImageIcon(
			RubyEnginePlugin.class.getResource("/images/ruby22x22.png"));

	/**
	 * Creates a new CyIRBConsole object.
	 * 
	 * @param title
	 *            DOCUMENT ME!
	 * @throws IOException
	 */
	public CyIRBConsole(String title) throws IOException {
		// super(title);

		InputStreamReader in = null;
		StringBuilder builder = new StringBuilder();

		in = new InputStreamReader(RubyEnginePlugin.class.getResource(
				BIORUBY_SCRIPT_LOCATION).openStream());

		BufferedReader br = new BufferedReader(in);

		String line;

		while ((line = br.readLine()) != null) {
			builder.append(line + "\n");
		}

		br.close();
		in.close();

		br = null;
		in = null;

		startScript = builder.toString();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws IOException
	 */
	public static void showConsole() throws IOException {
		if (console == null) {
			// Initialize console
			if (runtime == null)
				runtime = Ruby.newInstance();

			console = new CyIRBConsole("Cytoscape-Ruby Interactive Console");
			buildConsole(null);

			Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).add(
					"Cytoscape-BioRuby Console", tabIcon, console);

			Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).setState(
					CytoPanelState.DOCK);
		}

		consoleService.execute(new Runnable() {
			public void run() {
				runtime.evalScriptlet(startScript);

				Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST)
						.remove(console);
				if (Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST)
						.getCytoPanelComponentCount() == 0)
					Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST)
							.setState(CytoPanelState.HIDE);
				runtime.tearDown();
				console = null;
				System.out.println("Console terminated!");
			}
		});

	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param args
	 *            DOCUMENT ME!
	 */
	public static void buildConsole(final String[] args) {
		console.setLayout(new BorderLayout());
		console.setPreferredSize(new Dimension(500, 600));

		final JEditorPane text = new JTextPane();
		text.setMargin(new Insets(8, 8, 8, 8));
		text.setCaretColor(new Color(0xa4, 0x00, 0x00));
		text.setBackground(Color.white);
		text.setForeground(Color.red);

		final Font font = console.findFont("Monospaced", Font.PLAIN, 14,
				new String[] { "Monaco", "Andale Mono" });
		text.setFont(font);

		final JScrollPane pane = new JScrollPane();
		pane.setViewportView(text);
		pane.setBorder(BorderFactory.createLineBorder(Color.darkGray));
		console.add(pane);
		console.validate();

		final TextAreaReadLine2 tar = new TextAreaReadLine2(text,
				" Welcome to CyRuby Console \n"
						+ " Starting BioRuby Shell.  Please wait...\n\n");

		final RubyInstanceConfig config = new RubyInstanceConfig() {

			{
				setInput(tar.getInputStream());
				setOutput(new PrintStream(tar.getOutputStream()));
				setError(new PrintStream(tar.getOutputStream()));
				setObjectSpaceEnabled(true); // useful for code completion
				// inside the IRB
				// setArgv(args);
			}
		};

		runtime = Ruby.newInstance(config);

		runtime.getGlobalVariables().defineReadonly(
				"$$",
				new ValueAccessor(runtime.newFixnum(System
						.identityHashCode(runtime))));
		runtime.getLoadService().init(new ArrayList());

		tar.hookIntoRuntime(runtime);

	}

	private Font findFont(String otherwise, int style, int size,
			String[] families) {
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAvailableFontFamilyNames();
		Arrays.sort(fonts);

		Font font = null;

		for (int i = 0; i < families.length; i++) {
			if (Arrays.binarySearch(fonts, families[i]) >= 0) {
				font = new Font(families[i], style, size);

				break;
			}
		}

		if (font == null)
			font = new Font(otherwise, style, size);

		return font;
	}
}
