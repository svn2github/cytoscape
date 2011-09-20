#ifndef GS_SESSION_H
#define GS_SESSION_H


#include <string>


class GsSession {
	/*
	 * The GenomeSpace login token. It contains the username and an expiration
	 * date/time.
	 */
	std::string token_;

	bool isInitialised_;

	std::string dataManagerServiceUrl_;
public:
	GsSession();
	inline const std::string getSessionToken() { return token_; }
private:
	void initialise();
};


#endif // ifndef GS_SESSION_H


