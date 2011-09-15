/** \file    WebUtil.h
 *  \brief   WWW related utility functions.
 *  \author  Dr. Johannes Ruscheinski
 *  \author  Artur Kedzierski
 *  \author  Dr. Gordon W. Paynter
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

#ifndef WEB_UTIL_H
#define WEB_UTIL_H


#include <iostream>
#include <list>
#include <map>
#include <string>
#include <stdint.h>
#include <sys/types.h>
#include <Downloader.h>
#include <FileUtil.h>
#include <StringMap.h>
#include <TimeLimit.h>
#include <Url.h>


// forward declaration(s):
class HttpHeader;
class RobotsDotTxt;
class ValueListMap;


namespace WebUtil {


/** The default timeout (in milliseconds) for WebUtil functions that perform Internet operations. */
const unsigned DEFAULT_DOWNLOAD_TIMEOUT(20000);


/** \brief  Prevent Web browsers from timing out by sending HTML comments from a child process.
 */
class BrowserKeepAlive {
	pid_t child_pid_;
	bool cancelled_;
	int fd_;
public:
	/** \brief  Construct a properly initialised BrowserKeepAlive object.
	 *  \param  fd              An open TCP connection to a Web browser or STDOUT_FILENO.
	 *  \param  pulse_interval  Roughly (lower bound) the interval at which to send keepalive HTML comments (in
	 *                          seconds).
	 */
	explicit BrowserKeepAlive(const int fd = STDOUT_FILENO, const unsigned pulse_interval = 20);
	virtual ~BrowserKeepAlive() { if (not cancelled_) cancel(); }
	void cancel();
private:
	BrowserKeepAlive(const BrowserKeepAlive &rhs);                  // Intentionally unimplemented!
	const BrowserKeepAlive &operator=(const BrowserKeepAlive &rhs); // Intentionally unimplemented!
};


ptrdiff_t Unescape(char * const dest, const char * const src);


/** \brief  Parse all arguments from HTTP POST (via std::cin) into a ValueListMap. */
void GetPostArgs(ValueListMap * const post_args);


/** \brief  Parse all arguments from HTTP GET (via std::cin) into a ValueListMap. */
void GetGetArgs(ValueListMap * const get_args);


/** \brief  Parse all arguments from the command line into a ValueListMap. */
void GetArgvArgs(const int argc, char * argv[], ValueListMap * const argv_args);


/** \brief  Parse all multipart arguments (via std::cin) into a ValueListMap. */
void GetMultiPartArgs(ValueListMap * const post_args, const bool save_file_to_disk = true);


/** \brief  Obtains all arguments from CGI (submitted in GET or POST methods or provided on the command line).
 *  \param  cgi_args  The map that holds that variable -> value relation upon exit.
 *  \param  argc      The argument count as provided to the main function.
 *  \param  argv      The argument list as provided to the main function.
 *  \note   If "argc" and "argv" are set to their default values only HTTP GET and POST arguments will be extracted!
*/
void GetAllCgiArgs(ValueListMap * const cgi_args, int argc = 1, char *argv[] = NULL);


const unsigned MAX_MULIT_PART_FORM_RANDOM_NUMBER_LENGTH = 200;


/** \brief Parses a multipart/form-data header.
 *  \param field_name     The name of the file in the header field.
 *  \param file_name      If a file has been trasmitted, this variable will
 *                        contain the name of the file.
 *
 *  Checks for the correct occurence of "field_name" in the "name"
 *  header field.  If "random_number" is empty it stores the random
 *  number delimiter in it, otherwise it checks for a match.
 */
void ParseMultiPartFormDataHeader(std::string * const field_name, std::string * const file_name = NULL) throw(std::exception);


/** \brief Read an entire multipart/form-data file into an output stream.
 *  \param  random_number  The random number file delimiter.
 *  \param  output         The stream to write the file to.
 *  \return true           if end of form has been reached.
 *
 *  Read the entire content of the file into a file of the given name.
 */
bool ReadMultiPartFormData(const char * const random_number, std::ostream &output = std::cout);


/** \brief  Obtains a file from multi-part form and saves it under a unique name.
 *  \return The name of the file that contains the data.
 */
std::string GetFileViaHttpForm();


/** \brief  Provides support for setting and modifying HTTP cookies.
 *
 *  Usage:
 *  1) To set/modify a cookie in a client's browser:
 *     WebUtil::Cookies cookies;
 *     cookies.insert("Testing", "1");
 *     cookies.printHeader();
 *
 *     or
 *
 *     WebUtil::Cookies cookies("Testing", "1");
 *     cookies.printHeader();
 *
 *   2) To remove a previously set cookie from a browser:
 *      WebUtil::Cookies cookies;
 *      cookies.insert("Testing", "", 0);
 *      cookies.printHeader();
 *
 *   3) To obtain a cookie from a browser:
 *      std::map<std::string> cookies;
 *	WebUtil::GetCookies(&cookies);
 *      const std::string value = cookies["Testing"];
 */
class Cookies {
	struct Cookie {
		std::string name_, value_;
		unsigned max_age_;
		std::string domain_, path_;
		time_t expires_;
	public:
		Cookie(const std::string &name, const std::string &value, const unsigned max_age, const std::string &domain, const std::string &path, const time_t expires)
			: name_(name), value_(value), max_age_(max_age), domain_(domain), path_(path), expires_(expires) { }
	};
	std::map<std::string, Cookie> cookies_;
public:
	/** Creates an empty collection of cookies. */
	Cookies() { }

	/** \brief  Creates a new collection of cookies initialised with one initial cookie.
	 *  \param  name     The name of the cookie.  Must not start with a $-sign.
	 *  \param  value    The value of the cookie.
	 *  \param  max_age  The lifetime of the cookie in seconds.
	 *  \param  domain   The Domain attribute specifies the domain for which the cookie is valid.  An explicitly
	 *                   specified domain must always start with a dot.
	 *  \param  path     Optional.  The Path attribute specifies the subset of URLs to which this cookie applies.
	 */
	explicit Cookies(const std::string &name, const std::string &value = "", const unsigned max_age = 3600 * 24 * 365,
			 const std::string &domain = "", const std::string &path = "");

	size_t size() const { return cookies_.size(); }
	bool empty() const { return cookies_.empty(); }

	/** \brief  Inserts a new cookie into our collection of cookies.
	 *  \param  name     The name of the cookie.  Must not start with a $-sign.
	 *  \param  value    The value of the cookie.
	 *  \param  max_age  The lifetime of the cookie in seconds.
	 *  \param  domain   The Domain attribute specifies the domain for which the cookie is valid.  An explicitly
	 *                   specified domain must always start with a dot.
	 *  \param  path     Optional.  The Path attribute specifies the subset of URLs to which this cookie applies.
	 *  \param  expires  The cookie expiration time.  If set to 0, the current time plus "max_age" will be used.
	 */
	void insert(const std::string &name, const std::string &value = "", const uint64_t max_age = 3600 * 24 * 365, const std::string &domain = "",
		    const std::string &path = "", const time_t expires = 0);

	/** \brief  Prints out the cookie header.
	 *  \note   This function MUST be invoked before printing
	 *          "Content-type: text/html\r\n\r\n"
	 */
	void printHeader() const;
};


/** \brief  Obtains all the previously set HTTP cookies from the browser
 *  \param  cookies  Here we'll store the (cookie_name, cookie_value) pairs upon return.
 */
void GetCookies(std::map<std::string, std::string> * const cookies);


/** \brief  Split an HTML message into Header and body parts.
 *  \param  message  The combined header and body.
 *  \param  header   String to store the header in.
 *  \param  body     String to store the body in.
 *
 *  Assumes that the message does include both a header and body.
 */
void HtmlMessageToHeaderAndBody(const std::string &message, std::string * const header, std::string * const body);


/** \brief  Excutes a CGI script via POST.
 *  \param  username_password    A colon-separated username/password pair.  Currently we only support "Basic"
 *                               authorization!
 *  \param  address              The IP address or domain name.
 *  \param  port                 The TCP port number (typically 80).
 *  \param  time_limit           Up to how long to wait for the Web server to respond (in milliseconds).
 *  \param  cgi_path             The path to the CGI script, e.g. "/cgi-bin/canned_search".
 *  \param  post_args            A list of name/value pairs.
 *  \param  document_source      The output of the CGI script.
 *  \param  error_message        If an error occurs, a description of the error will be stored here..
 *  \param  accept               List of comman separated media types which are acceptable for the response.
 *  \param  include_http_header  Prepend the HTTP header to the document source if this is "true".
 *  \return True if no error occurred, otherwise false.
 */
bool ExecCGI(const std::string &username_password, const std::string &address, const unsigned short port, const TimeLimit &time_limit,
	     const std::string &cgi_path, const StringMap &post_args, std::string * const document_source, std::string * const error_message,
	     const std::string &accept = "text/html,text/xhtml,text/plain,www/source", const bool include_http_header = false);


/** \brief  Excutes a CGI script via POST.
 *  \param  address              The IP address or domain name.
 *  \param  port                 The TCP port number (typically 80).
 *  \param  time_limit           Up to how long to wait for the Web server to respond (in milliseconds).
 *  \param  cgi_path             The path to the CGI script, e.g. "/cgi-bin/canned_search".
 *  \param  post_args            A list of name/value pairs.
 *  \param  document_source      The output of the CGI script.
 *  \param  error_message        If an error occurs, a description of the error will be stored here..
 *  \param  accept               List of comman separated media types which are acceptable for the response.
 *  \param  include_http_header  Prepend the HTTP header to the document source if this is "true".
 *  \return True if no error occurred, otherwise false.
 */
inline bool ExecCGI(const std::string &address, const unsigned short port, const TimeLimit &time_limit, const std::string &cgi_path,
		    const StringMap &post_args, std::string * const document_source, std::string * const error_message,
		    const std::string &accept = "text/html,text/xhtml,text/plain,www/source", const bool include_http_header = false)
{
	return ExecCGI("", address, port, time_limit, cgi_path, post_args, document_source, error_message, accept,
		       include_http_header);
}


/** \brief  Return the user's IP Address.
 *  \note   Returns the Apache REMOTE_ADDR value, or an empty string if it s unknown.
 */
std::string UserIpAddress();


/** \brief  Return a description of the user's web browser.
 *  \note   We want a single-word description that lets us identify different browsers.
 */
std::string UserBrowserDescription();


/** \brief  Classifies a Web document as probably in English or not.
 *  \note   You should pass the associated \"http_header\" if it is available because it improves the accuracy of the classification provided by this
 *          function.
 *  \return True if \"html_document\", or optionally \"http_header\" indicate an English document, else false.
 */
bool IsProbablyEnglish(const std::string &html_document, const HttpHeader * const http_header = NULL);


/** \brief   Identify the "top" site that this page is part of
 *  \param   url  The URL whose site we want.
 *  \return  A string identifying the "major site" relevant to this URL.
 *
 *  This function takes a URL and returns an identifier for its "major" site; that is based on the topmost registered domain in its path.  For example,
 *  "http://www.somewhere.com" would return "somewhere.com" and "http://news.fred.co.nz" would return "fred.co.nz".
 */
std::string GetMajorSite(const Url &url);


/** \brief   Draw a set of URLs into the Page Cache.
 *  \param   url_list                 The list of URLs.
 *  \param   user_agent_string        The user agent string to use when downloading.
 *  \param   page_cacher_max_fanout   The number of pages to fetch in parallel.
 *  \param   overall_timeout          The total timeout (in milliseconds).
 *  \param   individual_page_timeout  The web page retrieval timeout (in milliseconds) per page.
 *  \param   use_canonize_mode        Pass the "--canonize-mode" flag to iViaCore-page-cacher.
 *  \param   ignore_robots_dot_txt    Advise the iViaCore-page-cacher to ignore "robots.txt" files.
 *  \param   verbosity                Passed to the iViaCore-page-cacher.
 *  \param   log_filename             Passed to the iViaCore-page-cacher.
 *  \return  True if the iViaCore-page-cacher indicated success, false otherwise.
 */
bool PrecacheURLs(const std::list<std::string> &url_list, const std::string &user_agent_string,
		  const unsigned page_cacher_max_fanout, const unsigned overall_timeout,
		  const unsigned individual_page_timeout, const bool use_canonize_mode = false,
		  const bool ignore_robots_dot_txt = false, const unsigned verbosity = 0,
		  const std::string &log_filename = "");


/** \enum   ExtractedUrlForm
 *  \brief  The form of the URLs to be extracted with ExtractURLs
 */
enum ExtractedUrlForm {
	RAW_URLS,        //< The URLs as they appear in the document.
	ABSOLUTE_URLS,   //< The raw URLs converted to absolute form.
	CLEAN_URLS,      //< The absolute URLs "cleaned up".
	CANONIZED_URLS   //< The absolute URLs in Canonical form (will cause all URLs to be pre-cached).
};


/** ExtractURLs flag: Do no report blacklisted URLs. */
const unsigned IGNORE_BLACKLISTED_URLS                  = 1u << 1u;
/** ExtractURLs flag: Do no report any URL more than once. */
const unsigned IGNORE_DUPLICATE_URLS                    = 1u << 2u;
/** ExtractURLs flag: Do no report URLs that are anchored by IMG tags. */
const unsigned IGNORE_LINKS_IN_IMG_TAGS                 = 1u << 3u;
/** ExtractURLs flag: Do no report URLs on the same conceptual site, as reported by Url::getSite(). */
const unsigned IGNORE_LINKS_TO_SAME_SITE                = 1u << 4u;
/** ExtractURLs flag: Do no report URLs on the same conceptual site, as reported by WebUtil::getMajorSite(). */
const unsigned IGNORE_LINKS_TO_SAME_MAJOR_SITE          = 1u << 5u;

/** ExtractURLs flag: Remove page anchors froom URLs (i.e. anything after the final '#' character). */
const unsigned REMOVE_DOCUMENT_RELATIVE_ANCHORS         = 1u << 6u;
/** ExtractURLs flag: Ignore robots.txt files when downloading pages for the purpose of canonization (this is usually very impolite).*/
const unsigned IGNORE_ROBOTS_DOT_TXT                    = 1u << 7u;
/** ExtractURLs flag: Only return URLs whose pages can actually be downloaded (requires CANONIZED_URLS form).*/
const unsigned REQUIRE_URLS_FOR_DOWNLOADABLE_PAGES_ONLY = 1u << 8u;
/** ExtractURLs flag: Clean up the anchor text. */
const unsigned CLEAN_UP_ANCHOR_TEXT                     = 1u << 9u;

/** ExtractURLs flag: Ignore https. */
const unsigned IGNORE_PROTOCOL_HTTPS                    = 1u << 10u;

/** ExtractURLs flag: Only return onsite links. */
const unsigned IGNORE_OFFSITE_LINKS                     = 1u << 11u;
/** ExtractURLs flag: Do our best to get URLs hidden in JavaScript code. */
const unsigned ATTEMPT_TO_EXTRACT_JAVASCRIPT_URLS       = 1u << 12u;

/** The default flags for the ExtractUrls function. */
const unsigned DEFAULT_EXTRACT_URL_FLAGS(IGNORE_DUPLICATE_URLS | IGNORE_LINKS_IN_IMG_TAGS | REMOVE_DOCUMENT_RELATIVE_ANCHORS | CLEAN_UP_ANCHOR_TEXT
					 | IGNORE_PROTOCOL_HTTPS | ATTEMPT_TO_EXTRACT_JAVASCRIPT_URLS);

class UrlAndAnchorTexts {
	std::string url_;
	std::set<std::string> anchor_texts_;
public:
	typedef std::set<std::string>::const_iterator const_iterator;
public:
	explicit UrlAndAnchorTexts(const std::string &url): url_(url) { }
	UrlAndAnchorTexts(const std::string &url, const std::string &anchor_text): url_(url) { anchor_texts_.insert(anchor_text); }
	const std::string &getUrl() const { return url_; }
	void setUrl(const std::string &new_url) { url_ = new_url; }
	void addAnchorText(const std::string &new_anchor_text) { anchor_texts_.insert(new_anchor_text); }
	const std::set<std::string> &getAnchorTexts() const { return anchor_texts_; }
	const_iterator begin() const { return anchor_texts_.begin(); }
	const_iterator end() const { return anchor_texts_.end(); }
};


/** \brief  Extracts all links from an HTML document.
 *  \param  document_source         The string containing the HTMl source.
 *  \param  default_base_url        Used to turn relative URLs into absolute URLs if requested unless a \<base href=...\> tag is found in
 *                                  "document_source".
 *  \param  extracted_url_form      The form of the extracted URLs.
 *  \param  urls                    The list of extracted URL's and their associated anchor text.
 *  \param  flags                   Behaviour modifying flags.
 *  \param  user_agent_string       The user agent string to use when downloading (CANONIZED_URLS only).
 *  \param  page_cacher_max_fanout  The number of pages to fetch in parallel CANONIZED_URLS only).
 *  \param  page_cacher_timeout     The per Web page retrieval timeout (in milliseconds, CANONIZED_URLS only)).
 *  \param  overall_timeout         Don't spend more than this amount of milliseconds time in this routine.  NULL means don't ever time out.
 *
 *  It is very important to pass in the correct base URL.  A common error is to pass in a URL like "http://example.org/about" which would normally be
 *  redirected to "http://example.org/about/" (with trailing slash).  Although both URLs appear to represnt the same page, they are functionally different.
 *  The version with the trailing slash is correct: if you use the version without a trailing slash, then all relative URLs will resolve incorrectly.
 *
 *  \note  If the requested URL form is CLEAN_URLS or CANONIZED_URLS and an extracted URL cannot be converted to this format, the URL will be ignored.
 *
 *  \note  If Canonized URLs are requested, then PrecacheURLs will be called to speed up the canonization process.  If you want PrecacheURLs to ignore
 *         robots.txt, you'll need to set the appropriate bit in "flags".
 */
void ExtractURLs(const std::string &document_source, const std::string &default_base_url, const ExtractedUrlForm extracted_url_form,
		 std::vector<UrlAndAnchorTexts> * const urls_and_anchor_texts, const unsigned flags = DEFAULT_EXTRACT_URL_FLAGS,
		 const std::string &user_agent_string = "", const unsigned page_cacher_max_fanout = 50,
		 const unsigned page_cacher_timeout = DEFAULT_DOWNLOAD_TIMEOUT, unsigned long * const overall_timeout = NULL);


/** \brief  Canonize a list of URL's.
 *  \param  user_agent_string       The user agent string to be used when retrieving Web pages for the
 *                                  purpose of canonizing them.
 *  \param  page_cacher_max_fanout  How much to parallelize the retrieval of pages (used internally to
 *                                  canonize the URL's).
 *  \param  download_timeout        Download timeout limit for internal per page download.
 *  \param  ignore_robots_dot_txt   Whether to ignore "robots.txt" files or not.
 *  \param  url_list                Both input and output!  On input, the list of URL's to be canonized,
 *                                  on output hopefully the list of cannonized URL's.
 */
void CanonizeUrlList(const std::string &user_agent_string, const unsigned page_cacher_max_fanout,
		     const unsigned download_timeout, const bool ignore_robots_dot_txt,
		     std::list<std::string> * const url_list);


/** \brief  Attempt to guess the file type of "url".
 *  \param  url  The URL for the resource whose file type we would like to determine.
 *  \return The guessed file type.
 */
FileUtil::FileType GuessFileType(const std::string &url);


/** \brief   Attempt to guess the media type of an URL.
 *  \param   url  The URL of the resource whose file type we would like to guess.
 *  \return  The guessed media type, or an empty string for none.
 *
 *  The media type (a.k.a. mime type) is defined here:
 *  http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.7
 */
std::string GuessMediaType(const std::string &url);


/** \brief  Given a list of URLs (as strings) remove any that are redundant.
 *  \param  url_list  The list of URLs.
 *  \return The number of URLs remaining in the list.
 */
unsigned RemoveRedundantUrlsFromList(std::list<std::string> * const url_list);


enum GeneratePageDigestOptions { NO_DOWNLOADS, DOWNLOAD_FRAMESETS };


/** \brief  Returns an SHA-1 digest (160 bits) representing a simplified Web page.
 *  \param  html_doc           An HTML document.
 *  \param  html_doc_url       The URL for "html_doc".
 *  \param  options            See the GeneratePageDigestOptions enum for values.
 *  \param  downloader_params  Parameters to be used if we want to download more pages.
 *  \param  overall_timeout    If we download individual pages of a frameset, how long to take at most for all the downloads.
 *  \return A string of length SHA_DIGEST_LENGTH or an empty string if not enough input data was available.
 *  \note   This function tries to base the returned digest on a simplified version of the page.  This facilitates using this function to try to detect
 *          duplicate Web pages.
 */
std::string GeneratePageDigest2(const std::string &html_doc, const std::string &html_doc_url = "", const GeneratePageDigestOptions options = NO_DOWNLOADS,
				const Downloader::Params &downloader_params = Downloader::Params(), const unsigned overall_timeout = UINT_MAX);


/** \brief   Analyse a web page to discover if it is a frameset and recursively fetch its frames.
 *  \param   url                The URL of the HTML document.
 *  \param   html               The HTML document to analyse.
 *  \param   downloader_params  The parameters passed to an instance of class Downloader/
 *  \param   frame_urls  Output parameter for the list of frame URLs (if any).
 *  \return  True if the page is a frameset, otherwise false.
 *
 *  This function analyses an HTML page and grabs the URLs of any frame pages if contains.  The frame URLs will be valid Web URLs, and will be absolute
 *  URLs.
 *
 *  If the top-level page is a frameset, its URLs will be downloaded, and checked to see if they too are framesets.  If so, they will be recusively
 *  analysed until frame URLs are found that are not themselves framesets.  Only non-frameset frames will appear in the result list.
 *
 *  For a non-recursive version of this function, see HtmlUtil::FrameAnalysis.
 */
bool FrameAnalysis(const std::string &url, const std::string &html, const Downloader::Params &downloader_params,
		   std::list<std::string> * const frame_urls);


/** Returns whether a character is valid within a URL or not. */
bool IsValidUrlChar(const char ch);


/** \brief  www-form-urlencodes a list of name/value pairs.
 *  \param  post_args                                         The name/value pairs to be encoded.
 *  \param  generate_content_type_and_content_length_headers  If true generates the "Content-Type" and "Content-Length"
 *                                                            headers.
 */
std::string WwwFormUrlEncode(const StringMap &post_args, const bool generate_content_type_and_content_length_headers = true);


/** \brief  Generates a user agent string consisting of the application name, the hostname for the currently running
 *          program, optional auxillary information in parentheses and a contact URL typically retrieved from a config
 *          file.
 *  \param  user_agent_contact_url  A contact URL that will be added in parentheses at the end of the user agent string.
 *  \param  auxillary_identifier    If provided this identifier will be added in parentheses between the application name
 *          and the contact URL.
 */
std::string GetUserAgentString(const std::string &user_agent_contact_url, const std::string &auxillary_identifier = "");


/** \brief This defines the follow behavior FollowFullTextLinksInPage. FOLLOW_SINGLE_PDF_LINKS means that the
 *  FollowFullTextLinksInPage will look at the URLs extracted from the intial page to see if there is a single PDF link.
 *  If FOLLOW_FULL_TEXT_ANCHORS is specified then FollowFullTextLinksInPage will look at the anchor text for all URLs
 *  extracted from the initial page for anchor text that matches exactly "Full Text".
 *  FOLLOW_ALL and NO_FOLLOW are self explanatory.
 */
enum FollowBehavior { FOLLOW_ALL, FOLLOW_SINGLE_PDF_LINKS, FOLLOW_FULL_TEXT_ANCHORS, FOLLOW_FULL_TEXT_LINKS, NO_FOLLOW };


/** \brief Follows links to "Full Text" web pages and pdfs in page_content.
 *  \param page_content      The body of the page in which to look for a full text link.
 *  \param original_url      The url of the page_content.
 *  \param follow_behavior   Controls if we look for pdf links, anchor text, or link text that says "Full Text".
 *  \param time_limit        Max time limit to wait on downloading followed page.
 *  \param new_url           On ouput, the url for the full text.
 *  \param downloader        On ouput, will have visited the new_url.
 *  \return                  Returns true when a full text link is found.
 */
bool FollowFullTextLinksInPage(const std::string &page_content, const std::string &original_url, const FollowBehavior &follow_behavior,
			       const TimeLimit &time_limit, Url * const new_url, Downloader * const downloader);


/**  \brief  Attempts to convert common Web date/time formats to a time_t.
 *   \param  possible_web_date_and_time  The mess we're trying to understand.
 *   \return If the parse succeeded, the converted time, otherwise TimeUtil::BAD_TIME_T.
 */
time_t ParseWebDateAndTime(const std::string &possible_web_date_and_time);


std::string ConvertToLatin9(const HttpHeader &http_header, const std::string &original_document);


} // namespace WebUtil


#endif // define WEB_UTIL_H
