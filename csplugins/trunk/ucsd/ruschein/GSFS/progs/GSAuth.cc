#include <iostream>
#include <string>
#include <IniFile.h>
#include <MiscUtil.h>
#include <StringUtil.h>
#include <UrlUtil.h>


int main(int /*argc*/, char */*argv*/[]) {
	try {
		const IniFile iniFile(MiscUtil::GetEnv("HOME") + "/etc/GenomeSpace.ini");
		const std::string host = iniFile.getString("", "gsIdentityServer");
		const std::string username = UrlUtil::UrlEncode(iniFile.getString("", "gsUsername"));
		const std::string password = UrlUtil::UrlEncode(iniFile.getString("", "gsPassword"));
		const std::string protocol = iniFile.getString("", "gsProtocol");
		const unsigned port = iniFile.getUnsigned("", "gsPort");
		const std::string context = iniFile.getString("", "gsContext");

		const std::string loginUrl = protocol + "://" + username + ":" + password + "@" + host
		                             + ":" + StringUtil::ToString(port) + "/" + context + "/auth";

		const std::string logoutUrl = protocol + "://" + host + ":" + StringUtil::ToString(port) + "/" + context + "/logout";

		std::cout << "loginUrl = " << loginUrl << '\n';
	} catch (const std::exception &x) {
		std::cerr << "** Caught exception: " << x.what() << '\n';
	}
}
