/** \file    ValueListMap.h
 *  \brief   Declaration of class ValueListMap.
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

#ifndef VALUE_LIST_MAP_H
#define VALUE_LIST_MAP_H


#include <fstream>
#include <string>
#include <map>
#include <list>


/** \class  ValueListMap
 *  \brief  A "mapping" from a single string value to a list of string values.
 *
 *  This class encapsullates a map from a string called the "key" to a
 *  list of strings associated with it, called the "values".
 *
 *  The ValueListMap is frequently used to hold a set of Parameters
 *  (e.g. the parameters of a CGI script) which can be interpreted as
 *  strings or numbers or booleans, and which may be repeated.
 */
class ValueListMap {
	std::map<std::string, std::list<std::string> > value_to_list_map_;
public:
	typedef std::map<std::string, std::list<std::string> >::const_iterator const_iterator;
	typedef std::map<std::string, std::list<std::string> >::iterator iterator;
public:
	ValueListMap() { }

	/** Constructs a ValueListMap object from a string that was returned by the toString() member function. */
	explicit ValueListMap(const std::string &string_rep);

	size_t size() const { return value_to_list_map_.size(); }
	void erase(const std::string &key) { value_to_list_map_.erase(key); }
	const_iterator find(const std::string &key) const { return value_to_list_map_.find(key); }
	std::list<std::string> operator[](const std::string &key) { return value_to_list_map_[key]; }

	/** \brief   Are there any values associated with a key?
	 *  \param   key  The key we wish to look up.
	 *  \return  True if there are values associted with the key, otherwise false.
	 */
	bool isDefined(const std::string &key) const { return (value_to_list_map_.find(key) != value_to_list_map_.end()); }


	/** \brief   How may values are associated with a key?
	 *  \param   key  The key we wish to look up.
	 *  \return  The number of values associated with the key.
	 */
	unsigned numberOfValues(const std::string &key) const;


	/** \brief   Return the list of values associated with "key".
	 *  \param   key  The key we wish to look up.
	 *  \return  A reference to the list of values associated with "key".
	 *
	 *  If "key" is not set, a reference to an empty list is returned.
	 */
	const std::list<std::string> &getValues(const std::string &key) const;


	/** \brief  Return the first string value associated with a key.
	 *  \param   key  The key we wish to look up.
	 *  \return  The first value associated with key.
	 */
	const std::string &getString(const std::string &key) const;


	/** \brief   Get the first value associated with a key as a string.
	 *  \param   key            The key we wish to look up.
	 *  \param   default_value  The value to return if "key" is not in the map.
	 *  \return  The first value associated with key, or the default value if there is none.
	 */
	const std::string &getString(const std::string &key, const std::string &default_value) const;


	/** \brief Return the single value associated with "key" as an unsigned.
	 *  \param   key  The key we wish to look up.
	 *  \return  The value associated with key as an unsigned.
	 *
	 *  This function throws an exception if "key" is undefined,
	 *  or has more than one value, or has a single value but
	 *  cannot be converted to an unsigned.
	 */
	unsigned getUnsigned(const std::string &key) const;


	/** \brief   Get the unsigned number value associated with "key".
	 *  \param   key            The key of the value to search for.
	 *  \param   default_value  The value to return if "key" is not in the map.
	 *  \return  The first value associated with key, or the default if there is none.
	 *
	 *  This function throws an exception if "key" exists but
	 *  cannot be converted to a number.
	 */
	unsigned getUnsigned(const std::string &key, const unsigned default_value) const;


	/** \brief  Return the single value associated with "key" as a double.
	 *  \param   key  The key we wish to look up.
	 *  \return  The value associated with key as a double.
	 */
	double getDouble(const std::string &key) const;


	/** \brief  Return the boolean value of a CGI argument.
         *
         *  Treats values of "yes", "true", "on" and "1" (in a case insensitive manner) as true, and "no", "false", "off"
	 *  and "1" as false.
         */
	bool getBool(const std::string &key) const;


	/** \brief  Return the boolean value of a CGI argument.
	 *  \param  key           The key of the value to search for.
	 *  \param  default_value  The value to return if "key" id not in the map.
         *
         *  Treats values of "yes", "true", "on" and "1" (in a case insensitive manner) as true, and "no", "false", "off"
	 *  and "1" as false.  If there is no value associated with key, the default value will be returned.
         */
	bool getBool(const std::string &key, const bool default_value) const;


	/** \brief  Remove all values corresponding to a key from the map.
	 *  \brief  key  The key whose values we want to remove.
	*/
	void clear(const std::string &key);


	/** \brief   Return the list of keys that have been defined for this map.
	 *  \param   reg_exp  A Perl compatible regular expression that keys will be matched against.
	 *  \return  A list of keys..
	 */
	std::list<std::string> getKeys(const std::string &reg_exp = ".*") const;


	/** \brief  Insert a key-value pair into the map.
	 *  \param  key    The key of the value to insert.
	 *  \param  value  The value to associate with the key.
	 *
	 *  The "value" will be associated with the given key.  Note that if the key is already in the map, then the value
	 *  will be added to the end of the list of values associated with that key.
	 */
	void insert(const std::string &key, const std::string &value);


	/** \brief  Insert a key-value pair into the map.
	 *  \param  key    The key of the value to insert.
	 *  \param  value  The value to associate with the key.
	 *
	 *  The "value" will be associated with the given key.  Note that if the key is already in the map, then the value
	 *  will be added to the end of the list of values associated with that key.
	 */
	void insert(const std::string &key, const char * const value) { insert(key, std::string(value)); }


	/** \brief  Insert a key-value pair into the map.
	 *  \param  key    The key of the value to insert.
	 *  \param  value  The value to associate with the key.
	 *  \param  radix  The number base of the number to be converted.
	 *  \param  width  Pad up to this width with spaces.  If width is positive the number will be right-justified, else it
	 *                 will be left-justified.
	 *
	 *  The "value" will be associated with the given key.  Note that if the key is already in the map, then the value
	 *  will be added to the end of the list of values associated with that key.
	 */
	void insert(const std::string &key, const int value, const unsigned radix = 10, const int width = 0);


	/** \brief  Insert a key-value pair into the map.
	 *  \param  key    The key of the value to insert.
	 *  \param  value  The value to associate with the key.
	 *  \param  radix  The number base of the number to be converted.
	 *  \param  width  Pad up to this width with spaces.  If width is positive the number will be right-justified, else it
	 *                 will be left-justified.
	 *
	 *  The "value" will be associated with the given key.  Note that if the key is already in the map, then the value
	 *  will be added to the end of the list of values associated with that key.
	 */
	void insert(const std::string &key, const long value, const unsigned radix = 10, const int width = 0);


	/** \brief  Insert a key-value pair into the map.
	 *  \param  key    The key of the value to insert.
	 *  \param  value  The value to associate with the key.
	 *  \param  radix  The number base of the number to be converted.
	 *  \param  width  Pad up to this width with spaces.  If width is positive the number will be right-justified, else it
	 *                 will be left-justified.
	 *
	 *  The "value" will be associated with the given key.  Note that if the key is already in the map, then the value
	 *  will be added to the end of the list of values associated with that key.
	 */
	void insert(const std::string &key, const long long value, const unsigned radix = 10, const int width = 0);


	/** \brief  Insert a key-value pair into the map.
	 *  \param  key    The key of the value to insert.
	 *  \param  value  The value to associate with the key.
	 *  \param  radix  The number base of the number to be converted.
	 *  \param  width  Pad up to this width with spaces.  If width is positive the number will be right-justified, else it
	 *                 will be left-justified.
	 *
	 *  The "value" will be associated with the given key.  Note that if the key is already in the map, then the value
	 *  will be added to the end of the list of values associated with that key.
	 */
	void insert(const std::string &key, const unsigned value, const unsigned radix = 10, const int width = 0);


	/** \brief  Insert a key-value pair into the map.
	 *  \param  key    The key of the value to insert.
	 *  \param  value  The value to associate with the key.
	 *  \param  radix  The number base of the number to be converted.
	 *  \param  width  Pad up to this width with spaces.  If width is positive the number will be right-justified, else it will be left-justified.
	 *
	 *  The "value" will be associated with the given key.  Note that if the key is already in the map, then the value will be added to the end of the
	 *  list of values associated with that key.
	 */
	void insert(const std::string &key, const unsigned long value, const unsigned radix = 10, const int width = 0);


	/** \brief  Insert a key-value pair into the map.
	 *  \param  key    The key of the value to insert.
	 *  \param  value  The value to associate with the key.
	 *  \param  radix  The number base of the number to be converted.
	 *  \param  width  Pad up to this width with spaces.  If width is positive the number will be right-justified, else it will be left-justified.
	 *
	 *  The "value" will be associated with the given key.  Note that if the key is already in the map, then the value will be added to the end of the
	 *  list of values associated with that key.
	 */
	void insert(const std::string &key, const unsigned long long value, const unsigned radix = 10, const int width = 0);


	/** \brief  Insert a key-value pair into the map.
	 *  \param  key    The key of the value to insert.
	 *  \param  value  The value to associate with the key.
	 *
	 *  The "value" will be associated with the given key.  Note that if the key is already in the map, then the value
	 *  will be added to the end of the list of values associated with that key.
	 */
	void insert(const std::string &key, const bool value) { insert(key, value ? "true" : "false"); }


	/** \brief  Insert a key and a list of values into the map.
	 *  \param  key         The key of the values to insert.
	 *  \param  value_list  The list of values to associate with the key.
	 *
	 *  The "value_list" will be associated with the given key.  Note that if the key is already in the map, then all
	 *  its values will be replaced by the new values.
	 */
	void insert(const std::string &key, const std::list<std::string> &value_list);


	/** \brief   Returns a string representation of a ValueListMap.
	 *  \return  A string representation of the map.
	 */
	std::string toString() const;

	const_iterator begin() const { return value_to_list_map_.begin(); }
	const_iterator end() const { return value_to_list_map_.end(); }
};


inline std::ostream &operator<<(std::ostream &output, const ValueListMap &value_list_map)
{
	output << value_list_map.toString();
	return output;
}


#endif // define VALUE_LIST_MAP_H
