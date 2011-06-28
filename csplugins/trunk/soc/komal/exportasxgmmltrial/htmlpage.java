package exportasxgmmltrial;

import java.io.*;
import java.lang.*;
import java.util.*;

public class htmlpage extends ExportToCytoscapeWeb{

    private Formatter x;
    String nameofwebpage = "C:/Users/Vampire/Documents/NetBeansProjects/ExportAsXGMMLTrial/src/exportasxgmmltrial/index.html";
   // String nameofwebpage = HtmlPath;
    //String nameofwebpage = "C:/Users/Vampire/Desktop/trial13.html";


    public void openFile() {
        try {
            x = new Formatter(nameofwebpage);
        } catch (Exception e) {
            System.out.println("ERROR");
        }
    }

    public void addRecords(String a) {
        x.format("%s", a);
    }

    public String completeurl() {
        return nameofwebpage;
    }

    public void closeFile() {
        x.close();
    }
}
