/** \file    GnuHash.h
 *  \brief   Attempts to make hash, hash_map and hash_set work with various versions of g++.
 *  \author  Dr. Johannes Ruscheinski
 */

/*
 *  Copyright 2002-2009 Project iVia.
 *  Copyright 2002-2009 The Regents of The University of California.
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

#ifndef GNU_HASH_H
#define GNU_HASH_H


#include <climits>
#include <stdint.h>
#if __GNUC__ < 3 || (__GNUC__ == 3 && __GNUC_MINOR__ == 0)
#       include <hash_map>
#       include <hash_set>
#elif __GNUC__ == 3 || (__GNUC__ == 4 && __GNUC_MINOR__ < 3)
#       include <ext/hash_set>
#       include <ext/hash_map>
#else
#       include <tr1/unordered_set>
#       include <tr1/unordered_map>
#endif
#include <StringUtil.h>


// As of GCC 3.1, hash, hash_map and hash_set are in __gnu_cxx namespace instead of std.
// The reason for it is that hash{,_map,_set} are not part of the C++ standard.
#if __GNUC__ < 3 || (__GNUC__ == 3 && __GNUC_MINOR__ == 0)
#       define GNU_HASH     std::hash
#       define GNU_HASH_MAP std::hash_map
#       define GNU_HASH_SET std::hash_set
#elif __GNUC__ == 3 || (__GNUC__ == 4 && __GNUC_MINOR__ < 3)
#       define GNU_HASH     __gnu_cxx::hash
#       define GNU_HASH_MAP __gnu_cxx::hash_map
#       define GNU_HASH_SET __gnu_cxx::hash_set
#else
#       define GNU_HASH     std::tr1::hash
#       define GNU_HASH_MAP std::tr1::unordered_map
#       define GNU_HASH_SET std::tr1::unordered_set
#endif


#if __GNUC__ < 3 || (__GNUC__ == 3 && __GNUC_MINOR__ == 0)
#       define OPEN_HASH_NAMESPACE namespace std {
#       define CLOSE_HASH_NAMESPACE }
#elif __GNUC__ == 3 || (__GNUC__ == 4 && __GNUC_MINOR__ < 3)
#       define OPEN_HASH_NAMESPACE namespace __gnu_cxx  {
#       define CLOSE_HASH_NAMESPACE }
#else
#       define OPEN_HASH_NAMESPACE namespace std {  namespace tr1 {
#       define CLOSE_HASH_NAMESPACE }}
#endif


OPEN_HASH_NAMESPACE


#if __GNUC__ < 4 || (__GNUC__ == 4 && __GNUC_MINOR__ < 3)
template<> struct hash<std::string> {
public:
	hash() { }
	size_t operator()(const std::string &s) const { return StringUtil::SuperFastHash(s); }
};
#endif


#ifndef LONG_BIT
#       error LONG_BIT must be defined!
#endif
#if LONG_BIT != 64 && (__GNUC__ < 4 || (__GNUC__ == 4 and  __GNUC_MINOR__ < 3))
template<> struct hash<uint64_t> {
public:
	size_t operator()(const uint64_t &n) const
		{
			if (sizeof(uint64_t) == sizeof(size_t))
				return n;
			else
				return static_cast<size_t>(n) ^ static_cast<size_t>(n >> 32u);
		}
};
#endif // LONG_BIT != 64


#if __GNUC__ < 4 || (__GNUC__ == 4 && __GNUC_MINOR__ < 3)
template<typename Type> struct hash<Type *> {
	typedef Type *TypePtr;
public:
	hash() { }
	size_t operator()(const TypePtr &p) const { return hash<size_t>()(size_t(p)); }
};
#endif


#if __GNUC__ > 4 || (__GNUC__ == 4 && __GNUC_MINOR__ >= 3)
template<> struct hash<char *> {
      size_t operator()(const char *s) const { return StringUtil::SuperFastHash(s); }
};
template<> struct hash<const char *> {
      size_t operator()(const char *s) const { return StringUtil::SuperFastHash(s); }
};
#endif


#if __GNUC__ < 4 || (__GNUC__ == 4 && __GNUC_MINOR__ < 3)
#       if LONG_BIT != 64
template<> struct hash<double> {
public:
	hash() { }
	size_t operator()(const double &d) const
	{
		union {
			double d_;
			size_t st_[2];
		} u;
		u.d_ = d; return u.st_[0] ^ u.st_[1];
	}
};
#       else
template<> struct hash<double> {
public:
	hash() { }
	size_t operator()(const double &d) const
	{
		union {
			double d_;
			size_t st_;
		} u;
		u.d_ = d; return u.st_;
	}
};
#       endif
#endif


CLOSE_HASH_NAMESPACE


#endif // ifndef GNU_HASH_H
