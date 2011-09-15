/** \file    Template.h
 *  \brief   Template processing functions.
 *  \author  Dr. Johannes Ruscheinski
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

#ifndef TEMPLATE_H
#define TEMPLATE_H


#include <iostream>
#include <string>
#include <fstream>
#include <list>
#include <stdexcept>
#include <StringMap.h>


/** \namespace Template
 *  \brief     Contains functions dealing with a template processor.  Templates can contain simple macros ($VAR),
 *             conditionals etc.
 *
 *  This template processor supports the concept of display "themes" to change the look and feel of, for example, a Web
 *  page.  Themes are supported through a file system path prefix that can typically be changed through an application's
 *  config file.  In order to reduce the maintanence overhead in maintaining a family of themes it is, optionally,
 *  possible to specify a default theme path.  If such a path has been specified, every lookup will first be attempted in
 *  the default theme path and then the regular theme path.
 */
namespace Template {


typedef void (*CallbackFunction)(std::ostream &output, int argc, const char *argv[], const StringMap &macros,
				 const std::string &template_filename, const unsigned lineno,
				 const bool remove_leading_whitespace);


/** \brief   Get the path of the top-level template data directory.
 *  \return  The current template path.
 *
 *  Template files are stored in subdirectories of the template path.  This function gets the path of ther template data
 *  directory; this directory contains one subdirectory for each template "theme"; each of these theme subdirectories
 *  contains one or more template files, optionally arranged in subdirectories directories.
 */
std::string GetTemplatePath();


/** \brief   Set the top-level template data directory.
 *  \param   path   The new location of the template data files.
 */
void SetTemplatePath(const std::string &path);


/** \brief  Set the top-level default template data directory.
 *  \param  path   The new location of the default template data files.
 *  \note   The path set with this function call is where we first look for templates.  If we don't find a given template
 *          using this path, we look in the directory specified with SetTemplatePath().
 */
void SetDefaultTemplatePath(const std::string &path);


/** Returns the path set with SetDefaultTemplatePath(). */
std::string GetDefaultTemplatePath();


/** \brief   Get the default theme.
 *  \return  The current default theme.
 *
 *  The default theme is used when the calling function fails to provide a theme, and is used when a different theme is
 *  provided by a calling function but that theme does not work.
 */
std::string GetDefaultTheme();


/** \brief   Set the default theme.
 */
void SetDefaultTheme(const std::string &theme);


/** \brief  A callback used by the template processor to output dynamic content.
 *
 *  Callback functions can be passed to the template processor that can be used to generate dynamic text.  For example,
 *  iVia contains several callback functions for dynamically generating the HTML for menus whose optins change
 *  dynamically as the Infomine database changes.
 *
 *  Many template users will not need Callback functions.
 */
class Callback {
	std::string name_;
	void (*callback_)(std::ostream &output, int argc, const char *argv[],
			  const StringMap &macros, const std::string &template_filename,
			  const unsigned lineno, const bool remove_leading_whitespace);
public:
	Callback(const std::string &name, CallbackFunction callback): name_(name), callback_(callback) { }
	bool operator==(const std::string &name) const { return name_ == name; }
	void operator()(std::ostream &output, int argc, const char *argv[], const StringMap &macros, const std::string &template_filename,
			const unsigned lineno, const bool remove_leading_whitespace) const
		{ (*callback_)(output, argc, argv, macros, template_filename, lineno, remove_leading_whitespace); }
	const std::string getName() const { return name_; }
};


/** \brief  A list of Callbacks.
 */
class Callbacks {
	std::list<Callback> callbacks_;
public:
	typedef std::list<Callback>::const_iterator const_iterator;
public:
	Callbacks() { }
	const_iterator find(const std::string &function_name) const;
        /** \brief  Insert a value into a list of callbacks, replacing any old value.
         *  \param  name      The name by which the callback function is known in templates.
         *  \param  callback  The function to be associated with "name".
         *
         *  The pair (name, value) is stored in the callbacks.  If there is an existing value associated with name, it is
         *  deleted first.
         */
 	void insert(const std::string &name, CallbackFunction callback);

        /** \brief   Delete a (name,callback function) pair from a list of callbacks.
         *  \param   name   The name of the function to be deleted.
         *  \return  true if "name" was in the list of callbacks, false otherwise.
         */
	bool remove(const std::string &name);

	const_iterator begin() const { return callbacks_.begin(); }
	const_iterator end() const { return callbacks_.end(); }
	void push_back(const Callback &new_callback) { callbacks_.push_back(new_callback); }
};


/** \brief  Find the absolute filename of a themed template file.
 *  \param  relative_filename  The name of the file relative to the theme directory.
 *  \param  full_filename      The absolute path of the theme file will be put here.
 *  \param  theme              The theme to use, or an empty string for the default theme.
 *
 *  The full_filename is calculated from the 'share' directory location, the theme in use and the relative filename of
 *  the template file.
 */
void GetTemplateFileName(const std::string &relative_filename, std::string * const full_filename, const std::string &theme = "");


/** \brief   Find the absolute filename of a themed template file.
 *  \param   relative_filename  The name of the file relative to the theme directory.
 *  \param   theme              The theme to use, or an empty string for the default theme.
 *  \return  The absolute path of the theme file.
 *
 *  The full_filename is calculated from the 'share' directory location, the theme in use and the relative filename of
 *  the template file.
 */
std::string GetTemplateFileName(const std::string &relative_filename, const std::string &theme = "");


/** \brief  Read "filename" into "buffer".
 *  \param  filename                   The name of the file.
 *  \param  buffer                     The destination buffer.
 *  \param  remove_leading_whitespace  If true removes all leading spaces and tabs from the read file.
 *  \note   Does $INCLUDE processing.
 */
void ReadFile(const std::string &filename, std::string * const buffer, const bool remove_leading_whitespace = false);


/** \brief  Read a file identified by "theme" and "relative_filename" into "buffer".
 *  \param  theme                      The theme of the file.
 *  \param  relative_filename          The path of the file relative to the theme directory.
 *  \param  buffer                     The destination buffer.
 *  \param  remove_leading_whitespace  If true removes all leading spaces and tabs from the read file.
 *  \note   Does $INCLUDE processing.
 */
inline void ReadFile(const std::string &theme, const std::string &relative_filename,
		     std::string * const buffer, const bool remove_leading_whitespace = false)
	{
		ReadFile(GetTemplateFileName(relative_filename, theme), buffer, remove_leading_whitespace);
	}


/** \brief Substitutes \$-macros in "template_string" and outputs it to "output".
 *  \param  template_string            The string upon which macro-substition will be performed.
 *  \param  macros                     The macro names and their definitions.
 *  \param  callbacks                  User defined functions for the \$CALL mechanism.
 *  \param  output                     The stream where the finished string will be output.
 *  \param  template_filename          The name used for error reporting.
 *  \param  remove_leading_whitespace  If true removes all leading spaces and tabs from the read file.
 *  \param  result_macros              All macros defined after the call to ProcessString.
 *
 *  ProcessString supports regular macro expansion and a range of other string processing features.  Various conditionals
 *  are supported (\$IF, \$IFDEF, \$IFNDEF, \$ELSIF and \$ENDIF), a \$CALL(function_name) construct supports special
 *  functions (PRINT_MACROS, SUBSTR), or user defined functions, \$SET and \$UNSET can be used to alter macro definitions
 *  and finally \$FOREACH provides a simple looping construct.  \$DOLLAR can be used to emit a literal \$-sign.
 *
 *  \par Macro names
 *  Macro names must follow the pattern [A-Z][A-Z0-9_]*.  Except or occurrences within \$IFDEF or \$IFNDEF macro
 *  invocations must always correspond to defined macro names.  Optionally, macro names may be enclosed in braces.  This
 *  feature is used mostly to separate the macro name from following characters which would otherwise be parsed as part of
 *  the name.
 *
 *  \par Conditionals
 *  The simplest conditionals are \$IFDEF and \$IFNDEF.  They both take a simple macro name enclosed in parenthesis (the
 *  leading \$-sign on the macro name may be omitted).  E.g.  \$IFNDEF(\$FRED) will evaluate to true if \$FRED has not
 *  been defined.  \$IF and \$ELSIF take expressions enclosed in parenthesis.  Expressions may be built up from binary
 *  comparison operator "==", "!=", "<", ">", "<=" and ">=" and the "defined(variable_name) predicate.  The "defined
 *  predicate may take macro names with or without a leading \$-sign.  Concerning comparisons: both string and numeric
 *  comparisons are allowed, e.g. \$ELSIF(\$NUM >= 10 and \$NUM <= 20).  If one of the operands of a binary comparison is
 *  a number (which may be any signed or unsigned integer) and the other operand a macro, the macro must evaluate to a
    string that contains a valid integer, e.g. "-5".  If neither operand of a binary comparison is a number string
 *  comparisons are applied based on the ISO 8859-1 locale.  Multiple binary comparisons as well as defined predicates
 *  may be combined by the use of the keywords "and" and "or" which must be lowercase. "and" and "or" have the lowest
    precedence.  Parenthesis may be used for grouping and to override the default precedence rules.
 *
 *  \par Unary Operators
 *  The template processor supports one unary operator "!" which negates the result of another boolean expression.
 *  Example: $IF(!defined(VARABILE))
 *
 *  \par Special function calls
 *  The language supports a function call mechanism (via \$CALL(function name)) for builtin functions.
 *  \$CALL(PRINT_MACROS) debugging function inserts an HTML definition list displaying the currently defined macro
 *  variables and their values preceeded by an HTML level 2 header.  \$CALL(SUBSTR,\$VARIABLE,start_pos[,substr_length]))
 *  outputs the substring of the value of \$VARIABLE starting at "start_pos" either extending to the end of the value of
 *  \$VARIABLE or, if specified, at most "substr_length" characters.  \$CALL(TOUPPER,\$VARIABLE)) inserts the uppercased
 *  value of \$VARIABLE into the template output stream, \$CALL(TOLOWER,\$VARIABLE)) is the lowercase equivalent.
 *  \$CALL(INC,\$VARIABLE[,increment] increments the value of \$VARIABLE which must be integer-valued by "increment", or
 *  if "increment" has not been specified by 1.  \$CALL(DEC,\$VARIABLE[,decrement] decrements the value of \$VARIABLE
 *  which must be integer-valued by "decrement", or  *  if "decrement" has not been specified by 1.
 *  \$CALL(EXEC,prog,arg1,arg2,...,argN) executes an external program "prog" with the provided arguments and inserts
 *  its standard output in the template output stream.
 *
 *  \par SET and UNSET
 *  Macros can be set and unset via the \$SET and \$UNSET directives.  The syntax for \$SET is \$SET(macro_name, value)
 *  where value can be another macro name, a signed or unsigned integer constant or a double-quoted string constant.
 *  String constants can include optional double quotes or backslashes by preceeding them with a single backslash each.
 *  E.g. the value "A quoted string!" would have to be represented like this: "\"A quoted string!\"".  The macro_name in
 *  \$SET or \$UNSET can have an optional leading \$-sign, e.g. \$UNSET(FRED) and \$UNSET(\$FRED) are equivalent.
 *
 *  \par FOREACH and ENDFOREACH
 *  The syntax for this simple looping construct is \$FOREACH(ITER, \$KEYS, \$DELIMITER [ , \$MAX_ITERATION_COUNT ]).
 *  ITER must be a valid but not previously set macro name.  \$KEYS and \$DELIMITER can be both macro names or string
 *  constants.  The optional \$MAX_ITERATION_COUNT can be either a macro name that evaluates to an unsigned number or an
 *  unsigned number constant.  Each \$FOREACH must be followed by an "\$ENDFOREACH".  \$FOREACH-\$ENDFOREACH blocks may be
 *  nested.  Iteration is over the list of string values (keys) obtained by applying StringUtil::Split to \$KEYS with
 *  \$DELIMITER as the delimiter.  Iteration terminates when we run out of string values or when \$MAX_ITERATION_COUNT
 *  (defaults to UINT_MAX) has been reached.  During each iteration \$ITER will be set to the current key.\n
 *  An alternate for is \$FOREACH(ITER, \$PATTERN) which will loop over all macros that match the value of \$PATTERN.
 *  \$PATTERN can be either a string constant or a macro name.  The \$PATTERN must be a Perl-compatible regular expression.
 *
 *  \par Builtin functions
 *  Builtin functions start with an \@-sign followed by all uppercase letters.
 *  Currently \@ATOI, \@LENGTH, \@FIND, \@SUBSTR, \@YEAR, \@MONTH, \@DAY, \@YEAR_AS_STRING, \@MONTH_AS_STRING, and
 *  \@DAY_AS_STRING are available.  \@ATOI, \@LENGTH, \@FIND, \@YEAR, \@MONTH, and \@DAY can only be used as an operand
 *  to a comparison operator to force a numeric interpretation of it's argument.
 *  The function \@FIND takes two string arguments "haystack" and "needle".  It returns the position of the string "needle"
 *  in the string "haystack" or -1 if "needle" can't be found.
 *  Example: \$IF(\@ATOI(\$NUM1) > \@ATOI(\$NUM2)).
 *  \@SUBSTR(\$VARIABLE,start_pos[,optional_length]) allows the extraction of substring values and can be used in
 *  expressions in lieu of string-valued variables or string constants.
 *  \@YEAR, \@MONTH, \@DAY, \@YEAR_AS_STRING, \@MONTH_AS_STRING, and \@DAY_AS_STRING all take a single string argument
 *  that should match one of the formats supported by TimeUtil::StringToBrokenDownTime.
 *
 *  \par To facilitate debugging ProcessString defines the macro name "TEMPLATE_NAME" which is set to the value of
 *       "template_filename".
 *
 *  \note  \$INCLUDE is not replaced here but is handled in ReadFile!
 */
void ProcessString(const std::string &template_string, const StringMap &macros, const Callbacks &callbacks,
		   std::ostream &output = std::cout, const std::string &template_filename="*string*",
		   const bool remove_leading_whitespace = false, StringMap *result_macros = NULL);
inline void ProcessString(const std::string &template_string, const StringMap &macros, std::ostream &output=std::cout,
			  const bool remove_leading_whitespace = false, StringMap *result_macros = NULL)
	{ ProcessString(template_string, macros, Callbacks(), output, "*string*", remove_leading_whitespace, result_macros); }


/** \brief  Substitute macros in a file and outputs it to "output".
 *  \param  absolute_filename          The file upon which macro-substition will be performed.  If the filename is not
 *                                     not an absolute path, the path specified with SetDefauktTemplatePath() will be prefixed
 *                                     to it if the resulting file exists, otherwise the path specified with SetTemplatePath()
 *                                     will be prefixed to it.
 *  \param  macros                     The macro names and their definitions.
 *  \param  callbacks                  User defined functions for the \$CALL mechanism.
 *  \param  output                     The stream where the finished string will be output.
 *  \param  remove_leading_whitespace  If true removes all leading spaces and tabs from the read file.
 *  \param  result_macros              All macros defined after the call to ProcessFile.
 */
void ProcessFile(const std::string &absolute_filename, const StringMap &macros,
		 const Callbacks &callbacks, std::ostream &output = std::cout,
		 const bool remove_leading_whitespace = false, StringMap *result_macros = NULL);

inline void ProcessFile(const std::string &absolute_filename, const StringMap &macros,
			std::ostream &output = std::cout, const bool remove_leading_whitespace = false,
			StringMap *result_macros = NULL) {
	ProcessFile(absolute_filename, macros, Callbacks(), output, remove_leading_whitespace, result_macros);
}

/** \brief  Substitutes macros in file "absolute_filename" and outputs it to "output".
 *  \param  theme                      The theme of the file to process
 *  \param  relative_filename          The relative path of the input file
 *  \param  macros                     The macro names and their definitions.
 *  \param  callbacks                  User defined functions for the \$CALL mechanism.
 *  \param  output                     The stream where the finished string will be output.
 *  \param  result_macros              If not NULL, the macro set will be output here. Any changes that occurred
                                       during the processing of the file will be reflected here. This can be used
                                       to return values from the processing of the script.
 *  \param  remove_leading_whitespace  If true removes all leading spaces and tabs from the read file.
 */
inline void ProcessFile(const std::string &theme, const std::string &relative_filename, const StringMap &macros,
			const Callbacks &callbacks=Callbacks(), std::ostream &output=std::cout,
			const bool remove_leading_whitespace = false, StringMap *result_macros = NULL)
	{
		ProcessFile(GetTemplateFileName(relative_filename, theme), macros, callbacks, output,
			    remove_leading_whitespace, result_macros);
	}


/** \brief  Highlights requested words in the values of those macros found in "highlight_macros".
 *  \param  highlight_words   The words that should be highlighted (must be lowercase).  A "word" that ends in an
 *                            asterisk is considered to be a prefix and matches any word that starts out with
 *                            the prefix.
 *  \param  highlight_start   The string that initiates a highlighting sequence.
 *  \param  highlight_stop    The string that terminates a highlighting sequence.
 *  \param  highlight_macros  The list of macros whose values should be processed.
 *  \param  macros            The list of all macros.
 *  \param  skip_html         If true, then we will not make changes inside HTML tags.
 *  \param  stem              Stem words before comparing them for potential highlighting.
 *  \param  highlight_suffix  If non-empty, we add a new macro for each of the macros in "highlight_macros"
 *                            whose name is derived from the original name with "highlight_suffix" appended.
 *                            The highlighting is then performed on the values of the new macros.
 */
void HighlightMacros(const std::list<std::string> &highlight_words, const std::string &highlight_start,
		     const std::string &highlight_stop, const std::list<std::string> &highlight_macros,
		     StringMap * const macros, const bool skip_html = false, const bool stem = false,
		     const std::string highlight_suffix = "");


/** \brief  Initialises macros used for pagination.
 *  \param  current_page_no        The number of the page we want to display.
 *  \param  total_no_of_pages      How pages there are overall.
 *  \param  current_page_template  A string containing a "%1u" (that's a "one" and not the letter "l") used to generate the
 *                                 HTML for displaying the current page number, e.g.
 *                                 "<span class=\"current_page\">%1u</span>" in the $PAGE_LIST macro.
 *  \param  page_link_template     A string containing a "%1u" (that's a "one" and not the letter "l") used to generate the
 *                                 HTML for displaying links in $PAGE_LIST for page numbers, e.g.
 *                                 "<a class=\"page_link\" href=\"some_cgi_program?&page_no=%1u">%1u</a>".  Please note
 *                                 how the "%1u" has been repeated and may occur any number of times.
 *  \param  macros                 Where to store the settings for the $PAGE_LIST and $CURRENT_PAGE_NO macros.
 *  \param  page_delta             Up to how many pages to display before and after the current page.
 */
void Paginate(const unsigned current_page_no, const unsigned total_no_of_pages, const std::string &current_page_template,
	      const std::string &page_link_template, StringMap * const macros, const unsigned page_delta = 5);


} // namespace Template


#endif // ifndef TEMPLATE_H
