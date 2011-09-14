/** \file    GnuSList.h
 *  \brief   Attempts to make slist work with various versions of g++.
 *  \author  Dr. Johannes Ruscheinski
 */

/*
 *  Copyright 2004-2005 Project iVia.
 *  Copyright 2004-2005 The Regents of The University of California.
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

#ifndef GNU_S_LIST_H
#define GNU_S_LIST_H


#if __GNUC__ < 3
#       ifndef S_LIST
#               include <slist>
#               define S_LIST
#       endif
#else
#       ifndef EXT_S_LIST
#               include <ext/slist>
#               define EXT_S_LIST
#       endif
#endif


// As of GCC 3.1, slist is in __gnu_cxx namespace instead of std.
// The reason for it is that slist is not part of the C++ standard.
#if __GNUC__ >= 3
#       if __GNUC__ == 3 && __GNUC_MINOR__ == 0
#               define GNU_S_LIST     std::slist
#       else
#               define GNU_S_LIST     __gnu_cxx::slist
#       endif
#endif


#endif // ifndef GNU_S_LIST_H
