package exportasxgmmltrial;

import java.net.*;
import java.net.URL;
import java.io.*;
import java.io.File;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        String newname = "C:\\Users\\Vampire\\Documents\\NetBeansProjects\\ExportAsXGMMLTrial\\src\\exportasxgmmltrial\\Man.xgmml";
        String newstr = new File(newname).toURI().toString();
        String nstr = newstr.replace("file:/", "file:///");
        System.out.println(nstr);
    }
}

