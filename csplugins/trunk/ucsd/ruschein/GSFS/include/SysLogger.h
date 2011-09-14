/** \file    SysLogger.h
 *  \brief   Declaration of class SysLogger.
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

#ifndef SYS_LOGGER_H
#define SYS_LOGGER_H


#include <cstddef>
#include <syslog.h>


/** \class SysLogger
 *  \brief A convenience wrapper around the syslog(3) standard Unix logging facility API.
 *  \note  In order to see what the "options" and "facility" arguments mean and which
 *         values they can take on please refer to the man page.
 */
class SysLogger {
public:
	/** \brief for the documentation of the arguments please see syslog(3) */
	explicit SysLogger(const char * const ident = NULL, const int options = LOG_CONS | LOG_PID, const int  facility = LOG_USER);
	~SysLogger();

	/** \brief for the documentation of the arguments please see syslog(3) */
	void log(const int level, const char * const fmt, ...) __attribute__((__format__(printf,3,4)));
};


#endif // ifndef SYS_LOGGER_H
