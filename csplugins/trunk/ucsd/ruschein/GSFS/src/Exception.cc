/** \file    Exception.cc
 *  \brief   Exception class based on std::exception
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

#include <Exception.h>
#ifndef MSG_UTIL
#       include <MsgUtil.h>
#endif


Exception::Exception(const std::string &message)
	: message_(MsgUtil::GetProgName() + ": " + message)
{
	if (TerminateHandler::GetActionOnError() & TerminateHandler::GDB_BACKTRACE)
		backtrace_ = StackTrace().getGdbBacktrace();
	else if (TerminateHandler::GetActionOnError() & TerminateHandler::WEAK_BACKTRACE)
		backtrace_ = StackTrace().getNonGdbBacktrace();
}


const char *Exception::what(void) const throw()
{
	display_message_ = message_ + "\n" + backtrace_;
	return display_message_.c_str();
}


void Exception::appendToMessage(const std::string &appendix)
{
	message_ += appendix;
}

