/** \file    Directory.h
 *  \brief   Declaration of class Directory.
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

#ifndef DIRECTORY_H
#define DIRECTORY_H


#include <string>
#include <list>
#include <dirent.h>
#include <sys/stat.h>


/** \class  Directory
 *  \brief  Represents a directory and its contents.
 *  \note   Patterns as understood by this class are shell-like with the exception that leading periods in filenames are
 *          not treated as special cases.
 *
 *  The contents of the directory are represented by "Entry" objects.
 */
class Directory {
	std::string path_;
	std::list<std::string> patterns_;
public:
	class Entry {
		std::string filename_;
		mutable struct stat stat_buffer_;
		mutable bool called_stat_; // Have we already called stat(2)?
	public:
		explicit Entry(const std::string &filename = ""): filename_(filename), called_stat_(false) { }
		bool empty() const { return filename_.empty(); }
		bool operator==(const Entry &rhs) const { return filename_ == rhs.filename_; }
		std::string getFileOrDirectoryName() const;
		off_t getSize() const;
		operator std::string() const { return getFileOrDirectoryName(); }
	};

	class const_iterator {
		friend class Directory;
		const Directory &directory_;
		DIR *dir_;
		Entry previous_entry_, current_entry_;
	public:
		const_iterator(const const_iterator &rhs);
		~const_iterator() { ::closedir(dir_); }
		const Entry *operator->() const { return &current_entry_; }
		const Entry &operator*() const { return current_entry_; }
		const Entry *operator++();
		const Entry *operator++(int);
		bool operator==(const const_iterator &rhs) const;
		bool operator!=(const const_iterator &rhs) const { return not operator==(rhs); }
	private:
		void advance();
		explicit const_iterator(const Directory &directory, const bool at_end = false);
		const const_iterator operator=(const const_iterator &rhs); // Intentionally unimplemented!
	};
private:
	friend class const_iterator;
public:
	/** \brief  Constructs a Directory object.
	 *  \param  path            A relative or absolute search path referring to a directory.
	 *  \param  filter_pattern  A shell-style pattern specifying which file names should be selected.
	 */
	explicit Directory(const std::string &path, const std::string &filter_pattern = "*")
		: path_(path) {  addFilterPattern(filter_pattern); }

	Directory(const std::string &path, const std::list<std::string> &filter_patterns)
		: path_(path), patterns_(filter_patterns) { }

	/** \brief  Adds an additional pattern for filename matching.
	 *  \param  new_filter_pattern  An additional pattern against which filenames will be matched.
	 */
	void addFilterPattern(const std::string &new_filter_pattern) { patterns_.push_back(new_filter_pattern); }

	size_t size() const;

	bool empty() const { return size() == 0; }

	const std::string &getPath() const { return path_; }

	const_iterator begin() const { return const_iterator(*this); }
	const_iterator end() const { return const_iterator(*this, /* at_end = */ true); }

private:
	bool match(const char * const filename) const;
};


#endif // ifndef DIRECTORY_H
