/** \file    ProcessUtil.cc
 *  \brief   Declarations of miscellaneous utility functions.
 *  \author  Dr. Johannes Ruscheinski
 */

/*
 *  Copyright 2002-2009 Project iVia.
 *  Copyright 2002-2009 The Regents of The University of California.
 *
 *  This file is part of the libiViaCore package.
 *
 *  The libiViaCore package is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License as published
 *  by the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  libiViaCore is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with libiViaCore; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

#include <ProcessUtil.h>
#include <cctype>
#include <cerrno>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <fcntl.h>
#include <libgen.h>
#include <signal.h>
#include <sys/ioctl.h>
#include <sys/resource.h>
#include <termios.h>
#ifndef	TIOCGWINSZ // This *must* follow the <termios.h> include statement!
#       include <sys/ioctl.h>
#endif
#include <unistd.h>
#include <FileUtil.h>
#include <FileUtil.h>
#include <GnuHash.h>
#include <MsgUtil.h>
#include <StringUtil.h>
#include <TimerUtil.h>


namespace {


// The following variables are set in Execute.
static bool alarm_went_off;
pid_t child_pid;


// SigAlarmHandler -- Used by Execute.
//
void SigAlarmHandler(int /* sig_no */)
{
	alarm_went_off = true;
	::kill(-child_pid, SIGKILL);
}


} // unnamed namespace


namespace ProcessUtil {


unsigned SplitCommandLineArgs(const std::string &command_line, std::list<std::string> * const args)
{
	unsigned arg_count(0);
	std::string current_arg;
	bool escaped(false);
	bool in_quotes(false);
	char delimiter('\0');
	for (std::string::const_iterator ch(command_line.begin()); ch != command_line.end(); ++ch) {
		if (escaped) {
			if (*ch != '\n')
				current_arg += *ch;
			escaped = false;
		}
		else if (in_quotes and *ch == delimiter)
			in_quotes = false;
		else {
			if (*ch == '\\')
				escaped = true;
			else if (isspace(*ch)) {
				if (in_quotes)
					current_arg += *ch;
				else if (not current_arg.empty()) {
					args->push_back(current_arg);
					current_arg.clear();
					++arg_count;
				}
			}
			else if (not in_quotes and (*ch == '\'' or *ch == '"')) {
				in_quotes = true;
				delimiter = *ch;
			}
			else
				current_arg += *ch;
		}
	}
	if (not current_arg.empty()) {
		args->push_back(current_arg);
		++arg_count;
	}

	return arg_count;
}


int Execute(const std::string &executable_filename, const std::list<std::string> &command_line_args,
	    const std::string &stdin_filename, const std::string &stdout_filename,
	    const std::string &stderr_filename, const unsigned flags, const unsigned timeout)
{
	if (::access(executable_filename.c_str(), X_OK) != 0)
		throw Exception("in ProcessUtil::Execute: can't execute \""
					 + executable_filename + "\"!");

	if (not stdin_filename.empty() and ::access(stdin_filename.c_str(), R_OK) != 0)
		throw Exception("in ProcessUtil::Execute: can't redirect stdin from \""
					 + stdin_filename + "\" while trying to execute \""
					 + executable_filename + "\"!");

	const int STDIN_OPEN_FAILURE  = 255;
	const int STDIN_DUP2_FAILURE  = 254;
	const int STDOUT_OPEN_FAILURE = 253;
	const int STDOUT_DUP2_FAILURE = 252;
	const int STDERR_OPEN_FAILURE = 251;
	const int STDERR_DUP2_FAILURE = 250;
	const int SETPGID_FAILURE     = 249;
	const int EXECVE_FAILURE      = 248;

	const pid_t pid = ::fork();
	if (pid == -1)
		throw Exception("in ProcessUtil::Execute: ::fork() failed: " + MsgUtil::ErrnoToString() + "!");

	// The child process:
	else if (pid == 0) {
		// We're in the child.

		// Start a new process group:
		if (not (flags & RUN_IN_FOREGROUND) and ::setpgid(0, 0) == -1)
			::_exit(SETPGID_FAILURE);

		if (not stdin_filename.empty()) {
			int new_stdin = ::open(stdin_filename.c_str(), O_RDONLY);
			if (new_stdin == -1)
				::_exit(STDIN_OPEN_FAILURE);
			if (::dup2(new_stdin, STDIN_FILENO) == -1)
				::_exit(STDIN_DUP2_FAILURE);
			::close(new_stdin);
		}

		if (not stdout_filename.empty()) {
			int trunc_or_append_flag = ((flags & APPEND_STDOUT) == APPEND_STDOUT)
				                   ? O_APPEND : O_TRUNC;
			int new_stdout = ::open(stdout_filename.c_str(),
						O_WRONLY | O_CREAT | trunc_or_append_flag, 0644);
			if (new_stdout == -1)
				::_exit(STDOUT_OPEN_FAILURE);
			if (::dup2(new_stdout, STDOUT_FILENO) == -1)
				::_exit(STDOUT_DUP2_FAILURE);
			::close(new_stdout);
		}

		if (not stderr_filename.empty()) {
			int trunc_or_append_flag = ((flags & APPEND_STDERR) == APPEND_STDERR)
				                   ? O_APPEND : O_TRUNC;
			int new_stderr = ::open(stderr_filename.c_str(),
						O_WRONLY | O_CREAT | trunc_or_append_flag, 0644);
			if (new_stderr == -1)
				::_exit(STDERR_OPEN_FAILURE);
                        if (::dup2(new_stderr, STDERR_FILENO) == -1)
                                ::_exit(STDERR_DUP2_FAILURE);
			::close(new_stderr);
		}

		// Build the argument list for execve(2):
		char *argv[1 + command_line_args.size() + 1];
		argv[0] = ::strdup(executable_filename.c_str());
		unsigned next_arg_index = 1;
		for (std::list<std::string>::const_iterator arg(command_line_args.begin());
		     arg != command_line_args.end(); ++arg)
			argv[next_arg_index++] = ::strdup(arg->c_str());
		argv[next_arg_index] = NULL;
		if ((flags & USE_SEARCH_PATH) == USE_SEARCH_PATH)
			::execvp(executable_filename.c_str(), argv);
		else
			::execv(executable_filename.c_str(), argv);

		::_exit(EXECVE_FAILURE);
	}

	// The parent of the fork:
	else {
		void (*old_alarm_handler)(int) = NULL;
		if (timeout != 0) {
			// Install new alarm handler...
			alarm_went_off = false;
			child_pid = pid;
			old_alarm_handler = ::signal(SIGALRM, SigAlarmHandler);

			// ...and wind the clock:
			::alarm(timeout);
		}

		int child_exit_status;
		errno = 0;
		int wait_retval = ::wait4(pid, &child_exit_status, 0, NULL);
		MSG_UTIL_ASSERT(wait_retval == pid or errno == EINTR);

		if (timeout != 0) {
			// Cancel any outstanding alarm:
			::alarm(0);

			// Restore the old alarm handler:
			::signal(SIGALRM, old_alarm_handler);

			// Check to see whether the test timed out or not:
			if (alarm_went_off) {
				// Snuff out all our offspring.
				::kill(-pid, SIGKILL);
				while (::wait4(-pid, &child_exit_status, 0, NULL) != -1)
					/* intentionally empty! */;

				throw Exception("in ProcessUtil::Execute: \"" + executable_filename
							 + "\" timed out!");
			}
		}

		// Now process the child's various exit status values:
		if (likely(WIFEXITED(child_exit_status))) {
			switch (WEXITSTATUS(child_exit_status)) {
			case STDIN_OPEN_FAILURE:
				throw Exception("in ProcessUtil::Execute: failed to open(2) new stdin!");
			case STDIN_DUP2_FAILURE:
				throw Exception("in ProcessUtil::Execute: failed to dup2(2) stdin!");
			case STDOUT_OPEN_FAILURE:
				throw Exception("in ProcessUtil::Execute: failed to open(2) new stdout!");
			case STDOUT_DUP2_FAILURE:
				throw Exception("in ProcessUtil::Execute: failed to dup2(2) stdout!");
			case STDERR_OPEN_FAILURE:
				throw Exception("in ProcessUtil::Execute: failed to open(2) new stderr!");
			case STDERR_DUP2_FAILURE:
				throw Exception("in ProcessUtil::Execute: failed to dup2(2) stderr!");
			case SETPGID_FAILURE:
				throw Exception("in ProcessUtil::Execute: failed to setpgid(2) in child!");
			case EXECVE_FAILURE:
				throw Exception("in ProcessUtil::Execute: failed to execve(2) in child!");
			default:
				return WEXITSTATUS(child_exit_status);
			}
		}
		else if (WIFSIGNALED(child_exit_status))
			throw Exception("in ProcessUtil::Execute: \"" + executable_filename
						 + "\" killed by signal "
						 + StringUtil::ToString(WTERMSIG(child_exit_status))
						 + "!");
		else // I have no idea how we got here!
			MsgUtil::Error("in ProcessUtil::Execute: dazed and confused!");
	}

	return 0; // Keep the compiler happy!
}


namespace {


std::string pid_dir;


} // unnamed namespace


void SetPidDir(const std::string &new_pid_dir)
{
	pid_dir = new_pid_dir;
}


bool GetPidDir(std::string * const _pid_dir, const bool create)
{
	// Use the default PID directory?
	if (pid_dir.empty()) { // Yes!
		const char *home = ::getenv("HOME");
		if (home == NULL)
			throw Exception("in ProcessUtil::GetPidDir: can't retrieve value of $HOME from the "
						 "process environment!");
		pid_dir = home;
		pid_dir += "/.iViaCore/pids";
	}

	*_pid_dir = pid_dir;
	if (FileUtil::IsDirectory(pid_dir))
		return true;
	else if (create)
	        return FileUtil::MakeDirectory(pid_dir, true /* recursive */);
	else
		return false;
}


// IsProgramRunning -- uses a program's PID file to determine if it is running.
//
bool IsProgramRunning(const std::string &program_name)
{
	std::string pid_dir;
	if (not GetPidDir(&pid_dir, false /* = create */))
		return false;
	const std::string pid_file(pid_dir + "/" + program_name + ".pid");
	if (not FileUtil::Exists(pid_file))
		return false;
	if (not FileUtil::IsReadable(pid_file))
		throw Exception("can't read PID file \"" + pid_file + "\"!");

	std::ifstream pid_stream(pid_file.c_str());
	std::string pid;
	pid_stream >> pid;
	if (not StringUtil::IsUnsignedNumber(pid))
		throw Exception("contents of PID file \"" + pid_file + "\" are garbled!");

	return FileUtil::IsDirectory("/proc/" + pid);
}


bool IsProgramRunning()
{
	return IsProgramRunning(MsgUtil::GetProgName());
}


bool SavePid(const pid_t pid, const std::string &_process_name)
{
	std::string pid_dir;
	if (not GetPidDir(&pid_dir))
		throw Exception("in ProcessUtil::IsProgramRunning: can't create the PID directory!");

	const std::string process_name(_process_name.empty() ? MsgUtil::GetProgName() : _process_name);
	const std::string pid_file(pid_dir + "/" + process_name + ".pid");
	errno = 0;
	::unlink(pid_file.c_str());
	if (errno != 0) {
		if (errno == ENOENT)
			errno = 0;
		else {
			MsgUtil::SysWarning("in ProcessUtil::SavePid: cannot unlink PID file (%s)", pid_file.c_str());
			return false;
		}
	}
	mode_t old_umask = ::umask(0007);
	int pid_fd = ::open(pid_file.c_str(), O_CREAT | O_TRUNC | O_WRONLY, 0660);
	::umask(old_umask);
	if (pid_fd == -1) {
		MsgUtil::SysWarning("in ProcessUtil::SavePid: cannot open PID file (%s)", pid_file.c_str());
		return false;
	}
	std::string pid_string(StringUtil::ToString(pid) + "\n");
	bool retcode = true; // Assume success!
	if (::write(pid_fd, static_cast<const void *>(pid_string.c_str()),
		    pid_string.size()) != static_cast<ssize_t>(pid_string.size()))
	{
		MsgUtil::SysWarning("in ProcessUtil::SavePid: cannot write to PID file (%s)", pid_file.c_str());
		retcode = false;
	}
	::close(pid_fd);
	return retcode;
}


bool SavePid()
{
	return SavePid(::getpid(), MsgUtil::GetProgName());
}


bool KillProcess(const pid_t pid, const bool send_sigterm, const unsigned timeout)
{
	errno = 0;
	if (not send_sigterm)
		return ::kill(pid,SIGKILL) == 0;
	else {
		if (::kill(pid,SIGTERM) != 0)
			return false;
		::sleep(timeout);
		::kill(pid,SIGKILL);
		return true;
	}

}


bool StopDaemon(const std::string &program_name, const unsigned timeout)
{
	std::string pid_dir;
	if (not GetPidDir(&pid_dir, false /* = create */))
		throw Exception("in ProcessUtil::IsProgramRunning: can't locate the PID directory!");
	const std::string pid_file(pid_dir + "/" + program_name + ".pid");
	std::ifstream pid_stream(pid_file.c_str());
	if (pid_stream.fail())
		return false;

	pid_t pid;
	pid_stream >> pid;
	if (pid_stream.bad())
		return false;

	pid_stream.close();

	return KillProcess(pid, true /* = send_sigterm */, timeout);
}


std::string CommandLine(const std::string &executable_name, const std::list<std::string> &arglist)
{
	// Assemble the command:
	std::string command_line(executable_name);
	command_line += StringUtil::Join(arglist.begin(), arglist.end(), " ");

	FILE *command_stream = ::popen(command_line.c_str(), "r");
	if (command_stream == NULL)
		MsgUtil::Error("can't popen(3) \"%s\" for reading!", executable_name.c_str());

	char line[512 + 1];
	std::string result;
	while (std::fgets(line, sizeof(line), command_stream) != NULL) {
		result += line;
	}

	::pclose(command_stream);

	return result;
}


std::string GetWorkingDirectory()
{
	char buf[20 * PATH_MAX];
	if (::getcwd(buf, sizeof buf) != NULL)
		return buf;
	throw Exception("in ProcessUtil::GetWorkingDirectory: getcwd(3) failed ("
				 + MsgUtil::ErrnoToString() + ")!");
}


pid_t PtyFork(int * const master_fd, std::string * const slave_pty_name, const termios * const slave_termios,
	      const winsize * const slave_winsize)
{
	if ((*master_fd = ::posix_openpt(O_RDWR | O_NOCTTY)) < 0)
		throw Exception("in ProcessUtil::PtyFork: can't open master pty ("
					 + MsgUtil::ErrnoToString() + ")!");

	*slave_pty_name = ::ptsname(*master_fd);

	const pid_t pid(::fork());
	if (pid < 0)
		return -1;
	else if (pid == 0) { // We're the child.
		if (::setsid() == -1) {
			::close(*master_fd);
			throw Exception("in ProcessUtil::PtyFork: setsid(2) failed ("
						 + MsgUtil::ErrnoToString() + ")!");
		}

		if (::grantpt(*master_fd) == -1) {
			::close(*master_fd);
			throw Exception("in ProcessUtil::PtyFork: grantpt(3) failed ("
						 + MsgUtil::ErrnoToString() + ")!");
		}

		if (::unlockpt(*master_fd) == -1) {
			::close(*master_fd);
			throw Exception("in ProcessUtil::PtyFork: unlockpt(3) failed ("
						 + MsgUtil::ErrnoToString() + ")!");
		}

		const int slave_fd = ::open(slave_pty_name->c_str(), O_RDWR);
		if (slave_fd == -1) {
			::close(*master_fd);
			throw Exception("in ProcessUtil::PtyFork: open(2) failed ("
						 + MsgUtil::ErrnoToString() + ")!");
		}

#ifdef TIOCSCTTY
		// TIOCSCTTY is the BSD way to acquire a controlling terminal:
		if (::ioctl(slave_fd, TIOCSCTTY, reinterpret_cast<char *>(NULL)) == -1) {
			::close(*master_fd);
			::close(slave_fd);
			throw Exception("in ProcessUtil::PtyFork: ioctl(2) failed (1) ("
						 + MsgUtil::ErrnoToString() + ")!");
		}
#endif

		// Set slave's termios and window size:
		if (slave_termios != NULL) {
			if (::tcsetattr(slave_fd, TCSANOW, slave_termios) < 0) {
				::close(*master_fd);
				::close(slave_fd);
				throw Exception("in ProcessUtil::PtyFork: tcsetattr(3) failed ("
							 + MsgUtil::ErrnoToString() + ")!");
		}
		}
		if (slave_winsize != NULL) {
			if (::ioctl(slave_fd, TIOCSWINSZ, slave_winsize) < 0) {
				::close(*master_fd);
				::close(slave_fd);
				throw Exception("in ProcessUtil::PtyFork: ioctl(2) failed (2) ("
							 + MsgUtil::ErrnoToString() + ")!");
			}
		}

		// Slave becomes stdin/stdout/stderr of child:
		if (::dup2(slave_fd, STDIN_FILENO) != STDIN_FILENO) {
			::close(*master_fd);
			::close(slave_fd);
			throw Exception("in ProcessUtil::PtyFork: dup2(2) failed (1) ("
						 + MsgUtil::ErrnoToString() + ")!");
		}
		if (::dup2(slave_fd, STDOUT_FILENO) != STDOUT_FILENO) {
			::close(*master_fd);
			::close(slave_fd);
			throw Exception("in ProcessUtil::PtyFork: dup2(2) failed (2) ("
						 + MsgUtil::ErrnoToString() + ")!");
		}
		if (::dup2(slave_fd, STDERR_FILENO) != STDERR_FILENO) {
			::close(*master_fd);
			::close(slave_fd);
			throw Exception("in ProcessUtil::PtyFork: dup2(2) failed (3) ("
						 + MsgUtil::ErrnoToString() + ")!");
		}
		if (slave_fd != STDIN_FILENO and slave_fd != STDOUT_FILENO and slave_fd != STDERR_FILENO)
			::close(slave_fd);
		return 0; // Child returns 0 just like fork(2).
	}
	else
		return pid ; // Parent returns PID of child like fork(2).
}


typedef char *CharPtr;


	/*
#ifdef __MACH__
extern char **environ;
#endif


int PtyExec(const std::vector<std::string> &args, pid_t * const child_pid,
	    const EnvironmentPassOption environment_pass_option, std::string * const slave_pty_name,
	    const struct termios * const slave_termios, const struct winsize * const slave_winsize)
{
	int master_fd;
	std::string slave_name;
	const pid_t pid = PtyFork(&master_fd, (slave_pty_name == NULL) ? &slave_name : slave_pty_name, slave_termios,
				  slave_winsize);
	if (pid == 0) { // We're the child.
		// Initialise the argument vector for the new child process:
		char **argv = (char **)::alloca((args.size() + 1) * sizeof(char *));
		argv[0] = ::strdup(::basename(::strdup(args[0].c_str())));
		unsigned arg_no = 1;
		for (; arg_no < args.size(); ++arg_no)
			argv[arg_no] = ::strdup(args[arg_no].c_str());
		argv[arg_no] = NULL;

		char *empty_environment[1] = { NULL };
		::execve(::strdup(args[0].c_str()), argv,
			 environment_pass_option == PASS_ENVIRONMENT ? environ : empty_environment);
		throw Exception("in ProcessUtil::PtyExec: execve(2) failed ("
					 + MsgUtil::ErrnoToString() + ")!");
	}
	else if (pid < 0)
		throw Exception("in ProcessUtil::PtyExec: PtyFork() failed!");

	// If we make it here, we're the parent process:
	*child_pid = pid;
	return master_fd;
}*/


namespace {


extern "C" void SigAlarmHandler(int /* sig_no */)
{
}


} // unnamed namespace


pid_t TimedWait(const pid_t pid, const unsigned timeout, int * const status, const int options, struct rusage * const rusage)
{
	struct sigaction new_action;

	// Set up the alarm handler:
	new_action.sa_handler = SigAlarmHandler;
	sigemptyset(&new_action.sa_mask);
#ifdef SA_INTERRUPT
	new_action.sa_flags = SA_INTERRUPT;
#else
	new_action.sa_flags = 0;
#endif
	if (unlikely(::sigaction(SIGALRM, &new_action, NULL) < 0))
		throw Exception("in ProcessUtil::TimedWait: sigaction(2) failed (" + MsgUtil::ErrnoToString() + ")!");

	// Wind up the clock:
	if (unlikely(TimerUtil::malarm(timeout) == static_cast<unsigned>(-1)))
		throw Exception("in ProcessUtil::TimedWait: TimerUtil::malarm() failed!");

	errno = 0;
	const pid_t retcode(::wait4(pid, status, options, rusage));

	// Did wait4() get interrupted by a SIGALRM?
	if (errno == EINTR)
		return 0; // Yes!

	// Cancel the alarm:
	TimerUtil::malarm(0);

	return retcode;
}


unsigned GetMaxOpenFileDescriptorCount()
{
	struct rlimit limits;
	if (unlikely(::getrlimit(RLIMIT_NOFILE, &limits) != 0))
		throw Exception("in ProcessUtil::GetMaxOpenFileDescriptorCount: getrlimit(2) failed (" + MsgUtil::ErrnoToString() + ")!");
	return static_cast<unsigned>(limits.rlim_max);
}


Command::Command(const std::string &executable_name, const std::list<std::string> &arglist,const time_t &deadline, const bool &use_search_path)
	: executable_(executable_name), deadline_(deadline), use_search_path_(use_search_path)
{
	const int EXECVE_FAILURE(248);

	if (::access(executable_.c_str(), X_OK) != 0)
		throw Exception("in ProcessUtil::Command::Command: can't execute \"" + executable_ + "\"!");

	if (::pipe(pipe_from_child_fds_) == -1)
		throw Exception("in ProcessUtil::Command::Command: pipe(2) failed!");

	pid_ = ::fork();

	if (pid_ == -1)
		throw Exception("in ProcessUtil::Command::Command: ::fork() failed: " + MsgUtil::ErrnoToString() + "!");
	else if (pid_ == 0) { // the child.
		::close(pipe_from_child_fds_[0]);
		if (::dup2(pipe_from_child_fds_[1], STDOUT_FILENO) == -1)
			throw Exception("in ProcessUtil::Command::Command: dup2(2) failed in child!");

		// Build the argument list for execve(2):
		char *argv[1 + arglist.size() + 1];
		argv[0] = ::strdup(executable_.c_str());
		unsigned next_arg_index(1);
		for (std::list<std::string>::const_iterator arg(arglist.begin()); arg != arglist.end(); ++arg)
			argv[next_arg_index++] = ::strdup(arg->c_str());
		argv[next_arg_index] = NULL;
		if (use_search_path_)
			::execvp(executable_.c_str(), argv);
		else
			::execv(executable_.c_str(), argv);

		::_exit(EXECVE_FAILURE);
	}
	else // the parent.
		::close(pipe_from_child_fds_[1]);
}


std::string Command::getResult()
{
	return "";
}


} // namespace ProcessUtil
