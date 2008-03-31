/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: ThreadListener.java,v $
 * $Revision: 1.3 $
 * $Date: 2004/12/21 03:28:14 $
 * $Name:  $
 *
 * This file is part of Java TreeView
 * Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
 *
 * This software is provided under the GNU GPL Version 2. In particular, 
 *
 * 1) If you modify a source file, make a comment in it containing your name and the date.
 * 2) If you distribute a modified version, you must do it under the GPL 2.
 * 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
 *
 * A full copy of the license can be found in gpl.txt or online at
 * http://www.gnu.org/licenses/gpl.txt
 *
 * END_HEADER 
 */
package edu.stanford.genetics.treeview;



/* Call static "List all threads" to list all running threads. Stolen
   from O'Reilly's Java in a nutshell first edition. alok@genome. */

/* I added a constuctor which will pop up a window which monitors
   running threads */

import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class ThreadListener extends Thread {
    boolean runin = true; // instance variable to tell if we are done...
    Frame top; // frame to hold thread monitor
    TextArea textarea;
    // Display info about a thread
    private static void print_thread_info(PrintStream out, Thread t, String indent) {
	if (t == null) return;
	out.println(indent + "Thread: " + t.getName() + " Priority: " +
		    t.getPriority() + (t.isDaemon()?"Daemon":"Not Daemon") +
		    (t.isAlive()?" Alive":" Not Alive"));
    }

    //Display info about a thread group and its threads and groups
    private static void list_group(PrintStream out, ThreadGroup g, String indent) {
	if (g == null) return;
	int num_threads = g.activeCount();
	int num_groups = g.activeGroupCount();
	Thread threads[] = new Thread[num_threads];
	ThreadGroup groups[] = new ThreadGroup[num_groups];
	g.enumerate(threads, false);
	g.enumerate(groups, false);
	
	out.println(indent + "Thread Group: " + g.getName() + " Max Priority " + g.getMaxPriority() + (g.isDaemon()?" Daemon":" Not Daemon"));
	
	for(int i = 0; i < num_threads; i++)
	    print_thread_info(out, threads[i], indent + " ");
	for(int i = 0; i < num_groups; i++)
	    list_group(out, groups[i], indent + " ");	
    }

    //find root thread and list recursively
    public static void listAllThreads(PrintStream out) {
	ThreadGroup current_thread_group;
	ThreadGroup root_thread_group;
	ThreadGroup parent;
	
	// Get the current thread group
	current_thread_group = Thread.currentThread().getThreadGroup();
	//now, go find root thread group
	root_thread_group = current_thread_group;
	parent = root_thread_group.getParent();
	while (parent != null) {
	    root_thread_group = parent;
	    parent = parent.getParent();
	}
	// list recursively
	list_group(out, root_thread_group,"");
    }
    
    public synchronized void run() {
	while (runin == true) {
	    ByteArrayOutputStream os = new ByteArrayOutputStream();
	    PrintStream ps = new PrintStream(os);
	    listAllThreads(ps);
	    textarea.setText(os.toString());
	    textarea.validate();
	    textarea.repaint();
	    try {
		this.wait(1000);
	    } catch (InterruptedException e) {
		// catches InterruptedException
		System.out.println("Somebody set us up the bomb!");
	    }
	}
    }

    public void finish() { runin = false;}

    public ThreadListener() {
	top = new Frame();
	textarea = new TextArea(20, 100);
	top.add(textarea);
	top.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		    runin = false;
		    top.dispose();		    
		}
	});
	
	top.pack();
	top.show();
	// start me up!
	this.start();
    }
}

