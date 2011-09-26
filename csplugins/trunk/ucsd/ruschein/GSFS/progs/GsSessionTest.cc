#include <iostream>
#include <stdexcept>
#include "GsSession.h"


void Usage() {
	std::cerr << "usage: GsSessionTest relative_url\n";
}


int main(int argc, char *argv[]) {
	if (argc != 2)
		Usage();

	GsSession session;
	std::cout << "Session token = " << session.getServerResponse(argv[1]) << '\n';
}
