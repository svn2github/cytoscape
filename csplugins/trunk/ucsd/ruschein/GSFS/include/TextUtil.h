/** \file    TextUtil.h
 *  \brief   Declarations of text related utility functions.
 *  \author  Dr. Johannes Ruscheinski
 *  \author  Jiangtao Hu
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

#ifndef TEXT_UTIL_H
#define TEXT_UTIL_H


#include <fstream>
#include <list>
#include <sstream>
#include <string>
#include <GnuHash.h>
#include <PerlCompatRegExp.h>


namespace TextUtil {


/** \brief   Escape a single character.
 *  \param   ch  The character to escpae.
 *  \return  Hexidecimal escape sequence.
 *
 *  Returns a hexdecimal escape sequence starting with a backslash if "ch" is not printable or a tab, newline, carriage return or form
 *  feed, otherwise "ch" itself will be returned.
 */
std::string EscapeChar(const char ch);


/*  \brief  Inserts "highlight_start" and "highlight_stop" around all words in
 *          "highlight_words" found in "text"
 *  \param  highlight_words   The words that should be highlighted.  A "word" that ends in an
 *                            asterisk is considered to be a prefix and matches any word that starts out with the prefix.
 *  \param  highlight_start   The string that initiates a highlighting sequence.
 *  \param  highlight_stop    The string that terminates a highlighting sequence.
 *  \param  text              The text that will have matching highlight_words highlighted.
 *  \param  skip_html         Skip HTML tags and entities if true.
 *  \param  stem              Stem words before comapring them for potential highlighting.
 *  \param  ignore_stopwords  Don't highlight stopwords if true.
 *  \return A reference to the processed text "*text".
 *  \note   One of the bizarre wrinkles of this function is that if any of the words in "text" when converted by
 *          StringUtil::AnsiToAscii or stripped of trailing "'s" or any trailing hyphens or single quotes matches
 *          one of the "highlight_words" it will also be highlighted.
 */
std::string &HighlightStrings(const std::list<std::string> &highlight_words, const std::string &highlight_start, const std::string &highlight_stop,
			      std::string * const text, const bool skip_html = false, const bool stem = false, const bool ignore_stopwords = true);


/** \brief  Base64 encodes a string.
 *  \param  s         The string that will be encoded.
 *  \param  symbol63  The character that will be used for symbol 63.
 *  \param  symbol64  The character that will be used for symbol 64.
 *  \return The encoded string.
 */
std::string Base64Encode(const std::string &s, const char symbol63 = '+', const char symbol64 = '/');


/** Replaces all sequences of tabs, spaces and non-break spaces with a single space.  Also removes all tabs, spaces or non-break spaces before a lineend
    and replaces single carriage returns with a newline or carriage return/newline pairs with a newline. */
std::string SqueezeWhitespace(const std::string &s);


} // namespace TextUtil


#endif // define TEXT_UTIL_H
