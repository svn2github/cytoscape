/** \file    StringMap.cc
 *  \brief   Implementation of class StringMap.
 *  \author  Dr. Johannes Ruscheinski
 *  \author  Dr. Gordon W. Paynter
 */

/*
 *  Copyright 2002-2005 Project iVia.
 *  Copyright 2002-2005 The Regents of The University of California.
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

#include <StringMap.h>
#ifndef VECTOR
#       include <vector>
#       define VECTOR
#endif
#ifndef STRING_UTIL_H
#       include <StringUtil.h>
#endif


// StringMap::insert -- Insert a value into a StringMap, replacing any old value
//
void StringMap::insert(const std::string &name, const std::string &value)
{
	StringMap::iterator iter = find(name);
	if (iter != end())
		std::map<std::string, std::string>::erase(iter);
	std::map<std::string, std::string>::insert(std::pair<std::string, std::string>(name, value));
}


void StringMap::insert(const std::string &name, const long n, const unsigned radix, const int width)
{
	insert(name, StringUtil::ToString(n, radix, width));
}


void StringMap::insert(const std::string &name, const unsigned long n, const unsigned radix, const int width)
{
	insert(name, StringUtil::ToString(n, radix, width));
}


void StringMap::insert(const std::string &name, const long long n, const unsigned radix, const int width)
{
	insert(name, StringUtil::ToString(n, radix, width));
}


void StringMap::insert(const std::string &name, const unsigned long long n, const unsigned radix, const int width)
{
	insert(name, StringUtil::ToString(n, radix, width));
}


void StringMap::insert(const std::string &name, const int n, const unsigned radix, const int width)
{
	insert(name, StringUtil::ToString(n, radix, width));
}


void StringMap::insert(const std::string &name, const unsigned n, const unsigned radix, const int width)
{
	insert(name, StringUtil::ToString(n, radix, width));
}


void StringMap::insert(const std::string &name, const double n, const unsigned precision)
{
	insert(name, StringUtil::ToString(n, precision));
}


void StringMap::insert(const std::string &name, const bool b)
{
	insert(name, b ? "true" : "false");
}


// StringMap::remove -- Delete a value from a StringMap based on it's name
//
bool StringMap::remove(const std::string &name)
{
	StringMap::iterator iter = find(name);
	if (iter == end())
		return false;

	std::map<std::string, std::string>::erase(iter);
	return true;
}


std::ostream &operator<<(std::ostream &output, const StringMap &string_map)
{
	for (StringMap::const_iterator pair(string_map.begin()); pair != string_map.end(); ++pair)
		output << pair->first << '=' << pair->second << '\n';

	return output;
}
