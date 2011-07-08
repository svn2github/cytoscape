package org.cytoscape.komal;

import java.net.URI;
import java.awt.Desktop;

public class OpenBrowser {

    public static void main(String[] args) {

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        for (String arg : args) {
            try {
                java.net.URI uri = new java.net.URI(arg);
                desktop.browse(uri);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
