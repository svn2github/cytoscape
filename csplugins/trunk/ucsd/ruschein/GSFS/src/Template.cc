/** \file    Template.cc
 *  \brief   Implementation of HTML template utility functions.
 *  \author  Dr. Johannes Ruscheinski
 *  \author  Dr. Gordon W. Paynter
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

#include <Template.h>
#include <algorithm>
#include <stdexcept>
#include <stack>
#include <cctype>
#include <cerrno>
#include <cstdio>
#include <cstdlib>
#include <sys/wait.h>
#include <unistd.h>
#include <Compiler.h>
#include <FileUtil.h>
#include <MsgUtil.h>
#include <PerlCompatRegExp.h>
#include <SList.h>
#include <StringUtil.h>
#include <TextUtil.h>
#include <TimeUtil.h>


namespace {


std::string GetShareDir() {
#ifdef __MACH__
	return std::string(::getenv("HOME")) + "/share";
#else
	return SHARE_DIR;
#endif
}


std::string template_home_dir(GetShareDir() + "/html_templates/");
std::string default_template_home_dir;
std::string default_theme("defaults");
std::string current_filename;


__attribute__((noreturn)) void ThrowError(const std::string &msg) throw(std::exception)
{
	throw Exception(msg + " while processing \"" + current_filename + "\"!");
}


unsigned template_line_no;


inline void SkipWhiteSpace(const char *&cp)
{
	while (isspace(*cp)) {
		if (*cp == '\n')
			++template_line_no;
		++cp;
	}
}


// IsValidMacroName -- returns true iff "potential_macro_name" is a valid macro name.
//                     Please note that "potential_macro_name" must not start with a $-sign.
//
bool IsValidMacroName(const std::string &potential_macro_name)
{
	std::string::const_iterator ch(potential_macro_name.begin());
	if (ch == potential_macro_name.end())
		return false;

	// Macro names must start with an uppercase letter...
	if (unlikely(not isupper(*ch)))
		return false;

	// ...and be followed by any combination of uppercase letters, digits or underscores:
	for (++ch; ch != potential_macro_name.end(); ++ch) {
		if (not (isupper(*ch) or isdigit(*ch) or *ch == '_'))
			return false;
	}

	return true;
}


/** \return The macro name without a leading $-sign.
 */
std::string ExtractMacroName(const char *&cp)
{
	SkipWhiteSpace(cp);

	if (*cp == '$')
		++cp;

	const bool braces = *cp == '{';
	if (unlikely(braces)) // Macro name is enclosed in braces.
		++cp;

	if (unlikely(not isupper(*cp))) {
		std::string msg("macro names must start with an upper case letter (invalid letter is '");
		msg += *cp;
		msg += "')";
		ThrowError(msg);
	}

	std::string macro_name;
	for (; isupper(*cp) or isdigit(*cp) or *cp == '_'; ++cp)
		macro_name += *cp;

	if (braces) {
		if (*cp != '}')
			ThrowError("macro \"" + macro_name + "\" has opening brace without closing brace");
		++cp;
	}

	return macro_name;
}


// IsValidCallbackFunctionName -- valid function names start with a letter and are followed by an optional sequence of
//                                letters and numbers.
//
bool IsValidCallbackFunctionName(const std::string &name)
{
	std::string::const_iterator ch(name.begin());
	if (ch == name.end() or not isalpha(static_cast<unsigned char>(*ch)))
		return false;

	for (; ch != name.end(); ++ch)
		if (not isalpha(static_cast<unsigned char>(*ch)) and not isdigit(static_cast<unsigned char>(*ch))
		    and *ch != '_')
			return false;

	return true;
}


// ProcessStringConstant -- returns the contents of a string constant.  Deals with backslash escapes.
//
bool ProcessStringConstant(const char *&cp, std::string * const processed_string)
{
	processed_string->clear();

	for (/* Empty! */; *cp != '\0'; ++cp) {
		if (*cp == '\\') {
			++cp;
			if (unlikely(*cp == '\0'))
				return false;
		}
		else if (*cp == '"')
			return true;

		*processed_string += *cp;
	}

	return false;
}


/** \class  CallArg
 *  \brief  Describes a single argument in $CALL(func_name, arg1, arg2, ..., argN).
 */
class CallArg {
public:
	enum ArgType { INVALID_TYPE, FUNCTION_NAME, VARIABLE, STRING_CONSTANT, NUMERIC_CONSTANT };
private:
	ArgType arg_type_;
	std::string value_;
	std::string aux_value_; // What this stores depends on the type of the argument.
public:
	CallArg(const ArgType arg_type, const std::string &value, const std::string &aux_value = "")
		: arg_type_(arg_type), value_(value), aux_value_(aux_value) { }
	ArgType getArgType() const { return arg_type_; }
	std::string getValue() const { return value_; }
	std::string getAuxValue() const { return aux_value_; }
};


class CallArgs: public SList<CallArg> { };


bool ExtractArgs(const char *&cp, const StringMap &macros, const bool skipping, CallArgs * const args, std::string * const error_msg)
{
	error_msg->clear();

	while (*cp != ')' and *cp != '\0') {
		SkipWhiteSpace(cp);

		std::string current_arg, current_aux_arg_value;
		CallArg::ArgType current_arg_type(CallArg::INVALID_TYPE);

		const bool found_string_constant(*cp == '"');
		if (found_string_constant) {
			current_arg_type = CallArg::STRING_CONSTANT;
			++cp;
			if (not ProcessStringConstant(cp, &current_arg)) {
				*error_msg = "bad string constant in argument list";
				return false;
			}

			++cp;
		}
		else {
			while (not isspace(*cp) and *cp != ',' and *cp != ')' and *cp != '\0')
				current_arg += *cp++;
		}

		if (unlikely(*cp == '\0')) {
			*error_msg = "unexpected end of input in argument list(first)";
			return false;
		}

		if (not found_string_constant) {
			if (current_arg[0] == '$') { // Process macros.
				if (not skipping) {
					current_arg_type = CallArg::VARIABLE;
					current_aux_arg_value = current_arg.substr(1);
					StringMap::const_iterator macro(macros.find(current_aux_arg_value));
					if (unlikely(macro == macros.end())) {
						*error_msg = "undefined macro variable \"" + current_arg + "\" in argument list";
						return false;
					}

					current_arg = macro->second;
				}
			}
			else if (unlikely(current_arg.empty())) {
				*error_msg = "invalid empty argument in argument list";
				return false;
			}
			else if (StringUtil::IsDigit(current_arg[0]) or current_arg[0] == '+' or current_arg[0] == '-') {
				const std::string rest(current_arg.substr(1));
				if (not rest.empty() and not StringUtil::IsUnsignedNumber(rest)) {
					*error_msg = "bad numeric constant \"" + current_arg + "\" in argument list";
					return false;
				}
				current_arg_type = CallArg::NUMERIC_CONSTANT;
			}
		}

		if (not skipping)
			args->push_back(CallArg(current_arg_type, current_arg, current_aux_arg_value));

		SkipWhiteSpace(cp);

		if (unlikely(*cp != ')' and *cp != ',')) {
			*error_msg = "invalid character in argument list ('" + TextUtil::EscapeChar(*cp) + "')";
			return false;
		}

		if (*cp == ',') {
			++cp; // skip over ','

			SkipWhiteSpace(cp);

			if (unlikely(*cp == ')')) {
				*error_msg = "missing argument after comma in argument list";
				return false;
			}
		}
	}

	if (unlikely(*cp == '\0')) {
		*error_msg = "unexpected end of input in argument list(second)";
		return false;
	}

	if (unlikely(args->size() == 0)) {
		*error_msg = "no arguments found in argument list";
		return false;
	}

	return true;
}


/** \brief Extracts arguments for $CALL(func_name, arg1, arg2, ..., argN).
 *  \note  The function name is returned as arg0 and as the return value.
 */
std::string ExtractCallArgs(const char *&cp, const StringMap &macros, const bool skipping, CallArgs * const args)
{
	// Extract the function name first:
	SkipWhiteSpace(cp);
	std::string function_name;
	while (not isspace(*cp) and *cp != ',' and *cp != ')' and *cp != '\0')
		function_name += *cp++;
	if (unlikely(function_name.empty()))
		ThrowError("missing function name in $CALL");
	args->push_back(CallArg(CallArg::FUNCTION_NAME, function_name));

	// Now get the remaining arguments if any:
	SkipWhiteSpace(cp);
	std::string error_msg;
	if (*cp == ',' and not ExtractArgs(++cp, macros, skipping, args, &error_msg))
		ThrowError("error while processing the $CALL argument list of \"" + function_name + "\" (" + error_msg + ")");

	return function_name;
}


// ExtractStringConstant -- extracts a double-quote delimited string constant (omitting the delimiters in "string_value").
//
bool ExtractStringConstant(const char *&cp, std::string * const string_value)
{
	string_value->clear();

	if (*cp != '"')
		return false;

	for (++cp; *cp != '"'; ++cp) {
		if (unlikely(*cp == '\0'))
			return false;

		if (unlikely(*cp == '\\')) {
			++cp;
			if (unlikely(*cp == '\0'))
				return false;
			switch(*cp) {
			case 'n':
				*string_value += '\n';
				break;
			case 't':
				*string_value += '\t';
				break;
			case 'b':
				*string_value += '\b';
				break;
			case 'r':
				*string_value += '\r';
				break;
			case 'f':
				*string_value += '\f';
				break;
			case 'v':
				*string_value += '\v';
				break;
			case 'a':
				*string_value += '\a';
				break;
			default:
				*string_value += *cp;
			}
		}
		else
			*string_value += *cp;
	}

	++cp;

	return true;
}


// ExtractUnsignedNumber -- extracts an unsigned number.
//
bool ExtractUnsignedNumber(const char *&cp, unsigned * const numeric_value, const unsigned base = 10)
{
	if (not StringUtil::IsDigit(*cp))
		return false;
	*numeric_value = *cp - '0';

	while (StringUtil::IsDigit(*++cp))
		*numeric_value = base * (*numeric_value) + (*cp - '0');

	return true;
}


// ExtractForeachArgs -- extracts arguments for $FOREACH(ITER, $KEYS, delimiter [ , max_iteration_count ]) or
//                       $FOREACH(ITER, $PATTERN).
//
void ExtractForeachArgs(const char *&cp, const StringMap &macros, const bool skipping,
			std::string * const iterator_variable_name, std::list<std::string> * const keys,
			unsigned * const max_iteration_count, std::string * const pattern)
{
	keys->clear();
	pattern->clear();

	SkipWhiteSpace(cp);
	*iterator_variable_name = ExtractMacroName(cp);
	SkipWhiteSpace(cp);

	if (unlikely(*cp != ','))
		ThrowError("missing comma in $FOREACH after iterator variable name");
	++cp;
	SkipWhiteSpace(cp);

	std::string keys_or_pattern_string;
	if (*cp == '$') {
		const std::string keys_or_pattern_macro = ExtractMacroName(cp);
		if (not skipping) {
			const StringMap::const_iterator keys_or_pattern_iter(macros.find(keys_or_pattern_macro));
			if (keys_or_pattern_iter == macros.end())
				ThrowError("undefined macro variable \"" + keys_or_pattern_macro + "\" "
					   "in \"keys\" slot of $FOREACH");
			keys_or_pattern_string = keys_or_pattern_iter->second;
		}
	}
	else if (*cp == '"') {
		if (not ExtractStringConstant(cp, &keys_or_pattern_string))
			ThrowError("bad string constant in \"keys\" slot of $FOREACH");	}
	else
		ThrowError("expecting variable or string constant in \"keys\" slot of $FOREACH");

	SkipWhiteSpace(cp);

	// Are we dealing with the short form of the $FOREACH construct?
	if (*cp == ')') { // Yes!
		*pattern = keys_or_pattern_string;
		return;
	}

	if (unlikely(*cp != ','))
		ThrowError("missing comma in $FOREACH after \"keys\" variable name or string constant");
	++cp;
	SkipWhiteSpace(cp);

	std::string delimiter;
	if (*cp == '$') {
		const std::string delimiter_macro = ExtractMacroName(cp);
		if (not skipping) {
			const StringMap::const_iterator delimiter_iter(macros.find(delimiter_macro));
			if (delimiter_iter == macros.end())
				ThrowError("undefined macro variable \"" + delimiter_macro + "\" "
							 "in \"delimiter\" slot of $FOREACH");
			delimiter = delimiter_iter->second;
		}
	}
	else if (*cp == '"') {
		if (not ExtractStringConstant(cp, &delimiter))
			ThrowError("bad \"delimiter\" string constant in $FOREACH");
	}
	else
		ThrowError("expecting variable or string constant in \"delimiter\" slot of $FOREACH");

	StringUtil::Split(keys_or_pattern_string, delimiter, keys, /* suppress_empty_components  = */ false);
	SkipWhiteSpace(cp);

	// Extract the optional max. iteration count:
	if (*cp == ',') {
		++cp;
		SkipWhiteSpace(cp);

		if (*cp == '$') {
			const std::string max_iteration_count_macro = ExtractMacroName(cp);
			if (not skipping) {
				const StringMap::const_iterator max_iteration_count_iter(
					macros.find(max_iteration_count_macro));
				if (max_iteration_count_iter == macros.end())
					ThrowError("undefined macro variable \"" + max_iteration_count_macro
						   + "\" in \"max_iteration_count\" slot of $FOREACH");
				if (not StringUtil::ToNumber(max_iteration_count_iter->second, max_iteration_count))
					ThrowError("macro variable \"" + max_iteration_count_macro
						   + "\" doesn't contain a numeric value in "
						   "\"max_iteration_count\" slot of $FOREACH");
			}
		}
		else if (not ExtractUnsignedNumber(cp, max_iteration_count))
			ThrowError("invalid max. iteration count in $FOREACH");

		SkipWhiteSpace(cp);
	}
}


// BuiltinSUBSTR -- returns the value of @SUBSTR(string_variable, start_pos [ , substr_length ]).
//
std::string BuiltinSUBSTR(const std::string &string_variable, const std::string &start_pos, const std::string &substr_length)
{
	if (string_variable.empty())
		ThrowError("missing 1st argument in call to SUBSTR");

	long unsigned start_pos_value;
	if (start_pos.empty())
		ThrowError("missing 2nd argument in call to SUBSTR function");
	if (not StringUtil::ToUnsignedLong(start_pos, &start_pos_value))
		ThrowError("expecting unsigned-valued variable or string constant in \"start_pos\" slot of SUBSTR function");

	// Extract the optional requested substring length:
	if (not substr_length.empty()) {
		long unsigned substr_length_value;
		if (not StringUtil::ToUnsignedLong(substr_length, &substr_length_value))
			ThrowError("invalid non-unsigned value in \"substr_length\" slot of SUBSTR function");

		if (start_pos_value >= string_variable.length())
			return "";
		if (start_pos_value + substr_length_value >= string_variable.length())
			return string_variable.substr(start_pos_value);
		return string_variable.substr(start_pos_value, substr_length_value);
	}
	else // The optional substring length was not specified.
		return (start_pos_value >= string_variable.length()) ? "" : string_variable.substr(start_pos_value);
}


long BuiltinATOI(const std::string &s)
{
	errno = 0;
	char *endptr;
	long retval = std::strtol(s.c_str(), &endptr, 10);
	if (unlikely(errno != 0 or *endptr != '\0'))
		ThrowError("conversion error in ATOI: \"" + s + "\" is not a valid integer");

	return retval;
}


long BuiltinYEAR(const std::string &date_and_time)
{
	unsigned year, month, day, hour, minute, second;
	bool is_definitely_zulu_time;
	const unsigned match_count = TimeUtil::StringToBrokenDownTime(date_and_time, &year, &month, &day, &hour,
								      &minute, &second, &is_definitely_zulu_time);
	if (unlikely(match_count == 0))
		ThrowError("conversion error in YEAR: \"" + date_and_time + "\" is not a recognisable date");

	return year;
}


long BuiltinMONTH(const std::string &date_and_time)
{
	unsigned year, month, day, hour, minute, second;
	bool is_definitely_zulu_time;
	const unsigned match_count = TimeUtil::StringToBrokenDownTime(date_and_time, &year, &month, &day, &hour,
								      &minute, &second, &is_definitely_zulu_time);
	if (unlikely(match_count == 0))
		ThrowError("conversion error in MONTH: \"" + date_and_time + "\" is not a recognisable date");

	return month;
}


long BuiltinDAY(const std::string &date_and_time)
{
	unsigned year, month, day, hour, minute, second;
	bool is_definitely_zulu_time;
	const unsigned match_count = TimeUtil::StringToBrokenDownTime(date_and_time, &year, &month, &day, &hour,
								      &minute, &second, &is_definitely_zulu_time);
	if (unlikely(match_count == 0))
		ThrowError("conversion error in DAY: \"" + date_and_time + "\" is not a recognisable date");

	return day;
}


enum Token { TOK_STRING, TOK_NUMBER, TOK_FUNCTION_NAME, TOK_LT, TOK_GT, TOK_LE, TOK_GE, TOK_EQ,
	     TOK_NE, TOK_EOF, TOK_AND, TOK_OR, TOK_LPAREN, TOK_RPAREN, TOK_COMMA, TOK_DEFINED, TOK_NOT } pushed_back_token;


// the following variables are used to communicate with and between GetToken and UngetToken:
bool pushed_back;
std::string string_token_value;
std::string function_name_token_value;
long numeric_token_value;
const char *next_ch;


Token GetToken(const StringMap &macros, const bool skipping) throw(std::exception)
{
	if (pushed_back) {
		pushed_back = false;
		return pushed_back_token;
	}

	SkipWhiteSpace(next_ch);

	if (*next_ch == '\0')
		return TOK_EOF;
	else if (*next_ch == '<') {
		++next_ch;
		if (*next_ch != '=')
			return TOK_LT;
		++next_ch;
		return TOK_LE;
	}
	else if (*next_ch == '>') {
		++next_ch;
		if (*next_ch != '=')
			return TOK_GT;
		++next_ch;
		return TOK_GE;
	}
	else if (*next_ch == '=') {
		++next_ch;
		if (unlikely(*next_ch != '='))
			ThrowError("second '=' expected during macro condition evaluation");
		++next_ch;
		return TOK_EQ;
	}
	else if (*next_ch == '!') {
		++next_ch;
		if (unlikely(*next_ch != '='))
			return TOK_NOT;
		++next_ch;
		return TOK_NE;
	}
	else if (*next_ch == '"') {
		if (unlikely(not ExtractStringConstant(next_ch, &string_token_value)))
			ThrowError("invalid string constant found during macro condition evaluation");
		return TOK_STRING;
	}
	else if (*next_ch == '$') {
		std::string macro_name(ExtractMacroName(next_ch));

		if (not skipping) {
			// Replace macro name with macro value:
			StringMap::const_iterator macro(macros.find(macro_name));
			if (unlikely(macro == macros.end())) // not found!
				ThrowError("in GetToken: macro \"" + macro_name + "\" not defined");
			string_token_value = macro->second.c_str();
		}

		return TOK_STRING;
	}
	else if (*next_ch == '@') {
		++next_ch;
		if (unlikely(not isupper(*next_ch)))
			ThrowError("invalid character after @-sign");

		function_name_token_value = ExtractMacroName(next_ch);
		if (unlikely(function_name_token_value != "ATOI" and function_name_token_value != "LENGTH"
			     and function_name_token_value != "FIND" and function_name_token_value != "SUBSTR"
			     and function_name_token_value != "YEAR_AS_STRING"
			     and function_name_token_value != "MONTH_AS_STRING"
			     and function_name_token_value != "DAY_AS_STRING"
			     and function_name_token_value != "YEAR" and function_name_token_value != "MONTH"
			     and function_name_token_value != "DAY"))
			ThrowError("bad function name \"" + function_name_token_value + "\"");
		return TOK_FUNCTION_NAME;
	}
	else if (isdigit(*next_ch) or *next_ch == '+' or *next_ch == '-') {
		// process optional leading sign:
		long sign = +1;
		if (*next_ch == '+' or *next_ch == '-') {
			sign = (*next_ch == '+') ? +1 : -1;
			++next_ch;
		}

		char *endptr;
		numeric_token_value = sign * std::strtol(next_ch, &endptr, 10);
		next_ch = endptr;
		return TOK_NUMBER;
	}
	else if (*next_ch == 'n') {
		if (likely(std::strncmp(next_ch, "not", 3) == 0)) {
			next_ch += 3;
			return TOK_NOT;
		}
		ThrowError("syntax error found during macro condition evaluation");
	}
	else if (*next_ch == 'a') {
		if (likely(std::strncmp(next_ch, "and", 3) == 0)) {
			next_ch += 3;
			return TOK_AND;
		}
		ThrowError("syntax error found during macro condition evaluation");
	}
	else if (*next_ch == 'o') {
		if (likely(std::strncmp(next_ch, "or", 2) == 0)) {
			next_ch += 2;
			return TOK_OR;
		}
		ThrowError("syntax error found during macro condition evaluation");
	}
	else if (*next_ch == 'd') {
		if (likely(std::strncmp(next_ch, "defined", 7) == 0)) {
			next_ch += 7;
			return TOK_DEFINED;
		}
		ThrowError("syntax error found during macro condition evaluation");
	}
	else if (*next_ch == '(') {
		++next_ch;
		return TOK_LPAREN;
	}
	else if (*next_ch == ')') {
		++next_ch;
		return TOK_RPAREN;
	}
	else if (*next_ch == ',') {
		++next_ch;
		return TOK_COMMA;
	}
	else {
		if (isprint(*next_ch)) {
			std::string msg("unexpected character '");
			msg += *next_ch;
			msg += "' found during macro condition evaluation";
			ThrowError(msg);
		}
		else
			ThrowError("unexpected character found during macro condition evaluation");
	}
}


void UngetToken(Token token)
{
	MSG_UTIL_ASSERT(not pushed_back);
	pushed_back_token = token;
	pushed_back = true;
}


// forward declaration(s):
bool Term(const StringMap &macros, const bool skipping);


bool Expr(const StringMap &macros, const bool skipping)
{
	bool retval = Term(macros, skipping);

	Token token;
	while ((token = GetToken(macros, skipping)) != TOK_EOF) {
		if (unlikely(token != TOK_AND and token != TOK_OR))
			ThrowError("in EvalCondition: 'and' or 'or' expected");
		if (token == TOK_AND and not retval) // Boolean short-circuiting for 'and'.
			return false;
		else if (token == TOK_OR and retval) // Boolean short-circuiting for 'or'.
			return true;

		const bool temp = Term(macros, skipping);
		if (token == TOK_AND)
			retval = retval and temp;
		else if (not retval)
			retval = temp;
	}

	return retval;
}


bool IsBinaryOp(const Token token)
{
	switch (token) {
	case TOK_EQ:
	case TOK_NE:
	case TOK_LT:
	case TOK_LE:
	case TOK_GT:
	case TOK_GE:
		return true;
	default:
		return false;
	}
}


bool IsNumericFunction(const std::string &function_name)
{
	return function_name == "ATOI" or function_name == "LENGTH" or function_name == "FIND" or function_name == "YEAR"
	       or function_name == "MONTH" or function_name == "DAY";
}


bool IsStringFunction(const std::string &function_name)
{
	return function_name == "SUBSTR" or function_name == "YEAR_AS_STRING" or function_name == "MONTH_AS_STRING"
	       or function_name == "DAY_AS_STRING";
}


long GetNumericFunctionValue(const std::string &function_name, const StringMap &macros, const bool skipping)
{
	Token token(GetToken(macros, skipping));
	if (unlikely(token != TOK_LPAREN))
		ThrowError("missing '(' after function name \"" + function_name + "\"");

	long retval;
	if (function_name == "ATOI") {
		token = GetToken(macros, skipping);
		if (unlikely(token != TOK_STRING))
			ThrowError("string-valued argument expected in @ATOI function call");
		retval = BuiltinATOI(string_token_value);
	}
	else if (function_name == "LENGTH") {
		token = GetToken(macros, skipping);
		if (unlikely(token != TOK_STRING))
			ThrowError("string-valued argument expected in @LENGTH function call");
		retval = string_token_value.length();
	}
	else if (function_name == "FIND") {
		token = GetToken(macros, skipping);
		if (unlikely(token != TOK_STRING))
			ThrowError("string-valued first argument expected in @FIND function call");
		const std::string haystack(string_token_value);

		token = GetToken(macros, skipping);
		if (unlikely(token != TOK_COMMA))
			ThrowError("comma expected after first argument in @FIND function call");

		token = GetToken(macros, skipping);
		if (unlikely(token != TOK_STRING))
			ThrowError("string-valued second argument expected in @FIND function call");
		const std::string needle(string_token_value);

		const std::string::size_type offset = haystack.find(needle);
		retval = (offset == std::string::npos) ? -1L : static_cast<long>(offset);
	}
	else if (function_name == "YEAR") {
		token = GetToken(macros, skipping);
		if (unlikely(token != TOK_STRING))
			ThrowError("string-valued argument expected in @YEAR function call");
		retval = BuiltinYEAR(string_token_value);
	}
	else if (function_name == "MONTH") {
		token = GetToken(macros, skipping);
		if (unlikely(token != TOK_STRING))
			ThrowError("string-valued argument expected in @MONTH function call");
		retval = BuiltinMONTH(string_token_value);
	}
	else if (function_name == "DAY") {
		token = GetToken(macros, skipping);
		if (unlikely(token != TOK_STRING))
			ThrowError("string-valued argument expected in @DAY function call");
		retval = BuiltinDAY(string_token_value);
	}
	else
		ThrowError("unknown numeric function  \"" + function_name + "\"");

	token = GetToken(macros, skipping);
	if (unlikely(token != TOK_RPAREN))
		ThrowError("missing ')' after function argument for \"" + function_name
			   + "\"");

	return retval;
}


std::string GetStringFunctionValue(const std::string &function_name, const char *&cp, const StringMap &macros,
				   const bool skipping)
{
	Token token(GetToken(macros, skipping));
	if (unlikely(token != TOK_LPAREN))
		ThrowError("missing '(' after function name \"" + function_name + "\"");

	std::string retval;
	if (function_name == "SUBSTR") {
		CallArgs args;
		std::string error_msg;
		if (not ExtractArgs(cp, macros, false, &args, &error_msg) or (args.size() != 2 and args.size() != 3))
			ThrowError("error while processing SUBSTR function arguments (" + error_msg + ")");

		CallArgs::const_iterator arg(args.begin());
		CallArg string_arg(*arg++);
		std::string optional_substr_length;
		CallArg start_pos_arg = *arg++;
		if (arg != args.end())
			optional_substr_length = arg->getValue();

		retval = BuiltinSUBSTR(string_arg.getValue(), start_pos_arg.getValue(), optional_substr_length);
	}
	else if (function_name == "YEAR_AS_STRING") {
		CallArgs args;
		std::string error_msg;
		if (not ExtractArgs(cp, macros, false, &args, &error_msg) or args.size() != 1)
			ThrowError("error while processing YEAR_AS_STRING function arguments (" + error_msg + ")");

		retval = BuiltinYEAR(args.front().getValue());
	}
	else if (function_name == "MONTH_AS_STRING") {
		CallArgs args;
		std::string error_msg;
		if (not ExtractArgs(cp, macros, false, &args, &error_msg) or args.size() != 1)
			ThrowError("error while processing MONTH_AS_STRING function arguments (" + error_msg + ")");

		retval = BuiltinMONTH(args.front().getValue());
	}
	else if (function_name == "DAY_AS_STRING") {
		CallArgs args;
		std::string error_msg;
		if (not ExtractArgs(cp, macros, false, &args, &error_msg) or args.size() != 1)
			ThrowError("error while processing DAY_AS_STRING function arguments (" + error_msg + ")");

		retval = BuiltinDAY(args.front().getValue());
	}
	else
		ThrowError("unknown string function  \"" + function_name + "\"");

	token = GetToken(macros, skipping);
	if (unlikely(token != TOK_RPAREN))
		ThrowError("missing ')' after function argument for \"" + function_name + "\"");

	return retval;
}


bool Term(const StringMap &macros, const bool skipping)
{
	bool retval(false);

	Token token(GetToken(macros, skipping));
	if (token == TOK_NOT) {
		retval = not Expr(macros, skipping);
	}
	else if (token == TOK_LPAREN) {
		token = GetToken(macros, skipping);
		retval = Expr(macros, skipping);
		if (unlikely(token != TOK_RPAREN))
			ThrowError("in Term: ')' expected");
	}
	else if (token == TOK_DEFINED) {
		token = GetToken(macros, skipping);
		if (unlikely(token != TOK_LPAREN))
			ThrowError("in Term: '(' expected after \"defined\" operator");
		if (not skipping)
			retval = macros.find(ExtractMacroName(next_ch)) != macros.end();
		token = GetToken(macros, skipping);
		if (unlikely(token != TOK_RPAREN))
			ThrowError("in Term: ')' expected after \"defined\" predicate");
	}
	else {
		if (unlikely(token != TOK_STRING and token != TOK_NUMBER and token != TOK_FUNCTION_NAME))
			ThrowError("in Term: string or number or function expected");

		if (token == TOK_NUMBER or (token == TOK_FUNCTION_NAME and IsNumericFunction(function_name_token_value))) {
			long n1 = (token == TOK_NUMBER) ? numeric_token_value
				                        : GetNumericFunctionValue(function_name_token_value,
										  macros, skipping);

			Token bin_op_token = GetToken(macros, skipping);
			if (unlikely(not IsBinaryOp(bin_op_token)))
				ThrowError("in Term: binary operator after numeric token expected");

			token = GetToken(macros, skipping);
			long n2;
			if (token == TOK_NUMBER or token == TOK_FUNCTION_NAME)
				n2 = (token == TOK_NUMBER) ? numeric_token_value
				                           : GetNumericFunctionValue(function_name_token_value,
										     macros, skipping);
			else if (unlikely(token != TOK_STRING or !StringUtil::ToNumber(string_token_value, &n2)))
				ThrowError("in Term: number expected after binary operator");

			switch (bin_op_token) {
			case TOK_EQ:
				retval = n1 == n2;
				break;
			case TOK_NE:
				retval = n1 != n2;
				break;
			case TOK_LT:
				retval = n1 < n2;
				break;
			case TOK_LE:
				retval = n1 <= n2;
				break;
			case TOK_GT:
				retval = n1 > n2;
				break;
			case TOK_GE:
				retval = n1 >= n2;
				break;
			default:
				MsgUtil::Error("in Term: unhandled binary operator (%d)!",
					       static_cast<int>(bin_op_token));
			}
		}
		else {
			MSG_UTIL_ASSERT(token == TOK_STRING
					or (token == TOK_FUNCTION_NAME and IsStringFunction(function_name_token_value)));
			std::string s1(token == TOK_STRING ? string_token_value
				                           : GetStringFunctionValue(function_name_token_value,
										    next_ch, macros,
										    skipping));

			Token bin_op_token = GetToken(macros, skipping);
			if (unlikely(not IsBinaryOp(bin_op_token)))
				ThrowError("in Term: binary operator after string token expected");

			token = GetToken(macros, skipping);
			long n1;
			if (unlikely(token != TOK_STRING and
				     (token != TOK_NUMBER or !StringUtil::ToNumber(s1, &n1))))
				ThrowError("in Term: invalid comparison using binary operator");
			else if (token == TOK_STRING) {
				std::string s2(string_token_value);

				switch (bin_op_token) {
				case TOK_EQ:
					retval = s1 == s2;
					break;
				case TOK_NE:
					retval = s1 != s2;
					break;
				case TOK_LT:
					retval = s1 < s2;
					break;
				case TOK_LE:
					retval = s1 <= s2;
					break;
				case TOK_GT:
					retval = s1 > s2;
					break;
				case TOK_GE:
					retval = s1 >= s2;
					break;
				default:
					MsgUtil::Error("in Term: unhandled binary operator (%d)!",
						       static_cast<int>(bin_op_token));
			}
			}
			else {
				MSG_UTIL_ASSERT(token == TOK_NUMBER);
				long n2 = numeric_token_value;

				switch (bin_op_token) {
				case TOK_EQ:
					retval = n1 == n2;
					break;
				case TOK_NE:
					retval = n1 != n2;
					break;
				case TOK_LT:
					retval = n1 < n2;
					break;
				case TOK_LE:
					retval = n1 <= n2;
					break;
				case TOK_GT:
					retval = n1 > n2;
					break;
				case TOK_GE:
					retval = n1 >= n2;
					break;
				default:
					MsgUtil::Error("in Term: unhandled binary operator (%d)!",
						       static_cast<int>(bin_op_token));
				}
			}
		}
	}

	return retval;
}


/**  \brief A helper function for "ProcessString".
 */
bool EvalCondition(const std::string &condition, const StringMap &macros, const bool skipping)
{
	next_ch = condition.c_str();
	pushed_back = false;

	return Expr(macros, skipping);
}


std::string ExtractCondition(const char *&cp)
{
	if (unlikely(*cp != '('))
		ThrowError("'(' expected at start of a condition");
	++cp;

	unsigned unmatched_open_brace_count = 1; // how many '(' without having seen a matching ')'.

	bool in_string_constant = false;
	for (std::string condition; *cp != '\0'; ++cp) {
		if (*cp == '\\' and in_string_constant) {
			++cp;
			if (unlikely(*cp == '\0'))
				ThrowError("unterminated condition found");
		}
		else if (*cp == '"') {
			if (in_string_constant)
				in_string_constant = false;
			else
				in_string_constant = true;
		}
		else if (*cp == '(' and !in_string_constant)
			++unmatched_open_brace_count;
		else if (*cp == ')' and !in_string_constant) {
			--unmatched_open_brace_count;
			if (unmatched_open_brace_count == 0) {
				++cp;
				return StringUtil::Trim(&condition);
			}
		}

		condition += *cp;
	}

	ThrowError("incomplete condition found");
}


void CallPrintMacros(const StringMap &macros, const Template::Callbacks &callbacks,
		     std::ostream &output)
{
	if (unlikely(macros.empty()))
		output << "<strong>No macros defined!</strong>\n";
	else {
		output << "<h2>Defined macros</h2>\n";
		output << "<dl>\n";
		for (StringMap::const_iterator macro(macros.begin()); macro != macros.end();
		     ++macro)
			output << "  <dt>" << macro->first << "</dt><dd>"
			       << macro->second << "</dd>\n";
		output << "</dl>\n";
	}

	output << "<h2>Defined callbacks</h2>\n";
	output << "<ul>\n";
	output << "  <li>PRINT_MACROS (builtin)</li>\n";
	output << "  <li>EXEC (builtin)</li>\n";
	output << "  <li>INC (builtin)</li>\n";
	output << "  <li>DEC (builtin)</li>\n";
	output << "  <li>SUBSTR (builtin)</li>\n";
	output << "  <li>TOUPPER (builtin)</li>\n";
	output << "  <li>TOLOWER (builtin)</li>\n";
	output << "  <li>YEAR_AS_STRING (builtin)</li>\n";
	output << "  <li>MONTH_AS_STRING (builtin)</li>\n";
	output << "  <li>DAY_AS_STRING (builtin)</li>\n";
	for (Template::Callbacks::const_iterator callback(callbacks.begin()); callback != callbacks.end(); ++callback)
		output << "  <li>" << callback->getName() << "</li>\n";
	output << "</ul>\n";
}


void CallExec(const CallArgs &args, std::ostream &output)
{
	const unsigned arg_count(args.size());
	if (arg_count < 1)
		ThrowError("invalid number of arguments in $CALL(EXEC,...)");

	CallArgs::const_iterator arg(args.begin());
	const std::string executable_path(arg->getValue());

	char *argv[args.size()];
	std::string dirname, basename;
	FileUtil::DirnameAndBasename(executable_path, &dirname, &basename);
	argv[0] = ::strdup(basename.c_str());

	unsigned index(1);
	for (++arg; arg != args.end(); ++arg)
		argv[index++] = ::strdup(arg->getValue().c_str());
	argv[index] = NULL;

	int pipe_from_child_fds[2];
	if (::pipe(pipe_from_child_fds) == -1)
		ThrowError("in CallExec: pipe(2) failed " + MsgUtil::ErrnoToString());

	pid_t pid = ::fork();
	if (pid == -1)
		throw Exception("in CallExec: fork(2) failed! (Out of memory?)");

	if (pid == 0) { // We're the child.
		::close(pipe_from_child_fds[0]);
		if (::dup2(pipe_from_child_fds[1], STDOUT_FILENO) == -1)
			ThrowError("in CallExec: dup2(2) failed in child " + MsgUtil::ErrnoToString());
		::execv(executable_path.c_str(), argv);
		ThrowError("in CallExec: execv(3) failed in child " + MsgUtil::ErrnoToString());
	}

	//
	// If we make it here, we're the parent!
	//

	::close(pipe_from_child_fds[1]);

	char buf[BUFSIZ];
	ssize_t byte_count;
	while ((byte_count = ::read(pipe_from_child_fds[0], buf, sizeof buf)) > 0)
		output.write(buf, byte_count);

	::close(pipe_from_child_fds[0]);

	if (byte_count < 0)
		ThrowError("in CallExec: read(2) failed: " + MsgUtil::ErrnoToString());

	int status;
	if (::waitpid(pid, &status, 0) == -1)
		ThrowError("in CallExec: waitpid(2) failed: " + MsgUtil::ErrnoToString());
}


void CallInc(const CallArgs &args, std::ostream &/*output*/, StringMap * const macros)
{
	const unsigned arg_count(args.size());
	if (arg_count < 1 or arg_count > 2)
		ThrowError("invalid number of arguments in $CALL(INC,...)");

	if (args.front().getArgType() != CallArg::VARIABLE)
		ThrowError("first argument of $CALL(INC,...) must be a variable");

	MSG_UTIL_ASSERT(macros->find(args.front().getAuxValue()) != macros->end());
	int number;
	if (not StringUtil::ToNumber(args.front().getValue(), &number))
		ThrowError("first argument of $CALL(INC,...) must be a variable that evaluates to a number");

	int increment(1);
	if (arg_count == 2) {
		if (args.back().getArgType() != CallArg::NUMERIC_CONSTANT)
			ThrowError("second argument of $CALL(INC,...) must be a numeric constant");
		StringUtil::ToNumber(args.back().getValue(), &increment);
	}

	number += increment;
	macros->insert(args.front().getAuxValue(), number);
}


void CallDec(const CallArgs &args, std::ostream &/*output*/, StringMap * const macros)
{
	const unsigned arg_count(args.size());
	if (arg_count < 1 or arg_count > 2)
		ThrowError("invalid number of arguments in $CALL(DEC,...)");

	if (args.front().getArgType() != CallArg::VARIABLE)
		ThrowError("first argument of $CALL(DEC,...) must be a variable");

	MSG_UTIL_ASSERT(macros->find(args.front().getAuxValue()) != macros->end());
	int number;
	if (not StringUtil::ToNumber(args.front().getValue(), &number))
		ThrowError("first argument of $CALL(DEC,...) must be a variable that evaluates to a number");

	int decrement(1);
	if (arg_count == 2) {
		if (args.back().getArgType() != CallArg::NUMERIC_CONSTANT)
			ThrowError("second argument of $CALL(DEC,...) must be a numeric constant");
		StringUtil::ToNumber(args.back().getValue(), &decrement);
	}

	number -= decrement;
	macros->insert(args.front().getAuxValue(), number);
}


void CallSubstr(const CallArgs &args, std::ostream &output)
{
	const unsigned arg_count(args.size());
	if (arg_count != 2 and arg_count != 3)
		ThrowError("invalid number of arguments in $CALL(SUBSTR,...)");

	CallArgs::const_iterator arg(args.begin());
	const std::string str(arg->getValue());
	++arg;
	const std::string start_pos(arg->getValue());
	++arg;
	std::string optional_substr_length;
	if (arg != args.end())
		optional_substr_length = arg->getValue();

	output << BuiltinSUBSTR(str, start_pos, optional_substr_length);
}


void CallYearAsString(const CallArgs &args, std::ostream &output)
{
	const unsigned arg_count(args.size());
	if (arg_count != 1)
		ThrowError("invalid number of arguments in $CALL(YEAR_AS_STRING,...)");

	output << BuiltinYEAR(args.front().getValue());
}


void CallMonthAsString(const CallArgs &args, std::ostream &output)
{
	const unsigned arg_count(args.size());
	if (arg_count != 1)
		ThrowError("invalid number of arguments in $CALL(MONTH_AS_STRING,...)");

	output << BuiltinMONTH(args.front().getValue());
}


void CallDayAsString(const CallArgs &args, std::ostream &output)
{
	const unsigned arg_count(args.size());
	if (arg_count != 1)
		ThrowError("invalid number of arguments in $CALL(DAY_AS_STRING,...)");

	output << BuiltinDAY(args.front().getValue());
}


void CallTolower(const CallArgs &args, std::ostream &output)
{
	const unsigned arg_count(args.size());
	if (arg_count != 1)
		ThrowError("invalid number of arguments in $CALL(TOLOWER,...)");

	output << StringUtil::ToLower(args.front().getValue());
}


void CallToupper(const CallArgs &args, std::ostream &output)
{
	const unsigned arg_count(args.size());
	if (arg_count != 1)
		ThrowError("invalid number of arguments in $CALL(TOUPPER,...)");

	output << StringUtil::ToUpper(args.front().getValue());
}


const bool SKIPPING     = true;    // SKIPPING means we are not evaluating the current block.
const bool NOT_SKIPPING = false;
const bool DONE         = true;    // DONE means the evaluations for all blocks at this level are complete.
const bool NOT_DONE     = false;


class SkipInfo {
	bool skipping_, done_;
public:
	SkipInfo(): skipping_(SKIPPING), done_(NOT_DONE) { }
	explicit SkipInfo(const bool initial_skipping)
		: skipping_(initial_skipping), done_(not initial_skipping) { }
	explicit SkipInfo(const bool initial_skipping, const bool initial_done)
		: skipping_(initial_skipping), done_(initial_done) { }
	bool skipping() const { return skipping_; }
	bool done() const { return done_; }
	void skipping(const bool skip);
};


void SkipInfo::skipping(const bool skip)
{
	if (done_)
		skipping_ = SKIPPING;
	else {
		skipping_ = skip;
		done_ = not skip;
	}
}


// DoSet -- helper function for "ProcessString" handling the inner part (between the
//          parentheses) of the $SET(name,value) syntax.
//
void DoSet(const char *&cp, StringMap * const macros, const bool skipping) throw(std::exception)
{
	std::string macro_name = ExtractMacroName(cp);

	SkipWhiteSpace(cp);

	if (unlikely(*cp != ',')) {
		std::string msg("comma excepted after $SET(" + macro_name + "...");
		ThrowError(msg);
	}
	++cp;

	std::string value_string;
	bool in_string_constant = false;
	unsigned open_parens_count(0);
	for (; *cp != '\0' and *cp != '\n'; ++cp) {
		if (*cp == '\\' and in_string_constant) {
			value_string += *cp++;
			if (unlikely(*cp == '\0'))
				ThrowError("prematurely terminated $SET statement found");
		}
		else if (*cp == '"') {
			if (in_string_constant)
				in_string_constant = false;
			else
				in_string_constant = true;
		}
		else if (*cp == '(' and !in_string_constant)
			++open_parens_count;
		else if (*cp == ')' and !in_string_constant) {
			if (open_parens_count > 0)
				--open_parens_count;
			else
				break;
		}

		value_string += *cp;
	}
	if (unlikely(*cp != ')'))
		ThrowError("missing ')' of $SET(name,value)");

	next_ch = value_string.c_str();
	pushed_back = false;
	Token token = GetToken(*macros, skipping);
	std::string new_value;
	if (token == TOK_FUNCTION_NAME)
		new_value = GetStringFunctionValue(function_name_token_value, next_ch, *macros, skipping);
	else if (token == TOK_STRING)
		new_value = string_token_value;
	else if (token == TOK_NUMBER)
		new_value = StringUtil::ToString(numeric_token_value);
	else
		ThrowError("string constant, macro variable, builtin function, or numeric constantas value in $SET(name,value) expected");

	token = GetToken(*macros, skipping);
	if (unlikely(token != TOK_EOF))
		ThrowError("unexpected garbage after value in $SET(name,value)");

	if (skipping)
		return;

	if (not skipping) {
		StringMap::iterator macro(macros->find(macro_name));
		if (macro == macros->end()) // brand new entry
			(*macros)[macro_name] = new_value;
		else // replace existing value
			macro->second = new_value;
	}
}


} // unnamed namespace


namespace Template {


// GetTemplatePath -- Get the path of the top-level template data directory.
//
std::string GetTemplatePath()
{
	return template_home_dir;
}


// SetTemplatePath -- Set the top-level template data directory.
//
void SetTemplatePath(const std::string &path)
{
	template_home_dir = path;
}


// GetDefaultTemplatePath -- Get the path of the top-level template data directory.
//
std::string GetDefaultTemplatePath()
{
	return default_template_home_dir;
}


// SetDefaultTemplatePath -- Set the top-level template data directory.
//
void SetDefaultTemplatePath(const std::string &path)
{
	default_template_home_dir = path;
}


// GetDefaultTheme -- Get the default theme.
//
std::string GetDefaultTheme()
{
	return default_theme;
}


// SetDefaultTheme -- Set the default theme.
//
void SetDefaultTheme(const std::string &theme)
{
	default_theme = theme;
}


// predicate for find_if in Callbacks::find
class CallbackEq {
	std::string name_;
public:
	CallbackEq(const std::string &name): name_(name) { }
	bool operator()(const Callback &callback) const
		{ return callback == name_; }
};


Callbacks::const_iterator Callbacks::find(const std::string &function_name) const
{
	return std::find_if(callbacks_.begin(), callbacks_.end(), CallbackEq(function_name));
}


// Callbacks::insert -- Insert a value into a Callbacks, replacing any old value
//
void Callbacks::insert(const std::string &name, const CallbackFunction callback)
{
	remove(name);
	callbacks_.push_back(Callback(name, callback));
}


// Callbacks::remove -- Delete a callback from a Callbacks based on it's name
//
bool Callbacks::remove(const std::string &name)
{
	std::list<Callback>::iterator iter(std::find_if(callbacks_.begin(), callbacks_.end(), CallbackEq(name)));
	if (iter == callbacks_.end())
		return false;

	callbacks_.erase(iter);

	return true;
}


// GetTemplateFileName -- Find the absolute filename of a themed template file.
//
void GetTemplateFileName(const std::string &relative_filename, std::string * const full_filename, const std::string &theme)
{
	*full_filename = GetTemplateFileName(relative_filename, (theme.empty() ? default_theme : theme));
}


// GetTemplateFileName -- Find the absolute filename of a themed template file.
//
std::string GetTemplateFileName(const std::string &relative_filename, const std::string &theme)
{
	std::string theme_dir(theme.empty() ? default_theme : theme);
	std::string full_filename(template_home_dir + "/" + theme_dir + "/" + relative_filename);

	// If the file is in the "html_templates/theme_dir" directory, return it.
	if (::access(full_filename.c_str(), R_OK) == 0)
		return full_filename;

	// If the file is in the "html_templates/user_themes/theme_dir" directory, return it.
	full_filename = template_home_dir + "/user_themes/" + theme_dir + "/" + relative_filename;
	if (::access(full_filename.c_str(), R_OK) == 0)
		return full_filename;

	full_filename = template_home_dir + "/" + theme_dir + "/" + relative_filename;

	// If this is the default theme, we do not know what to do.
	if (unlikely(theme_dir == default_theme))
		ThrowError("in Template::GetTemplateFileName: cannot read \"" +
			   full_filename + "\"");

	// Otherwise, we can use the "default" theme
	MSG_UTIL_TRACE("in Template::GetTemplateFileName: cannot read \"%s\","
		       " attempting default theme.", full_filename.c_str());
	return GetTemplateFileName(relative_filename, default_theme);
}


/** \brief  Read "filename" into "buffer".
 *  \param  filename  The name of the file to read.
 *  \param  buffer    Where to store the contents of the processed file.
 *  \note   Does $INCLUDE and line end processing.
 */
void ReadFile(const std::string &filename, std::string * const buffer, const bool remove_leading_whitespace)
{
	std::string actual_file_to_open;
	if (not FileUtil::Exists(FileUtil::CanonisePath(filename))) {
		if (filename.length() > GetDefaultTemplatePath().length()
		    and GetDefaultTemplatePath() == filename.substr(0, GetDefaultTemplatePath().length()))
			actual_file_to_open = GetTemplatePath() + "/"
				              + filename.substr(GetDefaultTemplatePath().length());
	}
	else
		actual_file_to_open = filename;
	actual_file_to_open = FileUtil::CanonisePath(actual_file_to_open);

	std::ifstream input(actual_file_to_open.c_str());
	if (input.fail())
		ThrowError("in Template::ReadFile: can't open \"" + actual_file_to_open + "\" for reading");

	*buffer = "$INCLUDED(" + actual_file_to_open + ")";
	unsigned line_count(0);
	while (not input.eof()) {
		std::string line;
		std::getline(input, line);
		if (remove_leading_whitespace)
			StringUtil::LeftTrim(" \t", &line);
		++line_count;
		if (line.empty()) {
		        *buffer += '\n';
			continue;
		}

		const std::size_t INCLUDE_LEN = std::strlen("$INCLUDE(");
		std::string::size_type include_start;
		while ((include_start = line.find("$INCLUDE(")) != std::string::npos) {
			*buffer += line.substr(0, include_start);

			std::string::size_type pos(include_start + INCLUDE_LEN);
			std::string::size_type closing_paren_pos = line.find(')', pos);
			if (unlikely(closing_paren_pos == std::string::npos))
				ThrowError("in Template::ReadFile: missing ')' after $INCLUDE filename in file \""
					   + actual_file_to_open + "\", line " + StringUtil::ToString(line_count));

			// extract the include filename:
			std::string include_filename(line.substr(pos, closing_paren_pos - pos));
			if (unlikely(include_filename.empty()))
				ThrowError("in Template::ReadFile: missing filename in an $INCLUDE in file \""
					   + actual_file_to_open + "\", line " + StringUtil::ToString(line_count));

			// If included file path is not absolute.
			if (include_filename[0] != '/') {
				std::string dirname, basename;
				FileUtil::DirnameAndBasename(filename, &dirname, &basename);
				if (not dirname.empty())
					include_filename = dirname + std::string("/") + include_filename;
			}

			std::string tmp_buffer;
			ReadFile(include_filename, &tmp_buffer, remove_leading_whitespace);
			*buffer += tmp_buffer;

			line = line.substr(closing_paren_pos + 1);
		}

		if (unlikely(line.empty()))
			*buffer += '\n';
		else {
			*buffer += line;
			*buffer += '\n';
		}
	}
}


namespace {


class ForeachInfo {
	enum Type { ITERATE_OVER_KEYS, USE_A_PATTERN } type_;
	const std::string iterator_variable_name_;
	const std::list<std::string> keys_;
	std::list<std::string>::const_iterator current_key_;
	const unsigned max_iteration_count_;
	unsigned current_iteration_count_;
	PerlCompatRegExp pcre_;
	const unsigned start_line_number_;
	const char *start_string_pos_;
	StringMap::const_iterator macros_iter_;
	const StringMap::const_iterator macros_end_;
public:
	ForeachInfo()
		: max_iteration_count_(0), current_iteration_count_(0), start_line_number_(0), start_string_pos_(NULL) { }
	ForeachInfo(const ForeachInfo &);
	ForeachInfo(const std::string &iterator_variable_name, const std::list<std::string> &keys,
		    const unsigned max_iteration_count, const unsigned start_line_number,
		    const char *start_string_pos, StringMap * const macros);
	ForeachInfo(const std::string &iterator_variable_name, const std::string &pattern,
		    const unsigned start_line_number, const char *start_string_pos, StringMap * const macros);
	const ForeachInfo operator=(const ForeachInfo &rhs);
	bool repeat(const char * * const cp, StringMap * const macros, unsigned * const line_number);
	bool done() const;
};


ForeachInfo::ForeachInfo(const ForeachInfo &rhs)
	: type_(rhs.type_), iterator_variable_name_(rhs.iterator_variable_name_), keys_(rhs.keys_),
	  max_iteration_count_(rhs.max_iteration_count_), current_iteration_count_(rhs.current_iteration_count_),
	  pcre_(rhs.pcre_.getPattern()), start_line_number_(rhs.start_line_number_),
	  start_string_pos_(rhs.start_string_pos_), macros_iter_(rhs.macros_iter_), macros_end_(rhs.macros_end_)
{
	if (type_ == ITERATE_OVER_KEYS) {
		current_key_ = keys_.begin();
		for (unsigned count(0); count < rhs.current_iteration_count_ and current_key_ != keys_.end(); ++count)
			++current_key_;
	}
}


ForeachInfo::ForeachInfo(const std::string &iterator_variable_name, const std::list<std::string> &keys,
			 const unsigned max_iteration_count, const unsigned start_line_number,
			 const char *start_string_pos, StringMap * const macros)
	: type_(ITERATE_OVER_KEYS), iterator_variable_name_(iterator_variable_name), keys_(keys),
	  max_iteration_count_(max_iteration_count), current_iteration_count_(0), start_line_number_(start_line_number),
	  start_string_pos_(start_string_pos)
{
	if (macros->find(iterator_variable_name) != macros->end())
		ThrowError("iterator variable name \"" + iterator_variable_name + "\"in $FOREACH already in use (1)");
	current_key_ = keys_.begin();
	if (current_key_ != keys_.end())
		macros->insert(iterator_variable_name_, *current_key_);
}


ForeachInfo::ForeachInfo(const std::string &iterator_variable_name, const std::string &pattern,
			 const unsigned start_line_number, const char *start_string_pos, StringMap * const macros)
	: type_(USE_A_PATTERN), iterator_variable_name_(iterator_variable_name), max_iteration_count_(0), pcre_(pattern),
	  start_line_number_(start_line_number), start_string_pos_(start_string_pos), macros_end_(macros->end())
{
	if (macros->find(iterator_variable_name) != macros->end())
		ThrowError("iterator variable name \"" + iterator_variable_name + "\"in $FOREACH already in use (2)");

	for (macros_iter_ = macros->begin(); macros_iter_ != macros->end(); ++macros_iter_) {
		if (macros_iter_->first != iterator_variable_name_ and pcre_.match(macros_iter_->first)) {
			macros->insert(iterator_variable_name_, macros_iter_->second);
			break;
		}
	}
}


const ForeachInfo ForeachInfo::operator=(const ForeachInfo &rhs)
{
	// Prevent self-assignment:
	if (this != &rhs) {
		type_ = rhs.type_;
		*const_cast<std::string *>(&iterator_variable_name_) = rhs.iterator_variable_name_;
		*const_cast<std::list<std::string> *>(&keys_) = rhs.keys_;
		current_key_ = keys_.begin();
		for (unsigned count(0); count < rhs.current_iteration_count_ and current_key_ != keys_.end(); ++count)
			++current_key_;
		*const_cast<unsigned *>(&max_iteration_count_) = rhs.max_iteration_count_;
		current_iteration_count_ = rhs.current_iteration_count_;
		pcre_                    = rhs.pcre_;
		*const_cast<unsigned *>(&start_line_number_) = rhs.start_line_number_;
		*const_cast<char **>(&start_string_pos_) = const_cast<char *>(rhs.start_string_pos_);
		macros_iter_ = rhs.macros_iter_;
		*const_cast<StringMap::const_iterator *>(&macros_end_) = rhs.macros_end_;
	}

	return *this;
}


// ForeachInfo::repeat -- Must be called when the $ENDFOREACH has been encountered!
//
bool ForeachInfo::repeat(const char * * const cp, StringMap * const macros, unsigned * const line_number)
{
	if (done())
		return false;
	macros->remove(iterator_variable_name_);

	if (type_ == ITERATE_OVER_KEYS) {
		if (++current_iteration_count_ >= max_iteration_count_)
			return false;

		++current_key_;
		if (current_key_ == keys_.end())
			return false;

		macros->insert(iterator_variable_name_, *current_key_);
	}
	else { // Assume type_ == USE_A_PATTERN.
		bool found_a_match(false);
		for (++macros_iter_; macros_iter_ != macros->end(); ++macros_iter_) {
			if (macros_iter_->first != iterator_variable_name_ and pcre_.match(macros_iter_->first)) {
				macros->insert(iterator_variable_name_, macros_iter_->second);
				found_a_match = true;
				break;
			}
		}

		if (not found_a_match)
			return false;
	}

	*cp = start_string_pos_;
	*line_number = start_line_number_;

	return true;
}


bool ForeachInfo::done() const
{
	if (type_ == ITERATE_OVER_KEYS)
		return current_key_ == keys_.end();
	else // Assume type_ == USE_A_PATTERN
		return macros_iter_ == macros_end_;
}


class ForeachInfoList: public SList<ForeachInfo> {
public:
	ForeachInfoList() { }
	bool skipping() const;
private:
	ForeachInfoList(const ForeachInfoList &); // Intentionally unimplemented!
	const ForeachInfoList &operator==(const ForeachInfoList &rhs); // Intentionally unimplemented!
};


bool ForeachInfoList::skipping() const
{
	for (const_iterator foreach_info(begin()); foreach_info != end(); ++foreach_info)
		if (foreach_info->done())
			return true;

	return false;
}


void ProcessStringHelper(const std::string &template_string, const StringMap &user_macros, const Callbacks &callbacks, std::ostream &output,
			 const std::string &template_filename, const bool remove_leading_whitespace, StringMap* result_macros)
{
	StringMap macros(user_macros);
	macros.insert("TEMPLATE_NAME", template_filename);

	std::stack<SkipInfo> skip_info;
	skip_info.push(SkipInfo(NOT_SKIPPING)); // initially we're not skipping at the top level

	ForeachInfoList foreach_info_list;

	const char *cp = template_string.c_str();

	while (*cp != '\0') {
		// Deal with \ representing "squelch following newline":
		if (*cp == '\\' and (*(cp + 1) == '\r' or *(cp + 1) == '\n')) {
			++cp;
			// Skip over line ending type characters. Don't remove more than ONE empty line:
			if (*cp == '\r')
				++cp;
			if (*cp == '\n')
				++cp;
			continue;
		}

		// Remove carriage returns:
		if (*cp == '\r') {
			++cp;
			continue;
		}

		if (*cp != '$'){
			if (not skip_info.top().skipping())
				output << *cp;
			++cp;
			continue;
		}

		std::string macro_name = ExtractMacroName(cp);

		if (macro_name == "IF") {
			std::string condition = ExtractCondition(cp);

			if (foreach_info_list.empty() or not foreach_info_list.skipping()) {
				if (not skip_info.top().skipping())
					skip_info.push(SkipInfo(not EvalCondition(condition, macros,
										  false)));
				else
					skip_info.push(SkipInfo(SKIPPING, DONE));
			}
		}
		else if (macro_name == "IFVALUE") {
			if (*cp != '(')
				ThrowError("'(' expected after $IFVALUE");
			++cp;
			std::string macro_name1 = ExtractMacroName(cp);

			if (foreach_info_list.empty() or not foreach_info_list.skipping()) {
				if (not skip_info.top().skipping()) {
					StringMap::const_iterator macro(macros.find(macro_name1));
					if (macro == macros.end())
						skip_info.push(SkipInfo(true));
					else if (macro->second.empty())
						skip_info.push(SkipInfo(true));
					else
						skip_info.push(SkipInfo(false));
				}
				else
					skip_info.push(SkipInfo(SKIPPING, DONE));
			}

			if (unlikely(*cp != ')'))
				ThrowError("')' expected to terminate $IFVALUE");
			++cp;
		}
		else if (macro_name == "IFDEF") {
			if (*cp != '(')
				ThrowError("'(' expected after $IFDEF");
			++cp;
			std::string macro_name1 = ExtractMacroName(cp);

			if (foreach_info_list.empty() or not foreach_info_list.skipping()) {
				if (not skip_info.top().skipping()) {
					StringMap::const_iterator macro(macros.find(macro_name1));
					skip_info.push(SkipInfo((macro == macros.end())));
				}
				else
					skip_info.push(SkipInfo(SKIPPING, DONE));
			}

			if (unlikely(*cp != ')'))
				ThrowError("')' expected to terminate $IFDEF");
			++cp;
		}
		else if (macro_name == "IFNDEF") {
			if (unlikely(*cp != '('))
				ThrowError("'(' expected after $IFNEF");
			++cp;
			std::string macro_name1 = ExtractMacroName(cp);

			if (foreach_info_list.empty() or not foreach_info_list.skipping()) {
				if (not skip_info.top().skipping()) {
					StringMap::const_iterator macro(macros.find(macro_name1));
					skip_info.push(SkipInfo((macro != macros.end())));
				}
				else
					skip_info.push(SkipInfo(SKIPPING, DONE));
			}

			if (unlikely(*cp != ')'))
				ThrowError("')' expected to terminate $IFNDEF");
			++cp;
		}
		else if (macro_name == "ELSIF") {
			std::string condition = ExtractCondition(cp);

			if (foreach_info_list.empty() or not foreach_info_list.skipping()) {
				if (skip_info.top().done())
					skip_info.top().skipping(true);
				else
					skip_info.top().skipping(not EvalCondition(condition, macros, false));
			}
		}
		else if (macro_name == "ELSE")
			skip_info.top().skipping(skip_info.top().done());
		else if (macro_name == "ENDIF") {
			if (foreach_info_list.empty() or not foreach_info_list.skipping()) {
				if (unlikely(skip_info.size() < 2))
					ThrowError("in Template::ProcessStringHelper: extraneous $ENDIF found");
				skip_info.pop();
			}
		}
		else if (macro_name == "CALL") {
			if (unlikely(*cp != '('))
				ThrowError("'(' expected after $CALL");
			++cp;

			CallArgs args;
			const std::string macro_name1(ExtractCallArgs(cp, macros, skip_info.top().skipping(), &args));
			if (not skip_info.top().skipping()) {
				if (foreach_info_list.empty() or not foreach_info_list.skipping()) {
					if (macro_name1 == "PRINT_MACROS")
						CallPrintMacros(macros, callbacks, output);
					else if (macro_name1 == "EXEC") {
						args.pop_front(); // Remove the function name.
						CallExec(args, output);
					}
					else if (macro_name1 == "INC") {
						args.pop_front(); // Remove the function name.
						CallInc(args, output, &macros);
					}
					else if (macro_name1 == "DEC") {
						args.pop_front(); // Remove the function name.
						CallDec(args, output, &macros);
					}
					else if (macro_name1 == "SUBSTR") {
						args.pop_front(); // Remove the function name.
						CallSubstr(args, output);
					}
					else if (macro_name1 == "TOLOWER") {
						args.pop_front(); // Remove the function name.
						CallTolower(args, output);
					}
					else if (macro_name1 == "TOUPPER") {
						args.pop_front(); // Remove the function name.
						CallToupper(args, output);
					}
					else if (macro_name1 == "YEAR_AS_STRING") {
						args.pop_front(); // Remove the function name.
						CallYearAsString(args, output);
					}
					else if (macro_name1 == "MONTH_AS_STRING") {
						args.pop_front(); // Remove the function name.
						CallMonthAsString(args, output);
					}
					else if (macro_name1 == "DAY_AS_STRING") {
						args.pop_front(); // Remove the function name.
						CallDayAsString(args, output);
					}
					else {
						Callbacks::const_iterator callback(callbacks.find(macro_name1));
						if (unlikely(callback == callbacks.end())) {
							std::string msg("unknown CALL function name \"");
							msg += macro_name1;
							msg += " (valid names are: PRINT_MACROS, SUBSTR";
							Callbacks::const_iterator callback1(callbacks.begin());
							while (callback1 != callbacks.end()) {
								msg += ", ";
								msg += callback1->getName();
								++callback1;
							}
							msg += ")\"";
							ThrowError(msg);
						}

						// initialize argv array from string list:
						const char *argv[args.size()+1];
						int argc = 0;
						for (CallArgs::const_iterator arg(args.begin()); arg != args.end(); ++arg)
							argv[argc++] = arg->getValue().c_str();
						argv[argc] = NULL;

						(*callback)(output, argc, argv, macros, template_filename, 0, remove_leading_whitespace);
					}
				}
			}

			if (unlikely(*cp != ')'))
				ThrowError("')' expected to terminate $CALL");
			++cp;
		}
		else if (macro_name == "SET") {
			if (unlikely(*cp != '('))
				ThrowError("'(' expected after $SET");
			++cp;
			DoSet(cp, &macros, skip_info.top().skipping()
			      or (not foreach_info_list.empty() and foreach_info_list.skipping()));
			if (unlikely(*cp != ')'))
				ThrowError("')' expected to terminate $SET");
			++cp;
		}
		else if (macro_name == "UNSET") {
			if (unlikely(*cp != '('))
				ThrowError("'(' expected after $UNSET");
			++cp;
			std::string macro_name1 = ExtractMacroName(cp);

			if (not (skip_info.top().skipping() or (not foreach_info_list.empty() and foreach_info_list.skipping()))) {
				StringMap::iterator macro(macros.find(macro_name1));
				if (macro == macros.end()) // not found!
					ThrowError("in Template::ProcessStringHelper: trying to UNSET "
						   "undefined macro $" + macro_name1);
				else
					macros.erase(macro);
			}

			if (unlikely(*cp != ')'))
				ThrowError("missing ')' of $UNSET(name)");
			++cp;
		}
		else if (macro_name == "FOREACH") {
			if (*cp != '(')
				ThrowError("'(' expected after $FOREACH");
			++cp;

			std::string iterator_variable_name;
			std::list<std::string> keys;
			unsigned max_iteration_count(UINT_MAX);
			std::string pattern;
			ExtractForeachArgs(cp, macros, skip_info.top().skipping(), &iterator_variable_name, &keys,
					   &max_iteration_count, &pattern);

			if (unlikely(*cp != ')'))
				ThrowError("')' expected to terminate $FOREACH");
			++cp;

			if (not skip_info.top().skipping()) {
				if (not pattern.empty()) // $FOREACH using a pattern.
					foreach_info_list.push_front(ForeachInfo(iterator_variable_name, pattern,
										 template_line_no, cp, &macros));
				else // $FOREACH using a set/list.
					foreach_info_list.push_front(ForeachInfo(iterator_variable_name, keys,
										 max_iteration_count, template_line_no,
										 cp, &macros));
			}
		}
		else if (macro_name == "ENDFOREACH") {
			if (not skip_info.top().skipping()) {
				if (foreach_info_list.empty())
					ThrowError("$ENDFOREACH without corresponding prior $FOREACH");
				if (not foreach_info_list.front().repeat(&cp, &macros, &template_line_no))
					foreach_info_list.pop_front();
			}
		}
		else if (macro_name == "INCLUDED") {
			if (*cp != '(')
				ThrowError("'(' expected after $INCLUDED");
			++cp;
			current_filename.clear();
			while (*cp != '\n' and *cp != '\0' and *cp != ')')
				current_filename += *cp++;
			if (unlikely(*cp == '\n' or *cp == '\0'))
				ThrowError("')' expected to terminate $INCLUDED");
			++cp;
		}
		else if (macro_name == "DOLLAR") {
			if (not skip_info.top().skipping())
				output << '$';
		}
		else if (macro_name == "BACKSLASH") {
			if (not skip_info.top().skipping())
				output << '$';
		}
		else if (not (skip_info.top().skipping()
			      or (not foreach_info_list.empty() and foreach_info_list.skipping())))
		{ // A normal macro?
			StringMap::const_iterator macro(macros.find(macro_name));
			if (unlikely(macro == macros.end())) {// not found!
				std::string msg("in Template::ProcessStringHelper: undefined macro $"
						+ macro_name);
				ThrowError(msg);
			}
			else // found => substitute
				output << macro->second.c_str();
		}
	}

	//if (not (skip_info.top().skipping() or (not foreach_info_list.empty() and foreach_info_list.skipping())))
	//output << cp;

	output.flush();

	if (result_macros != NULL)
		*result_macros =  macros;
}


} // unnamed namespace


void ProcessString(const std::string &template_string, const StringMap &user_macros, const Callbacks &callbacks,
		   std::ostream &output, const std::string &template_filename, const bool remove_leading_whitespace,
		   StringMap *result_macros)
{
	::template_line_no = 1;
	try {
		ProcessStringHelper(template_string, user_macros, callbacks, output, template_filename,
				    remove_leading_whitespace, result_macros);
	}
	catch (const std::exception &x) {
		std::string msg("Error at or after line #");
		msg += StringUtil::ToString(::template_line_no) + ":\n\t";
		msg += x.what();
		throw Exception(msg);
	}
}


// ProcessFile -- Substitutes macros in the given file
//
void ProcessFile(const std::string &filename, const StringMap &macros,
		 const Callbacks &callbacks, std::ostream &output, const bool remove_leading_whitespace,
		 StringMap *result_macros)
{
	if (unlikely(filename.empty()))
		throw Exception("in Template::ProcessFile: can't process empty file name!");

	std::string absolute_filename;

	// Absolute path provided?
	if (filename[0] == '/')
		absolute_filename = filename;
	else if (not GetDefaultTemplatePath().empty() and FileUtil::Exists(GetDefaultTemplatePath() + "/" + filename))
		absolute_filename = GetDefaultTemplatePath() + "/" + filename;
	else
		absolute_filename = GetTemplatePath() + "/" + filename;

	// Make sure the file requested is not below the template path in the file tree
	absolute_filename = FileUtil::CanonisePath(absolute_filename);
	if (absolute_filename.find(GetDefaultTemplatePath()) == std::string::npos and absolute_filename.find(GetTemplatePath()) == std::string::npos)
		throw Exception("in Template::ProcessFile: requested file is below template root directory!");

	std::string template_string;
	ReadFile(absolute_filename, &template_string, remove_leading_whitespace);
	ProcessString(template_string, macros, callbacks, output, absolute_filename, remove_leading_whitespace, result_macros);
}


void HighlightMacros(const std::list<std::string> &highlight_words, const std::string &highlight_start,
		     const std::string &highlight_stop, const std::list<std::string> &highlight_macros,
		     StringMap * const macros, const bool skip_html, const bool stem,
		     const std::string highlight_suffix)
{
	for (StringMap::iterator macro(macros->begin()); macro != macros->end(); ++macro) {
		if (std::find(highlight_macros.begin(), highlight_macros.end(),
			      macro->first) == highlight_macros.end())
			continue;

		if (highlight_suffix.empty())
			TextUtil::HighlightStrings(highlight_words, highlight_start, highlight_stop, &macro->second,
						   skip_html, stem);
		else {
			std::string new_macro_name = macro->first + highlight_suffix;
			std::string value(macro->second);
			TextUtil::HighlightStrings(highlight_words, highlight_start, highlight_stop, &value, skip_html,
						   stem);
			macros->insert(new_macro_name, value);
		}
	}
}


namespace {


// ReplacePercentOneU -- helper function for Paginate().  Replaces all occurrences of "%1u" in "template_string" with the
//                       value of "page_no".
//
std::string ReplacePercentOneU(const std::string &template_string, const unsigned page_no)
{
	std::string expanded_text(template_string);
	return StringUtil::ReplaceString("%1u", StringUtil::ToString(page_no), &expanded_text);
}


} // unnamed namespace


void Paginate(const unsigned current_page_no, const unsigned total_no_of_pages, const std::string &current_page_template,
	      const std::string &page_link_template, StringMap * const macros, const unsigned page_delta)
{
	std::string page_list;
        unsigned page_no(1);
        while (page_no <= total_no_of_pages) {
                if (page_no == current_page_no)
                        page_list += ReplacePercentOneU(current_page_template, page_no);
                else
                        page_list += ReplacePercentOneU(page_link_template, page_no);

                // Determine the next page number:
                if (page_no == 1) {
                        // If the current page is 1, the next is usually 2, but we may want to skip some values:
                        page_no = std::max(static_cast<int>(current_page_no) - static_cast<int>(page_delta), 2);

                        if (total_no_of_pages == 1)
                                /* intentionally empty! */;
                        else if (page_no == 2)
                                page_list += ",&nbsp;";
                        else
                                page_list += ",&nbsp;...,&nbsp;";
                }
                else if (page_no == total_no_of_pages)
                        // If this is the last page of results, make sure we don't loop again
                        ++page_no;
                else if (page_no + page_delta >= current_page_no and page_no < current_page_no + page_delta)
			// Fill in the "page_delta" pages around the current page number
                {
                        page_list += ",&nbsp;";
                        ++page_no;
                }
                else if (page_no == current_page_no + page_delta) {
                        // If this page is "page_delta" past current page, then skip to last page:
                        if (page_no == total_no_of_pages - 1)
                                page_list += ",&nbsp;";
                        else
                                page_list += ",&nbsp;...,&nbsp;";
                        page_no = total_no_of_pages;
                }
                else
                        throw Exception("in Template::Paginate: page_no = " + StringUtil::ToString(page_no)
						 + ", current_page_no = " + StringUtil::ToString(current_page_no));
        }

        macros->insert("PAGE_LIST", page_list);
        macros->insert("CURRENT_PAGE_NO", current_page_no);
}


} // namespace Template
