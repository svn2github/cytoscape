/** \file    Stemmer.h
 *  \brief   Interface to our stemming routine.
 *  \author  Dr. Johannes Ruscheinski
 */

/*
 *  Copyright 2002-2008 Project iVia.
 *  Copyright 2002-2008 The Regents of The University of California.
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

#ifndef STEMMER_H
#define STEMMER_H


#include <string>


namespace Stemmer {


enum StemmingMethod { NO_OP=0, CASE_FOLD=1, STEM=2, CASE_FOLD_AND_STEM=3 };


char *stem(char * const word, StemmingMethod method = CASE_FOLD_AND_STEM);
std::string &stem(std::string * const word, StemmingMethod method = CASE_FOLD_AND_STEM);
inline std::string stem(const std::string &word, StemmingMethod method = CASE_FOLD_AND_STEM) { std::string temp(word); return stem(&temp, method); }


} // namespace Stemmer


#endif // STEMMER_H
