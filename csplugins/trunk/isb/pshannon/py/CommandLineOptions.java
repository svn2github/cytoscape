package csplugins.isb.pshannon.py;


import org.python.core.*;

/**
 * This class is used to parse any command line options passed into the
 * SPyConsole class when it was invoked. It stores those options for easy
 * access.
 *
 * Developed by Tom Maxwell, maxwell@cbl.umces.edu
 * University of Maryland Institute for Ecological Economics
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * @author Tom Maxwell <maxwell@cbl.umces.edu>
 * @version 1.0
 */
class CommandLineOptions {
    public String filename;
    public boolean jar, interactive, notice;
    private boolean fixInteractive;
    public boolean help, version, debug = true;
    public String[] argv;
    public java.util.Properties properties;
    public String command;

    public CommandLineOptions() {
        filename = null;
        jar = fixInteractive = false;
        interactive = notice = true;
        properties = new java.util.Properties();
        help = version = false;
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
         try {
             System.setProperty(key, value);
         }
         catch (SecurityException e) {}
    }

    public boolean parse(String[] args) {
        int index=0;
        while (index < args.length && args[index].startsWith("-")) {
            String arg = args[index];
            if( debug ) { System.out.println("\n\n  SPyConsole processing arg: " + arg ); }
            if (arg.equals("--help")) {
                help = true;
                return false;
            }
            else if (arg.equals("--version")) {
                version = true;
                return false;
            }
            else if (arg.equals("-")) {
                if (!fixInteractive)
                    interactive = false;
                filename = "-";
            }
            else if (arg.equals("-i")) {
                fixInteractive = true;
                interactive = true;
            }
            else if (arg.equals("-jar")) {
                jar = true;
                if (!fixInteractive)
                    interactive = false;
            }
            else if (arg.equals("-S")) {
                Options.importSite = false;
            }
            else if (arg.equals("-c")) {
                command = args[++index];
                if (!fixInteractive) interactive = false;
                break;
            }
            else if (arg.startsWith("-D")) {
                String key = null;
                String value = null;
                int equals = arg.indexOf("=");
                if (equals == -1) {
                    String arg2 = args[++index];
                    key = arg.substring(2, arg.length());
                    value = arg2;
                }
                else {
                    key = arg.substring(2, equals);
                    value = arg.substring(equals+1, arg.length());
                }
                setProperty(key, value);
            }
            else {
                String opt = args[index];
                if (opt.startsWith("--"))
                    opt = opt.substring(2);
                else if (opt.startsWith("-"))
                    opt = opt.substring(1);
                System.err.println("jython: illegal option -- " + opt);
                return false;
            }
            index += 1;
        }
        notice = interactive;
        if (filename == null && index < args.length && command == null) {
            filename = args[index++];
            if (!fixInteractive)
                interactive = false;
            notice = false;
        }
        if (command != null)
            notice = false;

        int n = args.length-index+1;
        argv = new String[n];
        //new String[args.length-index+1];
        if (filename != null)
            argv[0] = filename;
        else argv[0] = "";

        for(int i=1; i<n; i++, index++) {
            argv[i] = args[index];
        }

        return true;
    }
}
