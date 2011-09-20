#include "GsSession.h"
#include <Downloader.h>
#include <IniFile.h>
#include <MiscUtil.h>
#include <UrlUtil.h>


GsSession::GsSession(): isInitialised_(false) {
	initialise();
}


namespace {


std::string GetToken() {
	const IniFile iniFile(MiscUtil::GetEnv("HOME") + "/etc/GSIdentity.ini");
	const std::string identityServerUrl = iniFile.getString("", "server_url");
	const std::string username = UrlUtil::UrlEncode(iniFile.getString("", "username"));
	const std::string password = UrlUtil::UrlEncode(iniFile.getString("", "password"));
	const char * const colonSlashSlash = std::strstr(identityServerUrl.c_str(), "://");
	if (!colonSlashSlash)
		throw std::runtime_error("identity server URL does not contain \"://\"!");
	const ptrdiff_t prefixLength = colonSlashSlash - identityServerUrl.c_str() + 3;
	const std::string authUrl = identityServerUrl.substr(0, prefixLength) + username + ":" + password
	                            + "@" + identityServerUrl.substr(prefixLength);
	Downloader downloader(authUrl);
	if (downloader.anErrorOccurred())
		throw std::runtime_error("authentication server error: " + downloader.getLastErrorMessage());
	return downloader.getMessageBody();
}


} // unnamed namespace


void GsSession::initialise() {
	token_ = GetToken();
	isInitialised_ = true;
}
