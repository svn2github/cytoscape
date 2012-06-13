/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cytoscape.functional.menus;

import static junit.framework.Assert.*;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.openengsb.labs.paxexam.karaf.options.LogLevelOption.LogLevel;
import org.openengsb.labs.paxexam.karaf.options.KarafDistributionConfigurationFilePutOption; 

import org.cytoscape.application.swing.CySwingApplication;

import org.uispec4j.UISpec4J;
import org.uispec4j.Window;
import org.uispec4j.MenuItem;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class BasicMenuTest {

	// Inject any other desired services in the same way.
	@Inject
	private CySwingApplication swingApp;

    @Configuration
    public Option[] config() {
		// Very important to initialize uispec4j before any guis are created.
		UISpec4J.init(); 

        return new Option[] {
            karafDistributionConfiguration("mvn:org.apache.karaf/apache-karaf/2.2.7/zip", "karaf", "2.2.7"),

			// These custom properties are the same as the custom properties 
			// found in framework/etc/custom.properties and are needed to
			// get the startlevels right.
			customProperty("karaf.systemBundlesStartLevel","99"),
			customProperty("karaf.startlevel.bundle","200"),
			customProperty("org.osgi.framework.startlevel.beginning","200"),
			customProperty("org.osgi.framework.system.packages.extra",
				"org.apache.karaf.branding,org.cytoscape.launcher.internal,com.sun.xml.internal.bind,com.apple.eawt"),

			// This ensures that the pax-exam probe starts at the proper start level.
			useOwnExamBundlesStartLevel(200),

			// The actual features file we're loading.
            scanFeatures( 
				maven().groupId("org.cytoscape.distribution").artifactId("features").type("xml")
					.classifier("features").version("3.0.0-M5-SNAPSHOT"), "cytoscape-gui").start(),

			// Load an OSGi-ified uispec4j jar - this makes the jar availabe to pax-exam,
			// and thus the tests below.
			mavenBundle().groupId("cytoscape-temp").artifactId("uispec4j").type("jar").versionAsInProject(),
		};
    }

	// Utility method for setting custom properties.
	private static KarafDistributionConfigurationFilePutOption customProperty(String key, String value) {
		return new KarafDistributionConfigurationFilePutOption("etc/custom.properties",key,value);
	}


	// Each @Test starts a new instance of Cytoscape, so it's probably
	// a good idea to do as many tests as you can on a single instance
	// rather than restarting Cytoscape a bunch of times with separate
	// @Test methods.

    @Test
    public void test() throws Exception {
        assertNotNull(swingApp);
		Window mainWindow = new Window(swingApp.getJFrame());
		checkTopLevelMenus(mainWindow);
		// more tests...
	}

	private void checkTopLevelMenus(Window mainWindow) {
		checkFileMenus(   mainWindow.getMenuBar().getMenu("File"));
		checkEditMenus(   mainWindow.getMenuBar().getMenu("Edit"));
		checkViewMenus(   mainWindow.getMenuBar().getMenu("View"));
		checkSelectMenus( mainWindow.getMenuBar().getMenu("Select"));
		checkLayoutMenus( mainWindow.getMenuBar().getMenu("Layout"));
		checkAppsMenus(   mainWindow.getMenuBar().getMenu("Apps"));
		checkToolsMenus(  mainWindow.getMenuBar().getMenu("Tools"));
		checkHelpMenus(   mainWindow.getMenuBar().getMenu("Help"));
    }

	private void checkFileMenus(MenuItem fileMenu) {
		assertNotNull(fileMenu);
		assertNotNull(fileMenu.getSubMenu("Recent Session"));
		assertNotNull(fileMenu.getSubMenu("New"));
		assertNotNull(fileMenu.getSubMenu("Open"));
		assertNotNull(fileMenu.getSubMenu("Save"));
		assertNotNull(fileMenu.getSubMenu("Save As"));
		assertNotNull(fileMenu.getSubMenu("Import"));
		assertNotNull(fileMenu.getSubMenu("Export"));
		assertNotNull(fileMenu.getSubMenu("Print Current Network..."));
	}

	private void checkEditMenus(MenuItem editMenu) {
		assertNotNull(editMenu);
		assertNotNull(editMenu.getSubMenu("Copy"));
		assertNotNull(editMenu.getSubMenu("Cut"));
		assertNotNull(editMenu.getSubMenu("Paste"));
		assertNotNull(editMenu.getSubMenu("Undo"));
		assertNotNull(editMenu.getSubMenu("Redo"));
		assertNotNull(editMenu.getSubMenu("Create View"));
		assertNotNull(editMenu.getSubMenu("Destroy View"));
		assertNotNull(editMenu.getSubMenu("Destroy Network"));
		assertNotNull(editMenu.getSubMenu("Remove Duplicated Edges"));
		assertNotNull(editMenu.getSubMenu("Remove Self-Loops"));
		assertNotNull(editMenu.getSubMenu("Delete Selected Nodes and Edges..."));
		assertNotNull(editMenu.getSubMenu("Rename Network..."));
		assertNotNull(editMenu.getSubMenu("Preferences"));
		assertNotNull(editMenu.getSubMenu("Connect Selected Nodes"));
	}

	private void checkViewMenus(MenuItem viewMenu) {
		assertNotNull(viewMenu);
		assertNotNull(viewMenu.getSubMenu("Hide Control Panel"));
		assertNotNull(viewMenu.getSubMenu("Hide Table Panel"));
		assertNotNull(viewMenu.getSubMenu("Show Results Panel"));
		assertNotNull(viewMenu.getSubMenu("Show Tool Panel"));
		assertNotNull(viewMenu.getSubMenu("Show Graphics Details"));
		assertNotNull(viewMenu.getSubMenu("Show All Visual Properties"));
		assertNotNull(viewMenu.getSubMenu("Open Custom Graphics Manager"));
		assertNotNull(viewMenu.getSubMenu("Arrange Network Windows"));
	}

	private void checkSelectMenus(MenuItem selectMenu) {
		assertNotNull(selectMenu);
		assertNotNull(selectMenu.getSubMenu("Nodes"));
		assertNotNull(selectMenu.getSubMenu("Edges"));
		assertNotNull(selectMenu.getSubMenu("Show all nodes and edges"));
		assertNotNull(selectMenu.getSubMenu("Hide selected nodes and edges"));
		assertNotNull(selectMenu.getSubMenu("Select all nodes and edges"));
		assertNotNull(selectMenu.getSubMenu("Deselect all nodes and edges"));
		assertNotNull(selectMenu.getSubMenu("Use Filters"));
	}

	private void checkLayoutMenus(MenuItem layoutMenu) {
		assertNotNull(layoutMenu);
		assertNotNull(layoutMenu.getSubMenu("Bundle Edges"));
		assertNotNull(layoutMenu.getSubMenu("Rotate"));
		assertNotNull(layoutMenu.getSubMenu("Scale"));
		assertNotNull(layoutMenu.getSubMenu("Align and Distribute"));
		assertNotNull(layoutMenu.getSubMenu("Settings..."));
		assertNotNull(layoutMenu.getSubMenu("Apply Preferred Layout"));
		assertNotNull(layoutMenu.getSubMenu("Cytoscape Layouts"));
		assertNotNull(layoutMenu.getSubMenu("Prefuse Layouts"));
		assertNotNull(layoutMenu.getSubMenu("yFiles Layouts"));
	}

	private void checkAppsMenus(MenuItem appsMenu) {
		assertNotNull(appsMenu);
		assertNotNull(appsMenu.getSubMenu("App Manager"));
	}

	private void checkToolsMenus(MenuItem toolsMenu) {
		assertNotNull(toolsMenu);
		assertNotNull(toolsMenu.getSubMenu("Network Analyzer"));
		assertNotNull(toolsMenu.getSubMenu("Run script..."));
		assertNotNull(toolsMenu.getSubMenu("SIF Interpreter"));
		assertNotNull(toolsMenu.getSubMenu("Run Commands..."));
		assertNotNull(toolsMenu.getSubMenu("List All Commands..."));
		assertNotNull(toolsMenu.getSubMenu("Merge Networks"));
	}

	private void checkHelpMenus(MenuItem helpMenu) {
		assertNotNull(helpMenu);
		assertNotNull(helpMenu.getSubMenu("Contents..."));
		assertNotNull(helpMenu.getSubMenu("About..."));
		assertNotNull(helpMenu.getSubMenu("Contact Help Desk..."));
		assertNotNull(helpMenu.getSubMenu("Report a Bug..."));
		assertNotNull(helpMenu.getSubMenu("Log Console"));
		assertNotNull(helpMenu.getSubMenu("Show Welcome Screen..."));
	}
}
