/** \file    Stopwords.h
 *  \brief   Declaration of function IsStopword.
 *  \author  Dr. Johannes Ruscheinski
 *  \author  Dr. Gordon W. Paynter
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

#ifndef STOPWORDS_H
#define STOPWORDS_H


#include <string>


/** \brief  Is the specified word a stopword?
 *  \param  word  The word to test.
 *  \return True if the word is a stopword.
 */
extern bool IsStopword(const std::string &word);


/** \brief  Is the specified word a "glue" stopword that can be used to link phrases?
 *  \param  word  The word to test.
 *  \return True if the word is a stopword.
 *
 *  The "gluon" stopword os a concept used in PhraseRate to identify words and phrases that are stopwords, but which may be useful in
 *  the "middle" of phrases; such as "and", "of", and "to".  (This is Keith's list, and it appears quite arbitrary.)
 *
 *  \note  There are about 35 glue stopwords.
 */
extern bool IsGlueStopword(const std::string &word);


#endif // ifndef STOPWORDS_H
