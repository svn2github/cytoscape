#include <iostream>
#include <vector>
#include <cstdlib>
#include <FileUtil.h>


void Usage() {
	std::cerr << "usage: ParseDirectory GenomeSpace_JSON_directory_listing\n";
	std::exit(EXIT_FAILURE);
}


class DirEntry {
};


bool Expect(std::string::const_iterator &ch, const std::string::const_iterator &end, const char * const expected_string) {
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


bool ParseBoolean(std::string::const_iterator &ch, const std::string::const_iterator &end, bool * const value) {
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


// Assumes that "ch" points to the first character of the string.
bool ParseString(std::string::const_iterator &ch, const std::string::const_iterator &end, std::string * const value) {
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


bool ParseListing(const std::string &listing, std::vector<DirEntry> * const entries) {
	entries->clear();

	for (std::string::const_iterator ch = listing.begin(); ch != listing.end(); ++ch) {
	}

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

	std::cout << json_directory_listing << '\n';
}
