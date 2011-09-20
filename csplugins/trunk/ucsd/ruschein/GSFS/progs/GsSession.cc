#include "GsSession.h"


GsSession::GsSession(): isInitialised_(false) {
}


GsSession::GsSession(const std::string &token): isInitialised_(false) {
	token_ = token;
}


bool GsSession::login(const std::string &/*username*/, const std::string &/*password*/) {
	if (!isInitialised_)
		initialise();

	return true;
}


void GsSession::initialise() {
	isInitialised_ = true;
}
