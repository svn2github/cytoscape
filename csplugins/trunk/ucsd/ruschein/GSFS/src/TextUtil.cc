/** \file    TextUtil.cc
 *  \author  Dr. Johannes Ruscheinski
 *  \author  Jiangtao Hu
 *  \brief   Implementation of text related utility functions.
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

#include <TextUtil.h>
#include <deque>
#include <MiscUtil.h>
#include <Stemmer.h>
#include <Stopwords.h>


#define DIM(array)	(sizeof(array) / sizeof(array[0]))


namespace TextUtil {


std::string EscapeChar(const char ch)
{
	if (isprint(ch) and ch != '\t' and ch != '\n' and ch != '\r' and ch != '\f' and ch != '\v')
		return std::string(1, ch);

	switch (ch) {
	case '\t':
		return "\\t";
	case '\n':
		return "\\n";
	case '\r':
		return "\\r";
	case '\f':
		return "\\f";
	case '\v':
		return "\\v";
	case '\a':
		return "\\a";
	case '\0':
		return "\\0";
	}

	std::string retval;
	retval += "\\x";
	retval += MiscUtil::HexDigit(static_cast<const unsigned char>(ch) >> 4u);
	retval += MiscUtil::HexDigit(static_cast<const unsigned char>(ch) & 0xFu);

	return retval;
}


namespace {


// IsWordChar -- helper function for HighlightStrings, returns true if "ch" is a character
//               that can a part of a `word' following an initial alphanumeric character.
//
inline bool IsWordChar(const char ch)
{
	return isalnum(static_cast<unsigned char>(ch)) or ch == '-' or ch == '\'';
}


// InvalidTrailingWordChar -- return true if "ch" is a character that is allowed within a word but
//                            not valid at the end of a word.
//
inline bool InvalidTrailingWordChar(const char ch)
{
	return ch == '-' or ch == '\'';
}


// MassageHighlightWords -- helper function for HighlightStrings, prepares the "original_highlight_words" by
//                          putting them into a form appropriate for comparison purposes in HighlightWordWhenMatched.
//
void MassageHighlightWords(const std::list<std::string> &original_highlight_words, const bool stem,
			   const bool ignore_stopwords, std::list<std::string> * const highlight_words)
{
	for (std::list<std::string>::const_iterator original_highlight_word(original_highlight_words.begin());
	     original_highlight_word != original_highlight_words.end(); ++original_highlight_word)
	{
		if (original_highlight_word->empty())
			continue;

		std::string highlight_word(*original_highlight_word);

		// Remove invalid trailing characters or "'s"
		const std::string::size_type original_length(original_highlight_word->length());
		std::string::size_type length(original_length);
		while (length > 0 and InvalidTrailingWordChar(highlight_word[length - 1]))
			--length;
		if (length != original_length)
			highlight_word = highlight_word.substr(0, length);
		else if (original_length >= 2 and highlight_word.substr(original_length - 2) == "'s")
			highlight_word = highlight_word.substr(0, original_length - 2);

		if (highlight_word.empty())
			continue;

		if (ignore_stopwords and IsStopword(highlight_word))
			continue;

		StringUtil::ToLower(&highlight_word);

		std::string ascii_highlight_word(highlight_word);
		StringUtil::AnsiToAscii(&ascii_highlight_word);

		if (stem) {
			Stemmer::stem(&highlight_word);
			Stemmer::stem(&ascii_highlight_word);
		}

		highlight_words->push_back(highlight_word);
		if (ascii_highlight_word != highlight_word)
			highlight_words->push_back(ascii_highlight_word);
	}
}


/** \class  WordMatch
 *  \brief  Used as a predicate for the find_if() STL algorithm.
 */
class WordMatch {
	std::string word_;
public:
	explicit WordMatch(std::string &word)
		: word_(word) { }

	/** If "pattern" ends in an asterisk we perfrom a prefix match, otherwise we match against the complete word. */
	bool operator()(const std::string &pattern) const;
};


bool WordMatch::operator()(const std::string &pattern) const
{
	const bool prefix_match(not pattern.empty() and pattern[pattern.length() - 1] == '*');

	if (prefix_match)
		return word_.length() >= pattern.length() - 1
			and pattern.substr(0, pattern.length() - 1) == word_.substr(0, pattern.length() - 1);
	else
		return pattern == word_;
}


// HighlightWordWhenMatched -- helper function for HighlightStrings.  Determines whether
//                             "current_word" is one of the words in "highlight_words" and if yes,
//                             concatenates it to "processed_text" bracketed by "highlight_start"
//                             and "highlight_stop" otherwise it will be appended to "processed_text"
//                             without bracketing.
//
inline void HighlightWordWhenMatched(const std::list<std::string> &highlight_words, const std::string &highlight_start,
				     const std::string &highlight_stop, const bool stem,
				     std::string * const current_word, std::string * const processed_text)
{
	// Certain characters that are allowed within a word are not valid at the end of a word => we
	// have to strip them off and later concatenate them onto "processed_text":
	std::deque<char> trailing_chars;
	while (unlikely(InvalidTrailingWordChar((*current_word)[current_word->length() - 1 - trailing_chars.size()])))
		trailing_chars.push_back((*current_word)[current_word->length() - 1 - trailing_chars.size()]);
	if (unlikely(not trailing_chars.empty()))
		*current_word = current_word->substr(0, current_word->length() - trailing_chars.size());
	else if (unlikely(current_word->length() >= 2 and current_word->substr(current_word->length() - 2) == "'s")) {
		*current_word = current_word->substr(0, current_word->length() - 2);
		trailing_chars.push_back('s');
		trailing_chars.push_back('\'');
	}

	// We match against lowercase and ASCII versions of the current word:
	std::string lowercase_word(*current_word);
	StringUtil::ToLower(&lowercase_word);
	std::string ascii_word(lowercase_word);
	StringUtil::AnsiToAscii(&ascii_word);

	if (stem) {
		Stemmer::stem(&lowercase_word);
		Stemmer::stem(&ascii_word);
	}

	if (std::find_if(highlight_words.begin(), highlight_words.end(), WordMatch(lowercase_word)) != highlight_words.end())
		*processed_text += highlight_start + *current_word + highlight_stop;
	else if (unlikely(ascii_word != lowercase_word)
		 and std::find_if(highlight_words.begin(), highlight_words.end(), WordMatch(ascii_word)) != highlight_words.end())
                *processed_text += highlight_start + *current_word + highlight_stop;
	else // Not one of the words that we want to highlight.
		*processed_text += *current_word;

	// If we stripped "current_word" off certain trailing characters, we need to
        // restore them here:
	while (unlikely(not trailing_chars.empty())) {
		do {
			*processed_text += trailing_chars.back();
			trailing_chars.pop_back();
		} while (not trailing_chars.empty());
	}
}


} // end unamed namespace


std::string &HighlightStrings(const std::list<std::string> &original_highlight_words, const std::string &highlight_start,
			      const std::string &highlight_stop, std::string * const text, const bool skip_html,
			      const bool stem, const bool ignore_stopwords)
{
	std::list<std::string> highlight_words;
	MassageHighlightWords(original_highlight_words, stem, ignore_stopwords, &highlight_words);

	std::string processed_text;
	processed_text.reserve(text->length() + 100);
	std::string current_word;
	char string_delimiter = '"';

	enum { IN_WORD, NOT_IN_WORD, IN_HTML_TAG, SKIPPING_QUOTED_STRING } state = NOT_IN_WORD;
	for (std::string::const_iterator ch(text->begin()); ch != text->end(); ++ch) {
		switch (state) {
		case IN_WORD:
			if (IsWordChar(*ch))
				current_word += *ch;
			else {
				state = NOT_IN_WORD;
				HighlightWordWhenMatched(highlight_words, highlight_start, highlight_stop, stem,
							 &current_word, &processed_text);
				processed_text += *ch;
			}
			break;
		case NOT_IN_WORD:
			if (isalnum(static_cast<unsigned char>(*ch))) {
				current_word = *ch;
				state = IN_WORD;
			}
			else {
				processed_text += *ch;
				if (*ch == '<' and skip_html)
					state = IN_HTML_TAG;
			}
			break;
		case IN_HTML_TAG:
			processed_text += *ch;
			if (unlikely(*ch == '>'))
				state = NOT_IN_WORD;
			else if (unlikely(*ch == '"')) {
				string_delimiter = *ch;
				state = SKIPPING_QUOTED_STRING;
			}
			else if (unlikely(*ch == '\'')) {
				string_delimiter = *ch;
				state = SKIPPING_QUOTED_STRING;
			}
			break;
		case SKIPPING_QUOTED_STRING:
			processed_text += *ch;
			if (unlikely(*ch == string_delimiter))
				state = IN_HTML_TAG;
			break;
		}
	}

	if (state == IN_WORD and !current_word.empty())
		HighlightWordWhenMatched(highlight_words, highlight_start, highlight_stop, stem, &current_word, &processed_text);

	return *text = processed_text;
}


std::string Base64Encode(const std::string &s, const char symbol63, const char symbol64)
{
	static char symbols[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789\0\0";
	symbols[62] = symbol63;
	symbols[63] = symbol64;

	std::string encoded_chars;
	std::string::const_iterator ch(s.begin());
	while (ch != s.end()) {
		// Collect groups of 3 characters:
		unsigned buf(static_cast<unsigned char>(*ch));
		buf <<= 8u;
		++ch;
		unsigned ignore_count(0);
		if (ch != s.end()) {
			buf |= static_cast<unsigned char>(*ch);
			++ch;
		}
		else
			++ignore_count;
		buf <<= 8u;
		if (ch != s.end()) {
			buf |= static_cast<unsigned char>(*ch);
			++ch;
		}
		else
			++ignore_count;

		// Now grab 6 bits at a time and encode them starting with the 4th character:
		char next4[4];
		for (unsigned char_no = 0; char_no < 4; ++char_no) {
			next4[4 - 1 - char_no] = symbols[buf & 0x3Fu];
			buf >>= 6u;
		}

		for (unsigned char_no = 0; char_no < 4 - ignore_count; ++char_no)
			encoded_chars += next4[char_no];
	}

	return encoded_chars;
}


std::string SqueezeWhitespace(const std::string &s)
{
	std::string result;
	result.reserve(s.length());

	bool last_char_was_a_tab_space_or_non_break_space(false);
	bool last_char_was_a_carriage_return(false);
	for (std::string::const_iterator ch(s.begin()); ch != s.end(); ++ch) {
		if (*ch == ' ' or *ch == '\t' or *ch == '\xA0') {
			if (not last_char_was_a_tab_space_or_non_break_space) {
				result += ' ';
				last_char_was_a_tab_space_or_non_break_space = true;
			}
		}
		else if (*ch == '\r') {
			if (last_char_was_a_tab_space_or_non_break_space) {
				result.resize(result.length() - 1);
				last_char_was_a_tab_space_or_non_break_space = false;
			}

			result += '\n';
			last_char_was_a_carriage_return = true;
		}
		else if (*ch == '\n') {
			if (last_char_was_a_carriage_return)
				last_char_was_a_carriage_return = false;
			else {
				if (last_char_was_a_tab_space_or_non_break_space) {
					result.resize(result.length() - 1);
					last_char_was_a_tab_space_or_non_break_space = false;
				}

				result += '\n';
			}
		}
		else {
			result += *ch;
			last_char_was_a_tab_space_or_non_break_space = false;
			last_char_was_a_carriage_return              = false;
		}
	}

	return result;
}


}// namespace TextUtil
