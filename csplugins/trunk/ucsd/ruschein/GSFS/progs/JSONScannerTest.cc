#include <iostream>
#include <cstdlib>
#include <FileUtil.h>
#include "JSONScanner.h"


void Usage() {
	std::cerr << "usage: JSONScannerTest JSON_document\n";
	std::exit(EXIT_FAILURE);
}


int main(int argc, char *argv[]) {
	if (argc != 2)
		Usage();


	std::string json_directory_listing;
	if (!FileUtil::ReadFile(argv[1], &json_directory_listing)) {
		std::cerr << "*** Failed to read a directory listing from \"" << argv[1] << "\"!";
		std::exit(EXIT_FAILURE);
	}

	JSONScanner scanner(json_directory_listing);
	for (;;) {
		const JSONScanner::TokenType token = scanner.getToken();
		switch (token) {
		case JSONScanner::END_OF_INPUT:
			return EXIT_SUCCESS;
		case JSONScanner::ERROR:
			std::cerr << "*** parse error (" << scanner.getLastErrorMsg() << ")\n";
			return EXIT_FAILURE;
		case JSONScanner::OPEN_BRACE:
			std::cout << "{\n";
			break;
		case JSONScanner::CLOSE_BRACE:
			std::cout << "}\n";
			break;
		case JSONScanner::OPEN_BRACKET:
			std::cout << "[\n";
			break;
		case JSONScanner::CLOSE_BRACKET:
			std::cout << "]\n";
			break;
		case JSONScanner::COMMA:
			std::cout << ",\n";
			break;
		case JSONScanner::COLON:
			std::cout << ":\n";
			break;
		case JSONScanner::STRING_CONSTANT:
			std::cout << "string(" << scanner.getLastString() << ")\n";
			break;
		case JSONScanner::INTEGER_CONSTANT:
			std::cout << "integer(" << scanner.getLastInteger() << ")\n";
			break;
		case JSONScanner::BOOLEAN_CONSTANT:
			std::cout << "bool(" << scanner.getLastBoolean() << ")\n";
			break;
		}
	}
}

