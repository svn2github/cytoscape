package csplugins.isb.pshannon.py;

import org.python.util.InteractiveConsole;
import org.python.core.*;
import java.util.Iterator;
import javax.swing.text.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.util.Vector;
import java.util.Hashtable;
import java.io.*;



/**
 * SPyConsole Application
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
public class SPyConsole extends InteractiveConsole {

   JFrame appFrame = null;
   
	Styles _styles = new Styles();
	StyledEditorKit.ForegroundAction _inputAction;
	DefaultStyledDocument _document;
	CommandLineOptions _opts = new CommandLineOptions();
	JTextPane _textpane;
	Vector _history = new Vector();
	Vector _initCommands = new Vector();
	Vector _killRing = new Vector();
	int	_oldHistoryLength = 0;
	int	_historyPosition = 0;
	int	_completionIndex = 0;
	int _killRingPosition = 0;
	int	_localNameSpaceCompletionIndex = -1;
	int _completionOffset = -1;
	int _completionCursorOffset;
	String _completionBuffer;
	StringBuffer _buff = new StringBuffer();
	boolean _waitingForInput = false;
	boolean _debug = true;
	Position _startInput;
	Keymap _keymap;
	Hashtable _actionTable = new Hashtable();
	
	private String info = null;
	

    private static String _usage =
        "usage: jython [options] [-jar jar | -c cmd | file | -] [args]\n"+
        "Options and arguments:\n"+
        "-i       : inspect interactively after running script, and force\n"+
        "           prompts, even if stdin does not appear to be a terminal\n"+
        "-S       : don't imply `import site' on initialization\n"+
        "-X       : disable class based standard exceptions\n"+
        "-Dprop=v : Set the property `prop' to value `v'\n"+
        "-c cmd   : program passed in as string (terminates option list)\n"+
        "file     : program read from script file\n"+
        "-        : program read from stdin (default; interactive mode if a tty)\n"+
        "--help   : print this usage message and exit\n"+
        "--version: print Jython version number and exit\n"+
        "args     : arguments passed to program in sys.argv[1:]";

	public SPyConsole() {

		addStyles();

		_inputAction = new StyledEditorKit.ForegroundAction("start input", Color.black);
		_document = new DefaultStyledDocument(_styles);
		_document.setLogicalStyle(0, _styles.getStyle("normal") );

		_textpane = new JTextPane(_document);


		MouseListener ml =  new MouseAdapter() {
		   public void mousePressed(MouseEvent e) {
			 //processMouseClick( e.getClickCount(), e.getPoint() );
		   };
		};
		 _textpane.addMouseListener(ml);

		KeyListener kl =  new KeyAdapter() {
		   public void keyPressed(KeyEvent ke) {
			 processKeyPress( ke );
		   };
		};
		 _textpane.addKeyListener(kl);

		defineKeymap();
	}

	void addStyles() {
		Style basic = _styles.addBase("normal", 3, 12, "Courier");
		_styles.addDerived("error", basic, Color.red);
		_styles.addDerived("output", basic, Color.blue);
		_styles.addDerived("input", basic, Color.black);
		_styles.addDerived("prompt", basic, Color.magenta);
	}
	
	public void processKeyPress( KeyEvent ke ) {
		int line_start = _startInput.getOffset() + 1;
		int caret_pos = _textpane.getCaretPosition();
 		if (caret_pos < line_start) {
                  System.out.println ("caret smaller than start, setting position:");
                  System.out.println ("line start: " + line_start + "  caret_pos: " + caret_pos);
                  int documentLength = _textpane.getText().length();
                  System.out.println ("document length: " + documentLength);
                  if (line_start <= documentLength)
                    _textpane.setCaretPosition (line_start);
                 }
	}

	KeyStroke setActionForKeyStroke( int key, int modifier, Action action ) {
		if( _keymap == null ) {
			_keymap = JTextComponent.getKeymap(JTextComponent.DEFAULT_KEYMAP);
			if( _debug ) {
				System.out.println("DEFAULT_KEYMAP:");
				KeyStroke keys[] = _keymap.getBoundKeyStrokes();
				for( int i=0; i< keys.length; ++i ) {
					KeyStroke ks0 = keys[i];
					Action a0 = _keymap.getAction(ks0);
					System.out.println( ks0.toString() + " : " + a0.getValue(Action.NAME) );
				}
			}
		}
		KeyStroke ks = KeyStroke.getKeyStroke( key, modifier );
		
        _keymap.removeKeyStrokeBinding( ks );
		_keymap.addActionForKeyStroke( ks, action );
		return ks;
	}
	KeyStroke setActionForKeyStroke( int key, Action action ) {
		return setActionForKeyStroke( key, 0, action );
	}

	KeyStroke setActionForKeyStroke( int key, int modifier, String  action_name ) {
		if( _keymap == null ) {
			_keymap = JTextComponent.getKeymap(JTextComponent.DEFAULT_KEYMAP);
		}
		Action action = getAction(action_name);
		KeyStroke ks = KeyStroke.getKeyStroke( key, modifier);
        _keymap.removeKeyStrokeBinding( ks );
		_keymap.addActionForKeyStroke( ks, action );
		return ks;
	}

	KeyStroke setActionForKeyStroke( int key, String  action_name ) {
		return setActionForKeyStroke( key, 0, action_name );
	}

	void defineKeymap() {
		loadActionTable();
		setActionForKeyStroke( KeyEvent.VK_TAB, 		new TabAction() );
		setActionForKeyStroke( KeyEvent.VK_KP_DOWN, 	new DownHistoryAction() );
		setActionForKeyStroke( KeyEvent.VK_KP_UP, 		new UpHistoryAction() );
		setActionForKeyStroke( KeyEvent.VK_KP_LEFT, 	new CaretMoveAction(-1) );
		setActionForKeyStroke( KeyEvent.VK_KP_RIGHT, 	new CaretMoveAction(1) );
		setActionForKeyStroke( KeyEvent.VK_LEFT, 	new CaretMoveAction(-1) );
		setActionForKeyStroke( KeyEvent.VK_RIGHT, 	new CaretMoveAction(1) );
		setActionForKeyStroke( KeyEvent.VK_DOWN, 		new DownHistoryAction() );
		setActionForKeyStroke( KeyEvent.VK_UP, 			new UpHistoryAction() );
		setActionForKeyStroke( KeyEvent.VK_ENTER, 		new EnterAction() );
		setActionForKeyStroke( KeyEvent.VK_ESCAPE, 		new EscapeAction() );
		setActionForKeyStroke( KeyEvent.VK_DELETE, 	    new DeleteCharAction(-1) );
		setActionForKeyStroke( KeyEvent.VK_N, InputEvent.CTRL_MASK, new DownHistoryAction() );
		setActionForKeyStroke( KeyEvent.VK_P, InputEvent.CTRL_MASK, new UpHistoryAction() );
		setActionForKeyStroke( KeyEvent.VK_F, InputEvent.CTRL_MASK, new CaretMoveAction(1) );
		setActionForKeyStroke( KeyEvent.VK_B, InputEvent.CTRL_MASK, new CaretMoveAction(-1) );
		setActionForKeyStroke( KeyEvent.VK_A, InputEvent.CTRL_MASK, new CaretMoveAction(-2) );
		setActionForKeyStroke( KeyEvent.VK_E, InputEvent.CTRL_MASK, new CaretMoveAction(2) );
		setActionForKeyStroke( KeyEvent.VK_D, InputEvent.CTRL_MASK, new DeleteCharAction(1) );
		setActionForKeyStroke( KeyEvent.VK_Y, InputEvent.CTRL_MASK, new YankLineAction() );
		setActionForKeyStroke( KeyEvent.VK_K, InputEvent.CTRL_MASK, new KillLineAction(true,true) );
		setActionForKeyStroke( KeyEvent.VK_X, InputEvent.CTRL_MASK, DefaultEditorKit.cutAction );
		setActionForKeyStroke( KeyEvent.VK_C, InputEvent.CTRL_MASK, DefaultEditorKit.copyAction );
		setActionForKeyStroke( KeyEvent.VK_V, InputEvent.CTRL_MASK, new PasteAction(this) );
		setActionForKeyStroke( KeyEvent.VK_W, InputEvent.CTRL_MASK, new KillLineAction(false,true) );
		_textpane.setKeymap(_keymap);
	}

	void loadActionTable() {
		Action[] actions = _textpane.getActions();
		if( _debug ) { System.out.println("Action table:"); }
		for( int i=0; i< actions.length; ++i ) {
			String aname = (String) actions[i].getValue(Action.NAME);
			_actionTable.put( aname, actions[i] );
//			if( _debug ) { System.out.println(aname); }
		}
	}

	public Action getAction( String name ) {
		return (Action) _actionTable.get(name);
	}

	/*
	public void processMouseClick( int clickCount, Point p ) {
		if( clickCount > 1 ) {
//			int offset = _textpane.viewToModel( p );
//			System.out.println("Got mouse clicks: " + clickCount + ", location = " + offset );
		}
	}
	*/


	public String raw_input(PyObject prompt) {
		String p = ((PyString)prompt).toString();
        startUserInput( p );
        waitForInput();
        return getInput() ;
    }

    public JTextPane getTextPane() { return _textpane; }


   /**
    * Writes the given text, in the given style, to the console window.
    * @param text The text to be displayed on the console
    * @param stylename The name of the style to use. This should be one of the
    *    following values: <pre>"error", "input", "output" or "prompt"</pre>
    */
   public void  write( String text, String stylename ) {
      Style style = _styles.getStyle(stylename);
      try {
         _document.insertString(_document.getLength(), text, style);
      } catch ( BadLocationException err ) {
         if( stylename.equals( "error" ) ) {
            System.out.println( "\n" + err.getMessage() );
         } else {
            error("Text write error",  err );
         }
      }
   }

   /**
    * Writes the given message to the console using the "error" style
    * @param label The basic error mesage
    * @param err The exception that was raised. The message associated with
    *    this exception will be appended to the <pre>label</pre> parameter.
    */
	public void error( String label, Exception err ) {
		if( _debug ) { err.printStackTrace(); }
		String msg = label + ": " + err.getMessage();
		try {
			write( msg, "error" );
		} catch ( Exception err1 ) {
			System.out.println( "\n" + msg );
		}
	}

	public void error( String message ) {
		write( message, "error" );
	}

	public void output( String message ) {
		write( message, "output" );
	}

	void waitForInput() {
		_waitingForInput = true;
		while ( _waitingForInput ) {
			try {  Thread.sleep(100L);   }
			catch (InterruptedException e0) {  break;  }
		}
	}

	public void beep() {
		_textpane.getToolkit().beep();
	}

	public String getLocalNameSpace() {
	  PyStringMap locals = (PyStringMap) getLocals();
	  PyList keys = locals.keys();
	  PyString ps = keys.__str__();
	  return ps.toString();
	}

	public String completeNameFromLocalNameSpace( String name ) {
	  PyStringMap locals = (PyStringMap) getLocals();
	  PyList keys = locals.keys();
	  int sp_index = name.lastIndexOf(' ');
	  if( sp_index > 0 ) {
		  name = name.substring(sp_index+1);
		  if( _completionOffset == -1 ) {
			  _completionOffset = _completionCursorOffset - name.length();
		  }
	  }
	  while( ++_localNameSpaceCompletionIndex < keys.__len__() ) {
		 PyString key =  (PyString) keys.__getitem__( _localNameSpaceCompletionIndex );
		 String skey = key.toString();
		 if( skey.startsWith(name) ) {
			 PyObject obj = locals.get(key);
			 if( obj.isCallable() ) { skey = skey + "()"; }
			 return skey;
		 }
	  }
	  return null;
	}



	/**
	 * Delete a single character
	 * @param dir The direction. -1 = the Delete key. 1 = the CTRL+D key
	 */
	public void deleteChar(int dir ) {
		int line_end = _document.getLength();
		int line_start = _startInput.getOffset() + 1;
		int caret_pos = _textpane.getCaretPosition();
		try {
			switch(dir) {
				case 1:
					if( caret_pos < line_end ) { _document.remove( caret_pos, 1 ); }
					else beep();
				break;
				case -1:
					if( caret_pos > line_start ) { _document.remove( caret_pos-1, 1 ); }
					else beep();
				break;
			}
		} catch ( BadLocationException err ) {
			error("Text write error",  err );
		}
	}

	public void moveCaret(int moveType ) {
		int line_end = _document.getLength();
		int line_start = _startInput.getOffset() + 1;
		int caret_pos = _textpane.getCaretPosition();
		switch(moveType) {
			case 1:
				if( caret_pos < line_end ) { _textpane.setCaretPosition( caret_pos + 1 ); }
			break;
			case 2:
				_textpane.setCaretPosition( line_end );
			break;
			case -1:
				if( caret_pos > line_start ) { _textpane.setCaretPosition( caret_pos - 1 ); }
			break;
			case -2:
				_textpane.setCaretPosition( line_start );
			break;
		}
	}

	String trimEnd( String s ) {
	  int end = s.length() - 1;
	  boolean cut = false;
	  while( ( end >= 0 ) && Character.isWhitespace( s.charAt(end) ) ) { cut = true; end--; }
	  return (cut) ? ( ( end < 0 ) ? "" : s.substring(0,end+1) ) : s;
	}


	public void completeInput() {
		int offset = _startInput.getOffset();
		try {
			if( _completionBuffer == null ) {
				_completionCursorOffset = _document.getLength();
				_completionBuffer = _document.getText(offset+1, _completionCursorOffset-offset);
				_completionBuffer =  _completionBuffer.trim();
			}
			int history_size = _history.size();
			while( _completionIndex++ <  history_size ) {
				int h_index = history_size-_completionIndex;
				String hstr = (String) _history.get(h_index);
				System.out.println(" complete Input test(" + h_index + "): " + hstr);
				if( hstr.startsWith(_completionBuffer) ) {
				  replaceInput( hstr );
				  return;
				}
			}
			String lnstr = completeNameFromLocalNameSpace( _completionBuffer );
			if( lnstr != null ) {
				replaceInput( lnstr );
				return;
			}
			beep();
		} catch ( BadLocationException err ) {
			error("Text completion error",  err );
		}
	}

	public void killLine( boolean addToKillRing, boolean removeText ) {
		ActionEvent ae0 = new ActionEvent(_textpane, ActionEvent.ACTION_PERFORMED, "select line");
		Action sel = getAction(DefaultEditorKit.selectionEndLineAction);
		sel.actionPerformed(ae0);

		if( addToKillRing ) {
			String text = _textpane.getSelectedText();
			_killRing.add(text);
			_killRingPosition = _killRing.size();
		}

		if( removeText ) {
			ActionEvent ae1 = new ActionEvent(_textpane, ActionEvent.ACTION_PERFORMED, "cut");
			Action cut = getAction(DefaultEditorKit.cutAction);
			cut.actionPerformed(ae1);
		}
	}

	public void yankLine() {
		if( _killRing.size() == 0 ) { beep(); return; }
		if( _killRingPosition == 0 ) { _killRingPosition = _killRing.size(); }
		replaceInput( (String) _killRing.get( --_killRingPosition ) );
	}

	public void replaceInput( String text ) {
		int offset = ( _completionOffset < 0 ) ? _startInput.getOffset() + 1 : _completionOffset;
		int length = _document.getLength()-offset;
		try {
			_document.remove(offset,length);
		} catch ( BadLocationException err ) {
			error("Text write error",  err );
		}
		write(text, "input");
	}

   /**
    * Tell the interpreter to execute the given command.
    * @param cmd The JPython command to execute.
    */
   public boolean executeCommand( String cmd ) {
      return executeCommand( cmd, true );
   }

   /**
    * Tell the interpreter to execute the given command.
    * @param cmd The Python command to execute.
    * @param wait_until_ready Sedt this to <pre>true</pre> if you want this
    *    method to block until a response is received from the inperpreter.
    * @return boolean Returns <pre>true</pre> if successful.
    */
   public boolean executeCommand( String cmd, boolean wait_until_ready ) {
      if( wait_until_ready ) {
         while ( !_waitingForInput ) {
            try {  Thread.sleep(100L);   }
            catch (InterruptedException e0) {
               break;
            }
         }
      }
      if( _waitingForInput ) {
         replaceInput(cmd);
         write("\n", "input");
         _waitingForInput = false;
         return true;
      } else {
         beep();
         return false;
      }
   }


   /**
    * This method is used to execute multiple commands in a single String
    * object. This method is usually caqlled from PasteAction and
    * LoadScriptAction.
    * @param cmds A String of command lines. Each line is trerminated with a
    *    "\n" character.
    * @return boolean Returns <pre>true</pre> if all commands executed
    *    successfully. Otherwise it returns <pre>false</pre>
    */
   public boolean executeCommandSet(String cmds) {
      boolean result = true; // Assume success
      try {
         //System.out.println("executing [" + data + "]");

         // read each line of the data
         StringReader sr = new StringReader(cmds);
         BufferedReader br = new BufferedReader(sr);
         String line;
         while((line = br.readLine()) != null) {
            //System.out.println("Got a line [" + line + "]");
            if(!executeCommand(line)) {
               result = false;
            }
         }
      } catch (IOException ex) {
         ex.printStackTrace();
      }
      return result;
   }


	public PyObject compile( java.io.InputStream s, String name ) {
	  return Py.compile(s, name, "exec");
	}

	public void addInitCommand( String cmd ) {
		_initCommands.add(cmd);
	}

   /**
    * This action handles the ENTER key
    */
   class EnterAction  extends AbstractAction   {
      public void actionPerformed( ActionEvent e ) { _waitingForInput = false;  write("\n", "input"); }
   }



   class TabAction  extends AbstractAction   {
      public void actionPerformed( ActionEvent e ) {
         //completeInput();
         write("   ", "input");
      }
   }

	class EscapeAction  extends AbstractAction   {
	  public void actionPerformed( ActionEvent e ) { _waitingForInput = false; }
    }

	class UpHistoryAction  extends AbstractAction   {
	  public void actionPerformed( ActionEvent e ) { getHistory(-1); }
    }

	class DownHistoryAction  extends AbstractAction   {
	  public void actionPerformed( ActionEvent e ) { getHistory(1); }
    }

	class CaretMoveAction  extends AbstractAction   {
	  int _dir;
	  public CaretMoveAction( int dir ) { _dir = dir; }
	  public void actionPerformed( ActionEvent e ) { moveCaret(_dir); }
    }

	class DeleteCharAction  extends AbstractAction   {
	  int _dir;
	  public DeleteCharAction( int dir ) { _dir = dir; }
	  public void actionPerformed( ActionEvent e ) { deleteChar( _dir ); }
    }

	class KillLineAction  extends AbstractAction   {
	  boolean _addToKillRing, _removeText;
	  public KillLineAction( boolean addToKillRing, boolean removeText ) {
		  _addToKillRing = addToKillRing; _removeText = removeText;
	  }
	  public void actionPerformed( ActionEvent e ) { killLine( _addToKillRing, _removeText); }
    }

	class YankLineAction  extends AbstractAction   {
	  public void actionPerformed( ActionEvent e ) { yankLine(); }
    }

	public void getHistory( int direction ) {
		int historyLength= _history.size();
		int pos = _historyPosition + direction;

		if ( (0 <= pos) && (pos < historyLength) ) {
			_historyPosition = pos;
			replaceInput( (String) _history.get(pos) );
		} else {
			beep();
		}
	}


	public void capturePythonOutput() {
		setOut( new OutputBuffer( this, "output" ) );
		setErr( new OutputBuffer( this, "error" ) );
	}

	public JScrollPane getConsolePanel() {
		return new JScrollPane( getTextPane() );
	}

	
	public void runShell(String info) {
		this.info = info;
		runShell();
	}
	
	public void runShell() {
		String banner = "SME Python Shell (SPyConsole)\n" +
		"Jython " + systemState.version + " on platform " + systemState.platform;
		capturePythonOutput();
		getTextPane().requestFocus();

        PyModule mod = imp.addModule("__main__");
        setLocals(mod.__dict__);

        if (Options.importSite) {
			try {
				imp.load("site");
			} catch (PyException pye) {
				if (!Py.matchException(pye, Py.ImportError)) {
					System.err.println("error importing site");
					Py.printException(pye);
				}
			}
		}		

        if (_opts.command != null) {
            try {
                exec(_opts.command);
            } catch (Throwable t) {
                Py.printException(t);
            }
        }

		Iterator cmd_iter = _initCommands.iterator();
		while( cmd_iter.hasNext() ) {
		   String cmd = (String) cmd_iter.next();
		   if( _debug ) { System.out.println("Processing init cmd: " + cmd); }
		   try {
                exec(cmd);
		   } catch (Throwable t) {
                Py.printException(t);
		   }
		}

        // was there a filename on the command line?
        if (_opts.filename != null) {
            String path = new java.io.File(_opts.filename).getParent();
            if (path == null) path = "";
            Py.getSystemState().path.insert(0, new PyString(path));
			try {
				execfile(_opts.filename);
			} catch (Throwable t) {
				Py.printException(t);
			}
        }

        if (null != info) {
        	banner += "\n\n" + info + "\n";
        }
        
		interact(banner);
	}

	public void processArgs(String args[]) {
        // Parse the command line options
        if( args == null ) {
			args = new String[1];
			args[0] = new String("-i");
		}
        if (!_opts.parse(args)) {
            if (_opts.version) {
                System.err.println(InteractiveConsole.getDefaultBanner());
                System.exit(0);
            }
            System.err.println(_usage);
            int exitcode = _opts.help ? 0 : -1;
            System.exit(exitcode);
        }

        // Setup the basic python system state from these options
        PySystemState.initialize(System.getProperties(), _opts.properties, _opts.argv);
	}


   /**
    * Get a reference to the JFrame that contains the console
    * @return JFrame The JFrame that contains the application
    */
   public JFrame getAppFrame() {
      return appFrame;
   }

	public void setAppFrame( JFrame f ) { appFrame = f; }

	public static void main(String args[]) {
		 SPyConsole c = new SPyConsole();
		 c.processArgs(args);

		 c.appFrame = new JFrame("Jython Console");
		 WindowListener l = new WindowAdapter() {
			  public void windowClosing(WindowEvent e) { System.exit(0); }
		 };
		 c.appFrame.addWindowListener( l );
		 c.appFrame.getContentPane().add( c.getConsolePanel(), BorderLayout.CENTER );
		 c.appFrame.setJMenuBar(new ConsoleMenubar(c));
		 c.appFrame.setSize(900, 600);
		 c.appFrame.setVisible(true);
//		 c.addInitCommand("from miiee.python.SPyMethods import *");
//		 c.addInitCommand("open()");
		 c.runShell();
	}



	public String getText() {
	   _buff.setLength(0);
	   String sep = System.getProperty("line.separator");
	   Iterator iter = _history.iterator();
		while( iter.hasNext() ) {
		   String line = (String) iter.next();
		   if( line.length() > 0 ) {
			 _buff.append( line );
			 _buff.append( sep );
		   }
		}
		return _buff.toString();
	}




	public String getInput() {
		int offset = _startInput.getOffset();
		try {
			String line = trimEnd( _document.getText(offset+1, _document.getLength()-offset) );
			registerNewInput( line );
			return line;
		} catch ( BadLocationException err ) {
			error("Text write error",  err );
			return null;
		}
	}

	void registerNewInput( String line ) {
		_history.add(line);
		_historyPosition = _history.size();
		_completionIndex = 0;
		_localNameSpaceCompletionIndex = -1;
		_completionOffset = -1;
		_completionBuffer = null;
	}


	public void startUserInput( String prompt ) {
		write(prompt, "prompt");
		try {
			_startInput = _document.createPosition(_document.getLength()-1);
		} catch ( BadLocationException err ) {
			error("Text write error",  err );
		}
//      _document.setCharacterAttributes(_document.getLength()-1, 1, _styles.getStyle("input"), 1)
		_textpane.setCaretPosition( _document.getLength() );
		ActionEvent ae = new ActionEvent(_textpane, ActionEvent.ACTION_PERFORMED, "start input");
		_inputAction.actionPerformed(ae);
	}
}


