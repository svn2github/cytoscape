#include "JSONScanner.h"
#include <iostream>
#include <stdexcept>
#include <cctype>
#include <cstdlib>


void JSONScanner::ungetToken() {
	if (pushed_back_)
		throw std::runtime_error("can't unget two tokens in a row!");

	pushed_back_ = true;
}


// Assumes that "ch" points to the first character of the string.
static bool ParseString(std::string::const_iterator &ch, const std::string::const_iterator &end, std::string * const value) {
	value->clear();

	for (;;) {
		if (ch == end)
			return false;

		if (*ch == '"') {
			++ch;
			return true;
		}
			
		if (*ch == '\\') { // escaped character (we're ignoring unicode values for now
			++ch;
			if (*ch == 'u') {
				std::cerr << "*** unicode char support not implemented!\n";
				return false;
			}
			*value += *ch;
			++ch;
		} else {
			*value += *ch;
			++ch;
		}
	}
}


static bool ParseInteger(std::string::const_iterator &ch, const std::string::const_iterator &end, int64_t * const value) {
	std::string s;
	if (*ch == '-') {
		s += '-';
		++ch;
	}

	while (ch != end and isdigit(*ch)) {
		s += *ch;
		++ch;
	}

	char *end_ptr;
	*value = ::strtoll(s.c_str(), &end_ptr, 10);

	return end_ptr != s.c_str();
}


static bool Expect(std::string::const_iterator &ch, const std::string::const_iterator &end, const char * const expected_string) {
	const char *next_expected_char = expected_string;
	while (*next_expected_char != '\0') {
		if (ch == end)
			return false;
		if (*ch != *next_expected_char)
			return false;
		++ch, ++next_expected_char;
	}

	return true;
}


static bool ParseBoolean(std::string::const_iterator &ch, const std::string::const_iterator &end, bool * const value) {
	if (*ch != 't' && *ch != 'f')
		return false;

	if (*ch == 't') {
		if (!Expect(ch, end, "true"))
			return false;
		*value = true;
	} else {
		if (!Expect(ch, end, "false"))
			return false;
		*value = false;
	}

	return true;
}


JSONScanner::TokenType JSONScanner::getToken() {
	if (pushed_back_) {
		pushed_back_ = false;
		return last_token_;
	}

	skipWhitespace();

	if (ch == end) {
		last_token_ = END_OF_INPUT;
		return END_OF_INPUT;
	}

	if (*ch == '{') {
		++ch;
		last_token_ = OPEN_BRACE;
		return OPEN_BRACE;
	}

	if (*ch == '}') {
		++ch;
		last_token_ = CLOSE_BRACE;
		return CLOSE_BRACE;
	}

	if (*ch == '[') {
		++ch;
		last_token_ = OPEN_BRACKET;
		return OPEN_BRACKET;
	}

	if (*ch == ']') {
		++ch;
		last_token_ = CLOSE_BRACKET;
		return CLOSE_BRACKET;
	}

	if (*ch == ':') {
		++ch;
		last_token_ = COLON;
		return COLON;
	}

	if (*ch == ',') {
		++ch;
		last_token_ = COMMA;
		return COMMA;
	}

	if (*ch == '"') {
		++ch;
		if (!ParseString(ch, end, &last_string_)) {
			last_error_message_ = "error parsing a string constant!";
			last_token_ = ERROR;
			return ERROR;
		}
		last_token_ = STRING_CONSTANT;
		return STRING_CONSTANT;
	}

	if (isdigit(*ch) or *ch == '-') {
		if (!ParseInteger(ch, end, &last_int_)) {
			last_error_message_ = "error parsing an integer constant!";
			last_token_ = ERROR;
			return ERROR;
		}
		last_token_ = INTEGER_CONSTANT;
		return INTEGER_CONSTANT;
	}

	if (*ch == 't' or *ch == 'f') {
		if (!ParseBoolean(ch, end, &last_bool_)) {
			last_error_message_ = "error parsing a boolean constant!";
			last_token_ = ERROR;
			return ERROR;
		}
		last_token_ = BOOLEAN_CONSTANT;
		return BOOLEAN_CONSTANT;
	}

	last_error_message_ = "unexpected input character '" + std::string(1, *ch) + "'!";
	last_token_ = ERROR;
	return ERROR;
}


void JSONScanner::skipWhitespace() {
	for (;;) {
		if (ch == end or !isspace(*ch))
			return;

		++ch;
	}
}
