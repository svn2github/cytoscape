/** \file    FileUtil.cc
 *  \brief   Implementation of file utility functions.
 *  \author  Dr. Johannes Ruscheinski
 *  \author  Artur Kedzierski
 *  \author  Dr. Gordon W. Paynter
 *  \author  Jiangtao Hu
 *  \author  Jason Scheirer
 */

/*
 *  Copyright 2002-2008 Project iVia.
 *  Copyright 2002-2008 The Regents of The University of California.
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

#include <FileUtil.h>
#include <list>
#include <vector>
#include <cctype>
#include <cerrno>
#include <cstdio>
#include <fcntl.h>
#include <glob.h>
#include <unistd.h>
#include <glob.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <pwd.h>
#include <sys/types.h>
#include <Directory.h>
#include <File.h>
#include <MsgUtil.h>
#include <ProcessUtil.h>
#include <SocketUtil.h>
#include <StringUtil.h>
#include <Compiler.h>


#ifdef __MACH__
#      define O_LARGEFILE	0
#      define ZIP		"/usr/bin/zip"
#      define DIFF		"/usr/bin/diff"
#endif


namespace FileUtil {


bool CompressedLineReader::eof() const
{
	if (eof_)
		return true;

	if (not buffer_.empty())
		return false;

	char uncompressed_data[1024];
	int uncompressed_data_size = ::gzread(gz_file_, uncompressed_data, sizeof uncompressed_data);
	switch (uncompressed_data_size) {
	case -1:
		throw Exception("in CompressedLineReader::eof: unexpected read error from compressed input stream \""
				+ filename_ + "\"!");
	case 0:
		eof_ = true;
		return true;
	default:
		buffer_ += std::string(uncompressed_data, uncompressed_data_size);
		return false;
	}
}


std::string CompressedLineReader::getNextLine()
{
	if (gz_file_ == NULL)
		throw Exception("in CompressedLineReader::getNextLine: can't read from a non-open input stream!");

	std::string next_line;
	if (eof_ and buffer_.empty())
		return next_line;

	std::string::size_type next_newline_pos = buffer_.find('\n');
	if (next_newline_pos != std::string::npos) {
		next_line = buffer_.substr(0, next_newline_pos);
		buffer_ = buffer_.substr(next_newline_pos + 1);
		return next_line;
	}

	char uncompressed_data[1024];
	int uncompressed_data_size;
	next_newline_pos = 0;
	while ((uncompressed_data_size = ::gzread(gz_file_, uncompressed_data, sizeof uncompressed_data)) != 0) {
		if (uncompressed_data_size == -1)
			throw Exception("in CompressedLineReader::getNextLine: unexpected read error from "
					"compressed input stream \"" + filename_ + "\"!");

		buffer_ += std::string(uncompressed_data, uncompressed_data_size);

		// Scan newly decompressed chunk for a newline:
		const void *ptr_to_newline = std::memchr(uncompressed_data, '\n', uncompressed_data_size);
		if (ptr_to_newline == NULL) { // No luck so far!
			next_newline_pos += uncompressed_data_size;
			continue;
		}

		// If we get here, the last decompressed chunk contained a newline and we're ready to return the
		// next line:
		next_newline_pos += reinterpret_cast<const char *>(ptr_to_newline) - uncompressed_data;
		next_line = buffer_.substr(0, next_newline_pos);
		buffer_ = buffer_.substr(next_newline_pos + 1);
		return next_line;
	}

	eof_ = uncompressed_data_size == 0;

	// If we make it here we have reached EOF and found no newline so we just return all remaining characters:
	next_line = buffer_;
	buffer_.clear();

	return next_line;
}


// UniqueFileName -- get a unique filename in the data directory
//
std::string UniqueFileName(const std::string &directory, const std::string &filename_prefix,
			   const std::string &filename_suffix)
{
	static unsigned generation_number(1);

	// Set default for prefix if necessary.
	std::string prefix(filename_prefix);
	if (prefix.empty())
		prefix = MsgUtil::GetProgName();

	std::string suffix(filename_suffix);
	if (not suffix.empty() and suffix[0] != '.')
		suffix = "." + suffix;

	std::string dir(directory);
	if (dir.empty())
		dir = "/tmp";

        return (dir + "/" + prefix + "." +
		StringUtil::ToString(getpid()) + "." +
		StringUtil::ToString(generation_number++) + suffix);
}


// ReadFile -- read "filename" into "*buffer".
//
bool ReadFile(const std::string &filename, std::string * const buffer)
{
	File input(filename, "r");
	if (unlikely(input.fail()))
		return false;

	ReadFile(input, buffer);

	return true;
}


std::string ReadFile(const std::string &filename)
{
	std::string file_contents;
	if (unlikely(not ReadFile(filename, &file_contents)))
		throw Exception("in FileUtil::ReadFile: could not read contents of \"" + filename + "\"!");

	return file_contents;
}


void ReadFile(std::istream &input, std::string * const buffer)
{
	buffer->clear();
	while (not input.eof()) {
		char buf[BUFSIZ];
		input.read(buf, sizeof buf);
		if (input.gcount() > 0)
			buffer->append(buf, input.gcount());
		else if (unlikely(input.bad()))
			throw Exception("in FileUtil::ReadFile: read failed!");
	}
}


void ReadFile(File &input, std::string * const buffer)
{
	buffer->clear();
	while (not input.eof()) {
		char buf[BUFSIZ];
		const size_t byte_count(input.read(buf, sizeof buf));
		if (byte_count != sizeof(buf) and unlikely(input.anErrorOccurred()))
			throw Exception("in FileUtil::ReadFile: I/O error while trying to read \"" + input.getPath()
					+ "\"!");
		buffer->append(buf, byte_count);
	}
}


// WriteFile -- write "size" bytes from "*buffer" into "filename".
//
bool WriteFile(const std::string &filename, const char * const buffer, const unsigned long size, const bool append)
{
	// We won't write out empty files.
	if (buffer == NULL or size == 0)
		return false;

	// Open the output file
	FILE *stream = std::fopen(filename.c_str(), append ? "a" : "w");
	if (stream == NULL) {
		MsgUtil::SysWarning("In FileUtil::WriteFile: failed to open \"%s\" for writing", filename.c_str());
		return false;
	}

	if (std::fwrite(buffer, 1, size, stream) != size) {
		std::fclose(stream);
		MsgUtil::SysWarning("In FileUtil::WriteFile: failed to write %ld bytes to \"%s\"", size,
				    filename.c_str());
		return false;
	}

	// close the file and return
	if (std::fclose(stream) != 0) {
		MsgUtil::SysWarning("In FileUtil::WriteFile: fclose(3) failed");
		return false;
	}

	return true;
}


void CopyFile(const std::string &from_filename, const std::string &to_filename)
{
	const int from_fd(::open(from_filename.c_str(), O_RDONLY | O_LARGEFILE));
	if (from_fd == -1)
		throw Exception("in CopyFile: can't open \"" + from_filename + "\" for reading (1)!");

	const int to_fd(::open(to_filename.c_str(), O_WRONLY | O_CREAT | O_TRUNC | O_LARGEFILE, 0600));
	if (to_fd == -1) {
		::close(from_fd);
		throw Exception("in CopyFile: can't open \"" + to_filename + "\" for writing!");
	}

	char buf[BUFSIZ];
	ssize_t ret_val;
	do {
		ret_val = ::read(from_fd, buf, sizeof buf);
		if (ret_val == -1) {
			::close(from_fd);
			::close(to_fd);
			throw Exception("in CopyFile: read(2) failed while trying to read from \""
					+ from_filename + "\" (" + MsgUtil::ErrnoToString() + ")!");
		}

		if (::write(to_fd, buf, ret_val) == -1) {
			::close(from_fd);
			::close(to_fd);
			throw Exception("in CopyFile: write(2) failed while trying to write to \""
						 + to_filename + "\" (" + MsgUtil::ErrnoToString() + ")!");
		}
	} while (ret_val == sizeof(buf));

	::close(from_fd);
	::close(to_fd);
}


// CopyFile -- copy content of "from_filename" to stream "to_file".
//
void CopyFile(const std::string &from_filename, std::ostream &to_file)
{
	const int from_fd(::open(from_filename.c_str(), O_RDONLY | O_LARGEFILE));
	if (from_fd == -1)
		throw Exception("in FileUtil::CopyFile: can't open \"" + from_filename + "\" for reading (1)!");

	char buf[BUFSIZ];
	ssize_t ret_val;
	do {
		if ((ret_val = ::read(from_fd, buf, sizeof buf)) == -1) {
			::close(from_fd);
			throw Exception("in FileUtil::CopyFile: read(2) failed while trying to read from \""
					+ from_filename + "\" (" + MsgUtil::ErrnoToString() + ")!");
		}

		if (to_file.write(buf, ret_val) == NULL) {
			::close(from_fd);
			throw Exception("in FileUtil::CopyFile: write(2) failed while trying to write ("
					+ MsgUtil::ErrnoToString() + ")!");
		}
	} while (ret_val == sizeof(buf));

	::close(from_fd);
}


bool DeleteFile(const std::string &path)
{
	return ::unlink(path.c_str()) == 0;
}


bool DeleteFiles(const std::string &pattern, const bool recursive)
{
	std::vector<std::string> list_of_matching_files;
	bool retcode(Glob(pattern, &list_of_matching_files));
	bool no_failure(true);

	for (std::vector<std::string>::const_iterator filename(list_of_matching_files.begin()); filename != list_of_matching_files.end();
	     ++filename)
	{
		// Are we dealing with a directory?
		if (IsDirectory(*filename)) {
			if (not DeleteDirectory(*filename, recursive))
				no_failure = false;
		}
		// So it's a file?
		else if (not DeleteFile(*filename))
			no_failure = false;
	}

	if (retcode != 0)
		return false;
	else
		return no_failure;
}


bool Glob(const std::string &pattern, std::vector<std::string> *list_to_append_to)
{
	glob_t glob;
	const int retcode(::glob(pattern.c_str(), GLOB_ERR | GLOB_MARK | GLOB_BRACE | GLOB_TILDE, NULL, &glob));

	for (size_t path_no(0); path_no < glob.gl_pathc; ++path_no) {
		std::string filename(glob.gl_pathv[path_no]);
		list_to_append_to->push_back(filename);
	}

	::globfree(&glob);

	if (retcode == 0)
		return true;
	else
		return false;
}


bool RenameFile(const std::string &old_path, const std::string &new_path)
{
	return 0 == ::rename(old_path.c_str(), new_path.c_str());
}


namespace {


bool DeleteDirectoryHelper(const std::string &path, const bool is_directory)
{
	if (is_directory)
		return ::rmdir(path.c_str()) == 0;
	else
		return ::unlink(path.c_str()) == 0;
}


} // unnamed namespace


bool DeleteDirectory(const std::string &directory, const bool recursive)
{
	errno = 0;

	if (recursive)
		return DepthFirstVisit(directory, DeleteDirectoryHelper);
	else
		return ::rmdir(directory.c_str()) == 0;
}


// SkipWhiteSpace -- skips over "whitespace" and returns the number of newlines
//                   that were skipped.
//
unsigned SkipWhiteSpace(std::istream &stream)
{
	unsigned newline_count = 0;

	int ch = stream.get();
	while (isspace(ch)) {
		if (ch == '\n')
			++newline_count;
		ch = stream.get();
	}

	if (ch != EOF)
		stream.unget();

	return newline_count;
}


// IsDirectory -- Is the specified file a directory?
//
bool IsDirectory(const std::string &dir_name)
{
	struct stat statbuf;
	if (::stat(dir_name.c_str(), &statbuf) != 0)
		return false;

	return S_ISDIR(statbuf.st_mode);
}


// MakeDirectory -- Create a directory.
//
bool MakeDirectory(const std::string &path, const bool recursive, const mode_t mode)
{
	bool absolute(path[0] == '/' ? true : false);
	// In NON-recursive mode we make a single attempt to create the directory:
	if (not recursive) {
		errno = 0;
		if (::mkdir(path.c_str(), mode) == 0)
			return true;
		return errno == EEXIST and IsDirectory(path);
	}

	std::vector<std::string> path_components;
	StringUtil::Split(path, '/', &path_components);

	std::string path_so_far;
	if (absolute)
		path_so_far += "/";
	for (std::vector<std::string>::const_iterator path_component(path_components.begin());
	     path_component != path_components.end(); ++path_component)
	{
		path_so_far += *path_component;
		path_so_far += '/';
		errno = 0;
		if (::mkdir(path_so_far.c_str(), mode) == -1 and errno != EEXIST)
			return false;
		if (errno == EEXIST and not IsDirectory(path_so_far))
			return false;
	}

	return true;
}


// ShellEscape -- returns a double-quotes string with escaped backticks, dollar signs,
//                double-quotes and backslashes.
//
std::string ShellEscape(const std::string &s)
{
	std::string escaped_string;
	for (std::string::const_iterator ch(s.begin()); ch != s.end(); ++ch) {
		switch (*ch) {
		case '`':
		case '$':
		case '"':
		case '\\':
			escaped_string += '\\';
			/* fall through is intentional! */
		default:
			escaped_string += *ch;
		}
	}

	return std::string("\"") + escaped_string + std::string("\"");
}


// Size -- Get the size of a file.
//
off_t Size(const char * const path) throw(std::exception)
{
	struct stat stat_buf;
	if (::stat(path, &stat_buf) == -1)
		throw Exception("in FileUtil::Size: can't stat(2) \"" + std::string(path) + "\" ("
				+ MsgUtil::ErrnoToString() + ")!");

	return stat_buf.st_size;
}


// LastModified -- Get the date at which a file was last modified.
//
time_t LastModified(const std::string &path)
{
	struct stat stat_buf;
	if (::stat(path.c_str(), &stat_buf) == -1)
		throw Exception("in FileUtil::LastModified: can't stat(2) \"" + path + "\" ("
				+ MsgUtil::ErrnoToString() + ")!");

	return stat_buf.st_mtime;
}


// DirnameAndBasename -- Split a path into a directory name part and filename part.
//
void DirnameAndBasename(const std::string &path, std::string * const dirname, std::string * const basename)
{
	if (unlikely(path.length() == 0)) {
		*dirname = *basename = "";
		return;
	}

	std::string::size_type last_slash_pos = path.rfind('/');
	if (last_slash_pos == std::string::npos) {
		*dirname  = "";
		*basename = path;
	}
	else {
		*dirname  = path.substr(0, last_slash_pos);
		*basename = path.substr(last_slash_pos + 1);
	}
}


// Filename -- Return the filename portion of a path.
//
std::string ExtractFilename(const std::string &full_path)
{
	std::string::size_type last_slash_pos = full_path.rfind('/');
	if (last_slash_pos == std::string::npos)
		return full_path;

	return full_path.substr(last_slash_pos + 1);

}


std::string Dirname(const std::string &path)
{
	std::string dirname, basename;
	DirnameAndBasename(path, &dirname, &basename);
	return dirname;
}


// AccessErrnoToString -- Converts an errno set by access(2) to a string.
//                        The string values were copied and pasted from a Linux man page.
//
std::string AccessErrnoToString(int errno_to_convert, const std::string &pathname,
				const std::string &mode)
{
	switch (errno_to_convert) {
	case 0: // Just in case...
		return "OK";
	case EACCES:
		return "The requested access would be denied to the file or search"
		       " permission is denied to one of the directories in '" + pathname + "'";
	case EROFS:
		return "Write  permission  was  requested  for  a  file  on  a read-only filesystem.";
	case EFAULT:
		return "'" + pathname + "' points outside your accessible address space.";
	case EINVAL:
		return mode + " was incorrectly specified.";
	case ENAMETOOLONG:
		return "'" + pathname + "' is too long.";
	case ENOENT:
		return "A directory component in '" + pathname + "' would have been accessible but"
			" does not exist or was a dangling symbolic link.";
	case ENOTDIR:
		return "A component used as a directory in '" + pathname + "' is not, in fact, a directory.";
	case ENOMEM:
		return "Insufficient kernel memory was available.";
	case ELOOP:
		return "Too many symbolic links were encountered in resolving '" + pathname + "'.";
	case EIO:
		return "An I/O error occurred.";
	}

	throw Exception("Unknown errno code in FileUtil::AccessErrnoToString");
}


// Exists -- test whether a file exists
//
bool Exists(const std::string &path, std::string * const error_message)
{
	errno = 0;
	int access_status = ::access(path.c_str(), F_OK);
	if (error_message != NULL)
		*error_message = AccessErrnoToString(errno, path, "F_OK");

	return (access_status == 0);
}


bool IsReadable(const std::string &path)
{
	return (::access(path.c_str(), R_OK) == 0);
}


namespace {


void MakeCanonicalPathList(const char * const path, std::list<std::string> * const canonical_path_list)
{
	canonical_path_list->clear();

	const char *cp = path;
	if (*cp == '/') {
		canonical_path_list->push_back("/");
		++cp;
	}
	while (*cp != '\0') {
		std::string directory;
		while (*cp != '\0' and *cp != '/')
			directory += *cp++;
		if (*cp == '/')
			++cp;

		if (directory.empty() or directory == ".")
			continue;

		if (directory == ".." and not canonical_path_list->empty()) {
			if (canonical_path_list->size() != 1 or canonical_path_list->front() != "/")
				canonical_path_list->pop_back();
		}
		else
			canonical_path_list->push_back(directory);
	}
}


} // unnamed namespace


std::string CanonisePath(const std::string &path)
{
	std::list<std::string> canonical_path_list;
	MakeCanonicalPathList(path.c_str(), &canonical_path_list);

	std::string canonised_path;
	for (std::list<std::string>::const_iterator path_component(canonical_path_list.begin());
	     path_component != canonical_path_list.end(); ++path_component)
	{
		if (not canonised_path.empty() and canonised_path != "/")
			canonised_path += '/';
		canonised_path += *path_component;
	}

	return canonised_path;
}


std::string MakeAbsolutePath(const std::string &reference_path, const std::string &relative_path)
{
	MSG_UTIL_ASSERT(not reference_path.empty() and reference_path[0] == '/');

	if (relative_path[0] == '/')
		return relative_path;

	std::string reference_dirname, reference_basename;
	DirnameAndBasename(reference_path, &reference_dirname, &reference_basename);

	std::list<std::string> resultant_dirname_components;
	MakeCanonicalPathList(reference_dirname.c_str(), &resultant_dirname_components);

	std::string relative_dirname, relative_basename;
	DirnameAndBasename(relative_path, &relative_dirname, &relative_basename);
	std::list<std::string> relative_dirname_components;
	MakeCanonicalPathList(relative_dirname.c_str(), &relative_dirname_components);

	// Now merge the two canonical path lists.
	for (std::list<std::string>::const_iterator component(relative_dirname_components.begin());
	     component != relative_dirname_components.end(); ++component)
	{
		if (*component == ".." and (resultant_dirname_components.size() > 1 or
					    resultant_dirname_components.front() != "/"))
			resultant_dirname_components.pop_back();
		else
			resultant_dirname_components.push_back(*component);
	}

	// Build the final path:
	std::string canonized_path;
	std::list<std::string>::const_iterator dir(resultant_dirname_components.begin());
	if (dir != resultant_dirname_components.end() and *dir == "/") {
		canonized_path = "/";
		++dir;
	}
	for (/* empty */; dir != resultant_dirname_components.end(); ++dir)
		canonized_path += *dir + "/";
	canonized_path += relative_basename;

	return canonized_path;
}


std::string MakeAbsolutePath(const std::string &relative_path)
{
	char buf[PATH_MAX];
	const char * const current_working_dir(::getcwd(buf, sizeof buf));
	if (unlikely(current_working_dir == NULL))
		throw Exception("in FileUtil::MakeAbsolutePath: getcwd(3) failed (" + MsgUtil::ErrnoToString() + ")!");
	return MakeAbsolutePath(std::string(current_working_dir) + "/", relative_path);
}


std::string MakeRelativePath(const std::string &reference_path, const std::string &path)
{
	if (reference_path.empty() or reference_path[0] != '/')
		throw Exception("in FileUtil::MakeRelativePath: the reference path must be non-empty and "
					 "absolute!");
	if (path.empty() or path[0] != '/')
		throw Exception("in FileUtil::MakeRelativePath: the path must be non-empty and absolute!");

	std::list<std::string> reference_path_components;
	StringUtil::Split(reference_path, "/", &reference_path_components);
	if (reference_path[reference_path.length() - 1] != '/')
		reference_path_components.pop_back();

	std::list<std::string> path_components;
	StringUtil::Split(path, "/", &path_components);
	std::string filename_component;
	if (not path_components.empty() and path[path.length() - 1] != '/') {
		filename_component = path_components.back();
		path_components.pop_back();
	}

	std::list<std::string>::const_iterator reference_path_iter(reference_path_components.begin());
	for (/* Empty! */; reference_path_iter != reference_path_components.end() and not path_components.empty();
	     ++reference_path_iter)
	{
		if (*reference_path_iter != path_components.front())
			break;
		path_components.pop_front();
	}

	std::string relative_path;
	for (/* Empty! */; reference_path_iter != reference_path_components.end(); ++reference_path_iter)
		relative_path += "../";

	while (not path_components.empty()) {
		relative_path += path_components.front() + "/";
		path_components.pop_front();
	}

	relative_path += filename_component;

	return relative_path.empty() ? "." : relative_path;
}


std::string TildeExpand(const std::string &path)
{
	if (path.empty() or path[0] != '~')
		return path;

	const char *username = ::getenv("USER");
	if (username == NULL)
		throw Exception("in FileUtil::TildeExpand: no environment setting for \"USER\"");

	struct passwd *pwd = ::getpwnam(username);
	if (pwd == NULL) {
		std::string msg("can't get password entry for \"");
		msg += username;
		msg += '"';
		throw Exception(msg);
	}

	return pwd->pw_dir + path.substr(1);
}


bool GetLine(std::istream &stream, std::string * const line, const char terminator)
{
	const std::string::size_type INITIAL_CAPACITY = 128;
	line->clear();
	line->reserve(INITIAL_CAPACITY);

	int ch;
	for (ch = stream.get(); ch != EOF and ch != terminator; ch = stream.get()) {
		if (line->size() == line->capacity())
			line->reserve(2 * line->capacity());
		line->push_back(static_cast<char>(ch));
	}

	return ch != EOF;
}


size_t GetLine(FILE *file, char *line, size_t max_length, const char * const terminators) throw()
{
	if (unlikely(max_length == 0))
		return 0;

	--max_length; // compensate for adding '\0' character

	int current_char;
	size_t chars_read = 0;
	bool has_terminator_been_found(false);

	while (chars_read < max_length and not has_terminator_been_found and (current_char = ::getc(file)) != EOF) {
		unsigned i = 0;
		while (i != std::strlen(terminators) and not has_terminator_been_found) {
			if (current_char == terminators[i])
				has_terminator_been_found = true;
			++i;
		}

		line[chars_read] = static_cast<char>(current_char);
		++chars_read;
	}

	// Terminate the line.
	line[chars_read] = '\0';

	// Return the number of characters read.
	return chars_read;
}


bool DepthFirstVisit(const std::string &root_directory, bool (*callback)(const std::string &path, const bool is_directory), const PermType perm_type,
		     const mode_t file_mode, const mode_t directory_mode)
{
	std::list<std::string> non_directory_filenames;
	Directory directory(root_directory);
	for (Directory::const_iterator entry(directory.begin()); entry != directory.end(); ++entry) {
		if (entry->getFileOrDirectoryName() == "." or entry->getFileOrDirectoryName() == "..")
			continue;

		const std::string path(root_directory + "/" + entry->getFileOrDirectoryName());
#ifdef __MACH__
		struct stat stat_buf;
		if (::stat(path.c_str(), &stat_buf) != 0)
#else
		struct stat64 stat_buf;
		if (::lstat64(path.c_str(), &stat_buf) != 0)
#endif
			return false;
		if (S_ISDIR(stat_buf.st_mode)) {
			switch (perm_type) {
			case PERM_IGNORE:
				break;
			case PERM_ANY:
				if ((stat_buf.st_mode & directory_mode) == 0)
					continue;
				break;
			case PERM_ALL:
				if ((stat_buf.st_mode & directory_mode) != directory_mode)
					continue;
				break;
			case PERM_MATCH:
				if (stat_buf.st_mode != directory_mode)
					continue;
				break;
			}

			if (not DepthFirstVisit(path, callback))
				return false;
		}
		else { // We're dealing with a non-directory file.
			switch (perm_type) {
			case PERM_IGNORE:
				break;
			case PERM_ANY:
				if ((stat_buf.st_mode & file_mode) == 0)
					continue;
				break;
			case PERM_ALL:
				if ((stat_buf.st_mode & file_mode) != file_mode)
					continue;
				break;
			case PERM_MATCH:
				if (stat_buf.st_mode != file_mode)
					continue;
				break;
			}

			non_directory_filenames.push_back(path);
		}
	}

	for (std::list<std::string>::const_iterator non_directory_filename =
		     non_directory_filenames.begin();
	     non_directory_filename != non_directory_filenames.end(); ++non_directory_filename)
		if (not (*callback)(*non_directory_filename, false))
			return false;

	return (*callback)(root_directory, true);
}


bool PollForFile(const std::string &path_pattern, const unsigned poll_interval,
		 const unsigned timeout)
{
	unsigned slept_so_far(0);
	for (;;) {
		glob_t a_glob;
		int glob_error_code;
		switch (glob_error_code = ::glob(path_pattern.c_str(),
						 GLOB_ERR | GLOB_NOSORT | GLOB_BRACE | GLOB_TILDE
						 #ifdef GLOB_NOESCAPE
						 	 | GLOB_NOESCAPE
						 #endif
						 , NULL, &a_glob))
		{
		case 0: {
			const bool found_at_least_one_match = a_glob.gl_pathc > 0;
			::globfree(&a_glob);
			if (found_at_least_one_match)
				return true;
			/* try again after sleeping. */
			break;
		}
		case GLOB_NOSPACE:
			::globfree(&a_glob);
			errno = ENOMEM;
			return false;
		#ifdef GLOB_ABORTED
		case GLOB_ABORTED:
			::globfree(&a_glob);
			errno = EPERM;
			return false;
		#endif // ifdef GLOB_ABORTED
		#ifdef GLOB_NOMATCH
		case GLOB_NOMATCH:
			/* try again after sleeping. */
			::globfree(&a_glob);
			break;
		#endif // ifdef GLOB_NOMATCH
		default:
			MsgUtil::Error("in FileUtil::PollForFile: unexpected error return %d from glob(3)!", glob_error_code);
		}

		::sleep(poll_interval);
		++slept_so_far;
		if (timeout != 0 and slept_so_far >= timeout) {
			errno = ETIME;
			return false;
		}
	}
}


bool IsSaneFilename(const std::string &filename)
{
	// Allow upper- and lowercase English letters, digits, hyphens and underscores:
	for (std::string::const_iterator ch(filename.begin()); ch != filename.end(); ++ch)
		if (not StringUtil::IsAlphanumeric(*ch) and *ch != '_' and *ch != '-')
			return false;

	return true;
}


FileUtil::FileType GuessFileType(const std::string &filename)
{
	if (filename.empty())
		return FILE_TYPE_UNKNOWN;

	// Cannot guess a mime type without an extension:
	const std::string::size_type extension_pos = filename.rfind('.');
	if (extension_pos == std::string::npos)
		return FILE_TYPE_UNKNOWN;

	std::string file_extension = filename.substr(extension_pos + 1);
	StringUtil::ToLower(&file_extension);
	if (file_extension.find("htm") != std::string::npos) // .phtml, .shtml, .html
		return FILE_TYPE_HTML;

	FileUtil::FileType file_type = FILE_TYPE_UNKNOWN;
	switch (file_extension[0]) {
	case 'c':
		if (file_extension == "c" or file_extension == "cc" or file_extension == "cpp"
		    or file_extension == "cxx")
			file_type = FILE_TYPE_CODE;
		else if (file_extension == "cgi")
			file_type = FILE_TYPE_HTML;
		break;
	case 'd':
		if (file_extension == "dvi")
			file_type = FILE_TYPE_DVI;
		else if (file_extension == "divx")
			file_type = FILE_TYPE_MOVIE;
		else if (file_extension == "doc")
			file_type = FILE_TYPE_DOC;
		break;
	case 'e':
		if (file_extension == "eps")
			file_type = FILE_TYPE_PS;
		break;
	case 'g':
		if (file_extension == "gif")
			file_type = FILE_TYPE_GRAPHIC;
		else if (file_extension == "gz")
			file_type = FILE_TYPE_GZIP;
		break;
	case 'h':
		if (file_extension == "h")
			file_type = FILE_TYPE_CODE;
		break;
	case 'j':
		if (file_extension == "jpg")
			file_type = FILE_TYPE_GRAPHIC;
		break;
	case 'p':
		switch (file_extension[1]) {
		case 'd':
			if (file_extension == "pdf")
				file_type = FILE_TYPE_PDF;
			break;
		case 'h':
			if (file_extension == "phtml") // serverside parsed html
				file_type = FILE_TYPE_HTML;
			else if (file_extension == "php") //
				file_type = FILE_TYPE_HTML;
			break;
		case 'l':
			if (file_extension == "pl")
				file_type = FILE_TYPE_HTML; // it might be a source code too!
		case 'n':
			if (file_extension == "png")
				file_type = FILE_TYPE_GRAPHIC;
			break;
		case 'p':
			if (file_extension == "ppt")
				file_type = FILE_TYPE_SLIDES;
			break;
		case 's':
			if (file_extension == "ps")
				file_type = FILE_TYPE_PS;
			break;
		case 'y':
			if (file_extension == "py")
				file_type = FILE_TYPE_HTML; // it might be a source code too!
			break;
		}
		break;
	case 'r':
		if (file_extension == "rtf")
			file_type = FILE_TYPE_RTF;
		break;
	case 's':
		if (file_extension == "sxi")
			file_type = FILE_TYPE_SLIDES;
		else if (file_extension == "sxw")
			file_type = FILE_TYPE_DOC;
		break;
	case 't':
		switch (file_extension[1]) {
		case 'a':
			if (file_extension == "tar")
				file_type = FILE_TYPE_TAR;
			break;
		case 'e':
			if (file_extension == "tex")
				file_type = FILE_TYPE_TEX;
			break;
		case 'g':
			if (file_extension == "tgz")
				file_type = FILE_TYPE_GZIP;
			break;
		case 'x':
			if (file_extension == "txt")
				file_type = FILE_TYPE_TEXT;
			break;
		}
		break;
	case 'x':
		if (file_extension == "xhtml") // serverside parsed html.
			file_type = FILE_TYPE_HTML;
		break;
	}

	return file_type;
}


std::string FileTypeToString(const FileType file_type)
{
	switch (file_type) {
	case FILE_TYPE_UNKNOWN:
		return "unknown";
	case FILE_TYPE_TEXT:
		return "text";
	case FILE_TYPE_HTML:
		return "html";
	case FILE_TYPE_PDF:
		return "pdf";
	case FILE_TYPE_PS:
		return "ps";
	case FILE_TYPE_DOC:
		return "doc";
	case FILE_TYPE_SLIDES:
		return "slides";
	case FILE_TYPE_TEX:
		return "tex";
	case FILE_TYPE_DVI:
		return "dvi";
	case FILE_TYPE_TAR:
		return "tar";
	case FILE_TYPE_RTF:
		return "rtf";
	case FILE_TYPE_GZIP:
		return "gzip";
	case FILE_TYPE_Z:
		return "z";
	case FILE_TYPE_CODE:
		return "code";
	case FILE_TYPE_GRAPHIC:
		return "graphics";
	case FILE_TYPE_AUDIO:
		return "audio";
	case FILE_TYPE_MOVIE:
		return "movie";
	default:
	        throw Exception("in FileUtil::FileTypeToString: Unknown file type!");
	}
}


bool SetNonblocking(const int fd)
{
	// First, retrieve current settings:
	int flags = ::fcntl(fd, F_GETFL, 0);
	if (flags == -1)
		return false;

	flags |= O_NONBLOCK;

	return ::fcntl(fd, F_SETFL, flags) != -1;
}


bool SetBlocking(const int fd)
{
	// First, retrieve current settings:
	int flags = ::fcntl(fd, F_GETFL, 0);
	if (flags == -1)
		return false;

	flags &= ~O_NONBLOCK;

	return ::fcntl(fd, F_SETFL, flags) != -1;
}


namespace {


// CopyDirHelper -- Helper for CopyDirectory().
//
void CopyDirHelper(const std::string &source_path, const std::string &target_path, const bool recursive)
{
	Directory directory(source_path);

	for (Directory::const_iterator entry(directory.begin()); entry != directory.end(); ++entry) {
		if (entry->getFileOrDirectoryName() == "." or entry->getFileOrDirectoryName() == "..")
			continue;

		const std::string extended_source_path(source_path + "/" + entry->getFileOrDirectoryName());
		const std::string extended_target_path(target_path + "/" + entry->getFileOrDirectoryName());

		if (IsDirectory(extended_source_path)) {
			if (not recursive)
				continue;

			if (not MakeDirectory(extended_target_path))
				throw Exception("in CopyDirHelper: FileUtil::MakeDirectory(" + extended_target_path
							 + ") failed!");
			CopyDirHelper(extended_source_path, extended_target_path, recursive);
		}
		else
			CopyFile(extended_source_path, extended_target_path);
	}
}


} // unnamed namespace


void CopyDirectory(const std::string &src, const std::string &target, const bool recursive)
{
	if (not IsDirectory(src))
		throw Exception("in CopyDirectory: source directory \"" + src + "\" doesn't exist!");

	if (Exists(target) and not IsDirectory(target))
		throw Exception("in CopyDirectory: target \"" + target + "\" exists and is not a directory!");

	if (not MakeDirectory(target, /* recursive = */ true))
		throw Exception("in CopyDirectory: can't create target directory \"" + target + "\"!");

	CopyDirHelper(src, target, recursive);
}


void ZipFiles(const std::string &archive_filename, const std::list<std::string> &files_to_archive, const bool delete_archived_files,
	      const bool strip_paths)
{
	// Make sure zip won't fail because the output file already exists:
	::unlink(archive_filename.c_str());

	const pid_t pid(::fork());
	if (pid == -1)
		throw Exception("in FileUtil::ZipFiles: fork(2) failed! (Out of memory?)");

	if (pid == 0) { // We're the child!
		const char *args[3 + files_to_archive.size() + 1];
		unsigned arg_index = 0;
		args[arg_index++] = "zip";
		args[arg_index++] = "-q";
		args[arg_index++] = "-r";
		if (strip_paths)
			args[arg_index++] = "-j";
		args[arg_index++] = const_cast<char *>(archive_filename.c_str());
		for (std::list<std::string>::const_iterator file_to_archive(files_to_archive.begin()); file_to_archive != files_to_archive.end();
		     ++file_to_archive)
			args[arg_index++] = const_cast<char *>(file_to_archive->c_str());
		args[arg_index] = NULL;
		::execv(ZIP, const_cast<char * const *>(args));
		::_exit(EXIT_FAILURE); // We should never get here!
	}
	else { // We're the parent.
		int status;
		::waitpid(pid, &status, 0);

		// Did the child process fail?
		if (not WIFEXITED(status) or WEXITSTATUS(status) != EXIT_SUCCESS)
			throw Exception("in FileUtil::ZipFiles: child process failed while trying to zip \""
						 + archive_filename + "\"!");

		// Cleanup request?
		if (delete_archived_files) {
			for (std::list<std::string>::const_iterator file_to_archive(files_to_archive.begin());
			     file_to_archive != files_to_archive.end(); ++file_to_archive)
			{
				if (::unlink(file_to_archive->c_str()) != 0)
					throw Exception("in FileUtil::ZipFiles: can't delete \"" + *file_to_archive + "\"!");
			}
		}
	}
}


void ZipFile(const std::string &archive_filename, const std::string &file_to_archive, const bool delete_archived_file, const bool strip_paths)
{
	std::list<std::string> files_to_archive;
	files_to_archive.push_back(file_to_archive);
	ZipFiles(archive_filename, files_to_archive, delete_archived_file, strip_paths);
}


unsigned GetOpenFileDescriptorCount()
{
	return static_cast<unsigned>(Directory("/proc/" + StringUtil::ToString(::getpid()) + "/fd").size())
	       - 1 /* Apparently this expression in itself uses a file descriptor internally! */;
}


bool DescriptorIsReadyForReading(const int fd, const TimeLimit &time_limit)
{
	return SocketUtil::TimedRead(fd, time_limit, reinterpret_cast<void *>(NULL), 0) == 0;
}


bool DescriptorIsReadyForWriting(const int fd, const TimeLimit &time_limit)
{
	return SocketUtil::TimedWrite(fd, time_limit, reinterpret_cast<void *>(NULL), 0) == 0;
}


bool Touch(const std::string &path, const bool create_if_not_exists, const int creation_mode)
{
	if (::access(path.c_str(), F_OK) != 0) {
		if (create_if_not_exists) {
			if (unlikely(::creat(path.c_str(), creation_mode) == -1))
				throw Exception("in FileUtil::Touch: creat(2) failed (" + MsgUtil::ErrnoToString() + ")!");
			return true;
		}
		else
			return false;
	}
	else if (unlikely(::utimes(path.c_str(), NULL) == -1))
		return false;

	return true;
}


bool IsReadOnly(const int fd)
{
	int flags;
	if (unlikely((flags = ::fcntl(fd, F_GETFL, &flags)) == -1))
		throw Exception("in FileUtil::IsReadOnly: fcntl(2) failed (" + MsgUtil::ErrnoToString() + ")!");
	return (flags & O_ACCMODE) == O_RDONLY;
}


bool Rewind(const int fd)
{
	return ::lseek(fd, 0, SEEK_SET) == 0;
}


std::string GetHomeDirectory()
{
	const char * const home_env(::getenv("HOME"));
	if (unlikely(home_env == NULL))
		throw Exception("in FileUtil::GetHomeDirectory: $HOME has not been set!");
	return home_env;
}


bool FilesAreIdentical(const std::string &filename_expected, const std::string &filename_actual, std::string * const diff)
{
	if (diff != NULL)
		diff->clear();

	std::list<std::string> cmdline_args;
	cmdline_args.push_back(filename_expected);
	cmdline_args.push_back(filename_actual);
	const std::string diff_filename(UniqueFileName("/tmp", "diff"));
	try {
		const int retcode(ProcessUtil::Execute(DIFF, cmdline_args, "/dev/null", diff_filename, diff_filename,
						       ProcessUtil::APPEND_STDERR | ProcessUtil::APPEND_STDOUT));
		if (retcode != 0 and diff != NULL)
			ReadFile(diff_filename, diff);

		::unlink(diff_filename.c_str());
		return retcode == 0;
	}
	catch (const std::exception &x) {
		::unlink(diff_filename.c_str());
		MsgUtil::Error("in FileUtil::FilesAreIdentical: \"%s\" failed: %s", DIFF, x.what());
		return false; // Keep the compiler happy!
	}
}


bool StripLastPathComponent(const std::string &path, std::string * const stripped_path)
{
	*stripped_path = path;

	// Remove all trailing slashes:
	while (not stripped_path->empty() and (*stripped_path)[stripped_path->size() - 1] == '/')
		stripped_path->resize(stripped_path->size() - 1);

	if (stripped_path->empty())
		return false;

	// Now remove the trailing path component:
	while (not stripped_path->empty() and (*stripped_path)[stripped_path->size() - 1] != '/')
		stripped_path->resize(stripped_path->size() - 1);

	return not stripped_path->empty();
}


/** \brief Creates a symbolic link in the file system. */
void CreateSymbolicLink(const std::string &real_file_name, const std::string &link_file_name)
{
	if (::symlink(real_file_name.c_str(), link_file_name.c_str()) == -1)
		throw Exception(StringUtil::Format("Attempting to link real file %s to link file %s failed: ", real_file_name.c_str(),
						   link_file_name.c_str()) + ErrorInfo());
}


std::string GetCurrentDirectory()
{
	char * const current_dir(::getcwd(NULL, 0));
	if (unlikely(current_dir == NULL))
		throw Exception("In FileUtil::GetCurrentDirectory: can't retrieve current directory! " + MsgUtil::ErrnoToString() + ")!");

	std::string retval(current_dir);
	std::free(current_dir);
	if (retval[retval.size() - 1] != '/')
		retval += '/';

	return retval;
}


bool CreateFIFO(const std::string &path, const mode_t mode)
{
	errno = 0;

	// Create the directory component of "path", if necessary:
	std::string dirname, basename;
	DirnameAndBasename(path, &dirname, &basename);
	if (not dirname.empty() and not MakeDirectory(dirname, /* recursive = */ true, mode | S_IRUSR | S_IWUSR | S_IXUSR))
		return false;

	return ::mkfifo(path.c_str(), mode) == 0;
}


} // namespace FileUtil
