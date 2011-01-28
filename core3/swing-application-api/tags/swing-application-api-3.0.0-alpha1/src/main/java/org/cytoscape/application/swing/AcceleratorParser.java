package org.cytoscape.application.swing;

import java.util.StringTokenizer;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import java.awt.Toolkit;
import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses accelerator combinations to be used with menu items.
 * <code>javax.swing.KeyStroke</code> provides a method called
 * <code>getKeyStroke</code> that parses accelerator combinations.
 * The purpose of this class is to address certain limitations of
 * <code>getKeyStroke</code>:
 * <ol>
 * <li>
 * <code>KeyStroke</code> is designed to parse any key combination,
 * whereas <code>AcceleratorParser</code> is designed specifically
 * for menu items. One can parse correctly formatted strings with
 * <code>getKeyStroke</code> that is not acceptable for menu items.
 * However, <code>AcceleratorParser</code> will always return
 * <code>KeyStroke</code>s valid for menu items.
 * </li>
 * <li>
 * If one attempts to parse an incorrectly formatted accelerator combination
 * string with <code>getKeyStroke</code>, it will return <code>null</code>
 * without describing a reason why the string is not formatted correctly.
 * <code>AcceleratorParser</code> attempts to provide the user with
 * as much information as necessary to determine why the string is
 * not formatted correctly.
 * </li>
 * <li>
 * On Linux and Windows, one typically uses the Command key to
 * issue a keyboard shortcut. On Mac, it is the Apple key. If the programmer
 * specifies that the Control key should be used in the accelerator combination,
 * <code>AcceleratorParser</code> will convert the Control key modifier to an
 * Apple key modifier if one is running on a Mac and vice-versa.
 * </li>
 * </ol>
 */
class AcceleratorParser
{
	private static final Map<String,Integer> modifiers = new HashMap<String,Integer>();
	private static final Logger logger = LoggerFactory.getLogger(AcceleratorParser.class);
	static {
		modifiers.put("command",	Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		modifiers.put("cmd",		Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		modifiers.put("meta",		Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		modifiers.put("control",	Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		modifiers.put("ctrl",		Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		modifiers.put("shift",		InputEvent.SHIFT_MASK);
		modifiers.put("alt",		InputEvent.ALT_MASK);
		modifiers.put("option",		InputEvent.ALT_MASK);
		modifiers.put("opt",		InputEvent.ALT_MASK);
	}

	/**
	 * Parses an accelerator combination.
	 *
	 * A well formed accelerator combination has the following syntax:
	 * <pre>
	 *    &lt;modifiers&gt;* &lt;virtualKey&gt;
	 *    modifiers  := command | cmd | meta | control |
	 *                  ctrl | shift | alt | option | opt
	 *    virtualKey := a constant in java.awt.event.KeyEvent beginning with VK_
	 * </pre>
	 * <p>Modifiers do not necessarily have the same meaning as those listed in
	 * <code>KeyEvent</code> or <code>InputEvent</code>.
	 * Modifiers have the following meanings:
	 * <ul>
	 * <li><code>command</code>, <code>cmd</code>, <code>meta</code>,
	 * <code>control</code>, <code>ctrl</code>: the Control key for Windows
	 * and Linux users or the Apple key for Mac users</li>
	 * <li><code>shift</code>: the Shift key</li>
	 * <li><code>alt</code>, <code>option</code>, <code>opt</code>:
	 * the Alt key, also called the Option key on Mac keyboards</li>
	 * </ul></p>
	 *
	 * <p>Examples of valid accelerator combinations:
	 * <ul>
	 * <li><code>cmd shift a</code></li>
	 * <li><code>insert</code></li>
	 * <li><code>shift circumflex</code></li>
	 * </ul></p>
	 * @param string A well formatted accelerator combination described above.
	 */
	static KeyStroke parse(String string) {
		int keyCode = 0;
		int modifierCode = 0;
		final StringTokenizer tokenizer = new StringTokenizer(string);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (tokenizer.hasMoreTokens()) {
				modifierCode |= lookupModifier(token); 
			} else {
				keyCode = lookupVKCode(token);
			}
		}

		if (keyCode == 0)
			return null;	

		return KeyStroke.getKeyStroke(keyCode, modifierCode);
	}

	private static int lookupModifier(String name) {
		Integer modifier = modifiers.get(name);
		if (modifier == null) {
			logger.warn("The modifier '" + name + "' is invalid; valid modifiers are: " + modifiers.keySet().toString());
			return 0;
		}
		return modifier.intValue();
	}

	private static int lookupVKCode(String name) {
		String error = "The virtual key 'VK_" + name +"' does not exist";

		int code = 0;
		try {
			code = KeyEvent.class.getField(name).getInt(KeyEvent.class);
		} catch (NoSuchFieldException ex) {
			code = 0;
			logger.warn(error);	
		} catch (IllegalAccessException ex) {
			code = 0;
			logger.warn(error);	
		}

		return code;
	}
}
