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

	std::string identityServiceUrl_;
	std::string dataManagerServiceUrl_;
public:
	/**
	 * Basic constructor uses the default URL for the Identity Service.
	 */
	GsSession();

	GsSession(const std::string &token);

	bool login(const std::string &username, const std::string &password);
private:
	void initialise();
};


#endif // ifndef GS_SESSION_H


