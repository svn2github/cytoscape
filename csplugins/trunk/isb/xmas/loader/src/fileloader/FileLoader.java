package fileloader;

import javax.swing.*;
import cytoscape.*;
import java.io.*;

public class FileLoader {

  public static void load () {
    
    File  file;
    File currentDirectory = Cytoscape.getCytoscapeObj().getCurrentDirectory();
    JFileChooser chooser = new JFileChooser(currentDirectory);
    if ( chooser.showOpenDialog( Cytoscape.getDesktop() ) == 
         chooser.APPROVE_OPTION) {
      currentDirectory = chooser.getCurrentDirectory();
      Cytoscape.getCytoscapeObj().setCurrentDirectory(currentDirectory);
      file = chooser.getSelectedFile();
    }

    



  }

  public static void load ( String file_name ) {

  }

}
