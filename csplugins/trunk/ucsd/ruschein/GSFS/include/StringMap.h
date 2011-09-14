/** \file     StringMap.h
 *  \brief    Declaration of class StringMap.
 *  \author   Dr. Johannes Ruscheinski
 *  \author   Dr. Gordon W. Paynter
 *  \author   Wagner Truppel
 */

/*
 *  Copyright 2002-2007 Project iVia.
 *  Copyright 2002-2007 The Regents of The University of California.
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

#ifndef STRING_MAP_H
#define STRING_MAP_H


#include <fstream>
#include <list>
#include <map>
#include <string>

/** \class  StringMap
 *  \brief  A "mapping" from one string value to another.
 *
 *  This class is a wrapper arounf the standard library map function
 *  with a simpler interface and slightly different insert and delete
 *  behaviour.  We find it useful in many places.
 *
 *  The differences are: keys are unique, the insert member function
 *  takes two strings as input (and will replace existing values), and
 *  the remove member function silently accepts attempts to remove
 *  keys that are not in the Map.
 *
 */
class StringMap: public std::map<std::string, std::string> {
public:
	StringMap() { }

        /** \brief  Insert a value, replacing any existing value.
         *  \param  name   The name of the key.
         *  \param  value  The value to be associated with "name".
         *
         *  The pair (name, value) is stored in the StringMap.  If
         *  there is an existing value associated with name, it is
         *  deleted first.
         */
 	void insert(const std::string &name, const std::string &value);
	void insert(const std::string &name, const long n, const unsigned radix = 10, const int width = 0);
	void insert(const std::string &name, const unsigned long n, const unsigned radix = 10, const int width = 0);
	void insert(const std::string &name, const long long n, const unsigned radix = 10, const int width = 0);
	void insert(const std::string &name, const unsigned long long n, const unsigned radix = 10, const int width = 0);
	void insert(const std::string &name, const int n, const unsigned radix = 10, const int width = 0);
	void insert(const std::string &name, const unsigned n, const unsigned radix = 10, const int width = 0);
	void insert(const std::string &name, const double n, const unsigned precision = 1);
	void insert(const std::string &name, const bool b);

	void insert(const std::string &name, const char *value)
		{ insert(name, std::string(value)); }

        /** \brief   Delete a (name,value) pair from a StringMap.
         *  \param   name   The name of the data to be deleted.
         *  \return  true if "name" was in the StringMap, false otherwise.
         */
	bool remove(const std::string &name);

        /** \brief   Get the value associated with a name.
         *  \param   name           The name of the data to be retrieved.
         *  \param   default_value  The value to return if the name has no associated value.
         *  \return  The value associated with name, or default if it is undefined.
         */
	std::string get(const std::string &name, const std::string &default_value = "") const
	        {
		        std::map<std::string, std::string>::const_iterator pair(this->find(name));
			return (pair == this->end() ? default_value : pair->second);
		}

        /** \brief   Is a particular key in the map?
         *  \param   name           The name of the key to test for.
         *  \return  True if "name" is a key in the map, otherwise false.
         */
	bool isDefined(const std::string &name) const
	        { return this->find(name) != this->end(); }

        /** \brief   Get the keys of this map.
         *  \param   keys  A list that will hold the unsorted keys.
         */
	void getKeys(std::list<std::string> * const keys) const
	        {
			keys->clear();
		        for (std::map<std::string, std::string>::const_iterator pair(begin());
			     pair != end(); ++pair)
				keys->push_back(pair->first);
		}
};


std::ostream &operator<<(std::ostream &output, const StringMap &string_map);


#endif // ifndef STRING_MAP_H
