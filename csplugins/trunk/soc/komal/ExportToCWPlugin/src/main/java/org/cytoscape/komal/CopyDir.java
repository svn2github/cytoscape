package org.cytoscape.komal;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import cytoscape.logger.CyLogger;

public class CopyDir extends ExportToCWPlugin{

     public static void main(String[] args)
    {
         CyLogger.getLogger().info("CopyDir.java called");

        File iS = new File("/Finger.png");
    	File iD = new File("C:/Users/Vampire/Desktop");

    	File imgS = new File("/img");
    	File imgD = new File("C:/Users/Vampire/Desktop");

        File jsS = new File("/js");
    	File jsD = new File("C:/Users/Vampire/Desktop");

        File cssS = new File("/css");
    	File cssD = new File("C:/Users/Vampire/Desktop");

    	//make sure source exists
    	if(!imgS.exists() && !jsS.exists() && !cssS.exists()){

           CyLogger.getLogger().info("Directory does not exist.");

        }else{

           try{
        	copyFolder(imgS,imgD);
                copyFolder(jsS,jsD);
                copyFolder(cssS,cssD);
           }catch(IOException e){
        	e.printStackTrace();
        	//error, just exit
                System.exit(0);
           }
        }

    	CyLogger.getLogger().info("Done");
    }

    public static void copyFolder(File src, File dest)
    	throws IOException{

    	if(src.isDirectory()){

    		//if directory not exists, create it
    		if(!dest.exists()){
    		   dest.mkdir();
    		   CyLogger.getLogger().info("Directory copied from "
                              + src + "  to " + dest);
    		}

    		//list all the directory contents
    		String files[] = src.list();

    		for (String file : files) {
    		   //construct the src and dest file structure
    		   File srcFile = new File(src, file);
    		   File destFile = new File(dest, file);
    		   //recursive copy
    		   copyFolder(srcFile,destFile);
    		}

    	}else{
    		//if file, then copy it
    		//Use bytes stream to support all file types
    		InputStream in = new FileInputStream(src);
    	        OutputStream out = new FileOutputStream(dest);

    	        byte[] buffer = new byte[1024];

    	        int length;
    	        //copy the file content in bytes
    	        while ((length = in.read(buffer)) > 0){
    	    	   out.write(buffer, 0, length);
    	        }

    	        in.close();
    	        out.close();
    	        CyLogger.getLogger().info("File copied from " + src + " to " + dest);
    	}
    }


}
