/** \file    FileUtil.h
 *  \brief   Declaration of file-related utility functions.
 *  \author  Dr. Gordon W. Paynter
 *  \author  Dr. Johannes Ruscheinski
 *  \author  Artur Kedzierski
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

#ifndef FILE_UTIL_H
#define FILE_UTIL_H


#include <fstream>
#include <list>
#include <MsgUtil.h>
#include <stdexcept>
#include <string>
#include <sys/stat.h>
#include <sys/types.h>
#include <TimeLimit.h>
#include <unistd.h>
#include <vector>
#include <zlib.h>


// Forward declaration(s):
class File;


/** \namespace  FileUtil
 *  \brief      File utility functions used in iVia.
 */
namespace FileUtil {


/** \brief Reads and decompresses lines from a gzipped file.
 */
class CompressedLineReader {
	const std::string filename_;
	mutable bool eof_;
	gzFile gz_file_;
	mutable std::string buffer_;
public:
	CompressedLineReader(const std::string &filename)
		: filename_(filename), eof_(false), gz_file_(::gzopen(filename.c_str(), "rb")) { }
	~CompressedLineReader() { if (gz_file_ != NULL) ::gzclose(gz_file_); }
	bool fail() const { return gz_file_ == NULL; }
	bool eof() const;

	/** Returns either the next line or an empty string on EOF. */
	std::string getNextLine();
private:
	CompressedLineReader(const CompressedLineReader &rhs);                  // Intentionally unimplemented!
	const CompressedLineReader &operator=(const CompressedLineReader &rhs); // Intentionally unimplemented!
};


/** \brief   Generate a unique filename
 *  \param   directory        The directory in which to create the temporary file (default: /tmp).
 *                            If directory is passed to be empty, the default is used again.
 *  \param   filename_prefix  Optional prefix for the filename.
 *  \param   filename_suffix  Optional suffix for the filename.
 *  \return  A new, unique file name.
 */
std::string UniqueFileName(const std::string &directory = "/tmp", const std::string &filename_prefix = "", const std::string &filename_suffix = "");


/** \brief  Reads a complete file into a string buffer.
 *  \param  filename  The name of the input file.
 *  \param  buffer    The buffer to read the file contents into.
 *  \return True if "filename" can successfully be opened for reading, else false.
 */
bool ReadFile(const std::string &filename, std::string * const buffer);


/** \brief  Reads a complete file into a string buffer.
 *  \param  filename  The name of the file to read in.
 *  \return           The string now containing the file contents.
 *  \warning          This flavour of ReadFile may be less efficient than the versions that take the address of a string buffer so please only use it if
 *                    you are sure that you only reading small files.
 */
std::string ReadFile(const std::string &filename);


/** \brief  Reads a complete file into a string buffer.
 *  \param  input   The input stream to read from.
 *  \param  buffer  The buffer to read the file contents into.
 */
void ReadFile(std::istream &input, std::string * const buffer);


/** \brief  Reads a complete file into a string buffer.
 *  \param  input   The input File to read from.
 *  \param  buffer  The buffer to read the file contents into.
 */
void ReadFile(File &input, std::string * const buffer);


/** \brief   Write a buffer to a file.
 *  \param   filename  The name of the output file.
 *  \param   buffer    The data to write into the file.
 *  \param   size      How much data starting at "buffer" to write into the file.
 *  \param   append    If true, don't overwrite an existing file but append to it.
 *  \return  True if the file was successfully written, false otherwise.
 */
bool WriteFile(const std::string &filename, const char * const buffer, const unsigned long size, const bool append = false);


/** \brief   Write a buffer to a file.
 *  \param   filename  The name of the output file.
 *  \param   buffer    The data to write into the file.
 *  \param   append    If true, don't overwrite an existing file but append to it.
 *  \return  True if the file was successfully written, false otherwise.
 */
inline bool WriteFile(const std::string &filename, const std::string &buffer, const bool append = false)
	{ return WriteFile(filename, buffer.c_str(), buffer.length(), append); }


/** \brief  Copy a file.
 *  \param  from_filename  The name of the file to copy from.
 *  \param  to_filename    The name of the file to copy to.
 */
void CopyFile(const std::string &from_filename, const std::string &to_filename);


/** \brief  Copies a file to a stream (which represents another file).
 *  \param  from_filename  The name of the file to copy from.
 *  \param  to_file        The stream to copy the data to.
 */
void CopyFile(const std::string &from_filename, std::ostream &to_file);


/** \brief   Delete a file.
 *  \param   path  The path of the file to delete.
 *  \return  True if the files was successfully deleted.
 */
bool DeleteFile(const std::string &path);


/** \brief   Deletes files based on a pattern as understood by glob(3).
 *  \param   pattern   The pattern for the files to delete.
 *  \param   recursive Toggle recursive behavior for directories.
 *  \return  True if all files matching "path_pattern" were successfully deleted.
 *  \note    The pattern matching performs tilde expansion and csh(1) style brace-expression expansion.
 */
bool DeleteFiles(const std::string &pattern, const bool recursive = false);


/** \brief   Returns a list of files that match the provided pattern
 *  \param   pattern         The pattern to match against; such as *.classifier
 *  \param   list_of_files   A list of strings to append all matches to
 *  \return  True if glob succeded; false otherwise
 *  \note    This does not clear the list_of_files parameters.
 */
bool Glob(const std::string &pattern, std::vector<std::string> *list_of_files);


/** \brief	Moves/renames a file.
 *  \param	old_path  Path to old file name.
 *  \param	new_path  Path to new file name.
 *  \return	True if rename succeeds.
 */
bool RenameFile(const std::string &old_path, const std::string &new_path);


/** \brief   Recursively delete a directory.
 *  \param   directory  The path of the directory to delete.
 *  \param   recursive  If true, attempt to perform a recursive delete (otherwise
 *                      the delete will fail on nonempty directories).
 *  \return  True if the directory has been deleted, else false.
 *  \note    If this function fails you might check "errno" for the reason.
 */
bool DeleteDirectory(const std::string &directory, const bool recursive = true);


/** \brief   Converts an errno set by access(2) to a string.
 *           The string values are just copied and pasted from the access(2) UNIX man page.
 *  \param   errno_to_convert  An 'errno' that will be converted.
 *  \param   pathname          The path that was used in access(2)
 *  \param   mode              The mode that was used in access(2)
 *  \return  A string containg a message explaining actual error.
 */
std::string AccessErrnoToString(int errno_to_convert, const std::string &pathname, const std::string &mode);


/** \brief  Does the named file (or directory) exist?.
 *  \param  path           The path of the file.
 *  \param  error_message  Where to store an error message if an error occurred.
 */
bool Exists(const std::string &path, std::string * const error_message = NULL);


/** \brief  Is the named file (or directory) readable?.
 *  \param  path  The path to the file or directory.
 */
bool IsReadable(const std::string &path);


/** \brief   Is the given path the name of a directory?
 *  \param   dir_name  The path to test.
 *  \return  True if the path is a directory and can be accessed.
 *
 *  IsDirectory returns false if "dir_name" either doesn't exist, we
 *  don't have sufficient priviledges to stat it or it exists but is
 *  not a directory.
 */
bool IsDirectory(const std::string &dir_name);


/** \brief   Create a directory.
 *  \param   path       The path to create.
 *  \param   recursive  If true, attempt to recursively create parent directoris too.
 *  \param   mode       The access permission for the directory/directories that will be created.
 *  \return  True if the directory already existed or has been created else false.
 */
bool MakeDirectory(const std::string &path, const bool recursive = false, const mode_t mode = 0775);


/** \brief   Test whether we have write access to a file or directory
 *  \param   path  The path to test.
 *  \return  True if the path is writable.
 */
inline bool IsWritable(const std::string &path)
        { return (::access(path.c_str(), W_OK) == 0); }


/** \brief   Change the permissions of a file or directory.
 *  \param   path  The path of the file or directory.
 *  \param   mode  The new permissions.
 *  \return  True if the operation succeeds
 */
inline bool ChMod(const std::string &path, mode_t mode)
        { return (::chmod(path.c_str(), mode) == 0); }


/** \brief   Returns the size of file "path".
 *  \param   path  The name of the file whose size we want.
 *  \return  The size of the file.
 */
off_t Size(const char * const path) throw(std::exception);


/** \brief   Returns the size of file "path".
 *  \param   path  The name of the file whose size we want.
 *  \return  The size of the file.
 */
inline off_t Size(const std::string &path) throw(std::exception)
{
	return Size(path.c_str());
}


/** \brief   Returns the date that the file "path" was modified.
 *  \param   path  The name of the file to examine..
 *  \return  The last modification date of the file.
 */
time_t LastModified(const std::string &path);


/** \brief  Escape a string so it can be used as a filename.
 *
 * ShellEscape returns a double-quotes string with escaped backticks,
 * dollar signs, double-quotes and backslashes.
 */
std::string ShellEscape(const std::string &s);


/** \brief   Skip over whitespace in a stream.
 *  \param   stream  The input stream from which to read whitespace.
 *  \return  The number of newline (not whitespace!) characters skipped.
 *
 *  Skip over "whitespace" at the start of the stream and return the
 *  number of newlines that were skipped.
 */
unsigned SkipWhiteSpace(std::istream &stream);


/** \brief  Split a path into a directory name part and filename part.
 *  \param  path      The path to split.
 *  \param  dirname   Will hold the directory name part.
 *  \param  basename  Will hold the filename part.
 */
void DirnameAndBasename(const std::string &path, std::string * const dirname, std::string * const basename);


/** \brief  Return only the filename portion of a full path.
 *  \param  full_path     The full path being examined.
 *  \return The filename component only.
 */
std::string ExtractFilename(const std::string &full_path);

/** \brief  Split the directory name part from a filename.
 *  \param  path      The path to split.
 *  \return The directory name part.
 *  \note   Generally the returned path will end in a trailing slash unless it is empty!
 */
std::string Dirname(const std::string &path);


/** \brief  Simplifies a path as much as possible.
 *  \param  path  The path to simplify.
 *  \return The simplified path.
 */
std::string CanonisePath(const std::string &path);


/** \brief  Makes a relative path absolute using an absolute reference path.
 *  \param  reference_path  An absolute path to use as the reference path for "relative_path".
 *  \param  relative_path   The path to make absolute (unless it already starts with a slash).
 *  \note   Unless "reference_path" path ends in a slash the last component is stripped off unconditionally.  So if you
 *          plan to use all of "reference_path" as the path prefix for "relative_path" you must ensure that it ends in a
 *          slash!
 *  \return The absolute path equivalent of "relative_path".
 */
std::string MakeAbsolutePath(const std::string &reference_path, const std::string &relative_path);


/** Makes "relative_path" absolute using the current working directory as the reference path. */
std::string MakeAbsolutePath(const std::string &relative_path);


inline std::string MakeAbsolutePath(const char * const relative_path)
{ return MakeAbsolutePath(std::string(relative_path)); }


/** \brief  Turns an absolute path into a relative path.
 *  \param  reference_path  An absolute path to use as the reference path for "path".
 *  \param  path An absolute path that will be made relative to "reference_path".
 *  \note   Unless "reference_path" path ends in a slash the last component is stripped off unconditionally.  So if you
 *          plan to use all of "reference_path" as the path prefix for "path" you must ensure that it ends in a slash!
 *  \return The relative path version of "path".
 */
std::string MakeRelativePath(const std::string &reference_path, const std::string &path);

/** \brief  Performs tilde expansion on "path".
 *  \param  path      The path to expand.
 *  \note   Right now this function only handles paths of the type "~/..."
 *          and then assumes that the "USER" environment variable has been
 *          set.
 *  \return The expanded path.
 */
std::string TildeExpand(const std::string &path);


/** \brief  Reads an arbitrarily long line.
 *  \param  stream      The input stream to read from.
 *  \param  line        Where to store the read line.
 *  \param  terminator  The character terminating the line (won't be stored in "line").
 *  \return False on EOF, otherwise true.
 */
bool GetLine(std::istream &stream, std::string * const line, const char terminator = '\n');


/** \brief  Reads an arbitrarily long line.
 *  \param  file        The input file to read from.
 *  \param  line        Where to store the read line.
 *  \param  max_length  The maximum length of characters to read.
 *  \param  terminators The characters terminating the line.
 *  \return Number of characters read.
 */
size_t GetLine(FILE *file, char *line, size_t max_length, const char * const terminators = "\n") throw();


enum PermType {
	PERM_IGNORE, /**< Report all files and directories regardless of their
			  permissions (modes). */
	PERM_ANY,    /**< Report those files and directories whose permissions (modes)
			  share at least one bit with the specified ones. */
	PERM_ALL,    /**< Only report files and directories whose permissions (modes)
			  are identical to, or a superset of, the specified ones. */
	PERM_MATCH   /**< Only report files and directories whose permissions (modes)
			  are identical to the specified ones. */
};


/** \brief  Performs a depth-first visit of "directory" and performs a call to a user-provided
 *          callback function for each file or directory that will be encountered.
 *  \param  root_directory  The directory whose contents will be visited in a depth-first manner.
 *  \param  callback        The function that will be called once each for all visited files and
 *                          directories.  The traversal will be aborted the first time "callback"
 *                          returns false.
 *  \param  perm_type       See enum PermType above for a detailed explanation.
 *  \param  file_mode       The meaning of these bits is determined by the value of "perm_type".
 *  \param  directory_mode  The meaning of these bits is determined by the value of "perm_type".
 *  \return False if the last call to "callback" returned false, otherwise true.
 *  \note   To see an example use of this function, look at the implementation of DeleteDirectory.
 */
bool DepthFirstVisit(const std::string &root_directory, bool (*callback)(const std::string &path,
									 const bool is_directory),
		     const PermType perm_type = PERM_IGNORE, const mode_t file_mode = 0,
		     const mode_t directory_mode = 0);


/** \brief  Periodically checks for the existence of a file or files.
 *  \param  path_pattern   A shell-style filename pattern.
 *  \param  poll_interval  How long to sleep (in seconds) between checking.
 *  \param  timeout        The maximum amount of time to check (in seconds).  Zero means infinite.
 *  \return True if a file was found matching "path_pattern" and false if a timeout occurred or
 *          some other error occured.  Please check "errno" if this function fails.  "ETIME" is
 *          the error code for a timeout.  Other error codes are also possible indicating other
 *          failures.
 */
bool PollForFile(const std::string &path_pattern, const unsigned poll_interval = 1, const unsigned timeout = 0);


/** \brief  Tests whether "filename" is a 'sane' file name, i.e. whether it would be safe
 *          to use under most circumstances.
 *  \param  filename  The file name to test
 *  \return True if we consider "filename" to be 'sane', else false.
 */
bool IsSaneFilename(const std::string &filename);


/** \enum   FileType
 *  \brief  Possible types for a file.
 */
enum FileType {
	FILE_TYPE_UNKNOWN,
	FILE_TYPE_TEXT,      // .txt
	FILE_TYPE_HTML,      // .htm .html .php
	FILE_TYPE_PDF,       // .pdf
	FILE_TYPE_PS,        // .ps, .eps
	FILE_TYPE_DOC,       // .sxw .doc
	FILE_TYPE_SLIDES,    // .sxi .ppt
	FILE_TYPE_TEX,       // .tex ???
	FILE_TYPE_DVI,       // .dvi
	FILE_TYPE_TAR,       // .tar
	FILE_TYPE_RTF,       // .rtf
	FILE_TYPE_GZIP,      // .tgz, .gz
	FILE_TYPE_Z,         // .Z    COMPRESS
	FILE_TYPE_CODE,      // .c, .cc, .h, .pm, ...
	FILE_TYPE_GRAPHIC,   // .gif, .jpg, ...
	FILE_TYPE_AUDIO,     // .ogg, .mp3
	FILE_TYPE_MOVIE      // .mpg, .mpeg, .divx
};


/** \brief  Attempt to guess the file type of "filename".
 *  \param  filename  The filename for which we'd like to determine the file type.
 *  \return The guessed file type.
 */
FileType GuessFileType(const std::string &filename);


/** \brief  Converts an enumerated type 'file_type' to a std::string.
 *  \param  file_type   The type of file that should be converted to a std::string.
 */
std::string FileTypeToString(FileType const file_type);


/** \brief  Attempts to set O_NONBLOCK on a file descriptor.
 *  \param  fd  An open file descriptor.
 *  \return True if we succeeded in setting O_NONBLOCK on "fd", else false.
 *  \note   If this function returns false errno contains an appropriate error code.
 */
bool SetNonblocking(const int fd);


/** \brief  Attempts to clear O_NONBLOCK on a file descriptor.
 *  \param  fd  An open file descriptor.
 *  \return True if we succeeded in clearing O_NONBLOCK on "fd", else false.
 *  \note   If this function returns false errno contains an appropriate error code.
 */
bool SetBlocking(const int fd);


/** \brief  Copies a directory and its contents.
 *  \param  src        The path to the directory to copy.
 *  \param  target     Where to copy the directory to, i.e. the name of the target directory.
 *  \param  recursive  If true, perform a recursive copy, i.e. descent into subdirectories of "src" when copying.
 */
void CopyDirectory(const std::string &src, const std::string &target, const bool recursive = true);


/** \brief  Compresses "filename" using zip(1).
 *  \param  archive_filename       Name (without ".zip") of the archive to be created.
 *  \param  files_to_archive       List of files or directories to be added to the archive.
 *  \param  delete_archived_files  If "true" will remove the files or directories specified by "files_to_archive" after
 *                                 the archive has been created.
 *  \param  strip_paths            If "true" strips path prefixes of "files_to_archive" in the names as stored in the
 *                                 archive.
 *  \note   The resulting file will have ".zip" appended to it's name.
 */
void ZipFiles(const std::string &archive_filename, const std::list<std::string> &files_to_archive,
	      const bool delete_archived_files = false, const bool strip_paths = false);


void ZipFile(const std::string &archive_filename, const std::string &file_to_archive,
	     const bool delete_archived_file = false, const bool strip_paths = false);


/** Returns the number of open file descriptors of the current process.  Caution: May be slow. */
unsigned GetOpenFileDescriptorCount();


/** \brief  Tests a file descriptor for readiness for reading.
 *  \param  fd          The file descriptor that we we want to test for read readiness.
 *  \param  time_limit  Up to how long to wait, in milliseconds, for the descriptor to become ready for reading.
 *  \return True if the specified file descriptor is ready for reading, otherwise false.
 *  \note   Please beware that on POSIX based systems it is possible that a file descriptor is ready for reading yet may
 *          still return 0 bytes!  It just means that read(2) will not hang.
 */
bool DescriptorIsReadyForReading(const int fd, const TimeLimit &time_limit = 0);


/** \brief  Tests a file descriptor for readiness for writing.
 *  \param  fd          The file descriptor that we we want to test for write readiness.
 *  \param  time_limit  Up to how long to wait, in milliseconds, for the descriptor to become ready for writing.
 *  \return True if the specified file descriptor is ready for writing, otherwise false.
 *  \note   Please beware that on POSIX based systems it is possible that a file descriptor is ready for writing yet may
 *          still return 0 bytes!  It just means that write(2) will not hang.
 */
bool DescriptorIsReadyForWriting(const int fd, const TimeLimit &time_limit = 0);


/** \brief  Like touch(1), updates the access and modification time of "path" or creates a new empty file.
 *  \param  path                  The file whose access and modification time to update.
 *  \param  create_if_not_exists  If true, "path" will be created if it doesn't exist.
 *  \param  creation_mode         Only used if "path" does not exist and "create_if_not_exists" is true.  In that case
 *                                the permissions of the created file are (mode & ~umask) where "umask" is the current
 *                                process' umask.
 *  \return True if we succeeded and false on failure.  A failure is typically due to access permission problems.  If
 *          false has been returned the caller may inspect errno for the cause of the failure.
 */
bool Touch(const std::string &path, const bool create_if_not_exists, const int creation_mode = 0660);


/** Returns true if "fd" refers to a read-only open file descriptor, otherwise returns false if "fd" is open.  Throws an
    exception if this information cannot be obtained.  Typically this means that "fd" does not correspond to an open
    file descriptor. */
bool IsReadOnly(const int fd);


/** Repositions the offset of the open file associated with the file descriptor "fd" to the start of the file.
 *  If this function fails it returns false and sets errno to an appropriate error code.
 */
bool Rewind(const int fd);


/** Returns the setting of the $HOME environment variable.  Should $HOME not be set, this function throws an
    exception! */
std::string GetHomeDirectory();


/** \brief  Compares two files for having identical contents.
 *  \param  filename1  The name of the first file to be compared.
 *  \param  filename2  The name of the second file to be compared.
 *  \param  diff       If non-NULL the output of "/usr/bin/diff" for the two files.
 *  \return True if the contents of "filename1" and "filename2" are identical, otherwise false.
 */
bool FilesAreIdentical(const std::string &filename1, const std::string &filename2, std::string * const diff = NULL);


/** \brief Creates a temporary directory and returns unique filenames for temporary purposes,
 *         deleting the directory and all temporary files upon destruction
 */
class TemporaryFilenameGenerator {
	std::string directory_path_;
	std::string file_suffix_;
public:
	/** \brief Constructs a temporary directory using FileUtil::UniqueFileName and hands off unique filenames in that
	 *         directory until the object is destroyed
	 *  \param file_path   The path to create the file in (default /tmp)
	 *  \param prefix      The prefix to add to the directory name (default blank)
	 *  \param suffix      The suffix for the directory name (default .files)
	 *  \param file_suffix What to end the files in (default .tmp; may want to change to .classifier, for example)
	 */
	TemporaryFilenameGenerator(const std::string &file_path = "/tmp", const std::string &prefix = "",
			           const std::string &suffix = ".files", const std::string &file_suffix = ".tmp")
				   : directory_path_(UniqueFileName(file_path, prefix, suffix)), file_suffix_(file_suffix)
	{ MakeDirectory(directory_path_); }

	~TemporaryFilenameGenerator() { DeleteDirectory(directory_path_, true); }

	/** \brief  Get a unique filename in the temporary directory
	 *  \return A full path/filename to a safe-to-write file
	 */
	std::string getFilename() { return UniqueFileName(directory_path_, "", file_suffix_); }

	/** \brief  Get the path that the files are stored in
	 *  \return A full path to the place where the files are stored
	 */
	std::string getPath() { return directory_path_; }
};


/** \brief Creates a symbolic link in the file system.
 *         deleting the directory and all temporary files upon destruction
 */
void CreateSymbolicLink(const std::string &real_file_name, const std::string &link_file_name);


/** \brief   Removes the last path component from a file name.
 *  \param   path           The original, "full" path.
 *  \param   stripped_path  The "path" minus the last component upon success.  On a successful return the stripped path will always end in one or more
 *                          trailing slashes!
 *  \note    The strategy used first removes optional trailing slashes and then attempts to remove a non-empty trailing path component.
 *  \note    It is perfectly legitimate to pass in the same string variable for both arguments!
 *  \return  True upon success, otherwise false.
 *  \warning An empty resulting stripped path will be treated as a failure.
 */
bool StripLastPathComponent(const std::string &path, std::string * const stripped_path);


/** \brief  Returns the current directory if valid or throws an exception.
 *  \note   If a directory is returned, it is guaranteed to end in a slash.
 */
std::string GetCurrentDirectory();


/** \brief  Create a named pipe.
 *  \param  path  Where to put the pipe in the file system.
 *  \mode   mode  Access rights to "path."  Please note that if a directory component has to be created, S_IRUSR | S_IWUSR | S_IXUSR will be added to
 *                the mode for the created directory or directories.
 *  \note   If there is a directory component to "path", we will attempt to create the directory or disrectories.
 *  \return True if we succeeded, false otherwise.
 *  \note   On error, the caller may consult "errno" for the reason for the failure.
 */
bool CreateFIFO(const std::string &path, const mode_t mode = S_IRUSR | S_IWUSR | S_IRGRP | S_IWGRP);


} // namespace FileUtil


#endif // ifndef FILE_UTIL_H
