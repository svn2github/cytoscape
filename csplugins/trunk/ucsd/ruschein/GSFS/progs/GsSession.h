#ifndef GS_SESSION_H
#define GS_SESSION_H


#include <string>


class GsSession {
	bool isInitialised_;

	/*
	 * The GenomeSpace login token. It contains the username and an expiration
	 * date/time.
	 */
	std::string token_;

	std::string username_;
	std::string data_manager_root_;
	std::string domain_;
public:
	GsSession();

	/** \return empty string if an error occurred, otherwise the message body of the message sent by the server. */
	std::string getServerResponse(const std::string &relative_url);
private:
	void initialise();
	inline const std::string getSessionToken() { return token_; }
};


#endif // ifndef GS_SESSION_H


