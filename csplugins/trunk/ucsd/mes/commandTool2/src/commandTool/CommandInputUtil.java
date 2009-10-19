/* vim: set ts=2: */
/**
 * Copyright (c) 2006 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package commandTool;

import java.io.StreamTokenizer;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommand;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;

/**
 * An interpreter that will attempt to parse a string and based on the contents, 
 * execute the specified command.  The Syntax of the string is:
 * <br/>
 * namespace  command  arguments...
 */
public class CommandInputUtil {

    /**
     * Attempts to execute the command specified by the input string.
     *
     * @param input The input string that specifies the command to be executed.
     */
    public static CyCommandResult executeCommand(String input) throws CyCommandException {

		// special case for help
		if ( input.matches("^\\s*help\\s*$") ) {
			return CyCommandManager.getCommand("help","help").execute( new HashMap<String,String>()); 
		}

		// now loop through normal commands
		for (CyCommand com : CyCommandManager.getCommandList() ) {

			String key = genKey(com.getNamespace(),com.getCommandName());
            Pattern p = Pattern.compile(key);
            Matcher m = p.matcher(input);
			
            if (m.matches()) {
                Map<String, String> settings = parseSettings(m.group(2));
                return com.execute(settings);
            }
        }

		// no commands match
        throw new CyCommandException("Unrecognized Command: '" + input +
                                     "'   Type \"help\" for available commands");
    }

    private static Map<String, String> parseSettings(String input) {
        Map<String, String> settings = new HashMap<String, String>();

        // Tokenize
        StringReader reader = new StringReader(input);
        StreamTokenizer st = new StreamTokenizer(reader);

        // We don't really want to parse numbers as numbers...
        st.ordinaryChar('-');
        st.ordinaryChar('.');
        st.ordinaryChars('0', '9');

        st.wordChars('-', '-');
        st.wordChars('.', '.');
        st.wordChars('0', '9');

        List<String> tokenList = new ArrayList();
        int tokenIndex = 0;
        int i;

        try {
            while ((i = st.nextToken()) != StreamTokenizer.TT_EOF) {
                switch (i) {
                case '=':
                    // Get the next token
                    i = st.nextToken();

                    if ((i == StreamTokenizer.TT_WORD) || (i == '"')) {
                        tokenIndex--;

                        String key = tokenList.get(tokenIndex);
                        settings.put(key, st.sval);
                        tokenList.remove(tokenIndex);
                    }

                    break;

                case '"':
                case StreamTokenizer.TT_WORD:
                    tokenList.add(st.sval);
                    tokenIndex++;

                    break;

                default:
                    break;
                }
            }
        } catch (Exception e) {
        }

        return settings;
    }

	private static String genKey(String ns, String n) {
		return "^(" + ns + "\\s+" + n + ")((\\s+\\S+)*)$";
	}

}
