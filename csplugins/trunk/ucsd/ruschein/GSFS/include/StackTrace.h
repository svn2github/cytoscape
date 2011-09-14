/** \file    StackTrace.h
 *  \brief   Show a stacktrace at the current stack depth.
 *  \author  Walter Howard
 *
 *  Use gdb to display stack information
 */

/*
 *  Copyright 2006-2009 Project iVia.
 *  Copyright 2006-2009 The Regents of The University of California.
 *
 *  This file is part of the iVia Internet Portal Software.
 *
 *  iVia is free software you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

#ifndef STACK_TRACE_H
#define STACK_TRACE_H


#include <exception>
#include <map>
#include <cstdlib>
#include <signal.h>
#include <Exception.h>
#include <MsgUtil.h>


/** \class StackTrace
 *  \brief Encapsulates the concept of a list of functions that were called up to the point where this
 *  object is created.
 *  \note This is supremely useful in debugging.
*/
class StackTrace {
	std::string trace_text_;
public:
	/**\brief Gets the stack trace by executing gdb as a child program.
	 * \note While slow, speed is probably not an issue when your program is crashing and the person debugging
	 * wants to know why. This approach also has the potential capability to provide virtually unlimited debugging
	 * information such as the value of local variables in each stack frame.
	 */
	std::string getGdbBacktrace();

	/** \brief Gets the stack trace using built in libc functions.  \note This doesn't require loading a subprogram
	 *  and is limited in what it can do but currently is just as good as getGdbBacktrace
	 */
	std::string getNonGdbBacktrace();


	/**\brief Helper function used by getNonGdbBacktrace(). It uses a libc function to demangle C++ function names
	 * into normal functions names.
	 */
	std::string Demangle(const char *mangled_function_prototype);
};


/** \class TerminateHandler
  * \brief Installs custom handlers for program events that are usually fatal such as unhandled C++
  *        exceptions and signals. Writes a stack trace when this happens to aid in debugging. One instance of this class
  *        is created as a static variable in this module which means that merely linking in this module provides the
  *        signal/exception trapping capability described above. By setting the environment variable
  *        IVIA_EXCEPTION_ACTION to particular values (listed in TerminateAction) one can set how the Terminate handlers
  *        will behave. These values can be OR'd together to set multiple options.
*/
class TerminateHandler {
	// Place to save previous C++ handlers for restoration upon our destruction:
	void (*prior_terminate_)();
	void (*prior_unexpected_)();

	// Place to save previous UNIX signal handlers so we can restore is we destruct:
	typedef std::map<unsigned, sig_t> PriorSigHandlers;
	PriorSigHandlers prior_sig_handlers_;

public:
	// Options that set the behavior of this class:
	enum TerminateAction {
		DO_NOTHING        = 0x0, /** Do nothing. Don't even install termination handlers. */
		GDB_BACKTRACE     = 0x1, /** Load gdb and get a stack dump using gdb bt command.  */
		WEAK_BACKTRACE    = 0x2, /** Get backtrace using glibc::backtrace. */
		TRAP_MANY_SIGNALS = 0x4, /** Trap a lot of signals not normally trapped. */
		USE_CURRENT       = 0x8, /** Inherit values already existing in previous handlers. */
		LOCAL_VARIABLES   = 0x10 /** Attempt to additionaly display local variables. */
	};
private:
	// Preferred action when a handler is called:
	static TerminateAction action_on_error_;                    // Optional behaviors when triggered.
	TerminateAction previous_action_on_error_;                  // Restore this to action_on_error_ when we destruct
public:

	/** \brief Constructor for class. Instantiating one of these sets the Termination behavior until this object is
	 * destructed at which point its destructor will restore the previous Termination behavior.
	 \param action to
	 * take if the TerminateHandler is invoked.
	 */
	TerminateHandler(const TerminateAction action = USE_CURRENT);


	/** \brief Destructor. Removes handlers and restores previous handlers. This enables the program to provide
	 *   different Termination capabilities in different scopes.
	 */
	~TerminateHandler();


	/** \brief Sets the action string from the environment variable "IVIA_EXCEPTION_ACTION" the names in the enum
	 *         TerminateAction.
	 * \note Useful to let programmer set exception action without having to recompile code. Since this is mainly a
	 * post mortem, logging function it is often needed in production environment where access to source code and/or
	 * a debugger are not available
	 */

	static TerminateAction GetActionViaEnvironment();


	/** \brief Given a string describing the action to take, returns the numeric bitmask. Uses the same strings as
	 * the names in the enum TerminateAction.
	 * \param       action_string String containing action constants.
	 * \note        Example: GetActionFromString("GDB_BACKTRACE+TRAP_MANY_SIGNALS|LOCAL_VARIABLES")
	 * \note        The elements of the string can be separated by +|/tab or space.
	 */
	static TerminateAction GetActionFromString(const std::string &action_string);


	/** \brief  Selects one of several strings you pass in. Like a limitless tertiary operator.
	 *  \param  zero_based_selection  - Which string to select starting with 0 for the first string.
	 *  \param  string0               - The first of any number of strings followed by a NULL.
         *  \return                       - Returns the string at the zero_based_selection index.
	 *  \note                         - Example: SelectString(2, "JOE", "bill", "Sam", "Martha", NULL) would return "Sam"
	 */
	static const std::string SelectString(const unsigned zero_based_selection, const char *string0, ...);


	/** \brief    Return the index of a string or -1 if the string doesn't exist in this list (case insensitive)
	 *  \param    search_for_string        - which string you are interested in.
	 *  \param    strings_to_search_in_0   - list of strings to match against the search_for_string
         *  \return                            - returns the index of the string that matches or -1 if none match
	 *  \note     Useful to reverse an enum value back to its symbolic name.
	 *  \note     Example: SelectString("sister", "MOTHER", "FATHER", "SISTER", "BROTHER", "UNCLE", "AUNT", NULL) would return 2
	 */
	static int IndexOfString(const char *search_for_string, const char *strings_to_search_in_0, ...);


        /** \brief  Tell the program to perform a particular action when an unexpected exception, unhandled exception
	 *          or SIGSEV are generated.
	 *  \param  actions - int 0 for nothing
	 */
	static void SetActionOnError(const TerminateAction actions);


        /** \brief  Helper function to simplify use of sigaction.
	 *  \return Previous handler.
	 *  \note  Throws exception on error.
	 */
	static sig_t SetSignalHandler(const int signal, const sig_t handler);


	/** \brief Get the action to take upon encountering an error.
         *  \return The action.
	 */
	static TerminateAction GetActionOnError() { return action_on_error_; }

	static void Common(const std::string &message); // Common routine shared by all termination handlers.
private:
	void setHandlers();

	void removeHandlers();

	static void CustomTerminate();

	static void CustomUnexpected();

	static void CustomSigHandler(int);

	static void CustomSigActionHandler(int signum, siginfo_t *info, void *arg);
};


#endif // #ifndef STACK_TRACE_H

