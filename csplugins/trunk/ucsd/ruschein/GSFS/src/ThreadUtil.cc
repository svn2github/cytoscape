/** \file    ThreadUtil.cc
 *  \brief   Implementation of thread utility functions.
 *  \author  Dr. Johannes Ruscheinski
 */

/*
 *  Copyright 2003-2009 Project iVia.
 *  Copyright 2003-2009 The Regents of The University of California.
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

#include <ThreadUtil.h>
#include <stdexcept>
#include <cerrno>
#include <cstdarg>
#include <sys/syscall.h>
#include <unistd.h>
#include <File.h>
#include <MsgUtil.h>
#include <TimeUtil.h>


namespace ThreadUtil {


Semaphore::Semaphore(const unsigned initial_count)
	: type_(SINGLE_PROCESS)
{
	semaphore_ = new sem_t;
	if (::sem_init(semaphore_, 0, initial_count) != 0)
		throw Exception("in ThreadUtil::Semaphore::Semaphore: sem_init(3) failed (" + MsgUtil::ErrnoToString() + ") (1)!");
}


Semaphore::Semaphore(char * const shared_memory, const bool init, const unsigned initial_count)
	: semaphore_(reinterpret_cast<sem_t *>(shared_memory)), type_(MULTI_PROCESS)
{
	if (init and ::sem_init(semaphore_, 1, initial_count) != 0)
		throw Exception("in ThreadUtil::Semaphore::Semaphore: sem_init(3) failed (" + MsgUtil::ErrnoToString() + ") (2)!");
}


Semaphore::~Semaphore()
{
	TestAndThrowOrReturn(::sem_destroy(semaphore_) != 0, "sem_destroy(3) failed!");
	if (type_ == SINGLE_PROCESS)
		delete semaphore_;
}


void Semaphore::wait()
{
try_again:
	if (::sem_wait(semaphore_) != 0) {
		if (errno == EINTR) {
			errno = 0;
			goto try_again;
		}
		throw Exception("in ThreadUtil::Semaphore::wait: sem_wait(3) failed (" + MsgUtil::ErrnoToString() + ")!");
	}
}


void Semaphore::post()
{
try_again:
	if (::sem_post(semaphore_) != 0) {
		if (errno == EINTR) {
			errno = 0;
			goto try_again;
		}
		throw Exception("in ThreadUtil::Semaphore::post: sem_post(3) failed (" + MsgUtil::ErrnoToString() + ")!");
	}
}


Mutex::Mutex()
{
	pthread_mutexattr_t mutex_attributes;
	::pthread_mutexattr_init(&mutex_attributes);
#ifdef __linux__
	if (unlikely(::pthread_mutexattr_settype(&mutex_attributes, PTHREAD_MUTEX_ERRORCHECK_NP))) // Linux-only!
#else
	if (unlikely(::pthread_mutexattr_settype(&mutex_attributes, PTHREAD_MUTEX_ERRORCHECK)))
#endif
		throw Exception("in ThreadUtil::Mutex::lock: :pthread_mutexattr_settype(3) failed!");
	if (unlikely((errno = ::pthread_mutex_init(&mutex_, &mutex_attributes)) != 0))
		throw Exception("in ThreadUtil::Mutex::lock: pthread_mutex_init(3) failed (" + MsgUtil::ErrnoToString() + ")!");
	::pthread_mutexattr_destroy(&mutex_attributes);
}


Mutex::~Mutex()
{
	if (unlikely(errno = ::pthread_mutex_destroy(&mutex_) != 0)) {
		if (std::uncaught_exception())
			return;
		throw Exception("in ThreadUtil:Mutex::~Mutex: trying to destroy an uninitialised or currently locked mutex (" + MsgUtil::ErrnoToString()
				+ ")!");
	}
}


void Mutex::lock()
{
	if ((errno = ::pthread_mutex_lock(&mutex_)) != 0)
		throw Exception("in ThreadUtil::Mutex::lock: pthread_mutex_lock(3) failed (" + MsgUtil::ErrnoToString() + ")!");
}


bool Mutex::tryLock()
{
	const int errcode(::pthread_mutex_trylock(&mutex_));
	if (errcode == 0)
		return true;

	if (unlikely(errcode != EBUSY))
                throw Exception("in ThreadUtil::Mutex::trylock: pthread_mutex_trylock(3) failed (" + MsgUtil::ErrnoToString() + ")!");

	return false; // Lock is busy, i.e. currently held by another process!
}


void Mutex::unlock()
{
	if (unlikely((errno = ::pthread_mutex_unlock(&mutex_)) != 0)) {
		if (std::uncaught_exception())
			return;
		throw Exception("in ThreadUtil::Mutex::lock: pthread_mutex_unlock(3) failed (" + MsgUtil::ErrnoToString() + ")!");
	}
}


const size_t MAX_BUF_SIZE(2048);


void Logger::reopen(const std::string &log_filename)
{
	mutex_.lock();

	if (destroy_file_)
		delete log_file_;

	if (log_filename.empty() and log_filename_.empty())
		throw Exception("in ThreadUtil::Logger::reopen: no log file name available!");

	if (not log_filename.empty())
		log_filename_ = log_filename;

	log_file_ = new File(log_filename_.c_str(), "a");
	if (log_file_->fail())
		throw Exception("in ThreadUtil::Logger::reopen: can't open \"" + log_filename_ + "\" for logging!");

	mutex_.unlock();
}


void Logger::log(const char *fmt, ...)
{
	mutex_.lock();

	char msg_buffer[MAX_BUF_SIZE];

	va_list args;
	va_start(args, fmt);
	::vsnprintf(msg_buffer, sizeof(msg_buffer), fmt, args);
	va_end(args);

	internalLog(std::string(msg_buffer));

	mutex_.unlock();
}


void Logger::log(const std::string &message)
{
	mutex_.lock();

	*log_file_ << TimeUtil::GetCurrentDateAndTime() << " [" << ::getpid() << "]: " << message << File::endl;
	if (log_file_->fail())
		throw Exception("in ThreadUtil::Logger::logAndDie: failed to write to the log file \"" + log_filename_ + "\"!");

	mutex_.unlock();
}


void Logger::sysLog(const char *fmt, ...)
{
	mutex_.lock();

	char msg_buffer[MAX_BUF_SIZE];

	va_list args;
	va_start(args, fmt);
	::vsnprintf(msg_buffer, sizeof(msg_buffer), fmt, args);
	va_end(args);

	internalSysLog(std::string(msg_buffer));

	mutex_.unlock();
}


void Logger::sysLog(const std::string &message)
{
	mutex_.lock();

	internalSysLog(message);

	mutex_.unlock();
}


void Logger::logAndDie(const char *fmt, ...)
{
	mutex_.lock();

	char msg_buffer[MAX_BUF_SIZE];

	va_list args;
	va_start(args, fmt);
	::vsnprintf(msg_buffer, sizeof(msg_buffer), fmt, args);
	va_end(args);

	internalLogAndDie(std::string(msg_buffer));

	mutex_.unlock();
}


void Logger::logAndDie(const std::string &message)
{
	mutex_.lock();

	internalLogAndDie(message);

	mutex_.unlock();
}


void Logger::sysLogAndDie(const char *fmt, ...)
{
	mutex_.lock();

	char msg_buffer[MAX_BUF_SIZE];

	va_list args;
	va_start(args, fmt);
	::vsnprintf(msg_buffer, sizeof(msg_buffer), fmt, args);
	va_end(args);

	internalSysLogAndDie(std::string(msg_buffer));

	mutex_.unlock();
}


void Logger::sysLogAndDie(const std::string &message)
{
	mutex_.lock();

	internalSysLogAndDie(message);

	mutex_.unlock();
}


// Logger::internalLog -- this low-level function intentionally does not do its own locking.
//
void Logger::internalLog(const std::string &message)
{
	*log_file_ << TimeUtil::GetCurrentDateAndTime() << " [" << ::getpid() << "]: " << message << File::endl;
	if (log_file_->fail())
		throw Exception("in ThreadUtil::Logger::internalLog: failed to write to the log file \"" + log_filename_ + "\"!");
}


// Logger::internalSysLog -- this low-level function intentionally does not do its own locking.
//
void Logger::internalSysLog(const std::string &message)
{
	*log_file_ << TimeUtil::GetCurrentDateAndTime() << " [" << ::getpid() << "]: " << message;
	if (errno != 0)
		*log_file_ << " [" << MsgUtil::ErrnoToString() << ']';
	*log_file_ << File::endl;
	if (log_file_->fail())
		throw Exception("in ThreadUtil::Logger::internalSysLog: failed to write to the log file \"" + log_filename_ + "\"!");
}


// Logger::internalSysLogAndDie -- this low-level function intentionally does not do its own locking.
//
void Logger::internalSysLogAndDie(const std::string &message)
{
	if (already_dead_)
		std::exit(EXIT_FAILURE);

	*log_file_ << TimeUtil::GetCurrentDateAndTime() << " [" << ::getpid() << "]: Exiting: " << message;
	if (errno != 0)
		*log_file_ << " [" << MsgUtil::ErrnoToString() << ']';
	*log_file_ << File::endl;
	if (log_file_->fail()) {
		already_dead_ = true;
		throw Exception("in ThreadUtil::Logger::internalSysLogAndDie: failed to write to the log file \"" + log_filename_ + "\"!");
	}

	std::exit(EXIT_FAILURE);
}


// Logger::internalLogAndDie -- this low-level function intentionally does not do its own locking.
//
void Logger::internalLogAndDie(const std::string &message)
{
	if (already_dead_)
		std::exit(EXIT_FAILURE);

	*log_file_ << TimeUtil::GetCurrentDateAndTime() << " [" << ::getpid() << "]: Exiting: " << message << File::endl;
	if (log_file_->fail()) {
		already_dead_ = true;
		throw Exception("in ThreadUtil::Logger::internalLogAndDie: failed to write to the log file \"" + log_filename_ + "\"!");
	}

	std::exit(EXIT_FAILURE);
}


pid_t GetThreadId()
{
	return ::syscall(SYS_gettid);
}


} // namespace ThreadUtil
