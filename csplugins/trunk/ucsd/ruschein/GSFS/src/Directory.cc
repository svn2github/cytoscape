/** \file    Directory.cc
 *  \brief   Implementation of file utility functions.
 *  \author  Dr. Johannes Ruscheinski
 */

/*
 *  Copyright 2003-2008 Project iVia.
 *  Copyright 2003-2008 The Regents of The University of California.
 *
 *  This file is part of the libiViaCore package.
 *
 *  The libiViaCore package is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License as published
 *  by the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  libiViaCore is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with libiViaCore; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

#include <Directory.h>
#include <Compiler.h>
#include <FileUtil.h>
#include <MsgUtil.h>
#include <cerrno>
#include <cstring>
#include <fnmatch.h>
#include <unistd.h>
#include <Exception.h>


std::string Directory::Entry::getFileOrDirectoryName() const
{
	return FileUtil::ExtractFilename(filename_);
}


off_t Directory::Entry::getSize() const
{
	if (not called_stat_) {
		if (unlikely(::stat(filename_.c_str(), &stat_buffer_) != 0))
			throw Exception("in Directory::Entry::getSize: stat(2) failed on \"" + filename_ + "\" (" + MsgUtil::ErrnoToString() + ")!");
		called_stat_ = true;
	}

	return stat_buffer_.st_size;
}


void Directory::const_iterator::advance()
{
	previous_entry_ = current_entry_;

	struct dirent *dirent;
	do {
		errno = 0;
		dirent = ::readdir(dir_);
		if (dirent == NULL and errno == EBADF)
			throw Exception("in Directory::const_iterator::advance: readdir(3) returned an error (" + MsgUtil::ErrnoToString() + ")!");
	} while (dirent != NULL and not directory_.match(dirent->d_name));

	if (dirent != NULL)
		current_entry_ = Entry(directory_.getPath() + std::string(1, '/') + dirent->d_name);
	else
		current_entry_ = Entry();
}


Directory::const_iterator::const_iterator(const Directory &directory, const bool at_end)
	: directory_(directory), dir_(NULL)
{
	if (at_end) {
		current_entry_ = Entry();
		return;
	}

	if (directory_.path_.empty())
		return;

	if ((dir_ = ::opendir(directory_.path_.c_str())) == NULL)
		throw Exception("in Directory::const_iterator::const_iterator: opendir(3) on \"" + directory_.path_ + "\" failed ("
				+ MsgUtil::ErrnoToString() + ") (1)!");

	advance();
}


Directory::const_iterator::const_iterator(const const_iterator &rhs)
	: directory_(rhs.directory_), dir_(NULL)
{
	if ((dir_ = ::opendir(directory_.path_.c_str())) == NULL)
		throw Exception("in Directory::const_iterator::const_iterator: opendir(3) on \"" + directory_.path_ + "\" failed ("
				+ MsgUtil::ErrnoToString() + ") (2)!");

	advance();
}


// Note: this function may be more paranoid than strictly necessary (but that shouldn't harm
//       anything).
//
bool Directory::const_iterator::operator==(const const_iterator &rhs) const
{
	if (previous_entry_ == rhs.previous_entry_ and current_entry_ == rhs.current_entry_)
		return true;

	// At the end?
	if (current_entry_.empty() and rhs.current_entry_.empty())
		return true;

	return false;
}


const Directory::Entry *Directory::const_iterator::operator++()
{
	advance();

	return &current_entry_;
}


const Directory::Entry *Directory::const_iterator::operator++(int)
{
	advance();

	return &previous_entry_;
}


size_t Directory::size() const
{
	if (path_.empty())
		return 0;

	DIR *dir(::opendir(path_.c_str()));
	if (dir == NULL)
		throw Exception("in Directory::size: opendir(3) on \"" + path_ + "\" failed (" + MsgUtil::ErrnoToString() + ")!");

	size_t count(0);
	struct dirent *dirent;
	do {
		errno = 0;
		dirent = ::readdir(dir);
		if (dirent == NULL) {
			if (errno == EBADF)
				throw Exception("in Directory::size: readdir(3) returned an error (" + MsgUtil::ErrnoToString() + ")!");
		}
		else if (match(dirent->d_name))
			++count;
	} while (dirent != NULL);

	::closedir(dir);
	return count;
}


bool Directory::match(const char * const filename) const
{
	for (std::list<std::string>::const_iterator pattern(patterns_.begin()); pattern != patterns_.end(); ++pattern) {
		if (::fnmatch(pattern->c_str(), filename, FNM_PATHNAME) == 0)
			return true;
	}

	return false;
}
