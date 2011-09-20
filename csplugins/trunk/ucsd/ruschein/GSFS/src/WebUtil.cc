/** \file    WebUtil.cc
 *  \brief   Implementation of WWW related utility functions.
 *  \author  Dr. Johannes Ruscheinski
 *  \author  Dr. Gordon W. Paynter
 *  \author  Artur Kedzierski
 */

/*
 *  Copyright 2002-2009 Project iVia.
 *  Copyright 2002-2009 The Regents of The University of California.
 *
 *  This file is part of the libiViaCore package.
 *
 *  The libiViaCore package is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2 of the License,
 *  or (at your option) any later version.
 *
 *  libiViaCore is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with libiViaCore; if not, write to the Free Software Foundation, Inc.,
 *  59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

#include <WebUtil.h>
#include <algorithm>
#include <memory>
#include <sstream>
#include <cctype>
#include <cerrno>
#include <cstdio>
#include <cstdlib>
#include <alloca.h>
#include <netinet/tcp.h>
#include <sstream>
#include <sys/socket.h>
#include <sys/time.h>
#include <sys/timeb.h>
#include <sys/utsname.h>
#include <sys/wait.h>
#include <unistd.h>
#include <DnsUtil.h>
#include <FileDescriptor.h>
#include <FileUtil.h>
#include <HttpHeader.h>
#include <HtmlUtil.h>
#include <IniFile.h>
#include <MediaTypeUtil.h>
#include <MiscUtil.h>
#include <MsgUtil.h>
#include <SocketUtil.h>
#include <StringUtil.h>
#include <TextUtil.h>
#include <TimeUtil.h>
#include <UrlUtil.h>
#include <ValueListMap.h>
#include <WallClockTimer.h>
#include <Logger.h>


namespace {


std::string GetBinDir() {
#ifdef __MACH__
	return MiscUtil::GetEnv("HOME") + "/bin";
#else
	return BIN_DIR;
#endif
}


// ShellEscapeSingleQuotes -- assumes "*s" are the contents of a single-quoted string.
//
std::string ShellEscapeSingleQuotes(std::string * const s)
{
	std::string temp;
	for (std::string::const_iterator ch(s->begin()); ch != s->end(); ++ch) {
		if (*ch == '\'')
			temp += "'\\''";
		else
			temp += *ch;
	}

	return *s = temp;
}


// ParseMultiPartFormDataHeader -- parses a multipart/form-data header.
//
void ParseMultiPartFormNumber(std::string * const random_number) throw(std::exception)
{
	// Read 29 dashes:
	char dashes[29]; // Caution: intentionally no room for a trailing '\0'
	std::cin.read(dashes, 29);
	if (std::cin.bad() or std::cin.gcount() != 29 or std::strncmp(dashes, "-----------------------------", 29) != 0)
		throw Exception("In WebUtil::ParseMultiPartFormDataHeader: Read failure while parsing multipart/form-data header!");

	// Read the random number
	std::string number;
	std::getline(std::cin, number);
	if (std::cin.bad())
		throw Exception("In WebUtil::ParseMultiPartFormDataHeader: Unexpected failure while trying to read the random number!");
	if (random_number->length() == 0) {
		StringUtil::RemoveTrailingLineEnd(&number);
		*random_number = number;
	}
	else if (number == *random_number)
		throw Exception("In WebUtil::ParseMultiPartFormDataHeader: Invalid random number in the multipart/form-data header!");
}


} // unnamed namespace


namespace WebUtil {


// Unescape -- replaces the escaped characters in "src" with the unescaped version in "dest."  "dest" should be at least size("src").  "src" has to be zero
//             terminated.  "dest" will also be zero terminated.   Returns the number of bytes written to "dest."
//
ptrdiff_t Unescape(char * const dest, const char * const src)
{
	char *dest_cp = dest;
	for (const char *cp = src; *cp != '\0'; ++cp) {
		if (*cp == '\\') {
			// Skip over the escape character (backslash)
			++cp;
			if (*cp == '\0')
				throw Exception("in WebUtil::Unescape: Unexpected end of encoded string!");
		}
		*dest_cp++ = *cp;
	}
	*dest_cp = '\0';
	return (dest_cp - dest) + 1;
}


BrowserKeepAlive::BrowserKeepAlive(const int fd, const unsigned pulse_interval)
{
	child_pid_ = ::fork();
	if (child_pid_ == -1)
		throw Exception("in BrowserKeepAlive::BrowserKeepAlive: fork(2) failed! (Out of memory?)");

	if (child_pid_ != 0) { // We're in the parent.
		cancelled_ = false;
		fd_ = fd;
	}
	else { // We're in the child.
		::alarm(0); // Just in case.  We don't want to interfere with sleep(3).
		cancelled_ = true;

		const std::string html_comment("<!-- keepalive -->\n");
		for (;;) {
			::sleep(pulse_interval);
			if (::write(fd_, html_comment.c_str(), html_comment.length()) == -1)
				::_exit(EXIT_FAILURE);
		}
	}
}


void BrowserKeepAlive::cancel()
{
	if (cancelled_)
		return;

	cancelled_ = true;
	if (::kill(child_pid_, SIGKILL) == -1)
	      MsgUtil::SysError("in WebUtil::BrowserKeepAlive::cancel: kill(2) failed: ");
	switch (::waitpid(child_pid_, NULL, 0)) {
	case 0:
		throw Exception("in WebUtil::BrowserKeepAlive::cancel: tried to wait on unavailable child!");
	case -1:
		if (errno == ECHILD)
			throw Exception("in WebUtil::BrowserKeepAlive::cancel: tried to wait on nonexistent child!");
		MsgUtil::SysError("in WebUtil::BrowserKeepAlive::cancel: unexpected error return from waitpid(2): ");
	}
}


namespace {


// ProcessArg -- helper function for GetPostArgs(), GetGetArgs() and GetArgvArgs() and others.
//
void ProcessArg(const char *line, std::string * const name, std::string * const value, const bool url_decode = true)
{
	const char * const equal_sign = std::strchr(line, '=');
	if (equal_sign != NULL) {
		// line now points to a string of the form "variable_name=value".
		*value = equal_sign + 1;
		if (url_decode)
			UrlUtil::UrlDecode(value);
		*name = std::string(line, equal_sign - line);
		if (url_decode)
			UrlUtil::UrlDecode(name);
	}
	else {
		*name = line;
		if (url_decode)
			UrlUtil::UrlDecode(name);
		value->clear();
	}
}


// GetPostArg -- retrieves the next HTTP POST argument from stdin.  If there is no argument then "name_value_pair" will be empty.  Returns "true" on
//               EOF, otherwise "false".
//
bool GetPostArg(std::string * const name_value_pair)
{
	*name_value_pair = "";
	for (;;) {
		int ch = std::cin.get();
		if (ch == '&')
			return false;
		if (ch == EOF)
			return true;
		*name_value_pair += static_cast<char>(ch);
	}
}


} // unnamed namespace


// GetPostArgs -- returns a mapping between HTTP POST arguments and their values.  The map is multivalued, i.e. each
//                variable has a list of values associated with it.
//
void GetPostArgs(ValueListMap * const post_args)
{
	bool eof_seen = false;
	while (not eof_seen) {
		std::string name_value_pair;
		eof_seen = GetPostArg(&name_value_pair);
		if (name_value_pair.length() > 0) {
			std::string name, value;
			ProcessArg(name_value_pair.c_str(), &name, &value);
			post_args->insert(name, value);
		}
	}
}


// GetGetArgs -- returns a mapping between HTTP GET arguments and their values.  The map is multivalued, i.e. each
//               variable has a list of values associated with it.
//
void GetGetArgs(ValueListMap * const post_args)
{
	char *query_string = ::getenv("QUERY_STRING");
	if ((query_string == NULL) or (std::strlen(query_string) == 0))
		return;

	std::list<std::string> get_args;
	StringUtil::SplitThenTrim(query_string, "&", "", &get_args);
	for (std::list<std::string>::const_iterator i = get_args.begin(); i != get_args.end(); ++i) {
		std::string name, value;
		ProcessArg((*i).c_str(), &name, &value);
		post_args->insert(name, value);
	}
}


// GetArgvArgs -- returns a mapping between arguments passed as ARGV and their values.
//                The map is multivalued, i.e. each variable has a list of values
//                associated with it.
//
void GetArgvArgs(const int argc, char * argv[], ValueListMap * const argv_args)
{
	for (int i = 1; i < argc; ++i) {
		std::string name, value;
		ProcessArg(argv[i], &name, &value, /* url_decode = */ false);
		argv_args->insert(name, value);
	}
}


// GetMultiPartArgs --  returns a mapping between arguments passed as multiform and their values.
//                      The map is multivalued, i.e. each variable has a list of values
//                      associated with it.
//
void GetMultiPartArgs(ValueListMap * const post_args, const bool save_file_to_disk)
{
	bool all_parsed = false;
	std::string field_name, random_number, file_name;
	ParseMultiPartFormNumber(&random_number);
	while (not all_parsed) {
		std::ostringstream field_value_stream;
		file_name = "";
		ParseMultiPartFormDataHeader(&field_name, &file_name);
		std::string arg_value;
		if (not file_name.empty() and save_file_to_disk) {
			std::string temp_filename = FileUtil::UniqueFileName("" /* default directory */, file_name);
			std::ofstream outfile(temp_filename.c_str(), std::ios::out);
			if (not outfile)
				throw Exception("WebUtil::GetMultiPartArgs: cannot open temporary file!");
			all_parsed = ReadMultiPartFormData(random_number.c_str(), outfile);
			arg_value = temp_filename;
		}
		else {
			all_parsed = ReadMultiPartFormData(random_number.c_str(), field_value_stream);
			arg_value = field_value_stream.str();
		}
		post_args->insert(field_name, arg_value);
		if (not file_name.empty()) {
			// If it is a file, we want to index its value using its file name
			// because the field name is always "filename"
			std::string filename_index = field_name + "_filename_";
			post_args->insert(filename_index, file_name);
		}
	}
}


void GetAllCgiArgs(ValueListMap * const cgi_args, int argc, char *argv[])
{
	// We check argv[1] because in GET method argv[1] is set to a blank line.
	if (argc > 1 and std::strlen(argv[1]) >= 2)
		GetArgvArgs(argc, argv, cgi_args);
	else {
		GetGetArgs(cgi_args);

		// Do not also attempt to get POST arguments if there is nothing to read on stdin within 1 second:
		if (not FileUtil::DescriptorIsReadyForReading(STDIN_FILENO, 1000 /* ms */))
			return;

		// Check whether this is a 'POST' or 'multipart' form.  Since variables don't begin with '-' in POST we
		// only test for a single dash.
		if (std::cin.peek() == '-')
			GetMultiPartArgs(cgi_args);
		else
			GetPostArgs(cgi_args);
	}
}


namespace {


// Google -- prepares a Google query "query".  If "link" is true then "query" must be a URL and the query will return
//           links to documents refering to the URL.  "max_no_of_results" is the max. number of search results requested
//           and "start" is the number of the first search result.
//
std::string Google(const std::string &query, const unsigned max_no_of_results, const bool link = false,
		   const unsigned start = 1)
{
	std::string request("http://www.google.com/search?");
	if (link)
		request += "as_lq=";
	else
		request += "q=";
	request += query;

	request += "&hl=en&lr=&start=";

	char buf[20+1];
	std::sprintf(buf, "%u", start);
	request += buf;

	request += "&num=";
	std::sprintf(buf, "%u", max_no_of_results);
	request += buf;

	request += "&safe=off";

	return request;
}


} // unnamed namespace


// ParseMultiPartFormDataHeader -- parses a multipart/form-data header.
//
void ParseMultiPartFormDataHeader(std::string * const field_name, std::string * const file_name) throw(std::exception)
{
	// Read Content-disposition line
	const char TEXT[] = "Content-Disposition: form-data; name=";
	char buf[sizeof(TEXT)];
	std::cin.getline(buf, sizeof(buf), '"');
	if (std::strcmp(buf, TEXT) != 0) {
		std::string msg = "In WebUtil::ParseMultiPartFormDataHeader: "
			"Can't find " + std::string(TEXT) + " in multipart/form-data header!";
		throw Exception(msg);
	}

	// Read the name of Content-Disposition
	std::string name;
	std::getline(std::cin, name, '"');
	if (name.empty())
		throw Exception("In WebUtil::ParseMultiPartFormDataHeader: "
			       "Field-name is empty!");
	*field_name = name;

	// Read filename, if provided.
	if (file_name != NULL and std::cin.peek() == ';') {
		const char FILE_NAME_TEXT[] = "; filename=";
		char buffer[sizeof(FILE_NAME_TEXT)];
		std::cin.getline(buffer, sizeof(buffer), '"');
		if (std::strcmp(buffer, FILE_NAME_TEXT) != 0) {
			std::string msg = "In WebUtil::ParseMultiPartFormDataHeader: "
				"Can't find \"" + std::string(FILE_NAME_TEXT) + "\" in multipart/form-data header!";
			throw Exception(msg);
		}

		// Now get the actual filename
		std::getline(std::cin, name, '"');
		*file_name = name;
	}
	std::getline(std::cin, name); // read the left-overs from the line

	// Ignore headers until the blank line.
	bool is_end_of_header_found=false;
	while (not std::cin.eof() and not is_end_of_header_found) {
		std::string line_to_ignore;
		std::getline(std::cin, line_to_ignore);
		StringUtil::RemoveTrailingLineEnd(&line_to_ignore);
		if (line_to_ignore == "")
			is_end_of_header_found = true;
	}
}


bool ReadMultiPartFormData(const char * const random_number, std::ostream &output)
{
	std::string buffer;
	std::string last_line = "-----------------------------";
	last_line += random_number;

	bool all_data_read = false;
	bool end_of_form_found = false;
	bool first_line = true;
	while (not std::cin.eof() and not all_data_read) {
		// Read the next line
		FileUtil::GetLine(std::cin, &buffer);
		StringUtil::RemoveTrailingLineEnd(&buffer);

		// If this is the last line, ignore the rest of the file
		if (buffer == last_line)
			all_data_read = true;
		else if (buffer == (last_line + "--")) {
			end_of_form_found = true;
			all_data_read = true;
		}
		else {
			// The 'last_line' is more of a new line character followed by dashes
			// and by the random number. We don't want to add that new
			// line character to the output.
			if (not first_line)
				output << "\n";
			else
				first_line = false;
			output << buffer;
		}
	}

	return end_of_form_found;
}


std::string GetFileViaHttpForm()
{
	// Read the Multi-part Form Data file
	std::string random_number;
	std::string field_name;
	std::string temporary_filename = FileUtil::UniqueFileName();

	bool file_found = true;
	ParseMultiPartFormNumber(&random_number);
	do {
		ParseMultiPartFormDataHeader(&field_name);
		if (field_name == "filename") {
			std::ofstream store_data(temporary_filename.c_str(), std::ios::out);
			if (not store_data) {
				std::string err_msg("Unable to open file \"%s\" for writing." + temporary_filename);
				throw Exception(err_msg);
			}
			ReadMultiPartFormData(random_number.c_str(), store_data);
			store_data.close();
		}
		else {
			// Discard the data part to get to the next part.
			std::ostringstream junk_stream;
			ReadMultiPartFormData(random_number.c_str(), junk_stream);
		}
	} while (not file_found);

	return temporary_filename;
}


Cookies::Cookies(const std::string &name, const std::string &value, const unsigned max_age, const std::string &domain, const std::string &path)
{
	insert(name, value, max_age, domain, path);
}


void Cookies::insert(const std::string &name, const std::string &value, const uint64_t max_age, const std::string &domain, const std::string &path,
		     const time_t expires)
{
	// Calculate the actual cookie expiration time form "expires":
	time_t actual_expires;
	if (expires == 0) { // Default => use current time + "max_age"
		const time_t now(::time(NULL));
		actual_expires = now + max_age;
	}
	else
		actual_expires = expires;

	std::string actual_domain(domain);
	if (unlikely(name.empty()))
		throw Exception("in WebUtil::Cookies::insert: you must provide a non-empty cookie name!");
	if (unlikely(name[0] == '$'))
		throw Exception("in WebUtil::Cookies::insert: a cookie name must not begin with a $-sign!");
	// User Agents should be able to fix this themselves but we don't trust them:
	if (not domain.empty() and unlikely(domain[0] != '.'))
		actual_domain.insert(0, ".");

	cookies_.insert(std::make_pair<std::string, Cookie>(name, Cookie(name, value, max_age, actual_domain, path, actual_expires)));
}


void Cookies::printHeader() const
{
	if (unlikely(cookies_.empty()))
		throw Exception("in WebUtil::Cookies::printHeader: you must specify at least one cookie before calling this function!");

	for (std::map<std::string, Cookie>::const_iterator cookie(cookies_.begin()); cookie != cookies_.end();
	     ++cookie)
	{
		std::cout << "Set-Cookie: ";
		std::cout << cookie->second.name_ << "=" << StringUtil::CStyleEscape(cookie->second.value_) << "; ";
		std::cout << "Version=1; ";
		std::cout << "Expires=" << TimeUtil::TimeTToUtcString(cookie->second.expires_, "%a, %d-%b-%Y %H:%M:%S GMT") << ";";
		std::cout << "Max-Age=" << cookie->second.max_age_
			  << (cookie->second.domain_.empty() and cookie->second.path_.empty() ? "" : "; ");
		if (not cookie->second.domain_.empty())
			std::cout << "Domain=" << cookie->second.domain_ << (cookie->second.path_.empty() ? "" : "; ");
		if (not cookie->second.path_.empty())
			std::cout << "Path=" << cookie->second.path_;
		std::cout << "\r\n";
	}

}


void GetCookies(std::map<std::string, std::string> * const cookies)
{
	cookies->clear();

	const std::string cookie_string(MiscUtil::SafeGetEnv("HTTP_COOKIE"));
	if (cookie_string.empty())
		return;

	std::list<std::string> names_and_values;
	StringUtil::SplitThenTrim(cookie_string, "; ", "", &names_and_values);
	for (std::list<std::string>::const_iterator name_and_value(names_and_values.begin());
	     name_and_value != names_and_values.end(); ++name_and_value)
	{
		std::string name, value;
		ProcessArg(name_and_value->c_str(), &name, &value);
		cookies->insert(std::make_pair<std::string, std::string>(name, StringUtil::CStyleUnescape(value)));
	}
}


// HtmlMessageToHeaderAndBody -- split an HTML header and body.
//
void HtmlMessageToHeaderAndBody(const std::string &message, std::string * const header, std::string * const body)
{
	std::string::size_type split(message.find("\r\n\r\n"));
	if (split == std::string::npos) {
		split = message.find("\n\n");
		if (split != std::string::npos)
			split += 2;
		else {
			*header = message;
			body->clear();
		}
	}
	else
		split += 4;

	if (split != std::string::npos) {
		*header = message.substr(0, split);
		if (split < message.size())
			*body = message.substr(split);
		else
			body->clear();
	}
}


bool ExecCGI(const std::string &username_password, const std::string &address, const unsigned short port,
	     const TimeLimit &time_limit, const std::string &cgi_path, const StringMap &post_args,
	     std::string * const document_source, std::string * const error_message, const std::string &accept,
	     const bool include_http_header)
{
	document_source->clear();
	error_message->clear();

	try {
		std::string tcp_connect_error_message;
		const FileDescriptor socket_fd(SocketUtil::TcpConnect(address, port, time_limit,
								      &tcp_connect_error_message));
		if (socket_fd == -1) {
			*error_message = "Could not open TCP connection to " + address + ", port "
				+ StringUtil::ToString(port) + ": " + tcp_connect_error_message;
			*error_message += " (Time remaining: " + StringUtil::ToString(time_limit.getRemainingTime())
				          + ").";
			return false;
		}

		std::string data_to_be_sent("POST ");
		data_to_be_sent += cgi_path;
		data_to_be_sent += " HTTP/1.0\r\n";
		data_to_be_sent += "Host: ";
		data_to_be_sent += address;
		data_to_be_sent += "\r\n";
		data_to_be_sent += "User-Agent: ExecCGI/1.0 iVia\r\n";
		data_to_be_sent += "Accept: " + accept + "\r\n";
		data_to_be_sent += "Accept-Encoding: identity\r\n";

		// Do we want a username and password to be sent?
		if (not username_password.empty()) { // Yes!
			if (unlikely(username_password.find(':') == std::string::npos))
				throw Exception("in WebUtil::ExecCGI: username/password pair is missing a "
							 "colon!");
			data_to_be_sent += "Authorization: Basic " + TextUtil::Base64Encode(username_password) + "\r\n";
		}

		data_to_be_sent += WwwFormUrlEncode(post_args);

		if (SocketUtil::TimedWrite(socket_fd, time_limit, data_to_be_sent.c_str(), data_to_be_sent.length())
		    == -1)
		{
			*error_message = "Could not write to socket";
			*error_message += " (Time remaining: " + StringUtil::ToString(time_limit.getRemainingTime())
				          + ")";
			*error_message += '!';
			return false;
		}

		char http_response_header[10240+1];
		ssize_t no_of_bytes_read = SocketUtil::TimedRead(socket_fd, time_limit, http_response_header,
								 sizeof(http_response_header) - 1);
		if (no_of_bytes_read == -1) {
			*error_message = "Could not read from socket (1).";
			*error_message += " (Time remaining: " + StringUtil::ToString(time_limit.getRemainingTime())
				          + ").";
			return false;
		}
		http_response_header[no_of_bytes_read] = '\0';
		HttpHeader http_header(http_response_header);

		// the 2xx codes indicate success:
		if (http_header.getStatusCode() < 200 or http_header.getStatusCode() > 299) {
			*error_message = "Web server returned error status code ("
				+ StringUtil::ToString(http_header.getStatusCode()) + ")";
			return false;
		}

		// read the returned document source:
		std::string response(http_response_header, no_of_bytes_read);
		char buf[10240+1];
		do {
			no_of_bytes_read = SocketUtil::TimedRead(socket_fd, time_limit, buf, sizeof(buf) - 1);
			if (no_of_bytes_read == -1) {
				*error_message = "Could not read from socket (2).";
				*error_message += " (Time remaining: "
					          + StringUtil::ToString(time_limit.getRemainingTime()) + ").";
				return false;
			}
			if (no_of_bytes_read > 0)
				response += std::string(buf, no_of_bytes_read);
		} while (no_of_bytes_read > 0);

		if (include_http_header)
			*document_source = response;
		else {
			std::string::size_type pos = response.find("\r\n\r\n"); // the header ends with two cr/lf pairs!
			if (pos != std::string::npos) {
				pos += 4;
				*document_source = response.substr(pos);
			}
		}

		return true;
	}
	catch (const std::exception &x) {
		std::string err_msg("in WebUtil::ExecCGI: (address = " + address + ") caught exception: "
				    + std::string(x.what()));
		throw Exception(err_msg);
	}
}


std::string UserIpAddress()
{
	// Read the IP Address
	return MiscUtil::SafeGetEnv("REMOTE_ADDR");
}


std::string UserBrowserDescription()
{
	// Read the user agent
	const std::string http_user_agent(MiscUtil::SafeGetEnv("HTTP_USER_AGENT"));
	if (http_user_agent.empty())
		return "unknown";

	std::string user_agent(http_user_agent);

	// If this is a non-Mozilla USER_AGENT, return the complete string
	if (user_agent.length() < 8
	    or user_agent.substr(0, 7) != "Mozilla")
		return user_agent;

	// If this is a 5.0 browser, determine if it is Netscape 6.x or 7.x
	if (user_agent.length() > 9 and user_agent[8] == '5') {
		if (user_agent.find("Netscape6/6") != std::string::npos)
			return "Netscape/6";
		else if (user_agent.find("Netscape/7") != std::string::npos)
			return "Netscape/7";
	}

	// If this is a better-than-4.X browser
	if (user_agent.length() > 9 and user_agent[8] != '4')
		return user_agent.substr(0, 9);

	// Is this IE reporting that it is Mozilla/4.0 compatible?
	if (user_agent.find("MSIE") != std::string::npos) {
		unsigned version_index = user_agent.find("MSIE") + 5;
		if (user_agent.length() > version_index)
			return "MSIE/" + user_agent.substr(version_index, 1);
	}

	// This must be Netscape 4
	return "Netscape/4";
}


bool IsProbablyEnglish(const std::string &html_document, const HttpHeader * const http_header)
{
	if (http_header != NULL and http_header->isProbablyEnglish())
		return true;

	return HtmlUtil::IsProbablyEnglish(html_document);
}


// GetMajorSite -- Get the highest-level registerable domain for a URL.
//
std::string GetMajorSite(const Url &url)
{
	// Sanity checks:
	if (not url.isValidWebUrl() or not url.isAbsolute())
		return "";
	const std::string authority(url.getAuthority());
	if (authority.empty())
		return "";

	// Parse the URL:
	std::vector<std::string> parts;
	StringUtil::Split(authority, ".", &parts);
	const unsigned size(parts.size());
	if (size < 2)
		return "";

	// Construct the simplest identifier:
	const std::string top_level(parts[size - 1]);
	const std::string second_level(parts[size - 2]);
	std::string result(second_level + "." + top_level);

	// We may need to extend the identifier to the third level
	// because some countries limit second-level domains, while
	// others allow them to be registered by anyone, and the US
	// and Canada use a mix of the two approaches:
	const bool is_country_domain(top_level.length() == 2);
	if (is_country_domain and size >= 3) {
		unsigned no_of_parts = 2;

		// Handle the USA and Canada:
		if (top_level == "us" or top_level == "ca") {
			if (second_level.length() == 2) {
				no_of_parts = 3;
				if (top_level == "us" and size >= 4)
					no_of_parts = 4;
			}
			else if (second_level == "biz")
				no_of_parts = 3;
		}

		// Handle the rest of the world:
		else if (top_level == "uk" or top_level == "au"
			 or second_level == "ac" or second_level == "biz" or second_level == "co"
			 or second_level == "com" or second_level == "edu" or second_level == "gen"
			 or second_level == "gov" or second_level == "govt"
			 or second_level == "net" or second_level == "org" or second_level == "school"
			 or top_level == "il" or top_level == "jp" or top_level == "kr" or top_level == "nz")
			no_of_parts = 3;

		// Construct the new identifier:
		if (no_of_parts == 3)
			result = parts[size - 3] + "." + result;
		else if (no_of_parts == 4)
			result = parts[size - 4] + "." + parts[size - 3] + "." + result;
	}

	return result;
}


// PrecacheURLs -- Pull the Web pages corresponding to a set of URLs into the page cache so that we can retrieve them
//                 quickly when we need them.
//
bool PrecacheURLs(const std::list<std::string> &url_list, const std::string &user_agent_string,
		  const unsigned page_cacher_max_fanout, const unsigned overall_timeout,
		  const unsigned individual_page_timeout, const bool use_canonize_mode,
		  const bool ignore_robots_dot_txt, const unsigned verbosity, const std::string &log_filename)
{
	if (url_list.empty())
		return true;

	std::auto_ptr<Logger> logger(log_filename.empty() ? NULL : new Logger(log_filename));

	int pipe_fds[2];
	if (::pipe(pipe_fds) == -1)
		throw Exception("in WebUtil::PrecacheURLs: pipe(2) failed: " + MsgUtil::ErrnoToString());

	const pid_t pid = ::fork();
	if (pid == -1)
		throw Exception("in WebUtil::PrecacheURLs: fork(2) failed! (Out of memory?)");

	if (pid == 0) { // We're the child.
		// Close the write-end of the pipe and make stdin the same as the read-end of the pipe:
		::close(pipe_fds[1]);
		::dup2(pipe_fds[0], STDIN_FILENO);

		::close(STDOUT_FILENO);
		::close(STDERR_FILENO);

		// Construct the "argv" array for the page-cacher program:
		std::list<std::string> args;
		args.push_back("iViaCore-page-cacher");
		if (use_canonize_mode)
			args.push_back("--canonize-mode");
		if (ignore_robots_dot_txt)
			args.push_back("--dont-honour-robots-dot-txt");
		args.push_back("--verbosity=" + StringUtil::ToString(verbosity));
		args.push_back("--user-agent=" + user_agent_string);
		args.push_back("--fanout=" + StringUtil::ToString(page_cacher_max_fanout));
		args.push_back("--max-consecutive-failure-count=" + StringUtil::ToString(page_cacher_max_fanout * 2));
		args.push_back("--overall-timeout=" + StringUtil::ToString(overall_timeout));
		args.push_back("--individual-page-timeout=" + StringUtil::ToString(individual_page_timeout));
		if (not log_filename.empty())
			args.push_back("--log-filename=" + log_filename);
		char *argv[args.size()];
		unsigned index(0);
		for (std::list<std::string>::iterator arg(args.begin()); arg != args.end(); ++arg)
			argv[index++] = ::strdup(arg->c_str());
		argv[index] = NULL;

		const std::string executable_path(GetBinDir() + "/iViaCore-page-cacher");
		::execv(executable_path.c_str(), argv);
		if (logger.get() != NULL)
			logger->log("Files open after executing " + executable_path + ": "
				    + StringUtil::ToString(FileUtil::GetOpenFileDescriptorCount()));
		std::string error_message("in WebUtil::PrecacheURLs: execv(3) failed in child process: ");
		error_message += MsgUtil::ErrnoToString();
		MsgUtil::Error(error_message);
	}

	//
	// If we get here, we're the parent.
	//

	// Close the read-end of the pipe:
	::close(pipe_fds[0]);

	// Feed the child the list of URLs to be precached:
	for (std::list<std::string>::const_iterator iter(url_list.begin()); iter != url_list.end(); ++iter) {
		const ssize_t count(std::strlen(iter->c_str()));
		if (::write(pipe_fds[1], iter->c_str(), count) != count) {
			::close(pipe_fds[1]); // Close the write-end of the pipe.
			throw Exception("in WebUtil::PrecacheURLs: can't write to child process ("
					+ MsgUtil::ErrnoToString() + ")!");
		}

		if (::write(pipe_fds[1], "\n", 1) != 1) {
			::close(pipe_fds[1]); // Close the write-end of the pipe.
			throw Exception("in WebUtil::PrecacheURLs: can't write newline to child process ("
					+ MsgUtil::ErrnoToString() + ")!");
		}
	}

	// Close the write-end of the pipe:
	::close(pipe_fds[1]);

	int status;
	if (::waitpid(pid, &status, 0) == -1)
		throw Exception("in WebUtil::PrecacheURLs: waitpid(2) failed: " + MsgUtil::ErrnoToString());

	if (logger.get() != NULL)
		logger->log("Files open after finishing feeding the child program URLs: "
			    + StringUtil::ToString(FileUtil::GetOpenFileDescriptorCount()));

	return WEXITSTATUS(status) == 0;
}


namespace {


// ExtractLinksFollowingString -- helper function for ExtractSomeJavaScriptLinks().
//
void ExtractLinksFollowingString(const std::string &document_source, const std::string &string_to_look_for,
				 std::vector<std::string> * const extracted_urls)
{
	std::string::size_type next_match(document_source.find(string_to_look_for));
	while (next_match != std::string::npos) {
		// Skip over optional whitespace:
		std::string::const_iterator ch(document_source.begin() + next_match + string_to_look_for.size());
		while (ch != document_source.end() and isspace(*ch))
			++ch;
		if (unlikely(ch == document_source.end())) // Gone too far?
			return;

		// Now we need to match an equal sign:
		if (*ch != '=')
			return;
		++ch;
		if (unlikely(ch == document_source.end())) // Gone too far?
			return;

		// Skip over optional whitespace:
		while (ch != document_source.end() and isspace(*ch))
			++ch;
		if (unlikely(ch == document_source.end())) // Gone too far?
			return;

		// Determine which string delimiter we're dealing with (single- or double-quote):
		const char delimiter(*ch);
		if (unlikely(delimiter != '\'' and delimiter != '"'))
			return; // Garbage!
			++ch;
		if (unlikely(ch == document_source.end())) // Gone too far?
			return;

		// Extract the URL between the delimiters:
		std::string url_candidate;
		for (/* Empty! */; *ch != delimiter and ch != string_to_look_for.end(); ++ch)
			url_candidate += *ch;
		if (unlikely(ch == document_source.end())) // Gone too far?
			return;
		extracted_urls->push_back(url_candidate);

		next_match = document_source.find(string_to_look_for, ch - document_source.begin());
	}
}


// ExtractSomeJavaScriptLinks -- helper function for ExtractURLs().
//
void ExtractSomeJavaScriptLinks(const std::string &document_source, std::vector<std::string> * const extracted_urls)
{
	extracted_urls->clear();
	ExtractLinksFollowingString(document_source, "location.href", extracted_urls);
	ExtractLinksFollowingString(document_source, "window.open", extracted_urls);
}


} // unnamed namespace


// CanonizeUrlList -- Canonize a list of URLs.
//
void CanonizeUrlList(const std::string &user_agent_string, const unsigned page_cacher_max_fanout,
		     const unsigned download_timeout, const bool ignore_robots_dot_txt,
		     std::list<std::string> * const url_list)
{
	PrecacheURLs(*url_list, user_agent_string, page_cacher_max_fanout, 0 /* overall timeout */, download_timeout,
		     true, ignore_robots_dot_txt);

	// Canonize each URL
	std::list<std::string> raw_list(*url_list);
	url_list->clear();

	for (std::list<std::string>::const_iterator url(raw_list.begin()); url != raw_list.end(); ++ url) {
		Url canonical_url(*url, Url::AUTO_CANONIZE,
				  (ignore_robots_dot_txt ? Url::IGNORE_ROBOTS_DOT_TXT : Url::CONSULT_ROBOTS_DOT_TXT),
				  download_timeout);
		if (canonical_url.isCanonical())
			if (std::find(url_list->begin(), url_list->end(), canonical_url.toString()) == url_list->end())
				url_list->push_back(canonical_url);
	}
}


FileUtil::FileType GuessFileType(const std::string &url)
{
	if (url.empty())
		return FileUtil::FILE_TYPE_UNKNOWN;

	std::string filename(url);

	// Remove a "fragment" if we can find one:
	const std::string::size_type hash_pos = url.find('#');
	if (hash_pos != std::string::npos)
		filename = filename.substr(0, hash_pos);

	const std::string::size_type last_slash_pos = filename.rfind('/');
	if (last_slash_pos == std::string::npos)
		return FileUtil::FILE_TYPE_UNKNOWN;

	return FileUtil::GuessFileType(filename);
}


std::string GuessMediaType(const std::string &url)
{
	FileUtil::FileType file_type = GuessFileType(url);

	switch (file_type) {
	case FileUtil::FILE_TYPE_UNKNOWN:
		return "";
	case FileUtil::FILE_TYPE_TEXT:
		return "text";
	case FileUtil::FILE_TYPE_HTML:
		return "text/html";
	case FileUtil::FILE_TYPE_PDF:
		return "application/pdf";
	case FileUtil::FILE_TYPE_PS:
		return "application/postscript";
	case FileUtil::FILE_TYPE_DOC:
		return "application/msword";
	case FileUtil::FILE_TYPE_RTF:
		return "application/rtf";
	case FileUtil::FILE_TYPE_CODE:
		return "text/plain";
	case FileUtil::FILE_TYPE_GRAPHIC:
		return "image";
	case FileUtil::FILE_TYPE_AUDIO:
		return "audio";
	case FileUtil::FILE_TYPE_MOVIE:
		return "video";
	default:
		return "";
	}

	return "";
}


// RemoveRedundantUrlsFromList -- Remove redundant entries froom a list of URLs
//
unsigned RemoveRedundantUrlsFromList(std::list<std::string> * const url_list)
{
	// Sort the list and get rid of exact duplicates:
	url_list->sort();
	url_list->unique();

	// Very short lists contaiin no redundancy:
	if (url_list->empty() or url_list->size() == 1)
		return url_list->size();

	// Get rid of URLs that differ only very slightly:
	std::list<std::string>::iterator current(url_list->begin());

	while (current != url_list->end()) {
		// Advance the current iterator, and set up the
		// "prev" iterator so it points to the previous entry:
		std::list<std::string>::iterator prev(current);
		++current;

		// Make sure we don;t urun over the end:
		if (current != url_list->end()) {

			// Id the current URL redundant?
			if (*current == *prev + "/"
			    or *current == *prev + "index.html"
			    or *current == *prev + "/index.html")
			{
				url_list->erase(current);
				current = prev;
			}
		}
	}

	return url_list->size();
}


namespace {


class PageDigestParser: public HtmlParser {
	std::string base_url_;
	const std::string::size_type max_text_length_;
	std::string &result_;
	bool found_a_frameset_;
	std::set<std::string> frameset_urls_;
public:
	PageDigestParser(const std::string &input_string, const std::string &base_url,
			 const std::string::size_type max_text_length, std::string * const result)
		: HtmlParser(input_string, HtmlParser::WORD | HtmlParser::OPENING_TAG), base_url_(base_url),
		  max_text_length_(max_text_length), result_(*result), found_a_frameset_(false) { }
	virtual void notify(const Chunk &chunk);
	bool foundAFrameSet() const { return found_a_frameset_; }
	const std::set<std::string> &getFrameSetUrls() const { return frameset_urls_; }
};


void PageDigestParser::notify(const Chunk &chunk)
{
	if (chunk.type_ == HtmlParser::WORD) {
		if (result_.length() < max_text_length_)
			result_ += chunk.text_;
	}
	else if (chunk.type_ == HtmlParser::OPENING_TAG) {
		if (chunk.text_ == "base") {
			AttributeMap::const_iterator attrib;
			if ((attrib = chunk.attribute_map_->find("href")) != chunk.attribute_map_->end())
				base_url_ = Url(attrib->second, base_url_, Url::AUTO_MAKE_ABSOLUTE | Url::AUTO_CANONIZE);
		}
		else if (chunk.text_ == "frame") {
			AttributeMap::const_iterator i;
			if ((i = chunk.attribute_map_->find("src")) != chunk.attribute_map_->end()) {
				Url url(i->second, base_url_);
				if (url.isValid())
					frameset_urls_.insert(url);
			}
		}
		else if (chunk.text_ == "frameset")
			found_a_frameset_ = true;
	}
}


} // unnamed namespace


std::string GeneratePageDigest2(const std::string &html_doc, const std::string &html_doc_url, const GeneratePageDigestOptions options,
				const Downloader::Params &downloader_params, const unsigned overall_timeout)
{
	// The maximum and minimum required size of the text to be digested:
	const std::string::size_type MIN_STRIPPED_TEXT_LENGTH   (100);  // approx. 20 words
	const std::string::size_type TARGET_STRIPPED_TEXT_LENGTH(1000); // approx. 200 words

	// Read the "words only" from the document:
	std::string words_only;
	PageDigestParser page_digest_parser(html_doc, html_doc_url, TARGET_STRIPPED_TEXT_LENGTH, &words_only);
	page_digest_parser.parse();

	// If we enconutered a frameset, and we haven't found enough
	// text, and we are allowed to download frames, then do so:
	if (page_digest_parser.foundAFrameSet() and words_only.length() < MIN_STRIPPED_TEXT_LENGTH
	    and options == DOWNLOAD_FRAMESETS)
	{
		Downloader downloader(downloader_params);
		WallClockTimer wall_clock_timer(WallClockTimer::CUMULATIVE_WITH_AUTO_STOP);
		wall_clock_timer.start();
		const std::set<std::string> &frame_set_urls(page_digest_parser.getFrameSetUrls());
		for (std::set<std::string>::const_iterator frame_set_url(frame_set_urls.begin());
		     words_only.length() < TARGET_STRIPPED_TEXT_LENGTH and frame_set_url != frame_set_urls.end(); ++frame_set_url)
		{
			wall_clock_timer.stop();
			if (wall_clock_timer.getTimeInMilliseconds() > overall_timeout)
				break;
			wall_clock_timer.start();

			downloader.newUrl(*frame_set_url);
			if (downloader.anErrorOccurred())
				continue;

			const std::string page_content(downloader.getMessageBody());
			if (page_content.empty())
				continue;

			PageDigestParser page_digest_parser2(page_content, *frame_set_url, TARGET_STRIPPED_TEXT_LENGTH, &words_only);
			page_digest_parser2.parse();
		}
	}

	return words_only.length() > MIN_STRIPPED_TEXT_LENGTH ? StringUtil::Sha1(words_only) : "";
}


namespace { // FrameAnalysis helper function


// InternalFrameAnalysis -- A version of FrameAnalysis that wont be broken by infinitely recursive frames.
//
bool InternalFrameAnalysis(const std::string &url, const std::string &html, const Downloader::Params &downloader_params,
			   const unsigned depth, std::list<std::string> * const frame_urls)
{
	// If we recur 5 levels deep, we assume a malicious Web page, and bail out:
	if (depth >= 5)
		return false;

	// If the page is not a frameset, we can return without recursion:
	if (not HtmlUtil::FrameAnalysis(url, html, frame_urls))
		return false;

	// This page is a frameset, so we need to check whether it's contents are also framests:
	Downloader downloader(downloader_params);
	std::list<std::string> frames(*frame_urls);
	frame_urls->clear();

	for (std::list<std::string>::iterator new_url(frames.begin()); new_url != frames.end(); ++new_url) {
		// Download the frame:
		std::string new_html;
		downloader.newUrl(*new_url);
		if (downloader.anErrorOccurred())
			continue;

		const std::string media_type(MediaTypeUtil::GetMediaType(downloader.getMessageHeader(), downloader.getMessageBody()));
		if (media_type == "text/html" or media_type == "text/xhtml")
			new_html = downloader.getMessageBody();
		if (new_html.empty())
			continue;

		// Get frame URLs fom the frame:
		std::list<std::string> interior_urls;
		if (InternalFrameAnalysis(*new_url, new_html, downloader_params, depth + 1, &interior_urls)) {
			// New page is a frameset: Add the interior frames:
			frame_urls->insert(frame_urls->end(), interior_urls.begin(), interior_urls.end());
		}
		else {
			// New page is not a frameset, so add it:
			frame_urls->push_back(*new_url);
		}
	}

	return true;
}


} // unnamed namespace


// FrameAnalysis -- Analyse an HTML page and recusively find its frames (if any).
//
bool FrameAnalysis(const std::string &url, const std::string &html, const Downloader::Params &downloader_params,
		   std::list<std::string> * const frame_urls)
{
	return InternalFrameAnalysis(url, html, downloader_params, 1, frame_urls);
}


// IsValidUrlChar -- Is a character valid within a URL.  This list is canonical.  Don't add to it!!
//
bool IsValidUrlChar(const char ch)
{
	if ((ch >= 'a' and ch <= 'z') or (ch >= 'A' and ch <= 'Z'))
		return true;

	if (ch >= '0' and ch <= '9')
		return true;

	switch (ch) {
	case '~':
	case '+':
	case '-':
	case '=':
	case '.':
	case '_':
	case '/':
	case '*':
	case '(':
	case ')':
	case ',':
	case '@':
	case '\'':
	case '$':
	case ':':
	case ';':
	case '&':
	case '%':
	case '!':
	case '?':
	case '#':
		return true;
	default:
		return false;
	}
}


std::string WwwFormUrlEncode(const StringMap &post_args, const bool generate_content_type_and_content_length_headers)
{
	std::string name_value_pairs;
	for (StringMap::const_iterator name_value_pair(post_args.begin());
	     name_value_pair != post_args.end(); ++name_value_pair)
	{
		if (name_value_pair != post_args.begin())
			name_value_pairs += '&';

		std::string name(name_value_pair->first);
		UrlUtil::UrlEncode(&name);
		std::string value(name_value_pair->second);
		UrlUtil::UrlEncode(&value);

		name_value_pairs += name;
		name_value_pairs += '=';
		name_value_pairs += value;
	}

	std::string form_data;
	if (generate_content_type_and_content_length_headers) {
		form_data = "Content-Type: application/x-www-form-urlencoded\r\n";
		form_data += "Content-Length: ";
		form_data += StringUtil::ToString(name_value_pairs.length());
		form_data += "\r\n\r\n";
	}

	form_data += name_value_pairs;
	return form_data;
}


std::string GetUserAgentString(const std::string &user_agent_contact_url, const std::string &auxillary_identifier)
{
	utsname utsname_buf;
	if (::uname(&utsname_buf) == -1)
		throw Exception("in WebUtil::GetUserAgentString: uname(2) failed ("
					 + MsgUtil::ErrnoToString() + ")!");
	std::string user_agent_string(MsgUtil::GetProgName());
	if (not auxillary_identifier.empty())
		user_agent_string += " " + auxillary_identifier;
	user_agent_string += " (";
	user_agent_string += utsname_buf.nodename;
	user_agent_string += "; ";
	user_agent_string += utsname_buf.sysname;
	user_agent_string += ' ';
	user_agent_string += utsname_buf.machine;
	user_agent_string += "; ";
	user_agent_string += user_agent_contact_url;
	user_agent_string += ")";

	return user_agent_string;
}


namespace {


/** Helper for FollowFullTextLinksInPage. Returns true if CachedPageFetcher successfully downloaded the page and it's not empty. */
template <typename PageDownloadObject>
bool FollowUrl(const std::string &link, const std::string &original_url, const TimeLimit &time_limit, Url * const new_url, PageDownloadObject * const cpf)
{
	*new_url = Url(link, original_url, Url::AUTO_MAKE_ABSOLUTE);
	if (new_url->isAbsolute() and new_url->isValid() and new_url->isValidWebUrl()) {
		cpf->newUrl(*new_url, time_limit);
		if (not cpf->anErrorOccurred())
			return not cpf->getMessageBody().empty();
	}
	return false;
}


template <typename PageDownloadObject>
bool DoFollowFullTextLinksInPage(const std::string &page_content, const std::string &original_url, const FollowBehavior &follow_behavior,
				 const TimeLimit &time_limit, Url * const new_url, PageDownloadObject * const page_fetcher)
{
	// Extract all the URLs from the primary page:
	std::vector<UrlAndAnchorTexts> extracted_urls;
	WebUtil::ExtractURLs(page_content, original_url, WebUtil::RAW_URLS, &extracted_urls, WebUtil::IGNORE_DUPLICATE_URLS);

	unsigned pdf_link_count(0), full_text_anchor_link_count(0), full_text_url_count(0);
	std::string pdf_link, full_text_anchor_link, full_text_url_link;

	PerlCompatRegExp pdf_url_pattern(".*\\.pdf$", PerlCompatRegExp::OPTIMIZE_FOR_MULTIPLE_USE);
	PerlCompatRegExp full_text_anchor_pattern("(?i:full\\s?text)", PerlCompatRegExp::OPTIMIZE_FOR_MULTIPLE_USE);
	PerlCompatRegExp full_text_url_pattern(".*(?i:full\\s?text).*", PerlCompatRegExp::OPTIMIZE_FOR_MULTIPLE_USE);
	PerlCompatRegExp viewcontent_url_pattern(".*(?i:viewcontent).*", PerlCompatRegExp::OPTIMIZE_FOR_MULTIPLE_USE);
	// Iterate through every URL looking for a single PDF document link and "full text" anchor text:
	for (std::vector<UrlAndAnchorTexts>::const_iterator url_and_anchor_texts(extracted_urls.begin());
	     url_and_anchor_texts != extracted_urls.end(); ++url_and_anchor_texts)
	{
		if (pdf_url_pattern.match(url_and_anchor_texts->getUrl())) {
			++pdf_link_count;
			pdf_link = url_and_anchor_texts->getUrl();
		}

		for (UrlAndAnchorTexts::const_iterator anchor_text(url_and_anchor_texts->begin()); anchor_text != url_and_anchor_texts->end();
		     ++anchor_text)
		{
			if (full_text_anchor_pattern.match(*anchor_text)) {
				++full_text_anchor_link_count;
				full_text_anchor_link = url_and_anchor_texts->getUrl();
				break;
			}
		}

		if (full_text_url_pattern.match(url_and_anchor_texts->getUrl()) or viewcontent_url_pattern.match(url_and_anchor_texts->getUrl())) {
			++full_text_url_count;
			full_text_url_link = url_and_anchor_texts->getUrl();
		}
	}

	// If we found a single PDF document we try to download it and make sure it is a valid PDF:
	if (pdf_link_count == 1 and (follow_behavior == FOLLOW_SINGLE_PDF_LINKS or follow_behavior == FOLLOW_ALL))
		return FollowUrl(pdf_link, original_url, time_limit, new_url, page_fetcher);

	// If we find anchor text that matches (?i:full\\s?text) then we follow that page if the link is good:
	if (full_text_anchor_link_count == 1 and (follow_behavior == FOLLOW_FULL_TEXT_ANCHORS or follow_behavior == FOLLOW_ALL))
		return FollowUrl(full_text_anchor_link, original_url, time_limit, new_url, page_fetcher);

	// If we find a single URL that has fulltext in it we follow that page if the link is good:
	if (full_text_url_count == 1 and (follow_behavior == FOLLOW_FULL_TEXT_LINKS or follow_behavior == FOLLOW_ALL))
		return FollowUrl(full_text_url_link, original_url, time_limit, new_url, page_fetcher);

	return false;
}


} // unamed namespace


bool FollowFullTextLinksInPage(const std::string &page_content, const std::string &original_url, const FollowBehavior &follow_behavior,
			       const TimeLimit &time_limit, Url * const new_url, Downloader * const downloader)
{
	return DoFollowFullTextLinksInPage(page_content, original_url, follow_behavior, time_limit, new_url, downloader);
}


namespace {


const int BAD_MONTH(-1);


/** \brief Converts a three letter month string to a 0-based month index.
 *  \param month The three letter month name.
 */
int MonthToInt(const std::string &const_month)
{
	std::string month(const_month);
	StringUtil::ToLower(&month);

	if (month == "jan")
		return 0;
	if (month == "feb")
		return 1;
	if (month == "mar")
		return 2;
	if (month == "apr")
		return 3;
	if (month == "may")
		return 4;
	if (month == "jun")
		return 5;
	if (month == "jul")
		return 6;
	if (month == "aug")
		return 7;
	if (month == "sep")
		return 8;
	if (month == "oct")
		return 9;
	if (month == "nov")
		return 10;
	if (month == "dec")
		return 11;

	return BAD_MONTH;
}


} // unnamed namespace


time_t ParseWebDateAndTime(const std::string &possible_web_date_and_time)
{
	if (::strcasecmp("now", possible_web_date_and_time.c_str()) == 0)
		return std::time(NULL);

	int month, day, hour, min, sec, year;

	size_t comma_pos = possible_web_date_and_time.find(',');
	if (comma_pos == std::string::npos) {
		// We should have the following format: "Mon Aug  6 19:01:42 1999":
		if (possible_web_date_and_time.length() < 24)
			return TimeUtil::BAD_TIME_T;
		if ((month = MonthToInt(possible_web_date_and_time.substr(4, 3))) == BAD_MONTH)
			return TimeUtil::BAD_TIME_T;
		if (std::sscanf(possible_web_date_and_time.substr(8).c_str(), "%d %2d:%2d:%2d %4d", &day, &hour, &min, &sec, &year) != 5)
			return TimeUtil::BAD_TIME_T;
	}
	else if (comma_pos == 3) {
		// We should have the following formats: "Mon, 06 Aug 1999 19:01:42" or "Mon, 06-Aug-1999 19:01:42 GMT" or "Mon, 06-Aug-99 19:01:42 GMT":
		if (possible_web_date_and_time.length() < 20)
			return TimeUtil::BAD_TIME_T;
		if (std::sscanf(possible_web_date_and_time.substr(5, 2).c_str(), "%2d", &day) != 1)
			return TimeUtil::BAD_TIME_T;
		if ((month = MonthToInt(possible_web_date_and_time.substr(8, 3))) == BAD_MONTH)
			return TimeUtil::BAD_TIME_T;
		if (std::sscanf(possible_web_date_and_time.substr(12).c_str(), "%4d %2d:%2d:%2d", &year, &hour, &min, &sec) != 4)
			return TimeUtil::BAD_TIME_T;

		// Normalise "year" to include the century:
		if (year > 90 and year < 100)
			year += 1900;
		else if (year <= 90)
			year += 2000;
	}
	else {
		// We should have the following format: "Monday, 06-Aug-99 19:01:42":
		if (possible_web_date_and_time.length() < comma_pos + 20)
			return TimeUtil::BAD_TIME_T;
		if (std::sscanf(possible_web_date_and_time.substr(comma_pos+2, 2).c_str(), "%2d", &day) != 1)
			return TimeUtil::BAD_TIME_T;
		if ((month = MonthToInt(possible_web_date_and_time.substr(comma_pos + 5, 3))) == BAD_MONTH)
			return TimeUtil::BAD_TIME_T;
		if (std::sscanf(possible_web_date_and_time.substr(comma_pos+9).c_str(), "%d %2d:%2d:%2d", &year, &hour, &min, &sec) != 4)
			return TimeUtil::BAD_TIME_T;

		// Normalise "year" to include the century:
		if (year > 90)
			year += 1900;
		else
			year += 2000;
	}

	struct tm time_struct;
	std::memset(&time_struct, '\0', sizeof time_struct);
	time_struct.tm_year  = year - 1900;
	time_struct.tm_mon   = month;
	time_struct.tm_mday  = day;
	time_struct.tm_hour  = hour;
	time_struct.tm_min   = min;
	time_struct.tm_sec   = sec;
	time_struct.tm_isdst = -1; // Don't change this!

	time_t retval = std::mktime(&time_struct);
	if (retval == TimeUtil::BAD_TIME_T)
		return TimeUtil::BAD_TIME_T;

	return retval;
}


std::string ConvertToLatin9(const HttpHeader &http_header, const std::string &original_document)
{
	std::string character_encoding;

	// Try to get the encoding from the HTTP header...
	character_encoding = http_header.getCharset();

	// ...if not available from the header, let's try to get it from the HTML:
	if (character_encoding.empty() and (http_header.getMediaType() == "text/html" or http_header.getMediaType() == "text/xhtml")) {
		std::list< std::pair<std::string, std::string> > extracted_data;
		HttpEquivExtractor http_equiv_extractor(original_document, "Content-Type", &extracted_data);
		http_equiv_extractor.parse();
		if (not extracted_data.empty())
			character_encoding = HttpHeader::GetCharsetFromContentType(extracted_data.front().second);
	}

	StringUtil::ToLower(&character_encoding);

	// If we can't find any character encoding information or we already have Latin-9 we just give up in disgust and return the original document:
	if (character_encoding.empty() or character_encoding == "latin-9" or character_encoding == "latin9" or character_encoding == "iso-8859-15"
	    or character_encoding == "latin-1" or character_encoding == "latin1" or character_encoding == "iso-8859-1" or character_encoding == "ascii")
		return original_document;

	// Now see if we're dealing with UTF-8:
	if (character_encoding == "utf-8" or character_encoding == "utf8")
		return StringUtil::UTF8ToISO8859_15(original_document);

	// If we got here we have an encoding that we don't know what to do with and for now just give up and return the original document:
	return original_document;
}


// ExtractURLs -- extracts all links from "document_source" and returns them in "urls".  "root_url" is used to turn relative URLs into absolute URLs if
//                requested.
//
void ExtractURLs(const std::string &/*document_source*/, const std::string &/*default_base_url*/,
		 const ExtractedUrlForm /*extracted_url_form*/, std::vector<UrlAndAnchorTexts> * const /*urls_and_anchor_texts*/,
		 const unsigned /*flags*/, const std::string &/*user_agent_string*/, const unsigned /*page_cacher_max_fanout*/,
		 const unsigned /*individual_page_timeout*/, unsigned long * const /*overall_timeout*/)
{
	throw std::runtime_error("WebUtil::ExtractURLs() has not been implemented!");
}


} // namespace WebUtil
