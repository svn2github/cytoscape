/** \file    MsgUtil.cc
 *  \brief   Implementation of message reporting utility functions.
 *  \author  Dr. Johannes Ruscheinski
 *  \author  Dr. Gordon W. Paynter
 *  \author  Artur Kedzierski
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

#include <MsgUtil.h>
#include <iostream>
#include <cstdarg>
#include <cstdlib>
#include <cstdio>
#include <cstring>
#include <cerrno>
#include <unistd.h>
#include <StringUtil.h>
#include <SysLogger.h>


const size_t MAX_BUF_SIZE(2048);


namespace {


std::string progname;


int InitProgname()
{
#ifdef __linux__
	FILE *cmdline = std::fopen("/proc/self/cmdline", "r");
	if (cmdline == NULL) // Maybe /proc has not been mounted.
		progname = "unknown";
	else {
		int ch;
		while ((ch = std::fgetc(cmdline)) != '\0' and ch != EOF)
			progname += static_cast<char>(ch);
		std::fclose(cmdline);

		// If we have at least one slash strip off everything before it and including the slash:
		std::string::size_type last_slash_pos = progname.rfind('/');
		if (last_slash_pos != std::string::npos)
			progname = progname.substr(last_slash_pos + 1);
	}
#else
#       error You must fix MsgUtil::InitProgname for your operating system!
#endif

	return 0; // Keep the compiler happy!
}


int dummy = InitProgname();


// PercentEscape -- replace single %-signs with double %-signs.
//
char *PercentEscape(char * const orig_buffer, const size_t orig_buffer_size)
{
	static char msg_buffer[MAX_BUF_SIZE];
	char *dest_cp = msg_buffer;
	char *orig_cp = orig_buffer;
	do {
		if (*orig_cp == '%') {
			*dest_cp++ = '%';
			*dest_cp++ = '%';
			++orig_cp;
		}
		else
			*dest_cp++ = *orig_cp++;
	} while (*orig_buffer != '\0' and dest_cp < msg_buffer+sizeof(msg_buffer)-2);
	if (dest_cp < msg_buffer+sizeof(msg_buffer)-1)
		*dest_cp = '\0';

	StringUtil::strlcpy(orig_buffer, msg_buffer, orig_buffer_size);
	return orig_buffer;
}


} // unnamed namespace


namespace MsgUtil {


const std::string &GetProgName()
{
	return ::progname;
}


// Error -- report error message and die (printf-like):
//
void Error(const char *fmt, ...)
{
	char msg_buffer[MAX_BUF_SIZE];
	std::strcpy(msg_buffer, ::progname.c_str());
	std::strcat(msg_buffer, ": Error: ");
	size_t len = std::strlen(msg_buffer);

	va_list args;
	va_start(args, fmt);
	::vsnprintf(msg_buffer+len, sizeof(msg_buffer)-len, fmt, args);
	va_end(args);

	std::fputs(msg_buffer, stderr);
	std::putc('\n', stderr);

	{
		SysLogger logger;
		logger.log(LOG_ERR, PercentEscape(msg_buffer, sizeof msg_buffer));
	}

	/** This section here so the debugger can catch Errors too using the catch throw facility. Though it does
	    nothing by itself, the debugger can be set to catch ALL throws.*/
	try { throw 0; } catch(const int &error) { }

	std::exit(EXIT_FAILURE);
}


// SysError -- report error message and die (printf-like):
//
void SysError(const char *fmt, ...)
{
	char msg_buffer[MAX_BUF_SIZE];
	std::strcpy(msg_buffer, ::progname.c_str());
	std::strcat(msg_buffer, ": Error: ");
	size_t len = std::strlen(msg_buffer);

	va_list args;
	va_start(args, fmt);
	::vsnprintf(msg_buffer+len, sizeof(msg_buffer)-len, fmt, args);
	va_end(args);
	len = std::strlen(msg_buffer);
	::snprintf(msg_buffer+len, sizeof(msg_buffer)-len, " (%s)!", ::strerror(errno));

	std::fputs(msg_buffer, stderr);
	std::putc('\n', stderr);

	{
		SysLogger logger;
		logger.log(LOG_ERR, PercentEscape(msg_buffer, sizeof msg_buffer));
	}

	/** This section here so the debugger can catch Errors too using the catch throw facility. Though it does
	    nothing by itself, the debugger can be set to catch ALL throws.*/
	try { throw 0; } catch(const int &error) { }

	std::exit(EXIT_FAILURE);
}


// Warning -- report a warning message (printf-like):
//
void Warning(const char *fmt, ...)
{
	char msg_buffer[MAX_BUF_SIZE];
	std::strcpy(msg_buffer, ::progname.c_str());
	std::strcat(msg_buffer, ": Warning: ");
	size_t len = std::strlen(msg_buffer);

	va_list args;
	va_start(args, fmt);
	::vsnprintf(msg_buffer+len, sizeof(msg_buffer)-len, fmt, args);
	va_end(args);

	std::fputs(msg_buffer, stderr);
	std::putc('\n', stderr);

	{
		SysLogger logger;
		logger.log(LOG_WARNING, PercentEscape(msg_buffer, sizeof msg_buffer));
	}
}


// SysWarning -- report error message and die (printf-like):
//
void SysWarning(const char *fmt, ...)
{
	char msg_buffer[MAX_BUF_SIZE];
	std::strcpy(msg_buffer, ::progname.c_str());
	std::strcat(msg_buffer, ": Warning: ");
	size_t len = std::strlen(msg_buffer);

	va_list args;
	va_start(args, fmt);
	::vsnprintf(msg_buffer + len, sizeof(msg_buffer) - len, fmt, args);
	va_end(args);
	len = std::strlen(msg_buffer);
	::snprintf(msg_buffer + len, sizeof(msg_buffer) - len, " (%s)!", ::strerror(errno));

	std::fputs(msg_buffer, stderr);
	std::putc('\n', stderr);

	{
		SysLogger logger;
		logger.log(LOG_ERR, PercentEscape(msg_buffer, sizeof msg_buffer));
	}
}


// Trace -- generate a trace message (printf-like):
//
void Trace(const char *fmt, ...)
{
	char msg_buffer[MAX_BUF_SIZE];
	std::strcpy(msg_buffer, ::progname.c_str());
	std::strcat(msg_buffer, ": Trace: ");
	size_t len = std::strlen(msg_buffer);

	va_list args;
	va_start(args, fmt);
	::vsnprintf(msg_buffer + len, sizeof(msg_buffer) - len, fmt, args);
	va_end(args);

	std::fputs(msg_buffer, stderr);
	std::putc('\n', stderr);

	{
		SysLogger logger;
		logger.log(LOG_WARNING, PercentEscape(msg_buffer, sizeof msg_buffer));
	}
}


// AssertHelper -- used by the MSG_UTIL_ASSERT macro.  Throws an exception.
//
void AssertHelper(const char * const condition, const std::string &file, const std::string &function,
		  const unsigned line_no)
{
	throw Exception("Assertion failed: " + std::string(condition) + " (File: " + file + ", function: "
			+ function + ", line: " + StringUtil::ToString(line_no) + ")!");
}


void HtmlInformation(const std::string &message)
{
	std::cout << "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n"
		"<html>\n"
		"  <head>\n"
		"    <script language=\"javascript\">\n"
		"      function PrintPage() {\n"
		"        document.close();\n"
		"        document.write('<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">');\n"
		"        document.write(\"  <head>\\n\");\n"
		"        document.write(\"    <title>Infomine Error</TITLE>\\n\");\n"
		"        document.write(\"  </head>\\n\");\n"
		"        document.write(\"\\n\");\n"
		"        document.write(\"  <body bgcolor=\\\"#ffffff\\\">\\n\");\n"
		"        document.write(\"    <div align=\\\"center\\\">\\n\");\n"
		"        document.write(\"      <a href=\\\"/\\\"><img src=\\\"/images/infomine.jpg\\\"></image></a>\\n\");\n"
		"        document.write(\"      <table border=\\\"1\\\" width=\\\"90%\\\">\\n\");\n"
		"        document.write(\"        <tr bgcolor=\\\"#ffffef\\\" align=\\\"center\\\">\\n\");\n"
		"        document.write(\"	      <td>\\n\");\n"
		"        document.write(\"            <font color=\\\"red\\\">\\n\");\n"
		"        document.write(\"	          Message:\\n\");\n"
		"        document.write(\"	        </font>\\n\");\n"
		"        document.write(\"	      </td>\\n\");\n"
		"        document.write(\"	    </tr>\\n\");\n"
		"        document.write(\"        <tr bgcolor=\\\"#ffffcf\\\" align=\\\"center\\\">\\n\");\n"
		"        document.write(\"	      <td>\\n\");\n"
		"        document.write(\"	        <font size=\\\"+1\\\">\\n\");\n"
		"        document.write(\"	          " << message << "\\n\");\n"
		"        document.write(\"	        </font>\\n\");\n"
		"        document.write(\"	      </td>\\n\");\n"
		"        document.write(\"	    </tr>\\n\");\n"
		"        document.write(\"      </table>\\n\");\n"
		"        document.write(\"    </div>\\n\");\n"
		"        document.write(\"  </body>\\n\");\n"
		"        document.write(\"</html>\\n\");\n"
		"        document.close();\n"
		"      }\n"
		"    </script>\n"
		"  </head>\n"
		"  <body bgcolor=\"#ffffff\" onload=\"PrintPage()\">\n"
		"  </body>\n"
		"</html>\n";
}


std::string ErrnoToString(const int error_code)
{
	char buf[1024];
	return strerror_r(error_code, buf, sizeof buf); // GNU version of strerror_r.
}


} // namespace MsgUtil
