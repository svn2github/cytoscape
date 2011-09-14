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


/** \class  Blacklister
 *  \brief  Implements text blacklisting.
 */
class Blacklister {
	struct PatternInfo {
		size_t max_applicable_document_length_; // 0 for no limit.
		size_t max_document_match_prefix_;      // 0 for no limit.
		PerlCompatRegExp reg_exp_;
	public:
		/** \brief  Initialises a new PatternInfo object.
		 *  \param  max_applicable_document_length  How long a document has to be before stop considering this pattern to be applicable.  (0 means
		 *                                          no limit).
		 *  \param  pattern                         The pattern to match documents against.
		 *  \param  max_document_match_prefix       The maximum prefix length against which we match.  (0 means no limit).
		 */
		PatternInfo(const size_t max_applicable_document_length, const size_t max_document_match_prefix, const std::string &pattern)
			: max_applicable_document_length_(max_applicable_document_length), max_document_match_prefix_(max_document_match_prefix),
			  reg_exp_(pattern, PerlCompatRegExp::OPTIMIZE_FOR_MULTIPLE_USE) { }
	};

	std::list<PatternInfo> pattern_infos_;
public:
	/** \brief  Initialise a Blacklister object.
	 *  \param  blacklist_filename  The name of a file that must contain lines having three fields separated by colons.  The first field, an unsigned
	 *                              integer, indicates the maximum length to which a pattern applies.  0 means there is no limit.  The 2nd field, also
	 *                              an unsigned integer quantity, indicates the length of a documents prefix that we match against.  0 means the match
	 *                              will be againts the entire document.  The 3rd and final field must be a Perl compatible regular expression.
	 */
	explicit Blacklister(const std::string &blacklist_filename);

	/** Matches against the internal pattern list and returns "true" if any applies. */
	bool hasBeenBlacklisted(const std::string &document) const;
};


/** \brief  Checks whether a given string is probably ordinary text (including HTML).
 *  \param  possible_text  The string to test.
 *  \return True if it seems possible that "possible_text" is ordinary text, false otherwise.
 *  \note   Empty documents will not be considered to be ordinary text!
 */
bool IsOrdinaryText(const std::string &possible_text);


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


/** \brief  Encrypts some plain text using DES.
 *  \param  password  A key will be generated from this.
 *  \param  data      The data to encrypt.
 *  \note   Should the length of the to be encrypted data not be a multiple of 8 it will be zero-byte padded!
 *  \return The encrypted data.
 */
std::string Encrypt(const std::string &password, const std::string &data);


std::string Decrypt(const std::string &password, const std::string &data);


/** \brief  Cleans up a raw word by removing "odd" characters etc.
 *  \param  raw_word          The original "raw" word.
 *  \param  cleaned_up_words  The list of cleaned up words e.g. "right-handed" might turn into "right-handed",
 *                            "right" and "handed".  If there are hyphens between characters we return the
 *                            hypenated word at the front of the list.  Acceptable 1- and 2-letter words are
 *                            determined by a whitelist.  The passed list will be cleared on entry.
 *  \param  lowercase         If true then use lowercase characters. (DEFAULT = true)
 *  \return Returns true if we managed to find an interpretation of "raw_word" that can be construed to be a word.
 */
bool CleanUpWord(const std::string &raw_word, std::list<std::string> * cleaned_up_words, const bool lowercase = true);


/** Returns true if "word" is an English ordinal (0th, 1st, 2nd, 3rd, 4th and so on), else false. */
bool IsOrdinal(const std::string &word);


/** Returns true if "number_candidate" represents a valid floating point number, else false. */
bool IsANumber(const std::string &number_candidate);


/** \brief  Test a string for whether it could be a single word.
 *  \param  test_string  The string to test.
 *  \return True if "test_string" may be a single word, else false.
 *  \note   We reject empty strings, strings that contain digits and letters but are not ordinals, strings that contain
 *          nonalphanumeric characters and strings that contain more than 3 consecutive letters that are identical.
 */
bool IsPossiblyAWord(const std::string &test_string);


/** Uppercases the first character of "s" if it has an uppercase equivalent in the currently selected locale. */
std::string InitialCaps(const std::string &s);


/** \brief  Generates a string of comma-separated values.
 *  \param  values                      The list of values to be processed.
 *  \param  unconditionally_use_quotes  Put double quotes around all values.
 *  \return The comma-separated string containing all values.
 */
std::string GenerateCSV(const std::list<std::string> &values, const bool unconditionally_use_quotes = false);


/** \brief   Escape a single character.
 *  \param   ch  The character to escpae.
 *  \return  Hexidecimal escape sequence.
 *
 *  Returns a hexdecimal escape sequence starting with a backslash if "ch" is not printable or a tab, newline, carriage return or form
 *  feed, otherwise "ch" itself will be returned.
 */
std::string EscapeChar(const char ch);


/** \brief   Create the "pseudophrase" corresponfing to a phrase.
 *  \param   s  The phrase to be converted to a pseudophrase.
 *  \return  The pseudophrase.
 *
 *  A "pseudophrase" is a concept borrowed from Kea 4.0 (published by Medelyan and Witten in EFITAWCCA2005).  It is a very simple representation of a
 *  string found by extracting every word in the phrase, eliminating stopwords, stemming the remaing words, sorting them alphabetically, and concatinating
 *  them to form a string.
 */
std::string Pseudophrase(const std::string &s);


/** \brief  Checks for proper nesting of certain pairs of characters like opening and closing parenthesis etc.
 *  \param  s             The string to check.
 *  \param  opening_char  The opening character e.g. '['.
 *  \param  closing_char  The closing character e.g. ']'.
 */
bool HasBalancedOpeningAndClosingChars(const std::string &s, const char opening_char, const char closing_char);


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


const GNU_HASH_SET<std::string> &GetNoiseWords();


class SentenceAndWords {
	std::string sentence_;
	std::vector<std::string> words_;
	unsigned index_;
	mutable size_t word_char_count_; // Uses lazy evaluation.
public:
	typedef std::vector<std::string>::const_iterator const_iterator;
public:
	SentenceAndWords(const std::string &sentence, const std::vector<std::string> &words, const unsigned index)
		: sentence_(sentence), words_(words), index_(index), word_char_count_(0) { }

	size_t getWordCount() const { return words_.size(); }
	const std::string &getSentence() const { return sentence_; }
	unsigned getIndex() const { return index_; }

	/** \brief  Return the sum of the character counts of all the words. */
	size_t getWordCharCount() const;

	/** \brief  Return count of all the characters in the sentence. */
	size_t getSentenceCharCount() const { return sentence_.length(); }

	bool operator<(const SentenceAndWords &rhs) const { return index_ < rhs.index_; }
	void toLowercase();

	const_iterator begin() const { return words_.begin(); }
	const_iterator end() const { return words_.end(); }
};


enum TextSummarizerOptions {
	PICK_RANDOM_SENTENCES        = 1u << 0,
	USE_TRIVIAL_SENTENCE_LINKING = 1u << 1,
	IGNORE_NOISE_WORDS           = 1u << 2,
	USE_GLOBAL_TF_IDF_SCALING    = 1u << 3,
	USE_THESAURUS                = 1u << 4,
	USE_STEMMING                 = 1u << 5,
	USE_WORD_FREQUENCIES         = 1u << 6,
	USE_PAGE_RANK                = 1u << 7,
};


const unsigned BEST_TEXT_SUMMARIZER_OPTIONS(USE_STEMMING | USE_PAGE_RANK);


std::string TextSummarizerOptionsToString(const unsigned text_summarizer_options);


/** \brief  Implements a text summarizer based on "Graph-based Ranking Algorithms for Sentence Extraction, Applied to Text Summarization" and the PHITS
 *          or PageRank algorithms.
 *
 * Basically "sentences" is being converted into an undirected graph where edges are implied by words co-occurring in sentences.  Then a graph analysis is
 * being performed using PHITS and finally the sentences are being ranked by their authority scores as determined by PHITS.  This ranking will be returned
 * in "ranked_sentences."  Please note that duplicate sentences have been ignored in the result list.
 *
 * \param  sentences                The list of input sentences to be ranked.
 * \param  ranked_sentences         The output list of ranked sentences.  (The list elements point to members of "sentences".)
 * \param  text_summarizer_options  Whether to include "noise words" in the analysis or not, etc.
 * \param  min_sentence_length      The length in words of the shortest sentence to include in the analysis.
 * \param  max_sentence_count       Do not use more than this many sentences.
 * \param  aliases                  Maps from sentences that were thrown out to the aliases that were retained.  Sentences that were thrown out for other
 *                                  reasons then being dupes after massaging are not contained in this map.
 */
void GraphBasedTextSummarizer(const std::vector<SentenceAndWords> &sentences, std::vector<const SentenceAndWords *> * const ranked_sentences,
			      unsigned text_summarizer_options = BEST_TEXT_SUMMARIZER_OPTIONS, const unsigned min_sentence_length = 3,
			      const unsigned max_sentence_count = 1000, GNU_HASH_MAP<std::string, std::string> * const aliases = NULL);


/** \brief  Extracts hopefully sentences that are highly descriptive of a plain text or HTML document's contents.
 *  \param  document                 The document to summarise.
 *  \param  min_no_of_words          The shortest number of words in a "sentence" before we consider it for inclusion in our result set.
 *  \param  text_summary             The extracted sentences.
 *  \param  max_sentence_count       Do not use more than this many sentences.
 *  \param  text_summarizer_options  Whether to include "noise words" in the analysis or not, etc.
 *  \param  max_sentence_overlap     If in [0.0,1.0), GenerateLowRedundancyText() will be used with "max_overlap."
 */
void SummarizeText(const std::string &document, const unsigned min_no_of_words, std::string * const text_summary, const unsigned max_sentence_count = 1000,
		   unsigned text_summarizer_options = BEST_TEXT_SUMMARIZER_OPTIONS, const double max_sentence_overlap = 1.0);


/** \brief  Splits a sentence into individual words.
 *  \param  sentence         The text to be processed.
 *  \param  words            The resulting words in the order in which they occurred in the "sentence".
 *  \param  stem             If true, we stem words before returning them.
 *  \param  trim_chars       Characters to trim off both ends of a "word" candidate.
 *  \param  force_lowercase  If true, lowercases the returned words.
 */
void ExtractWords(const std::string &sentence, std::vector<std::string> * const words, const bool stem = false,
		  const char * const trim_chars = " \n\t\r\"\\()[]{}&-.,;:*'!+/%#", const bool force_lowercase = true);


/** Returns the number of words in "text". */
unsigned CountWords(const std::string &text);


/** Converts PostScript documents to Latin-9 text. */
std::string PostScriptToText(const std::string &postscript);


/** \brief  Convertes Pdf documents to text.
 *  \param  pdf_data                         The PDF document to convert.
 *  \param  attempt_to_combine_broken_words  If true, an attempt will be made to combine words broken at line ends.
 *  \param  remove_headers_and_footers       If true, and attempt will be made to remove repeated headers and footers, including page numbers.  In rare
 *                                           cases this can lead to some numbers being removed that shouldn't have been removed.
 */
std::string PdfToText(const std::string &pdf_data, const bool attempt_to_combine_broken_words = true, const bool remove_headers_and_footers = false);


/** Convertes M$ Word documents to text in Latin-9 encoding. */
std::string WordToText(const std::string &word_data);


/** Convertes Pdf documents to HTML. */
std::string PdfToHtml(const std::string &pdf_data);


/** Attempts to remove hyphens at line-ends. */
std::string Dehyphenate(const std::string &hyphentated_text, const bool suppress_empty_lines);


/** Returns true if at least 50 percent of the characters are Ascii Letters. */
bool ContainsAtLeast50PercentAsciiLetters(const std::string &text);


/** Converts a PDF or PostScript document to plain text. */
std::string PdfOrPostScriptToText(const std::string &pdf_or_postscript, const bool attempt_to_combine_broken_words_in_pdfs = true,
				  const bool attempt_to_remove_headers_and_footers_from_pdfs = false);


class TextSection {
	std::string header_, contents_;
	unsigned section_no_;
public:
	TextSection() { }
	TextSection(const std::string &header, const std::string &contents, const size_t section_no)
		: header_(header), contents_(contents), section_no_(section_no) { }
	void clear() { header_.clear(); contents_.clear(); }
	bool empty() const { return header_.empty() and contents_.empty(); }
	const std::string &getHeader() const { return header_; }
	const std::string &getContents() const { return contents_; }

	/** \brief  Returns the section number or position of the section within the overall document. */
	unsigned getSectionNumber() const { return section_no_; }
};


class TextSections {
	std::vector<TextSection> text_sections_;
public:
	typedef std::vector<TextSection>::const_iterator const_iterator;
	static const unsigned minimum_valid_section_length_ = 80; // Don't consider less than this to be a valid text section
public:
	TextSections() { }
	explicit TextSections(const std::string &plain_text, const bool omit_bad_sections = true);
	size_t size() const { return text_sections_.size(); }
	bool empty() const { return text_sections_.empty(); }
	void clear() { text_sections_.clear(); }

	/** An optimisation hack.  Swaps the contents of the current object with those of rhs. */
	void swap(TextSections &rhs) { text_sections_.swap(rhs.text_sections_); }

	void add(const std::string &header, const std::string &contents, const unsigned section_no);

	/** \brief  Attempts to retrieve the contents of a section, given a section name.
	 *  \param  name        A fuzzy case-insensitive match will be attempted against this section name.
	 *  \param  contents    Where to store the contents for a matched section name.
	 *  \param  section_no  Indicates the relative order of the section in the original document.
	 *  \return True if we found the named section otherwise false.
	 */
	bool getNamedSection(const std::string &name, std::string * const contents, unsigned * const section_no) const;

	const std::string &getFirstLargeSection(unsigned * const section_no, const unsigned min_no_of_characters_in_section = 500) const;

	const_iterator begin() const { return text_sections_.begin(); }
	const_iterator end() const { return text_sections_.end(); }
};


enum ConversionProgram { SYSTEM_PDFTOTEXT, REXA_PSTOTEXT, NO_CONVERSION_DOCUMENT_IS_PLAIN_TEXT };


/** \brief  Returns a list of sections with associated headers for "pdf_or_postscript".
 *  \param  pdf_or_postscript                            The PDF or PostScript document.
 *  \param  conversion_program                           Specifies which program to use to convert the document to plain text.  Please note that
 *                                                       if the MIME type of the document is PostScript, we unconditionally use REXA_PSTOTEXT.
 *  \param  text_sections                                The extracted text sections.
 *  \param  attempt_to_combine_broken_words_in_pdf_docs  If true, words at the ends of lines that have been broken will be attempted to be recombined.
 *  \param  omit_bad_sections                            If "true", we suppress any sections that have less than 50% ASCII letters.
 */
void ExtractTextSections(const std::string &pdf_or_postscript, const ConversionProgram conversion_program,
			 TextSections * const text_sections, const bool attempt_to_combine_broken_words_in_pdf_docs = true,
			 const bool omit_bad_sections = true);


/** \brief   Returns an iterator into "words" indicating the likely start of a person's name.  If no such position could be determined it
 *           returns words.end().
 *  \param   words  A vector of strings containing words from which you wish to find the start of a person's name.
 *  \return  An iterator into words indicating the likely start of a person's name.
 */
std::vector<std::string>::const_iterator GetLikelyFullNameStart(const std::vector<std::string> &words);


/** Returns true if "s" contains at least one character for which isprint() returns false.  Otherwise we return false. */
bool ContainsNonprintableChars(const std::string &s);


/** Runs "gzipped_data" through the gunzip command-line utility to produce "ungzipped_data".  Returns "true" on success
    and "false" on failure. */
bool Gunzip(const std::string &gzipped_data, std::string * const ungzipped_data);


/** Uses a heuristic to fairly reliably guess whether "possible_email_address" may be a valid email address or not. */
bool IsValidEmailAddress(const std::string &possible_email_address);


/** Returns true if "possible_abbrev" is a US postal abbreviation, false otherwise. */
bool IsUsPostalAbbreviation(const std::string &possible_abbrev);


struct WordAndPhraseInfo {
	std::string word_or_phrase_;
	unsigned occurrence_count_;
public:
	WordAndPhraseInfo(): occurrence_count_(0) { }
	WordAndPhraseInfo(const std::string &word_or_phrase, const unsigned occurrence_count)
		: word_or_phrase_(word_or_phrase), occurrence_count_(occurrence_count) { }
};


enum GetCapsPhrasesBehaviour { DUMB                                     = 0,
			       EXCLUDE_SHORT_SUB_PHARSES                = 1u << 0,
			       EXCLUDE_PHRASES_STARTING_WITH_AN_INITIAL = 1u << 1,
};


/** \brief  Attempts to extract 2, 3 and 4 words caps phrases from "text".
 *  \param  text                        The text to parse.
 *  \param  caps_phrases                The extracted phrases.
 *  \param  min_phrase_frequency        The minimum number of times a phrase has to occur in "text" in order to be returned.
 *  \param  get_caps_phrases_behaviour  A or'ed together set of flags of type GetCapsPhrasesBehaviour.
 *  \note   The extracted phrases include the following forms:
 *               1) phrases consisting of 2 or 3 words starting with a capital letter,
 *               2) 3 or 4 word phrases with the word "of" in the middle and the other 2 words being caps words,
 *               3) 2, 3 or 4 caps words followed by a comma followed by a state, possession or country.
 */
void GetCapsPhrases(const std::string &text, std::vector<std::string> * const caps_phrases, const unsigned min_phrase_frequency = 1,
		    const int get_caps_phrases_behaviour = DUMB);


enum WordAndPhraseExtractionOptions {
	PHRASES_AND_INDIVIDUAL_WORDS      = 1u << 0u, ///< Extract individual words and phrases.
	PHRASES_ONLY                      = 1u << 1u, ///< Only extract multi-word, i.e. proper phrases.
	INDIVIDUAL_WORDS_ONLY             = 1u << 2u, ///< Only extract individual words.
	CAPS_PHRASES_AND_INDIVIDUAL_WORDS = 1u << 3u  ///< Extract individual words and short, capitalized phrases.
};


void GetWordsAndCapsPhrases(const std::string &text, std::vector<WordAndPhraseInfo> * const words_and_phrases,
			    const WordAndPhraseExtractionOptions options = PHRASES_AND_INDIVIDUAL_WORDS,
			    const unsigned min_phrase_or_word_frequency = 1);


enum LineWrapOptions {
	REMOVE_MULTIPLE_BLANK_LINES   = 0x2,
	CONVERT_TABS_TO_SPACES        = 0x4,  ///< Change each tab to 1 space.
	REMOVE_LEADING_WHITESPACE     = 0x8,
	REMOVE_TRAILING_WHITESPACE    = 0x10,
	REMOVE_MULTIPLE_WHITESPACE    = 0x20,
	DEFAULT_LINE_WRAP_OPTIONS     = REMOVE_TRAILING_WHITESPACE | REMOVE_LEADING_WHITESPACE | REMOVE_MULTIPLE_BLANK_LINES | CONVERT_TABS_TO_SPACES
};


/** brief  Formats a string to specified widths and indents
 *  param  max_width         The maximim width any line can be.
 *  param  indent            1st line indented this many columns.
 *  param  hanging_indent    2nd and following lines indent this much.
 *  param  whitespace        characters which are considered whitespace.
 *
 *  Given a block of text, this function will attempt to tidy it up. It will join all text that is not broken by two newlines into single paragraphs.  It
 *  will put leading_indent spaces in front of the first line of each paragraph and hanging_indent into each line except the first.
*/
std::string LineWrap(const std::string &source_string, const unsigned max_width = 80, const LineWrapOptions options = DEFAULT_LINE_WRAP_OPTIONS,
		     const unsigned leading_indent = 0, const unsigned hanging_indent = 0, const std::string &whitespace = " \t");


/** Returns the number of letters in "text".  Caution: This function is locale dependent! */
unsigned LetterCount(const std::string &text);


enum AboutnessTermRelevance { NO_RELEVANCE = -1, VERY_LOW_SCORE, LOW_SCORE, MEDIUM_SCORE, HIGH_SCORE, VERY_HIGH_SCORE };


std::string AboutnessTermRelevanceToString(const AboutnessTermRelevance aboutness_term_relevance);


struct AboutnessTermAndRelevance {
	std::string term_; // e.g. "abstract" etc.
	AboutnessTermRelevance relevance_;
public:
	AboutnessTermAndRelevance(): relevance_(VERY_LOW_SCORE) { }
	AboutnessTermAndRelevance(const std::string &term, const AboutnessTermRelevance relevance): term_(term), relevance_(relevance) { }
};


/** Gets a vector of well balanced aboutness terms for uses as a default if you don't want to provide your own. */
const std::vector<AboutnessTermAndRelevance> *GetDefaultAboutnessTermsAndRelevance();


/** \brief   Extracts words and short phrases from some text.
 *  \param   text                          The text to be processed.
 *  \param   words_and_phrases             The extracted words and phrases and their occurrence counts.
 *  \param   options                       What to extract.
 *  \param   max_phrase_length             Up to how many words should be in an extracted phrase.
 *  \param   min_phrase_or_word_frequency  Only return words of phrases that occur at least this many times.
 *  \warning If options is CAPS_PHRASES_AND_INDIVIDUAL_WORDS, the "max_phrase_length" parameter will be ignored!
 *  \note    The extracted words and phrases will be lowercased!
 */
void ExtractPhrases(const std::string &text, std::vector<WordAndPhraseInfo> * const words_and_phrases,
		    const WordAndPhraseExtractionOptions options = PHRASES_AND_INDIVIDUAL_WORDS, const unsigned max_phrase_length = 5,
		    const unsigned min_phrase_or_word_frequency = 1);


/** \brief  Converts various "text" document formats to plain text.
 *  \param  document    The document to convert to plain text.
 *  \param  media_type  The media type of the document.  If the argument is the empty string the media type will be dynamically determined.
 *  \param  plain_text  On success, the converted plain text.
 *  \return True if the conversion succeeded, false otherwise.
 *  \note   Currently supported formats are text/plain, text/(x)html, application/pdf, application/postscript, and application/msword.
 */
bool ConvertToPlainText(const std::string &document, const std::string &media_type, std::string * const plain_text);


/** \brief  Converts various "text" document formats to plain text.
 *  \param  document    The document to convert to plain text.
 *  \param  plain_text  On success, the converted plain text.
 *  \return True if the conversion succeeded, false otherwise.
 *  \note   Currently supported formats are text/plain, text/(x)html, application/pdf, application/postscript, and application/msword.
 */
inline bool ConvertToPlainText(const std::string &document, std::string * const plain_text)
{
	return ConvertToPlainText(document, "", plain_text);
}


/** Returns true is "month_candidate" is an English month name or an abbreviation for an English month name, else returns false. */
bool IsEnglishMonthOrMonthAbbrev(const std::string &month_candidate);


/** Returns true is "day_candidate" is an English day name or an abbreviation for an English day name, else returns false. */
bool IsEnglishDayOrDayAbbrev(const std::string &day_candidate);


enum TokenType { TT_LOWERCASE_WORD, TT_UPPERCASE_WORD, TT_INITIAL_CAPS_WORD, TT_CAPS_CHAR_AND_PERIOD, TT_INTEGER, TT_COMMA, TT_SEMICOLON, TT_PERIOD,
		 TT_EXCLAMATION_POINT, TT_QUESTION_MARK, TT_OPEN_PAREN, TT_CLOSE_PAREN, TT_EMAIL_ADDRESS, TT_INITIAL_CAPS_ONE, TT_INSTITUTION_INDICATOR,
		 TT_COMMON_ENGLISH_WORD, TT_WHITESPACE, TT_ORDINAL, TT_PREPOSITION, TT_CAPS_PREPOSITION, TT_POSSIBLE_RECENT_YEAR,
		 TT_ENGLISH_MONTH_OR_MONTH_ABBREV, TT_ENGLISH_DAY_OR_DAY_ABBREV, TT_FIRST_NAME, TT_LAST_NAME, TT_FIRST_NAME_OR_REGULAR_WORD,
		 TT_LAST_NAME_OR_REGULAR_WORD, TT_ONE, TT_LINEEND, TT_STATE_OR_POSSESSION, TT_COMMA_AND_US_STATE_ABBREV, TT_DETERMINER,
		 TT_INITIAL_CAPS_DETERMINER, TT_DETERMINER_OR_PRONOUN, TT_INITIAL_CAPS_DETERMINER_OR_PRONOUN, TT_CONJUNCTION, TT_CAPS_CONJUNCTION,
		 TT_PRONOUN, TT_CAPS_PRONOUN, TT_MISCELLANEOUS, TT_ABSTRACT, TT_CITY_AND_STATE, TT_POSSIBLE_US_ZIP_CODE, TT_CARDINALITY };
std::string TokenTypeToString(const TokenType token_type);
TokenType GetTokenType(const std::string &text);


struct StringAndTokenType {
	std::string string_;
	TokenType token_type_;
public:
	StringAndTokenType(): token_type_(TT_MISCELLANEOUS) { }
	StringAndTokenType(const std::string &string, const TokenType token_type): string_(string), token_type_(token_type) { }
};


void GetTokens(const std::string &text, std::vector<StringAndTokenType> * const strings_and_token_types);


inline unsigned MakeComboToken(const TokenType &token_type1, const TokenType &token_type2)
{ return (static_cast<uint32_t>(token_type1) << 16u) | static_cast<uint32_t>(token_type2); }


/** \brief A flavour of MakeComboToken that allows StringAndComboTokenType::DO_NOT_CARE as a second argument. */
inline unsigned MakeComboToken(const uint32_t &token_type1, const uint32_t &token_type2)
{ return (token_type1 << 16u) | token_type2; }


inline TokenType GetFirstToken(const unsigned combo_token) { return static_cast<TokenType>(combo_token >> 16u); }
inline TokenType GetSecondToken(const unsigned combo_token) { return static_cast<TokenType>(combo_token & 0xFFFFu); }
std::string ComboTokenTypeToString(const unsigned combo_token);


struct StringAndComboTokenType {
	std::string string_;
	uint32_t combo_token_;
public:
	static const uint32_t DO_NOT_CARE;
public:
	StringAndComboTokenType(): combo_token_(0) { }
	StringAndComboTokenType(const std::string &string, const TokenType &token_type1, const TokenType &token_type2)
		: string_(string), combo_token_(MakeComboToken(token_type1, token_type2)) { }

	/** \brief  Returns a string representation of a StringAndComboTokenType.
	 *  \param  tokens_only  If true, omits the "string" part from the returned representation.
	 */
	std::string toString(const bool tokens_only = false) const;
};


enum ComboTokenMode {
	USE_ALL_TOKENS,             ///< Use all TokenType tokens.
	DO_NOT_USE_WHITESPACE,      ///< Use all TokenType tokens except for TT_WHITESPACE and TT_LINEEND.
	ONLY_USE_LINEEND_WHITESPACE ///< Use all TokenType tokens and TT_WHITESPACE.
};


std::string ComboTokenModeToString(const ComboTokenMode mode);


class  ComboTokenEnumerator {
	ComboTokenMode mode_;
	uint32_t last_token_;
	static const uint32_t ITERATOR_END_TOKEN = 0xFFFFFFFFu;
public:
	explicit ComboTokenEnumerator(const ComboTokenMode mode): mode_(mode), last_token_(0) { }

	/** \return  The number of possible combo tokens. */
	size_t size() const { return TT_CARDINALITY * (TT_CARDINALITY + 1/* for DO_NOT_CARE */); }

	/** \brief   Allows iteration over all possible combo tokens.
	 *  \return  True if we were able to still return a valid combo token.  False when we have gone past the end of the list of possible tokens.
	 */
	bool getNextToken(uint32_t * const token);

	/** \brief  Resets the enumerator to start at the first token. */
	void restart() { last_token_ = 0; }
};


void GetComboTokens(const std::string &text, std::vector<StringAndComboTokenType> * const strings_and_combo_tokens, const ComboTokenMode mode);


void GetTextChunks(const std::string &text, std::vector<std::string> * const chunks, const bool lowercase = false);


bool IsCommonAmericanMaleFirstName(const std::string &first_name_candidate, double * const frequency, const bool casefold = false);
inline bool IsCommonAmericanMaleFirstName(const std::string &first_name_candidate, const bool casefold = false)
{
	return IsCommonAmericanMaleFirstName(first_name_candidate, NULL, casefold);
}


bool IsCommonAmericanFemaleFirstName(const std::string &first_name_candidate, double * const frequency, const bool casefold = false);
inline bool IsCommonAmericanFemaleFirstName(const std::string &first_name_candidate, const bool casefold = false)
{
	return IsCommonAmericanFemaleFirstName(first_name_candidate, NULL, casefold);
}


inline bool IsCommonAmericanFirstName(const std::string &first_name_candidate, double * const frequency, const bool casefold = false)
{
	return IsCommonAmericanMaleFirstName(first_name_candidate, frequency, casefold)
	       or IsCommonAmericanFemaleFirstName(first_name_candidate, frequency, casefold);
}


inline bool IsCommonAmericanFirstName(const std::string &first_name_candidate, const bool casefold = false)
{
	return IsCommonAmericanMaleFirstName(first_name_candidate, NULL, casefold)
	       or IsCommonAmericanFemaleFirstName(first_name_candidate, NULL, casefold);
}


bool IsCommonAmericanSurname(const std::string &first_name_candidate, double * const frequency, const bool casefold = false);


inline bool IsCommonAmericanSurname(const std::string &surname_candidate, const bool casefold = false)
{
	return IsCommonAmericanSurname(surname_candidate, NULL, casefold);
}


bool IsCommonFrenchMaleFirstName(const std::string &first_name_candidate, const bool casefold = false);


bool IsCommonFrenchFemaleFirstName(const std::string &first_name_candidate, const bool casefold = false);


inline bool IsCommonFrenchFirstName(const std::string &first_name_candidate, const bool casefold = false)
{
	return IsCommonFrenchMaleFirstName(first_name_candidate, casefold)
	       or IsCommonFrenchFemaleFirstName(first_name_candidate, casefold);
}


bool IsCommonFrenchSurname(const std::string &first_name_candidate, const bool casefold = false);


bool IsCommonChineseSurname(const std::string &first_name_candidate, const bool casefold = false);


inline bool IsCommonFirstName(const std::string &first_name_candidate, const bool casefold = false)
{
        return IsCommonAmericanSurname(first_name_candidate, casefold) or IsCommonFrenchFirstName(first_name_candidate, casefold);
}


inline bool IsCommonSurname(const std::string &surname_candidate, const bool casefold = false)
{
        return IsCommonAmericanSurname(surname_candidate, casefold) or IsCommonFrenchSurname(surname_candidate, casefold)
	       or IsCommonChineseSurname(surname_candidate, casefold);
}


enum SimpleTokenType { ST_WHITESPACE, ST_NON_ALNUM_CHAR, ST_ALPHA, ST_INTEGER, ST_OTHER };
struct SimpleToken {
	std::string value_;
	SimpleTokenType type_;
public:
	SimpleToken(const std::string &value, const SimpleTokenType &type): value_(value), type_(type) { }
};
class SimpleTokenStream {
	std::vector<SimpleToken> simple_tokens_;
public:
	typedef std::vector<SimpleToken>::const_iterator const_iterator;
public:
	size_t size() const { return simple_tokens_.size(); }
	void clear() { simple_tokens_.clear(); }

	/** \brief  Compares two token streams for equality.
	 *  \note   All whitespace is being treated as equivalent.
	 *  \param  rhs  The other (*this, the current object is "lhs") token stream in the comparison.
	 *  \return True if both token streams are equivalent, else false.
	 */
	bool equal(const SimpleTokenStream &rhs) const;

	const_iterator begin() const { return simple_tokens_.begin(); }
	const_iterator end() const { return simple_tokens_.end(); }
	void push_back(const SimpleToken &new_token) { simple_tokens_.push_back(new_token); }
};


/** \brief  Converts a text `stream' into a token `stream'.
 *  \param  text                  The text to parse.
 *  \param  simple_token_stream   The result of the parse.
 *  \param  normalize_whitespace  Whether or not to convert blocks of whitespace into single whitespace characters. For example \\n\\n\\n would become \\n
 *                                and "    " would become " ".
 *  \param  max_stream_size       The maxiumum length of the generated token stream.
 *  \note   This function would typically be used for a more sophisticated parser as a low-level building block.
 */
void CreateSimpleTokenStream(const std::string &text, SimpleTokenStream * const simple_token_stream, const bool normalize_whitespace = false,
			     const unsigned max_stream_size = UINT_MAX);


std::string SimpleTokenTypeToString(const SimpleTokenType simple_token_type);


/** \brief  Attempts to find a 2-letter US state or possession abbreviation.
 *  \param  state_or_possession_candidate  The candidate for a fully spelled-out US state or posession name.
 *  \return If a match was found, the 2-letter state or possession abbreviation, else the empty string.
 */
std::string GetStateOrPossessionAbbreviation(const std::string &state_or_possession_candidate);


/** \brief  Determines whether a prefix of up to 5 words constitute a U.S. state, country or U.S. possession.
 *  \param  word1  The 1st word to consider.
 *  \param  word2  The 1st word to consider.
 *  \param  word3  The 1st word to consider.
 *  \param  word4  The 1st word to consider.
 *  \param  word5  The 1st word to consider.
 *  \return The number of words that name a state or possession.
 */
unsigned GetStateOrPossessionWordCount(const std::string &word1, const std::string &word2, const std::string &word3,
				       const std::string &word4, const std::string &word5);


enum DocumentPart { DP_START, DP_INSTITUTION, DP_TITLE, DP_AUTHORS, DP_EMAIL, DP_KEYWORDS, DP_DATE, DP_ADDRESS, DP_ABSTRACT, DP_OTHER,
		    DP_END, DP_CARDINALITY };
std::string DocumentPartToString(const DocumentPart document_part);


struct StringAndDocumentPart {
	std::string string_;
	DocumentPart document_part_;
public:
	StringAndDocumentPart(): document_part_(DP_START) { }
	StringAndDocumentPart(const std::string &string, const DocumentPart document_part)
		: string_(string), document_part_(document_part) { }
	std::string toString() const;
};


/** \brief  Labels a document's text with part-of-document tags.
 *  \param  document                    The document to label.
 *  \param  strings_and_document_parts  The document bits with labels.
 *  \param  max_token_stream_length     If non-zero, we only keep a prefix of the tokenized document.
 */
void LabelDocumentParts(const std::string &document, std::vector<StringAndDocumentPart> * const strings_and_document_parts,
			const unsigned max_token_stream_length = 10000);


/** \brief  Labels a document's text with part-of-document tags.
 *  \param  document                    The document to label.
 *  \param  strings_and_document_parts  The document bits with labels.
 *  \param  max_token_stream_length     If non-zero, we only keep a prefix of the tokenized document.
 *  \param  mode                        Indicates which tokens should be generated.
 */
void LabelDocumentPartsUsingComboTokens(const std::string &document, std::vector<StringAndDocumentPart> * const strings_and_document_parts,
					const unsigned max_token_stream_length = 10000, const ComboTokenMode mode = USE_ALL_TOKENS);


/** \brief  Estimate the number of vertical rows required to display some text.
 *  \param  text       The text that we are querying.
 *  \param  delimiter  This character determines when a row is required for display.
 *  \param  limit      In addition to delimiters, lines longer than this imply a wrap or multiple wraps, which requires addition rows of display.
 *  \note   This is useful in trying to determine text height. Simply counting newlines doesn't always do the trick because some display contexts wrap
 *          lines longer than their width so lines that are longer than width also imply an additional row of height, as many rows as is necessary
 *          to contain their entire height. The total height required will be the number of delimiters in the text plus the wraps that were required
 *          because of extra long text.
 *  \return Number of rows that it will require to display this text.
 */
unsigned EstimateTextHeight(const std::string &text, const unsigned limit, const char delimiter = '\n');


/** \brief  Counts the 50 most common `words' of the British National Corpus (BNC).
 *  \param  plain_text             The text that is to be analyzed.
 *  \param  words_and_frequencies  A map from words to frequencies as found in "plain_text."
 *  \note   For a table of the 50 most common BNC `words' see http://acl.ldc.upenn.edu/C/C00/C00-2117.pdf.
 */
void CommonWordFrequencyCounter(const std::string &plain_text, GNU_HASH_MAP<std::string, unsigned> * const words_and_frequencies);


/** \brief  Computes a score for sentence similarity as described in http://www.cs.unt.edu/~rada/papers/mihalcea.emnlp04.pdf
 *  \param  first_sentence      S_i in the referenced paper.
 *  \param  second_sentence     S_j in the referenced paper.
 *  \return   The score for similarity. The more words the sentences have in common the higher the score.
 *  \note   Also, the score is normalized by the length of the two sentences.
 */
double SentenceSimilarityScore(const std::string &first_sentence, const std::string &second_sentence);


/** \return  True if "adverb_candidate" is likely to be an adverb, otherwise false. */
bool IsPossibleAdverb(const std::string &adverb_candidate);


/** \return  True if "present_participle_candidate" is likely to be an present participle, otherwise false. */
bool IsPossiblePresentParticiple(const std::string &present_participle_candidate);


enum CaptionProcessing { NO_PROCESSING, TRIM_LEADERS_AND_TRAILERS, KEEP_FIRST_SENTENCE_ONLY };


/** \brief  Attempts to extract figure and table captions from a document.
 *  \param  plain_text                 The text of the document.
 *  \param  trim_leaders_and_trailers  If true, remove leading "Fig." "Figure", etc.
 *  \param  captions                   The extracted captions.
 */
void FigureCaptionExtractor(const std::string &plain_text, const CaptionProcessing caption_processing, std::vector<std::string> * const captions);


/** \brief  Attempts to guess as to which words are acronyms and extracts them from a  plain text document.
 *  \param  plain_text                      The text from which we're extracting potential acronyms from.
 *  \param  acronyms_and_occurrence_counts  Acronym candidates and their corresponding occurrence counts.  Sorted in order of decreasing occurrence counts.
 */
void AcronymFinder(const std::string &plain_text, std::vector< std::pair<std::string, unsigned> > * const acronyms_and_occurrence_counts);


/** \brief  Returns an approximation to the number of syllables in "word."
 *  \param  word                            The word whose syllables we'd like to count.
 *  \param  use_table_lookup_when_possible  If true, a table of words read in from a disk file will be used for exact syllable counts for words found in
 *                                          the table.
 *  \note   The algorithm has been tweaked for American Enlish and is off by 1 for about 10% to 15% of the words in /usr/dict/words.  But, it requires
 *          no table lookups.
 */
unsigned GetApproximateSyllableCount(const std::string &word, const bool use_table_lookup_when_possible = true);


/** \brief  Returns true, if "word" contains at least one of "aeiouy" or "AEIOUY", else returns false. */
bool ContainsVowel(const std::string &word);


/** \brief  Returns the summary-level LCS F-measure.
 *  \note   This is also known as ROUGE-L.
 *  \param  reference_text      A typically human generated reference summary.
 *  \param  test_text           A typically machine generated test summary.
 *  \param  beta                A parameter of the F-measure, if set to a large number P_lcs will be returned.
 *  \param  stem                If true, words will be stemmed first before the LCS evaluations take place.
 *  \param  ignore_noise_words  If true, ignore very common English words during the calculation of this measure.
 *  \note   The summary-level LCS F-measure and how to calculate it are described in "ROUGE: A Package for Automatic Evaluation of Summaries" by Chin-Yew
 *          Lin.
 */
double RougeLTextOverlap(const std::string &reference_text, const std::string &test_text, const double &beta = 1.0, const bool stem = false,
			 const bool ignore_noise_words = true);


/** \brief  Returns the summary-level LCS F-measure.
 *  \note   This is also known as ROUGE-L.
 *  \param  reference_text      A typically human generated reference summary.
 *  \param  test_text           A typically machine generated test summary.
 *  \param  max_n_gram_length   N-grams used for comparison will be up to this long.
 *  \param  ignore_noise_words  If true, ignore very common English words during the calculation of this measure.
 *  \param  stem                If true, words will be stemmed first before the LCS evaluations take place.
 *  \note   The ROUGE-N measure and how to calculate it are described in "ROUGE: A Package for Automatic Evaluation of Summaries" by Chin-Yew Lin.
 */
double RougeNTextOverlap(const std::string &reference_text, const std::string &test_text, const unsigned max_n_gram_length = 8,
			 const bool stem = false, const bool ignore_noise_words = true);


enum TextOverlapMethod { ROUGE_L, ROUGE_N };
std::string TextOverlapMethodToString(const TextOverlapMethod method);


double CalcTextOverlap(const TextOverlapMethod method, const std::string &reference_text, const std::string &test_text, const double &beta = 1.0,
		       const unsigned max_n_gram_length = 8, const bool stem = false, const bool ignore_noise_words = true);


/** \brief  Generates a low-word-redundancy summary of ranked sentences.
 *  \param  sentences             List of ranked sentence references.  The idea is that the first entry is the most desirable and so on.
 *  \param  min_no_of_words       The minimum number of words that are supposed to be in the returned summary.
 *  \param  max_sentence_overlap  A number between 0.0 and 1.0.  Numbers closer to 1.0 imply more tolerance towards redundant sentences.
 *  \param  text                  Output summarized text.
 *  \param  stem                  If true, use stemming before comparing text.
 *  \note   The strategy taken is to keep adding sentences to "text" as long as newly added sentences have no more than "max_overlap" with previously
 *          added sentences.  "Noise words" are being ignored in the overlap calculation.  Finally selected sentences are ordered according to their
 *          increasing indices as specified by the "index_" data member of struct SentenceAndWords.
 */
void GenerateLowRedundancyText(const std::vector<const SentenceAndWords *> &sentences, const unsigned min_no_of_words, const double max_sentence_overlap,
			       std::string * const text, const bool stem = true);


/** \brief  Counts how often "needle" occurs in "haystack."
 *  \param  needle                The string whose occurences we'd like to count.
 *  \param  haystack              The string that will be scanned.
 *  \param  case_sensitive        If true, we don't take differences in capitalisation into account.
 *  \param  normalise_whitespace  If true, all whitespace sequences in "haystack" will be replaced by a single space before scanning/counting begins.
 */
unsigned CountOccurrences(const std::string &needle, const std::string &haystack, const bool case_sensitive = false,
			  const bool normalise_whitespace = true);


/** \brief  Determines the percentages of uppercase, lowercase and other characters in "text."
 *  \note   The counts can depend on the locale since the notion of what constitutes an uppercase or lowercase letter is locale dependent!
 */
void GetCaseMix(const std::string &text, double * const percent_upper, double * const percent_lower, double * const percent_other);


/** \brief  Determines a longest common substring of two strings.
 *  \note   Execution time is proprotional to the square of the length of "s1" and linear in the length of "s2."  This implies that, when dealing with
 *          long strings, the shorter one should be passed in as "s1."
*/
void GetLongestCommonSubstring(const std::string &s1, const std::string &s2, std::string * const longest_common_substring);


/** \brief  Uses several heuristics to split a plain text into sentences.
 *  \param  document                         The text to process.
 *  \param  sentences                        The result of the splitting
 *  \param  attempt_to_combine_broken_words  If true, parts of words and URLs that are broken across line-ends may be combined.
 *  \param  remove_possible_junk             If true, we attempt to recognise non-sentences and will remove the likely candidates.
 */
void SplitIntoSentences(const std::string &document, std::vector<std::string> * const sentences, const bool attempt_to_combine_broken_words = false,
			const bool remove_possible_junk = true);


/** \brief  Decomposes sentences into "words."
 *  \param  sentence         The sentence to decompose.
 *  \param  words            The result of the decomposition.
 *  \param  lowercase_words  If true, all words in "words" except of recognised URLs will be returned converted to their lowercase forms.
 *  \param  urls             If non-NULL, recognised URLs will be returned here in addition to "words."
 *  \note   Possessive s'es "'s" at the end of words will be removed, but contractions like "isn't" will be returned as a single `word'.
 *  \return The number of "words" that "sentence" has been split into.
 */
unsigned SplitSentenceIntoWords(const std::string &sentence, std::vector<std::string> * const words, const bool lowercase_words = true,
				std::vector<std::string> * const urls = NULL);


/** \brief  Attempts to combine parts of words and URLs that have been broken access line-ends.
 *  \param  document                            The text to process.
 *  \param  attempt_to_combine_broken_sections  If true, an attempt will be made to combine words broken at ends of sections.
 *  \return The text with the combined lines.
 */
std::string CombineLines(const std::string &document, const bool attempt_to_combine_broken_sections = true);


} // namespace TextUtil


#endif // define TEXT_UTIL_H
