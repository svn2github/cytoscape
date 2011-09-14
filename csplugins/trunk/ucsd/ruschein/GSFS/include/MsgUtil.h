/** \file     MsgUtil.h
 *  \brief    Declarations of message reporting utility functions.
 *  \author   Dr. Johannes Ruscheinski
 *  \author   Dr. Gordon W. Paynter
 *  \author   Artur Kedzierski
 */

/*
 *  Copyright 2002-2007 Project iVia.
 *  Copyright 2002-2007 The Regents of The University of California.
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

#ifndef MSG_UTIL_H
#define MSG_UTIL_H


#ifndef STRING
#       include <string>
#       define STRING
#endif
#ifndef COMPILER_H
#       include <Compiler.h>
#endif
#ifndef EXCEPTION_H
#       include <Exception.h>
#endif
#ifndef STRING_UTIL_H
#       include <StringUtil.h>
#endif


/** \namespace  MsgUtil
 *  \brief      Message reporting functions used in Infomine.
 */
namespace MsgUtil {


/** \brief  Usually returns the name of this program.
 *  \note   With the current implementation on Linux it will return "unknown"
 *          if the proc file system has not been mounted as /proc.
 */
extern const std::string &GetProgName();


/** \brief  Write an error to std::cerr and to the user log then exit.
 *  \param  fmt  A printf-style format string.
 *
 *  The first argument to this function is a format string; the
 *  remainder are the values to be substituted into the string and
 *  output.  The message is output to std::cerr and the user.log file;
 *  the program then exits with value EXIT_FAILURE.
 */
extern void Error(const char *fmt, ...) __attribute__((__format__(printf,1,2)));


/** \brief  Write a system error to std::cerr and to the user log then exit. */
extern void SysError(const char *fmt, ...) __attribute__((__format__(printf,1,2)));


/** \brief  Write an warning to std::cerr and to the user log. */
extern void Warning(const char *fmt, ...) __attribute__((__format__(printf,1,2)));


/** \brief  Write a system warning to std::cerr and to the user log. */
extern void SysWarning(const char *fmt, ...) __attribute__((__format__(printf,1,2)));


/** \brief  Write a "Trace" message to std::cerr and to the user log. */
extern void Trace(const char *fmt, ...) __attribute__((__format__(printf,1,2)));


/** \brief  Write an error to std::cerr and to the user log then exit. */
inline void Error(const std::string &msg)
	{ Error("%s", msg.c_str()); }


/** \brief  Write a system error to std::cerr and to the user log then exit. */
inline void SysError(const std::string &msg)
	{ SysError("%s", msg.c_str()); }


/** \brief  Write a warning to std::cerr and to the user log. */
inline void Warning(const std::string &msg)
	{ Warning("%s", msg.c_str()); }


/** \brief  Write a warning to std::cerr and to the user log. */
inline void SysWarning(const std::string &msg)
	{ SysWarning("%s", msg.c_str()); }


/** \brief  Write a "Trace" message to std::cerr and to the user log. */
inline void Trace(const std::string &msg)
	{ Trace("%s", msg.c_str()); }


/** \brief  Display an informational message as HTML to std::out.
 *  \param  message  The information to display.
 */
void HtmlInformation(const std::string &message);


/** Helper function for the MSG_UTIL_ASSERT macro. */
void AssertHelper(const char * const condition, const std::string &file, const std::string &function,
		  const unsigned line_no);



#define MSG_UTIL_ASSERT(cond)   do \
                                        if (unlikely(not (cond))) \
                                                MsgUtil::AssertHelper(#cond, __FILE__, __func__, __LINE__); \
                                while (0)


#define MSG_UTIL_THROW(msg)	do { \
					std::string xxx_error_msg(msg); \
					xxx_error_msg += " (File: \"" __FILE__ "\", line: "; \
					xxx_error_msg += StringUtil::ToString(__LINE__); \
					xxx_error_msg += ")"; \
					throw Exception(xxx_error_msg); \
                                } while (0)


#ifdef DEBUG
#define MSG_UTIL_TRACE(format, args...) MsgUtil::Trace(format, ## args)
#else
#define MSG_UTIL_TRACE(format, args...) do {} while (false)
#endif


/** \brief  Return a string representation of a system error code.
 *  \param  error_code  The error code to convert to a string.
 *  \return The string representation of error_code as returned by GNU strerror_r().
 *  \note   This function is threadsafe!
*/
std::string ErrnoToString(const int error_code = errno);


} // namespace MsgUtil


#endif // ifndef MSG_UTIL_H


