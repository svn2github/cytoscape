#include "GsSession.h"
#include <Downloader.h>
#include <IniFile.h>
#include <MiscUtil.h>
#include <SList.h>
#include <UrlUtil.h>


GsSession::GsSession(): isInitialised_(false) {
	initialise();
}


static std::string GetToken(const IniFile &iniFile, const std::string &username) {
	const std::string identityServerUrl = iniFile.getString("", "server_url");
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


void GsSession::initialise() {
	const IniFile iniFile(MiscUtil::GetEnv("HOME") + "/etc/GSIdentity.ini");
	username_ = UrlUtil::UrlEncode(iniFile.getString("", "username"));
	token_ = GetToken(iniFile, username_);
	data_manager_root_ = iniFile.getString("", "datamanager_root");
	domain_ = UrlUtil::UrlEncode(iniFile.getString("", "domain"));

	isInitialised_ = true;
}


std::string GsSession::getServerResponse(const std::string &relative_url) {
	SList<std::string> http_headers;
	http_headers.push_back("Cookie: gs-username=" + username_ + "; Domain=" + domain_);
	http_headers.push_back("Cookie: gs-token=" + token_ + "; Domain=" + domain_);
	http_headers.push_back("Accept: application/json,text/plain");
	const Url url(data_manager_root_ + relative_url);
	Downloader downloader(url, http_headers);
	if (downloader.anErrorOccurred()) {
		if (downloader.getLastErrorMessage() != "Please Authenticate")
			return "";
		const IniFile iniFile(MiscUtil::GetEnv("HOME") + "/etc/GSIdentity.ini");
		token_ = GetToken(iniFile, username_); // Refresh the token
		Downloader downloader2(url, http_headers);
		return downloader2.anErrorOccurred() ? "" : downloader2.getMessageBody();
	} else
		return downloader.getMessageBody();
}

