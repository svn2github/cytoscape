package Usefuls;

import java.io.*;
/* If you just want to use FileReader,
   import java.io.FileReader;
   Full path will work without using import:
   new java.io.FileReader(args[0]);
*/

import java.util.Hashtable;

public class Hash {
    private Hashtable<String, String> __h;

    public Hash(){
	__h = new Hashtable<String, String>();
    }

    public boolean read_file(String filename,
				    int col1, int col2){
	try {
	    FileReader fr = new FileReader(filename);
	    BufferedReader br = new BufferedReader(fr);
	    
	    String line;
	    while((line = br.readLine()) != null){
		String[] r = line.split("\t");
		__h.put(r[col1], r[col2]);
		System.out.println(r[0] + "---" + r[1]);
	    }
	    return true;

	}
	catch (Exception e){
	    System.out.println("Exception: " + e);
	    return false;
	}
    }

    public String val(String key){
    	return __h.get(key).toString();
    }


    public static void main(String args[]){
    	Hash h = new Hash();
    	//h.read_file(args[0], 0, 1);
    	h.read_file("DataFiles/Test1/tmpfile", 0, 1);
    	System.out.println(h.val("Hi"));
    }

}


