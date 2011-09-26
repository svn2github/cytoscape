#include <fstream>
#include <iostream>
#include <vector>
#include <cstdlib>
#include <FileUtil.h>
#include <StringUtil.h>
#include <TimeUtil.h>
#include "JSONScanner.h"


void Usage() {
	std::cerr << "usage: ParseDirectory GenomeSpace_JSON_directory_listing\n";
	std::exit(EXIT_FAILURE);
}


class DirEntry {
	std::string name_;
	std::string url_;
	std::string owner_;
	bool is_directory_;
	bool owner_has_read_premission_;
	bool owner_has_write_permission_;
	time_t last_modified_;
	uint64_t size_;
public:
	DirEntry(const std::string &name, const std::string &url, const std::string &owner, const bool is_directory,
		 const bool owner_has_read_premission, const bool owner_has_write_permission, const time_t last_modified,
		 const uint64_t size)
		: name_(name), url_(url), owner_(owner), is_directory_(is_directory),
		  owner_has_read_premission_(owner_has_read_premission), owner_has_write_permission_(owner_has_write_permission),
		  last_modified_(last_modified), size_(size) { }
	std::string toString() const;
};


std::string DirEntry::toString() const {
	return name_ + "(" + url_ + "), " + std::string(is_directory_ ? "directory, " : "ordinary file, ")
	       + "owner=" + owner_ + ", " + std::string(owner_has_read_premission_ ? "r" : "-")
	       + std::string(owner_has_write_permission_ ? "w" : "-") + ", size=" + StringUtil::ToString(size_);
}


static bool SkipBlock(JSONScanner * const scanner, const JSONScanner::TokenType open_token,
                      const JSONScanner::TokenType close_token)
{
	JSONScanner::TokenType token = scanner->getToken();
	if (token != open_token)
		return false;

	unsigned open_token_count(1);
	while (open_token_count != 0) {
		token = scanner->getToken();
		if (token == JSONScanner::END_OF_INPUT)
			return false;
		else if (token == open_token)
			++open_token_count;
		else if (token == close_token)
			--open_token_count;
	}

	return true;
}


static inline bool SkipBraceBlock(JSONScanner * const scanner) {
	return SkipBlock(scanner, JSONScanner::OPEN_BRACE, JSONScanner::CLOSE_BRACE);
}


static inline bool SkipBracketBlock(JSONScanner * const scanner) {
	return SkipBlock(scanner, JSONScanner::OPEN_BRACKET, JSONScanner::CLOSE_BRACKET);
}


static bool SkipUntil(JSONScanner * const scanner, const JSONScanner::TokenType look_for) {
	for (;;) {
		const JSONScanner::TokenType token = scanner->getToken();
		if (token == look_for)
			return true;
		else if (token == JSONScanner::END_OF_INPUT)
			return false;
	}
}


bool ParseEffectiveACL(const std::string &owner, JSONScanner * const scanner,
                       bool * const owner_has_read_premission, bool * const owner_has_write_premission)
{
	if (not SkipUntil(scanner, JSONScanner::OPEN_BRACKET))
		return false;

	*owner_has_read_premission  = false;
	*owner_has_write_premission = false;

	JSONScanner::TokenType token;
        token = scanner->getToken();

	for (;;) {
		if (token == JSONScanner::CLOSE_BRACKET)
			break;
		else if (token != JSONScanner::OPEN_BRACE)
			return false;

		// parse "sid":
		token = scanner->getToken();
		if (token != JSONScanner::STRING_CONSTANT)
			return false;
		if (scanner->getLastString() != "sid")
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::COLON)
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::OPEN_BRACE)
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::STRING_CONSTANT)
			return false;
		if (scanner->getLastString() != "id")
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::COLON)
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::STRING_CONSTANT)
			return false;
		const std::string id = scanner->getLastString();
		if (not SkipUntil(scanner, JSONScanner::CLOSE_BRACE))
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::COMMA)
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::STRING_CONSTANT)
			return false;
		if (scanner->getLastString() != "permission")
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::COLON)
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::STRING_CONSTANT)
			return false;
		if (id == owner) {
			const std::string permission = scanner->getLastString();
			if (permission == "W")
				*owner_has_read_premission = true;
			else if (permission == "R")
				*owner_has_write_premission = true;
		}
		token = scanner->getToken();
		if (token != JSONScanner::CLOSE_BRACE)
			return false;

		token = scanner->getToken();
		if (token == JSONScanner::COMMA)
			token = scanner->getToken();
	}

        token = scanner->getToken();
	if (token != JSONScanner::CLOSE_BRACE)
		return false;

	return true;
}


bool ProcessFileList(JSONScanner * const scanner, std::vector<DirEntry> * const entries) {
	JSONScanner::TokenType token;
	token = scanner->getToken();
	if (token == JSONScanner::CLOSE_BRACKET)
		return true;

	for (;;) {
		if (token != JSONScanner::OPEN_BRACE)
			return false;

		// "name"
		token = scanner->getToken();
		if (token != JSONScanner::STRING_CONSTANT)
			return false;
		if (scanner->getLastString() != "name")
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::COLON)
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::STRING_CONSTANT)
			return false;
		const std::string name = scanner->getLastString();
		token = scanner->getToken();
		if (token != JSONScanner::COMMA)
			return false;

		// "url"
		token = scanner->getToken();
		if (token != JSONScanner::STRING_CONSTANT)
			return false;
		if (scanner->getLastString() != "url")
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::COLON)
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::STRING_CONSTANT)
			return false;
		const std::string url = scanner->getLastString();
		token = scanner->getToken();
		if (token != JSONScanner::COMMA)
			return false;

		// "path"
		token = scanner->getToken();
		if (token != JSONScanner::STRING_CONSTANT)
			return false;
		if (scanner->getLastString() != "path")
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::COLON)
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::STRING_CONSTANT)
			return false;
		const std::string path = scanner->getLastString();
		token = scanner->getToken();
		if (token != JSONScanner::COMMA)
			return false;

		// "owner"
		token = scanner->getToken();
		if (token != JSONScanner::STRING_CONSTANT)
			return false;
		if (scanner->getLastString() != "owner")
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::COLON)
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::OPEN_BRACE)
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::STRING_CONSTANT)
			return false;
		if (scanner->getLastString() != "id")
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::COLON)
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::STRING_CONSTANT)
			return false;
		const std::string owner = scanner->getLastString();
		if (not SkipUntil(scanner, JSONScanner::CLOSE_BRACE))
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::COMMA)
			return false;

		// "size"
		token = scanner->getToken();
		if (token != JSONScanner::STRING_CONSTANT)
			return false;
		if (scanner->getLastString() != "size")
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::COLON)
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::INTEGER_CONSTANT)
			return false;
		const int64_t size = scanner->getLastInteger();
		token = scanner->getToken();
		if (token != JSONScanner::COMMA)
			return false;

		// "lastModified"
		time_t last_modified = 0;
		token = scanner->getToken();
		if (token != JSONScanner::STRING_CONSTANT)
			return false;
		if (scanner->getLastString() != "lastModified")
			goto isDirectory;
		token = scanner->getToken();
		if (token != JSONScanner::COLON)
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::STRING_CONSTANT)
			return false;
		last_modified = TimeUtil::Iso8601StringToTimeT(scanner->getLastString(), TimeUtil::UTC);
		token = scanner->getToken();
		if (token != JSONScanner::COMMA)
			return false;

		// "isDirectory"
		token = scanner->getToken();
		if (token != JSONScanner::STRING_CONSTANT)
			return false;
isDirectory:
		if (scanner->getLastString() != "isDirectory")
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::COLON)
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::BOOLEAN_CONSTANT)
			return false;
		const bool is_directory = scanner->getLastBoolean();
		token = scanner->getToken();
		if (token != JSONScanner::COMMA)
			return false;

		if (!is_directory) {
			// "dataFormat"
			token = scanner->getToken();
			if (token != JSONScanner::STRING_CONSTANT)
				return false;
			if (scanner->getLastString() != "dataFormat")
				goto availableDataFormats;
			token = scanner->getToken();
			if (token != JSONScanner::COLON)
				return false;
			SkipBraceBlock(scanner);
			token = scanner->getToken();
			if (token != JSONScanner::COMMA)
				return false;

			// "availableDataFormats"
			token = scanner->getToken();
			if (token != JSONScanner::STRING_CONSTANT)
				return false;
availableDataFormats:
			if (scanner->getLastString() != "availableDataFormats")
				return false;
			token = scanner->getToken();
			if (token != JSONScanner::COLON)
				return false;
			SkipBracketBlock(scanner);
			token = scanner->getToken();
			if (token != JSONScanner::COMMA)
				return false;
		}

		// "effectiveAcl"
		token = scanner->getToken();
		if (token != JSONScanner::STRING_CONSTANT)
			return false;
		if (scanner->getLastString() != "effectiveAcl")
			return false;
		token = scanner->getToken();
		if (token != JSONScanner::COLON)
			return false;
		bool owner_has_read_premission, owner_has_write_premission;
		if (not ParseEffectiveACL(owner, scanner, &owner_has_read_premission, &owner_has_write_premission))
			return false;

		token = scanner->getToken();
		if (token != JSONScanner::CLOSE_BRACE)
			return false;

		entries->push_back(DirEntry(name, url, owner, is_directory, owner_has_read_premission,
		                            owner_has_write_premission, last_modified, size));

		token = scanner->getToken();
		if (token == JSONScanner::COMMA)
			token = scanner->getToken();
	}
}


bool ParseListing(const std::string &listing, std::vector<DirEntry> * const entries) {
	entries->clear();

	JSONScanner scanner(listing);

	// top-level opening brace
	if (scanner.getToken() != JSONScanner::OPEN_BRACE)
		return false;

	if (scanner.getToken() != JSONScanner::STRING_CONSTANT)
		return false;
	if (scanner.getToken() != JSONScanner::COLON)
		return false;
	if (scanner.getLastString() != "directory")
		return false;

	if (not SkipBraceBlock(&scanner))
		return false;

	if (scanner.getToken() != JSONScanner::COMMA)
		return false;
	if (scanner.getToken() != JSONScanner::STRING_CONSTANT)
		return false;
	if (scanner.getLastString() != "contents")
		return false;
	if (scanner.getToken() != JSONScanner::COLON)
		return false;

	if (scanner.getToken() != JSONScanner::OPEN_BRACKET)
		return false;

	if (not ProcessFileList(&scanner, entries))
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
	ParseListing(json_directory_listing, &dir_entries);
	for (std::vector<DirEntry>::const_iterator entry(dir_entries.begin());
	     entry != dir_entries.end(); ++entry)
		std::cout << entry->toString() << '\n';
}
