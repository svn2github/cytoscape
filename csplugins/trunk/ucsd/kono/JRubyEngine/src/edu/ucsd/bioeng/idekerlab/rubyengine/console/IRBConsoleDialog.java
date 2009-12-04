package edu.ucsd.bioeng.idekerlab.rubyengine.console;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.jruby.Ruby;
import org.jruby.demo.TextAreaReadline;

import sun.misc.RequestProcessor;

import com.sun.jmx.snmp.tasks.Task;

public class IRBConsoleDialog extends JDialog {

	private boolean finished = true;
    private JTextPane text;

	
	public void createTerminal() {
        text = new JTextPane();

        text.setMargin(new Insets(8,8,8,8));
        text.setCaretColor(new Color(0xa4, 0x00, 0x00));
        text.setBackground(new Color(0xf2, 0xf2, 0xf2));
        text.setForeground(new Color(0xa4, 0x00, 0x00));
        
        // From core/output2/**/AbstractOutputPane
        Integer i = (Integer) UIManager.get("customFontSize"); //NOI18N
        int size;
        if (i != null) {
            size = i.intValue();
        } else {
            Font f = (Font) UIManager.get("controlFont"); // NOI18N
            size = f != null ? f.getSize() : 11;
        }
        text.setFont(new Font ("Monospaced", Font.PLAIN, size)); //NOI18N
        //setBorder (BorderFactory.createEmptyBorder());
        
        // Try to initialize colors from NetBeans properties, see core/output2
        Color c = UIManager.getColor("nb.output.selectionBackground"); // NOI18N
        if (c != null) {
            text.setSelectionColor(c);
        }
        
      
        
        
        JScrollPane pane = new JScrollPane();
        pane.setViewportView(text);
        pane.setBorder(BorderFactory.createLineBorder(Color.darkGray));
        add(pane);
        validate();

        final Ruby runtime = getRuntime(text);
//        startIRB(runtime);
        
//        RequestProcessor.Task task = RequestProcessor.getDefault().create(new Runnable() {
//        //RequestProcessor.getDefault().post(new Runnable() {
//            public void run() {
//                
//            }
//        });
//        task.addTaskListener(new TaskListener() {
//            public void taskFinished(Task task) {
//                finished = true;
//                //tar.writeMessage(" " + NbBundle.getMessage(IrbTopComponent.class, "IrbGoodbye") + " "); // NOI18N
//                text.setEditable(false);
//                SwingUtilities.invokeLater(new Runnable() {
//                    public void run() {
//                        IrbTopComponent.this.close();
//                        IrbTopComponent.this.removeAll();
//                        text = null;
//                    }
//                });
//            }
//        });
//        task.schedule(10);
        
	}
	
	
	static Ruby getRuntime(final JTextComponent text) {
//            final TextAreaReadline tar = new TextAreaReadline(text,
//                    " " + NbBundle.getMessage(IrbTopComponent.class, "IrbWelcome") + " \n\n"); // NOI18N
//            // Ensure that ClassPath can find libraries etc.
//            RubyInstallation.getInstance().setJRubyLoadPaths();
//
//            final PipedInputStream pipeIn = new PipedInputStream();
//            final RubyInstanceConfig config = new RubyInstanceConfig() {{
//                setInput(pipeIn);
//                setOutput(new PrintStream(tar));
//                setError(new PrintStream(tar));
//                setObjectSpaceEnabled(false);
//                //setArgv(args);
//            }};
//            final Ruby runtime = Ruby.newInstance(config);
//
//            runtime.getGlobalVariables().defineReadonly("$$", new ValueAccessor(runtime.newFixnum(System.identityHashCode(runtime))));
//            runtime.getLoadService().init(new ArrayList());
//
//            tar.hookIntoRuntime(runtime);
//            return runtime;
//        }
//        
//        private static void startIRB(final Ruby runtime) {
//            runtime.evalScriptlet("require 'irb'; require 'irb/completion'; IRB.start"); // NOI18N
//        }
//	
		return null;
	}
}
