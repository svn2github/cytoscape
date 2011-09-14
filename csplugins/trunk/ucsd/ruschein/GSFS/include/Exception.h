/** \file    Exception.h
 *  \brief   Exception class based on std::exception.
 *  \author  Walter Howard
 */

/*
 *  Copyright 2006 Project iVia.
 *  Copyright 2006 The Regents of The University of California.
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

#ifndef EXCEPTION_H
#define EXCEPTION_H


#include <stdexcept>
#include <string>
#include <cerrno>
#include <cstring>
#include <Compiler.h>
#include <FileUtil.h>
#include <MsgUtil.h>
#include <StackTrace.h>
#include <TimeUtil.h>


// Macros to create strings describing where and why an error occurred. Must be macros to access __FILE__ and __LINE__.
// This gobble-dee-goop necessary to turn __LINE__ into a string. See doctor dobs: http://www.ddj.com/dept/cpp/184403864
//
#define Stringize(S) ReallyStringize(S)
#define ReallyStringize(S) #S


// Used in log statements to print time, file, line.
#define LogStamp() (std::string(TimeUtil::GetCurrentTime() + ' ' + FileUtil::ExtractFilename(__FILE__) +  \
                           ":"  Stringize(__LINE__)))


#define FileAndLine() (FileUtil::ExtractFilename(__FILE__) + ":" Stringize(__LINE__))


#define ErrorLocation() (std::string("in function \"") + __PRETTY_FUNCTION__ + "\" in file \"" + \
			 FileUtil::ExtractFilename(__FILE__) + "\" at line " Stringize(__LINE__))


#define ErrorInfo() (ErrorLocation() + (errno != 0 ? std::string(" reason: ") + MsgUtil::ErrnoToString(errno) : ""))


// TestAndThrowOrReturn -- tests condition "cond" and, if it evaluates to "true", throws an exception unless another
//                         exception is already in progress.  In the latter case, TestAndThrowOrReturn() simply returns.
//
#define TestAndThrowOrReturn(cond, err_text)                                                                        \
	do {                                                                                                        \
		if (unlikely(cond)) {                                                                               \
			if (unlikely(std::uncaught_exception()))                                                    \
                		return;                                                                             \
			else                                                                                        \
				throw Exception(std::string("in ") + __PRETTY_FUNCTION__ + "(" __FILE__ ":"         \
                                                Stringize(__LINE__) "): " + std::string(err_text)                   \
					        + std::string(errno != 0 ? " (" + MsgUtil::ErrnoToString() + ")"    \
							                 : std::string("")));                       \
		}                                                                                                   \
	} while (false)


// TestAndThrowOrReturnWithValue -- like TestAndThrowOrReturn() except that "retval" will be returned if another
//                                  exception is already in progress.
//
#define TestAndThrowOrReturnWithValue(cond, err_text, retval)                                                       \
	do {                                                                                                        \
		if (unlikely(cond)) {                                                                               \
			if (unlikely(std::uncaught_exception()))                                                    \
                		return retval;                                                                      \
			else                                                                                        \
				throw Exception(std::string("in ") + __PRETTY_FUNCTION__ + "(" __FILE__ ":"         \
                                                Stringize(__LINE__) "): " + std::string(err_text)                   \
					        + std::string(errno != 0 ? " (" + MsgUtil::ErrnoToString() + ")"    \
							                 : std::string("")));                       \
		}                                                                                                   \
	} while (false)


// ThrowOrContinue -- like other TestAndThrow macros except that this throws if not already in an Exception.
//
//
#define ThrowOrContinue(err_text)                                                                                   \
        if (unlikely(std::uncaught_exception()))                                                                    \
		continue;                                                                                           \
        else                                                                                                        \
                throw Exception(std::string("in ") + __PRETTY_FUNCTION__ + "(" __FILE__ ":" Stringize(__LINE__)     \
				"):" + std::string(err_text));                                                      \


// Throw Exception if "statement" returns a false value
#define ThrowOnFalse(statement) if (unlikely(not(statement))) throw Exception(#statement " failed: " + ErrorInfo())


// Throw Exception if "statement" returns a true value
#define ThrowOnTrue(statement) if (unlikely(statement)) throw Exception(#statement " is true but should not be: " + ErrorInfo())


// Throw Exception if "statement" returns -1
#define ThrowOnError(statement) if (unlikely((statement) == -1)) throw Exception(#statement " failed: " + ErrorInfo())


// Throw Exception if "statement" returns anything except 0 and consider that value to be an errno
#define ThrowOnErrno(statement) do { int _rval = (statement); \
                                     if (_rval) \
                                     throw Exception(#statement " Failed: " + ErrorLocation() + " reason: " + MsgUtil::ErrnoToString(_rval)); \
                                   } while(false);

class Exception: public std::exception {
	std::string message_;             // Explain the error.
	std::string backtrace_;           // The stack trace showing the functions that led up to this.
	mutable std::string display_message_;
public:
        /** \brief   Creates the Exception
         *  \param   message  Text describing the error. Intended to help the catcher understand the problem.
         *  \note    Example: Exception ex("Could not open file");
         */
        explicit Exception(const std::string &message);

	Exception() { };

	~Exception() throw() { }

	const char *what() const throw();
	const std::string &getMessage() const { return message_; }
	const std::string &getBackTrace() const { return backtrace_; }
	void appendToMessage(const std::string &appendix);
};


/** Exception without a stack trace or internal information. Meant to be read and understood by a user of the program */
class UserException: public Exception {
public:
        explicit UserException(const std::string &message = "")
	{
		appendToMessage(message);
	}
};


#endif // ifndef EXCEPTION_H
