/** \file    ProcessUtil.h
 *  \brief   Declarations of process related utility functions.
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

#ifndef PROCESS_UTIL_H
#define PROCESS_UTIL_H


#include <list>
#include <string>
#include <vector>
#include <sys/types.h>
#include <sys/wait.h>
#include <Logger.h>
#include <SList.h>


// Forward declarations:
struct termios;
struct winsize;
struct rusage;


namespace ProcessUtil {


/** \brief  Shell-style argument processing.
 *  \param  command_line  The original command line with potential quoted strings and backslash escaped whitespace etc.
 *  \param  args          The processed, e.g. quoted strings had their quotes removed, command-line arguments.
 *  \return The number of command-line arguments returned in "args".
 */
unsigned SplitCommandLineArgs(const std::string &command_line, std::list<std::string> * const args);


const unsigned USE_SEARCH_PATH   = 1u << 0;
const unsigned APPEND_STDOUT     = 1u << 1;
const unsigned APPEND_STDERR     = 1u << 2;
const unsigned RUN_IN_FOREGROUND = 1u << 3;


/** \brief  Executes an executable file (throws upon various errors).
 *  \param  executable_filename  The name of the file to be executed.
 *  \param  command_line_args    The list of command line arguments (may be empty).
 *  \param  stdin_filename       Where to redirect stdin from if nonempty.
 *  \param  stdout_filename      Where to redirect stdout to if nonempty.
 *  \param  stderr_filename      Where to redirect stderr to if nonempty.
 *  \param  flags                Can be the bitwise or of the following values:
 *                               USE_SEARCH_PATH -- search for executable in $PATH if it doesn't have a slash in its name.
 *                               APPEND_STDOUT -- if "stdout_filename" is nonempty, open stdout for appending.
 *                               APPEND_STDERR -- if "stderr_filename" is nonempty, open stderr for appending.
 *  \param  timeout              After how many seconds to abort the running program if nonzero.
 *  \return Returns the exit code of the executed program (or throws an exception if the program couldn't be executed).
 */
int Execute(const std::string &executable_filename, const std::list<std::string> &command_line_args, const std::string &stdin_filename,
	    const std::string &stdout_filename, const std::string &stderr_filename, const unsigned flags = 0, const unsigned timeout = 0);


/** \brief  Executes an executable file (throws upon various errors).
 *  \param  executable_filename  The name of the file to be executed.
 *  \param  command_line_args    Will be processed by SplitCommandLineArgs.
 *  \param  stdin_filename       Where to redirect stdin from if nonempty.
 *  \param  stdout_filename      Where to redirect stdout to if nonempty.
 *  \param  stderr_filename      Where to redirect stderr to if nonempty.
 *  \param  flags                Can be the bitwise or of the following values:
 *                               USE_SEARCH_PATH -- search for executable in $PATH if it doesn't have a slash in its name.
 *                               APPEND_STDOUT -- if "stdout_filename" is nonempty, open stdout for appending.
 *                               APPEND_STDERR -- if "stderr_filename" is nonempty, open stderr for appending.
 *  \param  timeout              After how many seconds to abort the running program if nonzero.
 *  \return Returns the exit code of the executed program (or throws an exception if the program couldn't be executed).
 */
inline int Execute(const std::string &executable_filename, const std::string &command_line_args, const std::string &stdin_filename,
		   const std::string &stdout_filename, const std::string &stderr_filename, const unsigned flags = 0, const unsigned timeout = 0)
{
	std::list<std::string> args;
	SplitCommandLineArgs(command_line_args, &args);
	return Execute(executable_filename, args, stdin_filename, stdout_filename, stderr_filename, flags, timeout);
}


/** Allows a client to override the default PID directory. */
void SetPidDir(const std::string &new_pid_dir);


/** \brief  Retrieve the path for the directory where we store *.pid files.
 *  \param  pid_dir  The generated path to the directory.
 *  \param  create   If "true" and the PID directory does not exist we attempt to create it.
 *  \return True if we found the directory or succeeded in creating it, false otherwise.
 */
bool GetPidDir(std::string * const pid_dir, const bool create = true);


/** \brief  Uses a program's PID file to determine if it is running.
 *  \param  program_name  The name of the program that we're inquiring about.
 *  \return True if the program has been found to be running, else false.
 */
bool IsProgramRunning(const std::string &program_name);


/** \brief  Uses a program's PID file to determine if it is running.
 *  \return True if the program has been found to be running, else false.
 *  \note   Calls MsgUtil::GetProgName to determine the currently running program's name.
 */
bool IsProgramRunning();


/** \brief  Saves a program's PID in $localstatedir/run/process_name.pid.
 *  \param  pid           The PID to save.
 *  \param  process_name  The prefix for the "process_name.pid" filename.  If empty, the current process' name will be used.
 *  \note   To get more information on why this function may have failed please consult the value of errno.
 *  \return True if the process ID file has been successfully written, else false.
 */
bool SavePid(const pid_t pid, const std::string &process_name = "");


/** \brief  Saves a program's PID in $localstatedir/run/program_name.pid.
 *  \note   To get more information on why this function may have failed please consult the value of errno.
 *  \return True if the process ID file has been successfully written, else false.
 */
bool SavePid();


/** \brief  Kill a process.
 *  \param  pid           The PID of the process that should be killed.
 *  \param  send_sigterm  Whether we should send a SIGTERM before we send a SIGKILL.
 *  \param  timeout       The time to wait after we send the SIGTERM and before we send the SIGKILL.
 *  \note   To get more information on why this function may have failed please consult the value of errno.
 *  \return True if the process has been successfully terminated, else false.
 */
bool KillProcess(const pid_t pid, const bool send_sigterm = true, const unsigned timeout = 5);


/** \brief  Terminate a daemon process gracefully.
 *  \param  program_name  The name of the daemon to kill.
 *  \param  timeout       The time to wait after we send the SIGTERM and before we send the SIGKILL.
 *  \note   This function attempts to determine the PID of the process that we want to terminate by reading its PID file (program_name.pid).
 *  \note   To get more information on why this function may have failed please consult the value of errno.
 *  \return True if the daemon process has been successfully terminated, else false.
 */
bool StopDaemon(const std::string &program_name, const unsigned timeout = 5);


/** \brief  Execute an executable
 *  \param  executable_name The name of the file to be executed.
 *  \param  arglist         The command line arguments (may be empty).
 *  \return The output of the program on stdout
 */
std::string CommandLine(const std::string &executable_name, const std::list<std::string> &arglist);


/** Returns the current process' working directory if possible, otherwise throws an exeption. */
std::string GetWorkingDirectory();


/** \brief  Version of fork(2) that sets up a pty connected to the stdin/stdout/stderr of the child process.
 *  \param  master_fd       After a successful return this will be the master pty file descriptor.
 *  \param  slave_pty_name  After a successful return this will be the name of the slave pty
 *  \param  slave_termios   If this is not NULL, the slave pty's termios settings will be updated based on this.
 *  \param  slave_winsize   If this is not NULL, the slave pty's winsize settings will be updated based on this.
 *  \return The child's PID in the parent process and 0 in the child process or -1 on error.
 */
pid_t PtyFork(int * const master_fd, std::string * const slave_pty_name, const struct termios * const slave_termios = NULL,
	      const struct winsize * const slave_winsize = NULL);


enum EnvironmentPassOption { PASS_ENVIRONMENT, DO_NOT_PASS_ENVIRONMENT };


/** \brief  Execute a command using a PTY.
 *  \param  args                    An array, the zeroth element of which has to be a path to a command to be executed.
 *                                  The basename of the zeroth argument and the optional 1st, 2nd, 3rd etc. arguments
 *                                  will be passed as arguments of the command.
 *  \param  child_pid               The process ID of the spawned child process.
 *  \param  environment_pass_option Toggle passing environment to new PTY.
 *  \param  slave_pty_name          After a successful return this will be the name of the slave pty
 *  \param  slave_termios           If this is not NULL, the slave pty's termios settings will be updated based on this.
 *  \param  slave_winsize           If this is not NULL, the slave pty's winsize settings will be updated based on this.
 *  \return The master file descriptor or -1 if there was a problem.
 *  \note   You should use select(2) or some other mechanism to determine when it is safe to start I/O on the returned file descriptor.
 */
int PtyExec(const std::vector<std::string> &args, pid_t * const child_pid, const EnvironmentPassOption environment_pass_option = PASS_ENVIRONMENT,
	    std::string * const slave_pty_name = NULL, const struct termios * const slave_termios = NULL,
	    const struct winsize * const slave_winsize = NULL);


/** \brief   Wait up to a predetermined maximum amount of time for a child process to exit.
 *  \param   pid      Can be either the PID of the child to wait for, -1 to indicate any child, or a negative value indicating to wait on whose process
 *                    group ID is equal to the absolute value of pid, or 0 meaning wait for the child whose process ID is equal to the value of pid.
 *  \param   timeout  The maximum amount of time to wait in milliseconds.
 *  \param   status   If not NULL, return status information will be stored here.  See wait4(2) for details.
 *  \param   options  See wait4(2) for details.
 *  \param   rusage   If  rusage  is  not  NULL, the struct rusage to which it points will be filled with accounting information about the child.  See
 *                    getrusage(2) for details.
 *  \return  -1 if there was a syscall error (consult errno), 0 if a timeout occurred, otherwise the pid of the child that exited.
 *  \warning This function uses SIGALRM internally and is absolutely *not* threadsafe!
 *  \note    If the child did not exit as expected, the "status" and "rusage" parameters have not been set to any meaningful values and you probably
 *           should kill(2) the child process yourself and follow it up with a call to waitpid(2) to avoid a zombie process!
 */
pid_t TimedWait(const pid_t pid, const unsigned timeout, int * const status = NULL, const int options = 0, struct rusage * const rusage = NULL);


/** Returns the max. number of file descriptors for the current process. */
unsigned GetMaxOpenFileDescriptorCount();


class Command {
	int pipe_from_child_fds_[2];
	std::string executable_;
	std::string result_;
	time_t deadline_;
	bool use_search_path_;
	pid_t pid_;
public:
	Command(const std::string &executable_name, const std::list<std::string> &arglist, const time_t &deadline, const bool &use_search_path = false);
	~Command();
	std::string getResult();
private:
	bool isLate() { return deadline_ < ::time(NULL); }
};


namespace {


struct ChildInfo {
	time_t doomsday_; // the child with the pid "pid_" should not live past this time
	pid_t pid_;
	std::string url_;
public:
	ChildInfo(): doomsday_(0), pid_(0) { }
	ChildInfo(const time_t doomsday, const pid_t pid, const std::string &url)
		: doomsday_(doomsday), pid_(pid), url_(url) { }
};


class ChildInfos: public SList<ChildInfo> {
public:
	ChildInfos::const_iterator find(pid_t pid) const;
};


ChildInfos::const_iterator ChildInfos::find(pid_t pid) const
{
	for (ChildInfos::const_iterator child_info(begin()); child_info != end(); ++child_info)
		if (child_info->pid_ == pid)
			return child_info;

	return end();
}


class HasExited {
	pid_t pid_to_be_deleted_;
public:
	explicit HasExited(pid_t pid_to_be_deleted): pid_to_be_deleted_(pid_to_be_deleted) { }
	bool operator()(const ChildInfo &child_info) const { return child_info.pid_ == pid_to_be_deleted_; }
};


} // unnamed namespace


/** \brief  Framework for processing work in a specified number of subprocesses in parallel.
 *  \param  fanout                         The maximum number of child processes at any one time as spawned by this function.
 *  \param  overall_time_limit             We abort if we ever exceed this time limit.  If you want no overall time limit pass in
 *                                         TimeLimit(static_cast<unsigned>(-1)).
 *  \param  individual_process_timeout     The maximum amount of time per child process.  Any spawned child process that takes longer will be kill(2)ed.
 *  \param  max_consecutive_failure_count  Abort spawning additional child processes if at least this many consecutive failures are observed.  If this
 *                                         number has been carefully picked, this should never happend and would indicate some fundamental problem.
 *  \param  verbosity                      Controls chattiness of logging.  Should be in [0..5].
 *  \param  ProcessWorkload                The function will be called for each "workload" in a separate child process.
 *  \param  workloads                      The parameters that will individually passed into ProcessWorkload().
 *  \param  logger                         Nomen est omen.
 *  \note   Type <em>Workload</em> needs to provide a getName() member function in order to uniquely identify each workload.  This is only used for
 *          informational messages.  A really simple and lame implementation would return the address of a "Workload" as a string.
 */
template<typename Workload, typename Container> void ParalleliseChildProcesses(const unsigned fanout, const TimeLimit &overall_time_limit,
									       const unsigned individual_process_timeout, const unsigned max_consecutive_failure_count,
									       const unsigned verbosity,
									       void (*ProcessWorkload)(const Workload &workload, Logger * const logger),
									       const Container &workloads, Logger * const logger)
{
	const unsigned INDIVIDUAL_PROCESS_TIMEOUT_IN_SECONDS((individual_process_timeout + 500) / 1000);

	ChildInfos timeouts;
	unsigned consecutive_failure_count(0);
	typename Container::const_iterator workload(workloads.begin());
	do {
		// Spawn up to "fanout" many children:
		for (/*empty*/; workload != workloads.end() and timeouts.size() < fanout and not overall_time_limit.limitExceeded(); ++workload)
		{
			if (verbosity >= 4)
				logger->log("Processing \"" + workload->getName() + "\".");
			if (verbosity >= 5)
				logger->log("\t%zu %s not reaped.", timeouts.size(), (timeouts.size() == 1 ? "child" : "children"));

			pid_t pid = fork();
			if (pid == 0) { // We're the child!
				(*ProcessWorkload)(*workload, logger);
				std::exit(EXIT_SUCCESS);
			}
			else { // We're the parent!
				const time_t now = std::time(NULL); // Get the current time.
				timeouts.push_back(ChildInfo(now + INDIVIDUAL_PROCESS_TIMEOUT_IN_SECONDS, pid, workload->getName()));
			}
		}

		//
		// Reap at least one exited child.
		//

		bool reaped_at_least_one_child(false);

		// reap all exited children:
		pid_t exited_child_pid;
		do {
			int status;
			do {
				errno = 0;
				exited_child_pid = ::wait3(&status, WNOHANG, NULL);
			} while (errno == EINTR);
			if (exited_child_pid > 0) {
				reaped_at_least_one_child = true;
				ChildInfos::iterator exited_child = std::find_if(timeouts.begin(), timeouts.end(), HasExited(exited_child_pid));
				const std:: string exited_url(exited_child->url_);
				timeouts.erase(exited_child);

				if (WIFEXITED(status)) {
					int exit_code = WEXITSTATUS(status);
					if (exit_code != 0) {
						if (verbosity >= 4)
							logger->log("Bad child exit status (" + StringUtil::ToString(exit_code) + ") for " + exited_url);
						++consecutive_failure_count;
					}
					else
						consecutive_failure_count = 0;
				}
			}
		} while (exited_child_pid > 0);

		// Get the current time:
		const time_t now(std::time(NULL));

		// Kill any overdue stragglers:
		while (not timeouts.empty() and timeouts.front().doomsday_ + 2 < now) {
			::kill(timeouts.front().pid_, SIGKILL);
			errno = 0;
			do
				::waitpid(timeouts.front().pid_, NULL, 0);
			while (errno == EINTR);
			reaped_at_least_one_child = true;
			if (verbosity >= 3)
				logger->log("Killed straggler (" + StringUtil::ToString(timeouts.front().pid_) + "): " + timeouts.front().url_);

			++consecutive_failure_count;
			timeouts.pop_front();
		}

		if (consecutive_failure_count > max_consecutive_failure_count)
			throw Exception("in ProcessUtil::ParalleliseChildProcesses: Too many consecutive errors ("
					+ StringUtil::ToString(consecutive_failure_count) + ").");

		// If we didn't reap or kill anybody we want to suspend ourselves for a bit:
		if (not reaped_at_least_one_child and timeouts.size() > 0 and not overall_time_limit.limitExceeded()) {
			if (verbosity >= 5)
				logger->log("Zzzz (waiting on %zu %s).", timeouts.size(), (timeouts.size() == 1 ? "child" : "children"));
			::sleep(1);
		}
	} while ((timeouts.size() > 0 or workload != workloads.end()) and not overall_time_limit.limitExceeded());

	// We may have to kill off outstanding children because the overall timeout has been exceeded.
	const size_t overdue_count(timeouts.size());
	if (overdue_count > 0) {
		for (ChildInfos::const_iterator child(timeouts.begin()); child != timeouts.end(); ++child) {
			::kill(child->pid_, SIGKILL);
			if (verbosity >= 5)
				logger->log("Killed overdue child (" + StringUtil::ToString(child->pid_) + "): " + child->url_);
		}

		while (::waitpid(0, NULL, WNOHANG) > 0)
			/* Intentionally empty! */;

		if (verbosity >= 3)
			logger->log("Killed %zu overdue children.", overdue_count);
	}
}


} // namespace ProcessUtil


#endif // ifndef PROCESS_UTIL_H
