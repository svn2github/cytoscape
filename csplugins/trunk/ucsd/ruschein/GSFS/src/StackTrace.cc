/** \file    StackTrace.cc
 *  \brief   Installs default handler for serious, usually fatal, signals and exceptions
 *  \brief   that displays stack data useful for debugging
 *  \author  Walter Howard
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

#include <StackTrace.h>
#include <cxxabi.h>
#include <cerrno>
#include <cstdarg>
#include <vector>
#include <execinfo.h>
#include <fcntl.h>
#include <wait.h>
#include <FileUtil.h>
#include <StringUtil.h>
#include <PerlCompatRegExp.h>


// Set the default action_on_error_ from an environment so the developer or user can set a long term action:
// The Value here must be a number literal but is taken from the TerminateHandler::TerminateAction enum.
TerminateHandler::TerminateAction TerminateHandler::action_on_error_ = TerminateHandler::GetActionViaEnvironment();

// IMPORTANT! This single object constructed here installs custom terminate handlers just by linking in
// this module. However, unless the environment variable IVIA_EXCEPTION_ACTION is set, it does nothing
TerminateHandler root_terminate_handler;


namespace {


void WriteToStderr(const std::string &message)
{
	::write(STDERR_FILENO, message.c_str(), message.length());
}


void WriteToStdout(const std::string &message)
{
	::write(STDOUT_FILENO, message.c_str(), message.length());
}


void ChildProcessTimeout(int)
{
	WriteToStderr("GDB stack trace timeout after 6 seconds.\n");
}


} // unnamed namespace


TerminateHandler::TerminateAction TerminateHandler::GetActionFromString(const std::string &action_string)
{
	// Figure out what an environment value like "IVIA_EXCEPTION_ACTION=WEAK_TRACE+TRAP_MANY_SIGNALS" means:
	unsigned action_number(0);

	std::list<std::string> symbolic_values;
	StringUtil::SplitThenTrim(action_string, "+|/\t ", "", &symbolic_values);
	for (std::list<std::string>::const_iterator value(symbolic_values.begin()); value != symbolic_values.end(); ++value) {
		int index = std::atoi(value->c_str()); // Is this a literal number?
		if (index != 0)
			action_number |= index;
		else {
			index = IndexOfString(value->c_str(), "DO_NOTHING", "GDB_BACKTRACE", "WEAK_BACKTRACE",
					      "TRAP_MANY_SIGNALS", "USE_CURRENT", "LOCAL_VARIABLES", NULL);
			if (index != -1)
				action_number |= (1 << index) >> 1;
			else
				WriteToStderr("Unknown IVIA_EXCEPTION_ACTION action: " + *value + "\n" +
					      "available actions: DO_NOTHING GDB_BACKTRACE WEAK_BACKTRACE TRAP_MANY_SIGNALS LOCAL_VARIABLES\n");
		}
	}

	return static_cast<TerminateAction>(action_number);
}


TerminateHandler::TerminateAction TerminateHandler::GetActionViaEnvironment()
{
	// Figure out what an environment value like "WEAK_TRACE+TRAP_MANY_SIGNALS" means:
	unsigned action_number(0);
	const char *action_string = ::getenv("IVIA_EXCEPTION_ACTION");
	if (action_string == NULL)
		action_number = WEAK_BACKTRACE|TRAP_MANY_SIGNALS;
	else {
		action_number = GetActionFromString(action_string);
	}

	return static_cast<TerminateAction>(action_number);
}


const std::string TerminateHandler::SelectString(const unsigned zero_based_selection, const char *string0, ...)
{
	va_list args;
	va_start(args, string0);

	std::vector<const char *> choices;
	choices.push_back(string0);

	const char *choice(NULL);

	for (unsigned which(1); which <= zero_based_selection; ++which) {
		choice = va_arg(args, const char *);
		if (choice == NULL or which > zero_based_selection) {
			va_end(args);
			return ""; // Could not find value, return empty string.
		}
		if (which == zero_based_selection) { // Found a string at index? Return it.
			va_end(args);
			return choice;
		}
		choices.push_back(choice);
	}

	return "";
}


int TerminateHandler::IndexOfString(const char * const search_for_string, const char * const strings_to_search_in_0, ...)
{
	va_list args;
	va_start(args, strings_to_search_in_0);
	int index(0);

	for (const char* current = strings_to_search_in_0; current != NULL; current = va_arg(args, const char *)) {
		if (::strcasecmp(current, search_for_string) == 0) {
			va_end(args);
			return index;
		}
		++index;
	}
	va_end(args);
	return -1;
}


TerminateHandler::TerminateHandler(const TerminateAction action)
	: prior_terminate_(0), prior_unexpected_(0), previous_action_on_error_(action_on_error_)
{
	if (not (action & USE_CURRENT))
		action_on_error_ = action;

	setHandlers();
}


TerminateHandler::~TerminateHandler()
{
	action_on_error_ = previous_action_on_error_;
	removeHandlers();
}


sig_t TerminateHandler::SetSignalHandler(const int signal, const sig_t handler)
{
	struct sigaction new_action;
	new_action.sa_handler = handler;
	sigemptyset(&new_action.sa_mask);
#ifdef SA_INTERRUPT
	new_action.sa_flags = SA_INTERRUPT;
#else
	new_action.sa_flags = 0;
#endif
	struct sigaction old_action;
	int success = ::sigaction(signal, &new_action, &old_action);

	if (success < 0) {
		WriteToStderr("Setting handler for signal:" + StringUtil::ToString(signal) + " " + ErrorInfo() + "\n");
		::_exit(EXIT_FAILURE);
	}

	return old_action.sa_handler;
}


void TerminateHandler::SetActionOnError(const TerminateHandler::TerminateAction actions)
{
	action_on_error_ = actions;
	// If we change the termination behavior, reset the root handler
	root_terminate_handler.setHandlers();
}


void TerminateHandler::setHandlers()
{
	if (action_on_error_ == DO_NOTHING)
		return;

	prior_terminate_ = std::set_terminate(CustomTerminate);
	prior_unexpected_ = std::set_unexpected(CustomUnexpected);
	prior_sig_handlers_[SIGSEGV] = SetSignalHandler(SIGSEGV, CustomSigHandler);
	prior_sig_handlers_[SIGILL]  = SetSignalHandler(SIGILL, CustomSigHandler);
	if (action_on_error_ & TRAP_MANY_SIGNALS) {
		prior_sig_handlers_[SIGABRT] = SetSignalHandler(SIGABRT, CustomSigHandler);
		prior_sig_handlers_[SIGFPE]  = SetSignalHandler(SIGFPE, CustomSigHandler);
		prior_sig_handlers_[SIGQUIT] = SetSignalHandler(SIGQUIT, CustomSigHandler);
		prior_sig_handlers_[SIGTRAP] = SetSignalHandler(SIGTRAP, CustomSigHandler);
		prior_sig_handlers_[SIGTERM] = SetSignalHandler(SIGTERM, CustomSigHandler);
		prior_sig_handlers_[SIGSYS] = SetSignalHandler(SIGSYS, CustomSigHandler);
		prior_sig_handlers_[SIGHUP] = SetSignalHandler(SIGHUP, CustomSigHandler);
		prior_sig_handlers_[SIGINT] = SetSignalHandler(SIGINT, CustomSigHandler);
		prior_sig_handlers_[SIGXCPU] = SetSignalHandler(SIGXCPU, CustomSigHandler);
		prior_sig_handlers_[SIGXFSZ] = SetSignalHandler(SIGXFSZ, CustomSigHandler);
		prior_sig_handlers_[SIGBUS] = SetSignalHandler(SIGBUS, CustomSigHandler);
		prior_sig_handlers_[SIGPIPE] = SetSignalHandler(SIGPIPE, CustomSigHandler);
	}
}


void TerminateHandler::removeHandlers()
{
	/*
	  It's possible that the user CHANGED action_on_error_ manually, therefore we can't use it
	  to detect whether we changed the terminate handler functions. Therefore we check prior_signal_handlers
	  to see if it contains anything which means we DID set stuff when we constructed.
	 */
	if (prior_sig_handlers_.empty())
		return;
	std::set_terminate(prior_terminate_);
	std::set_unexpected(prior_unexpected_);
	for(PriorSigHandlers::iterator sig(prior_sig_handlers_.begin());
	    sig != prior_sig_handlers_.end();
	    ++sig)
		signal(sig->first, prior_sig_handlers_[sig->first]);
	prior_sig_handlers_.clear();
}


void TerminateHandler::Common(const std::string &message)
{
	std::string local_message(MsgUtil::GetProgName() + "\n" + message);
	if (action_on_error_ & GDB_BACKTRACE)
		local_message += StackTrace().getGdbBacktrace();
	else if (action_on_error_ & WEAK_BACKTRACE)
		local_message += StackTrace().getNonGdbBacktrace();

	// We are in a sensitive situation where not much can be done except to print the error:
	WriteToStderr(local_message);
	::_exit(EXIT_FAILURE);
}


void TerminateHandler::CustomUnexpected()
{
	// This one occurs if the exception is not caught by a catch clause:
	Common("Unexpected exception\n");
}


void TerminateHandler::CustomTerminate()
{
	// This one occurs if an exception is thrown DURING an exception being thrown:
	Common("Unhandled exception\n");
}


void TerminateHandler::CustomSigHandler(const int signal)
{
	// Log the stack trace and exit:
	std::string message = MsgUtil::GetProgName() + "\nSignal " + StringUtil::ToString(signal) + " " +
		SelectString(signal, "", "SIGHUP", "SIGINT", "SIGQUIT", "SIGILL", "SIGTRAP", "SIGABRT", "SIGBUS", "SIGFPE", "SIGKILL", "SIGUSR1",
			     "SIGSEGV", "SIGUSR2", "SIGPIPE", "SIGALRM", "SIGTERM", "SIGSTKFLT", "SIGCHLD", "SIGCONT", "SIGSTOP", "SIGTSTP", "SIGTTIN",
			     "SIGTTOU", "SIGURG", "SIGXCPU", "SIGXFSZ", "SIGVTALRM", "SIGPROF", "SIGWINCH", "SIGIO", "SIGPWR", "SIGSYS", "SIGRTMIN",
			     reinterpret_cast<char *>(NULL)) + "\n";

	if (action_on_error_ & GDB_BACKTRACE)
		message += StackTrace().getGdbBacktrace();
	else if (action_on_error_ & WEAK_BACKTRACE)
		message += StackTrace().getNonGdbBacktrace();

	// We are in a sensitive situation where not much can be done except to print the error:
	WriteToStderr(message);
	SetSignalHandler(SIGABRT, SIG_IGN);
	::_exit(EXIT_FAILURE);
}


void TerminateHandler::CustomSigActionHandler(const int signum, siginfo_t *, void *)
{
	CustomSigHandler(signum);
}


std::string StackTrace::getGdbBacktrace()
{
	if (not FileUtil::Exists(GDB))
		return getNonGdbBacktrace();

	const pid_t pid_to_debug(::getpid());

	const std::string stack_trace_filename(StringUtil::Format("/tmp/stack_trace_%s_%d.tmp", MsgUtil::GetProgName().c_str(),
								  static_cast<int>(pid_to_debug)));

	// Both parent and child will write and read from this same file:
	const int output_file_handle = ::creat(stack_trace_filename.c_str(), S_IRWXU);
	if (output_file_handle == -1 )
		return getNonGdbBacktrace();

        // Execute gdb in batch mode and have it dump a stack trace to a file:
	const pid_t process_id = ::fork();

	if (process_id == -1)
		return getNonGdbBacktrace();

	if (process_id == 0) {
		// Redirect standard out to stack_trace_filename opened earlier:
		if (-1 == ::dup2(output_file_handle, STDOUT_FILENO)) {
			const std::string message(std::string("Unable to dup: ") += MsgUtil::ErrnoToString());
			WriteToStderr(message.c_str());
			std::exit(EXIT_FAILURE);
		}

		// Redirect standard error to stack_trace_filename opened earlier:
		if (-1 == ::dup2(output_file_handle, STDERR_FILENO)) {
			const std::string message(std::string("Unable to dup: ") += MsgUtil::ErrnoToString());
			WriteToStderr(message.c_str());
			std::exit(EXIT_FAILURE);
		}

		// Remove path from GDB leaving only executable name for used as arg0
		const char *arg0(GDB);
		if (const char *dir_char = std::strrchr(GDB, '/'))
			arg0 = dir_char + 1;

		WriteToStderr(MsgUtil::GetProgName());
		WriteToStderr("\n");
		WriteToStderr(StringUtil::Format("gdb command: %s --quiet --batch attach %u --eval-command=bt full\n", arg0, pid_to_debug));

		if (-1 == ::execlp(GDB, arg0, "--quiet", "--batch", "attach", StringUtil::ToString(static_cast<int>(pid_to_debug)).c_str(),
				   "--eval-command=bt full", reinterpret_cast<char *>(NULL)))
		{
			const std::string message(std::string("Unable to exec debugger:") += MsgUtil::ErrnoToString());
			WriteToStderr(message);
			std::exit(EXIT_FAILURE);
		}
	}
	else {
		TerminateHandler::SetSignalHandler(SIGALRM, ChildProcessTimeout);

		::alarm(10);
		int status(0);
		int rval = ::waitpid(process_id, &status, 0);
		::alarm(0);

		// If there is a waitpid error something is terribly wrong or a timeout occurred. In either case,
		// Kill the child outright:
		if (rval == -1) {
			::alarm(0);
			::kill(process_id, SIGKILL);
			return getNonGdbBacktrace();
		}

		if (not(WIFEXITED(status) and WEXITSTATUS(status) == EXIT_SUCCESS))
			WriteToStderr(std::string(GDB) + " " + MsgUtil::ErrnoToString());
	}


	std::ifstream file(stack_trace_filename.c_str());
	std::string stack_string = std::string(std::istreambuf_iterator<char>(file), std::istreambuf_iterator<char>());

	// Rewind trace file in preparation for reading it in:
	if (::lseek(output_file_handle, 0, SEEK_SET) != 0) {
		const std::string msg("in StackTrace::gdbBacktrace: lseek(2) failed (" + MsgUtil::ErrnoToString() + ")!");
		WriteToStderr(msg.c_str());
	}

	char read_buffer[1024];
	while (const ssize_t amount = ::read(output_file_handle, read_buffer, sizeof(read_buffer)) > 0) {
		read_buffer[amount] = '\0';
		stack_string += read_buffer;
	}

	::close(output_file_handle);
	::unlink(stack_trace_filename.c_str());
	if (TerminateHandler::GetActionOnError() & TerminateHandler::LOCAL_VARIABLES)
		return stack_string;

	// Remove lines that are not functions on the stack.
	stack_string = PerlCompatRegExp::Subst("^[^#].*\n", "", stack_string, /* global = */ true, PCRE_MULTILINE);
	// Replace file:line with file(line): so editors who read compiler error messages and interpret then can also interpret
	// stack trace lines
	stack_string = PerlCompatRegExp::Subst("^([^ ]+) +([^ ]+) +(.+)( at )(.+)(:+)([0123456789]+)$", "$1  $2 $5($7): $3", stack_string,
					       /* global = */ true, PCRE_MULTILINE);

	return stack_string;
}


std::string StackTrace::Demangle(const char * const mangled_function_prototype)
{
	char extracted_symbol[1024];
	if (std::sscanf(mangled_function_prototype, "%*[^(](%[^)]", extracted_symbol) < 1)
		return mangled_function_prototype; // Could not understand input format. Don't try.

	// If the symbol has a trailing offset (example: +04a1), split extracted symbol at the + sign
        // by inserting a '\0' thereby creating two C style strings: extracted symbol and offset + 1.
	char *offset = std::strrchr(extracted_symbol, '+');
	if (offset != NULL)
		*(offset++) = '\0';
	char demangled_symbol[1024];
        size_t demangled_symbol_length(sizeof demangled_symbol);
	int status(0);

	__cxxabiv1::__cxa_demangle(extracted_symbol, demangled_symbol, &demangled_symbol_length, &status);
	if (status != 0)
		return mangled_function_prototype; // Error demangling. Just return original;

	std::string demangled(demangled_symbol);
	if (offset != NULL)
		(demangled += "+") += offset;

	return demangled;
}


std::string StackTrace::getNonGdbBacktrace()
{
	std::string stack_trace;
	void *stack_frames[200];
	const int stack_depth = ::backtrace(stack_frames, 20);
	char **symbols = ::backtrace_symbols(stack_frames, stack_depth);
	for (int i(0); i < stack_depth; ++i) {
		// Only use file name if full path is present in symbol
		const char *path_char = std::strrchr(symbols[i], '/');
		if (path_char)
			stack_trace += Demangle(path_char + 1) + "\n";
		else
			stack_trace += Demangle(symbols[i]) + "\n";
	}

	std::free(symbols);

	return stack_trace;
}


