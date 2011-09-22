#include <iostream>
#include <vector>
#include <cstdlib>
#include <FileUtil.h>
#include "JSONScanner.h"


void Usage() {
	std::cerr << "usage: ParseDirectory GenomeSpace_JSON_directory_listing\n";
	std::exit(EXIT_FAILURE);
}


class DirEntry {
};


static bool SkipBraceBlock(JSONScanner * const scanner) {
	JSONScanner::TokenType token = scanner->getToken();
	if (token != JSONScanner::OPEN_BRACE)
		return false;

	unsigned open_brace_count(1);
	while (open_brace_count != 0) {
		token = scanner->getToken();
		if (token == JSONScanner::END_OF_INPUT)
			return false;
		else if (token == JSONScanner::OPEN_BRACE)
			++open_brace_count;
		else if (token == JSONScanner::CLOSE_BRACE)
			--open_brace_count;
	}

	return true;
}


static bool SkipBracketBlock(JSONScanner * const scanner) {
	JSONScanner::TokenType token = scanner->getToken();
	if (token != JSONScanner::OPEN_BRACKET)
		return false;

	unsigned open_bracket_count(1);
	while (open_bracket_count != 0) {
		token = scanner->getToken();
		if (token == JSONScanner::END_OF_INPUT)
			return false;
		else if (token == JSONScanner::OPEN_BRACKET)
			++open_bracket_count;
		else if (token == JSONScanner::CLOSE_BRACKET)
			--open_bracket_count;
	}

	return true;
}


bool ParseListing(const std::string &listing, std::vector<DirEntry> * const entries) {
	entries->clear();

	JSONScanner scanner(listing);

	// top-level opening brace
	if (scanner.getToken() != JSONScanner::OPEN_BRACE)
		return false;

	if (scanner.getToken() != JSONScanner::STRING_CONSTANT)
		return false;
	if (scanner.getLastString() != "directory")
		return false;

	if (!SkipBraceBlock(&scanner))
		return false;

	if (scanner.getToken() != JSONScanner::STRING_CONSTANT)
		return false;
	if (scanner.getLastString() != "contents")
		return false;

	if (!SkipBracketBlock(&scanner))
		return false;

	if (scanner.getToken() != JSONScanner::COMMA)
                return false;

	// top-level closing brace
	if (scanner.getToken() != JSONScanner::CLOSE_BRACE)
		return false;

	return true;
}


int main(int argc, char *argv[]) {
	if (argc != 2)
		Usage();

	std::string json_directory_listing;
	if (!FileUtil::ReadFile(argv[1], &json_directory_listing)) {
		std::cerr << "*** Failed to read a directory listing from \"" << argv[1] << "\"!";
		std::exit(EXIT_FAILURE);
	}

	std::vector<DirEntry> dir_entries;
	std::cout << "ParseListing() returned " << ParseListing(json_directory_listing, &dir_entries) << '\n';
}
