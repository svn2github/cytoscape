/** \file    ValueListMap.cc
 *  \brief   Definition of ValuesListMap class.
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

#include <ValueListMap.h>
#ifndef COMPILER_H
#       include <Compiler.h>
#endif
#ifndef PERL_COMPAT_REG_EXP_H
#       include <PerlCompatRegExp.h>
#endif
#ifndef STRING_UTIL_H
#       include <StringUtil.h>
#endif


// ValueListMap::ValueListMap -- attempts to decode a set of newline-separated lines that each look like
//                               name=value(s).  If "name" has an embedded equal sign it will be backslash-
//                               escaped and if there is more than one value in a line, the values are
//                               comma-separated.  Should a value contain and embedded comma it will be
//                               assumed to be backslash-escaped.
//
ValueListMap::ValueListMap(const std::string &string_rep)
{
	std::list<std::string> lines;
	StringUtil::Split(string_rep, "\n", &lines);
	for (std::list<std::string>::const_iterator line(lines.begin()); line != lines.end(); ++line) {
		std::string::const_iterator ch(line->begin());

		// First get the name before the equal-sign...
		std::string name;
		bool unescaped_equal_sign_seen(false), backslash_seen(false);
		for (/* Intentionally empty! */; ch != line->end(); ++ch) {
			if (backslash_seen) {
				backslash_seen = false;
				name += *ch;
			}
			else if (*ch == '\\')
				backslash_seen = true;
			else if (*ch == '=') {
				++ch;
				unescaped_equal_sign_seen = true;
				break;
			}
			else
				name += *ch;
		}
		if (unlikely(not unescaped_equal_sign_seen or backslash_seen))
			throw Exception("in ValueListMap::ValueListMap: bad string encoding of a ValueListMap object "
						 "(1) (" + string_rep + ")!");

		//...and now extract the comma-separated values:
		std::list<std::string> values;
		while (ch != line->end()) {
			std::string value;
			bool unescaped_comma_seen = false;
			backslash_seen = false;

			// Extract a single value:
			for (/* Intentionally empty! */; ch != line->end(); ++ch) {
				if (backslash_seen) {
					backslash_seen = false;
					value += *ch;
				}
				else if (*ch == '\\')
					backslash_seen = true;
				else if (*ch == ',') {
					++ch;
					unescaped_comma_seen = true;
					break;
				}
				else
					value += *ch;
			}
			if (unlikely((not unescaped_comma_seen and ch != line->end()) or backslash_seen))
				throw Exception("in ValueListMap::ValueListMap: bad string encoding of a "
							 "ValueListMap object (2) (" + string_rep + ")!");

			values.push_back(value);
		}

		insert(name, values);
	}
}


const std::list<std::string> &ValueListMap::getValues(const std::string &key) const
{
	static const std::list<std::string> empty_list;
	ValueListMap::const_iterator iter(value_to_list_map_.find(key));
	if (iter == value_to_list_map_.end())
		return empty_list;

	return iter->second;
}


unsigned ValueListMap::numberOfValues(const std::string &key) const
{
	ValueListMap::const_iterator iter(value_to_list_map_.find(key));
	if (iter == value_to_list_map_.end())
		return 0;

	return (iter->second).size();
}


const std::string &ValueListMap::getString(const std::string &key) const
{
	const std::list<std::string> &arg_list(getValues(key));
	if (arg_list.size() == 1)
		return arg_list.front();
	else if (arg_list.size() > 1)
		throw Exception("in ValueListMap::getString: expected a single query argument with \"" + key + "=...\"!");
	else
		throw Exception("in ValueListMap::getString: entry for \"" + key + "\" is missing!");

	return arg_list.front(); // Will never come here. Keep the compiler happy!
}


const std::string &ValueListMap::getString(const std::string &key, const std::string &default_value) const
{
	const std::list<std::string> &values(getValues(key));
	return (values.empty() ? default_value : values.front());
}


namespace { // helper functions for getBool


// ConvertStringToBool -- converts a string to a boolean "value".  Returns "true" if the conversion succeeded, else
//                        "false".
//
bool ConvertStringToBool(const std::string &word, bool * const value)
{
	if (::strcasecmp("yes",  word.c_str()) == 0 or
	    ::strcasecmp("on",   word.c_str()) == 0 or
	    ::strcasecmp("true", word.c_str()) == 0 or
	    std::strcmp("1",     word.c_str()) == 0)
	{
		*value = true;
		return true;
	}

	if (::strcasecmp("no",    word.c_str()) == 0 or
	    ::strcasecmp("off",   word.c_str()) == 0 or
	    ::strcasecmp("false", word.c_str()) == 0 or
	    std::strcmp("0",      word.c_str()) == 0)
	{
		*value = false;
		return true;
	}

	return false;
}


} // unnamed namepsace


bool ValueListMap::getBool(const std::string &key, const bool default_value) const
{
	const std::list<std::string> &values(getValues(key));
	if (values.empty())
		return default_value;
	else {
		bool value;
		if (not ConvertStringToBool(values.front(), &value))
			throw Exception("in ValueListMap::getBool: can't convert \"" + values.front()
						 + "\" to a boolean value (key: " + key + ") (1)!");
		else
			return value;
	}
}


bool ValueListMap::getBool(const std::string &key) const
{
	const std::list<std::string> &values(getValues(key));
	if (values.size() == 1) {
		bool value;
		if (not ConvertStringToBool(values.front(), &value))
			throw Exception("in ValueListMap::getBool: can't convert \"" + values.front()
						 + "\" to a boolean value (key: " + key + ") (2)!");
		else
			return value;
	}
	else if (values.size() > 1) {
		throw Exception("in ValueListMap::getBool: expected a single query "
					 "argument with \"" + key + "=...\"!");
		return false; // Keep the compiler happy!
	}
	else
		return false;
}


unsigned ValueListMap::getUnsigned(const std::string &key) const
{
	const std::list<std::string> &arg_list = getValues(key);
	if (arg_list.size() == 1) {
		long number;
		if (not StringUtil::ToNumber(arg_list.front(), &number))
			throw Exception("in ValueListMap::getUnsigned: failed to convert value of \"" + key
						 + "\" (" + arg_list.front() + ") to a number!");
		return number;
	}
	else if (arg_list.size() > 1)
		throw Exception("in ValueListMap::getUnsigned: expected a single query "
					 "argument with \"" + key + "=...\"!");
	else
		throw Exception("in ValueListMap::getUnsigned: entry for \"" + key + "\" is missing!");

	return 0; // Keep the compiler happy!
}


unsigned ValueListMap::getUnsigned(const std::string &key, const unsigned default_value) const
{
	const std::list<std::string> &values = getValues(key);
	if (values.empty())
		return default_value;

	unsigned n;
	if (std::sscanf(values.front().c_str(), "%u", &n) != 1)
		throw Exception("in ValueListMap::getUnsigned: entry for \"" + key + "\" is not an "
					 "unsigned number!");

	return n;
}


double ValueListMap::getDouble(const std::string &key) const
{

	const std::list<std::string> &arg_list = getValues(key);
	if (arg_list.size() == 1) {
		return StringUtil::ToDouble(arg_list.front());
	}
	else if (arg_list.size() > 1)
		throw Exception("in ValueListMap::getDouble:"
					 " expected a single query argument"
					 " with \"" + key + "=...\"!");
	else
		throw Exception("in ValueListMap::getDouble: entry for \"" + key + "\" is missing!");
	return 0.0; // Keep the compiler happy!
}


// ValueListMap::clear -- Remove all values matching a key from the map.
//
void ValueListMap::clear(const std::string &key)
{
	iterator map_iter(value_to_list_map_.find(key));
	if (map_iter != value_to_list_map_.end())
		value_to_list_map_.erase(map_iter);
}


// ValueListMap::getKeys -- Get the keys of this map.
//
std::list<std::string> ValueListMap::getKeys(const std::string &reg_exp) const
{
	const PerlCompatRegExp perl_compat_reg_exp(reg_exp);
	std::list<std::string> result;
	for (const_iterator iter(begin()); iter != end(); ++iter) {
		if (perl_compat_reg_exp.match(iter->first))
			result.push_back(iter->first);
	}

	return result;

}


// ValueListMap::insert -- Insert a value into the map.
//
void ValueListMap::insert(const std::string &key, const std::string &value)
{
	// Is this variable is already in our map?
	iterator map_iter(value_to_list_map_.find(key));

	if (map_iter == value_to_list_map_.end()) {
		// A "key" we have not seen before:
		std::list<std::string> new_list;
		new_list.push_back(value);
		value_to_list_map_.insert(std::pair<std::string,std::list<std::string> >(key, new_list));
	}
	else {
		// The variable is already in the map:
		map_iter->second.push_back(value);
	}
}


void ValueListMap::insert(const std::string &key, const int value, const unsigned radix, const int width)
{
	insert(key, StringUtil::ToString(value, radix, width));
}


void ValueListMap::insert(const std::string &key, const long value, const unsigned radix, const int width)
{
	insert(key, StringUtil::ToString(value, radix, width));
}


void ValueListMap::insert(const std::string &key, const long long value, const unsigned radix, const int width)
{
	insert(key, StringUtil::ToString(value, radix, width));
}


void ValueListMap::insert(const std::string &key, const unsigned value, const unsigned radix, const int width)
{
	insert(key, StringUtil::ToString(value, radix, width));
}


void ValueListMap::insert(const std::string &key, const unsigned long value, const unsigned radix, const int width)
{
	insert(key, StringUtil::ToString(value, radix, width));
}


void ValueListMap::insert(const std::string &key, const unsigned long long value, const unsigned radix, const int width)
{
	insert(key, StringUtil::ToString(value, radix, width));
}


// ValueListMap::insert -- Insert multiple values into the map, replacing the existing values.
//
void ValueListMap::insert(const std::string &key, const std::list<std::string> &value_list)
{
	iterator map_iter(value_to_list_map_.find(key));

	// If we have not seen the key before, insert the list:
	if (map_iter == value_to_list_map_.end())
		value_to_list_map_.insert(std::pair<std::string,std::list<std::string> >(key, value_list));
	// Otherwise, replace the existing list:
	else
		map_iter->second = value_list;
}


std::string ValueListMap::toString() const
{
	std::string string_rep;
	for (const_iterator entry(begin()); entry != end(); ++entry) {
		string_rep += StringUtil::Escape('\\', "=", entry->first) + "=";
		for (std::list<std::string>::const_iterator value(entry->second.begin()); value != entry->second.end(); ++value) {
			if (value != entry->second.begin())
			      string_rep += ",";
			string_rep += StringUtil::Escape('\\', ",", *value);
		}
		string_rep += '\n';
	}

	return string_rep;
}
