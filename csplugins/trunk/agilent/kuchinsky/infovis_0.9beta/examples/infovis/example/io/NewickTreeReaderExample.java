package infovis.example.io;
import infovis.Tree;
import infovis.tree.DefaultTree;
import infovis.tree.io.NewickTreeReader;
import infovis.tree.io.XMLTreeWriter;

import java.io.*;

/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

/**
 * Class NewickTreeReaderExample
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class NewickTreeReaderExample {

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        String fname = args[0];
        try {
            InputStream in = new FileInputStream(fname);
            Tree tree = new DefaultTree();
            int offset1 = fname.lastIndexOf('/') + 1;
            int offset2 = fname.indexOf('.', offset1);
            String name;
            if (offset2 == -1)
                name = fname.substring(offset1);
            else {
                if ((offset2 - offset1) > 4)
                    offset1 += 4;

                name = fname.substring(offset1, offset2).toLowerCase();
            }
            NewickTreeReader reader =
                new NewickTreeReader(in, name, tree);
            if (reader.load()) {
                OutputStream out = System.out;
                if (args.length == 2) {
                    try {
                        out = new BufferedOutputStream(new FileOutputStream(args[1]));
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                XMLTreeWriter writer = new XMLTreeWriter(out, tree);
                writer.write();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
