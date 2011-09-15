/** \file    HtmlUtil.cc
 *  \brief   Implementation of HTML-related utility functions.
 *  \author  Dr. Johannes Ruscheinski
 *  \author  Dr. Gordon W. Paynter
 *  \author  Artur Kedzierski
 */

/*
 *  Copyright 2002-2008 Project iVia.
 *  Copyright 2002-2008 The Regents of The University of California.
 *
 *  This file is part of the libiViaCore package.
 *
 *  The libiViaCore package is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2 of the License,
 *  or (at your option) any later version.
 *
 *  libiViaCore is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with libiViaCore; if not, write to the Free Software Foundation, Inc.,
 *  59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

#include <HtmlUtil.h>
#include <algorithm>
#include <iostream>
#include <stack>
#include <cctype>
#include <cerrno>
#include <HttpHeader.h>
#include <IniFile.h>
#include <MiscUtil.h>
#include <MsgUtil.h>
#include <NGramUtil.h>
#include <PerlCompatRegExp.h>
#include <StringUtil.h>
#include <Template.h>
#include <TextUtil.h>
#include <Url.h>


#ifndef DIM
#       define DIM(array)	(sizeof(array) / sizeof(array[0]))
#endif


namespace {


std::string GetEtcDir() {
#ifdef __MACH__
	return MiscUtil::GetEnv("HOME") + std::string("/etc");
#else
	return ETC_DIR;
#endif
}


class HtmlToTextParser: public HtmlParser {
	static const bool APPEND_NEWLINE = true;
	static const unsigned NO_OF_SPACES_TO_INDENT = 2;
	struct Level {
		enum Type { ORDERED_LIST, UNORDERED_LIST, BLOCKQUOTE } type_;
		unsigned item_count_;
	public:
		explicit Level(const Type type)
			: type_(type), item_count_(0) { }
	};

	const unsigned max_line_length_;
	std::string &plain_text_;
	std::string current_line_;
	bool convert_to_uppercase_, skip_;
	std::stack<Level> levels_;
public:
	explicit HtmlToTextParser(const std::string &input_string, const unsigned max_line_length,
				  std::string * const plain_text);
	virtual void notify(const Chunk &chunk);
private:
	void appendToCurrentLine(const std::string &text);
	void flushCurrentLine(const bool append_new_line = false);
};


HtmlToTextParser::HtmlToTextParser(const std::string &input_string, const unsigned max_line_length,
				   std::string * const plain_text)
	: HtmlParser(input_string, OPENING_TAG | CLOSING_TAG | WORD | PUNCTUATION
		     | WHITESPACE | END_OF_STREAM | UNEXPECTED_END_OF_STREAM),
	  max_line_length_(max_line_length), plain_text_(*plain_text),
	  convert_to_uppercase_(false), skip_(false)
{
}


void HtmlToTextParser::appendToCurrentLine(const std::string &text)
{
	if (current_line_.empty()) {
		if (text != " ")
			current_line_ += text;
	}
	else if (NO_OF_SPACES_TO_INDENT * levels_.size() + current_line_.length() + text.length()
		 > max_line_length_)
	{
		flushCurrentLine();
		current_line_ = text;
	}
	else if (text == " ") {
		if (current_line_[current_line_.length() - 1] != ' ')
			current_line_ += ' ';
	}
	else
		current_line_ += text;
}


void HtmlToTextParser::flushCurrentLine(const bool append_new_line)
{
	StringUtil::Trim(&current_line_);
	if (not current_line_.empty()) {
		for (unsigned level = 0; level < NO_OF_SPACES_TO_INDENT * levels_.size(); ++level)
			plain_text_ += "  ";
		plain_text_ += current_line_ + "\n";
		if (append_new_line)
			plain_text_ += '\n';
		current_line_.clear();
	}
}


void HtmlToTextParser::notify(const Chunk &chunk)
{
	switch (chunk.type_) {
	case OPENING_TAG:
		if (chunk.text_ == "title")
			skip_ = true;
		else if (chunk.text_ == "h1")
			convert_to_uppercase_ = true;
		else if (chunk.text_ == "br")
			flushCurrentLine();
		else if (chunk.text_ == "p")
			flushCurrentLine(APPEND_NEWLINE);
		else if (chunk.text_ == "ul") {
			flushCurrentLine();
			levels_.push(Level(Level::UNORDERED_LIST));
		}
		else if (chunk.text_ == "ol") {
			flushCurrentLine();
			levels_.push(Level(Level::ORDERED_LIST));
		}
		else if (chunk.text_ == "blockquote") {
			flushCurrentLine(APPEND_NEWLINE);
			levels_.push(Level(Level::BLOCKQUOTE));
		}
		else if (chunk.text_ == "li") {
			flushCurrentLine();

			// Handle a pathological case (there was neither a <ul> nor an <ol>):
			if (levels_.empty())
				levels_.push(Level(Level::UNORDERED_LIST));

			++levels_.top().item_count_;
			if (levels_.top().type_ == Level::ORDERED_LIST)
				appendToCurrentLine(StringUtil::ToString(levels_.top().item_count_)
						    + " ");
			else // Assume levels_.top().type_ == Level::UNORDERED_LIST
				appendToCurrentLine("* ");
		}

		break;
	case CLOSING_TAG:
		if (chunk.text_ == "title")
			skip_ = false;
		else if (chunk.text_ == "h1") {
			flushCurrentLine(APPEND_NEWLINE);
			convert_to_uppercase_ = false;
		}
		else if (chunk.text_ == "ul" or chunk.text_ == "ol" or chunk.text_ == "blockquote") {
			flushCurrentLine(APPEND_NEWLINE);
			if (not levels_.empty())
				levels_.pop();
		}

		break;
	case WORD: {
		if (skip_)
			return;
		std::string word(chunk.text_);
		if (convert_to_uppercase_)
			StringUtil::ToUpper(&word);
		appendToCurrentLine(word);
		break;
	}
	case PUNCTUATION:
		if (skip_)
			return;
		appendToCurrentLine(chunk.text_);
		break;
	case WHITESPACE:
		if (skip_)
			return;
		appendToCurrentLine(" ");
		break;
	case END_OF_STREAM:
	case UNEXPECTED_END_OF_STREAM:
		flushCurrentLine();
		return;
	default:
		MsgUtil::Error("in HtmlToTextParser::notify: unhandled case: %d!",
			       static_cast<int>(chunk.type_));
	}
}


void IsAppropriateDocumentHelper(std::list<std::string> * const meta_tag_names,
				 std::list<std::string> * const banned_words)
{
	const std::string CONF_FILENAME(GetEtcDir() + "/AppropriateDocs.conf");
	IniFile ini_file(CONF_FILENAME);
	IniFile::SectionContents section_contents = ini_file.getSection("meta_tag_names");
	for (IniFile::SectionContents::const_iterator entry(section_contents.begin());
	     entry != section_contents.end(); ++entry)
		meta_tag_names->push_back(entry->second);

	section_contents = ini_file.getSection("banned_words");
	for (IniFile::SectionContents::iterator entry(section_contents.begin());
	     entry != section_contents.end(); ++entry)
		banned_words->push_back(StringUtil::ToLower(&entry->second));
}


} // unnamed namespace


namespace HtmlUtil {


bool DecodeEntity(const char * const entity_string, char * const ch)
{
	// numeric entity?
	if (entity_string[0] == '#') { // Yes!
		errno = 0;
		unsigned long code;
		if (entity_string[1] == 'x')
			code = ::strtoul(entity_string + 2, NULL, 16);
		else
			code = ::strtoul(entity_string + 1, NULL, 10);
		if (errno != 0)
			return false;

		if (code <= 255) {
			*ch = static_cast<char>(code);
			return true;
		}

		switch (code) {
		case 946:
		case 0xCF90:
			*ch = static_cast<char>(223); // Map the lowercase Greek beta to a German sharp-s.
			return true;
		default:
			return false;
		}
	}

	if (std::strcmp(entity_string, "quot") == 0) {
		*ch = '"';
		return true;
	}

	if (std::strcmp(entity_string, "amp") == 0) {
		*ch = '&';
		return true;
	}

	if (std::strcmp(entity_string, "lt") == 0) {
		*ch = '<';
		return true;
	}

	if (std::strcmp(entity_string, "gt") == 0) {
		*ch = '>';
		return true;
	}

	if (std::strcmp(entity_string, "nbsp") == 0) {
		*ch = ' ';
		return true;
	}

	if (std::strcmp(entity_string, "iexcl") == 0) {
		*ch = static_cast<char>(161);
		return true;
	}

	if (std::strcmp(entity_string, "cent") == 0) {
		*ch = static_cast<char>(162);
		return true;
	}

	if (std::strcmp(entity_string, "pound") == 0) {
		*ch = static_cast<char>(163);
		return true;
	}

	if (std::strcmp(entity_string, "curren") == 0) {
		*ch = static_cast<char>(164);
		return true;
	}

	if (std::strcmp(entity_string, "yen") == 0) {
		*ch = static_cast<char>(165);
		return true;
	}

	if (std::strcmp(entity_string, "brvbar") == 0) {
		*ch = static_cast<char>(166);
		return true;
	}

	if (std::strcmp(entity_string, "sect") == 0) {
		*ch = static_cast<char>(167);
		return true;
	}

	if (std::strcmp(entity_string, "uml") == 0) {
		*ch = static_cast<char>(168);
		return true;
	}

	if (std::strcmp(entity_string, "copy") == 0) {
		*ch = static_cast<char>(169);
		return true;
	}

	if (std::strcmp(entity_string, "ordf") == 0) {
		*ch = static_cast<char>(170);
		return true;
	}

	if (std::strcmp(entity_string, "laquo") == 0) {
		*ch = static_cast<char>(171);
		return true;
	}

	if (std::strcmp(entity_string, "not") == 0) {
		*ch = static_cast<char>(172);
		return true;
	}

	if (std::strcmp(entity_string, "shy") == 0) {
		*ch = static_cast<char>(173);
		return true;
	}

	if (std::strcmp(entity_string, "reg") == 0) {
		*ch = static_cast<char>(174);
		return true;
	}

	if (std::strcmp(entity_string, "macr") == 0) {
		*ch = static_cast<char>(175);
		return true;
	}

	if (std::strcmp(entity_string, "deg") == 0) {
		*ch = static_cast<char>(176);
		return true;
	}

	if (std::strcmp(entity_string, "plusmn") == 0) {
		*ch = static_cast<char>(177);
		return true;
	}

	if (std::strcmp(entity_string, "sup2") == 0) {
		*ch = static_cast<char>(178);
		return true;
	}

	if (std::strcmp(entity_string, "sup3") == 0) {
		*ch = static_cast<char>(179);
		return true;
	}

	if (std::strcmp(entity_string, "acute") == 0) {
		*ch = static_cast<char>(180);
		return true;
	}

	if (std::strcmp(entity_string, "micro") == 0) {
		*ch = static_cast<char>(181);
		return true;
	}

	if (std::strcmp(entity_string, "para") == 0) {
		*ch = static_cast<char>(182);
		return true;
	}

	if (std::strcmp(entity_string, "middot") == 0) {
		*ch = static_cast<char>(183);
		return true;
	}

	if (std::strcmp(entity_string, "cedil") == 0) {
		*ch = static_cast<char>(184);
		return true;
	}

	if (std::strcmp(entity_string, "sup1") == 0) {
		*ch = static_cast<char>(185);
		return true;
	}

	if (std::strcmp(entity_string, "ordm") == 0) {
		*ch = static_cast<char>(186);
		return true;
	}

	if (std::strcmp(entity_string, "raquo") == 0) {
		*ch = static_cast<char>(187);
		return true;
	}

	if (std::strcmp(entity_string, "fraq14") == 0) {
		*ch = static_cast<char>(188);
		return true;
	}

	if (std::strcmp(entity_string, "fraq12") == 0) {
		*ch = static_cast<char>(189);
		return true;
	}

	if (std::strcmp(entity_string, "fraq34") == 0) {
		*ch = static_cast<char>(190);
		return true;
	}

	if (std::strcmp(entity_string, "iquest") == 0) {
		*ch = static_cast<char>(191);
		return true;
	}

	if (std::strcmp(entity_string, "Agrave") == 0) {
		*ch = static_cast<char>(192);
		return true;
	}

	if (std::strcmp(entity_string, "Aacute") == 0) {
		*ch = static_cast<char>(193);
		return true;
	}

	if (std::strcmp(entity_string, "Acirc") == 0) {
		*ch = static_cast<char>(194);
		return true;
	}

	if (std::strcmp(entity_string, "Atilde") == 0) {
		*ch = static_cast<char>(195);
		return true;
	}

	if (std::strcmp(entity_string, "Auml") == 0) {
		*ch = static_cast<char>(196);
		return true;
	}

	if (std::strcmp(entity_string, "Aring") == 0) {
		*ch = static_cast<char>(197);
		return true;
	}

	if (std::strcmp(entity_string, "AElig") == 0) {
		*ch = static_cast<char>(198);
		return true;
	}

	if (std::strcmp(entity_string, "Ccedil") == 0) {
		*ch = static_cast<char>(199);
		return true;
	}

	if (std::strcmp(entity_string, "Egrave") == 0) {
		*ch = static_cast<char>(200);
		return true;
	}

	if (std::strcmp(entity_string, "Eacute") == 0) {
		*ch = static_cast<char>(201);
		return true;
	}

	if (std::strcmp(entity_string, "Ecirc") == 0) {
		*ch = static_cast<char>(202);
		return true;
	}

	if (std::strcmp(entity_string, "Euml") == 0) {
		*ch = static_cast<char>(203);
		return true;
	}

	if (std::strcmp(entity_string, "Igrave") == 0) {
		*ch = static_cast<char>(204);
		return true;
	}

	if (std::strcmp(entity_string, "Iacute") == 0) {
		*ch = static_cast<char>(205);
		return true;
	}

	if (std::strcmp(entity_string, "Icirc") == 0) {
		*ch = static_cast<char>(206);
		return true;
	}

	if (std::strcmp(entity_string, "Iuml") == 0) {
		*ch = static_cast<char>(207);
		return true;
	}

	if (std::strcmp(entity_string, "ETH") == 0) {
		*ch = static_cast<char>(208);
		return true;
	}

	if (std::strcmp(entity_string, "Ntilde") == 0) {
		*ch = static_cast<char>(209);
		return true;
	}

	if (std::strcmp(entity_string, "Ograve") == 0) {
		*ch = static_cast<char>(210);
		return true;
	}

	if (std::strcmp(entity_string, "Oacute") == 0) {
		*ch = static_cast<char>(211);
		return true;
	}

	if (std::strcmp(entity_string, "Ocirc") == 0) {
		*ch = static_cast<char>(212);
		return true;
	}

	if (std::strcmp(entity_string, "Otilde") == 0) {
		*ch = static_cast<char>(213);
		return true;
	}

	if (std::strcmp(entity_string, "Ouml") == 0) {
		*ch = static_cast<char>(214);
		return true;
	}

	if (std::strcmp(entity_string, "times") == 0) {
		*ch = static_cast<char>(215);
		return true;
	}

	if (std::strcmp(entity_string, "Oslash") == 0) {
		*ch = static_cast<char>(216);
		return true;
	}

	if (std::strcmp(entity_string, "Ugrave") == 0) {
		*ch = static_cast<char>(217);
		return true;
	}

	if (std::strcmp(entity_string, "Uacute") == 0) {
		*ch = static_cast<char>(218);
		return true;
	}

	if (std::strcmp(entity_string, "Ucirc") == 0) {
		*ch = static_cast<char>(219);
		return true;
	}

	if (std::strcmp(entity_string, "Uuml") == 0) {
		*ch = static_cast<char>(220);
		return true;
	}

	if (std::strcmp(entity_string, "Yacute") == 0) {
		*ch = static_cast<char>(221);
		return true;
	}

	if (std::strcmp(entity_string, "THORN") == 0) {
		*ch = static_cast<char>(222);
		return true;
	}

	if (std::strcmp(entity_string, "szlig") == 0) {
		*ch = static_cast<char>(223);
		return true;
	}

	if (std::strcmp(entity_string, "beta") == 0) {
		*ch = static_cast<char>(223);
		return true;
	}

	if (std::strcmp(entity_string, "agrave") == 0) {
		*ch = static_cast<char>(224);
		return true;
	}

	if (std::strcmp(entity_string, "aacute") == 0) {
		*ch = static_cast<char>(225);
		return true;
	}

	if (std::strcmp(entity_string, "acirc") == 0) {
		*ch = static_cast<char>(226);
		return true;
	}

	if (std::strcmp(entity_string, "atilde") == 0) {
		*ch = static_cast<char>(227);
		return true;
	}

	if (std::strcmp(entity_string, "auml") == 0) {
		*ch = static_cast<char>(228);
		return true;
	}

	if (std::strcmp(entity_string, "aring") == 0) {
		*ch = static_cast<char>(229);
		return true;
	}

	if (std::strcmp(entity_string, "aelig") == 0) {
		*ch = static_cast<char>(230);
		return true;
	}

	if (std::strcmp(entity_string, "ccedil") == 0) {
		*ch = static_cast<char>(231);
		return true;
	}

	if (std::strcmp(entity_string, "egrave") == 0) {
		*ch = static_cast<char>(232);
		return true;
	}

	if (std::strcmp(entity_string, "eacute") == 0) {
		*ch = static_cast<char>(233);
		return true;
	}

	if (std::strcmp(entity_string, "ecirc") == 0) {
		*ch = static_cast<char>(234);
		return true;
	}

	if (std::strcmp(entity_string, "euml") == 0) {
		*ch = static_cast<char>(235);
		return true;
	}

	if (std::strcmp(entity_string, "igrave") == 0) {
		*ch = static_cast<char>(236);
		return true;
	}

	if (std::strcmp(entity_string, "iacute") == 0) {
		*ch = static_cast<char>(237);
		return true;
	}

	if (std::strcmp(entity_string, "icirc") == 0) {
		*ch = static_cast<char>(238);
		return true;
	}

	if (std::strcmp(entity_string, "iuml") == 0) {
		*ch = static_cast<char>(239);
		return true;
	}

	if (std::strcmp(entity_string, "eth") == 0) {
		*ch = static_cast<char>(240);
		return true;
	}

	if (std::strcmp(entity_string, "ntilde") == 0) {
		*ch = static_cast<char>(241);
		return true;
	}

	if (std::strcmp(entity_string, "ograve") == 0) {
		*ch = static_cast<char>(242);
		return true;
	}

	if (std::strcmp(entity_string, "oacute") == 0) {
		*ch = static_cast<char>(243);
		return true;
	}

	if (std::strcmp(entity_string, "ocirc") == 0) {
		*ch = static_cast<char>(244);
		return true;
	}

	if (std::strcmp(entity_string, "otilde") == 0) {
		*ch = static_cast<char>(245);
		return true;
	}

	if (std::strcmp(entity_string, "ouml") == 0) {
		*ch = static_cast<char>(246);
		return true;
	}

	if (std::strcmp(entity_string, "divide") == 0) {
		*ch = static_cast<char>(247);
		return true;
	}

	if (std::strcmp(entity_string, "oslash") == 0) {
		*ch = static_cast<char>(248);
		return true;
	}

	if (std::strcmp(entity_string, "ugrave") == 0) {
		*ch = static_cast<char>(249);
		return true;
	}

	if (std::strcmp(entity_string, "uacute") == 0) {
		*ch = static_cast<char>(250);
		return true;
	}

	if (std::strcmp(entity_string, "ucirc") == 0) {
		*ch = static_cast<char>(251);
		return true;
	}

	if (std::strcmp(entity_string, "uuml") == 0) {
		*ch = static_cast<char>(252);
		return true;
	}

	if (std::strcmp(entity_string, "yacute") == 0) {
		*ch = static_cast<char>(253);
		return true;
	}

	if (std::strcmp(entity_string, "thorn") == 0) {
		*ch = static_cast<char>(254);
		return true;
	}

	if (std::strcmp(entity_string, "yuml") == 0) {
		*ch = static_cast<char>(255);
		return true;
	}

	*ch = '\0';
	return false;
}


namespace {


/** \class  RemoveTagsParser
 *  \brief  Removes tags fromn an HTML document, leaving whitespace-separated text.
 *
 *  The parser is used in the RemoveTags function.  It also gathers
 *  some META tag information, which is used by a ferw other callers.
 *  The parser replaces tags with a space character, so the result
 *  will usually include a lot of unneccesary, repeated whitespace.
 */
class RemoveTagsParser: public HtmlParser {
	std::string &stripped_text_;
	std::list< std::pair<std::string, std::string> > * const http_equivalents_;
public:
	/** \brief  Construct a RemoveTagsParser
	 *  \param  input_string   The HTML to parse.
	 *  \param  stripped_text  Output parameter that will hold the text of the document without tags (but with lots of extra whitespace).
	 */
	RemoveTagsParser(const std::string &input_string, std::string * const stripped_text,
			 std::list< std::pair<std::string, std::string> > * const http_equivalents = NULL)
		: HtmlParser(input_string), stripped_text_(*stripped_text),
		  http_equivalents_(http_equivalents) { }
	virtual void notify(const Chunk &chunk);
};


void RemoveTagsParser::notify(const Chunk &chunk)
{
	switch (chunk.type_) {
	case HtmlParser::OPENING_TAG:
		if (http_equivalents_ != NULL and chunk.text_ == "meta" and chunk.attribute_map_ != NULL) {
			HtmlParser::AttributeMap::const_iterator entry(chunk.attribute_map_->find("http-equiv"));
			if (entry != chunk.attribute_map_->end()) {
				const std::string http_header(entry->second);
				entry = chunk.attribute_map_->find("content");
				if (entry != chunk.attribute_map_->end())
					http_equivalents_->push_back(std::make_pair(http_header, entry->second));
			}
		}
		else if (chunk.text_ == "p")
			stripped_text_ += '\n';
		else
			stripped_text_ += ' ';
		break;
	case HtmlParser::CLOSING_TAG:
	case HtmlParser::MALFORMED_TAG:
	case HtmlParser::COMMENT:
		stripped_text_ += ' ';
		break;
	case HtmlParser::WORD:
	case HtmlParser::PUNCTUATION:
	case HtmlParser::WHITESPACE:
		stripped_text_ += chunk.text_;
		break;
	}
}


} // unnamed namespace


std::string &RemoveTags(std::string * const s)
{
	std::string stripped_text;
	RemoveTagsParser remove_tags_parser(*s, &stripped_text);
	remove_tags_parser.parse();
	stripped_text = TextUtil::SqueezeWhitespace(stripped_text);

	// Remove leading whitespace:
	std::string::const_iterator ch(stripped_text.begin());
	while (ch != stripped_text.end() and isspace(*ch))
		++ch;

	std::string result;

	// Replace multiple line-end and similar characters with a single newline character:
	unsigned consecutive_lineend_count(0);
	for (/* Intentionally empty! */; ch != stripped_text.end(); ++ch) {
		if (*ch == '\n' or *ch == '\r' or *ch == '\f' or *ch == '\v') {
			if (consecutive_lineend_count < 2) {
				result += '\n';
				++consecutive_lineend_count;
			}
		}
		else {
			consecutive_lineend_count = 0;
			result += *ch;
		}
	}

	return *s = result;
}


// RemoveHtmlComments -- strip HTML comments out of "buf".
//
char *RemoveHtmlComments(char * const buf)
{
	char *cp = buf;
	char *new_cp = cp;
	unsigned dash_count = 0;
	for (; *cp != '\0'; ++cp) {
		// Comment start?
		if (*cp == '<' and *(cp + 1) == '!' and *(cp + 2) == '-' and *(cp + 3) == '-') {
			for (++cp; dash_count < 2 or *cp != '>'; ++cp) {
				// Unterminated comment?
				if (*cp == '\0')
					goto abort;

				if (*cp == '-')
					++dash_count;
				else
					dash_count = 0;
			}
		}
		else {
			if (new_cp != cp)
				*new_cp = *cp;
			++new_cp;
		}

	}
abort:
	*new_cp = '\0';

	return buf;
}


std::string &ReplaceEntities(std::string * const s, const UnknownEntityMode unknown_entity_mode)
{
	std::string result;
	std::string::const_iterator ch(s->begin());
	while (ch != s->end()) {
		if (*ch != '&') {
			// A non-entity character:
			result += *ch;
			++ch;
		}
		else {
			// The start of an entity:
			++ch;

			// Read the entity:
			std::string entity;
			while (ch != s->end() and *ch != ';' and *ch != '&') {
				entity += *ch;
				++ch;
			}

			// Output the entity:
			char decoded_char;
			if (DecodeEntity(entity, &decoded_char))
				result += decoded_char;
			else if (unknown_entity_mode == IGNORE_UNKNOWN_ENTITIES)
				result += "&" + entity + ";";

			// Advance to next letter:
			if (ch != s->end() and *ch != '&')
				++ch;
		}
	}

	return *s = result;
}


// IsHtmlEscaped --  Are all '&' and '<' and '>' and quotes escaped?
//
bool IsHtmlEscaped(const std::string &raw_text)
{
	for (std::string::const_iterator ch(raw_text.begin()); ch != raw_text.end(); ++ch) {
		if (*ch == '&') {
			++ch;
			std::string possible_entity;
			const unsigned MAX_ENTITY_NAME_LENGTH(6);
			for (unsigned char_count(0); ch != raw_text.end() and *ch != ';' and char_count < MAX_ENTITY_NAME_LENGTH + 1; ++ch, ++char_count)
				possible_entity += *ch;
			if (ch == raw_text.end() or *ch != ';') // No entity!
				return false;

			char dummy;
			if (not DecodeEntity(possible_entity, &dummy))
				return false; // Not an entity!
		}
		else if (*ch == '<' or *ch == '>' or *ch == '"' or *ch == '\'')
			return false;
	}

	// If we make it this far, everything is okay:
	return true;
}


std::string HtmlEscape(const std::string &raw_text)
{
	std::string processed_text(raw_text);
	HtmlEscape(&processed_text);

	return processed_text;
}


std::string CgiEscape(const std::string &raw_text)
{
	std::string escaped_text;

	for (std::string::const_iterator ch(raw_text.begin()); ch != raw_text.end(); ++ch) {
		if (likely(StringUtil::IsAlphanumeric(*ch)))
			escaped_text += *ch;
		else if (*ch == '-' or *ch == '$' or *ch == '_' or *ch == '@' or *ch == '.' or *ch == '!' or *ch == '*'
			 or *ch == '"' or *ch == '\'' or *ch == '(' or *ch == ')' or *ch == ',')
			escaped_text += *ch;
		else {
			escaped_text += '%';
			escaped_text += MiscUtil::HexDigit(static_cast<const unsigned char>(*ch) >> 4u);
			escaped_text += MiscUtil::HexDigit(static_cast<const unsigned char>(*ch) & 0xFu);
		}
	}

	return escaped_text;
}


std::string &HtmlEscape(std::string * const raw_text)
{
	std::string processed_text;
	processed_text.reserve(raw_text->size() + 20);

	for (std::string::const_iterator ch(raw_text->begin()); ch != raw_text->end(); ++ch) {
		switch (*ch) {
		case '&':
			processed_text += "&amp;";
			break;
		case '<':
			processed_text += "&lt;";
			break;
		case '>':
			processed_text += "&gt;";
			break;
		case '"':
			processed_text += "&quot;";
			break;
		case '\'':
			processed_text += "&#039;";
			break;
		default:
			processed_text += *ch;
			break;
		}
	}

	return *raw_text = processed_text;
}


bool IsAppropriateDocument(const std::string &document_source)
{
	static bool initialized;
	static std::list<std::string> meta_tag_names, banned_words;
	if (not initialized) {
		initialized = true;
		IsAppropriateDocumentHelper(&meta_tag_names, &banned_words);
	}

	// Extract metadata from the HTML document:
	std::list< std::pair<std::string, std::string> > extracted_data;
	MetaTagExtractor meta_tag_extractor(document_source, meta_tag_names, &extracted_data);
	meta_tag_extractor.parse();

	// Break up all extracted metadata into words and convert to lowercase:
	std::list<std::string> words;
	for (std::list< std::pair<std::string, std::string> >::const_iterator item(extracted_data.begin());
	     item != extracted_data.end(); ++item)
	{
		std::list<std::string> temp_words;
		StringUtil::SplitThenTrim(item->second, "-;., \t", "", &temp_words);
		for (std::list<std::string>::iterator temp_word(temp_words.begin());
		     temp_word != temp_words.end(); ++temp_word)
			if (not temp_word->empty())
				words.push_back(StringUtil::ToLower(&*temp_word));
	}

	// Now check to see whether any of the banned words occur in the metadata:
	for (std::list<std::string>::const_iterator banned_word(banned_words.begin());
	     banned_word != banned_words.end(); ++banned_word)
		if (std::find(words.begin(), words.end(), *banned_word) != words.end())
			return false;

	return true;
}


const std::string HtmlToText(const std::string &html_document, const unsigned max_line_length)
{
	std::string plain_text;
	HtmlToTextParser html_to_text_parser(html_document, max_line_length, &plain_text);
	html_to_text_parser.parse();

	return plain_text;
}


bool GetCharSet(const std::string &html_document, std::string * const charset)
{
	std::string plain_text;
	std::list< std::pair<std::string, std::string> > http_equivalents;
	RemoveTagsParser remove_tags_parser(html_document, &plain_text, &http_equivalents);
	remove_tags_parser.parse();

	for (std::list< std::pair<std::string, std::string> >::const_iterator http_equiv(http_equivalents.begin());
	     http_equiv != http_equivalents.end(); ++http_equiv)
	{
		if (::strcasecmp(http_equiv->first.c_str(), "Content-Type") == 0) {
			std::string::size_type pos = http_equiv->second.find("charset=");
			if (pos != std::string::npos) {
				*charset = http_equiv->second.substr(pos + 8);
				StringUtil::Trim(charset);
				return !charset->empty();
			}
		}
	}

	return false;
}


bool IsProbablyEnglish(const std::string &html_document)
{
	std::string plain_text;
	std::list< std::pair<std::string, std::string> > http_equivalents;
	RemoveTagsParser remove_tags_parser(html_document, &plain_text, &http_equivalents);
	remove_tags_parser.parse();

	std::string charset, content_languages;
	for (std::list< std::pair<std::string, std::string> >::const_iterator http_equiv(http_equivalents.begin());
	     http_equiv != http_equivalents.end(); ++http_equiv)
	{
		if (::strcasecmp(http_equiv->first.c_str(), "Content-Type") == 0) {
			std::string::size_type pos = http_equiv->second.find("charset=");
			if (pos != std::string::npos) {
				charset = http_equiv->second.substr(pos + 8) ;
				charset = StringUtil::Trim(&charset);
			}
		}
		if (::strcasecmp(http_equiv->first.c_str(), "Content-Language") == 0) {
			content_languages = http_equiv->second;
			StringUtil::Trim(&content_languages);
		}
	}
	if (HttpHeader::IsProbablyNotEnglish(charset, content_languages))
		return false;

	if (not charset.empty() and ::strcasecmp(charset.c_str(), "utf-8") == 0)
		plain_text = StringUtil::UTF8ToISO8859_15(plain_text);

	StringUtil::CollapseWhitespace(&plain_text);
	StringUtil::Trim(&plain_text);

	if (plain_text.empty())
		return false;

	std::list<std::string> languages;
	NGramUtil::ClassifyLanguage(plain_text, &languages);
	return not languages.empty() and languages.front() == "english";
}


namespace { // Helper class for FrameAnalysis


/** \class  FrameAnalysisParser
 *  \brief  A class used to extract the frames (if present) from an HTML document.
 */
class FrameAnalysisParser: public HtmlParser {
	bool is_a_frameset_;
	std::list<std::string> * const frame_urls_;
	std::string base_url_;
public:
	explicit FrameAnalysisParser(const std::string &url, const std::string &html, std::list<std::string> * const frame_urls)
		: HtmlParser(html, HtmlParser::OPENING_TAG), is_a_frameset_(false), frame_urls_(frame_urls), base_url_(url) { }
	bool isAFrameset() const { return is_a_frameset_; }
	void notify(const Chunk &chunk);
};


void FrameAnalysisParser::notify(const Chunk &chunk)
{
	// Chunk must be an opening tag:
	if (chunk.text_ == "frame") {
		HtmlParser::AttributeMap::const_iterator entry(chunk.attribute_map_->find("src"));
		if (entry != chunk.attribute_map_->end()) {
			const std::string relative_url(entry->second);
			Url frame_url(relative_url, base_url_, Url::AUTO_MAKE_ABSOLUTE | Url::AUTO_CLEAN_UP);
			if (not frame_url.anErrorOccurred() and frame_url.isValidWebUrl())
				frame_urls_->push_back(frame_url.toString());
		}
	}
	else if (chunk.text_ == "frameset")
		is_a_frameset_ = true;
	else if (chunk.text_ == "base") {
		HtmlParser::AttributeMap::const_iterator entry(chunk.attribute_map_->find("href"));
                if (entry != chunk.attribute_map_->end())
			base_url_ = entry->second;
	}
}


} // unnamed namespace



// FrameAnalysis -- Is the page a frameset?
//
bool FrameAnalysis(const std::string &url, const std::string &html, std::list<std::string> * const frame_urls)
{
	FrameAnalysisParser parser(url, html, frame_urls);
	parser.parse();
	return parser.isAFrameset();
}


namespace {


// DoubleQuoteEscape -- Replaces double quotes with "&quot;".  Helper function for GenerateSelectMenu.
//
std::string DoubleQuoteEscape(const std::string &raw_text)
{
	std::string processed_text;
	processed_text.reserve(raw_text.size() + 20);

	for (std::string::const_iterator ch(raw_text.begin()); ch != raw_text.end(); ++ch) {
		if (*ch == '"')
			processed_text += "&quot;";
		else
			processed_text += *ch;
	}

	return processed_text;
}


} // unnamed namespace


/** \brief  Creates an HTML "select" element from a list of values.
 *  \param  select_name_attrib  What to set the 'name' attribute of the select tag to.
 *  \param  name_value_pairs    Names and their corresponding values.
 *  \param  initial_selection   If any name in "name_value_pairs" matches this value its entry will initially be set.
 *                              May be empty.
 *  \param  indent_level        How many levels to indent the select menu.
 *  \param  indent_increment    How many spaces correspond to one indentation level.
 *  \return The HTML for the select menu.
 *
 *  This function generates the HTML for a select element that will provide a drop-down menu from which the user can make
 *  a choice.
 */
std::string GenerateSelectMenu(const std::string &select_name_attrib,
			       const std::list< std::pair<std::string, std::string> > &name_value_pairs,
			       const std::string &initial_selection, const unsigned indent_level,
			       const unsigned indent_increment)
{
        // Output the start of the menu:
	std::string generated_html(std::string(indent_level * indent_increment, ' '));
	generated_html += "<select name=\"" + DoubleQuoteEscape(select_name_attrib) + "\">\n";

        // Output the set of possible choices:
        for (std::list< std::pair<std::string, std::string> >::const_iterator name_and_value(name_value_pairs.begin());
	     name_and_value != name_value_pairs.end(); ++name_and_value)
        {
                generated_html += std::string((indent_level + 1) * indent_increment, ' ')
			          + "<option value=\"" + DoubleQuoteEscape(name_and_value->second) + '"';

                if (name_and_value->first == initial_selection.c_str())
                        generated_html += " selected=\"selected\"";

                generated_html += '>' + DoubleQuoteEscape(name_and_value->first) + "</option>\n";
        }

        generated_html += std::string(indent_level * indent_increment, ' ') + "</select>\n";

        return generated_html;

}


const std::string DEFAULT_DISPLAY_MESSAGE_TEMPLATE(
	"<html>\n"
	"  <title>$TITLE</title>\n"
	"  <body>\n"
	"    <blockquote>\n"
	"      $MESSAGE\n"
	"    </blockquote>\n"
	"  </body>\n"
	"</html>\n"
);


void DisplayMessage(const std::string &title, const std::string &message, const bool require_content_type_header,
		    const std::string &message_template)
{
	if (require_content_type_header)
		std::cout << "Content-type: text/html\r\n\r\n";

	StringMap macros;
	macros.insert("TITLE", title);
	macros.insert("MESSAGE", message);
	Template::ProcessString(message_template.empty() ? DEFAULT_DISPLAY_MESSAGE_TEMPLATE : message_template, macros);
}


void DisplayMessageAndExit(const std::string &title, const std::string &message, const bool require_content_type_header,
			   const std::string &message_template)
{
	DisplayMessage(title, message, require_content_type_header, message_template);
	std::exit(EXIT_SUCCESS);
}


DomTreeNode::~DomTreeNode()
{
	for (std::list<DomTreeNode *>::const_iterator child_node(children_.begin()); child_node != children_.end();
             ++child_node)
		delete *child_node;
}


void DomTreeNode::print(std::ostream &output, const unsigned indent_per_level, const unsigned current_level) const
{
	for (unsigned level_no(0); level_no < current_level; ++level_no) {
		for (unsigned space_count(0); space_count < indent_per_level; ++space_count)
			output << ' ';
	}

	switch (type_) {
	case HTML_TAG:
		output << value_;
		if (not tag_attribs_.empty()) {
			output << ' ';
			for (std::map<std::string, std::string>::const_iterator tag_name_and_value(tag_attribs_.begin());
			     tag_name_and_value != tag_attribs_.end(); ++tag_name_and_value)
			{
				if (tag_name_and_value != tag_attribs_.begin())
					output << ", ";
				output << tag_name_and_value->first << '=' << tag_name_and_value->second;
			}
		}
		output << '\n';
		break;
	case TEXT:
		output << '"' << StringUtil::BackslashEscape('"', value_) << "\"\n";
		break;
	}

	for (std::list<DomTreeNode *>::const_iterator child_node(children_.begin()); child_node != children_.end();
	     ++child_node)
		(*child_node)->print(output, indent_per_level, current_level + 1);
}


void DomTreeNode::depthFirstVisit(NodeVisitor node_visitor, void * const aux_data) const
{
	for (std::list<DomTreeNode *>::const_iterator child(children_.begin()); child != children_.end(); ++child)
		(*child)->depthFirstVisit(node_visitor, aux_data);

	node_visitor(this, aux_data);
}


DomTreeNode *DomTreeNode::CreateHtmlTagNode(const DomTreeNode* const parent, const std::string &tag_name, const HtmlParser::AttributeMap &attribute_map)
{
	return new DomTreeNode(HTML_TAG, tag_name, attribute_map, parent);
}


DomTreeNode *DomTreeNode::CreateTextNode(const DomTreeNode* const parent, const std::string &text)
{
	return new DomTreeNode(TEXT, text, parent);
}


// See for example http://www.htmlhelp.com/reference/html40/block.html
const char * const block_level_tags[] = {
	"address",
	"blockquote",
	"center",
	"dd",
	"dir",
	"div",
	"dl",
	"dt",
	"fieldset",
	"form",
	"frameset",
	"h1",
	"h2",
	"h3",
	"h4",
	"h5",
	"h6",
	"hr",
	"isindex",
	"li",
	"menu",
	"noscript",
	"ol",
	"p",
	"pre",
	"table",
	"tbody",
	"td",
	"tfoot",
	"th",
	"thead",
	"tr",
	"ul",
};
const char * const conditional_block_level_tags[] = { // These tags are only considered to be block-level tags if
	"applet",                                     // contained within an inline element or <p> tag.
	"button",
	"del",
	"iframe",
	"ins",
	"map",
	"object",
	"script",
};


// tag_compare -- helper function for std::bsearch in IsSimpleBlockLevelTag() and IsConditionalBlockLevelTag().
//
int tag_compare(const void *tag_name1, const void *tag_name2)
{
	return std::strcmp(*reinterpret_cast<const char * const *>(tag_name1),
			   *reinterpret_cast<const char * const *>(tag_name2));
}


inline bool IsSimpleBlockLevelTag(const char * const tag_name)
{
	return std::bsearch(&tag_name, &block_level_tags[0], DIM(block_level_tags), sizeof(char *), tag_compare) != NULL;
}


// IsConditionalBlockLevelTag -- this should only be called if the "tag_name" being tested is contained withing a <p>
//                               tag or following an inline tag.
//
inline bool IsConditionalBlockLevelTag(const char * const tag_name)
{
	return ::bsearch(&tag_name, &conditional_block_level_tags[0], DIM(conditional_block_level_tags), sizeof(char *),
			 tag_compare) != NULL;
}


struct DomTreeStackEntry {
	DomTreeNode *dom_tree_node_;
	bool is_inline_element_;
public:
	DomTreeStackEntry(DomTreeNode *dom_tree_node, const bool is_inline_element)
		: dom_tree_node_(dom_tree_node), is_inline_element_(is_inline_element) { }
};


class DomTreeStack: private std::vector<DomTreeStackEntry> {
public:
	DomTreeStack() { }
	DomTreeNode *getTop() const { return back().dom_tree_node_; }
	void pop(const std::string &tag_name = "");
	void push(DomTreeNode *new_top);
	std::string toString() const;
};


class TagMatch: public std::unary_function<const DomTreeNode *, bool> {
	std::string match_name_;
public:
	explicit TagMatch(const std::string &match_name): match_name_(match_name) { }
	bool operator()(const DomTreeStackEntry &dom_tree_stack_entry) const
		{ return dom_tree_stack_entry.dom_tree_node_->getValue() == match_name_; }
};


class TagSetMatch: public std::unary_function<const DomTreeNode *, bool> {
	const char * const *match_names_;
public:
	explicit TagSetMatch(const char * const *match_names): match_names_(match_names) { }
	bool operator()(const DomTreeStackEntry &dom_tree_stack_entry) const;
};


bool TagSetMatch::operator()(const DomTreeStackEntry &dom_tree_stack_entry) const
{
	for (const char * const *match_name(match_names_); *match_name != NULL; ++match_name) {
		if (std::strcmp(*match_name, dom_tree_stack_entry.dom_tree_node_->getValue().c_str()) == 0)
			return true;
	}

	return false;
}


void DomTreeStack::pop(const std::string &tag_name)
{
	if (tag_name.empty()) {
		pop_back();
		return;
	}

	// Scan the stack for open tags with the same tag name and pop everything up to and including the found tag if
	// any:
	reverse_iterator riter(std::find_if(rbegin(), rend(), TagMatch(tag_name)));
	if (riter != rend()) {
		difference_type no_of_elements_to_be_erased = (riter - rbegin()) + 1;
		erase(end() - no_of_elements_to_be_erased, end());
	}
}


const char * const li_antecedents[] = { "li", "ul", "ol", NULL };


void DomTreeStack::push(DomTreeNode *new_node)
{
	// We only want to store HTML tags on the stack!
	MSG_UTIL_ASSERT(new_node->getType() == DomTreeNode::HTML_TAG);

	// Determine if the new element acts an an "effective" inline element or not:
	bool top_is_p_or_inline;
	if (empty())
		top_is_p_or_inline = false;
	else
		top_is_p_or_inline = getTop()->getValue() == "p" or end()->is_inline_element_;
	bool is_inline_element;
	const std::string &tag_name(new_node->getValue());
	if (tag_name == "html" or tag_name == "frameset")
		is_inline_element = false;
	else if (tag_name == "meta" or tag_name == "link" or tag_name == "title" or tag_name == "script"
		 or tag_name == "style" or tag_name == "base")
	{
		back().dom_tree_node_->addChild(new_node);
		// Note that we *don't* push this on our internal stack because nothing should ever be a child of this!
		return;
	}
	else if (tag_name == "li") {
		is_inline_element = false;
		const reverse_iterator riter(std::find_if(rbegin(), rend(), TagSetMatch(li_antecedents)));
		if (riter != rend()) {
			for (reverse_iterator stack_entry(rbegin()); stack_entry != riter; ++stack_entry)
				pop_back();
		}
	}
	else if (IsSimpleBlockLevelTag(tag_name.c_str())) {
		is_inline_element = false;

		// Now close all open inline elements:
		reverse_iterator stack_entry(rbegin());
		for (/* Intentionally empty! */; stack_entry != rend() and stack_entry->is_inline_element_; ++stack_entry)
			pop_back();

		// If the top of the stack is a paragraph element we also close that one:
		if (stack_entry != rend() and stack_entry->dom_tree_node_->getValue() == "p")
			pop_back();
	}
	else
		is_inline_element = true;

	if (not empty())
		back().dom_tree_node_->addChild(new_node);

	push_back(DomTreeStackEntry(new_node, is_inline_element));
}


std::string DomTreeStack::toString() const
{
	std::string string_rep;
	for (const_reverse_iterator riter(rbegin()); riter != rend(); ++riter) {
		if (not string_rep.empty())
			string_rep += ',';
		string_rep += riter->dom_tree_node_->getValue();
	}

	return string_rep;
}


class DomTreeParser: public HtmlParser {
	DomTreeNode *root_node_;
	DomTreeStack open_tag_stack_;
public:
	DomTreeParser(const std::string &input_string, DomTreeNode **root_node);
	virtual void notify(const Chunk &chunk);
};


DomTreeParser::DomTreeParser(const std::string &input_string, DomTreeNode **root_node)
	: HtmlParser(input_string, OPENING_TAG | CLOSING_TAG | WORD | PUNCTUATION
		     | WHITESPACE | END_OF_STREAM | UNEXPECTED_END_OF_STREAM), root_node_(NULL)
{
	root_node_ = DomTreeNode::CreateHtmlTagNode(NULL, "html", HtmlParser::AttributeMap());
	*root_node = root_node_;
	open_tag_stack_.push(root_node_);
}


void DomTreeParser::notify(const Chunk &chunk)
{
	static bool done(false);
	if (unlikely(done))
		return;

	if (chunk.type_ == OPENING_TAG and chunk.text_ != "html") {
		// If we open a new tag that is of the same kind as what's currently on top of the stack,
		// we close the previously opened tag first before pushing the new tag onto the stack:
		if (open_tag_stack_.getTop()->getValue() == chunk.text_)
			open_tag_stack_.pop();

		open_tag_stack_.push(DomTreeNode::CreateHtmlTagNode(open_tag_stack_.getTop(), chunk.text_,
								    *chunk.attribute_map_));
	}
	else if (chunk.type_ == CLOSING_TAG) {
		if (chunk.text_ == "html")
			done = true;
		else
			open_tag_stack_.pop(chunk.text_);
	}
	else if (chunk.type_ == END_OF_STREAM or chunk.type_ == UNEXPECTED_END_OF_STREAM)
		done = true;
	else if (chunk.type_ == WHITESPACE) {
		std::list<DomTreeNode *> &top_children(open_tag_stack_.getTop()->children_);
		if (not top_children.empty() and top_children.back()->getType() == DomTreeNode::TEXT) {
			std::string top_value(top_children.back()->getValue());
			if (not top_value.empty() and top_value[top_value.length() - 1] != ' ')
				top_children.back()->value_ += ' ';
		}
	}
	else if (chunk.type_ == WORD or chunk.type_ == PUNCTUATION) {
		std::list<DomTreeNode *> &top_children(open_tag_stack_.getTop()->children_);
		if (not top_children.empty() and top_children.back()->getType() == DomTreeNode::TEXT)
			top_children.back()->value_ += chunk.text_;
		else
			open_tag_stack_.getTop()->addChild(DomTreeNode::CreateTextNode(open_tag_stack_.getTop(),
										       chunk.text_));
	}
}


void HtmlDocumentToDomTree(const std::string &html_document, DomTreeNode **tree_root)
{
	DomTreeParser dom_tree_parser(html_document, tree_root);
	dom_tree_parser.parse();
}


std::string UrlToAHref(const std::string &url)
{
	// Paranoia: Replace all double quotes with %22:
	std::string href(url);
	StringUtil::ReplaceString("\"", "%22", &href);

	return "<a href=\"" + href + "\">" + HtmlEscape(url) + "</a>";
}


} // namespace HtmlUtil
