/** \file    HtmlUtil.h
 *  \brief   Declarations of HTML-related utility functions.
 *  \author  Dr. Gordon W. Paynter
 *  \author  Dr. Johannes Ruscheinski
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

#ifndef HTML_UTIL_H
#define HTML_UTIL_H


#include <string>
#include <map>
#include <list>
#include <HtmlParser.h>
#include <StringMap.h>


namespace HtmlUtil {


bool DecodeEntity(const char *entity_string, char * const ch);

inline bool DecodeEntity(const std::string &entity_string, char * const ch)
	{ return DecodeEntity(entity_string.c_str(), ch); }


/** \brief   Strips HTML comments out of a string.
 *  \param   buf  A zero-terminated HTML string.
 *  \return  The address of the stripped HTML string.
 */
char *RemoveHtmlComments(char * const buf);


/** \brief   Replace all the HTML tags in a string with a single space.
 *  \param   s  A string that may contain HTML tags.
 *  \return  A reference to the altered string.
 */
std::string &RemoveTags(std::string * const s);


enum UnknownEntityMode { IGNORE_UNKNOWN_ENTITIES, REMOVE_UNKNOWN_ENTITIES };


/** \brief  Replaces all HTML entities in "s" with the actual characters.
 *  \param  s                    The string that may contain optional HTML entities.
 *  \param  unknown_entity_mode  tells whether to remove unknown entities.
 *  \note   It is probably a good idea to call RemoveTags before calling this function.
 */
std::string &ReplaceEntities(std::string * const s,
			     const UnknownEntityMode unknown_entity_mode = REMOVE_UNKNOWN_ENTITIES);

inline std::string ReplaceEntities(const std::string &s,
				   const UnknownEntityMode unknown_entity_mode = REMOVE_UNKNOWN_ENTITIES)
{
	std::string temp_s(s);
	ReplaceEntities(&temp_s, unknown_entity_mode);
	return temp_s;
}


/** \brief Combines removing tags and replacing entities.
 *  \param s                    The string from which to remove tags and replace entities.
 *  \param unknown_entity_mode  The Unknown Entity Mode.
 *  \return A string without tags and with the known entities replaced.
 */
inline std::string RemoveTagsAndReplaceEntities(const std::string &s, const UnknownEntityMode unknown_entity_mode = REMOVE_UNKNOWN_ENTITIES)
{
	std::string temp_s(s);
	RemoveTags(&temp_s);
	ReplaceEntities(&temp_s, unknown_entity_mode);
	return temp_s;
}


/** \brief   Test whether a string is correctly HTML-escaped.
 *  \param   raw_text  The text to test.
 *  \return  True if the string appears to be fully HTML escaped, otherwise false.
 *
 *  This function tests for the presence of '&', '<', '>' and quotes which are not escaped with "&amp;", "&lt;", "&gt;" etc.
 */
bool IsHtmlEscaped(const std::string &raw_text);


/** \brief   Convert ampersands, less-than signs, greater-than signs and quotes to HTML entities.
 *  \param   raw_text  The text to be processed.
 *  \return  The text on which the substitutions have taken place.
 */
std::string HtmlEscape(const std::string &raw_text);


/** \brief   Replace ampersands, less-than signs, greater-than signs and quotes with HTML entities.
 *  \param   raw_text  The text to be that will be updated.
 *  \return  The text on which the substitutions have taken place.
 */
std::string &HtmlEscape(std::string * const raw_text);


/** \brief  Replaces characters that would have special meaning in CGI parameters lists with a percent sign followed by
 *          two hexadecimal characters.
 *  \param  raw_text  The text to be processed.
 *  \return The text on which the substitutions have taken place.
 */
std::string CgiEscape(const std::string &raw_text);


/** \brief   Determines whether a document contains appropriate content based on meta tags, keywords etc.
 *  \param   document_source  An HTML document.
 *  \return  True if "document_source" has acceptable content, otherwise false.
 */
bool IsAppropriateDocument(const std::string &document_source);


/** \brief   Converts HTML documents to plain text, applying semiintelligent translation.
 *  \param   html_document    An HTML document.
 *  \param   max_line_length  The maximum line length of the generated plain text document.
 *  \return  The plain text version of "html_document".
 */
const std::string HtmlToText(const std::string &html_document, const unsigned max_line_length = 100);


/** \brief   Attempts to extract the character set from an HTML document.
 *  \param   html_document  Where to extract the information from.
 *  \param   charset        If not empty upon return this contains the character set of the document.
 *  \return  True if a character set was successfully extracted otherwise false.
 */
bool GetCharSet(const std::string &html_document, std::string * const charset);


bool IsProbablyEnglish(const std::string &html_document);


/** \brief   Analyse a web page to discover if it is a frameset.
 *  \param   url         The URL of the HTML document.
 *  \param   html        The HTML document to analyse.
 *  \param   frame_urls  Output parameter for the list of frame URLs (if any).
 *  \return  True if the page is a frameset, otherwise false.
 *
 *  The frame_urls will be valid Web URLs, and will be absolute URLs.
 */
bool FrameAnalysis(const std::string &url, const std::string &html, std::list<std::string> * const frame_urls);


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
			       const std::string &initial_selection = "", const unsigned indent_level = 5,
			       const unsigned indent_increment = 2);


/** \brief  Writes an HTML "select" element from a list of values to an output stream.
 *  \param  output              The stream to write to.
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
inline void GenerateSelectMenu(std::ostream &output, const std::string &select_name_attrib,
			       const std::list< std::pair<std::string, std::string> > &name_value_pairs,
			       const std::string &initial_selection = "", const unsigned indent_level = 5,
			       const unsigned indent_increment = 2)
{
        output << GenerateSelectMenu(select_name_attrib, name_value_pairs, initial_selection, indent_level,
				     indent_increment);
}


/** \brief  Outputs a message to a Web page.
 *  \param  title                        The title of the message page
 *  \param  message                      The message to be displayed
 *  \param  require_content_type_header  If true outputs a 'Content-type' header.
 *  \param  message_template             Should be a template for a complete HTML document containing template variables
 *                                       $TITLE and $MESSAGE, corresponding to "title" and "message" respectively.
 *  \note   This function outputs a message in HTML to std::cout.  Generally, you use it to send a message in a Web page,
 *          but you have to be careful that you have not already started outputting HTML.
 */
void DisplayMessage(const std::string &title, const std::string &message,
		    const bool require_content_type_header = false, const std::string &message_template = "");


/** \brief  Outputs a message to a Web page and exits.
 *  \param  title                        The title of the message page
 *  \param  message                      The message to be displayed
 *  \param  require_content_type_header  If true outputs a 'Content-type' header.
 *  \param  message_template             Should be a template for a complete HTML document containing template variables
 *                                       $TITLE and $MESSAGE, corresponding to "title" and "message" respectively.
 *  \note   This function outputs a message in HTML to std::cout.  Generally, you use it to send a message in a Web page,
 *          but you have to be careful that you have not already started outputting HTML.
 */
void DisplayMessageAndExit(const std::string &title, const std::string &message,
			   const bool require_content_type_header = false, const std::string &message_template = "");


struct DomTreeNode {
	friend class DomTreeParser;
	enum Type { HTML_TAG, TEXT }; // What kind of node we are.
	Type type_;
	std::string value_;
	HtmlParser::AttributeMap tag_attribs_; // Only used if "type_" is "HTML_TAG".
	const DomTreeNode * const parent_;
	std::list<DomTreeNode *> children_;
public:
	typedef bool NodeVisitor(const DomTreeNode * const node, void * const aux_data = NULL);
public:
	/** Destroys a DomTreeNode object. */
	~DomTreeNode();

	Type getType() const { return type_; }
	std::string getValue() const { return value_; }
	std::string &getValue() { return value_; }
	void addChild(DomTreeNode * const new_child) { children_.push_back(new_child); }

	/** \brief  Prints a (sub)tree representation for the DOM tree starting at the current node.
	 *  \param  output            The stream to write the tree representation to.
	 *  \param  indent_per_level  indent_per_level  How many leading spaces to add per level.
	 *  \param  current_level     The indentation level so far.
	 */
	void print(std::ostream &output, const unsigned indent_per_level = 2, const unsigned current_level = 0) const;

	/** Calls "node_visitor" with information about each node until either the entire tree has been visited or
	    "node_visitor" returns false. */
	void depthFirstVisit(NodeVisitor node_visitor, void * const aux_data = NULL) const;

	/** Factory method to create a DomTreeNode of type "HTML_TAG". */
	static DomTreeNode *CreateHtmlTagNode(const DomTreeNode * const parent, const std::string &tag_name,
					      const HtmlParser::AttributeMap &attribute_map);
	static DomTreeNode *CreateTextNode(const DomTreeNode * const parent, const std::string &text);
private:
	DomTreeNode(const Type type, const std::string &value, const DomTreeNode * const parent)
		: type_(type), value_(value), parent_(parent) { }
	DomTreeNode(const Type type, const std::string &value, const HtmlParser::AttributeMap &tag_attribs, const DomTreeNode * const parent)
		: type_(type), value_(value), tag_attribs_(tag_attribs), parent_(parent) { }
};


/** \brief    Parses an HTML document and produces a DOM tree from it.
 *  \param    html_document  The document to parse.
 *  \param    tree_root      Where to return the DOM tree.
 *  \warning  Make sure that you pass in the address of a "DomTreeNode *" for the "tree_root" parameter!
 */
void HtmlDocumentToDomTree(const std::string &html_document, DomTreeNode **tree_root);


/** Returns "<a href="url">url</a>" taking into account potentially problematic characters in "url". */
std::string UrlToAHref(const std::string &url);


} // namespace HtmlUtil


#endif // define HTML_UTIL_H
