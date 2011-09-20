#include <iostream>
#include <stdexcept>
#include "GsSession.h"


int main() {
	GsSession session;
	std::cout << "Session token = " << session.getSessionToken() << '\n';
}
