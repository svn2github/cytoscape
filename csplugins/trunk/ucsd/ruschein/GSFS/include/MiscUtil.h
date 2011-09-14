/** \file    MiscUtil.h
 *  \brief   Declarations of miscellaneous utility functions.
 *  \author  Dr. Johannes Ruscheinski
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

#ifndef MISC_UTIL_H
#define MISC_UTIL_H


#include <algorithm>
#include <fstream>
#include <list>
#include <set>
#include <vector>
#include <stdexcept>
#include <string>
#include <cerrno>
#include <clocale>
#include <cstdlib>
#include <GnuHash.h>
#include <StringUtil.h>


namespace MiscUtil {


/** \brief  Convert a numeric value between 0 and 15 to a hex digit.
 *  \param  value  A number between 0 and 15.
 *  \return The character representing the hexadecimal value.
 */
char HexDigit(const unsigned value);


/** \brief  A safe (i.e. throws on error) wrapper around getenv(3).
 *  \param  name  The name of an environment variable.
 *  \return The value of the environment variable if set (else throws an exception).
 */
std::string GetEnv(const char * const name);
inline std::string GetEnv(const std::string &name) { return GetEnv(name.c_str()); }


/** \brief  A safe wrapper around getenv(3).
 *  \param  name  The name of an environment variable.
 *  \return The value of the environment variable if set otherwise the empty string.
 */
std::string SafeGetEnv(const char * const name);
inline std::string SafeGetEnv(const std::string &name) { return SafeGetEnv(name.c_str()); }


/** \brief Checks for existence of an environment variable
 *  \param  name  The name of an environment variable.
 *  \return true of the variable exists, false otherwise.
 */
bool EnvironmentVariableExists(const std::string &name);


/** \brief A wrapper around setenv(3).
 *  \param name      The name of the environment variable.
 *  \param value     The value of the environment variable.
 *  \param overwrite Whether or not the current value for the given environment variable may be overwritten.
 *  \return          True if the addition of "name=value" is successful.
 */
void SetEnv(const std::string &name, const std::string &value, const bool overwrite = true);


/** \brief  Converts a "char" to a std::string of zeroes and ones.
 */
std::string CharToBitString(char ch);


/** \brief  Converts a std::string of 8-bit bytes to a std::string of zeroes and ones.
 *  \param  s          The string to convert.
 *  \param  separator  How to separate the converted representations of each byte.
 *  \return The converted "bit" string.
 */
std::string StringToBitString(const std::string &s, const std::string &separator = "");


/** \brief  Do a hex dump for memory starting at "pointer"
 *  \param  pointer   The pointer to the block of memory to be printed.
 *  \param  num_rows  How many rows of output to print.
 *  \return The output string
 */
std::string HexPrint(const int * const pointer, unsigned num_rows);


/** \brief  Converts a std::list to a std::set.
 *  \param  list  The list to be converted.
 *  \return The set.
 */
template<typename T> inline std::set<T> ListToSet(const std::list<T> &list)
{
	typename std::set<T> set;
	for (typename std::list<T>::const_iterator element(list.begin()); element != list.end(); ++element)
		set.insert(*element);

	return set;
}


/** \brief  Converts a std::set to a std::list.
 *  \param  set  The set to be converted.
 *  \return A list containing all members of the set
 */
template<typename T> inline std::list<T> SetToList(const std::set<T> &set)
{
	typename std::list<T> list;
	for (typename std::set<T>::const_iterator element(set.begin()); element != set.end(); ++element)
		list.push_back(*element);

	return list;
}


/** \brief  Converts a std::vector to a std::set.
 *  \param  vector  The vector to be converted.
 *  \return The set.
 */
template<typename T> inline std::set<T> VectorToSet(const std::vector<T> &vector)
{
	typename std::set<T> set;
	for (typename std::vector<T>::const_iterator element(vector.begin()); element != vector.end(); ++element)
		set.insert(*element);

	return set;
}


/** \brief  Converts a std::set to a std::vector.
 *  \param  set  The set to be converted.
 *  \return A vector containing all members of the set.
 */
template<typename T> inline std::vector<T> SetToVector(const std::set<T> &set)
{
	typename std::vector<T> vector;
	for (typename std::set<T>::const_iterator element(set.begin()); element != set.end(); ++element)
		vector.push_back(*element);

	return vector;
}


/** \brief  Converts a GNU_HASH_SET to a std::vector.
 *  \param  gnu_set The set to be converted.
 *  \return A vector containing all members of GNU_HASH_SET.
 */
template<typename T> inline std::vector<T> GnuHashSetToVector(const GNU_HASH_SET<T> &gnu_set)
{
	typename std::vector<T> vector;
	for (typename GNU_HASH_SET<T>::const_iterator element(gnu_set.begin()); element != gnu_set.end(); ++element)
		vector.push_back(*element);

	return vector;
}


/** \brief  Converts a std::vector to a GNU_HASH_SET.
 *  \param  vector The vector to be converted.
 *  \return A GNU_HASH_SET containing the unique elements of the std::vector vector.
 *  \note   This function will remove duplicate elements in std::vector.
 */
template <typename T> inline GNU_HASH_SET<T> VectorToGnuHashSet(const std::vector<T> &vector)
{
	typename GNU_HASH_SET<T> gnu_set;
	for (typename std::vector<T>::const_iterator element(vector.begin()); element != vector.end(); ++element)
		gnu_set.insert(*element);

	return gnu_set;
}


/** \brief  Converts a std::vector to a std::list.
 *  \param  vector  The vector to be converted.
 *  \return A list containing all members of the vector.
 */
template<typename T> inline std::list<T> VectorToList(const std::vector<T> &vector)
{
	typename std::list<T> list;
	for (typename std::vector<T>::const_iterator element(vector.begin()); element != vector.end(); ++element)
		list.push_back(*element);

	return list;
}


/** \brief  Converts a std::list to a std::vector.
 *  \param  list  The list to be converted.
 *  \return A vector containing all members of the list.
 */
template<typename T> inline std::vector<T> ListToVector(const std::list<T> &list)
{
	typename std::vector<T> vector;
	for (typename std::list<T>::const_iterator element(list.begin()); element != list.end(); ++element)
		vector.push_back(*element);

	return vector;
}


struct MemInfo {
	unsigned long mem_total_;
	unsigned long mem_free_;
	unsigned long buffers_;
	unsigned long cached_;
	unsigned long swap_cached_;
	unsigned long active_;
	unsigned long inactive_;
public:
	std::string toString() const;
};


/** \brief  Retrieve some stats from /proc/meminfo.
 *  \param  mem_info  Where to store the stats in kB.
 *  \return True if the information could be retrieved, else false.
 */
bool GetMemInfo(MemInfo * const mem_info);


/** Returns a locally (across processes etc. unique ID). */
std::string GetLocallyUniqueID();


/** Returns a globally (across machines, processes etc. unique ID). */
std::string GetGloballyUniqueID();


/** Remove consecutive duplicate entries from a container.  If "sort" is true, we assume that we have to sort the
    contents first. */
template<typename ContainerType> void RemoveDups(ContainerType * const container, const bool sort = true)
{
	if (sort)
		std::sort(container->begin(), container->end());
	std::unique(container->begin(), container->end());
}


/** Generates a backtrace in a file in /tmp starting with the name of the program that is running
    (Currently badly broken!!!) . */
void BackTrace(const std::string &message = "");


/** Returns the number of set bits in "word". */
template<typename WordType> inline unsigned CountBits(WordType word)
{
	unsigned count(0);
	for (/* Empty. */; word != 0; word >>= 1) {
		if (word & 1)
			++count;
	}

	return count;
}


/** Returns the number of bits that differ between "word1" and "word2." */
template<typename WordType> inline unsigned BitDiffCount(const WordType &word1, const WordType &word2)
{
	return CountBits(word1 ^ word2);
}


/** Returns "amount" formatted according to the current locale's national currency format. */
std::string GetCurrencyString(const double amount);


/** Returns the number of CPUs on the current computer system. */
unsigned GetCpuCount();


enum LoadAveragingInterval { ONE_MINUTE, FIVE_MINUTES, FIFTEEN_MINUTES };
double GetLoadAverage(const LoadAveragingInterval load_averaging_interval = ONE_MINUTE);


/** \brief  Finds both minimum and maximum of the elements of a container.
 *  \param  first      The start of the range we'd like to scan.
 *  \param  last       One past the end of the range we'd like to scan.
 *  \param  min        The minimum element of the container.
 *  \param  max        The maximum element of the container.
 */
template<typename Element, typename ForwardIterator>
void MinMax(const ForwardIterator &first, const ForwardIterator &last, Element * const min, Element * const max)
{
	if (unlikely(first != last))
		throw Exception("in MiscUtil::MinMax: can't find the minimum and maximum of an empty container!");

	ForwardIterator element(first);
	Element min_so_far(*element);
	Element max_so_far(*element);
	for (++element; element < last; ++element) {
		if (*element < min_so_far)
			min_so_far = *element;
		else if (*element > max_so_far)
			max_so_far = *element;
	}

	*min = min_so_far;
	*max = max_so_far;
}


/** \brief  Finds both minimum and maximum of the elements of a container.
 *  \param  container  The container whose minimum and maximum we desire.
 *  \param  min        The minimum element of the container.
 *  \param  max        The maximum element of the container.
 */
template<typename Container>
inline void MinMax(const Container &container, typename Container::value_type * const min,
		   typename Container::value_type * const max)
{
	MinMax(container.begin(), container.end(), min, max);
}


/**
 * \brief Attempts to read length "length" of memory at address "address". Will obviously segfault if there is
 *        a problem. This is really only useful during debugging memory access problems where you want a crash
 *        to show itself as soon as possible. This can later be enhanced with kernel memory access functions
 *        to not simply crash.
 * \param address  The memory address to check.
 * \param length   How much memory at address to attempt to access.
 */
bool IsValidReadMemory(const void * const address, const size_t length = 1);


/** \brief Displays a memory buffer with text on left and hex values on the right.
 *  \param buffer        The area of memory to display
 *  \param amount        How much of this area to display
 *  \param outer_offset  Add this amount to the displayed offset when displaying. By default
 *                       the first byte's offset of the dump will be assumed to be 0, but you might be displaying some area of
 *	 		 memory, or some offset in a file where you want the reader to know where in
 *			 the source area the buffer was taken from.
 *  \param width         How many characters to display in each line, default 16.
 *  \return              Returns the hexdump as a std::string.
 */
std::string HexDump(const char * const buffer, const unsigned amount, const unsigned outer_offset = 0, const unsigned width = 16);


/** Returns a Unix-style encrypted password using a random 2-character salt (unless one has been explicitly provided) and crypt(3). */
std::string UnixCrypt(const std::string &unencrypted_password, const std::string &salt = "");


enum AddressRoundingMode { UP, DOWN };


/* Returns "address" rounded (either up or down) to the nearest multiple of a systems page size. */
char *GetNearestAddressInIncrementsOfPageSize(char * const address, const AddressRoundingMode address_rounding_mode = UP);


/* Returns "offset" rounded (either up or down) to the nearest multiple of a systems page size. */
off_t GetNearestFileOffsetInIncrementsOfPageSize(const off_t offset, const AddressRoundingMode address_rounding_mode = DOWN);


/** \brief  Implements a boolean exclusive or. */
inline bool Xor(const bool &arg1, const bool &arg2)
{
	return arg1 xor arg2;
}


/**
Standardized verbosity settings. Code that outputs messages should tag the message with a priority, a verbosity level. Then, when the program is run the
user can specify a verbosity level and the output that is tagged with that level, or below will be output and output labelled higher will be squelched.
Each higher number also implies the lower number. For example if VERBOSITY_NORMAL is set, you would output errors and warnings also.
*/
enum Verbosity { VERBOSITY_SILENT     = 0,   //< If you want to designate that the program be entirely without output.
		 VERBOSITY_ERRORS     = 1,   //< Only output fatal errors.
		 VERBOSITY_WARNINGS   = 2,   //< Only output warnings and fatal errors.
		 VERBOSITY_NORMAL     = 3,   //< Normal output, which implies that errors and warnings are also output.
		 VERBOSITY_CHATTY     = 4,   //< Extra output that might be useful for a normal user.
		 VERBOSITY_DEBUG      = 5,   //< Messages only intended for programmer use in debugging the program.
		 VERBOSITY_EVERYTHING = 6 }; //< Output everything without limitation.


/** \brief   Gets an estimate of how much stack space has been used in bytes since StackUsage() was called the first time.
 */
unsigned long StackUsage();


/** \brief  Eliminates subphrases from a list of phrases.
 *  \param  strings         The list of "phrases" to be processed.
 *  \param  case_sensitive  If true, phrases comparisons will be case sensitive, otherwise they will be case insensitive.
 *  \note   Duplicate sentences are eliminated and the relative order of the retained phrases will be retained.
 */
void SubstringMerge(std::vector<std::string> * const strings, const bool case_sensitive = false);


// BackTraceLCS -- reconstruct the actual indices (in reverse order) of an LCS of "s1" and "s2" as a helper function for CalculateLongestCommonSubsequence.
//
template<class Sequence> void BackTraceLCS(const unsigned table[], const Sequence &s1, const Sequence &s2,
					   const unsigned i, const unsigned j, std::vector<unsigned> * const common_indices)
{
	if (i == 0 or j == 0)
		return;

	if (s1[i - 1] == s2[j - 1]) {
		common_indices->push_back(i - 1);
		BackTraceLCS(table, s1, s2, i - 1, j - 1, common_indices);
	}
	else if (table[i * (s2.size() + 1) + (j - 1)] == table[(i - 1) * (s2.size() + 1) + j])
		BackTraceLCS(table, s1, s2, i, j - 1, common_indices);
	else
		BackTraceLCS(table, s1, s2, i - 1, j, common_indices);
}


/** \brief  Returns length(LCS(s1, s2)).
 *  \param  s1              The first sequence to compare.
 *  \param  s2              The second sequence to compare.
 *  \param  common_indices  0-based indices into "s1" for construction of an actual LCS.
 *  \note   Algorithm implementation taken from http://en.wikipedia.org/wiki/Longest_common_subsequence_problem#Example
 */
template<class Sequence> unsigned CalculateLongestCommonSubsequence(const Sequence &s1, const Sequence &s2,
								    std::vector<unsigned> * const common_indices = NULL)
{
	unsigned *table(new unsigned[(s1.size() + 1) * (s2.size() + 1)]);
	for (unsigned i(0); i <= s1.size(); ++i)
		table[i * (s2.size() + 1) + 0] = 0;
	for (unsigned j(0); j <= s2.size(); ++j)
		table[0 + j] = 0;

	for (unsigned i(1); i <= s1.size(); ++i) {
		for (unsigned j(1); j <= s2.size(); ++j) {
			if (s1[i - 1] == s2[j - 1])
				table[i * (s2.size() + 1) + j] = table[(i - 1) * (s2.size() + 1) + (j - 1)] + 1;
			else
				table[i * (s2.size() + 1) + j] = std::max(table[i * (s2.size() + 1) + (j - 1)], table[(i - 1) * (s2.size() + 1) + j]);
		}
	}

	const unsigned lcs_length(table[s1.size() * (s2.size() + 1) + s2.size()]);

	// Reconstruct the actual sequence?
	if (common_indices != NULL) {
		common_indices->clear();
		BackTraceLCS(table, s1, s2, s1.size(), s2.size(), common_indices);
		std::reverse(common_indices->begin(), common_indices->end());
	}

	delete [] table;

	return lcs_length;
}


/** \brief  Returns the set of all characters for which isspace() returns true as well as the no-break space.
 *  \note   Determination for set inclusion is made based on what's in StringUtil::WHITE_SPACE.
 *  \note   This function is thread safe.
 */
const std::set<char> &GetWhiteSpaceSet();


template<typename UnsignedIntegerType> inline UnsignedIntegerType NaturalBinaryToGrayCode(const UnsignedIntegerType standard_binary)
{
	return standard_binary ^ (standard_binary >> 1u);
}


template<typename UnsignedIntegerType> inline UnsignedIntegerType GrayCodeToNaturalBinary(const UnsignedIntegerType gray_code_binary)
{
	UnsignedIntegerType standard_binary(gray_code_binary);
	for (unsigned shift(sizeof(UnsignedIntegerType) * BITSPERBYTE / 2); shift > 0; shift >>= 1u)
		standard_binary ^= (standard_binary >> shift);

	return standard_binary;
}


/** Converts an fd_set to a std::vector of file descriptors. */
void FdSetToFdVector(const fd_set &set, std::vector<int> * const vector);


struct XYCutNode {
	double x0_, y0_, x1_, y1_;
	const void *data_;
public:
	XYCutNode(const double x0, const double y0, const double x1, const double y1, const void *data)
		: x0_(x0), y0_(y0), x1_(x1), y1_(y1), data_(data) { }
	bool operator<(const XYCutNode &rhs) const { return this < &rhs; }
};


class XYCutNodeSet: public std::set<const XYCutNode *> { };


class Block {
	XYCutNodeSet cut_node_set_;
	double x_min_, x_max_, y_min_, y_max_;
public:
	class const_iterator {
		friend class Block;
		XYCutNodeSet::const_iterator iter_;
	public:
		bool operator!=(const const_iterator &rhs) const { return iter_ != rhs.iter_; }
		void operator++() { ++iter_; }
		const XYCutNode &operator*() { return **iter_; }
	private:
		explicit const_iterator(XYCutNodeSet::const_iterator iter): iter_(iter) { }
	};
public:
	Block(): x_min_(0.0), x_max_(0.0), y_min_(0.0), y_max_(0.0) { }
	std::string getName() const;
	size_t size() const { return cut_node_set_.size(); }
	double getXMin() const { return x_min_; }
	double getXMax() const { return x_max_; }
	double getYMin() const { return y_min_; }
	double getYMax() const { return y_max_; }
	double getHeight() const { return y_max_ - y_min_; }
	double getWidth() const { return x_max_ - x_min_; }
	void addCutNode(const XYCutNode &cut_node);
	const XYCutNodeSet &getCutNodeSet() const { return cut_node_set_; }
	const_iterator begin() const { return const_iterator(cut_node_set_.begin()); }
	const_iterator end() const { return const_iterator(cut_node_set_.end()); }
	bool isAbove(const Block &other) const { return getYMax() <= other.getYMin(); }
	bool isToTheRightOf(const Block &other) const { return getXMin() >= other.getXMax(); }
};


} // namespace MiscUtil


#endif // ifndef MISC_UTIL_H
