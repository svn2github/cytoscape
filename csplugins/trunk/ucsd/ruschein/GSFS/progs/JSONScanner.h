#ifndef JSON_SCANNER_H
#define JSON_SCANNER_H


#include <string>
#include <inttypes.h>


class JSONScanner {
	std::string last_string_;
	int64_t last_int_;
	bool last_bool_;
	const std::string::const_iterator end;
	std::string::const_iterator ch;
	std::string last_error_message_;
	bool pushed_back_;
public:
	enum TokenType { ERROR, OPEN_BRACE, CLOSE_BRACE, OPEN_BRACKET, CLOSE_BRACKET, COMMA, COLON, STRING_CONSTANT, INTEGER_CONSTANT, BOOLEAN_CONSTANT, END_OF_INPUT };
private:
	TokenType last_token_;
public:

 JSONScanner(const std::string &document): end(document.end()), ch(document.begin()), pushed_back_(false) { } 
	TokenType getToken();
	void ungetToken(); // Can only be called once after a call to getToken().
	inline std::string getLastString() { return last_string_; }
	inline int64_t getLastInteger() { return last_int_; }
	inline bool getLastBoolean() { return last_bool_; }
	inline std::string getLastErrorMsg() { return last_error_message_; }
private:
	void skipWhitespace();
};


#endif // ifndef JSON_SCANNER_H
