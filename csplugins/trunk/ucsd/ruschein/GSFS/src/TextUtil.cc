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
#include <algorithm>
#include <deque>
#include <functional>
#include <cctype>
#include <climits>
#include <cmath>
#include <cstdio>
#include <cstdlib>
#include <sys/wait.h>
#include <BinaryIO.h>
#include <BNCWordFrequencies.h>
#include <CommonContractions.h>
#include <CommonWords.h>
#include <Directory.h>
#include <File.h>
#include <FileUtil.h>
#include <GnuHash.h>
#include <GzStream.h>
#include <HMM.h>
#include <HtmlSentenceParser.h>
#include <LineIterator.h>
#include <Logger.h>
#include <HtmlUtil.h>
#include <MediaTypeUtil.h>
#include <MiscUtil.h>
#include <MsgUtil.h>
#include <PageRankAnalyzer.h>
#include <PdfToText.h>
#include <PerlCompatRegExp.h>
#include <PhitsAnalyzer.h>
#include <ProcessUtil.h>
#include <rpc/des_crypt.h>
#include <Speller.h>
#include <Stemmer.h>
#include <StlHelpers.h>
#include <Stopwords.h>
#include <StringToIndexMap.h>
#include <StringUtil.h>
#include <Thesaurus.h>


#define DIM(array)	(sizeof(array) / sizeof(array[0]))


namespace TextUtil {


Blacklister::Blacklister(const std::string &blacklist_filename)
{
	std::ifstream input(blacklist_filename.c_str());
	if (input.fail())
		throw Exception("in TextUtil::Blacklister::Blacklister: can't open \"" + blacklist_filename + "\" for reading!");

	unsigned line_no(0);
	while (not input.eof()) {
		std::string line;
		std::getline(input, line);
		++line_no;
		StringUtil::Trim(&line);
		if (line.empty())
			continue;

		// Attempt to extract the max. applicable document length:
		const std::string::size_type first_colon_pos(line.find(':'));
		if (first_colon_pos == std::string::npos or first_colon_pos == 0 or first_colon_pos == line.length() - 1)
			throw Exception("in TextUtil::Blacklister::Blacklister: invalid line #" + StringUtil::ToString(line_no) + " in \""
					+ blacklist_filename + "\" (1)!");
		unsigned max_applicable_document_length;
		if (std::sscanf(line.substr(0, first_colon_pos).c_str(), "%u", &max_applicable_document_length) != 1)
			throw Exception("in TextUtil::Blacklister::Blacklister: invalid line #" + StringUtil::ToString(line_no) + " in \""
					+ blacklist_filename + "\" (2)!");
		if (max_applicable_document_length == 0)
			max_applicable_document_length = UINT_MAX;

		// Attempt to extract the max. document match prefix:
		const std::string::size_type second_colon_pos(line.find(':', first_colon_pos + 1));
		if (second_colon_pos == std::string::npos or second_colon_pos == line.length() - 1)
			throw Exception("in TextUtil::Blacklister::Blacklister: invalid line #" + StringUtil::ToString(line_no) + " in \""
					+ blacklist_filename + "\" (3)!");
		unsigned max_document_match_prefix;
		if (std::sscanf(line.substr(first_colon_pos + 1, second_colon_pos - first_colon_pos - 1).c_str(), "%u",
				&max_document_match_prefix) != 1)
			throw Exception("in TextUtil::Blacklister::Blacklister: invalid line #" + StringUtil::ToString(line_no) + " in \""
					+ blacklist_filename + "\" (4)!");
		if (max_document_match_prefix == 0)
			max_document_match_prefix = UINT_MAX;

		pattern_infos_.push_back(PatternInfo(max_applicable_document_length, max_document_match_prefix, line.substr(second_colon_pos + 1)));
	}
}


bool Blacklister::hasBeenBlacklisted(const std::string &document) const
{
	const size_t doc_size(document.size());
	for (std::list<PatternInfo>::const_iterator info(pattern_infos_.begin()); info != pattern_infos_.end(); ++info) {
		// We only match documents that are not too long:
		if (doc_size > info->max_applicable_document_length_)
			continue;

		// Match against the entire document?
		if (doc_size <= info->max_document_match_prefix_) { // Yes!
			if (info->reg_exp_.match(document))
					return true;
		}
		// Match against a prefix only?
		else if (info->reg_exp_.match(document.substr(0, info->max_document_match_prefix_)))
			return true;
	}

	return false;
}


bool IsOrdinaryText(const std::string &possible_text)
{
	return MediaTypeUtil::GetMediaType(possible_text) == "text/plain";
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


// See the "BUGS" section of xcrypt(3) in order to understand the need for the following declaration:
extern "C" void passwd2des(char *passwd, char *key);


std::string Encrypt(const std::string &password, const std::string &data)
{
	// Create a DES key:
	char key[8];
	passwd2des(const_cast<char *>(password.c_str()), key);

	std::string encoded_data(data);

	// Pad to an integer multiple of 8 bytes using zero bytes:
	unsigned modulus = encoded_data.size() % 8;
	if (modulus != 0) {
		for (unsigned i = 0; i < 8 - modulus; ++i)
			encoded_data += '\0';
	}

	// Encode using Electronic Code Book mode:
	int retcode = ::ecb_crypt(key, const_cast<char *>(encoded_data.c_str()),
				  encoded_data.length(), DES_ENCRYPT
				  #ifdef DES_HW
				  	| DES_HW
				  #endif
		                 );
	if (DES_FAILED(retcode)) {
		std::string msg("in TextUtil::Encrypt: ecb_crypt(3) returned an error (");
		msg += retcode == DESERR_HWERROR ? "an error occurred in the hardware or driver" : "bad parameter to routine";
		msg += ")!";
		throw Exception(msg);
	}

	return encoded_data;
}


std::string Decrypt(const std::string &password, const std::string &data)
{
	// Create a DES key:
	char key[8];
	passwd2des(const_cast<char *>(password.c_str()), key);

	// Decode using Electronic Code Book mode:
	std::string decoded_data(data);
	int retcode = ::ecb_crypt(key, const_cast<char *>(decoded_data.c_str()),
				  data.length(), 0
				  #ifdef DES_DECRYPT
				  	| DES_DECRYPT
				  #endif
				  #ifdef DES_HW
				  	| DES_HW
				  #endif
		                 );
	if (DES_FAILED(retcode)) {
		std::string msg("in TextUtil::Decrypt: ecb_crypt(3) returned an error (");
		msg += retcode == DESERR_HWERROR ? "an error occurred in the hardware or driver"
			                         : "bad parameter to routine";
		msg += ")!";
		throw Exception(msg);
	}

	return decoded_data;
}


namespace {


const char * const two_letter_words[] = {
	"aa", "ab", "ad", "ae", "ag", "ah", "ai", "al", "am", "an", "ar", "as", "at", "aw", "ax", "ay", "ba",
        "be", "bi", "bo", "by", "de", "do", "ed", "ef", "eh", "el", "em", "en", "er", "es", "et", "ex", "fa",
	"go", "ha", "he", "hi", "hm", "ho", "id", "if", "in", "is", "it", "jo", "ka", "la", "li", "lo", "ma",
	"me", "mi", "mm", "mo", "mu", "my", "na", "ne", "no", "nu", "od", "oe", "of", "oh", "om", "on", "op",
	"or", "os", "ow", "ox", "oy", "pa", "pe", "pi", "re", "sh", "si", "so", "ta", "ti", "to", "uh", "um",
	"un", "up", "us", "ut", "we", "wo", "xi", "xu", "ya", "ye", "yo"
};


// WordCompare -- helper function for std::bsearch() in CleanUpWord().
//
int WordCompare(const void *word1, const void *word2)
{
	return ::strcasecmp(*reinterpret_cast<const char * const *>(word1), *reinterpret_cast<const char * const *>(word2));
}


} // unnamed namespace


bool CleanUpWord(const std::string &raw_word, std::list<std::string> * cleaned_up_words, const bool lowercase)
{
	cleaned_up_words->clear();

	std::string word(raw_word);

	// Convert everything that is not a letter, number, hyphen or quote to a space:
	for (std::string::iterator ch(word.begin()); ch != word.end(); ++ch)
		if (not isalnum(static_cast<unsigned char>(*ch)) and *ch != '-' and *ch != '\'')
			*ch = ' ';

	// Replace multiple spaces with a single space and trim leading and trailing spaces and hyphens:
	StringUtil::Collapse(&word);
	StringUtil::Trim(" -", &word);

	// We should really only have a single word!
	if (word.find(' ') != std::string::npos)
		MsgUtil::Error("in TextUtil::CleanUpWord: we have more than one word \"" + word + "\"!");

	// Collapse possessive forms, e.g. "houses'" -> "houses" and "fred's" -> "fred":
	const std::string::size_type word_length(word.length());
	if (word_length > 0 and word[word_length - 1] == '\'')
		word = word.substr(word_length - 1);
	else if (word_length > 1 and word.substr(word_length - 2) == "'s")
		word = word.substr(0, word_length - 2);

	if (word.empty() or word.find('\'') != std::string::npos)
		return false;

	const bool hyphenated = word.find('-') != std::string::npos;
	std::list<std::string> component_words;
	if (hyphenated)
		StringUtil::Split(word, "-", &component_words);
	else
		component_words.push_back(word);

	for (std::list<std::string>::iterator component_word(component_words.begin()); component_word != component_words.end(); ++component_word) {
		const std::string::size_type component_word_length(component_word->length());

		// We accept any string of more than two letters and digits as a valid "word":
		if (component_word_length > 2) {
			if (lowercase)
				StringUtil::ToLower(&(*component_word));
			cleaned_up_words->push_back(*component_word);
			continue;
		}

		// We accept all 1- and 2-letter strings of digits as a valid word:
		if (component_word_length == 2) {
			if (isdigit((*component_word)[0]) and isdigit((*component_word)[1])) {
				cleaned_up_words->push_back(*component_word);
				continue;
			}
		}
		else if (isdigit((*component_word)[0])) { // Assume component_word_length == 1
			cleaned_up_words->push_back(*component_word);
			continue;
		}

		// We explicitly check 2-letter words against a whitelist:
		if (component_word_length == 2) {
			const char *key(component_word->c_str());
			if (std::bsearch(&key, &two_letter_words[0], DIM(two_letter_words), sizeof(two_letter_words[0]), WordCompare) == NULL) {
				cleaned_up_words->clear();
				return false;
			}

			if (lowercase)
				StringUtil::ToLower(&(*component_word));
			cleaned_up_words->push_back(*component_word);
			continue;
		}

		// If we make it here we need to check 1-letter no-digit "words".  We accept [a-z] and a few
		// accented characters (we assume further that a-z form a consecutive range in our character set):
		const char lowercase_char(tolower((*component_word)[0]));
		if ((lowercase_char >= 'a' and lowercase_char <= 'z') or lowercase_char == '·') {
			if (lowercase)
				StringUtil::ToLower(&(*component_word));
			cleaned_up_words->push_back(*component_word);
			continue;
		}

		// If we get here we're in trouble!
		cleaned_up_words->clear();
		return false;
	}

	if (hyphenated) {
		if (lowercase)
			StringUtil::ToLower(&word);
		cleaned_up_words->push_front(word);
	}

	return true;
}


bool IsOrdinal(const std::string &word)
{
	// Handle special cases first:
	if (word == "0th" or word == "1st" or word == "2nd" or word == "3rd")
		return true;

	if (word.length() < 3)
		return false;

	if (word.substr(word.length() - 2) != "th")
		return false;

	if (not StringUtil::IsUnsignedNumber(word.substr(0, word.length() - 2)))
		return false;

	return word[0] != '0';
}


bool IsANumber(const std::string &number_candidate)
{
	double dummy;
	return StringUtil::ToDouble(number_candidate, &dummy);
}


bool IsPossiblyAWord(const std::string &word_candidate)
{
	if (unlikely(word_candidate.empty()))
		return false;

	if (IsANumber(word_candidate))
		return true;

	const std::string lowercase_string(StringUtil::ToLower(word_candidate));

	static CommonEnglishContractions common_english_contractions;
	if (common_english_contractions.isKnownContraction(lowercase_string))
		return true;

	bool contains_digit(false), contains_letter(false);
	bool is_first_char(true);
	char previous_ch('\0');

	unsigned consecutive_letter_count(0);
	char current_letter('\0');

	for (std::string::const_iterator ch(lowercase_string.begin()); ch != lowercase_string.end(); ++ch) {
		// A digit is read:
		if (isdigit(*ch))
			contains_digit = true;
		// A letter is read:
		else if (isalpha(*ch)) {
			contains_letter = true;
			if (*ch == current_letter) {
				++consecutive_letter_count;
				if (consecutive_letter_count > 2)
					return false;
			}
			else {
				current_letter = *ch;
				consecutive_letter_count = 1;
			}
		}
		// A hyphens and underscores must be preceeded by a letter:
		else if (*ch == '-' or *ch == '_') {
			if (is_first_char or not isalpha(previous_ch))
				return false;
		}
		else
			return false;

		previous_ch = *ch;
		is_first_char = false;
	}

	if (contains_digit and contains_letter)
		return IsOrdinal(lowercase_string);

	return true;
}


std::string InitialCaps(const std::string &s)
{
	if (s.empty())
		return s;

	std::string retval(s);
	retval[0] = toupper(retval[0]);

	return retval;
}


std::string GenerateCSV(const std::list<std::string> &values, const bool unconditionally_use_quotes)
{
	std::string csv;

	bool emit_comma(false); // Don't emit a comma before the first value!
	for (std::list<std::string>::const_iterator value(values.begin()); value != values.end(); ++value) {
		if (not emit_comma)
			emit_comma = true;
		else
			csv += ',';

		// Replace all tabs with a single space:
		const std::string tabless_value(StringUtil::Map(*value, '\t', ' '));

		// Quote the value if we either asked for doing this unconditionally or the value contains a comma or
		// a double quote character:
		const bool quote_escape(unconditionally_use_quotes or
					(StringUtil::FindAnyOf(tabless_value, ",\" ;") != std::string::npos));
		if (quote_escape) {
			csv += '"';
			for (std::string::const_iterator ch(value->begin()); ch != value->end(); ++ch) {
				if (*ch == '"') // Replace single double-quotes with two double quotes.
					csv += "\"\"";
				else
					csv += *ch;
			}
			csv += '"';
		 }
		else // Nothing to escape.
			csv += tabless_value;
	}

	return csv;
}


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


// Pseudophrase -- Generate a pseudophrase from a string.
//
std::string Pseudophrase(const std::string &s)
{
	// Eliminate all unknown characters from the input string:
	std::string text(s);
	for (std::string::iterator ch(text.begin()); ch != text.end(); ++ch)
		*ch = (isalnum(*ch) ? tolower(*ch) : ' ');

	// Elimiate stopwords and stem content words:
	std::list<std::string> words;
	StringUtil::Split(text, " ", &words);

	std::set<std::string> result;
	for (std::list<std::string>::iterator word(words.begin()); word != words.end(); ++word) {
		if (not IsStopword(*word)) {
			Stemmer::stem(&(*word));
			result.insert(*word);
		}
	}

	// Return the sorted, stemmed content-words:
	return StringUtil::Join(result, " ");
}


bool HasBalancedOpeningAndClosingChars(const std::string &s, const char opening_char, const char closing_char)
{
	unsigned unclosed_count(0);
	for (std::string::const_iterator ch(s.begin()); ch != s.end(); ++ch) {
		if (*ch == closing_char) {
			if (unclosed_count == 0)
				return false;
			--unclosed_count;
		}
		else if (*ch == opening_char)
			++unclosed_count;
	}

	return unclosed_count == 0;
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


const GNU_HASH_SET<std::string> &GetNoiseWords()
{
	static GNU_HASH_SET<std::string> common_words;
	static bool initialised(false);
	if (not initialised) {
		const std::string input_filename(ETC_DIR "/noise_words");
		std::ifstream input(input_filename.c_str());
		if (unlikely(input.fail()))
			throw Exception("in TextUtil::GetNoiseWords: can't open \"" + input_filename + "\" for reading!");
		while (initialised) {
			std::string word;
			input >> word;
			StringUtil::Trim("\t ", &word);
			if (likely(not word.empty()))
				common_words.insert(word);
		}
	}

	return common_words;
}


size_t SentenceAndWords::getWordCharCount() const
{
	if (word_char_count_ > 0 or words_.empty())
		return word_char_count_;

	for (std::vector<std::string>::const_iterator word(words_.begin()); word != words_.end(); ++word)
		word_char_count_ += word->length();

	return word_char_count_;
}


void SentenceAndWords::toLowercase()
{
	StringUtil::ToLower(&sentence_);
	for (std::vector<std::string>::iterator word(words_.begin()); word != words_.end(); ++word)
		StringUtil::ToLower(&*word);
}


std::string TextSummarizerOptionsToString(const unsigned text_summarizer_options)
{
	std::string options_as_string;

	for (unsigned bit(1); bit != 0; bit <<= 1u) {
		if ((bit & text_summarizer_options) != 0) {
			if (not options_as_string.empty())
				options_as_string += " | ";
			switch (bit) {
			case PICK_RANDOM_SENTENCES:
				options_as_string += "PICK_RANDOM_SENTENCES";
				break;
			case USE_TRIVIAL_SENTENCE_LINKING:
				options_as_string += "USE_TRIVIAL_SENTENCE_LINKING";
				break;
			case IGNORE_NOISE_WORDS:
				options_as_string += "IGNORE_NOISE_WORDS";
				break;
			case USE_GLOBAL_TF_IDF_SCALING:
				options_as_string += "USE_GLOBAL_TF_IDF_SCALING";
				break;
			case USE_THESAURUS:
				options_as_string += "USE_THESAURUS";
				break;
			case USE_STEMMING:
				options_as_string += "USE_STEMMING";
				break;
			case USE_WORD_FREQUENCIES:
				options_as_string += "USE_WORD_FREQUENCIES";
				break;
			case USE_PAGE_RANK:
				options_as_string += "USE_PAGE_RANK";
				break;
			default:
				throw Exception("in TextUtil::TextSummarizerOptionsToString: unknown option " + StringUtil::ToString(bit) + "!");
			}
		}
	}

	return options_as_string;
}


namespace {


struct PreprocessedSentence {
	GNU_HASH_SET<std::string> preprocessed_words_;
	const SentenceAndWords *original_sentence_and_words_;
public:
	PreprocessedSentence(const GNU_HASH_SET<std::string> &preprocessed_words, const SentenceAndWords * const original_sentence_and_words)
		: preprocessed_words_(preprocessed_words), original_sentence_and_words_(original_sentence_and_words) { }
};


// CalculateSentenceSimilarity -- helper function for GraphBasedTextSummarizer().  Based on equation [1] in "Extractive Automatic Summarization: Does more
//                                linguistic knowledge make a difference?".  Also added my (JR) own ingedient by using word frequencies to as weights.
//
double CalculateSentenceSimilarity(const GNU_HASH_SET<std::string> &sentence1, const GNU_HASH_SET<std::string> &sentence2, const bool stem,
				   const bool use_synonyms_and_antonyms, const Thesaurus &thesaurus, const bool use_word_frequencies,
				   const bool use_trivial_sentence_linking, const BNCWordFrequencies &word_frequencies)
{
	GNU_HASH_SET<std::string> canonised_sentence1, canonised_sentence2;

	// If stemming was requested, replace all words in both sentences with their stemmmed equivalents:
	if (stem) {
		GNU_HASH_SET<std::string> stemmed_sentence1;
		for (GNU_HASH_SET<std::string>::const_iterator word(sentence1.begin()); word != sentence1.end(); ++word)
			stemmed_sentence1.insert(Stemmer::stem(*word, Stemmer::STEM));
		canonised_sentence1.swap(stemmed_sentence1);

		GNU_HASH_SET<std::string> stemmed_sentence2;
		for (GNU_HASH_SET<std::string>::const_iterator word(sentence2.begin()); word != sentence2.end(); ++word)
			stemmed_sentence2.insert(Stemmer::stem(*word, Stemmer::STEM));
		canonised_sentence2.swap(stemmed_sentence2);
	}
	else {
		for (GNU_HASH_SET<std::string>::const_iterator word(sentence1.begin()); word != sentence1.end(); ++word)
			canonised_sentence1.insert(*word);
		for (GNU_HASH_SET<std::string>::const_iterator word(sentence2.begin()); word != sentence2.end(); ++word)
			canonised_sentence2.insert(*word);
	}

	double shared_word_score(0.0);

	if (use_trivial_sentence_linking) {
		for (GNU_HASH_SET<std::string>::const_iterator word(canonised_sentence1.begin()); word != canonised_sentence1.end(); ++word) {
			if (canonised_sentence2.find(*word) != canonised_sentence2.end())
				++shared_word_score;
		}

		return shared_word_score;
	}

	const double least_common_word_factor(0.1);
	for (GNU_HASH_SET<std::string>::const_iterator word(canonised_sentence1.begin()); word != canonised_sentence1.end(); ++word) {
		if (use_synonyms_and_antonyms) {
			GNU_HASH_SET<std::string> synonyms;
			thesaurus.getSynonyms(*word, &synonyms);
			double weight;
			bool found_a_match(false);
			if (synonyms.empty()) {
				found_a_match = canonised_sentence2.find(*word) != canonised_sentence2.end();
				if (found_a_match) {
					if (use_word_frequencies)
						weight = 1.0 / word_frequencies.getRelativeFrequency(*word, least_common_word_factor);
					else
						weight = 1.0;
				}
			}
			else {
				synonyms.insert(*word);
				for (GNU_HASH_SET<std::string>::const_iterator synonym(synonyms.begin()); synonym != synonyms.end(); ++synonym) {
					if (canonised_sentence2.find(*synonym) != canonised_sentence2.end()) {
						found_a_match = true;
						break;
					}
				}

				if (found_a_match) {
					if (use_word_frequencies) {
						weight = 0.0;
						for (GNU_HASH_SET<std::string>::const_iterator synonym(synonyms.begin()); synonym != synonyms.end();
						     ++synonym)
							weight += word_frequencies.getRelativeFrequency(*synonym, least_common_word_factor);
						weight = 1.0 / weight;
					}
					else
						weight = 1.0;
				}
			}

			if (found_a_match)
				shared_word_score += weight;
			else { // Look for antonyms.
				GNU_HASH_SET<std::string> antonyms;
				if (thesaurus.getAntonyms(*word, &antonyms)) {
					for (GNU_HASH_SET<std::string>::const_iterator antonym(antonyms.begin()); antonym != antonyms.end(); ++antonym) {
						if (canonised_sentence2.find(*antonym) != canonised_sentence2.end()) {
							found_a_match = true;
							break;
						}
					}

					if (found_a_match) {
						if (use_word_frequencies) {
							weight = 0.0;
							for (GNU_HASH_SET<std::string>::const_iterator antonym(antonyms.begin()); antonym != antonyms.end();
							     ++antonym)
								weight += word_frequencies.getRelativeFrequency(*antonym, least_common_word_factor);
							shared_word_score += (1.0 / weight) * 0.8;
						}
						else
							shared_word_score += 0.8;
					}
				}
			}
		}
		else if (canonised_sentence2.find(*word) != canonised_sentence2.end()) {
			if (use_word_frequencies)
				shared_word_score += (stem ? 1.0 / word_frequencies.getRelativeStemmedFrequency(*word, least_common_word_factor)
						           : 1.0 / word_frequencies.getRelativeFrequency(*word, least_common_word_factor));
			else
				++shared_word_score;
		}
	}

	return shared_word_score / std::log(static_cast<double>(canonised_sentence1.size() + canonised_sentence2.size()));
}


} // unnamed namespace


void GraphBasedTextSummarizer(const std::vector<SentenceAndWords> &sentences, std::vector<const SentenceAndWords *> * const ranked_sentences,
			      unsigned text_summarizer_options, const unsigned min_sentence_length, const unsigned max_sentence_count,
			      GNU_HASH_MAP<std::string, std::string> * const aliases)
{
	ranked_sentences->clear();
	if (aliases != NULL)
		aliases->clear();

	// Remove options that are mutually incompatible:
	if (text_summarizer_options & PICK_RANDOM_SENTENCES)
		text_summarizer_options = PICK_RANDOM_SENTENCES;
	else if (text_summarizer_options & USE_TRIVIAL_SENTENCE_LINKING) {
		text_summarizer_options &= ~USE_THESAURUS;
		text_summarizer_options &= ~USE_WORD_FREQUENCIES;
	}
	else if (text_summarizer_options & USE_THESAURUS)
		text_summarizer_options &= ~USE_STEMMING;

	if (text_summarizer_options & PICK_RANDOM_SENTENCES) {
		for (std::vector<SentenceAndWords>::const_iterator sentence(sentences.begin()); sentence != sentences.end(); ++sentence)
			ranked_sentences->push_back(&*sentence);
		std::random_shuffle(ranked_sentences->begin(), ranked_sentences->end());
		return;
	}

	const GNU_HASH_SET<std::string> empty_set;
	const GNU_HASH_SET<std::string> &ignore_set(text_summarizer_options & IGNORE_NOISE_WORDS ? GetNoiseWords() : empty_set);

	// Preprocess sentences:
	std::vector<PreprocessedSentence> preprocessed_sentences;
	preprocessed_sentences.reserve(sentences.size());
	GNU_HASH_MAP<uint64_t, std::string> cryptographic_hashes; // To eliminate duplicate sentences.
	for (std::vector<SentenceAndWords>::const_iterator sentence(sentences.begin());
	     sentence != sentences.end() and preprocessed_sentences.size() < max_sentence_count; ++sentence)
	{
		GNU_HASH_SET<std::string> new_preprocessed_sentence;
		for (std::vector<std::string>::const_iterator word(sentence->begin()); word != sentence->end(); ++word) {
			std::string lowercase_word(*word);
			StringUtil::ToLower(&lowercase_word);

			// Only keep words that we don't want to skip:
			if (ignore_set.find(lowercase_word) == ignore_set.end())
				new_preprocessed_sentence.insert(lowercase_word);
		}

		// Ignore duplicate sentences:
		const uint64_t folded_md5_sum(StringUtil::Md5As64Bits(StringUtil::Join(new_preprocessed_sentence, "@")));
		const GNU_HASH_MAP<uint64_t, std::string>::const_iterator hash_and_original_sentence(cryptographic_hashes.find(folded_md5_sum));
		if (hash_and_original_sentence != cryptographic_hashes.end()) {
			if (aliases != NULL and sentence->getSentence() != hash_and_original_sentence->second)
				aliases->insert(std::make_pair<std::string, std::string>(sentence->getSentence(), hash_and_original_sentence->second));
			continue;
		}
		cryptographic_hashes.insert(std::make_pair<uint64_t, std::string>(folded_md5_sum, sentence->getSentence()));

		// Only keep `sentences' that are long enough:
		if (new_preprocessed_sentence.size() >= min_sentence_length)
			preprocessed_sentences.push_back(PreprocessedSentence(new_preprocessed_sentence, &*sentence));
	}

	// Now generate the square graph matrix for the PHITS algorithm:
	SparseMatrix graph(preprocessed_sentences.size());
	const bool stem(text_summarizer_options & USE_STEMMING);
	const bool use_synonyms_and_antonyms(text_summarizer_options & USE_THESAURUS);
	const bool use_word_frequencies(text_summarizer_options & USE_WORD_FREQUENCIES);
	const bool use_trivial_sentence_linking(text_summarizer_options & USE_TRIVIAL_SENTENCE_LINKING);
	const bool use_page_rank(text_summarizer_options & USE_PAGE_RANK);
	PageRankSolver<const SentenceAndWords *> page_rank_solver;
	static const EnglishThesaurus thesaurus;
	static const BNCWordFrequencies word_frequencies;
	for (unsigned i(0); i < preprocessed_sentences.size(); ++i) {
		for (unsigned k(0); k < i; ++k) {
			const double similarity_score(CalculateSentenceSimilarity(preprocessed_sentences[i].preprocessed_words_,
										  preprocessed_sentences[k].preprocessed_words_, stem,
										  use_synonyms_and_antonyms, thesaurus, use_word_frequencies,
										  use_trivial_sentence_linking, word_frequencies));
			if (similarity_score > 0.0) {
				if (use_page_rank)
					page_rank_solver.addLink(preprocessed_sentences[i].original_sentence_and_words_,
								 preprocessed_sentences[k].original_sentence_and_words_, /* reciprocate_link = */ true,
								 similarity_score);
				else { // Use PHITS.
					graph.setValue(i, k, similarity_score);
					graph.setValue(k, i, similarity_score);
				}
			}
		}
	}

	// Now perform the graph analysis using PageRank or PHITS:
	if (use_page_rank) {
		std::vector<PageRankSolver<const SentenceAndWords *>::RankedScore> ranked_scores;
		page_rank_solver.getPageRanks(&ranked_scores);
		for (std::vector<PageRankSolver<const SentenceAndWords *>::RankedScore>::const_iterator ranked_score(ranked_scores.begin());
		     ranked_score != ranked_scores.end(); ++ranked_score)
			ranked_sentences->push_back(ranked_score->first);
	}
	else { // Use PHITS.
		Logger logger("/dev/null", Logger::VL_ERRORS_ONLY, Logger::DO_NOT_CLEAR);
		PhitsAnalyzer phits_analyzer(graph, 0.15, 0.15, &logger);
		const unsigned max_no_of_iterations(1000);
		const double max_relative_error(1e-8);
		unsigned actual_no_of_iterations;
		double actual_max_relative_error;
		if (not phits_analyzer.iterate(max_no_of_iterations, max_relative_error, &actual_no_of_iterations, &actual_max_relative_error))
			throw Exception("in GraphBasedTextSummarizer: PHITS analysis failed!");
		const PhitsAnalyzer::ResultSortOrder result_sort_order(PhitsAnalyzer::AUTHORITY_DESCENDING);
		std::vector<PhitsAnalyzer::Result> sorted_results;
		phits_analyzer.getBestNodes(result_sort_order, max_sentence_count, &sorted_results);

		// Map sentence indices back to sentences: while ignoring duplicate sentences:
		for (std::vector<PhitsAnalyzer::Result>::const_iterator result(sorted_results.begin()); result != sorted_results.end(); ++result)
			ranked_sentences->push_back(preprocessed_sentences[result->index_].original_sentence_and_words_);
	}
}


namespace {


inline bool SentenceAndWordsPtrCompare(const SentenceAndWords * const lhs, const SentenceAndWords * const rhs)
{
	return *lhs < *rhs;
}


} // unnamed namespace


void SummarizeText(const std::string &document, const unsigned min_no_of_words, std::string * const text_summary, const unsigned max_sentence_count,
		   unsigned text_summarizer_options, const double max_sentence_overlap)
{
	text_summary->clear();

	std::vector<SentenceAndWords> sentences_and_words;
	HtmlSentenceParser html_sentence_parser(document, &sentences_and_words);
	html_sentence_parser.parse();

	std::vector<const SentenceAndWords *> ranked_sentences;
	GraphBasedTextSummarizer(sentences_and_words, &ranked_sentences, text_summarizer_options, /* min_sentence_length = */ 3, max_sentence_count);

	if (max_sentence_overlap >= 0 and max_sentence_overlap < 1.0) {
		const bool use_stemming(text_summarizer_options & USE_STEMMING);
		GenerateLowRedundancyText(ranked_sentences, min_no_of_words, max_sentence_overlap, text_summary, use_stemming);
	}
	else {
		std::vector<const SentenceAndWords *> selected_sentences;
		unsigned extracted_word_count(0);
		for (std::vector<const SentenceAndWords *>::const_iterator ranked_sentence(ranked_sentences.begin());
		     ranked_sentence != ranked_sentences.end() and extracted_word_count < min_no_of_words; ++ranked_sentence)
		{
			selected_sentences.push_back(*ranked_sentence);
			extracted_word_count += (*ranked_sentence)->getWordCount();
		}

		std::sort(selected_sentences.begin(), selected_sentences.end(), SentenceAndWordsPtrCompare);
		for (std::vector<const SentenceAndWords *>::const_iterator selected_sentence(selected_sentences.begin());
		     selected_sentence != selected_sentences.end(); ++selected_sentence)
		{
			if (selected_sentence != selected_sentences.begin())
				*text_summary += ' ';
			*text_summary += (*selected_sentence)->getSentence();
		}
	}
}


namespace {


// IsNoiseChar -- Returns true if "ch" is considered to be a noise character.  Helper function for ExtractWords().
//
inline bool IsNoiseChar(const char ch)
{
	static const char non_noise_chars[] = "-\'";
	return not isalnum(ch) and std::strchr(non_noise_chars, ch) == NULL;
}


// CanonizeText -- helper function for ExtractWords().  Converts many special characters to blanks, replaces multiple
//                 space sequences into a single space, lowercases letters and trims leading and trailing whitespace.
//
void CanonizeText(const bool force_lowercase, std::string * const text)
{
	std::string::const_iterator leading(text->begin());
	std::string::iterator current(text->begin());

	// Skip over leading whitespace and certain nonalphanumeric characters:
	while (StringUtil::IsSpace(*leading) or IsNoiseChar(*leading))
		++leading;

	// Collapse multiple whitespace + certain nonalphanumeric characters into a single space and lowercase letters:
	bool skipping(false);
	for (/* Empty. */; leading != text->end(); ++leading) {
		if (isupper(*leading)) {
			skipping = false;
			if (force_lowercase)
				*current++ = tolower(*leading);
			else
				*current++ = *leading;
		}
		else if (StringUtil::IsSpace(*leading) or IsNoiseChar(*leading)) {
			if (skipping)
				continue;
			*current++ = ' ';
			skipping = true;
		}
		else {
			skipping = false;
			*current++ = *leading;
		}
	}

	// Remove trailing spaces and dashes:
	while (current != text->begin() and (*(current - 1) == ' ' or *(current - 1) == '-'))
		--current;

	text->resize(current - text->begin());
}


// NormalizeText -- Peform many normalizations that previously required multiple functions.
//
std::string NormalizeText(std::string * const text)
{
	static const std::string special_keep_chars("()\'\"-?.,;:");
	std::string::iterator destination(text->begin());

	for (std::string::iterator source(text->begin()); source != text->end(); ++source, ++destination) {
		if (not isalnum(*source) and special_keep_chars.find(*source) == std::string::npos) {
			*destination = ' ';
			// Skip over multiple whitespace
			for (std::string::iterator whitespace(source + 1); whitespace != text->end() and *whitespace == ' ';  ++whitespace)
				++source;
		}
		else
			*destination = tolower(*source);
	}

	return *text;
}


} // unnamed namespace


void ExtractWords(const std::string &sentence, std::vector<std::string> * const words, const bool stem, const char * const trim_chars,
		  const bool force_lowercase)
{
	words->clear();

	std::string normalised_sentence(sentence);

	CanonizeText(force_lowercase, &normalised_sentence);

	// No "words" to process?
	if (unlikely(normalised_sentence.empty()))
		return;

	// add the individual words of the subfield:
	normalised_sentence += ' '; // add a trailing word separator
	size_t start(0);
	for (size_t space_pos = normalised_sentence.find(' '); space_pos != std::string::npos; space_pos = normalised_sentence.find(' ', space_pos + 1)) {
		// Get the next word:
		std::string word = normalised_sentence.substr(start, space_pos - start);
		start = space_pos + 1;

		StringUtil::Trim(trim_chars, &word);

		// Collapse possessive forms, e.g. "houses'" -> "houses" and "fred's" -> "fred":
		if (StringUtil::EndsWith(word, "'"))
			word.resize(word.length() - 1);
		else if (StringUtil::EndsWith(word, "'s"))
			word.resize(word.length() - 2);

		if (stem)
			Stemmer::stem(&word);

		if (IsPossiblyAWord(word))
			words->push_back(word);
	}
}


unsigned CountWords(const std::string &text)
{
	std::vector<std::string> words;
	return StringUtil::SplitThenTrim(text, " \n\t\r\"\\()[]{}&-.,;:*'!+/%#", "", &words);
}


namespace {


// LoadWordsFromFile -- loads a list of words, one per line, from a file.  (Helper function for LoadDiacriticalWords().)
//
template<typename Container> void LoadWordsFromFile(const std::string &filename, Container * const words)
{
	words->clear();

	File input(SHARE_DIR + filename, "r");
	if (unlikely(input.fail()))
		throw Exception("in LoadWordsFromFile: can't open \"" SHARE_DIR + filename + "\" for reading!");
	while (not input.eof()) {
		std::string word;
		if (input.getline(&word) == 0)
			break;
		StringUtil::TrimWhite(&word);
		if (likely(not word.empty()))
			words->insert(words->end(), word);
	}
	if (unlikely(words->empty()))
		throw Exception("in LoadWordsFromFile: found no words in \"" SHARE_DIR + filename + "\"!");
}


const char *UPPERCASE_C_WITH_CEDILLA("«");
const char *LOWERCASE_C_WITH_CEDILLA("Á");
const char *UPPERCASE_UMLAUTS("ƒÀœ÷‹");
const char *LOWERCASE_UMLAUTS("‰ÎÔˆ¸ˇ");
const char *UPPERCASE_ACUTE("¡…Õ”⁄›");
const char *LOWERCASE_ACUTE("·ÈÌÛ˙˝");


GNU_HASH_SET<std::string> diacrit_starters;     // Contains words that start with a letter with a diacritical mark.
GNU_HASH_SET<std::string> diacrit_non_starters; // Contains words that contain a non-leading letter with a diacritical mark.


const char * const LOWERCASE_DIACRITICALS("Á‰ÎÔˆ¸ˇ·ÈÌÛ˙˝");
const char * const UPPERCASE_DIACRITICALS("«ƒÀœ÷‹¡…Õ”⁄›");


void LoadDiacriticalWordsHelper(const char * const filename)
{
	const std::string lowercase_diacriticals(LOWERCASE_DIACRITICALS);

	std::vector<std::string> words;
	LoadWordsFromFile(filename, &words);
	for (std::vector<std::string>::const_iterator word(words.begin()); word != words.end(); ++word) {
		if (__builtin_strchr(UPPERCASE_DIACRITICALS, (*word)[0]) != NULL)
			diacrit_starters.insert(*word);
		if (StringUtil::FindAnyOf(lowercase_diacriticals, *word) != std::string::npos)
			diacrit_non_starters.insert(*word);
	}
}


// LoadDiacriticalWords -- initializes "diacrit_starters" and "diacrit_non_staters."
//
void LoadDiacriticalWords()
{
	LoadDiacriticalWordsHelper("/names/French.female.first");
	LoadDiacriticalWordsHelper("/names/French.male.first");

	if (unlikely(diacrit_starters.empty()))
		throw Exception("in LoadDiacriticalWords(TextUtil.cc): did not find any names that start with a letter with a diacritical mark!");
	if (unlikely(diacrit_non_starters.empty()))
		throw Exception("in LoadDiacriticalWords(TextUtil.cc): did not find any names that contain a letter with a diacritical mark!");
}


// FindReplacementChar -- attempts to find a character with a diacritical mark that would fit into our "test_word" at "diacritical_mark_pos."
//                        Returns the replacement character if a matching word was found, otherwise returns a NUL.  (Helper function for
//                        PatchDiacriticals().)
//
inline char FindReplacementChar(std::string test_word, const std::string::size_type diacritical_mark_pos,
				const char * const potential_replacement_chars, const GNU_HASH_SET<std::string> &search_set)
{
	for (const char *cp(potential_replacement_chars); *cp != '\0'; ++cp) {
		test_word[diacritical_mark_pos] = *cp;
		if (search_set.find(test_word) != search_set.end())
			return *cp;
	}

	return '\0';
}


// PatchDiacriticals -- attempts to fix up pdftotext conversion where instead of letters w/ diacritical marks we only get the mark.
//
void PatchDiacritialcs(const std::string &raw_text, std::string * const patched_text)
{
	patched_text->clear();
	patched_text->reserve(raw_text.size());

	static bool initialized(false);
	if (not initialized) {
		LoadDiacriticalWords();
		initialized = true;
	}

	const std::string::size_type raw_text_length(raw_text.length());
	std::string::size_type search_start_pos(0);
	while (search_start_pos < raw_text_length) {
		// First look for the next diacritical mark:
		const std::string::size_type diacritical_mark_pos(raw_text.find_first_of("®¥∏", search_start_pos));
		if (diacritical_mark_pos == std::string::npos) {
			std::copy(raw_text.begin() + search_start_pos, raw_text.end(), std::back_inserter<std::string>(*patched_text));
			return;
		}
		else
			std::copy(raw_text.begin() + search_start_pos, raw_text.begin() + diacritical_mark_pos,
				  std::back_inserter<std::string>(*patched_text));

		// Special hack for Scandinavian "¯":
		if (raw_text[diacritical_mark_pos] == '∏' and diacritical_mark_pos > 0 and raw_text[diacritical_mark_pos - 1] == '√') {
			(*patched_text)[patched_text->length() - 1] = '¯';
			search_start_pos = diacritical_mark_pos + 1;
			continue;
		}

		// Then grab letters from either side of it:
		std::string::size_type word_candidate_start(diacritical_mark_pos);
		while (word_candidate_start > 0 and isalpha(raw_text[word_candidate_start - 1]))
			--word_candidate_start;
		std::string::size_type word_candidate_end(diacritical_mark_pos);
		while (word_candidate_end < raw_text_length and isalpha(raw_text[word_candidate_end + 1]))
			++word_candidate_end;

		const std::string test_word(raw_text.substr(word_candidate_start, word_candidate_end - word_candidate_start + 1));

		// Now determine the candidate letters with diacritical marks:
		const char *check_list(NULL); // The list of characters to try:
		if (diacritical_mark_pos == word_candidate_start) { // We'll consider only uppercase letters.
			switch (raw_text[diacritical_mark_pos]) {
			case '®':
				check_list = UPPERCASE_UMLAUTS;
				break;
			case '¥':
				check_list = UPPERCASE_ACUTE;
				break;
			case '∏':
				check_list = UPPERCASE_C_WITH_CEDILLA;
				break;
			default:
				throw Exception("in PatchDiacritialcs: unknown diacritical (" + StringUtil::ToString(raw_text[diacritical_mark_pos])
						+ ") (1)!");
			}

			const char replacement_char(FindReplacementChar(test_word, diacritical_mark_pos - word_candidate_start, check_list,
									diacrit_starters));
			if (replacement_char != '\0')
				*patched_text += replacement_char;
			else
				*patched_text += raw_text[diacritical_mark_pos];
		}
		else { // We'll consider only lowercase letters.
			switch (raw_text[diacritical_mark_pos]) {
			case '®':
				check_list = LOWERCASE_UMLAUTS;
				break;
			case '¥':
				check_list = LOWERCASE_ACUTE;
				break;
			case '∏':
				check_list = LOWERCASE_C_WITH_CEDILLA;
				break;
			default:
				throw Exception("in PatchDiacritialcs: unknown diacritical (" + StringUtil::ToString(raw_text[diacritical_mark_pos])
						+ ") (2)!");
			}

			const char replacement_char(FindReplacementChar(test_word, diacritical_mark_pos - word_candidate_start, check_list,
									diacrit_non_starters));
			if (replacement_char != '\0')
				*patched_text += replacement_char;
			else
				*patched_text += raw_text[diacritical_mark_pos];
		}

		// Copy the remainder of the word that contained the diacritical mark:
		std::copy(raw_text.begin() + diacritical_mark_pos + 1, raw_text.begin() + word_candidate_end + 1,
			  std::back_inserter<std::string>(*patched_text));

		search_start_pos = word_candidate_end + 1;
	}
}


enum PdfConversionType { CONVERT_TO_TEXT, CONVERT_TO_HTML };


std::string PdfToTextOrHtml(const std::string &pdf_data, const PdfConversionType conversion_type)
{
	// See if we have the same conversion request as the last time we were called:
	static PdfConversionType last_conversion_type;
	static std::string last_checksum, cached_result;
	const std::string new_checksum(StringUtil::Md5(pdf_data));
	if (new_checksum == last_checksum and conversion_type == last_conversion_type)
		return cached_result;
	else {
		last_checksum = new_checksum;
		last_conversion_type = conversion_type;
	}

	const std::string pdf_filename(FileUtil::UniqueFileName("/tmp", "ps_or_pdf-"));
	const std::string text_filename(FileUtil::UniqueFileName("/tmp", "text-"));
	if (unlikely(not FileUtil::WriteFile(pdf_filename, pdf_data)))
		throw Exception("in PdfToTextOrHtml(TextUtil.cc): can't write \"" + pdf_filename + "\"!");

	const pid_t pid = ::fork();
	if (pid == -1)
		throw Exception("in PdfToTextOrHtml(TextUtil.cc): fork(2) failed! (Out of memory?)");

	if (pid == 0) { // We're the child!
		std::string dirname, basename;
		FileUtil::DirnameAndBasename(PDFTOTEXT, &dirname, &basename);
		::close(STDERR_FILENO); // Suppress error output to stderr.
		if (conversion_type == CONVERT_TO_TEXT)
			::execl(PDFTOTEXT, basename.c_str(), pdf_filename.c_str(), text_filename.c_str(), reinterpret_cast<char *>(NULL));
		else
			::execl(PDFTOTEXT, basename.c_str(), "-htmlmeta", pdf_filename.c_str(), text_filename.c_str(), reinterpret_cast<char *>(NULL));
		throw Exception("in PdfToTextOrHtml(TextUtil.cc): failed to exec(3) \"" PDFTOTEXT " " + pdf_filename + " " + text_filename + "\" ("
				+ MsgUtil::ErrnoToString() + ")!");
	}

	int status;
	if (ProcessUtil::TimedWait(pid, 2000 /* ms */, &status) == 0) {
		::kill(pid, SIGKILL);
		::waitpid(pid, &status, 0);
	}
	::unlink(pdf_filename.c_str());
	if (unlikely(status != 0)) {
		::unlink(text_filename.c_str());
		return "";
	}

	std::string converted_text;
	if (unlikely(not FileUtil::ReadFile(text_filename, &converted_text)))
		throw Exception("in PdfToTextOrHtml(TextUtil.cc): failed to read converted text from \"" + text_filename + "\"!");
	::unlink(text_filename.c_str());

	// We're done if we want HTML:
	if (conversion_type == CONVERT_TO_HTML) {
		cached_result = converted_text;
		return converted_text;
	}

	// Insert blank lines after headings:
	std::vector<std::string> lines;
	StringUtil::SplitThenTrim(converted_text, "\r\n", " \t", &lines, /* suppress_empty_words = */ false);
	std::string normalised_text;
	bool last_line_was_blank(true);
	for (std::vector<std::string>::const_iterator line(lines.begin()); line != lines.end(); ++line) {
		if (line->empty())
			last_line_was_blank = true;
		else {
			normalised_text += *line;
			if (last_line_was_blank)
				normalised_text += '\n';
			last_line_was_blank = false;
		}

		normalised_text += '\n';
	}

	PatchDiacritialcs(normalised_text, &cached_result);

	// Map soft hyphens to regular hyphens/minus signs:
	const char SOFT_HYPHEN(0xAD); // Code in ISO-8859-1/ISO-8859-15.
	for (std::string::iterator ch(cached_result.begin()); ch != cached_result.end(); ++ch) {
		if (*ch == SOFT_HYPHEN)
			*ch = '-';
	}

	return cached_result;
}


} // unnamed namespace


std::string PdfToText(const std::string &pdf_data, const bool attempt_to_combine_broken_words, const bool remove_headers_and_footers)
{
	::PdfToText pdf_to_text(pdf_data, remove_headers_and_footers);
	if (pdf_to_text.failed())
		return "";
	return pdf_to_text.getPlainText(attempt_to_combine_broken_words, attempt_to_combine_broken_words);
}


std::string WordToText(const std::string &word_data)
{
	// See if we have the same conversion request as the last time we were called:
	static std::string last_checksum, cached_result;
	const std::string new_checksum(StringUtil::Md5(word_data));
	if (new_checksum == last_checksum)
		return cached_result;
	else
		last_checksum = new_checksum;

	const std::string word_filename(FileUtil::UniqueFileName("/tmp", "word-"));
	if (unlikely(not FileUtil::WriteFile(word_filename, word_data)))
		throw Exception("in TextUtil::WordToText: can't write \"" + word_filename + "\"!");

	const std::string text_filename(FileUtil::UniqueFileName("/tmp", "text-"));

	const pid_t pid = ::fork();
	if (pid == -1)
		throw Exception("in TextUtil::WordToText: fork(2) failed! (Out of memory?)");

	if (pid == 0) { // We're the child!
		std::string dirname, basename;
		FileUtil::DirnameAndBasename(WV, &dirname, &basename);
		::close(STDERR_FILENO); // Suppress error output to stderr.

		// Redirect stdout:
		if (unlikely(::close(STDOUT_FILENO) == -1))
			throw Exception("in TextUtil::WordToText: close(2) failed (" + MsgUtil::ErrnoToString() + ")!");
		const int text_fd(::open(text_filename.c_str(), O_WRONLY | O_CREAT, 0600));
		if (unlikely(text_fd == -1))
			throw Exception("in TextUtil::WordToText: open(2) failed (" + MsgUtil::ErrnoToString() + ")!");

		::execl(WV, basename.c_str(), word_filename.c_str(), text_filename.c_str(), reinterpret_cast<char *>(NULL));
		throw Exception("in TextUtil::WordToText: failed to exec(3) \"" WV " " + word_filename + " " + text_filename + "\" ("
				+ MsgUtil::ErrnoToString() + ")!");
	}

	int status;
	::waitpid(pid, &status, 0);
	::unlink(word_filename.c_str());
	if (unlikely(status != 0)) {
		::unlink(text_filename.c_str());
		return "";
	}

	std::string converted_text;
	if (unlikely(not FileUtil::ReadFile(text_filename, &converted_text)))
		throw Exception("in TextUtil::WordToText: failed to read converted text from \"" + text_filename + "\"!");
	::unlink(text_filename.c_str());

	cached_result = StringUtil::UTF8ToISO8859_15(converted_text);

	return cached_result;
}


std::string PdfToHtml(const std::string &pdf_data)
{
	return PdfToTextOrHtml(pdf_data, CONVERT_TO_HTML);
}


namespace {


struct Line {
	std::string text_;
	double llx_, lly_;
	double delta_x_, delta_y_;
	bool emit_empty_line_;
public:
	Line(): llx_(0.0), lly_(0.0) { }
	Line(const std::string &text, const double &llx, const double lly)
		: text_(text), llx_(llx), lly_(lly), emit_empty_line_(false) { }
	std::string debugToString() const
		{ return "[" + StringUtil::ToString(llx_) + ", " + StringUtil::ToString(lly_) + "] " + text_
			  + (emit_empty_line_ ? "\n\n" : "\n"); }
	std::string toString() const { return text_ + (emit_empty_line_ ? "\n\n" : "\n"); }
};


class PsToTextXmlParser {
	std::vector<Line> &lines_;
	double last_llx_, last_lly_;
	std::string::const_iterator ch_, text_end_;
	unsigned line_no_;
public:
	enum ChunkType { START_ELEMENT, CHARACTERS };

	class AttributeMap {
		std::map<std::string, std::string> map_;
	public:
		void clear() { map_.clear(); }

		std::string &operator[](const std::string &key) { return map_[key]; }

		/** \brief  Insert a value into an AttributeMap, replacing any old value.
		 *  \param  name   The name of the key.
		 *  \param  value  The value to be associated with "name".
		 *  \return True if the attribute wasn't in the map yet, else false.
		 *
		 *  The pair (name, value) is stored in the AttributeMap.  If
		 *  there is an existing value associated with name, it is
		 *  not inserted.
		 */
		bool insert(const std::string &name, const std::string &value);
	};
	struct Chunk {
		const ChunkType type_;
		const std::string text_;
		AttributeMap * const attribute_map_; // only non-NULL if type_ == OPENING_TAG
	public:
		Chunk(const ChunkType type, const std::string &text, AttributeMap * const attribute_map = NULL)
			: type_(type), text_(text), attribute_map_(attribute_map) { }
		~Chunk() { delete attribute_map_; }
	};
public:
	PsToTextXmlParser(const std::string &xml_text, std::vector<Line> * const lines)
		: lines_(*lines), ch_(xml_text.begin()), text_end_(xml_text.end()), line_no_(1) { }
	void parse();
private:
	void skipWhiteSpace();
	void skipOverJunk();
	void skipToEndOfTag();
	void extractCharacterConstant(std::string * const char_constant);
	void extractAttribute(std::string * const name, std::string * const value);
	void extractOpeningTag(std::string * const tag_name, AttributeMap * const attrib_map);
	void extractCDATA(std::string * const characters);
	void notify(const Chunk &chunk);
};


void PsToTextXmlParser::skipWhiteSpace()
{
	while (ch_ != text_end_ and (*ch_ == ' ' or *ch_ == '\t' or *ch_ == '\n' or *ch_ == '\r')) {
		if (*ch_ == '\n')
			++line_no_;
		++ch_;
	}
}


void PsToTextXmlParser::skipOverJunk()
{
	while (ch_ != text_end_ and *ch_ != '<')
		++ch_;
}


void PsToTextXmlParser::skipToEndOfTag()
{
	while (ch_ != text_end_ and *ch_ != '>') {
		if (*ch_ == '\'' or *ch_ == '"') {
			std::string char_constant;
			extractCharacterConstant(&char_constant);
		}
		else {
			if (unlikely(*ch_ == '\n'))
				++line_no_;
			++ch_;
		}
	}

	// Skip over the closing angle-bracket:
	if (likely(ch_ != text_end_))
		++ch_;
}


void PsToTextXmlParser::extractCharacterConstant(std::string * const char_constant)
{
	char_constant->clear();

	const char delimiter(*ch_); // Need to keep track of this to distinguish single- from double-quotes.
	for (++ch_; ch_ != text_end_ and *ch_ != delimiter; ++ch_)
		*char_constant += *ch_;

	// Skip closing delimiter?
	if (ch_ != text_end_)
		++ch_;
}


void PsToTextXmlParser::extractAttribute(std::string * const name, std::string * const value)
{
	name->clear();
	value->clear();

	while (ch_ != text_end_ and isalnum(*ch_))
		*name += *ch_++;

	skipWhiteSpace();

	if (ch_ == text_end_ or *ch_ != '=')
		return;
	++ch_;

	skipWhiteSpace();

	if (ch_ == text_end_ or (*ch_ != '\'' and *ch_ != '"'))
		return;

	extractCharacterConstant(value);
}


void PsToTextXmlParser::extractOpeningTag(std::string * const tag_name, AttributeMap * const attrib_map)
{
	tag_name->clear();
	attrib_map->clear();

	*tag_name += *ch_;

	// Extract tag name:
	for (++ch_; ch_ != text_end_ and *ch_ != '>' and isalnum(*ch_); ++ch_)
		*tag_name += *ch_;

	// Extract attributes, if any:
	while (ch_ != text_end_ and *ch_ != '>') {
		skipWhiteSpace();

		if (ch_ == text_end_ or *ch_ == '>')
			break;

		if (isalpha(*ch_)) {
			std::string attrib_name, attrib_value;
			extractAttribute(&attrib_name, &attrib_value);
			(*attrib_map)[attrib_name] = attrib_value;
		}
		else
			skipToEndOfTag();
	}

	// Skip over closing angle bracket?
	if (ch_ != text_end_ and *ch_ == '>')
		++ch_;
}


void PsToTextXmlParser::parse()
{
	while (ch_ != text_end_) {
		if (*ch_ == '<') {
			++ch_;
			if (unlikely(ch_ == text_end_))
				return; // Malformed XML document!

			if (*ch_ == '/') { // Not interested in closing tags.
				skipToEndOfTag();
				continue;
			}
			else if (*ch_ == '!') { // Comment or CDATA
				std::string characters;
				extractCDATA(&characters);
				if (not characters.empty()) {
					const Chunk cdata_chunk(CHARACTERS, characters);
					notify(cdata_chunk);
				}
			}
			else if (isalpha(*ch_)) {
				std::string tag_name;
				AttributeMap *attrib_map = new AttributeMap;
				extractOpeningTag(&tag_name, attrib_map);
				if (likely(not tag_name.empty())) {
					const Chunk opening_tag_chunk(START_ELEMENT, tag_name, attrib_map);
					notify(opening_tag_chunk);
				}
			}
			else
				skipToEndOfTag();
		}
		else
			skipWhiteSpace();

		if (ch_ != text_end_ and *ch_ != '<')
			skipOverJunk();
	}
}


void PsToTextXmlParser::extractCDATA(std::string * const characters)
{
	characters->clear();

	++ch_; // To skip over the exclamation point.

	if (unlikely(ch_ == text_end_))
		return;

	if (*ch_ == '-') { // probably a comment
		do {
			unsigned dash_count(0);
			while (ch_ != text_end_ and dash_count < 2) {
				if (*ch_ == '-')
					++dash_count;
				else
					dash_count = 0;

				++ch_;
			}

			if (unlikely(ch_ == text_end_))
				return;
		} while (*ch_ != '>');
	}
	else { // Hopefully "[CDATA[[".
		std::string leader;
		for (unsigned char_count(0); ch_ != text_end_ and * ch_ != '>' and char_count < 7; ++char_count, ++ch_) {
			if (unlikely (*ch_ == '\n')) {
				skipToEndOfTag();
				return;
			}

			leader += *ch_;
		}

		if (unlikely(leader != "[CDATA[")) {
			skipToEndOfTag();
			return;
		}

		// Extract until we find "]]>":
		std::string cdata;
		for (unsigned bracket_count(0); ch_ != text_end_; ++ch_) {
			cdata += *ch_;
			if (bracket_count >= 2 and (likely(*ch_ == '>'))) {
				*characters = cdata.substr(0, cdata.length() - 3);
				++ch_;
				return;
			}
			else if (*ch_ == ']')
				++bracket_count;
			else
				bracket_count = 0;
		}
	}
}


void PsToTextXmlParser::notify(const PsToTextXmlParser::Chunk &chunk)
{
	if (chunk.type_ == START_ELEMENT and chunk.text_ == "tbox") {
		last_llx_ = StringUtil::ToDouble((*(chunk.attribute_map_))["llx"]);
		last_lly_ = StringUtil::ToDouble((*(chunk.attribute_map_))["lly"]);
	}

	if (chunk.type_ == CHARACTERS) {
		std::string trimmed_text(chunk.text_);
		StringUtil::RightTrim(" \t\n\r", &trimmed_text);
		if (not trimmed_text.empty())
			lines_.push_back(Line(trimmed_text, last_llx_, last_lly_));
	}
}


} // unnamed namespace


std::string PostScriptToText(const std::string &postscript)
{
	// 0. Handle empty documents first:
	if (postscript.empty())
		return "";

	// 1. Check to see if we already converted this document:
	static std::string converted_document;
	static std::string checksum;
	const std::string new_checksum(StringUtil::Md5(postscript));
	if (checksum == new_checksum)
		return converted_document;
	checksum = new_checksum;

	// 2. Write the PostScript of PDF text that we would like to convert to a temporary file in /tmp:
	const std::string output_filename(FileUtil::UniqueFileName("/tmp", "ps_or_pdf-"));
	if (unlikely(not FileUtil::WriteFile(output_filename, postscript)))
		throw Exception("in TextUtil::PostScriptToText: can't write \"" + output_filename + "\"!");

	// 3. Convert the temporary file to XML with pstotext:
	std::vector<std::string> args;
	args.push_back(BIN_DIR "/pstotext");
	args.push_back(output_filename);
	pid_t child_pid;
	const int master_fd(ProcessUtil::PtyExec(args, &child_pid));
	if (unlikely(master_fd == -1))
		throw Exception("in TextUtil::PostScriptToText: ProcessUtil::PtyExec() failed!");
	fd_set read_fds;
	FD_ZERO(&read_fds);
	FD_SET(master_fd, &read_fds);
	if (::select(master_fd + 1, &read_fds, NULL, NULL, NULL) < 0)
		throw Exception("in TextUtil::PostScriptToText: select(2) failed ("
				+ MsgUtil::ErrnoToString() + ")!");
	char read_buffer[BUFSIZ];
	ssize_t no_of_bytes_read;
	std::string xml_data;
	while ((no_of_bytes_read = ::read(master_fd, read_buffer, sizeof(read_buffer))) > 0)
		xml_data.append(read_buffer, no_of_bytes_read);
	::close(master_fd);
	MSG_UTIL_ASSERT(::waitpid(child_pid, NULL, 0) == child_pid);
	::unlink(output_filename.c_str());

	// 4. Extract plain text and text position information from the XML output of pstotext:
	std::vector<Line> lines;
	PsToTextXmlParser ps_to_text_xml_parser(xml_data, &lines);
	ps_to_text_xml_parser.parse();

	// 5. Attempt to insert additional blank lines where it may make sense:
	double previous_llx(0.0), previous_lly(0.0);
	for (std::vector<Line>::iterator line(lines.begin()); line != lines.end(); ++line) {
		line->delta_x_ = line->llx_ - previous_llx;
		line->delta_y_ = line->lly_ - previous_lly;
		previous_llx = line->llx_;
		previous_lly = line->lly_;
	}
	for (std::vector<Line>::iterator line(lines.begin()); line != lines.end(); ++line) {
		if (line != lines.begin()) {
			// Emit an empty line after the previous line if the current line spacing is at least
			// 20% larger than the last interline spacing: A LARGE gap indicating a change in
			// columns or pages might NOT mean a new paragraph but we are assuming it does as
			// the danger of accidentally assigning such a paragraph as aboutness data is minimal.

			if (std::fabs((line - 1)->delta_y_) > 0 and
			    std::fabs(line->delta_y_) > 1.2 * std::fabs((line - 1)->delta_y_))
				(line - 1)->emit_empty_line_ = true;
		}
	}

	// 6. Convert our Line data structures into plain text.
	std::string text;
	for (std::vector<Line>::const_iterator line(lines.begin()); line != lines.end(); ++line)
		text += line->toString();

	converted_document = Dehyphenate(text, /* suppress_empty_lines */ false);
	return converted_document;
}


std::string PdfOrPostScriptToText(const std::string &pdf_or_postscript, const bool attempt_to_combine_broken_words_in_pdfs,
				  const bool attempt_to_remove_headers_and_footers_from_pdfs)
{
	if (MediaTypeUtil::GetMediaType(pdf_or_postscript) == "application/pdf")
		return PdfToText(pdf_or_postscript, attempt_to_combine_broken_words_in_pdfs, attempt_to_remove_headers_and_footers_from_pdfs);
	else
		return PostScriptToText(pdf_or_postscript);
}


namespace {


// ExtractLastWordFragment -- splits a line into a trailing word fragment with a trailing hyphen and the rest of the
//                            line if possible.  Assumes that the "line" passed in is known to end in a hyphen.
//                            Returns true if a "fragment" has been successfully split off, else false.
//
bool ExtractLastWordFragment(const std::string &line, std::string * const fragment, std::string * const remainder)
{
	fragment->clear();
	remainder->clear();

	if (line.length() < 2) {
		*remainder = line;
		return false;
	}

	const std::string::size_type last_separator_pos(StringUtil::RFindAnyOf(line, " .!?,;"));
	std::string fragment_candidate;
	if (last_separator_pos == std::string::npos)
		fragment_candidate = line.substr(0, line.length() - 2);
	else
		fragment_candidate = line.substr(last_separator_pos + 1, line.length() - 2);

	fragment_candidate = fragment_candidate.substr(0, fragment_candidate.length() - 1);
	if (not StringUtil::IsAlphabetic(fragment_candidate)) {
		*remainder = line;
		return false;
	}

	*fragment  = fragment_candidate;
	*remainder = line.substr(0, last_separator_pos + 1);

	return true;
}


// GetLeadingWord -- attempts to split off a leading word from a "line".  Assumes that "line" does not start with a
//                   space!
//
void GetLeadingWord(const std::string &line, std::string * const leading_word, std::string * const remainder)
{
	const std::string::size_type first_separator_pos(StringUtil::FindAnyOf(line, " .!?,;"));
	if (first_separator_pos == std::string::npos) {
		*leading_word = line;
		remainder->clear();
		return;
	}

	std::string leading_word_candidate(line.substr(0, first_separator_pos));
	if (StringUtil::IsAlphabetic(leading_word_candidate)) {
		*leading_word = leading_word_candidate;
		*remainder = line.substr(first_separator_pos);
	}
	else {
		leading_word->clear();
		*remainder = line;
	}
}


} // unnamed namespace


std::string Dehyphenate(const std::string &hyphentated_text, const bool suppress_empty_lines)
{
	std::vector<std::string> lines;
	StringUtil::SplitThenTrim(hyphentated_text, "\r\n", " \t", &lines, suppress_empty_lines);

	std::string candidate_fragment;
	std::string dehyphenated_text;
	static const Speller &speller(Speller::GetDefaultSpeller());
	for (std::vector<std::string>::iterator line(lines.begin()); line != lines.end(); ++line) {
		StringUtil::SanitizeText(&*line);
		StringUtil::Trim(&*line);
		if (line->empty()) {
			if (not candidate_fragment.empty()) {
				dehyphenated_text += candidate_fragment;
				candidate_fragment.clear();
			}
			dehyphenated_text += '\n';
			continue;
		}

		if (not candidate_fragment.empty()) {
			std::string leading_word, remainder;
			GetLeadingWord(*line, &leading_word, &remainder);
			if (leading_word.empty()) {
				dehyphenated_text += candidate_fragment;
				dehyphenated_text += '-';
			}
			else {
				const std::string word_candidate(candidate_fragment + leading_word);
				if (speller.isSpelledCorrectly(word_candidate))
					dehyphenated_text += word_candidate;
				else
	 				dehyphenated_text += candidate_fragment + "-" + leading_word;
				*line = remainder;
			}
			candidate_fragment.clear();
		}

		const bool current_line_ends_in_hyphen(not line->empty() and (*line)[line->length() - 1] == '-');
		if (not current_line_ends_in_hyphen)
			dehyphenated_text += *line + '\n';
		else {
			std::string remainder;
			ExtractLastWordFragment(*line, &candidate_fragment, &remainder);
			dehyphenated_text += remainder + '\n';
		}
	}
	if (not candidate_fragment.empty())
		dehyphenated_text += candidate_fragment + '\n';

	return dehyphenated_text;
}


bool ContainsAtLeast50PercentAsciiLetters(const std::string &text)
{
	unsigned ascii_letter_count(0);
	for (std::string::const_iterator ch(text.begin()); ch!= text.end(); ++ch) {
		if (isalpha(*ch) and isascii(*ch))
			++ascii_letter_count;
	}

	return text.size() > 0 and ascii_letter_count >= (text.size() / 2);
}


namespace {


inline bool ContainsAtLeastOneVowel(const std::string &text)
{
	return StringUtil::FindAnyOf(text, "aioueAIOUE") != std::string::npos;
}


enum ExtractionStates { LOOKING_FOR_HEADER, LOOKING_FOR_BODY, EXTRACTING_BODY };


} // unnamed namespace


TextSections::TextSections(const std::string &plain_text, const bool omit_bad_sections)
{
	std::vector<std::string> lines;
	StringUtil::SplitThenTrim(plain_text, "\r\n", " \t", &lines, /* suppress_empty_words = */ false);

	ExtractionStates state(LOOKING_FOR_HEADER);
	std::string potential_header;
	std::string potential_body;
	unsigned section_no(1);
	for (std::vector<std::string>::const_iterator line(lines.begin()); line != lines.end(); ++section_no, ++line) {
		switch (state) {
		case LOOKING_FOR_HEADER:
			if (not line->empty() and ContainsAtLeastOneVowel(*line)) {
				potential_header += *line;
				potential_header += '\n';
				if (line + 1 != lines.end() and (line + 1)->empty()) {
					// Is this not a header, but possibly a headerless section?
					if (potential_header.size() > 200) {
						if (omit_bad_sections) {
							if (ContainsAtLeast50PercentAsciiLetters(potential_header))
								add("", potential_header, section_no);
						}
						else
							add("", potential_header, section_no);
						potential_header.clear();
					}
					else
						state = LOOKING_FOR_BODY;
				}
			}
			continue;
		case LOOKING_FOR_BODY:
			if (not line->empty()) {
				if (ContainsAtLeastOneVowel(*line)) {
					potential_body += *line;
					potential_body += '\n';
					state = EXTRACTING_BODY;
				}
				else {
					// Did we find something that seems to be too large to be a header?  If yes, we
					// simply assume it's a headerless body.
					if (potential_header.length() > 50) {
						if (omit_bad_sections) {
							if (ContainsAtLeast50PercentAsciiLetters(potential_header))
								add("", potential_header, section_no);
						}
						else
							add("", potential_header, section_no);
					}
					potential_header.clear();
					state = LOOKING_FOR_HEADER;
				}
			}
			continue;
		case EXTRACTING_BODY:
			if (line->empty()) {
				if (potential_body.size() > 100 and ContainsAtLeastOneVowel(potential_body)) {
					if (omit_bad_sections) {
						if (ContainsAtLeast50PercentAsciiLetters(potential_body))
							add(potential_header, potential_body, section_no);
					}
					else
						add(potential_header, potential_body, section_no);
					potential_header.clear();
					potential_body.clear();
					state = LOOKING_FOR_HEADER;
				}
				else if (potential_header.length() > potential_body.length()) {
					if (omit_bad_sections) {
						if (ContainsAtLeast50PercentAsciiLetters(potential_header))
							add("", potential_header, section_no);
					}
					else
						add("", potential_header, section_no);
					potential_header = potential_body;
					potential_body.clear();
					state = LOOKING_FOR_BODY;
				}
				else {
					potential_header = potential_body;
					potential_body.clear();
					state = LOOKING_FOR_BODY;
				}
			}
			else {
				potential_body += *line;
				potential_body += '\n';
			}
			continue;
		}
	}

	if (not potential_body.empty()) {
		if (omit_bad_sections) {
			if (ContainsAtLeast50PercentAsciiLetters(potential_body))
				add(potential_header, potential_body, section_no);
		}
		else
			add(potential_header, potential_body, section_no);
	}
}


void TextSections::add(const std::string &header, const std::string &contents, const unsigned section_no)
{
	if (contents.size() < minimum_valid_section_length_)
		return;

	text_sections_.push_back(TextSection(header, contents, section_no));
}


namespace {


const char *common_header_prefixes[] = {
	"1.",
	"2.",
	"3.",
	"4.",
	"5.",
	"6.",
	"7.",
	"8.",
	"9.",
	"10.",
	"11.",
	"12.",
	"13.",
	"14.",
	"15.",
	"16.",
	"17.",
	"18.",
	"19.",
	"20.",
	"i.",
	"ii.",
	"iii.",
	"iv.",
	"v.",
	"vi.",
	"vii.",
	"viii.",
	"ix.",
	"x.",
	"xi.",
	"xii.",
	"xiii.",
	"xiv.",
	"xv.",
	"xvi.",
	"xvii.",
	"xviii.",
	"xix.",
	"xx.",
	"1)",
	"2)",
	"3)",
	"4)",
	"5)",
	"6)",
	"7)",
	"8)",
	"9)",
	"10)",
	"11)",
	"12)",
	"13)",
	"14)",
	"15)",
	"16)",
	"17)",
	"18)",
	"19)",
	"20)",
	"i)",
	"ii)",
	"iii)",
	"iv)",
	"v)",
	"vi)",
	"vii)",
	"viii)",
	"ix)",
	"x)",
	"xi)",
	"xii)",
	"xiii)",
	"xiv)",
	"xv)",
	"xvi)",
	"xvii)",
	"xviii)",
	"xix)",
	"xx)",
	"a.",
	"b.",
	"c.",
	"d.",
	"e.",
	"f.",
	"g.",
	"h.",
	"j.",
	"k.",
	"l.",
	"m.",
	"n.",
	"o.",
	"p.",
	"q.",
	"r.",
	"s.",
	"t.",
	"a)",
	"b)",
	"c)",
	"d)",
	"e)",
	"f)",
	"g)",
	"h)",
	"j)",
	"k)",
	"l)",
	"m)",
	"n)",
	"o)",
	"p)",
	"q)",
	"r)",
	"s)",
	"t)",
};


// SkipCommonHeaderPrefix -- a helper function for TextSections::getNamedSection() that attempts to skip over the numbers in numbered section headers.
//
const char *SkipCommonHeaderPrefix(const char *header_start, const bool accept_prefixless_headers = true)
{
	const char *actual_start(header_start);
	for (unsigned prefix_no(0); prefix_no < DIM(common_header_prefixes); ++prefix_no) {
		if (::strncasecmp(common_header_prefixes[prefix_no], header_start, std::strlen(common_header_prefixes[prefix_no])) == 0) {
			actual_start += std::strlen(common_header_prefixes[prefix_no]);
			break;
		}
	}

	// If we didn't find a prefix and we won't accept such headers => fail!
	if (not accept_prefixless_headers and actual_start == header_start)
		return false;

	// Skip over (some) whitespace:
	while (isspace(*actual_start))
		++actual_start;

	return actual_start;
}


} // unnamed namespace


bool TextSections::getNamedSection(const std::string &name, std::string * const contents, unsigned * const section_no) const
{
	contents->clear();

	// 1) Try headers first.
	for (const_iterator section(text_sections_.begin()); section != text_sections_.end(); ++section) {
		const std::string &header(section->getHeader());

		const char * const relevant_header_start(SkipCommonHeaderPrefix(header.c_str()));
		const size_t relevant_header_length(std::strlen(relevant_header_start));

		// Do we have a header match?
		if (relevant_header_length >= name.length() and relevant_header_length <= name.length() + 4
		    and ::strcasestr(relevant_header_start, name.c_str()) != NULL)
		{
			*contents   = section->getContents();
			*section_no = section->getSectionNumber();
			return true;
		}
	}

	// 2) Try section bodies.
	for (const_iterator section(text_sections_.begin()); section != text_sections_.end(); ++section) {
		const std::string &section_contents(section->getContents());
		const char * const relevant_section_start(SkipCommonHeaderPrefix(section_contents.c_str()));

		// Do we have a match at the start of the contents?
		if (::strncasecmp(relevant_section_start, name.c_str(), name.length()) == 0 and not isalpha(relevant_section_start[name.length()])) {
			const char *cp(relevant_section_start + name.length());
			while (*cp != '\0' and not isalpha(*cp))
				++cp;
			*contents   = cp;
			*section_no = section->getSectionNumber();
			return true;
		}
	}

	// 3) Now scour the start of lines in the section bodies:
	for (const_iterator section(text_sections_.begin()); section != text_sections_.end(); ++section) {
		LineIterator line_iterator(section->getContents());

		const char *line_start;
		while ((line_start = line_iterator.getNextLine()) != NULL) {
			const char * const relevant_section_start(SkipCommonHeaderPrefix(line_start, /* accept_prefixless_headers = */ false));
			if (relevant_section_start == NULL)
				continue;

			if (::strncasecmp(relevant_section_start, name.c_str(), name.length()) == 0 and isupper(*relevant_section_start)
			    and not isalpha(relevant_section_start[name.length()]))
			{
				const char *cp(relevant_section_start + name.length());
				while (*cp != '\0' and not isalpha(*cp))
					++cp;
				*contents   = cp;
				*section_no = section->getSectionNumber();
				return true;
			}
		}
	}

	return false;
}


const std::string &TextSections::getFirstLargeSection(unsigned * const section_no, const unsigned min_no_of_characters_in_section) const
{
	for (std::vector<TextSection>::const_iterator section(text_sections_.begin()); section != text_sections_.end(); ++section) {
		if (section->getContents().size() >= min_no_of_characters_in_section) {
			*section_no = section->getSectionNumber();
			return section->getContents();
		}
	}

	*section_no = 0;
	return StringUtil::EmptyString;
}


std::string AboutnessTermRelevanceToString(const AboutnessTermRelevance aboutness_term_relevance)
{
	switch (aboutness_term_relevance) {
	case NO_RELEVANCE:
		return "NO_RELEVANCE";
	case VERY_LOW_SCORE:
		return "VERY_LOW_SCORE";
	case LOW_SCORE:
		return "LOW_SCORE";
	case MEDIUM_SCORE:
		return "MEDIUM_SCORE";
	case HIGH_SCORE:
		return "HIGH_SCORE";
	case VERY_HIGH_SCORE:
		return "VERY_HIGH_SCORE";
	default:
		throw Exception("in TextUtil::AboutnessTermRelevanceToString: unknown aboutness term relevance "
				+ StringUtil::ToString(aboutness_term_relevance) + "!");
	}
}


namespace {


struct AboutnessTerm {
	const char *term_;
	const AboutnessTermRelevance relevance_;
} aboutness_terms[] = {
	{ "about",                      VERY_HIGH_SCORE },
	{ "abstract",                   HIGH_SCORE      },
	{ "analysis",                   LOW_SCORE       },
	{ "conclusion",                 HIGH_SCORE      },
	{ "conclusions",                HIGH_SCORE      },
	{ "conspectus",                 LOW_SCORE       },
	{ "contents",                   HIGH_SCORE      },
	{ "description",                HIGH_SCORE      },
	{ "epilog",                     LOW_SCORE       },
	{ "epilogue",                   LOW_SCORE       },
	{ "faq",                        MEDIUM_SCORE    },
	{ "foreword",                   LOW_SCORE       },
	{ "frequently asked questions", MEDIUM_SCORE    },
	{ "help",                       VERY_LOW_SCORE  },
	{ "index",                      HIGH_SCORE      },
	{ "info",                       MEDIUM_SCORE    },
	{ "information",                MEDIUM_SCORE    },
	{ "intro",                      HIGH_SCORE      },
	{ "introduction",               HIGH_SCORE      },
	{ "introductory",               LOW_SCORE       },
	{ "mission",                    MEDIUM_SCORE    },
	{ "outline",                    HIGH_SCORE      },
	{ "overview",                   VERY_HIGH_SCORE },
	{ "preamble",                   LOW_SCORE       },
	{ "precis",                     VERY_LOW_SCORE  },
	{ "preface",                    MEDIUM_SCORE    },
	{ "prelude",                    LOW_SCORE       },
	{ "prolog",                     LOW_SCORE       },
	{ "prologue",                   LOW_SCORE       },
	{ "results",                    HIGH_SCORE      },
	{ "site map",                   MEDIUM_SCORE    },
	{ "subjects",                   MEDIUM_SCORE    },
	{ "summarisation",              HIGH_SCORE      },
	{ "summarise",                  HIGH_SCORE      },
	{ "summarization",              HIGH_SCORE      },
	{ "summarize",                  HIGH_SCORE      },
	{ "summary",                    HIGH_SCORE      },
	{ "summation",                  HIGH_SCORE      },
	{ "syllabus",                   MEDIUM_SCORE    },
	{ "services",                   VERY_LOW_SCORE  },
	{ "synopsis",                   MEDIUM_SCORE    },
	{ "table of contents",          MEDIUM_SCORE    },
	{ "theme",                      MEDIUM_SCORE    },
	{ "themes",                     MEDIUM_SCORE    },
	{ "toc",                        HIGH_SCORE      },
	{ "topics",                     MEDIUM_SCORE    },
	{ "welcome",                    LOW_SCORE       },
};


std::vector<AboutnessTermAndRelevance> *default_aboutness_terms_and_relevance;


class AboutnessTermCmp: public std::unary_function<const AboutnessTermAndRelevance, bool> {
	std::string test_string_;
public:
	explicit AboutnessTermCmp(const std::string &test_string): test_string_(test_string) { }
	bool operator()(const AboutnessTermAndRelevance &rhs) const
		{ return ::strcasecmp(rhs.term_.c_str(), test_string_.c_str()) == 0; }
};


} // unnamed namespace


const std::vector<AboutnessTermAndRelevance> *GetDefaultAboutnessTermsAndRelevance()
{
	if (default_aboutness_terms_and_relevance == NULL) {
		default_aboutness_terms_and_relevance = new std::vector<AboutnessTermAndRelevance>;
		default_aboutness_terms_and_relevance->reserve(DIM(aboutness_terms));
		for (unsigned term_index = 0; term_index < DIM(aboutness_terms); ++term_index)
			default_aboutness_terms_and_relevance->push_back(AboutnessTermAndRelevance(aboutness_terms[term_index].term_,
												   aboutness_terms[term_index].relevance_));
	}

	return default_aboutness_terms_and_relevance;
}


void ExtractTextSections(const std::string &pdf_or_postscript_document, const ConversionProgram conversion_program,
			 TextSections * const text_sections, const bool attempt_to_combine_broken_words_in_pdf_docs, const bool omit_bad_sections)
{
	std::string plain_text;
	if (conversion_program == SYSTEM_PDFTOTEXT)
		plain_text = StringUtil::Map(PdfOrPostScriptToText(pdf_or_postscript_document, attempt_to_combine_broken_words_in_pdf_docs,
								   /* attempt_to_remove_headers_and_footers_from_pdfs = */ true), '\f', '\n');
	else if (conversion_program == REXA_PSTOTEXT)
		plain_text = StringUtil::Map(PostScriptToText(pdf_or_postscript_document), '\f', '\n');
	else if (conversion_program != NO_CONVERSION_DOCUMENT_IS_PLAIN_TEXT)
		throw Exception("in TextUtil::ExtractTextSections: unknown conversion program selection!");

	TextSections new_text_sections(conversion_program == NO_CONVERSION_DOCUMENT_IS_PLAIN_TEXT ? pdf_or_postscript_document : plain_text,
				       omit_bad_sections);
	text_sections->swap(new_text_sections);
}


// GetLikelyFullNameStart -- returns an iterator into "words" indicating the likely start of a person's name.  If no such position could be
//                           determined it returns words.end().
//
std::vector<std::string>::const_iterator GetLikelyFullNameStart(const std::vector<std::string> &words)
{
	static const Speller &speller(Speller::GetDefaultSpeller());
	for (std::vector<std::string>::const_iterator word(words.begin()); word != words.end(); ++word) {
		if (IsCommonAmericanFirstName(*word)) {
			// We accept this if the all lowercase version of *word is not also in our general dictionary which may also include
			// people's names, otherwise we try harder:
			if (speller.isSpelledCorrectly(StringUtil::ToLower(*word))) {
				// We don't accept a first name at the very end:
				if (word + 1 == words.end())
					break;

				// Look for a potential middle initial:
				int offset(1);
				if ((word + 1)->length() == 1 and isupper((*(word + 1))[0]))
					++offset;

				// We do require a surname:
				if (word + offset == words.end())
                                        break;

				// If the surname candidate is not in the general dictionary we assume we have a person's full name:
				if (not speller.isSpelledCorrectly(StringUtil::ToLower(*(word + offset))))
					return word;
			}
			else if (word + 1 != words.end()) // Make sure there is room for a surname.
				return word;
		}
		else if (IsCommonSurname(*word)) {
			// We use a two-pronged approach here.  If the name is not also in the general dictionary, we just accept it as a
			// surname, otherwise we do some more checking:
			const bool be_more_careful(speller.isSpelledCorrectly(StringUtil::ToLower(*word)));

			// We don't accept a surname at the beginning:
			if (word == words.begin())
				continue;

			// Look for an initial:
			const char &initial_candidate((*(word - 1))[0]);
			if ((word - 1)->length() == 1) {
				if (isupper(initial_candidate)) {
					if (be_more_careful and (initial_candidate == 'A' or initial_candidate == 'I'))
						continue;
					return word - 1;
				}
			}
			else if (not speller.isSpelledCorrectly(StringUtil::ToLower(*(word - 1)))
				 and not StringUtil::IsAllUppercase(*(word - 1)))
				return word - 1;
		}
	}

	return words.end();
}


bool ContainsNonprintableChars(const std::string &s)
{
	for (std::string::const_iterator ch(s.begin()); ch != s.end(); ++ch) {
		if (not isprint(*ch))
			return true;
	}

	return false;
}


bool Gunzip(const std::string &gzipped_data, std::string * const ungzipped_data)
{
	try {
		*ungzipped_data = GzStream::DecompressString(gzipped_data, GzStream::GUNZIP);
		return true;
	}
	catch (...) {
		ungzipped_data->clear();
		return false;
	}
}


bool IsValidEmailAddress(const std::string &possible_email_address)
{
	return PerlCompatRegExp::Match(
		"^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.(?:[A-Za-z]{2}|com|edu|org|net|gov|biz|info|name|aero|info|jobs|museum|name)$",
		possible_email_address);
}


namespace {


inline bool StartsWithAnInitial(const WordAndPhraseInfo &word_and_phrase_info)
{
	return word_and_phrase_info.word_or_phrase_.length() >= 2 and isupper(word_and_phrase_info.word_or_phrase_[0])
	       and word_and_phrase_info.word_or_phrase_[1] == '.';
}


} // unnamed namespace


void GetCapsPhrases(const std::string &text, std::vector<std::string> * const caps_phrases, const unsigned min_phrase_frequency,
		    const int get_caps_phrases_behaviour)
{
	std::vector<WordAndPhraseInfo> phrases;
	GetWordsAndCapsPhrases(text, &phrases, PHRASES_ONLY, min_phrase_frequency);

	if (get_caps_phrases_behaviour & EXCLUDE_PHRASES_STARTING_WITH_AN_INITIAL) {
		const std::vector<WordAndPhraseInfo>::iterator junk_start(std::remove_if(phrases.begin(), phrases.end(), StartsWithAnInitial));
		phrases.resize(junk_start - phrases.begin());
	}

	caps_phrases->clear();
	caps_phrases->reserve(phrases.size());
	for (std::vector<WordAndPhraseInfo>::const_iterator phrase(phrases.begin()); phrase != phrases.end(); ++phrase)
		caps_phrases->push_back(phrase->word_or_phrase_);

	if (get_caps_phrases_behaviour & EXCLUDE_SHORT_SUB_PHARSES and caps_phrases->size() > 1) {
		std::vector<std::string> sorted_phrases(*caps_phrases);
		std::sort(sorted_phrases.begin(), sorted_phrases.end());
		caps_phrases->clear();
		for (std::vector<std::string>::const_iterator phrase(sorted_phrases.begin()); phrase != sorted_phrases.end() - 1; ++phrase) {
			if (phrase->size() >= (phrase + 1)->size() or ::strncmp(phrase->c_str(), (phrase + 1)->c_str(), phrase->size()) != 0)
				caps_phrases->push_back(*phrase);
		}
		caps_phrases->push_back(sorted_phrases.back());
	}
}


namespace {


const char * const states_and_possessions[] = {
	"Afghanistan",
	"Alabama",
	"Alaska",
	"Albania",
	"Algeria",
	"American Samoa",
	"Andorra",
	"Angola",
	"Antigua and Barbuda",
	"Argentina",
	"Arizona",
	"Arkansas",
	"Armenia",
	"Australia",
	"Austria",
	"Azerbaijan",
	"Bahamas",
	"Bahrain",
	"Bangladesh",
	"Barbados",
	"Belarus",
	"Belgium",
	"Belize",
	"Benin",
	"Bhutan",
	"Bolivia",
	"Bosnia and Herzegovina",
	"Botswana",
	"Brazil",
	"Brazzaville",
	"Brunei",
	"Bulgaria",
	"Burkina Faso",
	"Burma",
	"Burundi",
	"California",
	"Cambodia",
	"Cameroon",
	"Canada",
	"Cape Verde",
	"Central African Republic",
	"Chad",
	"Chile",
	"China",
	"Colombia",
	"Colorado",
	"Comoros",
	"Connecticut",
	"Costa Rica",
	"Croatia",
	"Cuba",
	"Cyprus",
	"Czech Republic",
	"CÙte d'Ivoire",
	"Delaware",
	"Democratic Republic of the Congo",
	"Denmark",
	"District of Columbia",
	"Djibouti",
	"Dominica",
	"Dominican Republic",
	"East Timor",
	"Ecuador",
	"Egypt",
	"El Salvador",
	"Equatorial Guinea",
	"Eritrea",
	"Estonia",
	"Ethiopia",
	"Federated States of Micronesia",
	"Fiji",
	"Finland",
	"Florida",
	"France",
	"France",
	"Gabon",
	"Gambia",
	"Georgia",
	"Germany",
	"Ghana",
	"Great Britain",
	"Greece",
	"Grenada",
	"Guam",
	"Guatemala",
	"Guinea",
	"Guinea-Bissau",
	"Guyana",
	"Haiti",
	"Hawaii",
	"Honduras",
	"Hungary",
	"Iceland",
	"Idaho",
	"Illinois",
	"India",
	"Indiana",
	"Indonesia",
	"Iowa",
	"Iran",
	"Iraq",
	"Ireland",
	"Israel",
	"Italy",
	"Ivory Coast",
	"Jamaica",
	"Japan",
	"Jordan",
	"Kansas",
	"Kazakhstan",
	"Kentucky",
	"Kenya",
	"Kiribati",
	"Kuwait",
	"Kyrgyzstan",
	"Laos",
	"Latvia",
	"Lebanon",
	"Lesotho",
	"Liberia",
	"Libya",
	"Liechtenstein",
	"Lithuania",
	"Louisiana",
	"Luxembourg",
	"Macedonia",
	"Madagascar",
	"Maine",
	"Malawi",
	"Malaysia",
	"Maldives",
	"Mali",
	"Malta",
	"Marshall Islands",
	"Maryland",
	"Massachusetts",
	"Mauritania",
	"Mauritius",
	"Mexico",
	"Michigan",
	"Minnesota",
	"Mississippi",
	"Missouri",
	"Moldova",
	"Monaco",
	"Mongolia",
	"Montana",
	"Montenegro",
	"Morocco",
	"Mozambique",
	"Myanmar",
	"Namibia",
	"Nauru",
	"Nebraska",
	"Nepal",
	"Netherlands",
	"Nevada",
	"New Hampshire",
	"New Jersey",
	"New Mexico",
	"New York",
	"New Zealand",
	"Nicaragua",
	"Niger",
	"Nigeria",
	"North Carolina",
	"North Dakota",
	"North Korea",
	"Northern Mariana Islands",
	"Norway",
	"Ohio",
	"Oklahoma",
	"Oman",
	"Oregon",
	"Pakistan",
	"Palau",
	"Palau",
	"Panama",
	"Papua New Guinea",
	"Paraguay",
	"Pennsylvania",
	"Peru",
	"Philippines",
	"Poland",
	"Portugal",
	"Puerto Rico",
	"Qatar",
	"Rhode Island",
	"Romania",
	"Russia",
	"Rwanda",
	"Saint Kitts and Nevis",
	"Saint Lucia",
	"Saint Vincent and The Grenadines",
	"Samoa",
	"San Marino",
	"Sao Tome and Principe",
	"Saudi Arabia",
	"Senegal",
	"Serbia",
	"Seychelles",
	"Sierra Leone",
	"Singapore",
	"Slovakia",
	"Slovenia",
	"Solomon Islands",
	"Somalia",
	"South Africa",
	"South Carolina",
	"South Dakota",
	"South Korea",
	"Spain",
	"Sri Lanka",
	"Sudan",
	"Suriname",
	"Swaziland",
	"Sweden",
	"Switzerland",
	"Syria",
	"Taiwan",
	"Tajikistan",
	"Tanzania",
	"Tennessee",
	"Texas",
	"Thailand",
	"Togo",
	"Tonga",
	"Trinidad and Tobago",
	"Tunisia",
	"Turkey",
	"Turkmenistan",
	"Tuvalu",
	"Uganda",
	"Ukraine",
	"United Arab Emirates",
	"United Kingdom",
	"United States",
	"Uruguay",
	"Utah",
	"Uzbekistan",
	"Vanuatu",
	"Vatican City",
	"Venezuela",
	"Vermont",
	"Vietnam",
	"Virgin Islands",
	"Virginia",
	"Washington",
	"West Virginia",
	"Western Sahara",
	"Wisconsin",
	"Wyoming",
	"Yemen",
	"Zambia",
	"Zimbabwe",
};


const char * const state_or_possession_abbreviations[] = {
	"AK",
	"AL",
	"AR",
	"AS",
	"AZ",
	"CA",
	"CO",
	"CT",
	"DC",
	"DE",
	"FL",
	"FM",
	"GA",
	"GU",
	"HI",
	"IA",
	"ID",
	"IL",
	"IN",
	"KS",
	"KY",
	"LA",
	"MA",
	"MD",
	"ME",
	"MH",
	"MI",
	"MN",
	"MO",
	"MP",
	"MS",
	"MT",
	"NC",
	"ND",
	"NE",
	"NH",
	"NJ",
	"NM",
	"NV",
	"NY",
	"OH",
	"OK",
	"OR",
	"PA",
	"PR",
	"PW",
	"RI",
	"SC",
	"SD",
	"TN",
	"TX",
	"UT",
	"VA",
	"VI",
	"VT",
	"WA",
	"WI",
	"WV",
	"WY",
};


inline bool PhraseIsStateOrCountry(const char * const phrase)
{
	return std::binary_search(states_and_possessions, states_and_possessions + DIM(states_and_possessions),
				  phrase, StringUtil::strless);
}


} // unnamed namespace


// GetStateOrPossessionWordCount -- returns the number of words that are a state or possession.  Proceeds first testing
//                                  whether the 1st word by itself is a state or posession, then tries the first two
//                                  words with a space inserted inbetween etc.  If any of the tests succeed we return
//                                  the number of words that matched.  If no words match we return 0.
//
unsigned GetStateOrPossessionWordCount(const std::string &word1, const std::string &word2, const std::string &word3,
				       const std::string &word4, const std::string &word5)
{
	if (word1.size() < 2)
		return 0;
	else if (word1.size() == 2) {
		// Perform a lookup in the US state abbreviations list:
		if (std::binary_search(state_or_possession_abbreviations,
				       state_or_possession_abbreviations + DIM(state_or_possession_abbreviations),
				       word1.c_str(), StringUtil::strless))
			return 1;
		return 0;
	}
	else {
		// Check if the first word matches a US state or a country:
		if (PhraseIsStateOrCountry(word1.c_str()))
			return 1;

		if (word2.empty())
			return 0;

		char buf[word1.size() + 1 + word2.size() + 1 + word3.size() + 1 + word4.size() + 1 + word5.size() + 1];

		// Check if the first two words match a US state or a country:
		const char * const buf_start(buf);
		std::strcpy(buf, word1.c_str());
		buf[word1.size()] = ' ';
		std::strcpy(buf + word1.size() + 1, word2.c_str());
		if (PhraseIsStateOrCountry(buf_start))
			return 2;

		if (word3.empty())
			return 0;

		// Check if the first three words match a US state or a country:
		buf[word1.size() + 1 + word2.size()] = ' ';
		std::strcpy(buf + word1.size() + 1 + word2.size() + 1, word3.c_str());
		if (PhraseIsStateOrCountry(buf_start))
			return 3;

		if (word4.empty())
			return 0;

		// Check if the first four words match a US state or a country:
		buf[word1.size() + 1 + word2.size() + 1 + word3.size()] = ' ';
		std::strcpy(buf + word1.size() + 1 + word2.size() + 1 + word3.size() + 1, word4.c_str());
		if (PhraseIsStateOrCountry(buf_start))
			return 4;

		if (word5.empty())
			return 0;

		// Check if all the words match a US state or a country:
		buf[word1.size() + 1 + word2.size() + 1 + word3.size() + 1 + word4.size()] = ' ';
		std::strcpy(buf + word1.size() + 1 + word2.size() + 1 + word3.size() + 1 + word4.size() + 1, word5.c_str());
		if (PhraseIsStateOrCountry(buf_start))
			return 5;

		return 0;
	}
}


namespace {


#if 0
std::string::const_iterator ScanForStateOrCountry(const std::string::const_iterator &first, const std::string::const_iterator &last)
{
	std::string::const_iterator ch(first);

	// Skip over leading whitespace:
	while (ch != last and StringUtil::IsLatin9Whitespace(*ch))
		++ch;

}
#endif


inline bool StartsWithChar(const std::string &s, const char ch)
{
	return not s.empty() and s[0] == ch;
}


// SplitIntoWords -- helper function for GetCapsWords.  Breaks text into chunks that are hopefully frequently words.
//                   Commas followed by states, possessions and countries are returned as one "word" with a leading
//                   comma.  Possessive noun forms retain their trailing apostrophe or apostrophe followed by an s.
//                   Irish names like O'Reilly are also handled as special cases.  Furthermore empty "words" are inserted
//                   at sentence ends, commas, and various other punctuation marks as well as parentheses, braces etc.
//
void SplitIntoWords(const std::string &text, std::vector<std::string> * const words)
{
	words->clear();

	// Processing is broken down into 2 phases.  During the first phase text is broken down into words and individual
	// commas.  The second phase decides whether commas are lead-ins to country, possession or state modifiers or should
	// instead be treated as an empty word to indicate a parsing break.

	// 1) Split into words and commas:
	std::vector<std::string> raw_words;
	std::string word;
	const std::string empty_word;
	for (std::string::const_iterator ch(text.begin()); ch != text.end(); ++ch) {
		//< or > break on an html tag. Would be unlikely that a phrase would cross tags.
		const bool break_character_candidate(std::strchr("()[]{};:.!?<>", *ch) != NULL);
		if (break_character_candidate) {
			// Test for middle initial:
			if (*ch == '.' and word.length() == 1 and isupper(word[0])) {
				word += '.';
				raw_words.push_back(word);
				word.clear();
				continue; // Not a break character.
			}

			// Really a break character!
			raw_words.push_back(word);
			word.clear();
			raw_words.push_back(empty_word);
		}
		else if (*ch == ',') { // We now add the comma by itself as a separate "word".
			if (not word.empty()) {
				raw_words.push_back(word);
				word.clear();
			}
			raw_words.push_back(",");
		}
		else if (*ch == '\n') {
			if (not word.empty()) {
				raw_words.push_back(word);
				word.clear();
			}
			unsigned newline_count(1);
			while (ch + 1 != text.end() and (*(ch + 1) == '\n' or *(ch + 1) == '\r')) {
				++ch;
				if (*ch == '\n')
					++newline_count;
			}
			if (newline_count > 1)
				raw_words.push_back(empty_word);
		}
		else if (std::strchr(" \t\r\xA0\"\\&*+/%#", *ch) != NULL) { // A word separator?
			if (not word.empty()) {
				raw_words.push_back(word);
				word.clear();
			}
		}
		else if (*ch == '\'') { // Deal with names like O'Reilly:
			if (word.length() == 1 and word[0] == 'O')
				word += *ch;
			else if (word.length() > 1 and ch + 1 != text.end() and *(ch + 1) == 's')
				word += *ch;
			else {
				raw_words.push_back(word);
				word.clear();
			}
		}
		else
			word += *ch;
	}

	// 2) Deal with sequences starting with a comma.  We determine whether we keep the comma and combine it with a state,
	//    possession or country or whether we turn it into a "reset/break" token:
	for (std::vector<std::string>::const_iterator raw_word(raw_words.begin()); raw_word != raw_words.end(); ++raw_word) {
		if ((*raw_word)[0] != ',')
			words->push_back(*raw_word);
		else { // A comma.
			if (unlikely(raw_word + 1 == raw_words.end()))
				break;

			//
			// Now we collect up to 5 word candidates that are neither commas themselves and not empty.
			//

			const std::string * const word1 = &*(raw_word + 1);
			// Make sure we don't overindex our iterator:
			const std::vector<std::string>::size_type dist(raw_words.end() - raw_word);
			const std::string *word2, *word3, *word4, *word5;
			if (dist > 2 and not StartsWithChar(*(raw_word + 2), ',')) {
				word2 = &*(raw_word + 2);
				if (dist > 3 and not StartsWithChar(*(raw_word + 3), ',')) {
					word3 = &*(raw_word + 3);
					if (dist > 4 and not StartsWithChar(*(raw_word + 4), ',')) {
						word4 = &*(raw_word + 4);
						if (dist > 5 and not StartsWithChar(*(raw_word + 5), ','))
							word5 = &*(raw_word + 5);
						else
							word5 = &empty_word;
					}
					else
						word4 = word5 = &empty_word;
				}
				else
					word3 = word4 = word5 = &empty_word;
			}
			else
				word2 = word3 = word4 = word5 = &empty_word;

			// Determine how many words immediately following the comma are a state, possession or country, if any:
			const unsigned matched_word_count(GetStateOrPossessionWordCount(*word1, *word2, *word3, *word4, *word5));
			if (matched_word_count == 1) {
				words->push_back(", " + *(raw_word + 1));
				++raw_word;
			}
			else if (matched_word_count == 2) {
				words->push_back(", " + *(raw_word + 1) + " " + *(raw_word + 2));
				raw_word += 2;
			}
			else if (matched_word_count == 3) {
				words->push_back(", " + *(raw_word + 1) + " " + *(raw_word + 2) + " " + *(raw_word + 3));
				raw_word += 3;
			}
			else if (matched_word_count == 4) {
				words->push_back(", " + *(raw_word + 1) + " " + *(raw_word + 2) + " " + *(raw_word + 3)
						 + " " + *(raw_word + 4));
				raw_word += 4;
			}
			else if (matched_word_count == 5) {
				words->push_back(", " + *(raw_word + 1) + " " + *(raw_word + 2) + " " + *(raw_word + 3) + " " + *(raw_word + 4) + " "
						 + *(raw_word + 5));
				raw_word += 4;
			}
			words->push_back(empty_word);
		}
	}
}


enum WordState { LAST_WAS_HYPHEN, LAST_WAS_ACCEPTABLE_LETTER };


// IsAnInitialCapsWordCandidate -- checks whether "word" starts with a capital letter followed by lowercase letters.
//                                 Also accepts possessive forms of nouns and certain Irish names.
//
bool IsAnInitialCapsWordCandidate(const std::string &word)
{
	if (word.length() < 2)
		return false;

	std::string::const_iterator ch(word.begin());
	if (not isupper(*ch))
		return false;

	// Possible middle initial:
	if (word.length() == 2 and word[1] == '.')
		return true;

	// Handle Irish last names like "O'Reilly" etc.:
	if (word.length() > 3 and word[0] == 'O' and word[1] == '\'' and isupper(word[2]))
		ch += 2;

	// Make sure the rest of the word candidate is either all letters:
	WordState state(LAST_WAS_ACCEPTABLE_LETTER);
	for (++ch; ch != word.end(); ++ch) {
		switch (state) {
		case LAST_WAS_ACCEPTABLE_LETTER:
			if (not isalpha(*ch) and *ch != '-' and *ch != '\'')
				return false;
			if (*ch == '-')
				state = LAST_WAS_HYPHEN;
			else if (*ch == '\'') {
				if (ch + 1 == word.end() or *(ch + 1) != 's' or ch + 2 != word.end())
					return false;
				else
					return true;
			}
			break;
		case LAST_WAS_HYPHEN:
			if (not isalpha(*ch))
				return false;
			state = LAST_WAS_ACCEPTABLE_LETTER;
			break;
		}
	}

	return state != LAST_WAS_HYPHEN;
}


const char * const bad_leaders[] = {
	"after",
	"all",
	"always",
	"an",
	"and",
	"another",
	"any",
	"are",
	"as",
	"asks",
	"at",
	"be",
	"been",
	"by",
	"can",
	"does",
	"each",
	"few",
	"first",
	"for",
	"from",
	"has",
	"have",
	"hence",
	"however",
	"if",
	"in",
	"is",
	"it",
	"its",
	"large",
	"last",
	"many",
	"may",
	"not",
	"of",
	"on",
	"one",
	"only",
	"our",
	"over",
	"same",
	"see",
	"since",
	"small",
	"some",
	"soon",
	"such",
	"than",
	"that",
	"the",
	"then",
	"then",
	"there",
	"these",
	"they",
	"this",
	"those",
	"to",
	"two",
	"under",
	"unlike",
	"we",
	"well",
	"what",
	"when",
	"when",
	"when",
	"which",
	"while",
	"will",
	"with",
};


bool WordIsBadLeader(const std::string &leading_word)
{
	if (leading_word.empty())
		return true;

	std::string lowercase_word(leading_word);
	StringUtil::ToLower(&lowercase_word);
	return std::binary_search(&bad_leaders[0], &bad_leaders[0] + DIM(bad_leaders), lowercase_word.c_str(),
				  StringUtil::strless);
}


const char * const bad_trailers[] = {
	"after",
	"all",
	"always",
	"among",
	"an",
	"and",
	"another",
	"any",
	"are",
	"as",
	"asks",
	"at",
	"be",
	"been",
	"both",
	"by",
	"does",
	"each",
	"few",
	"first",
	"for",
	"from",
	"has",
	"have",
	"hence",
	"however",
	"if",
	"in",
	"is",
	"it",
	"its",
	"large",
	"last",
	"many",
	"new",
	"not",
	"of",
	"on",
	"one",
	"only",
	"our",
	"over",
	"same",
	"see",
	"since",
	"small",
	"some",
	"soon",
	"such",
	"than",
	"that",
	"the",
	"then",
	"then",
	"there",
	"these",
	"they",
	"this",
	"those",
	"to",
	"two",
	"under",
	"unlike",
	"we",
	"what",
	"when",
	"when",
	"which",
	"while",
	"with",
};


bool WordIsBadTrailer(const std::string &trailing_word)
{
	if (trailing_word.empty())
		return true;

	// Reject initials as trailers:
	if (trailing_word.length() == 2 and trailing_word[1] == '.')
		return true;

	std::string lowercase_word(trailing_word);
	StringUtil::ToLower(&lowercase_word);
	return std::binary_search(&bad_trailers[0], &bad_trailers[0] + DIM(bad_trailers), lowercase_word.c_str(),
				  StringUtil::strless);
}


const char * const middle1_words[] = { // MUST be kept in alphabetical order
	"and",
	"at",
	"by",
	"for",
	"in",
	"into",
	"of",
	"onto",
	"or",
	"over",
	"through",
	"to",
	"under",
	"with"
};


bool IsMiddleWord1(const std::string &word)
{
	return std::binary_search(&middle1_words[0], &middle1_words[0] + DIM(middle1_words), word.c_str(),
				  StringUtil::strless);
}


bool IsMiddleWord2(const std::string &word)
{
	return word == "the" or word == "a" or word == "an";
}


enum WordClass { RESET, IGNORE, CAPS_WORD, MIDDLE_WORD1, MIDDLE_WORD2, STATE_OR_POSSESSION };


std::string WordClassToString(const WordClass word_class)
{
	switch (word_class) {
	case RESET:
		return "RESET";
	case IGNORE:
		return "IGNORE";
	case CAPS_WORD:
		return "CAPS_WORD";
	case MIDDLE_WORD1:
		return "MIDDLE_WORD1";
	case MIDDLE_WORD2:
		return "MIDDLE_WORD2";
	case STATE_OR_POSSESSION:
		return "STATE_OR_POSSESSION";
	}

	throw Exception("in WordClassToString(TextUtil.cc): unhandled case!");
}


void AssignWordClasses(const std::vector<std::string> &words, std::vector<WordClass> * const word_classes)
{
	for (std::vector<std::string>::const_iterator word(words.begin()); word != words.end(); ++word) {
		if (word->empty())
			word_classes->push_back(RESET);
		else if ((*word)[0] == ',')
			word_classes->push_back(STATE_OR_POSSESSION);
		else if (IsMiddleWord1(*word))
			word_classes->push_back(MIDDLE_WORD1);
		else if (IsMiddleWord2(*word))
			word_classes->push_back(MIDDLE_WORD2);
		else if (IsAnInitialCapsWordCandidate(*word))
			word_classes->push_back(CAPS_WORD);
		else
			word_classes->push_back(IGNORE);
	}
}


enum PhraseState { ONE_LEADING_CAPS_WORD_SEEN, TWO_LEADING_CAPS_WORDS_SEEN, THREE_LEADING_CAPS_WORDS_SEEN,
		   MIDDLE_WORD1_SEEN, MIDDLE_WORD2_SEEN, ONE_TRAILING_CAPS_WORD_SEEN };


unsigned GetPhraseLength(const std::vector<std::string>::const_iterator &words_end, const std::vector<std::string>::const_iterator &current_word,
			 std::vector<WordClass>::const_iterator word_class)
{
	std::vector<std::string>::const_iterator word(current_word);
	if (*word_class != CAPS_WORD or *word_class == STATE_OR_POSSESSION or WordIsBadLeader(*word))
		return 0;

	PhraseState state(ONE_LEADING_CAPS_WORD_SEEN), last_state(ONE_LEADING_CAPS_WORD_SEEN);
	for (++word, ++word_class; word != words_end; ++word, ++word_class) {
		switch (state) {
		case ONE_LEADING_CAPS_WORD_SEEN:
			switch (*word_class) {
			case RESET:
			case IGNORE:
			case MIDDLE_WORD2:
				return 0;
			case CAPS_WORD:
				last_state = state;
				state = TWO_LEADING_CAPS_WORDS_SEEN;
				break;
			case MIDDLE_WORD1:
				last_state = state;
				state = MIDDLE_WORD1_SEEN;
				break;
			case STATE_OR_POSSESSION:
				return 2;
			}
			break;
		case TWO_LEADING_CAPS_WORDS_SEEN:
			switch (*word_class) {
			case RESET:
			case IGNORE:
			case MIDDLE_WORD2:
				return WordIsBadTrailer(*(word - 1)) ? 0 : 2;
			case CAPS_WORD:
				last_state = state;
                                state = THREE_LEADING_CAPS_WORDS_SEEN;
                                break;
			case MIDDLE_WORD1:
				last_state = state;
				state = MIDDLE_WORD1_SEEN;
				break;
			case STATE_OR_POSSESSION:
				return 3;
			}
			break;
		case THREE_LEADING_CAPS_WORDS_SEEN:
			switch (*word_class) {
			case CAPS_WORD:
				if (not WordIsBadTrailer(*word))
					return 4;
			case RESET:
			case IGNORE:
			case MIDDLE_WORD2:
				if (not WordIsBadTrailer(*(word - 1)))
					return 3;
				else
					return WordIsBadTrailer(*(word - 2)) ? 0 : 2;
			case MIDDLE_WORD1:
				last_state = state;
				state = MIDDLE_WORD1_SEEN;
				break;
			case STATE_OR_POSSESSION:
				return 4;
			}
			break;
		case MIDDLE_WORD1_SEEN:
			switch (*word_class) {
			case RESET:
			case IGNORE:
			case MIDDLE_WORD1:
			case STATE_OR_POSSESSION: {
				const std::vector<std::string>::size_type leading_caps_word_count(word - current_word - 1);
				if (leading_caps_word_count == 1)
					return 0;
				else if (leading_caps_word_count == 3) {
					if (WordIsBadTrailer(*(word - 2)))
						return WordIsBadTrailer(*(word - 3)) ? 0 : 2;
					else
						return 3;
				}
				else if (leading_caps_word_count == 2)
					return WordIsBadTrailer(*(word - 2)) ? 0 : 2;
			}
			case MIDDLE_WORD2:
				last_state = state;
				state = MIDDLE_WORD2_SEEN;
				break;
			case CAPS_WORD:
				last_state = state;
				state = ONE_TRAILING_CAPS_WORD_SEEN;
				break;
			}
			break;
		case MIDDLE_WORD2_SEEN:
			switch (*word_class) {
			case RESET:
			case IGNORE:
			case MIDDLE_WORD2:
			case MIDDLE_WORD1: {
				const std::vector<std::string>::size_type leading_caps_word_count(word - current_word - 2);
				if (leading_caps_word_count == 1)
					return 0;
				else if (leading_caps_word_count == 3) {
					if (WordIsBadTrailer(*(word - 2)))
						return WordIsBadTrailer(*(word - 3)) ? 0 : 2;
					else
						return 3;
				}
				else if (leading_caps_word_count == 2)
					return WordIsBadTrailer(*(word - 2)) ? 0 : 2;
			}
			case CAPS_WORD:
				last_state = state;
				state = ONE_TRAILING_CAPS_WORD_SEEN;
				break;
			case STATE_OR_POSSESSION:
				return word - current_word;
			}
			break;
		case ONE_TRAILING_CAPS_WORD_SEEN:
			switch (*word_class) {
			case CAPS_WORD:
				if (not WordIsBadTrailer(*word))
					return word - current_word + 1;
				/* Fall through! */
			case RESET:
			case IGNORE:
			case MIDDLE_WORD2:
			case MIDDLE_WORD1: {
				if (not WordIsBadTrailer(*(word - 1)))
					return word - current_word;
				else {
					const std::vector<std::string>::size_type
						middle_word_count(last_state == MIDDLE_WORD2_SEEN ? 2 : 1);
					const std::vector<std::string>::size_type
						leading_caps_word_count(word - current_word - middle_word_count - 1);

					if (leading_caps_word_count == 1)
						return 0;
					else if (leading_caps_word_count == 3) {
						if (WordIsBadTrailer(*(word - 2 - middle_word_count)))
							return WordIsBadTrailer(*(word - 3 - middle_word_count)) ? 0 : 2;
						else
							return 3;
					}
					else if (leading_caps_word_count == 2)
						return WordIsBadTrailer(*(word - 2 - middle_word_count)) ? 0 : 2;
				}
			}
			case STATE_OR_POSSESSION:
				return word - current_word;
			}
			break;
		default:
			throw Exception("in GetPhraseLength(TextUtil.cc): we should never get here (1)!");
		}
	}

	switch (state) {
	case ONE_LEADING_CAPS_WORD_SEEN:
		return 0;
	case TWO_LEADING_CAPS_WORDS_SEEN:
		return WordIsBadTrailer(*(word - 1)) ? 0 : 2;
	case THREE_LEADING_CAPS_WORDS_SEEN:
		if (WordIsBadTrailer(*(word - 1)))
			return WordIsBadTrailer(*(word - 2)) ? 0 : 3;
		else
			return 3;
	case MIDDLE_WORD1_SEEN: {
		const std::vector<std::string>::size_type leading_caps_word_count(word - current_word - 1);
		if (leading_caps_word_count == 1)
			return 0;
		else if (leading_caps_word_count == 3) {
			if (WordIsBadTrailer(*(word - 2)))
				return WordIsBadTrailer(*(word - 3)) ? 0 : 2;
			else
				return 3;
		}
		else if (leading_caps_word_count == 2)
			return WordIsBadTrailer(*(word - 2)) ? 0 : 2;
	}
	case MIDDLE_WORD2_SEEN: {
		const std::vector<std::string>::size_type leading_caps_word_count(word - current_word - 2);
		if (leading_caps_word_count == 1)
			return 0;
		else if (leading_caps_word_count == 3) {
			if (WordIsBadTrailer(*(word - 3)))
				return WordIsBadTrailer(*(word - 4)) ? 0 : 2;
			else
				return 3;
		}
		else if (leading_caps_word_count == 2)
			return WordIsBadTrailer(*(word - 3)) ? 0 : 2;
	}
	case ONE_TRAILING_CAPS_WORD_SEEN: {
		const std::vector<std::string>::size_type
			middle_word_count(last_state == MIDDLE_WORD2_SEEN ? 2 : 1);
		const std::vector<std::string>::size_type
			leading_caps_word_count(word - current_word - middle_word_count - 1);

		if (leading_caps_word_count == 1)
			return 0;
		else if (leading_caps_word_count == 3) {
			if (WordIsBadTrailer(*(word - 2 - middle_word_count)))
				return WordIsBadTrailer(*(word - 3 - middle_word_count)) ? 0 : 2;
			else
				return 3;
		}
		else if (leading_caps_word_count == 2)
			return WordIsBadTrailer(*(word - 2 - middle_word_count)) ? 0 : 2;
	}
	default:
		throw Exception("in GetPhraseLength(TextUtil.cc): we should never get here (2)!");
	}
}


void UpdateWordOrPhraseCounts(const std::string &word_or_phrase, GNU_HASH_MAP<std::string, unsigned> * const word_or_phrase_to_occurrence_count_map)
{
	GNU_HASH_MAP<std::string, unsigned>::iterator word_or_phrase_and_count(word_or_phrase_to_occurrence_count_map->find(word_or_phrase));
	if (word_or_phrase_and_count == word_or_phrase_to_occurrence_count_map->end())
		word_or_phrase_to_occurrence_count_map->insert(std::make_pair<std::string, unsigned>(word_or_phrase, 1));
	else // Increment occurrence count.
		++word_or_phrase_and_count->second;
}


inline bool HasHigherOccurrenceCount(const WordAndPhraseInfo &word_and_phrase_info1,
				     const WordAndPhraseInfo &word_and_phrase_info2)
{
	return word_and_phrase_info1.occurrence_count_ > word_and_phrase_info2.occurrence_count_;
}


} // unnamed namespace


bool IsUsPostalAbbreviation(const std::string &possible_abbrev)
{
	if (possible_abbrev.length() != 2)
		return false;

	// Perform a lookup in the US state abbreviations list:
	return std::binary_search(state_or_possession_abbreviations, state_or_possession_abbreviations + DIM(state_or_possession_abbreviations),
				  possible_abbrev.c_str(), StringUtil::strless);
}


void GetWordsAndCapsPhrases(const std::string &text, std::vector<WordAndPhraseInfo> * const words_and_phrases,
			    const WordAndPhraseExtractionOptions options, const unsigned min_phrase_or_word_frequency)
{
	words_and_phrases->clear();

	std::vector<std::string> words;
	SplitIntoWords(text, &words);

	GNU_HASH_MAP<std::string, unsigned> word_or_phrase_and_occurrence_count;

	if (options & INDIVIDUAL_WORDS_ONLY or options & PHRASES_AND_INDIVIDUAL_WORDS) {
		for (std::vector<std::string>::const_iterator word(words.begin()); word != words.end(); ++word) {
			if (not word->empty())
				UpdateWordOrPhraseCounts(StringUtil::ToLower(*word), &word_or_phrase_and_occurrence_count);
		}
	}

	if (options & PHRASES_ONLY or options & PHRASES_AND_INDIVIDUAL_WORDS) {
		// Assign word classes:
		std::vector<WordClass> word_classes;
		word_classes.reserve(words.size());
		AssignWordClasses(words, &word_classes);

		std::vector<WordClass>::const_iterator word_class(word_classes.begin());
		for (std::vector<std::string>::const_iterator word(words.begin()); word != words.end(); ) {
			unsigned phrase_length(GetPhraseLength(words.end(), word, word_class));
			if (phrase_length == 0)
				++word, ++word_class;
			else { // Extract the phrase:
				std::string phrase(*word);
				++word, ++word_class, --phrase_length;
				for (/* Empty. */; phrase_length > 0; ++word, ++word_class, --phrase_length) {
					if (phrase_length > 1 or *word_class != STATE_OR_POSSESSION)
						phrase += ' ';
					phrase += *word;
				}

				UpdateWordOrPhraseCounts(phrase, &word_or_phrase_and_occurrence_count);
			}
		}
	}

	for (GNU_HASH_MAP<std::string, unsigned>::const_iterator word_and_count(word_or_phrase_and_occurrence_count.begin());
	     word_and_count != word_or_phrase_and_occurrence_count.end(); ++word_and_count)
		words_and_phrases->push_back(WordAndPhraseInfo(word_and_count->first, word_and_count->second));

	// Sort by deacreasing number of occurrences:
	std::sort(words_and_phrases->begin(), words_and_phrases->end(), HasHigherOccurrenceCount);

	// Remove anything that does not meet the minimum occurrence count requirement:
	std::vector<WordAndPhraseInfo>::reverse_iterator word_or_phrase(words_and_phrases->rbegin());
	for (/* Empty. */; word_or_phrase != words_and_phrases->rend(); ++word_or_phrase) {
		if (word_or_phrase->occurrence_count_ >= min_phrase_or_word_frequency)
			break;
	}
	words_and_phrases->resize(words_and_phrases->rend() - word_or_phrase);
}


std::string LineWrap(const std::string &source_string, const unsigned max_width, const LineWrapOptions options,
		     const unsigned leading_indent, const unsigned hanging_indent, const std::string &whitespace)
{
	const std::string leading_indent_string(std::string(leading_indent, ' '));
	const std::string hanging_indent_string(std::string(hanging_indent, ' '));

	unsigned output_line_count(0);
	std::string work_line;                 // We add to this and when done, append it to the output.
	                                       // Useful when backtracking to decide where to break a line.

	std::string output;

	for (std::string::const_iterator source(source_string.begin()); source != source_string.end(); source++) {
		// Ignore carriage returns
		if (*source == '\r')
			continue;

		// Convert tab characters to spaces:
		if (options | CONVERT_TABS_TO_SPACES and *source == '\t') {
			output += ' ';
			continue;
		}

		// How much line break do we have here? If more than one line, it's a hard ending, accept users decision to
		// break here. Otherwise, merge with previous line.
		if (*source == '\n') {
			source++;
			if (source == source_string.end())
				break;
			if (*source != '\n') { // Just a standalone \n? convert it to a space and keep parsing.
				work_line += ' ';
				continue;
			}

			if (output_line_count < 1)
				output += leading_indent_string;
			else
				output += hanging_indent_string;
			output += work_line;
			output += "\n\n";
			work_line.clear();
			++output_line_count;
			continue;
		}

		// All junk characters gone or transformations performed, append to work_line:
		work_line += *source;

		if (work_line.length() >= max_width) {
			std::string::size_type whitespace_offset = work_line.find_last_of(whitespace.c_str());
			if (output_line_count < 1)
				output += leading_indent_string;
			else
				output += hanging_indent_string;
			// Append leftmost part of workline broken at last whitespace before max_length:
			output += work_line.substr(0, whitespace_offset);
			output += '\n';
			work_line = work_line.substr(whitespace_offset, work_line.length() - whitespace_offset);
			++output_line_count;
		}
	}

	// Append any leftover:
	output += work_line;

	return output;
}


// LetterCount -- returns the number of letters in "text".
//
unsigned LetterCount(const std::string &text)
{
	unsigned letter_count(0);
	for (std::string::const_iterator ch(text.begin()); ch != text.end(); ++ch) {
		if (isalpha(*ch))
			++letter_count;
	}

	return letter_count;
}


namespace ExtractPhrasesHelpers {


const char RESET_TOKEN('^');


// NormaliseSpacesAndLowercase -- convert all "space" characters into blanks and all uppercase letters to lowercase.
//                                Helper function for ExtractWords().
//
std::string &NormaliseSpacesAndLowercase(std::string * const text)
{
	const char NO_BREAK_SPACE = 0xA0; // Does not seem to be treated as a space character by isspace for some reason!
	for (std::string::iterator ch(text->begin()); ch != text->end(); ++ch) {
		if (isspace(*ch) or *ch == NO_BREAK_SPACE)
			*ch = ' ';
		else if (isupper(*ch))
			*ch = tolower(*ch);
	}

	return *text;
}


// CanonizeText -- helper function for ExtractWords().  Converts many special characters to blanks and reset tokens.
//
std::string CanonizeText(std::string * const exact_phrase_text)
{
	std::string result;
	result.reserve(exact_phrase_text->size() * 2);

	const char reset_token_string[] = { ' ', RESET_TOKEN, ' ', '\0' };
	for (std::string::iterator ch(exact_phrase_text->begin()); ch != exact_phrase_text->end(); ++ch) {
		if (*ch == '(' or *ch == ')' or *ch == '"' or *ch == '.' or *ch == ';' or *ch == ':')
			result += reset_token_string;
		else if (not isalnum(*ch) and *ch != '\'' and *ch != '-' and *ch != '?' and *ch != ',' and *ch != '_') {
			if (ch != exact_phrase_text->begin() or *(ch - 1) != ' ')
				result += ' ';
		}
		else
			result += *ch;
	}

	return *exact_phrase_text = result;
}


void ExtractWords(const std::string &sentence, std::vector<std::string> * const words, const char * const trim_chars = " \n\t\r\"\\()[]{}&-.,;:*'!+/%#")
{
	words->clear();

	std::string normalised_sentence(sentence);
	NormaliseSpacesAndLowercase(&normalised_sentence);

	CanonizeText(&normalised_sentence);

	// Convert everything that is not a letter, number, period, hyphen or RESET_TOKEN to a space:
	for (std::string::iterator ch(normalised_sentence.begin()); ch != normalised_sentence.end(); ++ch) {
		if (not isalnum(*ch) and *ch != '-' and *ch != '\'' and *ch != '.' and *ch != '_' and *ch != RESET_TOKEN) {
			if (ch != normalised_sentence.begin() and *(ch - 1) != ' ')
				*ch = ' ';
		}
	}

	// No "words" to process?
	if (unlikely(normalised_sentence.empty()))
		return;

	// Add the individual words of the subfield:
	normalised_sentence += ' '; // add a trailing word separator
	size_t start = 0;
	const char reset_token_string_c_str[] = { RESET_TOKEN, '\0' };
	const std::string reset_token_string(reset_token_string_c_str);
	for (size_t space_pos = normalised_sentence.find(' '); space_pos != std::string::npos; space_pos = normalised_sentence.find(' ', space_pos + 1)) {
		// Get the next word:
		std::string word(normalised_sentence.substr(start, space_pos - start));
		start = space_pos + 1;

		StringUtil::Trim(trim_chars, &word);
		if (word == reset_token_string)
			words->push_back(word);
		else {
			// Collapse possessive forms, e.g. "houses'" -> "houses" and "fred's" -> "fred":
			if (StringUtil::EndsWith(word, "'"))
				word = word.substr(word.length() - 1);
			else if (StringUtil::EndsWith(word, "'s"))
				word = word.substr(0, word.length() - 2);

			if (TextUtil::IsPossiblyAWord(word))
				words->push_back(word);
		}
	}
}


class StringCountMap: public GNU_HASH_MAP<std::string, unsigned> {
public:
	void add(const std::string &s);
};


void StringCountMap::add(const std::string &s)
{
	iterator string_and_count(find(s));
	if (string_and_count != end())
		++string_and_count->second;
	else
		insert(std::pair<std::string, unsigned>(s, 1));
}


// LookAhead -- returns the next character folloing "ch" unless ch == end, in which case it returns a NUL.
//
inline char LookAhead(const std::string::const_iterator &ch, const std::string::const_iterator &end)
{
	if (ch + 1 == end)
		return '\0';
	else
		return *(ch + 1);
}


// IsInitialcapsWord -- returns true if the first character of "initialcaps_word_candidate" is an uppercase letter and any additional, optional
//                      characters are lowercase letters.  Please note that this function is locale dependent and that a single upper case letter causes it
//                      to return true.
//
bool IsInitialcapsWord(const std::string &initialcaps_word_candidate)
{
	if (unlikely(initialcaps_word_candidate.empty()) or not isupper(initialcaps_word_candidate[0]))
		return false;

	for (std::string::const_iterator ch(initialcaps_word_candidate.begin() + 1); ch != initialcaps_word_candidate.end(); ++ch) {
		if (not islower(*ch))
			return false;
	}

	return true;
}


inline bool IsUppercaseletter(const std::string &s)
{
	if (s.length() != 1)
		return false;
	return isupper(s[0]);
}


void TranslateCapsPhraseCandidate(const std::vector<std::string> &caps_word_candidate, StringCountMap * const word_or_phrase_and_occurrence_count)
{
	if (not caps_word_candidate.empty()) {
		std::string phrase;
		StringUtil::Join(caps_word_candidate, ' ', &phrase);
		word_or_phrase_and_occurrence_count->add(phrase);
	}
}


enum State { IGNORING, EXTRACTING_WORD, EXTRACTING_NUMBER };


void ExtractCapsPhrasesAndIndividualWords(const std::string &text, StringCountMap * const word_or_phrase_and_occurrence_count)
{
	std::vector<std::string> components;

	State state(IGNORING);
	std::string word_or_number;
	for (std::string::const_iterator ch(text.begin()); ch != text.end(); ++ch) {
		switch (state) {
		case IGNORING:
			if (isalpha(*ch)) {
				word_or_number = *ch;
				state = EXTRACTING_WORD;
			}
			else if (isdigit(*ch)) {
				word_or_number = *ch;
				state = EXTRACTING_NUMBER;
			}
			else if (*ch == '.')
				components.push_back(".");
			else if (*ch == '!')
				components.push_back("!");
			else if (*ch == '?')
				components.push_back("?");
			break;
		case EXTRACTING_WORD:
			if (isalnum(*ch) or *ch == '-')
				word_or_number += *ch;
			else {
				components.push_back(word_or_number);
				word_or_number.clear();
				state = IGNORING;
			}
			break;
		case EXTRACTING_NUMBER:
			if (isdigit(*ch) or (*ch == '.' and isdigit(LookAhead(ch, text.end()))))
				word_or_number += *ch;
			else {
				components.push_back(word_or_number);
				if (isalpha(*ch)) {
					word_or_number = *ch;
					state = EXTRACTING_WORD;
				}
				else {
					word_or_number.clear();
					state = IGNORING;
				}
			}
			break;
		}
	}

	bool possible_sentence_start(true);
	std::vector<std::string> current_caps_phrase_candidate;
	for (std::vector<std::string>::const_iterator component(components.begin()); component != components.end(); ++component) {
		if (IsInitialcapsWord(*component)) {
			static CommonEnglishWords common_english_words;
			if (common_english_words.isCommonWord(*component)) {
				TranslateCapsPhraseCandidate(current_caps_phrase_candidate, word_or_phrase_and_occurrence_count);
				current_caps_phrase_candidate.clear();
				word_or_phrase_and_occurrence_count->add(StringUtil::ToLower(*component));
			}
			else if (not possible_sentence_start or (component + 1 != components.end() and IsInitialcapsWord(*(component + 1)))
				 or IsCommonFirstName(*component) or IsCommonSurname(*component))
				current_caps_phrase_candidate.push_back(*component);
			else {
				TranslateCapsPhraseCandidate(current_caps_phrase_candidate, word_or_phrase_and_occurrence_count);
				current_caps_phrase_candidate.clear();
				word_or_phrase_and_occurrence_count->add(StringUtil::ToLower(*component));
			}
		}
		else if (*component == ".") {
			if (not current_caps_phrase_candidate.empty() and IsUppercaseletter(current_caps_phrase_candidate.back()))
				current_caps_phrase_candidate.back() += '.';
			else {
				TranslateCapsPhraseCandidate(current_caps_phrase_candidate, word_or_phrase_and_occurrence_count);
				current_caps_phrase_candidate.clear();
				possible_sentence_start = true;
				continue;
			}
		}
		else if (*component == "?" or *component == "!") {
			TranslateCapsPhraseCandidate(current_caps_phrase_candidate, word_or_phrase_and_occurrence_count);
			current_caps_phrase_candidate.clear();
			possible_sentence_start = true;
			continue;
		}
		else {
			TranslateCapsPhraseCandidate(current_caps_phrase_candidate, word_or_phrase_and_occurrence_count);
			current_caps_phrase_candidate.clear();
			word_or_phrase_and_occurrence_count->add(*component);
		}

		possible_sentence_start = false;
	}

	TranslateCapsPhraseCandidate(current_caps_phrase_candidate, word_or_phrase_and_occurrence_count);
}


} // unnamed ExtractPhrasesHelpers


void ExtractPhrases(const std::string &text, std::vector<WordAndPhraseInfo> * const words_and_phrases, const WordAndPhraseExtractionOptions options,
		    const unsigned max_phrase_length, const unsigned min_phrase_or_word_frequency)
{
	if (unlikely(max_phrase_length > 5))
		throw Exception("in TextUtil::ExtractPhrases: \"max_phrase_length\" parameter must be <= 5!");

	std::vector<std::string> words;
	ExtractPhrasesHelpers::ExtractWords(text, &words);
	ExtractPhrasesHelpers::StringCountMap word_or_phrase_and_occurrence_count;

	if (options == CAPS_PHRASES_AND_INDIVIDUAL_WORDS)
		ExtractPhrasesHelpers::ExtractCapsPhrasesAndIndividualWords(text, &word_or_phrase_and_occurrence_count);

	if (options & INDIVIDUAL_WORDS_ONLY or options & PHRASES_AND_INDIVIDUAL_WORDS) {
		for (std::vector<std::string>::const_iterator word(words.begin()); word != words.end(); ++word) {
			if (not word->empty() and (*word)[0] != ExtractPhrasesHelpers::RESET_TOKEN)
				UpdateWordOrPhraseCounts(StringUtil::ToLower(*word), &word_or_phrase_and_occurrence_count);
		}
	}

	if (options & PHRASES_ONLY or options & PHRASES_AND_INDIVIDUAL_WORDS) {
		ExtractPhrasesHelpers::StringCountMap two_word_sequences_and_counts, three_word_sequences_and_counts, four_word_sequences_and_counts,
			                              five_word_sequences_and_counts;
		std::string last_word, second_to_last_word, third_to_last_word, fourth_to_last_word;
		for (std::vector<std::string>::const_iterator current_word(words.begin()); current_word != words.end(); ++current_word) {
			if (current_word->length() == 1) {
				fourth_to_last_word.clear();
				third_to_last_word.clear();
				second_to_last_word.clear();
				last_word.clear();
				continue;
			}

			if (not WordIsBadLeader(last_word) and not WordIsBadTrailer(*current_word)) {
				const std::string two_word_sequence(last_word + " " + *current_word);
				two_word_sequences_and_counts.add(two_word_sequence);
			}

			if (not WordIsBadLeader(second_to_last_word) and not WordIsBadTrailer(*current_word)) {
				const std::string three_word_sequence(second_to_last_word + " " + last_word + " " + *current_word);
				three_word_sequences_and_counts.add(three_word_sequence);
			}

			if (not WordIsBadLeader(third_to_last_word) and not WordIsBadTrailer(*current_word)) {
				const std::string four_word_sequence(third_to_last_word + " " + second_to_last_word + " " + last_word + " "
								     + *current_word);
				four_word_sequences_and_counts.add(four_word_sequence);
			}

			if (not WordIsBadLeader(fourth_to_last_word) and not WordIsBadTrailer(*current_word)) {
				const std::string five_word_sequence(fourth_to_last_word + " " + third_to_last_word + " " + second_to_last_word + " "
								     + last_word + " " + *current_word);
				five_word_sequences_and_counts.add(five_word_sequence);
			}

			fourth_to_last_word = third_to_last_word;
			third_to_last_word  = second_to_last_word;
			second_to_last_word = last_word;
			last_word           = *current_word;
		}

		for (GNU_HASH_MAP<std::string, unsigned>::const_iterator sequence_and_count(two_word_sequences_and_counts.begin());
		     sequence_and_count != two_word_sequences_and_counts.end(); ++sequence_and_count)
			words_and_phrases->push_back(WordAndPhraseInfo(sequence_and_count->first, sequence_and_count->second));

		if (max_phrase_length > 2) {
			for (GNU_HASH_MAP<std::string, unsigned>::const_iterator sequence_and_count(three_word_sequences_and_counts.begin());
			     sequence_and_count != three_word_sequences_and_counts.end(); ++sequence_and_count)
				words_and_phrases->push_back(WordAndPhraseInfo(sequence_and_count->first, sequence_and_count->second));
		}

		if (max_phrase_length > 3) {
			for (GNU_HASH_MAP<std::string, unsigned>::const_iterator sequence_and_count(four_word_sequences_and_counts.begin());
			     sequence_and_count != four_word_sequences_and_counts.end(); ++sequence_and_count)
				words_and_phrases->push_back(WordAndPhraseInfo(sequence_and_count->first, sequence_and_count->second));
		}

		if (max_phrase_length > 4) {
			for (GNU_HASH_MAP<std::string, unsigned>::const_iterator sequence_and_count(five_word_sequences_and_counts.begin());
			     sequence_and_count != five_word_sequences_and_counts.end(); ++sequence_and_count)
				words_and_phrases->push_back(WordAndPhraseInfo(sequence_and_count->first, sequence_and_count->second));
		}
	}

	for (GNU_HASH_MAP<std::string, unsigned>::const_iterator word_and_count(word_or_phrase_and_occurrence_count.begin());
	     word_and_count != word_or_phrase_and_occurrence_count.end(); ++word_and_count)
		words_and_phrases->push_back(WordAndPhraseInfo(word_and_count->first, word_and_count->second));

	// Sort by decreasing number of occurrences:
	std::sort(words_and_phrases->begin(), words_and_phrases->end(), HasHigherOccurrenceCount);

	// Remove anything that does not meet the minimum occurrence count requirement:
	std::vector<WordAndPhraseInfo>::reverse_iterator word_or_phrase(words_and_phrases->rbegin());
	for (/* Empty. */; word_or_phrase != words_and_phrases->rend(); ++word_or_phrase) {
		if (word_or_phrase->occurrence_count_ >= min_phrase_or_word_frequency)
			break;
	}
	words_and_phrases->resize(words_and_phrases->rend() - word_or_phrase);
}


bool ConvertToPlainText(const std::string &document, const std::string &media_type, std::string * const plain_text)
{
	std::string guessed_media_type;
	if (media_type.empty())
		guessed_media_type = MediaTypeUtil::GetMediaType(document);

	if (PerlCompatRegExp::Match("text/x?(ht)?ml(;.*)?", (media_type.empty() ? guessed_media_type : media_type)))
		*plain_text = HtmlUtil::HtmlToText(document);
	// Assume if it's not text/html it's some acceptable form of plaintext such as plain or c source code
	else if (PerlCompatRegExp::Match("text/(.*?)(;.*)?", (media_type.empty() ? guessed_media_type : media_type)))
		*plain_text = document;
	else if (PerlCompatRegExp::Match("application/pdf(;.*)?", (media_type.empty() ? guessed_media_type : media_type)))
		*plain_text = TextUtil::PdfToText(document);
	else if (PerlCompatRegExp::Match("application/postscript(;.*)?", (media_type.empty() ? guessed_media_type : media_type)))
		*plain_text = TextUtil::PostScriptToText(document);
	else if (PerlCompatRegExp::Match("application/msword(;.*)?", (media_type.empty() ? guessed_media_type : media_type)))
		*plain_text = TextUtil::WordToText(document);
	else
		return false;

	return true;
}


namespace {


const char *english_months[] = {
	"january",
	"february",
	"march",
	"april",
	"may",
	"june",
	"july",
	"august",
	"september",
	"october",
	"november",
	"december",
};


const char *english_month_abbrevs[] = {
	"jan",
	"feb",
	"mar",
	"apr",
	"may",
	"jun",
	"jul",
	"aug",
	"sep",
	"oct",
	"nov",
	"dec",
};


} // unnamed namespace


bool IsEnglishMonthOrMonthAbbrev(const std::string &month_candidate)
{
	if (month_candidate.length() < 3 or month_candidate.length() > 9)
		return false;

	const std::string lowercase_month_candidate(StringUtil::ToLower(month_candidate));

	// 1. Deal with the full month names...
	for (const char **english_month(english_months);
	     english_month < english_months + DIM(english_months); ++english_month)
	{
		if (std::strcmp(lowercase_month_candidate.c_str(), *english_month) == 0)
			return true;
	}

	// 2. ...and now the month abbreviations:
	if (likely(month_candidate.length() != 3))
		return false;
	for (const char **english_month_abbrev(english_month_abbrevs);
	     english_month_abbrev < english_month_abbrevs + DIM(english_month_abbrevs); ++english_month_abbrev)
	{
		if (std::strcmp(lowercase_month_candidate.c_str(), *english_month_abbrev) == 0)
			return true;
	}

	return false;
}


namespace {


const char *english_days[] = {
	"monday",
	"tuesday",
	"wednesday",
	"thursday",
	"friday",
};


const char *english_day_abbrevs[] = {
	"mon",
	"tue",
	"wed",
	"thur",
	"fri",
};


} // unnamed namespace


bool IsEnglishDayOrDayAbbrev(const std::string &day_candidate)
{
	if (day_candidate.length() < 3 or day_candidate.length() > 9)
		return false;

	const std::string lowercase_day_candidate(StringUtil::ToLower(day_candidate));

	// 1. Deal with the full day names...
	for (const char **english_day(english_days); english_day < english_days + DIM(english_days); ++english_day) {
		if (std::strcmp(lowercase_day_candidate.c_str(), *english_day) == 0)
			return true;
	}

	// 2. ...and now the day abbreviations:
	if (day_candidate.length() != 3 and day_candidate.length() != 4)
		return false;
	for (const char **english_day_abbrev(english_day_abbrevs); english_day_abbrev < english_day_abbrevs + DIM(english_day_abbrevs);
	     ++english_day_abbrev)
	{
		if (std::strcmp(lowercase_day_candidate.c_str(), *english_day_abbrev) == 0)
			return true;
	}

	return false;
}


namespace {


const char *english_prepositions[] = {
	"aboard",
	"about",
	"above",
	"absent",
	"according to",
	"across",
	"after",
	"against",
	"ahead of",
	"along",
	"alongside",
	"amid",
	"amidst",
	"among",
	"amongst",
	"anti",
	"around",
	"as",
	"as far as",
	"as to",
	"as well as",
	"aside from",
	"astride",
	"at",
	"atop",
	"because of",
	"before",
	"behind",
	"below",
	"beneath",
	"beside",
	"besides",
	"between",
	"betwixt",
	"beyond",
	"but",
	"by",
	"by means of",
	"circa",
	"close to",
	"concerning",
	"considering",
	"cum",
	"despite",
	"down",
	"due to",
	"during",
	"except",
	"far from",
	"following",
	"for",
	"from",
	"in",
	"in accordance with",
	"in addition to",
	"in case of",
	"in front of",
	"in lieu of",
	"in place of",
	"in spite of",
	"in to",
	"inside",
	"inside of",
	"instead of",
	"into",
	"like",
	"mid",
	"minus",
	"near to",
	"near",
	"nearest",
	"next to",
	"notwithstanding",
	"of",
	"off",
	"on",
	"on account of",
	"on behalf of",
	"on to",
	"on top of",
	"onto",
	"opposite",
	"out of",
	"out",
	"outside of",
	"outside",
	"over",
	"owing to",
	"past",
	"per",
	"prior to",
	"qua",
	"re",
	"regarding",
	"round",
	"sans",
	"save",
	"since",
	"subsequent to",
	"than",
	"through",
	"throughout",
	"till",
	"to",
	"toward",
	"towards",
	"under",
	"underneath",
	"unlike",
	"until",
	"unto",
	"up",
	"upon",
	"versus",
	"via",
	"vis-‡-vis",
	"with",
	"with regard to",
	"within",
	"without",
};


} // unnamed namespace


namespace {


const char *english_determiner[] = {
	"a",
	"an",
	"every",
	"her",
	"his",
	"its",
	"me",
	"my",
	"no",
	"our",
	"the",
	"their",
	"your",
};


const char *english_determiner_or_pronoun[] = {
	"all",
	"another",
	"any",
	"both",
	"each",
	"either",
	"enough",
	"few",
	"fewer",
	"former",
	"half",
	"latter",
	"less",
	"little",
	"many",
	"more",
	"most",
	"much",
	"neither",
	"own",
	"same",
	"several",
	"some",
	"such",
	"that",
	"these",
	"this",
	"those",
	"what",
	"whatever",
	"which",
	"whose",
};


const char *english_conjunctions[] = { // Also includes conjunctional phrases.
	"after",
	"albeit",
	"although",
	"and",
	"and/or",
	"as",
	"as if",
	"as long as",
	"as soon as",
	"as though",
	"because",
	"before",
	"but",
	"even if",
	"even though",
	"even when",
	"except",
	"except that",
	"for",
	"given that",
	"if",
	"in case",
	"in that",
	"like",
	"nor",
	"now that",
	"once",
	"or",
	"provided",
	"provided that",
	"rather than",
	"since",
	"so",
	"so long as",
	"so that",
	"than",
	"that",
	"though",
	"till",
	"unless",
	"until",
	"when",
	"where",
	"whereas",
	"whether",
	"whether or not",
	"while",
	"whilst",
};


const char *english_pronouns[] = {
	"anybody",
	"anyone",
	"anything",
	"each other",
	"everybody",
	"everyone",
	"everything",
	"he",
	"her",
	"hers",
	"herself",
	"him",
	"himself",
	"his",
	"itself",
	"lots",
	"me",
	"mine",
	"myself",
	"no one",
	"nobody",
	"none",
	"noone",
	"nothing",
	"one another",
	"ours",
	"ourselves",
	"plenty",
	"she",
	"somebody",
	"someone",
	"something",
	"theirs",
	"them",
	"themselves",
	"they",
	"us",
	"we",
	"who",
	"whoever",
	"whom",
	"you",
	"yours",
	"yourself",
};


} // unnamed namespace


inline bool IsEnglishDeterminer(const std::string &determiner_candidate)
{
	const char **end(&english_determiner[0] + DIM(english_determiner));
	return StlHelpers::BinarySearch(&english_determiner[0], end, determiner_candidate.c_str(), StringUtil::strless) != end;
}


inline bool IsEnglishDeterminerOrPronoun(const std::string &determiner_or_pronoun_candidate)
{
	const char **end(&english_determiner_or_pronoun[0] + DIM(english_determiner_or_pronoun));
	return StlHelpers::BinarySearch(&english_determiner_or_pronoun[0], end, determiner_or_pronoun_candidate.c_str(), StringUtil::strless)
	       != end;
}


SimpleTokenStream::const_iterator RangeMatch(const SimpleTokenStream::const_iterator &start, const SimpleTokenStream::const_iterator &end,
					     const char ** const list_start, const char ** const list_end,
					     std::string * const matched_string)
{
	// This should never happen!
	if (unlikely(start == end))
		throw Exception("in RangeMatch: gone too far!");

	// First component must be a word:
	if (start->type_ != ST_ALPHA)
		return end;

	std::string prefix_candidate(start->value_);
	StringUtil::ToLower(&prefix_candidate);

	const char **match(std::lower_bound(list_start, list_end, prefix_candidate.c_str(), StringUtil::strless));

	// If we don't even have a prefix match, we bail now:
	if (match == list_end) {
		matched_string->clear();
		return end;
	}

	// Remember an exact match, if we actually have one:
	SimpleTokenStream::const_iterator longest_exact_match_so_far(std::strcmp(prefix_candidate.c_str(), *match) == 0 ? start : end);

	// If we don't have an exact match we make sure that the match is a proper prefix or we bail:
	if (longest_exact_match_so_far == end) {
		const size_t first_word_length(prefix_candidate.size());
		const size_t first_match_length(std::strlen((*match)));
		if (first_match_length <= first_word_length or isalpha((*match)[first_word_length])) {
			matched_string->clear();
			return end;
		}
	}

	SimpleTokenStream::const_iterator current_token(start);
	++current_token;

	// We now have to sequentially probe for a longer matching sequence:
	const char **next_match_candidate(match);
	if (longest_exact_match_so_far != end)
		++next_match_candidate;
	for (/* Empty. */; current_token != end; ++current_token) {
		size_t next_match_candidate_length(std::strlen(*next_match_candidate));
		if (next_match_candidate_length <= prefix_candidate.length())
			goto bail;

		if (current_token->type_ == ST_WHITESPACE) {
			if ((*next_match_candidate)[prefix_candidate.length()] != ' ')
				goto bail;
			prefix_candidate += ' ';
		}
		else if (current_token->type_ == ST_NON_ALNUM_CHAR) {
			if ((*next_match_candidate)[prefix_candidate.length()] != current_token->value_[0])
				goto bail;
			prefix_candidate += current_token->value_[0];
		}
		else
			goto bail;

		//
		// Look for the next word!
		//

		++current_token;
		if (current_token == end or current_token->type_ != ST_ALPHA) {
			--current_token;
			goto bail;
		}

		prefix_candidate += StringUtil::ToLower(current_token->value_);

		while (prefix_candidate > *next_match_candidate) {
			++next_match_candidate;

			// Gone too far?
			if (next_match_candidate == list_end) {
				--current_token;
				goto bail;
			}
		}

		if (not StringUtil::IsPrefixOf(prefix_candidate, *next_match_candidate)) {
			--current_token;
			goto bail;
		}

		if (prefix_candidate == *next_match_candidate)
			longest_exact_match_so_far = current_token;
	}

bail:
	matched_string->clear();
	if (longest_exact_match_so_far != end) {
		// Reconstruct the original string:
		for (SimpleTokenStream::const_iterator token(start); token != longest_exact_match_so_far + 1; ++token)
			*matched_string += token->value_;
	}

	return longest_exact_match_so_far;
}


inline SimpleTokenStream::const_iterator GetConjunctionEnd(const SimpleTokenStream::const_iterator &start, const SimpleTokenStream::const_iterator &end,
							   std::string * const matched_string)
{
	return RangeMatch(start, end, &english_conjunctions[0], &english_conjunctions[0] + DIM(english_conjunctions), matched_string);
}


inline SimpleTokenStream::const_iterator GetPrepositionEnd(const SimpleTokenStream::const_iterator &start, const SimpleTokenStream::const_iterator &end,
							   std::string * const matched_string)
{
	return RangeMatch(start, end, &english_prepositions[0], &english_prepositions[0] + DIM(english_prepositions), matched_string);
}


inline SimpleTokenStream::const_iterator GetPronounEnd(const SimpleTokenStream::const_iterator &start, const SimpleTokenStream::const_iterator &end,
						       std::string * const matched_string)
{
	// Deal with the special always uppercase pronoun "I":
	if (start->type_ == ST_ALPHA and start->value_ == "I") {
		*matched_string = 'I';
		return start;
	}

	return RangeMatch(start, end, &english_pronouns[0], &english_pronouns[0] + DIM(english_pronouns), matched_string);
}


std::string TokenTypeToString(const TokenType token_type)
{
	switch (token_type) {
	case TT_LOWERCASE_WORD:
		return "TT_LOWERCASE_WORD";
	case TT_UPPERCASE_WORD:
		return "TT_UPPERCASE_WORD";
	case TT_INITIAL_CAPS_WORD:
		return "TT_INITIAL_CAPS_WORD";
	case TT_CAPS_CHAR_AND_PERIOD:
		return "TT_CAPS_CHAR_AND_PERIOD";
	case TT_INTEGER:
		return "TT_INTEGER";
	case TT_COMMA:
		return "TT_COMMA";
	case TT_SEMICOLON:
		return "TT_SEMICOLON";
	case TT_PERIOD:
		return "TT_PERIOD";
	case TT_QUESTION_MARK:
		return "TT_QUESTION_MARK";
	case TT_EXCLAMATION_POINT:
		return "TT_EXCLAMATION_POINT";
	case TT_OPEN_PAREN:
		return "TT_OPEN_PAREN";
	case TT_CLOSE_PAREN:
		return "TT_CLOSE_PAREN";
	case TT_EMAIL_ADDRESS:
		return "TT_EMAIL_ADDRESS";
	case TT_INSTITUTION_INDICATOR:
		return "TT_INSTITUTION_INDICATOR";
	case TT_COMMON_ENGLISH_WORD:
		return "TT_COMMON_ENGLISH_WORD";
	case TT_WHITESPACE:
		return "TT_WHITESPACE";
	case TT_LINEEND :
		return "TT_LINEEND";
	case TT_ORDINAL:
		return "TT_ORDINAL";
	case TT_PREPOSITION:
		return "TT_PREPOSITION";
	case TT_CAPS_PREPOSITION:
		return "TT_CAPS_PREPOSITION";
	case TT_POSSIBLE_RECENT_YEAR:
		return "TT_POSSIBLE_RECENT_YEAR";
	case TT_FIRST_NAME:
		return "TT_FIRST_NAME";
	case TT_LAST_NAME:
		return "TT_LAST_NAME";
	case TT_FIRST_NAME_OR_REGULAR_WORD:
		return "TT_FIRST_NAME_OR_REGULAR_WORD";
	case TT_LAST_NAME_OR_REGULAR_WORD:
		return "TT_LAST_NAME_OR_REGULAR_WORD";
	case TT_DETERMINER:
		return "TT_DETERMINER";
	case TT_INITIAL_CAPS_DETERMINER:
		return "TT_INITIAL_CAPS_DETERMINER";
	case TT_DETERMINER_OR_PRONOUN:
		return "TT_DETERMINER_OR_PRONOUN";
	case TT_INITIAL_CAPS_DETERMINER_OR_PRONOUN:
		return "TT_INITIAL_CAPS_DETERMINER_OR_PRONOUN";
	case TT_CONJUNCTION:
		return "TT_CONJUNCTION";
	case TT_CAPS_CONJUNCTION:
		return "TT_CAPS_CONJUNCTION";
	case TT_ONE:
		return "TT_ONE";
	case TT_INITIAL_CAPS_ONE:
		return "TT_INITIAL_CAPS_ONE";
	case TT_PRONOUN:
		return "TT_PRONOUN";
	case TT_CAPS_PRONOUN:
		return "TT_CAPS_PRONOUN";
	case TT_MISCELLANEOUS:
		return "TT_MISCELLANEOUS";
	case TT_ENGLISH_MONTH_OR_MONTH_ABBREV:
		return "TT_ENGLISH_MONTH_OR_MONTH_ABBREV";
	case TT_ENGLISH_DAY_OR_DAY_ABBREV:
		return "TT_ENGLISH_DAY_OR_DAY_ABBREV";
	case TT_STATE_OR_POSSESSION:
		return "TT_STATE_OR_POSSESSION";
	case TT_COMMA_AND_US_STATE_ABBREV:
		return "TT_COMMA_AND_US_STATE_ABBREV";
	case TT_ABSTRACT:
		return "TT_ABSTRACT";
	case TT_CITY_AND_STATE:
		return "TT_CITY_AND_STATE";
	case TT_POSSIBLE_US_ZIP_CODE:
		return "TT_POSSIBLE_US_ZIP_CODE";
	default:
		throw Exception("in TextUtil::TokenTypeToString: unknown token type " + StringUtil::ToString(token_type) + "!");
	}
}


inline bool IsPossibleUsZipCode(const std::string &text)
{
	static PerlCompatRegExp zip_code_regexp("^\\d\\d\\d\\d\\d(-\\d\\d\\d\\d)?$");
	return zip_code_regexp.match(text);
}


bool IsInstitutionIndicator(const std::string &text)
{
	if (::strcasecmp(text.c_str(), "center") == 0)
		return true;

	if (::strcasecmp(text.c_str(), "centre") == 0)
		return true;

	if (::strcasecmp(text.c_str(), "college") == 0)
		return true;

	if (::strcasecmp(text.c_str(), "dept") == 0)
		return true;

	if (::strcasecmp(text.c_str(), "department") == 0)
		return true;

	if (::strcasecmp(text.c_str(), "foundation") == 0)
		return true;

	if (::strncasecmp(text.c_str(), "institute", 7) == 0)
		return true;

	if (::strcasecmp(text.c_str(), "laboratory") == 0)
		return true;

	if (::strcasecmp(text.c_str(), "labs") == 0)
		return true;

	if (::strncasecmp(text.c_str(), "politec", 7) == 0)
		return true;

	if (::strcasecmp(text.c_str(), "school") == 0)
		return true;

	if (::strncasecmp(text.c_str(), "universi", 8) == 0)
		return true;

	if (::strcasecmp(text.c_str(), "univ") == 0)
		return true;

	return false;
}


TokenType GetTokenType(const std::string &text)
{
	if (unlikely(text.empty()))
		throw Exception("in TextUtil::GetTokenType: \"text\" should never be empty!");

	if (IsInstitutionIndicator(text))
		return TT_INSTITUTION_INDICATOR;

	if (IsEnglishDeterminer(text))
		return TT_DETERMINER;

	if (IsEnglishDeterminerOrPronoun(text))
		return TT_DETERMINER_OR_PRONOUN;

	static PerlCompatRegExp initial_caps_word("[[:upper:]][[:lower:]]+");
	static const Speller &speller(Speller::GetDefaultSpeller());
	const bool is_initial_caps_word(initial_caps_word.match(text));
	if (is_initial_caps_word) {
		std::string all_lowercase_text(text);
		all_lowercase_text[0] = tolower(all_lowercase_text[0]);
		if (IsEnglishDeterminer(text))
			return TT_INITIAL_CAPS_DETERMINER;
		if (IsEnglishDeterminerOrPronoun(text))
			return TT_INITIAL_CAPS_DETERMINER_OR_PRONOUN;

		double surname_frequency, first_name_frequency;
		if (IsEnglishMonthOrMonthAbbrev(text))
			return TT_ENGLISH_MONTH_OR_MONTH_ABBREV;
		else if (IsEnglishDayOrDayAbbrev(text))
			return TT_ENGLISH_DAY_OR_DAY_ABBREV;
		else if (IsCommonAmericanSurname(text, &surname_frequency)) {
			IsCommonAmericanFirstName(text, &first_name_frequency);
			if (speller.isSpelledCorrectly(StringUtil::ToLower(text)))
				return surname_frequency > first_name_frequency ? TT_LAST_NAME_OR_REGULAR_WORD : TT_FIRST_NAME_OR_REGULAR_WORD;
			else
				return surname_frequency > first_name_frequency ? TT_LAST_NAME : TT_FIRST_NAME;
		}
		else if (IsCommonChineseSurname(text)) {
			if (speller.isSpelledCorrectly(StringUtil::ToLower(text)))
				return TT_LAST_NAME_OR_REGULAR_WORD;
			else
				return TT_FIRST_NAME;
		}
		else if (IsCommonFrenchSurname(text)) {
			if (speller.isSpelledCorrectly(StringUtil::ToLower(text)))
				return TT_LAST_NAME_OR_REGULAR_WORD;
			else
				return TT_FIRST_NAME;
		}
		else if (IsCommonFirstName(text)) {
			if (speller.isSpelledCorrectly(StringUtil::ToLower(text)))
				return TT_FIRST_NAME_OR_REGULAR_WORD;
			else
				return TT_FIRST_NAME;
		}
	}

	static CommonEnglishWords common_english_words;
	if (common_english_words.isCommonWord(text))
		return TT_COMMON_ENGLISH_WORD;

	if (is_initial_caps_word)
		return TT_INITIAL_CAPS_WORD;

	if (IsOrdinal(text))
		return TT_ORDINAL;

	if (TextUtil::IsValidEmailAddress(text))
		return TT_EMAIL_ADDRESS;

	if (isalpha(text[0])) {
		std::string::const_iterator ch(text.begin());
		TokenType token_type(isupper(*ch) ? TT_UPPERCASE_WORD : TT_LOWERCASE_WORD);
		++ch;
		if (ch == text.end())
			return token_type;
		if (not isalpha(*ch))
			return (token_type == TT_UPPERCASE_WORD and text.length() == 2 and *ch == '.')
				? TT_CAPS_CHAR_AND_PERIOD : TT_MISCELLANEOUS;
		if (isupper(*ch) and token_type == TT_LOWERCASE_WORD)
			return TT_MISCELLANEOUS;
		if (islower(*ch) and token_type == TT_UPPERCASE_WORD)
			token_type = TT_INITIAL_CAPS_WORD;

		for (/* Empty. */; ch != text.end(); ++ch) {
			if (*ch == '-') { // Deal w/ hyphenated words.
				++ch;
				if (ch == text.end())
					return TT_MISCELLANEOUS;
				else if (isupper(*ch)) {
					if (token_type == TT_LOWERCASE_WORD)
						return TT_MISCELLANEOUS;
				}
				else if (islower(*ch)) {
					if (token_type == TT_UPPERCASE_WORD)
						return TT_MISCELLANEOUS;
				}
				else
					return TT_MISCELLANEOUS;
			}
			else if (isupper(*ch)) {
				if (token_type != TT_UPPERCASE_WORD)
					return TT_MISCELLANEOUS;
			}
			else if (islower(*ch)) {
				if (token_type != TT_LOWERCASE_WORD and token_type != TT_INITIAL_CAPS_WORD)
					return TT_MISCELLANEOUS;
			}
			else
				return TT_MISCELLANEOUS;
		}

		return token_type;
	}

	if (text == ",")
		return TT_COMMA;
	if (text == ";")
		return TT_SEMICOLON;
	if (text == ".")
		return TT_PERIOD;
	if (text == "?")
		return TT_QUESTION_MARK;
	if (text == "!")
		return TT_EXCLAMATION_POINT;
	if (text == "(")
		return TT_OPEN_PAREN;
	if (text == ")")
		return TT_CLOSE_PAREN;

	if (StringUtil::IsUnsignedNumber(text)) {
		unsigned value;
		if (StringUtil::ToUnsigned(text, &value))
			return (value >= 1950 and value <= 2020) ? TT_POSSIBLE_RECENT_YEAR : TT_INTEGER;
		else
			return TT_INTEGER;
	}

	return TT_MISCELLANEOUS;
}


namespace {


struct StateAndStateAbbrev {
	const char *state_;
	const char *abbrev_;
public:
	StateAndStateAbbrev(const char *state, const char *abbrev): state_(state), abbrev_(abbrev) { }
} states_and_state_abbrevs[] = {
	StateAndStateAbbrev("Alabama", "AL"),
	StateAndStateAbbrev("Alaska", "AK"),
	StateAndStateAbbrev("American Samoa", "AS"),
	StateAndStateAbbrev("Arizona", "AZ"),
	StateAndStateAbbrev("Arkansas", "AR"),
	StateAndStateAbbrev("California", "CA"),
	StateAndStateAbbrev("Colorado", "CO"),
	StateAndStateAbbrev("Connecticut", "CT"),
	StateAndStateAbbrev("Delaware", "DE"),
	StateAndStateAbbrev("District of Columbia", "DC"),
	StateAndStateAbbrev("Federated States of Micronesia", "FM"),
	StateAndStateAbbrev("Florida", "FL"),
	StateAndStateAbbrev("Georgia", "GA"),
	StateAndStateAbbrev("Guam", "GU"),
	StateAndStateAbbrev("Hawaii", "HI"),
	StateAndStateAbbrev("Idaho", "ID"),
	StateAndStateAbbrev("Illinois", "IL"),
	StateAndStateAbbrev("Indiana", "IN"),
	StateAndStateAbbrev("Iowa", "IA"),
	StateAndStateAbbrev("Kansas", "KS"),
	StateAndStateAbbrev("Kentucky", "KY"),
	StateAndStateAbbrev("Louisiana", "LA"),
	StateAndStateAbbrev("Maine", "ME"),
	StateAndStateAbbrev("Marshall Islands", "MH"),
	StateAndStateAbbrev("Maryland", "MD"),
	StateAndStateAbbrev("Massachusetts", "MA"),
	StateAndStateAbbrev("Michigan", "MI"),
	StateAndStateAbbrev("Minnesota", "MN"),
	StateAndStateAbbrev("Mississippi", "MS"),
	StateAndStateAbbrev("Missouri", "MO"),
	StateAndStateAbbrev("Montana", "MT"),
	StateAndStateAbbrev("Nebraska", "NE"),
	StateAndStateAbbrev("Nevada", "NV"),
	StateAndStateAbbrev("New Hampshire", "NH"),
	StateAndStateAbbrev("New Jersey", "NJ"),
	StateAndStateAbbrev("New Mexico", "NM"),
	StateAndStateAbbrev("New York", "NY"),
	StateAndStateAbbrev("North Carolina", "NC"),
	StateAndStateAbbrev("North Dakota", "ND"),
	StateAndStateAbbrev("Northern Mariana Islands", "MP"),
	StateAndStateAbbrev("Ohio", "OH"),
	StateAndStateAbbrev("Oklahoma", "OK"),
	StateAndStateAbbrev("Oregon", "OR"),
	StateAndStateAbbrev("Palau", "PW"),
	StateAndStateAbbrev("Pennsylvania", "PA"),
	StateAndStateAbbrev("Puerto Rico", "PR"),
	StateAndStateAbbrev("Rhode Island", "RI"),
	StateAndStateAbbrev("South Carolina", "SC"),
	StateAndStateAbbrev("South Dakota", "SD"),
	StateAndStateAbbrev("Tennessee", "TN"),
	StateAndStateAbbrev("Texas", "TX"),
	StateAndStateAbbrev("Utah", "UT"),
	StateAndStateAbbrev("Vermont", "VT"),
	StateAndStateAbbrev("Virgin Islands", "VI"),
	StateAndStateAbbrev("Virginia", "VA"),
	StateAndStateAbbrev("Washington", "WA"),
	StateAndStateAbbrev("West Virginia", "WV"),
	StateAndStateAbbrev("Wisconsin", "WI"),
	StateAndStateAbbrev("Wyoming", "WY"),
};


// StateAndAbbrevCompare -- helper function for std::bsearch() in GetStateOrPossessionAbbreviation().
//
int StateAndAbbrevCompare(const void *state_and_abbrev1, const void *state_and_abbrev2)
{
	return std::strcmp(reinterpret_cast<const StateAndStateAbbrev * const>(state_and_abbrev1)->state_,
			   reinterpret_cast<const StateAndStateAbbrev * const>(state_and_abbrev2)->state_);
}


} // unnamed namespace


std::string GetStateOrPossessionAbbreviation(const std::string &state_or_possession_candidate)
{
	StateAndStateAbbrev key(state_or_possession_candidate.c_str(), "");
	const StateAndStateAbbrev * const state_and_abbrev(reinterpret_cast<const StateAndStateAbbrev * const>(
								   std::bsearch(&key, &states_and_state_abbrevs[0], DIM(states_and_state_abbrevs),
										sizeof(states_and_state_abbrevs[0]), StateAndAbbrevCompare)));
	return (state_and_abbrev == NULL) ? "" : state_and_abbrev->abbrev_;

}


namespace {


typedef GNU_HASH_MAP< std::string, std::vector<std::vector<std::string> > > CitiesAndStates;
CitiesAndStates cities_and_states;


void LoadCitiesAndStates(const std::string &filename, const bool use_state_abbrevs = false)
{
	File input(filename, "r");
	if (unlikely(input.fail()))
		throw Exception("in LoadCitiesAndStates (TextUtil.cc): can't open \"" + filename + "\" for reading!");

	unsigned line_no(0);
	while (not input.eof()) {
		++line_no;
		std::string line;
		input.getline(&line);
		StringUtil::Trim(&line);
		if (unlikely(line.empty()))
			continue;

		std::vector<std::string> parts;
		StringUtil::SplitThenTrimWhite(line, ',', &parts);
		if (unlikely(parts.size() < 2))
			throw Exception("in LoadCitiesAndStates (TextUtil.cc): malformed line in \"" + filename + "\" (line #"
					+ StringUtil::ToString(line_no) + ") (1)!");

		std::vector<std::string> state_words;
		StringUtil::Split(parts.back(), ' ', &state_words);
		if (unlikely(state_words.empty()))
			throw Exception("in LoadCitiesAndStates (TextUtil.cc): malformed line in \"" + filename + "\" (line #"
					+ StringUtil::ToString(line_no) + ") (2)!");

		std::string state_abbrev;
		if (use_state_abbrevs)
			state_abbrev = GetStateOrPossessionAbbreviation(parts.back());

		for (std::vector<std::string>::const_iterator part(parts.begin()); part != parts.end() - 1; ++part) {
			std::vector<std::string> city_words;
			StringUtil::Split(*part, ' ', &city_words);
			if (unlikely(city_words.empty()))
				throw Exception("in LoadCitiesAndStates (TextUtil.cc): malformed line in \"" + filename + "\" (line #"
						+ StringUtil::ToString(line_no) + ") (3)!");

			std::vector<std::string> rest_of_city_comma_and_state;
			for (std::vector<std::string>::const_iterator city_word(city_words.begin() + 1); city_word != city_words.end(); ++city_word)
				rest_of_city_comma_and_state.push_back(*city_word);
			rest_of_city_comma_and_state.push_back(",");
			for (std::vector<std::string>::const_iterator state_word(state_words.begin()); state_word != state_words.end(); ++state_word)
				rest_of_city_comma_and_state.push_back(*state_word);

			std::vector<std::string> rest_of_city_comma_and_state2;
			if (not state_abbrev.empty()) {
				for (std::vector<std::string>::const_iterator city_word(city_words.begin() + 1); city_word != city_words.end();
				     ++city_word)
					rest_of_city_comma_and_state2.push_back(*city_word);
				rest_of_city_comma_and_state2.push_back(",");
				rest_of_city_comma_and_state2.push_back(state_abbrev);
			}

			CitiesAndStates::iterator city_and_state(cities_and_states.find(city_words.front()));
			if (city_and_state != cities_and_states.end()) {
				city_and_state->second.push_back(rest_of_city_comma_and_state);
				if (not rest_of_city_comma_and_state2.empty())
					city_and_state->second.push_back(rest_of_city_comma_and_state2);
			}
			else {
				std::vector< std::vector<std::string> > rests;
				rests.push_back(rest_of_city_comma_and_state);
				if (not rest_of_city_comma_and_state2.empty())
					rests.push_back(rest_of_city_comma_and_state2);
				cities_and_states.insert(std::pair< std::string, std::vector< std::vector<std::string> > >(city_words.front(), rests));
			}
		}
	}
}


bool cities_and_states_have_been_initialised(false);


void InitCitiesAndStates()
{
	LoadCitiesAndStates(SHARE_DIR "/names/world_cities");
	LoadCitiesAndStates(SHARE_DIR "/names/actual_unique_us_towns", /* use_state_abbrevs = */ true);
}


SimpleTokenStream::const_iterator GetCitiesAndStatesEnd(const SimpleTokenStream::const_iterator &start, const SimpleTokenStream::const_iterator &end,
							std::string * const matched_string)
{
	matched_string->clear();

	if (start == end or start->type_ != ST_ALPHA)
		return end;

	if (unlikely(not cities_and_states_have_been_initialised)) {
		InitCitiesAndStates();
		cities_and_states_have_been_initialised = true;
	}

	CitiesAndStates::const_iterator city_and_rests(cities_and_states.find(start->value_));
	if (city_and_rests == cities_and_states.end())
		return end;

	for (std::vector< std::vector<std::string> >::const_iterator rest(city_and_rests->second.begin()); rest != city_and_rests->second.end(); ++rest) {
		std::string city_and_state_candidate(start->value_);
		SimpleTokenStream::const_iterator simple_token(start + 1);
		std::vector<std::string>::const_iterator chunk(rest->begin());
		for (/* Empty! */; chunk != rest->end(); ++simple_token) {
			if (simple_token == end or simple_token->type_ == ST_OTHER or simple_token->type_ == ST_INTEGER)
				break;
			if (simple_token->type_ == ST_WHITESPACE)
				continue;
			if (*chunk == "," and (simple_token->type_ != ST_NON_ALNUM_CHAR or simple_token->value_ != ","))
				break;

			if (simple_token->value_ != *chunk)
				break;

			// If we make it here we have another match:
			if (simple_token->value_ == ",")
				city_and_state_candidate += ',';
			else
				city_and_state_candidate += " " + *chunk;

			++chunk;

			// Complete match?
			if (chunk == rest->end()) {
				*matched_string = city_and_state_candidate;
				return simple_token + 1;
			}
		}
	}

	return end;
}


} // unnamed namespace


namespace {


inline bool TokenTypeIsWord(const TokenType token_type)
{
	return token_type == TT_COMMON_ENGLISH_WORD or token_type == TT_LOWERCASE_WORD or token_type == TT_INITIAL_CAPS_WORD
	       or token_type == TT_UPPERCASE_WORD or token_type == TT_INSTITUTION_INDICATOR or token_type == TT_PREPOSITION
	       or token_type == TT_STATE_OR_POSSESSION or token_type == TT_ABSTRACT;
}


inline void ClassifyChunk(std::string chunk, std::vector<StringAndTokenType> * const strings_and_tokens)
{
	const TokenType token_type(GetTokenType(chunk));
	strings_and_tokens->push_back(StringAndTokenType(chunk, token_type));
}


// BacktrackEmailAddress -- attemps to backwards extend "email_address" through the token stream represented by "strings_and_tokens."
//
void BacktrackEmailAddress(std::vector<StringAndTokenType> * const strings_and_tokens, std::string * const email_address)
{
	if (unlikely(strings_and_tokens->empty()))
		return;

	std::string possible_email_address(*email_address);
	std::vector<StringAndTokenType>::reverse_iterator string_and_token(strings_and_tokens->rbegin());
	while (string_and_token != strings_and_tokens->rend()) {
		possible_email_address = string_and_token->string_ + possible_email_address;
		if (IsValidEmailAddress(possible_email_address)) {
			++string_and_token;
			*email_address = possible_email_address;
			strings_and_tokens->pop_back();
		}
		else
			return;
	}
}


bool OnlyContainsAlnumUnderscoreOrHyphen(const std::string &s)
{
	if (s.empty())
		return false;

	for (std::string::const_iterator ch(s.begin()); ch != s.end(); ++ch) {
		if (not isalnum(*ch) and *ch != '_' and *ch != '-')
			return false;
	}

	return true;
}


inline std::string GetValueOrEmpty(const SimpleTokenStream::const_iterator &simple_token, ptrdiff_t offset, const SimpleTokenStream::const_iterator &end)
{
	// Gone too far?
	if (end - simple_token - offset < 1)
		return "";

	return (simple_token + offset)->value_;
}


SimpleTokenStream::const_iterator GetStateOrPossessionEnd(const SimpleTokenStream::const_iterator &start, const SimpleTokenStream::const_iterator &end)
{
	std::string candidate_words[5];
	candidate_words[0] = start->value_;

	SimpleTokenStream::const_iterator simple_token(start + 1);
	for (unsigned word_index(1); simple_token != end and word_index < 5; ++word_index) {
		if (not simple_token->type_ == ST_WHITESPACE)
			break;
		++simple_token;
		if (simple_token == end or simple_token->type_ != ST_ALPHA)
			break;
		candidate_words[word_index] = simple_token->value_;
		++simple_token;
	}

	unsigned matched_word_count(GetStateOrPossessionWordCount(candidate_words[0], candidate_words[1], candidate_words[2], candidate_words[3],
								  candidate_words[4]));
	if (matched_word_count == 0)
		return end;

	SimpleTokenStream::const_iterator last_word_of_a_state_or_possession(start);
	for (--matched_word_count; matched_word_count > 0; --matched_word_count)
		last_word_of_a_state_or_possession += 2;

	return last_word_of_a_state_or_possession;
}


} // unnamed namespace


void GetTokens(const std::string &text, std::vector<StringAndTokenType> * const strings_and_tokens)
{
	strings_and_tokens->clear();

	SimpleTokenStream simple_token_stream;
	CreateSimpleTokenStream(text, &simple_token_stream);

	for (SimpleTokenStream::const_iterator simple_token(simple_token_stream.begin()); simple_token != simple_token_stream.end(); ++simple_token) {
		if (simple_token->type_ == ST_WHITESPACE)
			strings_and_tokens->push_back(StringAndTokenType(simple_token->value_,
									 (simple_token->value_.find_first_of("\n\r") != std::string::npos)
									 ? TT_LINEEND : TT_WHITESPACE));
		else if (simple_token->type_ == ST_INTEGER and IsPossibleUsZipCode(simple_token->value_)) {
			std::string zip_candidate(simple_token->value_);

			// Check for ZIP+4:
			if ((simple_token + 1) != simple_token_stream.end() and (simple_token + 2) != simple_token_stream.end()
			    and (simple_token + 1)->type_ == ST_NON_ALNUM_CHAR and (simple_token + 1)->value_[0] == '-' and
			    (simple_token + 2)->type_ == ST_INTEGER and (simple_token + 2)->value_.length() == 4) // Likely to be ZIP+4!
			{
				++simple_token;
				zip_candidate += simple_token->value_;
				++simple_token;
				zip_candidate += simple_token->value_;
			}

			strings_and_tokens->push_back(StringAndTokenType(zip_candidate, TT_POSSIBLE_US_ZIP_CODE));
		}
		else if (simple_token->type_ == ST_ALPHA) {
			if (::strcasecmp("abstract", simple_token->value_.c_str()) == 0) {
				strings_and_tokens->push_back(StringAndTokenType(simple_token->value_, TT_ABSTRACT));
				continue;
			}

			// Test for a possible middle initial:
			if (simple_token->value_.length() == 1 and isupper(simple_token->value_[0])
			    and simple_token + 1 != simple_token_stream.end() and (simple_token + 1)->value_[0] == '.')
			{
				strings_and_tokens->push_back(StringAndTokenType(simple_token->value_ + ".", TT_CAPS_CHAR_AND_PERIOD));
				++simple_token;
				continue;
			}

			std::string conjunction;
			SimpleTokenStream::const_iterator match(GetConjunctionEnd(simple_token, simple_token_stream.end(), &conjunction));
			if (match != simple_token_stream.end()) {
				strings_and_tokens->push_back(StringAndTokenType(conjunction, isupper(simple_token->value_[0]) ? TT_CAPS_CONJUNCTION
										                                               : TT_CONJUNCTION));
				simple_token = match;
				continue;
			}

			std::string preposition;
			match = GetPrepositionEnd(simple_token, simple_token_stream.end(), &preposition);
			if (match != simple_token_stream.end()) {
				strings_and_tokens->push_back(StringAndTokenType(preposition, isupper(simple_token->value_[0]) ? TT_CAPS_PREPOSITION
										                                               : TT_PREPOSITION));
				simple_token = match;
				continue;
			}

			std::string city_and_state;
			match = GetCitiesAndStatesEnd(simple_token, simple_token_stream.end(), &city_and_state);
			if (match != simple_token_stream.end()) {
				strings_and_tokens->push_back(StringAndTokenType(city_and_state, TT_CITY_AND_STATE));
				simple_token = match;
				continue;
			}

			std::string pronoun;
			match = GetPronounEnd(simple_token, simple_token_stream.end(), &pronoun);
			if (match != simple_token_stream.end()) {
				strings_and_tokens->push_back(StringAndTokenType(pronoun, isupper(simple_token->value_[0]) ? TT_CAPS_PRONOUN
										                                           : TT_PRONOUN));
				simple_token = match;
				continue;
			}

			if (simple_token->value_ == "one" or simple_token->value_ == "One") {
				strings_and_tokens->push_back(StringAndTokenType(simple_token->value_,
										 isupper(simple_token->value_[0]) ? TT_INITIAL_CAPS_ONE : TT_ONE));
				continue;
			}

			// Test for a state or possession:
			const SimpleTokenStream::const_iterator state_or_possession_end(
				GetStateOrPossessionEnd(simple_token, simple_token_stream.end()));
			if (state_or_possession_end != simple_token_stream.end()) {
				std::string state_or_possession(simple_token->value_);
				for (++simple_token; simple_token < state_or_possession_end; ++simple_token) {
					++simple_token; // To skip over whitespace.
					state_or_possession += ' ';
					state_or_possession += simple_token->value_;
				}
				--simple_token;
				strings_and_tokens->push_back(StringAndTokenType(state_or_possession, TT_STATE_OR_POSSESSION));
				continue;
			}
			else { // Rely on ClassifyChunk() to tell us what's what.
				ClassifyChunk(simple_token->value_, strings_and_tokens);
				if (strings_and_tokens->back().token_type_ == TT_INITIAL_CAPS_WORD
				    or strings_and_tokens->back().token_type_ == TT_FIRST_NAME or strings_and_tokens->back().token_type_ == TT_LAST_NAME)
				{
					// Look ahead for a hypen + another word:
					if (simple_token_stream.end() - simple_token > 2 and (simple_token + 1)->type_ == ST_NON_ALNUM_CHAR
					    and (simple_token + 1)->value_ == "-" and (simple_token + 2)->type_ == ST_ALPHA)
					{
						strings_and_tokens->back().string_ += '-';
						strings_and_tokens->back().string_ += (simple_token + 2)->value_;
						simple_token += 2;
					}
				}
				else if (strings_and_tokens->back().token_type_ == TT_FIRST_NAME_OR_REGULAR_WORD) { // Attempt to disambiguate.
					if (simple_token_stream.end() - simple_token > 2 and (simple_token + 1)->type_ == ST_WHITESPACE) {
						const TokenType token_type(GetTokenType((simple_token + 2)->value_));
						if (token_type == TT_LAST_NAME or token_type == TT_FIRST_NAME)
							strings_and_tokens->back().token_type_ = TT_FIRST_NAME;
						else
							strings_and_tokens->back().token_type_ = TT_COMMON_ENGLISH_WORD;
					}
				}
			}
		}
		else if (simple_token->type_ == ST_NON_ALNUM_CHAR) {
			switch (simple_token->value_[0]) {
			case '.':
				strings_and_tokens->push_back(StringAndTokenType(".", TT_PERIOD));
				break;
			case '!':
				strings_and_tokens->push_back(StringAndTokenType("!", TT_EXCLAMATION_POINT));
				break;
			case '?':
				strings_and_tokens->push_back(StringAndTokenType("?", TT_QUESTION_MARK));
				break;
			case ',':
				strings_and_tokens->push_back(StringAndTokenType(",", TT_COMMA));
				break;
			case ';':
				strings_and_tokens->push_back(StringAndTokenType(";", TT_SEMICOLON));
				break;
			case '(':
				strings_and_tokens->push_back(StringAndTokenType("(", TT_OPEN_PAREN));
				break;
			case ')':
				strings_and_tokens->push_back(StringAndTokenType(")", TT_CLOSE_PAREN));
				break;
			case '@': { // May be part of an email address:
				if (strings_and_tokens->empty()
				    or not OnlyContainsAlnumUnderscoreOrHyphen(strings_and_tokens->back().string_)
				    or simple_token + 1 == simple_token_stream.end()
				    or not StringUtil::IsAlphanumeric((simple_token + 1)->value_))
					strings_and_tokens->push_back(StringAndTokenType("@", TT_MISCELLANEOUS));
				else { // We still have a chance that we may have seen an email address.
					std::string possible_email_address(strings_and_tokens->back().string_);
					possible_email_address += '@';
					SimpleTokenStream::const_iterator saved(simple_token);
					for (++simple_token; simple_token != simple_token_stream.end()
					     and (simple_token->value_ == "." or simple_token->value_ == "-"
						  or StringUtil::IsAlphanumeric(simple_token->value_)); ++simple_token)
						possible_email_address += simple_token->value_;
					--simple_token;

					// Strip off a trailing period, if any:
					if (possible_email_address[possible_email_address.length() - 1] == '.') {
						possible_email_address.resize(possible_email_address.length() - 1);
						--simple_token;
					}

					if (IsValidEmailAddress(possible_email_address)) {
						strings_and_tokens->pop_back();
						BacktrackEmailAddress(strings_and_tokens, &possible_email_address);
						strings_and_tokens->push_back(StringAndTokenType(possible_email_address, TT_EMAIL_ADDRESS));
					}
					else {
						simple_token = saved;
						strings_and_tokens->push_back(StringAndTokenType("@", TT_MISCELLANEOUS));
					}
				}
				break;
			}
			default:
				strings_and_tokens->push_back(StringAndTokenType(simple_token->value_, TT_MISCELLANEOUS));
			}
		}
		else
			ClassifyChunk(simple_token->value_, strings_and_tokens);
	}
}


const uint32_t StringAndComboTokenType::DO_NOT_CARE(0xFFFFu);


std::string StringAndComboTokenType::toString(const bool tokens_only) const
{
	std::string string_and_token_combo_as_string(ComboTokenTypeToString(combo_token_));
	if (tokens_only)
		return string_and_token_combo_as_string;

	string_and_token_combo_as_string += ": ";

	// Now add the string:
	string_and_token_combo_as_string += '"';
	string_and_token_combo_as_string += StringUtil::CStyleEscape(string_);
	string_and_token_combo_as_string += '"';

	return string_and_token_combo_as_string;
}


std::string ComboTokenTypeToString(const uint32_t combo_token)
{
	std::string combo_token_as_string;

	// Get the first token:
	TokenType token_type(static_cast<TokenType>(combo_token >> 16u));
	if (token_type == StringAndComboTokenType::DO_NOT_CARE)
		combo_token_as_string = "DO_NOT_CARE";
	else
		combo_token_as_string = TokenTypeToString(token_type);
	combo_token_as_string += '/';

	// Get the second token:
	token_type = static_cast<TokenType>(combo_token & 0xFFFFu);
	if (token_type == StringAndComboTokenType::DO_NOT_CARE)
                combo_token_as_string += "DO_NOT_CARE";
        else
                combo_token_as_string += TokenTypeToString(token_type);

	return combo_token_as_string;
}


std::string ComboTokenModeToString(const ComboTokenMode mode)
{
	switch (mode) {
	case USE_ALL_TOKENS:
		return "USE_ALL_TOKENS";
	case DO_NOT_USE_WHITESPACE:
		return "DO_NOT_USE_WHITESPACE";
	case ONLY_USE_LINEEND_WHITESPACE:
		return "ONLY_USE_LINEEND_WHITESPACE";
	default:
		throw Exception("in ComboTokenModeToString: unknown mode \"" + StringUtil::ToString(mode) + "\"!");
	}
}


bool ComboTokenEnumerator::getNextToken(uint32_t * const token)
{
	if (unlikely(last_token_ == ITERATOR_END_TOKEN))
		return false;

	*token = last_token_;

	uint32_t first_token(last_token_ >> 16u);
	uint32_t second_token(last_token_ & 0xFFFFu);

	// Can't we advance the 2nd token?
	if (unlikely(second_token == StringAndComboTokenType::DO_NOT_CARE)) {
		++first_token;
		if (mode_ == DO_NOT_USE_WHITESPACE and (first_token == TT_WHITESPACE or first_token == TT_LINEEND))
			++first_token;
		if (mode_ == ONLY_USE_LINEEND_WHITESPACE and first_token == TT_WHITESPACE)
			++first_token;
		if (unlikely(first_token == TT_CARDINALITY))
			last_token_ = ITERATOR_END_TOKEN;
		else
			last_token_ = first_token << 16u; // 2nd token is implicitly 0.
	}
	else if (likely(second_token < TT_CARDINALITY - 1)) {
		++second_token;
		if (mode_ == DO_NOT_USE_WHITESPACE and (second_token == TT_WHITESPACE or second_token == TT_LINEEND))
			++second_token;
		if (mode_ == ONLY_USE_LINEEND_WHITESPACE and second_token == TT_WHITESPACE)
			++second_token;
		last_token_ = MakeComboToken(first_token, second_token);
	}
	else // second_token == TT_CARDINALITY - 1
		last_token_ = MakeComboToken(first_token, StringAndComboTokenType::DO_NOT_CARE);

	return true;
}


void GetComboTokens(const std::string &text, std::vector<StringAndComboTokenType> * const strings_and_combo_tokens, const ComboTokenMode mode)
{
	strings_and_combo_tokens->clear();

	// Get singleton tokens first:
	std::vector<StringAndTokenType> strings_and_token_types;
	GetTokens(text, &strings_and_token_types);

	// Now combine the singletons into combo tokens:
	for (std::vector<StringAndTokenType>::const_iterator string_and_token(strings_and_token_types.begin());
	     string_and_token != strings_and_token_types.end(); ++string_and_token)
	{
		if (mode == DO_NOT_USE_WHITESPACE and (string_and_token->token_type_ == TT_WHITESPACE or string_and_token->token_type_ == TT_LINEEND))
			continue;
		if (mode == ONLY_USE_LINEEND_WHITESPACE and string_and_token->token_type_ == TT_WHITESPACE)
			continue;

		const std::vector<StringAndTokenType>::const_iterator current_string_and_token(string_and_token);

		// Make sure the next token is acceptable:
		while (likely(string_and_token + 1 != strings_and_token_types.end())) {
			if (mode == USE_ALL_TOKENS) // All tokens are acceptable.
				break;
			if (mode == DO_NOT_USE_WHITESPACE
			    and ((string_and_token + 1)->token_type_ != TT_WHITESPACE and (string_and_token + 1)->token_type_ != TT_LINEEND))
				break;
			if (mode == ONLY_USE_LINEEND_WHITESPACE and (string_and_token + 1)->token_type_ != TT_WHITESPACE)
				break;
			++string_and_token;
		}

		// Special case the end.  Here there can be no meaningful 2nd token:
		if (unlikely(string_and_token + 1 == strings_and_token_types.end()))
			strings_and_combo_tokens->push_back(StringAndComboTokenType(current_string_and_token->string_,
										    current_string_and_token->token_type_,
										    static_cast<TokenType>(StringAndComboTokenType::DO_NOT_CARE)));
		else
			strings_and_combo_tokens->push_back(StringAndComboTokenType(current_string_and_token->string_,
										    current_string_and_token->token_type_,
										    (string_and_token + 1)->token_type_));
	}
}


void GetTextChunks(const std::string &text, std::vector<std::string> * const chunks, const bool lowercase)
{
	chunks->clear();

	std::string current_chunk;
	for (std::string::const_iterator ch(text.begin()); ch != text.end(); ++ch) {
		if (StringUtil::IsLatin9Whitespace(*ch)) {
			if (not current_chunk.empty()) {
				chunks->push_back(lowercase ? StringUtil::ToLower(current_chunk) : current_chunk);
				current_chunk.clear();
			}
		}
		else if (ispunct(*ch)) {
			chunks->push_back(std::string(1, *ch));
			if (not current_chunk.empty()) {
				chunks->push_back(lowercase ? StringUtil::ToLower(current_chunk) : current_chunk);
				current_chunk.clear();
			}
		}
		else
			current_chunk += *ch;
	}
}


namespace {


struct NameAndFrequency {
	std::string name_;
	double frequency_;
public:
	NameAndFrequency(const std::string &name, const double frequency): name_(name), frequency_(frequency) { }
	bool operator==(const NameAndFrequency &rhs) const { return name_ == rhs.name_; }
};


class NameAndFrequencyHash {
public:
	size_t operator()(const NameAndFrequency &name_and_frequency) const
		{
			return GNU_HASH<std::string>()(name_and_frequency.name_);
		}
};


void LoadNamesFromCensusStatsFile(const std::string &name_stats_filename, GNU_HASH_SET<NameAndFrequency, NameAndFrequencyHash> * const names)
{
	File name_stats(name_stats_filename, "r");
	if (unlikely(name_stats.fail()))
		throw Exception("in LoadNamesFromCensusStatsFile: can't open \"" + name_stats_filename + "\" for reading!");
	while (not name_stats.eof()) {
		std::string line;
		if (name_stats.getline(&line) == 0)
			break;
		std::vector<std::string> chunks;
		StringUtil::Split(line, ' ', &chunks);
		double frequency;
		if (unlikely(chunks.size() < 2) or not StringUtil::ToDouble(chunks[1], &frequency))
			throw Exception("in LoadNamesFromCensusStatsFile: can't parse line from \"" + name_stats_filename + "\" ("
					+ line + ")!");
		names->insert(NameAndFrequency(StringUtil::CapitalizeWord(chunks[0]), frequency));
	}
	if (unlikely(names->empty()))
		throw Exception("in LoadNamesFromCensusStatsFile: found no first names in \"" + name_stats_filename + "\"!");
}


} // unnamed namespace


bool IsCommonAmericanMaleFirstName(const std::string &first_name_candidate, double * const frequency, const bool casefold)
{
	std::string name_candidate(first_name_candidate);
	if (casefold)
		StringUtil::CapitalizeWord(&name_candidate);

	static bool initialised(false);
	static GNU_HASH_SET<NameAndFrequency, NameAndFrequencyHash> first_names;
	if (unlikely(not initialised)) {
		LoadNamesFromCensusStatsFile(SHARE_DIR "/names/dist.male.first", &first_names);
		initialised = true;
	}

	const NameAndFrequency test_name_and_frequency(name_candidate, 0.0);
	GNU_HASH_SET<NameAndFrequency, NameAndFrequencyHash>::const_iterator name_and_frequency(first_names.find(test_name_and_frequency));
	if (name_and_frequency != first_names.end()) {
		if (frequency != NULL)
			*frequency = name_and_frequency->frequency_ / 100.0;
		return true;
	}

	if (frequency != NULL)
		*frequency = 0.0;
	return false;
}


bool IsCommonAmericanFemaleFirstName(const std::string &first_name_candidate, double * const frequency, const bool casefold)
{
	std::string name_candidate(first_name_candidate);
	if (casefold)
		StringUtil::CapitalizeWord(&name_candidate);

	static bool initialised(false);
	static GNU_HASH_SET<NameAndFrequency, NameAndFrequencyHash> first_names;
	if (unlikely(not initialised)) {
		LoadNamesFromCensusStatsFile(SHARE_DIR "/names/dist.female.first", &first_names);
		initialised = true;
	}

	const NameAndFrequency test_name_and_frequency(name_candidate, 0.0);
	GNU_HASH_SET<NameAndFrequency, NameAndFrequencyHash>::const_iterator name_and_frequency(first_names.find(test_name_and_frequency));
	if (name_and_frequency != first_names.end()) {
		if (frequency != NULL)
			*frequency = name_and_frequency->frequency_ / 100.0;
		return true;
	}

	if (frequency != NULL)
		*frequency = 0.0;
	return false;
}


bool IsCommonAmericanSurname(const std::string &surname_candidate, double * const frequency, const bool casefold)
{
	std::string name_candidate(surname_candidate);
	if (casefold)
		StringUtil::CapitalizeWord(&name_candidate);

	static bool initialised(false);
	static GNU_HASH_SET<NameAndFrequency, NameAndFrequencyHash> surnames;
	if (unlikely(not initialised)) {
		LoadNamesFromCensusStatsFile(SHARE_DIR "/names/dist.all.last", &surnames);
		initialised = true;
	}

	const NameAndFrequency test_name_and_frequency(name_candidate, 0.0);
	GNU_HASH_SET<NameAndFrequency, NameAndFrequencyHash>::const_iterator name_and_frequency(surnames.find(test_name_and_frequency));
	if (name_and_frequency != surnames.end()) {
		if (frequency != NULL)
			*frequency = name_and_frequency->frequency_ / 100.0;
		return true;
	}

	if (frequency != NULL)
		*frequency = 0.0;
	return false;
}


bool IsCommonFrenchMaleFirstName(const std::string &first_name_candidate, const bool casefold)
{
	std::string name_candidate(first_name_candidate);
	if (casefold)
		StringUtil::CapitalizeWord(&name_candidate);

	static bool initialised(false);
	static std::set<std::string> first_names;
	if (unlikely(not initialised)) {
		LoadWordsFromFile("/names/French.male.first", &first_names);
		initialised = true;
	}

	return first_names.find(name_candidate) != first_names.end();
}


bool IsCommonFrenchFemaleFirstName(const std::string &first_name_candidate, const bool casefold)
{
	std::string name_candidate(first_name_candidate);
	if (casefold)
		StringUtil::CapitalizeWord(&name_candidate);

	static bool initialised(false);
	static std::set<std::string> first_names;
	if (unlikely(not initialised)) {
		LoadWordsFromFile("/names/French.female.first", &first_names);
		initialised = true;
	}

	return first_names.find(name_candidate) != first_names.end();
}


bool IsCommonFrenchSurname(const std::string &/*surname_candidate*/, const bool /*casefold*/)
{
	return false; // Stub, for now.
}


bool IsCommonChineseSurname(const std::string &surname_candidate, const bool casefold)
{
	std::string name_candidate(surname_candidate);
	if (casefold)
		StringUtil::CapitalizeWord(&name_candidate);

	static bool initialised(false);
	static std::set<std::string> surnames;
	if (unlikely(not initialised)) {
		LoadWordsFromFile("/names/Chinese.last", &surnames);
		initialised = true;
	}

	return surnames.find(name_candidate) != surnames.end();
}


bool SimpleTokenStream::equal(const SimpleTokenStream &rhs) const
{
	if (rhs.size() != this->size())
		return false;

	SimpleTokenStream::const_iterator lhs_iter(this->begin());
	SimpleTokenStream::const_iterator rhs_iter(rhs.begin());
	for (/* Empty. */; lhs_iter != this->end(); ++lhs_iter, ++rhs_iter) {
		if (lhs_iter->type_ != rhs_iter->type_)
			return false;
		if (lhs_iter->type_ != ST_WHITESPACE or lhs_iter->value_ != rhs_iter->value_)
			return false;
	}

	return true;
}


namespace {


// ClassifyAlnumText -- helper function for CreateSimpleTokenStream().  Can only process empty and alphanumeric chunks.  If "*chunk" is non-
//                      empty the correct token will be appended to "simple_token_stream."
//
void ClassifyAlnumText(std::string * const chunk, SimpleTokenStream * const simple_token_stream)
{
	if (chunk->empty())
		return;

	std::string::const_iterator ch(chunk->begin());
	SimpleTokenType type;
	if (isalpha(*ch))
		type = ST_ALPHA;
	else if (isdigit(*ch))
		type = ST_INTEGER;
	else
		throw Exception("in ClassifyAlnumText: can only handle letters and numbers (1)!");

	for (++ch; ch != chunk->end(); ++ch) {
		if (isalpha(*ch)) {
			if (type != ST_OTHER and type != ST_ALPHA)
				type = ST_OTHER;
		}
		else if (isdigit(*ch)) {
			if (type != ST_OTHER and type != ST_INTEGER)
				type = ST_OTHER;
		}
		else
			throw Exception("in ClassifyAlnumText: can only handle letters and numbers (2)!");
	}

	// Try to separate accidentally concatenated words:
	const PerlCompatRegExp multi_word_pattern("^([[:upper:]][[:lower:]]+){2,}$");
	if (type == ST_ALPHA and multi_word_pattern.match(*chunk) and not ((*chunk)[0] == 'M' and (*chunk)[1] == 'c')
	    and not ((*chunk)[0] == 'M' and (*chunk)[1] == 'a' and (*chunk)[2] == 'c'))
	{
		// Split into word candidates:
		std::string word;
		std::string::const_iterator letter(chunk->begin());
		word += *letter++;
		while (letter != chunk->end()) {
			if (isupper(*letter)) {
				simple_token_stream->push_back(SimpleToken(word, type));
				word.clear();
				word += *letter++;
			}
			else
				word += *letter++;
		}
		simple_token_stream->push_back(SimpleToken(word, type));
	}
	else
		simple_token_stream->push_back(SimpleToken(*chunk, type));

	chunk->clear();
}


} // unnamed namespace


void CreateSimpleTokenStream(const std::string &text, SimpleTokenStream * const simple_token_stream, const bool normalize_whitespace,
			     const unsigned max_stream_size)
{
	simple_token_stream->clear();
	for (std::string::const_iterator ch(text.begin()); ch != text.end() and simple_token_stream->size() < max_stream_size; /* Empty. */) {
		if (StringUtil::IsLatin9Whitespace(*ch)) {
			std::string whitespace(1, *ch);
			for (++ch; ch != text.end() and StringUtil::IsLatin9Whitespace(*ch); ++ch)
				whitespace += *ch;
			if (normalize_whitespace) {
				// Replace any whitespace that has a lineend with a single newline or if there are no lineends, with a single space:
				if (whitespace.find('\n') != std::string::npos or whitespace.find('\r') != std::string::npos)
					whitespace = '\n';
				else
					whitespace = ' ';
			}
			simple_token_stream->push_back(SimpleToken(whitespace, ST_WHITESPACE));
			whitespace.clear();
		}
		else if (isalnum(*ch)) {
			std::string alnum_text(1, *ch);
			for (++ch; ch != text.end() and isalnum(*ch); ++ch)
				alnum_text += *ch;
			ClassifyAlnumText(&alnum_text, simple_token_stream);
		}
		else { // Assume we have punctuation.
			simple_token_stream->push_back(SimpleToken(std::string(1, *ch), ST_NON_ALNUM_CHAR));
			++ch;
		}
	}
}


std::string SimpleTokenTypeToString(const SimpleTokenType simple_token_type)
{
	switch (simple_token_type) {
	case ST_WHITESPACE:
		return "WHITESPACE";
	case ST_NON_ALNUM_CHAR:
		return "NON_ALNUM_CHAR";
	case ST_ALPHA:
		return "ALPHA";
	case ST_INTEGER:
		return "INTEGER";
	case ST_OTHER:
		return "OTHER";
	default:
		throw Exception("in TextUtil::SimpleTokenTypeToString: unknown simple token type " + StringUtil::ToString(simple_token_type) + "!");
	}
}


std::string DocumentPartToString(const DocumentPart document_part)
{
	switch (document_part) {
	case DP_START:
		return "DP_START";
	case DP_INSTITUTION:
		return "DP_INSTITUTION";
	case DP_TITLE:
		return "DP_TITLE";
	case DP_AUTHORS:
		return "DP_AUTHORS";
	case DP_EMAIL:
		return "DP_EMAIL";
	case DP_KEYWORDS:
		return "DP_KEYWORDS";
	case DP_DATE:
		return "DP_DATE";
	case DP_ADDRESS:
		return "DP_ADDRESS";
	case DP_ABSTRACT:
		return "DP_ABSTRACT";
	case DP_OTHER:
		return "DP_OTHER";
	case DP_END:
		return "DP_END";
	default:
		throw Exception("in TextUtil::DocumentPartToString: unknown document part " + StringUtil::ToString(document_part) + "!");
	}
}


std::string StringAndDocumentPart::toString() const
{
	return "\"" + StringUtil::CStyleEscape(string_) + "\"<" + DocumentPartToString(document_part_) + ">";
}


namespace {


DocumentPart StringToDocumentPart(const std::string &document_part_as_string)
{
	if (document_part_as_string == "DP_START")
		return DP_START;
	else if (document_part_as_string == "DP_INSTITUTION")
		return DP_INSTITUTION;
	else if (document_part_as_string == "DP_TITLE")
		return DP_TITLE;
	else if (document_part_as_string == "DP_AUTHORS")
		return DP_AUTHORS;
	else if (document_part_as_string == "DP_EMAIL")
		return DP_EMAIL;
	else if (document_part_as_string == "DP_KEYWORDS")
		return DP_KEYWORDS;
	else if (document_part_as_string == "DP_DATE")
		return DP_DATE;
	else if (document_part_as_string == "DP_ADDRESS")
		return DP_ADDRESS;
	else if (document_part_as_string == "DP_ABSTRACT")
		return DP_ABSTRACT;
	else if (document_part_as_string == "DP_OTHER")
		return DP_OTHER;
	else if (document_part_as_string == "DP_END")
		return DP_END;
	else
		throw Exception("in StringToDocumentPart() (in TextUtil.cc): unknown document part \"" + document_part_as_string + "\"!");
}


TokenType StringToTokenType(const std::string &token_type_as_string)
{
	if (token_type_as_string == "TT_LOWERCASE_WORD")
		return TT_LOWERCASE_WORD;
	else if (token_type_as_string == "TT_UPPERCASE_WORD")
		return TT_UPPERCASE_WORD;
	else if (token_type_as_string == "TT_INITIAL_CAPS_WORD")
		return TT_INITIAL_CAPS_WORD;
	else if (token_type_as_string == "TT_CAPS_CHAR_AND_PERIOD")
		return TT_CAPS_CHAR_AND_PERIOD;
	else if (token_type_as_string == "TT_INTEGER")
		return TT_INTEGER;
	else if (token_type_as_string == "TT_COMMA")
		return TT_COMMA;
	else if (token_type_as_string == "TT_SEMICOLON")
		return TT_SEMICOLON;
	else if (token_type_as_string == "TT_PERIOD")
		return TT_PERIOD;
	else if (token_type_as_string == "TT_QUESTION_MARK")
		return TT_QUESTION_MARK;
	else if (token_type_as_string == "TT_EXCLAMATION_POINT")
		return TT_EXCLAMATION_POINT;
	else if (token_type_as_string == "TT_OPEN_PAREN")
		return TT_OPEN_PAREN;
	else if (token_type_as_string == "TT_CLOSE_PAREN")
		return TT_CLOSE_PAREN;
	else if (token_type_as_string == "TT_EMAIL_ADDRESS")
		return TT_EMAIL_ADDRESS;
	else if (token_type_as_string == "TT_INSTITUTION_INDICATOR")
		return TT_INSTITUTION_INDICATOR;
	else if (token_type_as_string == "TT_COMMON_ENGLISH_WORD")
		return TT_COMMON_ENGLISH_WORD;
	else if (token_type_as_string == "TT_WHITESPACE")
		return TT_WHITESPACE;
	else if (token_type_as_string == "TT_LINEEND")
		return TT_LINEEND;
	else if (token_type_as_string == "TT_ORDINAL")
		return TT_ORDINAL;
	else if (token_type_as_string == "TT_PREPOSITION")
		return TT_PREPOSITION;
	else if (token_type_as_string == "TT_CAPS_PREPOSITION")
		return TT_CAPS_PREPOSITION;
	else if (token_type_as_string == "TT_POSSIBLE_RECENT_YEAR")
		return TT_POSSIBLE_RECENT_YEAR;
	else if (token_type_as_string == "TT_FIRST_NAME")
		return TT_FIRST_NAME;
	else if (token_type_as_string == "TT_LAST_NAME")
		return TT_LAST_NAME;
	else if (token_type_as_string == "TT_FIRST_NAME_OR_REGULAR_WORD")
		return TT_FIRST_NAME_OR_REGULAR_WORD;
	else if (token_type_as_string == "TT_LAST_NAME_OR_REGULAR_WORD")
		return TT_LAST_NAME_OR_REGULAR_WORD;
	else if (token_type_as_string == "TT_DETERMINER")
		return TT_DETERMINER;
	else if (token_type_as_string == "TT_INITIAL_CAPS_DETERMINER")
		return TT_INITIAL_CAPS_DETERMINER;
	else if (token_type_as_string == "TT_DETERMINER_OR_PRONOUN")
		return TT_DETERMINER_OR_PRONOUN;
	else if (token_type_as_string == "TT_INITIAL_CAPS_DETERMINER_OR_PRONOUN")
		return TT_INITIAL_CAPS_DETERMINER_OR_PRONOUN;
	else if (token_type_as_string == "TT_CONJUNCTION")
		return TT_CONJUNCTION;
	else if (token_type_as_string == "TT_CAPS_CONJUNCTION")
		return TT_CAPS_CONJUNCTION;
	else if (token_type_as_string == "TT_ONE")
		return TT_ONE;
	else if (token_type_as_string == "TT_INITIAL_CAPS_ONE")
		return TT_INITIAL_CAPS_ONE;
	else if (token_type_as_string == "TT_PRONOUN")
		return TT_PRONOUN;
	else if (token_type_as_string == "TT_CAPS_PRONOUN")
		return TT_CAPS_PRONOUN;
	else if (token_type_as_string == "TT_MISCELLANEOUS")
		return TT_MISCELLANEOUS;
	else if (token_type_as_string == "TT_ENGLISH_MONTH_OR_MONTH_ABBREV")
		return TT_ENGLISH_MONTH_OR_MONTH_ABBREV;
	else if (token_type_as_string == "TT_ENGLISH_DAY_OR_DAY_ABBREV")
		return TT_ENGLISH_DAY_OR_DAY_ABBREV;
	else if (token_type_as_string == "TT_STATE_OR_POSSESSION")
		return TT_STATE_OR_POSSESSION;
	else if (token_type_as_string == "TT_COMMA_AND_US_STATE_ABBREV")
		return TT_COMMA_AND_US_STATE_ABBREV;
	else if (token_type_as_string == "TT_ABSTRACT")
		return TT_ABSTRACT;
	else if (token_type_as_string == "TT_POSSIBLE_US_ZIP_CODE")
		return TT_POSSIBLE_US_ZIP_CODE;
	else
		throw Exception("in StringToTokenType() (in TextUtil.cc): unknown token type \"" + token_type_as_string + "\"!");
}


// InitHMM -- helper function for LabelDocumentParts().
//
void InitHMM(const std::string &serialized_hmm_filename, HMM ** const hmm, std::string * const unknown_token,
	     GNU_HASH_SET<std::string> * const known_tokens, StringToIndexMap * const observation_names_to_observation_indices,
	     std::map<unsigned, TextUtil::DocumentPart> * const state_index_to_document_part_map)
{
	File input(serialized_hmm_filename, "r");
	if (unlikely(input.fail()))
		throw Exception("in InitHMM() (in TextUtil.cc): can't open \"" + serialized_hmm_filename + "\" for reading!");

	if (unlikely(not BinaryIO::Read(input, unknown_token)))
		throw Exception("in InitHMM() (in TextUtil.cc): can't read unknown token from \"" + serialized_hmm_filename + "\"!");
	if (unlikely(not BinaryIO::Read(input, known_tokens)))
		throw Exception("in InitHMM() (in TextUtil.cc): can't read known tokens from \"" + serialized_hmm_filename + "\"!");
	*hmm = new HMM(input);
	const StringToIndexMap state_label_to_state_index_map(input);
	*observation_names_to_observation_indices = StringToIndexMap(input);

	for (StringToIndexMap::const_iterator state_label_and_index(state_label_to_state_index_map.begin());
	     state_label_and_index != state_label_to_state_index_map.end(); ++state_label_and_index)
		state_index_to_document_part_map->insert(
			std::make_pair<unsigned, TextUtil::DocumentPart>(state_label_and_index->second,
									 StringToDocumentPart(state_label_and_index->first)));
}


} // unnamed namespace


void LabelDocumentParts(const std::string &document, std::vector<StringAndDocumentPart> * const strings_and_document_parts,
			const unsigned max_token_stream_length)
{
	strings_and_document_parts->clear();

	static std::string unknown_token;
	static GNU_HASH_SET<std::string> known_tokens;
	static HMM *hmm(NULL);
	static StringToIndexMap observation_names_to_observation_indices(StringToIndexMap::DO_NOT_ALLOW_DUPS);
	static std::map<unsigned, DocumentPart> state_index_to_document_part_map;
	if (hmm == NULL)
		InitHMM(SHARE_DIR "/named_entity.hmm", &hmm, &unknown_token, &known_tokens, &observation_names_to_observation_indices,
			&state_index_to_document_part_map);

	std::vector<StringAndTokenType> strings_and_token_types;
	GetTokens(document, &strings_and_token_types);

	// Only keep a limited number of leading tokens if requested by the caller (0 means no limit):
	if (max_token_stream_length > 0 and strings_and_token_types.size() > max_token_stream_length)
		strings_and_token_types.resize(max_token_stream_length);

	HMM::Observations observations;
#if 0
	HMM::TokenizeText(document, known_tokens, unknown_token, observation_names_to_observation_indices, &observations);
#else
	for (std::vector<StringAndTokenType>::const_iterator string_and_token_type(strings_and_token_types.begin());
	     string_and_token_type != strings_and_token_types.end(); ++string_and_token_type)
		observations.push_back(observation_names_to_observation_indices[TokenTypeToString(string_and_token_type->token_type_)]);
#endif

	HMM::ViterbiPath viterbi_path;
	double path_probability;
	hmm->getViterbiPath(observations, &viterbi_path, &path_probability);

	// Cheat and replace DP_START with whatever tag follows:
	if (viterbi_path.size() > 1)
		viterbi_path[0] = viterbi_path[1];

	std::vector<StringAndTokenType>::const_iterator string_and_token_type(strings_and_token_types.begin());
	// Note that the number of states is one larger than the number of observations and therefore we need to subtract 1 from
	// viterbi_path.end() - 1 in the following loop:
	for (HMM::ViterbiPath::const_iterator state_index(viterbi_path.begin()); state_index != viterbi_path.end() - 1;
	     ++state_index, ++string_and_token_type)
		strings_and_document_parts->push_back(StringAndDocumentPart(string_and_token_type->string_,
									    state_index_to_document_part_map[*state_index]));
}


void LabelDocumentPartsUsingComboTokens(const std::string &document, std::vector<StringAndDocumentPart> * const strings_and_document_parts,
					const unsigned max_token_stream_length, const ComboTokenMode mode)
{
	strings_and_document_parts->clear();

	static std::string unknown_token;
	static GNU_HASH_SET<std::string> known_tokens;
	static HMM *hmm(NULL);
	static StringToIndexMap observation_names_to_observation_indices(StringToIndexMap::DO_NOT_ALLOW_DUPS);
	static std::map<unsigned, DocumentPart> state_index_to_document_part_map;
	if (hmm == NULL)
		InitHMM(SHARE_DIR "/named_entity_combo_token.hmm-" + ComboTokenModeToString(mode), &hmm, &unknown_token, &known_tokens,
			&observation_names_to_observation_indices, &state_index_to_document_part_map);

	std::vector<StringAndComboTokenType> strings_and_token_types;
	GetComboTokens(document, &strings_and_token_types, mode);

	// Only keep a limited number of leading tokens if requested by the caller (0 means no limit):
	if (max_token_stream_length > 0 and strings_and_token_types.size() > max_token_stream_length)
		strings_and_token_types.resize(max_token_stream_length);

	HMM::Observations observations;
#if 0
	HMM::TokenizeText(document, known_tokens, unknown_token, observation_names_to_observation_indices, &observations);
#else
	for (std::vector<StringAndComboTokenType>::const_iterator string_and_combo_token(strings_and_token_types.begin());
	     string_and_combo_token != strings_and_token_types.end(); ++string_and_combo_token)
		observations.push_back(observation_names_to_observation_indices[ComboTokenTypeToString(string_and_combo_token->combo_token_)]);
#endif

	HMM::ViterbiPath viterbi_path;
	double path_probability;
	hmm->getViterbiPath(observations, &viterbi_path, &path_probability);

	// Cheat and replace DP_START with whatever tag follows:
	if (viterbi_path.size() > 1)
		viterbi_path[0] = viterbi_path[1];

	std::vector<StringAndComboTokenType>::const_iterator string_and_combo_token(strings_and_token_types.begin());
	// Note that the number of states is one larger than the number of observations and therefore we need to subtract 1 from
	// viterbi_path.end() - 1 in the following loop:
	for (HMM::ViterbiPath::const_iterator state_index(viterbi_path.begin()); state_index != viterbi_path.end() - 1;
	     ++state_index, ++string_and_combo_token)
		strings_and_document_parts->push_back(StringAndDocumentPart(string_and_combo_token->string_,
									    state_index_to_document_part_map[*state_index]));
}


unsigned EstimateTextHeight(const std::string &text, const unsigned limit, const char delimiter)
{
	unsigned rows_required(0);
	unsigned current_line_width(0);
	for (std::string::const_iterator i(text.begin()); i != text.end(); ++i) {
		if (*i == delimiter or current_line_width > limit) {
			current_line_width = 0;
			++rows_required;
		}
		++current_line_width;
	}

	return rows_required;
}


namespace {


const char *common_words[] = {
	"'s",
	"a",
	"all",
	"an",
	"and",
	"are",
	"as",
	"at",
	"be",
	"been",
	"but",
	"by",
	"can",
	"for",
	"from",
	"had",
	"has",
	"have",
	"he",
	"her",
	"his",
	"i",
	"if",
	"in",
	"is",
	"it",
	"n't",
	"not",
	"of",
	"on",
	"or",
	"said",
	"she",
	"that",
	"the",
	"their",
	"there",
	"they",
	"this",
	"to",
	"was",
	"we",
	"were",
	"what",
	"which",
	"who",
	"will",
	"with",
	"would",
	"you",
};


inline bool IsCommonWord(const std::string &word)
{
	const char *key(word.c_str());
	return std::bsearch(&key, &common_words[0], DIM(common_words), sizeof(common_words[0]), WordCompare) != NULL;
}


inline void IncCommonWordCount(std::string * const common_word_candidate, GNU_HASH_MAP<std::string, unsigned> * const words_and_frequencies)
{
	if (IsCommonWord(*common_word_candidate))
		++(*words_and_frequencies)[*common_word_candidate];

	common_word_candidate->clear();
}


} // unnamed namespace


void CommonWordFrequencyCounter(const std::string &plain_text, GNU_HASH_MAP<std::string, unsigned> * const words_and_frequencies)
{
	words_and_frequencies->clear();

#if 1
	// Set the initial word counts to zero:
	for (size_t word_no(0); word_no < DIM(common_words); ++word_no)
		(*words_and_frequencies)[common_words[word_no]] = 0;
#endif

	std::string current_word;
	for (std::string::const_iterator ch(plain_text.begin()); ch != plain_text.end(); ++ch) {
		if (isalpha(*ch))
			current_word += std::tolower(*ch);
		else if (*ch == '\'') {
			if (not current_word.empty() and (ch + 1) != plain_text.end()) {
				if (current_word[current_word.size() - 1] == 'n' and (*(ch + 1) == 't' or *(ch + 1) == 'T')) {
					++(*words_and_frequencies)["n't"];
					if (current_word != "can")
						current_word.resize(current_word.size() - 1); // Trim of the trailing 'n'.
					IncCommonWordCount(&current_word, words_and_frequencies);
				}
				else if (*(ch + 1) == 's' or *(ch + 1) == 'S') {
					++(*words_and_frequencies)["'s"];
					IncCommonWordCount(&current_word, words_and_frequencies);
				}
			}

			if (not current_word.empty())
				IncCommonWordCount(&current_word, words_and_frequencies);
		}
		else if (not current_word.empty())
			IncCommonWordCount(&current_word, words_and_frequencies);
	}

	if (not current_word.empty())
		IncCommonWordCount(&current_word, words_and_frequencies);
}


double SentenceSimilarityScore(const std::string &first_sentence, const std::string &second_sentence)
{
	std::vector<std::string> words;
	ExtractWords(first_sentence, &words);
	std::string lc_second_sentence(StringUtil::ToLower(second_sentence));
	unsigned words_in_common(0);
	for (std::vector<std::string>::const_iterator word(words.begin()); word != words.end(); ++word) {
		if (PerlCompatRegExp::Match("\\b" + *word + "\\b", lc_second_sentence))
			words_in_common++;
	}

	unsigned first_length(CountWords(first_sentence));
	unsigned second_length(CountWords(second_sentence));
	unsigned single_word_sentence_correction_factor((first_length == 1 and second_length == 1) ? 1 : 0);
	return static_cast<double>(words_in_common) / (std::log(first_length) + std::log(second_length) + single_word_sentence_correction_factor);
}


namespace {


void LoadCommonAdverbs(GNU_HASH_SET<std::string> * const common_adverbs)
{
	common_adverbs->clear();

	const std::string filename(SHARE_DIR "/adverbs.txt");
	File adverbs(filename, "r");
	if (unlikely(adverbs.fail()))
		throw Exception("in LoadCommonAdverbs(TextUtil.cc): can't open \"" + filename + "\" for reading!");

	while (adverbs()) {
		std::string line;
		adverbs.getline(&line);
		std::vector<std::string> individual_adverbs;
		StringUtil::SplitThenTrimWhite(line, ' ', &individual_adverbs);
		for (std::vector<std::string>::const_iterator adverb(individual_adverbs.begin()); adverb != individual_adverbs.end(); ++adverb) {
			// Skip over words that are longer than 4 characters and end in -ly:
			if (adverb->length() < 5 or (*adverb)[adverb->length() - 1] != 'y' or (*adverb)[adverb->length() - 2] != 'l')
				common_adverbs->insert(*adverb);
		}
	}
}


} // unnamed namespace


bool IsPossibleAdverb(const std::string &adverb_candidate)
{
	if (adverb_candidate.length() < 2 or not isalpha(adverb_candidate[0]))
		return false;

	static GNU_HASH_SET<std::string> common_adverbs;
	if (common_adverbs.empty())
		LoadCommonAdverbs(&common_adverbs);

	std::string lowercase_adverb_candidate(adverb_candidate);
	StringUtil::ToLower(&lowercase_adverb_candidate);

	// 1. Look for a table entry:
	if (common_adverbs.find(lowercase_adverb_candidate) != common_adverbs.end())
		return true;

	// 2. Consider words of length 5+ ending in -ly:
	if (lowercase_adverb_candidate.length() < 5 or lowercase_adverb_candidate[lowercase_adverb_candidate.length() - 1] != 'y'
	    or lowercase_adverb_candidate[lowercase_adverb_candidate.length() - 2] != 'l')
		return false;
	return true;
}


namespace {


void LoadNonPresentParticiples(GNU_HASH_SET<std::string> * const non_present_participles)
{
	non_present_participles->clear();

	const std::string filename(SHARE_DIR "/non_present_participles.txt");
	File non_present_participles_file(filename, "r");
	if (unlikely(non_present_participles_file.fail()))
		throw Exception("in LoadNonPresentParticiples(TextUtil.cc): can't open \"" + filename + "\" for reading!");

	while (non_present_participles_file()) {
		std::string line;
		non_present_participles_file.getline(&line);
		std::vector<std::string> individual_non_present_participles;
		StringUtil::SplitThenTrimWhite(line, ' ', &individual_non_present_participles);
		for (std::vector<std::string>::const_iterator present_participle(individual_non_present_participles.begin());
		     present_participle != individual_non_present_participles.end(); ++present_participle)
			non_present_participles->insert(*present_participle);
	}
}


} // unnamed namespace


bool IsPossiblePresentParticiple(const std::string &present_participle_candidate)
{
	if (not isalpha(present_participle_candidate[0]))
		return false;

	if (unlikely(present_participle_candidate == "doing"))
		return true;

	if (present_participle_candidate.length() < 6)
		return false;

	GNU_HASH_SET<std::string> non_present_participles;
	if (non_present_participles.empty())
		LoadNonPresentParticiples(&non_present_participles);

	// Are with dealing with "faux amis"?
	if (non_present_participles.find(present_participle_candidate) != non_present_participles.end())
		return false;

	// Word ending in -ing?
	return present_participle_candidate[present_participle_candidate.length() - 3] == 'i'
	       and present_participle_candidate[present_participle_candidate.length() - 2] == 'n'
	       and present_participle_candidate[present_participle_candidate.length() - 1] == 'g';
}


namespace {


// TrimLeaderAndTrailer -- removes the leading "Fig." or "Figure" and other junk from a caption.
//
void TrimLeaderAndTrailer(std::string * const caption)
{
	const char * const cp(caption->c_str());
	const char *start(caption->c_str());
	if (std::strncmp(cp, "Fig. ", 5) == 0)
		start += 5;
	else if (std::strncmp(cp, "Figure ", 7) == 0)
		start += 7;
	else if (std::strncmp(cp, "Table ", 6) == 0)
		start += 6;
	else
		throw Exception("in TrimLeaderAndTrailer(TextUtil.cc): caption \"" + *caption + "\" does not start with a leader!");

	// Skip over spaces:
	while (*start == ' ')
		++start;

	// Skip over digits:
	while (isdigit(*start))
		++start;

	// Skip over spaces:
	while (*start == ' ')
		++start;

	// Skip over an optional period:
	if (*start == '.')
		++start;

	// Skip over spaces:
	while (*start == ' ')
		++start;

	*caption = caption->substr(start - cp);
	static const std::string trailer("(See text for details.)");
	if (caption->length() > trailer.length() and std::strcmp(caption->c_str() + caption->length() - trailer.length(), trailer.c_str()) == 0)
		caption->resize(caption->length() - trailer.length());

	StringUtil::RightTrim(caption);
}


// StartsWithLowercaseLetter -- helper function for FigureCaptionExtractor().  Skips over leading "(i)", "(ii)", "(A)", "(a)" etc.
//
bool StartsWithLowercaseLetter(const std::string &text)
{
        if (unlikely(text.empty()))
                return false;

	std::string::const_iterator start(text.begin());
        if (*start == '(') {
		// Skip until we find a closing parenthesis.
                do
                        ++start;
                while (start != text.end() and *start != ')');
                if (start == text.end())
                        return false;
                ++start;

                // Now skip over whitespace:
                while (start != text.end() and StringUtil::IsWhitespace(*start))
                        ++start;
                if (start == text.end())
                        return false;
        }

        return islower(*start);
}


void RemoveTrailingParenthesizedPhrase(std::string * const caption)
{
	if (caption->length() < 4)
		return;

	std::string::reverse_iterator ch(caption->rbegin()), rstart(ch);
	if (*ch != ')') {
		--ch; // Skip over last character.
		rstart = ch;

		// Optionally skip over whitespace:
		while (ch != caption->rbegin() and StringUtil::IsWhitespace(*ch))
			--ch;

		if (ch == caption->rbegin() or *ch != ')')
			return;
	}

	// Look for matching opening parenthesis:
	while (ch != caption->rbegin() and *ch != '(')
		--ch;

	if (ch == caption->rbegin() or *ch != '(')
		return;

	// Remove the parenthesize section, including possible trailing whitespace:
	const char last_char(rstart != caption->rbegin() ? *caption->rbegin() : '\0');
	caption->resize(caption->size() - (caption->rbegin() - ch));
	if (last_char != '\0')
		*caption += last_char;
}


} // unnamed namespace


void FigureCaptionExtractor(const std::string &plain_text, const CaptionProcessing caption_processing, std::vector<std::string> * const captions)
{
	captions->clear();

	const char *cp(plain_text.c_str());
	while ((cp = std::strchr(cp, '\n')) != NULL) {
		++cp;
		const char * const start(cp);

		if (std::strncmp(cp, "Fig. ", 5) == 0)
			cp += 5;
		else if (std::strncmp(cp, "Figure ", 7) == 0)
			cp += 7;
		else if (std::strncmp(cp, "Table ", 6) == 0)
			cp += 6;
		else
			continue;

		// Skip over spaces:
		while (*cp == ' ')
			++cp;

		if (not isdigit(*cp))
			continue;

		const char *end(std::strstr(cp, "\n\n"));
		if (end == NULL)
			end = std::strchr(cp, '\n');
                const std::string raw_caption_candidate(end == NULL ? std::string(start) : std::string(start, end - start));
                std::string trimmed_caption_candidate(raw_caption_candidate);
                TrimLeaderAndTrailer(&trimmed_caption_candidate);

                // Skip over "captions" whose text starts with a lowercase letter.  (This is usually noncaption text referring to a figure or table.):
                if (StartsWithLowercaseLetter(trimmed_caption_candidate))
                        continue;

                if (caption_processing == TRIM_LEADERS_AND_TRAILERS)
                        captions->push_back(trimmed_caption_candidate);
                else if (caption_processing == KEEP_FIRST_SENTENCE_ONLY) {
                        std::vector<SentenceAndWords> extracted_sentences;
                        HtmlSentenceParser sentence_parser(trimmed_caption_candidate, &extracted_sentences);
                        sentence_parser.parse();
                        if (not extracted_sentences.empty()) {
				std::string extracted_sentence(extracted_sentences.front().getSentence());
				RemoveTrailingParenthesizedPhrase(&extracted_sentence);
                                captions->push_back(extracted_sentence);
			}
                }
                else
                        captions->push_back(raw_caption_candidate);
	}
}


namespace {


const char *common_acronyms[] = {
	"ATM",
	"IBM",
	"USA",
	"VOL",
};


inline bool IsCommonAcronym(const std::string &word)
{
	const char *key(word.c_str());
	return std::bsearch(&key, &common_acronyms[0], DIM(common_acronyms), sizeof(common_acronyms[0]), WordCompare) != NULL;
}


bool ContainsMultipleAsciiCapsLettersAndNoMoreThan2LowercaseLetters(const std::string &word)
{
	unsigned caps_letter_count(0), lowercase_letter_count(0);
	for (std::string::const_iterator ch(word.begin()); ch != word.end(); ++ch) {
		if (not StringUtil::IsAsciiLetter(*ch))
			return false;
		if (isupper(*ch))
			++caps_letter_count;
		else if (islower(*ch))
			++lowercase_letter_count;
	}

	return caps_letter_count > 1 and lowercase_letter_count <= 2;
}


inline bool TokenIsOperator(const SimpleTokenStream::const_iterator &token)
{
//	static const std::string equation_context("+-*/=[]");
	static const std::string equation_context("=[]");
	return token->type_ == ST_NON_ALNUM_CHAR and equation_context.find(token->value_[0]) != std::string::npos;
}


bool InFormulaContext(const SimpleTokenStream::const_iterator &token, const SimpleTokenStream::const_iterator &begin,
		      const SimpleTokenStream::const_iterator &end)
{
	// First look for a leading indication that the token is in a mathematical context:
	if (token != begin) {
		if (TokenIsOperator(token - 1))
			return true;
		else if ((token - 1) != begin and (token - 1)->type_ == ST_WHITESPACE and TokenIsOperator(token - 2))
			return true;
	}

	// Now look for a trailing indication that the token is in a mathematical context:
	if ((token + 1) != end) {
		if (TokenIsOperator(token + 1))
			return true;
		else if ((token + 2) != end and (token + 1)->type_ == ST_WHITESPACE and TokenIsOperator(token + 2))
			return true;
	}

	return false;
}


inline bool IsAcronymCandidate(const SimpleTokenStream::const_iterator &token, const SimpleTokenStream::const_iterator &end)
{
	const size_t word_length(token->value_.length());
	return token != end and (word_length > 2 and word_length < 10) and ContainsMultipleAsciiCapsLettersAndNoMoreThan2LowercaseLetters(token->value_);
}


inline bool TokenIsSlash(const SimpleTokenStream::const_iterator &token, const SimpleTokenStream::const_iterator &end)
{
	return token != end and token->type_ == ST_NON_ALNUM_CHAR and token->value_[0] == '/';
}


inline bool CompareOnOccurrenceCount(const std::pair<std::string, unsigned> &acronym_and_count1,
				     const std::pair<std::string, unsigned> &acronym_and_count2)
{
	return acronym_and_count1.second > acronym_and_count2.second;
}


} // unnamed namespace


void AcronymFinder(const std::string &plain_text, std::vector< std::pair<std::string, unsigned> > * const acronyms_and_occurrence_counts)
{
	acronyms_and_occurrence_counts->clear();

	GNU_HASH_MAP<std::string, unsigned> unsorted_acronyms_and_occurrence_counts;
	SimpleTokenStream simple_token_stream;
	CreateSimpleTokenStream(plain_text, &simple_token_stream);
	GNU_HASH_SET<std::string> non_acronyms;
	for (SimpleTokenStream::const_iterator token(simple_token_stream.begin()); token != simple_token_stream.end(); ++token) {
		if (token->type_ == ST_ALPHA) {
			if (IsAcronymCandidate(token, simple_token_stream.end())) {
				std::string acronym_candidate(token->value_);
				bool skip_two(false);
				if (TokenIsSlash(token + 1, simple_token_stream.end()) and IsAcronymCandidate(token + 2, simple_token_stream.end())) {
					skip_two = true;
					acronym_candidate += '/';
					acronym_candidate += (token + 2)->value_;
				}
				if (InFormulaContext(token, simple_token_stream.begin(), simple_token_stream.end())) {
					GNU_HASH_MAP<std::string, unsigned>::iterator found(
						unsorted_acronyms_and_occurrence_counts.find(token->value_));
					if (found != unsorted_acronyms_and_occurrence_counts.end())
						unsorted_acronyms_and_occurrence_counts.erase(found);
					else
						non_acronyms.insert(token->value_);
				}
				else if (non_acronyms.find(acronym_candidate) == non_acronyms.end()) {
					GNU_HASH_MAP<std::string, unsigned>::iterator
						acronym_and_count(unsorted_acronyms_and_occurrence_counts.find(acronym_candidate));
					if (acronym_and_count != unsorted_acronyms_and_occurrence_counts.end())
						++acronym_and_count->second;
					else {
						static const Speller &speller(Speller::GetDefaultSpeller());
						if (not speller.isSpelledCorrectly(StringUtil::ToUpper(acronym_candidate))
						    and not IsCommonAcronym(acronym_candidate))
							unsorted_acronyms_and_occurrence_counts.insert(
								std::make_pair<std::string, unsigned>(acronym_candidate, 1));
					}
				}
				if (skip_two)
					token += 2;
			}
		}
	}

	acronyms_and_occurrence_counts->reserve(unsorted_acronyms_and_occurrence_counts.size());
	std::copy(unsorted_acronyms_and_occurrence_counts.begin(), unsorted_acronyms_and_occurrence_counts.end(),
		  std::back_inserter(*acronyms_and_occurrence_counts));
	std::sort(acronyms_and_occurrence_counts->begin(), acronyms_and_occurrence_counts->end(), CompareOnOccurrenceCount);
}


namespace {


void InitSyllableCountTable(GNU_HASH_MAP<std::string, unsigned> * const words_to_syllables_count_map)
{
	const std::string input_filename(SHARE_DIR "/webster_1996_syllable_counts");
	File input(input_filename, "r");
	if (unlikely(input.fail()))
		throw Exception("in InitSyllableCountTable(TextUtil.cc): can't open \"" + input_filename + "\" for reading!");

	for (;;) {
		std::string word;
		if (unlikely(not BinaryIO::Read(input, &word))) {
			if (input.eof())
				return;
			throw Exception("in InitSyllableCountTable(TextUtil.cc): error while trying to read a word from \"" + input_filename + "\"!");
		}

		unsigned syllable_count;
		if (unlikely(not BinaryIO::Read(input, &syllable_count)))
			throw Exception("in InitSyllableCountTable(TextUtil.cc): error while trying to read a syllable count from \"" + input_filename
					+ "\"!");

		words_to_syllables_count_map->insert(std::make_pair<std::string, unsigned>(word, syllable_count));
	}
}


const char *sub_syllable_pattern[] = {
	"cial",
	"tia",
	"cius",
	"cious",
	"giu",   // Belgium!
	"ion",
	"iou",
	"sia$",
	".ely$", // absolutely! (but not ely!)
};


const char *add_syllable_pattern[] = {
	"ia",
	"riet",
	"dien",
	"iu",
	"io",
	"ii",
	"[aeiouym]bl$",     // -Vble, plus -mble
	"[aeiou]{3}",       // agreeable
	"^mc",
	"ism$",             // -isms
	"([^aeiouy])\\1l$", // middle twiddle battle bottle, etc.
	"[^l]lien",         // alien, salient [1]
	"^coa[dglx].",      // [2]
	"[^gq]ua[^auieo]",  // i think this fixes more than it breaks
	"dnt$",             // couldn't
};


// (comments refer to titan's /usr/dict/words)
// [1] alien, salient, but not lien or ebbullient...
//     (those are the only 2 exceptions i found, there may be others)
// [2] exception for 7 words:
//     coadjutor coagulable coagulate coalesce coalescent coalition coaxial


inline void NonvowelSplit(const std::string &word, std::vector<std::string> * const chunks)
{
	chunks->clear();

	std::string current_chunk;
	for (std::string::const_iterator ch(word.begin()); ch != word.end(); ++ch) {
		if (std::strchr("aeiouy", *ch) != NULL)
			current_chunk += *ch;
		else if (not current_chunk.empty()) {
			chunks->push_back(current_chunk);
			current_chunk.clear();
		}
	}

	if (not current_chunk.empty())
		chunks->push_back(current_chunk);
}


} // unnamed namespace


// GetApproximateSyllableCount -- based on Perl module Lingua::EN::Syllable.
//
unsigned GetApproximateSyllableCount(const std::string &raw_word, const bool use_table_lookup_when_possible)
{
	std::string word;
	word.reserve(raw_word.size());

	// Lowercase letters and remove single quotes:
	for (std::string::const_iterator ch(raw_word.begin()); ch != raw_word.end(); ++ch) {
		if (*ch == '\'')
			continue;
		word += tolower(*ch);
	}

	if (unlikely(word.empty()))
		return 0;

	// If requested first perform a table lookup using exact syllable counts:
	if (use_table_lookup_when_possible) {
		static GNU_HASH_MAP<std::string, unsigned> words_to_syllables_count_map;
		if (unlikely(words_to_syllables_count_map.empty()))
			InitSyllableCountTable(&words_to_syllables_count_map);
		const GNU_HASH_MAP<std::string, unsigned>::const_iterator word_and_syllable_count(words_to_syllables_count_map.find(word));
		if (word_and_syllable_count != words_to_syllables_count_map.end())
			return word_and_syllable_count->second;
	}

	// Remove a trailing 'e':
	if (word[word.size() - 1] == 'e')
		word.resize(word.size() - 1);

	int syllable_count(0);

	std::vector<std::string> chunks;
	NonvowelSplit(word, &chunks);

	// Special cases:
	PerlCompatRegExp reg_exp;
	for (unsigned index(0); index < DIM(sub_syllable_pattern); ++index) {
		reg_exp.resetPattern(sub_syllable_pattern[index]);
		if (reg_exp.match(word))
			--syllable_count;
	}
	for (unsigned index(0); index < DIM(add_syllable_pattern); ++index) {
		reg_exp.resetPattern(add_syllable_pattern[index]);
		if (reg_exp.match(word))
			++syllable_count;
	}

	// `Words' like "x":
	if (word.length() == 1)
		++syllable_count;

	// Count vowel groupings:
	syllable_count += chunks.size();

	// Got no vowels, like "the" or "crwth"?
	if (syllable_count < 1)
		syllable_count = 1;

	return syllable_count;
}


bool ContainsVowel(const std::string &word)
{
	for (std::string::const_iterator ch(word.begin()); ch != word.end(); ++ch) {
		if (__builtin_strchr("aeiouyAEIOUY", *ch) != NULL)
			return true;
	}

	return false;
}


namespace {


/** \class  WordToIndexMapper
 *  \brief  Maintains a unique word to an numeric index map.
 *  \note   The idea is that words in sentences can be replaced by numbers for faster processing.
 */
class WordToIndexMapper {
	GNU_HASH_MAP<std::string, unsigned> word_to_index_map_;
	unsigned next_index_;
public:
	WordToIndexMapper(): next_index_(0) { }

	/** \brief  Returns the `index' corressponding to "word."
	 *  \param  word         The word whose index we want.
	 *  \param  ignore_case  If true, "word" will be converted to lowercase and then the index will be determined.\
	 *  \note   If "word" has never been seen before, a new, unique index will be assigned to it.
	 */
	unsigned getIndex(const std::string &word, const bool ignore_case = false);

	unsigned getNextIndex() const { return next_index_; }
};


unsigned WordToIndexMapper::getIndex(const std::string &word, const bool ignore_case)
{
	const std::string normalised_word(ignore_case ? StringUtil::ToLower(word) : word);
	const GNU_HASH_MAP<std::string, unsigned>::const_iterator word_and_index(word_to_index_map_.find(normalised_word));
	if (word_and_index != word_to_index_map_.end())
		return word_and_index->second;
	else {
		const unsigned new_index(next_index_++);
		word_to_index_map_.insert(std::make_pair<std::string, unsigned>(normalised_word, new_index));
		return new_index;
	}
}


class Sentence: public std::vector<std::string> { };


void ExtractAndMassageSentences(const std::string &text, const bool stem, const GNU_HASH_SET<std::string> &ignore_set, std::vector<Sentence> * const sentences)
{
	sentences->clear();

	std::vector<SentenceAndWords> extracted_sentences;
	HtmlSentenceParser parser(text, &extracted_sentences);
	parser.parse();
	for (std::vector<SentenceAndWords>::iterator sentence(extracted_sentences.begin()); sentence != extracted_sentences.end(); ++sentence) {
		Sentence massaged_sentence;
		sentence->toLowercase();
		for (SentenceAndWords::const_iterator word(sentence->begin()); word != sentence->end(); ++word) {
			if (ignore_set.find(*word) != ignore_set.end())
				continue;
			if (stem) {
				std::string stemmed_word(*word);
				Stemmer::stem(&stemmed_word, Stemmer::STEM);
				massaged_sentence.push_back(stemmed_word);
			}
			else
				massaged_sentence.push_back(*word);
		}

		if (not massaged_sentence.empty())
			sentences->push_back(massaged_sentence);
	}
}


} // unnamed namespace


double RougeLTextOverlap(const std::string &reference_text, const std::string &test_text, const double &beta, const bool stem,
			 const bool ignore_noise_words)
{
	const GNU_HASH_SET<std::string> empty_set;
	const GNU_HASH_SET<std::string> &ignore_set(ignore_noise_words ? GetNoiseWords() : empty_set);

	std::vector<Sentence> reference_sentences;
	ExtractAndMassageSentences(reference_text, stem, ignore_set, &reference_sentences);

	std::vector<Sentence> test_sentences;
	ExtractAndMassageSentences(test_text, stem, ignore_set, &test_sentences);

	unsigned reference_total_word_count(0);
	unsigned union_lcs_sum(0);
	for (std::vector<Sentence>::const_iterator reference_sentence(reference_sentences.begin()); reference_sentence != reference_sentences.end();
	     ++reference_sentence)
	{
		reference_total_word_count += reference_sentence->size();

		std::set<unsigned> cumulative_overlap_indices;
		for (std::vector<Sentence>::const_iterator test_sentence(test_sentences.begin()); test_sentence != test_sentences.end(); ++test_sentence) {
			std::vector<unsigned> common_indices;
			MiscUtil::CalculateLongestCommonSubsequence(*reference_sentence, *test_sentence, &common_indices);
			for (std::vector<unsigned>::const_iterator index(common_indices.begin()); index != common_indices.end(); ++index)
				cumulative_overlap_indices.insert(*index);
		}

		union_lcs_sum += cumulative_overlap_indices.size();
	}

	unsigned test_total_word_count(0);
	for (std::vector<Sentence>::const_iterator test_sentence(test_sentences.begin()); test_sentence != test_sentences.end(); ++test_sentence)
		test_total_word_count += test_sentence->size();

	const double R_lcs(static_cast<double>(union_lcs_sum) / static_cast<double>(reference_total_word_count));
	const double P_lcs(static_cast<double>(union_lcs_sum) / static_cast<double>(test_total_word_count));
	if (R_lcs < 1.0e-30 and P_lcs < 1.0e-30)
		return 0.0;

	const double F_lcs(((1.0 + beta * beta) * R_lcs * P_lcs) / (R_lcs + beta * beta * P_lcs));
	return F_lcs;
}


namespace {


void GenerateNGrams(const std::vector<Sentence> &sentences, const unsigned max_n_gram_length, GNU_HASH_SET<std::string> * const n_grams)
{
	n_grams->clear();
	for (std::vector<Sentence>::const_iterator sentence(sentences.begin()); sentence != sentences.end(); ++sentence) {
		for (unsigned n_gram_length(1); n_gram_length <= max_n_gram_length; ++n_gram_length) {
			for (int start_index(0); start_index <= static_cast<int>(sentence->size()) - static_cast<int>(n_gram_length); ++start_index) {
				std::string n_gram_token((*sentence)[start_index]);
				for (int index(start_index + 1); index < start_index + static_cast<int>(n_gram_length); ++index) {
					n_gram_token += '&';
					n_gram_token += (*sentence)[index];
				}
				n_grams->insert(n_gram_token);
			}
		}
	}
}


} // unnamed namespace


double RougeNTextOverlap(const std::string &reference_text, const std::string &test_text, const unsigned max_n_gram_length, const bool stem,
			 const bool ignore_noise_words)
{
	if (max_n_gram_length < 1 or max_n_gram_length > 10)
		throw Exception("in TextUtil::RougeNTextOverlap: \"n\" must be in [1,10]!");

	const GNU_HASH_SET<std::string> empty_set;
	const GNU_HASH_SET<std::string> &ignore_set(ignore_noise_words ? GetNoiseWords() : empty_set);

	std::vector<Sentence> reference_sentences;
	ExtractAndMassageSentences(reference_text, stem, ignore_set, &reference_sentences);
	GNU_HASH_SET<std::string> reference_n_grams;
	GenerateNGrams(reference_sentences, max_n_gram_length, &reference_n_grams);
	if (reference_n_grams.size() == 0)
		return 0.0;

	std::vector<Sentence> test_sentences;
	ExtractAndMassageSentences(test_text, stem, ignore_set, &test_sentences);
	GNU_HASH_SET<std::string> test_n_grams;
	GenerateNGrams(test_sentences, max_n_gram_length, &test_n_grams);

	unsigned overlap_count(0);
	for (GNU_HASH_SET<std::string>::const_iterator test_n_gram(test_n_grams.begin()); test_n_gram != test_n_grams.end(); ++test_n_gram) {
		if (reference_n_grams.find(*test_n_gram) != reference_n_grams.end())
			++overlap_count;
	}

	return static_cast<double>(overlap_count) / static_cast<double>(reference_n_grams.size());
}


std::string TextOverlapMethodToString(const TextOverlapMethod method)
{
	switch (method) {
	case ROUGE_L:
		return "ROUGE-L";
		break;
	case ROUGE_N:
		return "ROUGE-N";
		break;
	default:
		throw Exception("in TextUtil::TextOverlapMethodToString: unknown method " + StringUtil::ToString(method) + "!");
	}
}


double CalcTextOverlap(const TextOverlapMethod method, const std::string &reference_text, const std::string &test_text, const double &beta,
		       const unsigned max_n_gram_length, const bool stem, const bool ignore_noise_words)
{
	if (method == ROUGE_L)
		return RougeLTextOverlap(reference_text, test_text, beta, stem, ignore_noise_words);
	else if (method == ROUGE_N)
		return RougeNTextOverlap(reference_text, test_text, max_n_gram_length, stem, ignore_noise_words);
	else
		throw Exception("in TextOverlapMethod::CalcTextOverlap: unknown text overlap method " + StringUtil::ToString(method) + "!");
}


void GenerateLowRedundancyText(const std::vector<const SentenceAndWords *> &sentences, const unsigned min_no_of_words, const double max_sentence_overlap,
			       std::string * const text, const bool stem)
{
	if (unlikely(max_sentence_overlap < 0.0 or max_sentence_overlap >= 1.0))
		throw Exception("in TextUtil::GenerateLowRedundancyText: \"max_sentence_overlap\" must be in [0.0,1.0)!");
	text->clear();

	const GNU_HASH_SET<std::string> &ignore_set(GetNoiseWords());
	GNU_HASH_SET<std::string> already_seen;

	unsigned accumulated_no_of_words(0);
	std::vector<const SentenceAndWords *> selected_sentences;
	for (std::vector<const SentenceAndWords *>::const_iterator sentence(sentences.begin());
	     accumulated_no_of_words < min_no_of_words and sentence != sentences.end(); ++sentence)
	{
		double overlap(0.0);
		unsigned significant_word_count(0);
		for (std::vector<std::string>::const_iterator word((*sentence)->begin()); word != (*sentence)->end(); ++word) {
			const std::string canonised_word(Stemmer::stem(*word, stem ? Stemmer::CASE_FOLD_AND_STEM : Stemmer::CASE_FOLD));
			if (ignore_set.find(canonised_word) == ignore_set.end()) {
				++significant_word_count;
				if (already_seen.find(canonised_word) != already_seen.end())
					++overlap;
			}
		}
		overlap /= significant_word_count;

		if (overlap < max_sentence_overlap) {
			for (std::vector<std::string>::const_iterator word((*sentence)->begin()); word != (*sentence)->end(); ++word) {
				const std::string canonised_word(Stemmer::stem(*word, stem ? Stemmer::CASE_FOLD_AND_STEM : Stemmer::CASE_FOLD));
				if (ignore_set.find(canonised_word) == ignore_set.end())
					already_seen.insert(canonised_word);
			}
			selected_sentences.push_back(*sentence);
			accumulated_no_of_words += (*sentence)->getWordCount();
		}
	}

	std::sort(selected_sentences.begin(), selected_sentences.end(), SentenceAndWordsPtrCompare);
	for (std::vector<const SentenceAndWords *>::const_iterator selected_sentence(selected_sentences.begin());
	     selected_sentence != selected_sentences.end(); ++selected_sentence)
	{
		if (selected_sentence != selected_sentences.begin())
			*text += ' ';
		*text += (*selected_sentence)->getSentence();
	}
}


typedef char *(*SearchFunc)(const char *haystack, const char *needle);


unsigned CountOccurrences(const std::string &needle, const std::string &haystack, const bool case_sensitive, const bool normalise_whitespace)
{
	std::string text(haystack);
	if (normalise_whitespace)
		StringUtil::CollapseWhitespace(&text);

	SearchFunc search_func;
	if (case_sensitive)
		search_func = std::strstr;
	else
		search_func = ::strcasestr;

	unsigned count(0);
	for (const char *cp(text.c_str()); (cp = search_func(cp, needle.c_str())) != NULL; cp += needle.size())
		++count;

	return count;
}


void GetCaseMix(const std::string &text, double * const percent_upper, double * const percent_lower, double * const percent_other)
{
	if (unlikely(text.empty())) {
		*percent_upper = *percent_lower = *percent_other = 0.0;
		return;
	}

	size_t upper_count(0), lower_count(0), other_count(0);
	for (std::string::const_iterator ch(text.begin()); ch != text.end(); ++ch) {
		if (islower(*ch))
			++lower_count;
		else if (isupper(*ch))
			++upper_count;
		else
			++other_count;
	}

	const double total_count(upper_count + lower_count + other_count);
	*percent_upper = 100.0 * upper_count / total_count;
	*percent_lower = 100.0 * lower_count / total_count;
	*percent_other = 100.0 * other_count / total_count;
}


namespace {


template<typename type> class Matrix2D {
	unsigned no_of_rows_, no_of_cols_;
	type *elements_;
public:
	Matrix2D(const unsigned no_of_rows, const unsigned no_of_cols)
		: no_of_rows_(no_of_rows), no_of_cols_(no_of_cols), elements_(new type[no_of_rows * no_of_cols]) { }
	~Matrix2D() { delete [] elements_; }
	const type &operator()(unsigned row, unsigned col) const { return elements_[row * no_of_cols_ + col]; }
	type &operator()(unsigned row, unsigned col) { return elements_[row * no_of_cols_ + col]; }
};


} // unnamed namespace


// GetLongestCommonSubstring -- determines the longest common substring of "s1" and "s2" using dynamic programming,  Based on
//                              http://www.ics.uci.edu/~dan/class/161/notes/6/Dynamic.html.
//
void GetLongestCommonSubstring(const std::string &s1, const std::string &s2, std::string * const longest_common_substring)
{
	longest_common_substring->clear();

	if (unlikely(s1.empty() or s2.empty()))
		return;

	Matrix2D<unsigned> l(s1.length() + 1, s2.length() + 1);
	for (unsigned i(0); i <= s1.length(); ++i)
		l(i, 0) = 0;
	for (unsigned j(0); j <= s2.length(); ++j)
		l(0, j) = 0;

	unsigned max_length(0), s1_end_index(0);
	for (unsigned i(1); i <= s1.length(); ++i) {
		for (unsigned j(1); j <= s2.length(); ++j) {
			if (s1[i - 1] != s2[j - 1])
				l(i, j) = 0;
			else {
				l(i, j) = 1 + l(i - 1, j - 1);
				if (l(i, j) > max_length) {
					max_length = l(i, j);
					s1_end_index = i;
				}
			}
		}
	}

	if (max_length > 0)
		*longest_common_substring = s1.substr(s1_end_index - max_length, max_length);
}


namespace {


bool IsUrlPrefix(const std::string &current_word)
{
	return StringUtil::StartsWith(current_word, "http://") or StringUtil::StartsWith(current_word, "https://")
	       or StringUtil::StartsWith(current_word, "ftp://");
}


bool IsPossibleEmailPrefix(const std::string &current_word)
{
	return current_word.find('@') != std::string::npos;
}


bool IsCommonAbbrevOrTitle(const std::string &s)
{
	if (s == "Dr." or s == "Mr." or s == "Mrs." or s == "Ms." or s == "Jr." or s == "Sr." or s == "Ph.D." or s == "vs." or s == "St." or s == "Vol."
	    or s == "vol." or s == "al." or s == "ibid." or s == "op." or s == "cit." or s == "i.e." or s == "e.g." or s == "Inc." or s == "LLC."
	    or s == "Ltd." or s == "B.A." or s == "M.S." or s == "etc.")
		return true;

	return false;
}


bool IsNotSentenceEndIfFollowedByPeriod(const std::string &current_word, const char next_char)
{
	static const PerlCompatRegExp reg_exp("^[[:alpha:]](\\.[[:alpha:]])*$");
	if (reg_exp.match(current_word))
		return true;

	if (IsPossibleEmailPrefix(current_word) and next_char != ' ')
		return true;

	return IsCommonAbbrevOrTitle(current_word + ".");
}


bool EndsInWhiteSpaceFollowedByCommonAbbrevOrInitials(const std::string &s)
{
	const std::string::size_type last_whitespace_pos(s.find_last_of(StringUtil::WHITE_SPACE));
	if (last_whitespace_pos == std::string::npos or last_whitespace_pos == s.size() - 1 or unlikely(s.empty()) or s[s.size() - 1] != '.')
		return false;

	const std::string test_string(s.substr(last_whitespace_pos + 1, s.length() - last_whitespace_pos - 1));

	if (IsCommonAbbrevOrTitle(test_string))
		return true;

	static const PerlCompatRegExp reg_exp("^([[:alpha:]]\\.)+$");
	return reg_exp.match(test_string);
}


enum LookAheadBehaviour { SKIP_WHITESPACE, DO_NOT_SKIP_WHITESPACE };


// LookAhead -- returns the next character after "ch" unless ch == end, in which case NUL will be returned.
//
char LookAhead(const std::string::const_iterator &ch, const std::string::const_iterator &end, const LookAheadBehaviour behaviour)
{
	if (unlikely(ch == end))
		return '\0';

	std::string::const_iterator next_ch(ch + 1);

	if (behaviour == SKIP_WHITESPACE) {
		while (next_ch != end and StringUtil::IsWhitespace(*next_ch))
			++next_ch;
	}

	return next_ch == end ? '\0' : *next_ch;
}


bool IsJunkSentence(const std::string &sentence_candidate)
{
	if (sentence_candidate.length() < 6)
		return true;

	unsigned letter_count(0), non_letter_count(0), space_count(0);
	for (std::string::const_iterator ch(sentence_candidate.begin()); ch != sentence_candidate.end(); ++ch) {
		if (isalpha(*ch))
			++letter_count;
		else if (*ch != ' ')
			++non_letter_count;
		else
			++space_count;
	}

	return space_count > 0 and letter_count < 2 * non_letter_count;
}


bool IsValidUrlChar(const char ch)
{
	return isalnum(ch) or ch == ':' or ch == '/' or ch == '?' or std::strchr("$-_.+!*'(),", ch) != NULL;
}


} // unnamed namespace


void SplitIntoSentences(const std::string &document, std::vector<std::string> * const sentences, const bool attempt_to_combine_broken_words,
			const bool remove_possible_junk)
{
	sentences->clear();

	std::string sanitised_document(attempt_to_combine_broken_words ? CombineLines(document) : document);
	StringUtil::Map(&sanitised_document, "\r\n", "  ");
	StringUtil::CollapseWhitespace(&sanitised_document);

	const char END_CHARS[] = ".?!";

	std::string::const_iterator ch(sanitised_document.begin());
	bool starts_with_paren(LookAhead(ch, sanitised_document.end(), SKIP_WHITESPACE) == '(');

	std::string current_word, current_sentence;
	bool in_url(false);
	for (/* Empty! */; ch != sanitised_document.end(); ++ch) {
		current_sentence += *ch;

		if (in_url) {
			if (not IsValidUrlChar(*ch)) {
				if (*(ch - 1) == '.' or *(ch - 1) == '?') {
					sentences->push_back(current_sentence);
					current_sentence.clear();
					current_word.clear();
				}
			}
			else
				current_word += *ch;
		}
		else if (isalnum(*ch) or *ch == ':' or *ch == '/' or *ch == '@')
			current_word += *ch;
		else if (std::strchr(END_CHARS, *ch) == NULL)
			current_word.clear();
		else if (*ch == '.') {
			if (islower(LookAhead(ch, sanitised_document.end(), SKIP_WHITESPACE))
			    or IsNotSentenceEndIfFollowedByPeriod(current_word, LookAhead(ch, sanitised_document.end(), DO_NOT_SKIP_WHITESPACE)))
				current_word += *ch;
			else if (IsUrlPrefix(current_word))
				in_url = true;
			else { // We may have a legitimate end-of-sentence.
				// Period embedded in a number?
				if (StringUtil::IsUnsignedNumber(current_word) and *ch == '.'
				    and StringUtil::IsDigit(LookAhead(ch, sanitised_document.end(), DO_NOT_SKIP_WHITESPACE)))
				{
					current_word.clear();
					continue;
				}

				// Etc. is the end?
				if (::strcasecmp(current_word.c_str(), "etc") == 0) {
					const char look_ahead_char(LookAhead(ch, sanitised_document.end(), SKIP_WHITESPACE));
					if (look_ahead_char != '\0' and not isupper(look_ahead_char) and not starts_with_paren and look_ahead_char == ')') {
						current_word.clear();
						continue;
					}
				}

				current_word.clear();
				if (LookAhead(ch, sanitised_document.end(), DO_NOT_SKIP_WHITESPACE) == '"') {
					current_sentence += *ch;
					++ch;
					const char next_char(LookAhead(ch, sanitised_document.end(), DO_NOT_SKIP_WHITESPACE));
					if (next_char != '\0' and std::strchr(END_CHARS, next_char) != NULL) {
						current_sentence += *ch;
						++ch;
					}
				}

				if (starts_with_paren and LookAhead(ch, sanitised_document.end(), SKIP_WHITESPACE) == ')') {
					do {
						++ch;
						current_sentence += *ch;
					} while (*ch != ')');
				}

				if (remove_possible_junk) {
					if (not IsJunkSentence(current_sentence))
						sentences->push_back(current_sentence);
				}
				else
					sentences->push_back(current_sentence);
				current_sentence.clear();

				// Skip whitespace:
				while (StringUtil::IsWhitespace(LookAhead(ch, sanitised_document.end(), DO_NOT_SKIP_WHITESPACE)))
					++ch;

				starts_with_paren = LookAhead(ch, sanitised_document.end(), DO_NOT_SKIP_WHITESPACE) == '(';
			}
		}
	}
}


namespace {


char LookAhead(const std::string::const_iterator &ch, const std::string::const_iterator &end)
{
	if (ch == end or ch + 1 == end)
		return '\0';
	return *(ch + 1);
}


} // unnamed namespace


unsigned SplitSentenceIntoWords(const std::string &sentence, std::vector<std::string> * const words, const bool lowercase_words,
				std::vector<std::string> * const urls)
{
	words->clear();
	if (urls != NULL)
		urls->clear();

	// Determine the end of the words:
	static std::string alnum_chars;
	if (alnum_chars.empty()) {
		for (int ch(0); ch < 256; ++ch) {
			if (isalnum(ch))
				alnum_chars += static_cast<char>(ch);
		}
	}
	const std::string::size_type last_pos(sentence.find_last_of(alnum_chars));
	if (unlikely(last_pos == std::string::npos))
		return 0;
	const std::string::const_iterator end(sentence.begin() + last_pos + 1);

	enum { LOOK_FOR_WORD_START, IN_NUMBER, IN_SIMPLE_WORD, IN_URL } mode(LOOK_FOR_WORD_START);
	std::string current_word;
	for (std::string::const_iterator ch(sentence.begin()); ch != end; ++ch) {
		switch (mode) {
		case LOOK_FOR_WORD_START:
			if (isalpha(*ch)) {
				current_word += *ch;
				mode = IN_SIMPLE_WORD;
			}
			else if (isdigit(*ch)) {
				current_word += *ch;
				mode = IN_NUMBER;
			}
			break;
		case IN_NUMBER:
			if (isdigit(*ch) or *ch == ',' or *ch == '.' or *ch == ':')
				current_word += *ch;
			else {
				if (current_word[current_word.size() - 1] == ',' or current_word[current_word.size() - 1] == ':')
					current_word.resize(current_word.size() - 1);
				words->push_back(current_word);
				current_word.clear();
				if (isalpha(*ch)) {
					current_word = *ch;
					mode = IN_SIMPLE_WORD;
				}
				else
					mode = LOOK_FOR_WORD_START;
			}
			break;
		case IN_SIMPLE_WORD:
			if (*ch == '\'') {
				if (isalpha(LookAhead(ch, end)))
					current_word += *ch;
				else {
					if (lowercase_words)
						StringUtil::ToLower(&current_word);
					words->push_back(current_word);
					current_word.clear();
					mode = LOOK_FOR_WORD_START;
				}
			}
			else if (*ch == ':'
				 and (current_word == "http" or current_word == "https" or current_word == "ftp" or current_word == "gopher"
				      or current_word == "telnet"))
			{
				current_word += *ch;
				mode = IN_URL;
			}
			else if (*ch == '-' and LookAhead(ch, end) == '-') {
				if (lowercase_words)
					StringUtil::ToLower(&current_word);
				words->push_back(current_word);
				current_word.clear();
				mode = LOOK_FOR_WORD_START;
			}
			else if (isalnum(*ch) or *ch == '-' or *ch == '.')
				current_word += *ch;
			else {
				if (current_word.size() > 2 and StringUtil::EndsWith(current_word, "'s", /* ignore_case = */ true))
					current_word.resize(current_word.size() - 2);
				else if (current_word[current_word.size() - 1] == '-')
					current_word.resize(current_word.size() - 1);
				if (lowercase_words)
					StringUtil::ToLower(&current_word);
				words->push_back(current_word);
				current_word.clear();
				mode = LOOK_FOR_WORD_START;
			}
			break;
		case IN_URL:
			if (not IsValidUrlChar(*ch)) {
				if (StringUtil::IsLatin9Whitespace(*ch)) {
					if (current_word[current_word.size() - 1] == ',' or current_word[current_word.size() - 1] == '.'
					    or current_word[current_word.size() - 1] == ')')
						current_word.resize(current_word.size() - 1);
				}
				if (urls != NULL)
					urls->push_back(current_word);
				words->push_back(current_word);
				current_word.clear();
				mode = LOOK_FOR_WORD_START;
			}
			else
				current_word += *ch;
			break;
		}
	}

	if (not current_word.empty()) {
		if (mode == IN_NUMBER) {
			if (current_word[current_word.size() - 1] == ',')
				current_word.resize(current_word.size() - 1);
		}
		else if (mode == IN_SIMPLE_WORD) {
			if (current_word.size() > 2 and StringUtil::EndsWith(current_word, "'s", /* ignore_case = */ true))
				current_word.resize(current_word.size() - 2);
			else if (current_word[current_word.size() - 1] == '-')
				current_word.resize(current_word.size() - 1);
		}
		else if (mode == IN_URL) {
			if (current_word[current_word.size() - 1] == ',' or current_word[current_word.size() - 1] == '.'
			    or current_word[current_word.size() - 1] == ')')
				current_word.resize(current_word.size() - 1);
			if (urls != NULL)
				urls->push_back(current_word);
		}

		if (not current_word.empty()) {
			if (lowercase_words and mode != IN_URL)
				StringUtil::ToLower(&current_word);
			words->push_back(current_word);
		}
	}

	return words->size();
}


namespace {


std::string ExtractLeadingLetters(const std::string &s, const bool attempt_to_combine_broken_sections, bool * const strip_leading_formfeed)
{
	std::string::const_iterator ch(s.begin());
	if (attempt_to_combine_broken_sections and ch != s.end() and *ch == '\f') {
		*strip_leading_formfeed = true;
		++ch;
	}
	else
		*strip_leading_formfeed = false;

	std::string leading_letters;
	for (/* Intentionally empty! */; ch != s.end() and isalpha(*ch); ++ch)
		leading_letters += *ch;

	return leading_letters;
}


} // unnamed namespace


std::string CombineLines(const std::string &document, const bool attempt_to_combine_broken_sections)
{
	static const Speller &speller(Speller::GetDefaultSpeller());

	std::vector<std::string> lines;
	StringUtil::SplitThenTrim(document, "\n\r", " \t", &lines, /* suppress_empty_components = */ false);
	if (unlikely(lines.size() < 2))
		return document;

	std::string combined_text;
	bool strip_leading_formfeed(false);
	for (std::vector<std::string>::const_iterator line(lines.begin()); line != lines.end() - 1; ++line) {
		if (line->empty() or line->length() < 2) {
			combined_text += '\n';
			continue;
		}

		if ((*line)[line->length() - 1] != '-') {
			if (strip_leading_formfeed) {
				strip_leading_formfeed = false;
				combined_text += line->substr(1);
			}
			else {
				combined_text += *line;

				// Check for possible broken URLs and other weird constructs:
				if (std::strchr("./", (*line)[line->size() - 1]) != NULL and islower(*((line + 1)->c_str()))) {
					if ((*line)[line->size() - 1] != '.' or not EndsInWhiteSpaceFollowedByCommonAbbrevOrInitials(*line))
						continue;
				}
			}
		}
		else { // Check to see if we recognise a broken word:
			// 1. Try to extract the trailing word fragment:
			const std::string::size_type last_likely_non_letter_pos(line->substr(0, line->length() - 1).find_last_of(" \t,;.?!-("));
			if (last_likely_non_letter_pos == std::string::npos) { // No such luck!
				if (strip_leading_formfeed) {
					strip_leading_formfeed = false;
					combined_text += line->substr(1);
				}
				else
					combined_text += *line;
				continue;
			}
			const std::string trailing_fragment_candidate(
				line->substr(last_likely_non_letter_pos + 1, line->length() - last_likely_non_letter_pos - 2));

			// 2. Now try to get the leading line fragment on the following line:
			const std::string leading_fragment_candidate(
				ExtractLeadingLetters(*(line + 1), attempt_to_combine_broken_sections, &strip_leading_formfeed));
			if (leading_fragment_candidate.empty()) {
				if (strip_leading_formfeed) {
					strip_leading_formfeed = false;
					combined_text += line->substr(1);
				}
				else
					combined_text += *line;
				continue;
			}

			// 3. Now see if the two fragment candidates combine to a known word:
			if (not speller.isSpelledCorrectly(trailing_fragment_candidate + leading_fragment_candidate)) {
				if (strip_leading_formfeed) {
					strip_leading_formfeed = false;
					combined_text += line->substr(1);
				}
				else
					combined_text += *line;
				continue;
			}

			// 4. Now combine the two lines:
			combined_text += *line;
			combined_text.resize(combined_text.length() - 1); // Remove the trailing hypen.
			continue;
		}

		combined_text += '\n';
	}

	combined_text += lines.back();
	combined_text += '\n';

	return StringUtil::ReplaceString("\f", "\n\n", combined_text);
}


}// namespace TextUtil
