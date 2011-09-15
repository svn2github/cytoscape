/** \file    Collection.h
 *  \brief   This is a container adapter which begins to homogenize STL container functionality and adds some functionality which makes STL containers
 *           easier to use.
 *  \author  Mr. Walter Howard
 *  \author  Dr. Johannes Ruscheinski
 */

/*
 *  Copyright 2007 Project iVia.
 *  Copyright 2007 The Regents of The University of California.
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

#ifndef COLLECTION_H
#define COLLECTION_H


#include <algorithm>
#include <iterator>
#include <map>
#include <set>
#include <string>
#include <string.h>
#include <File.h>
#include <MathUtil.h>
#include <PerlCompatRegExp.h>
#include <StlHelpers.h>


#ifdef __MACH__
#      define strdupa(A) ((char*)strcpy(alloca(strlen(A)+1), A))
#endif


/** \class  Functor
 *  \brief  Converts any function or object that has operator() into a functor.
 */
template <typename FunctorReturnType, typename CallableType> class Functor
{
	/** This variable can hold any entity that can be followed by (). */
	CallableType callable_;

public:
	/** This typedef is used to parameterize the return type for the CallableType. Sometimes this is needed by external
	    callers who don't have access to the FunctorReturnType template parameter.
	*/
	typedef FunctorReturnType ReturnType;


	/** \brief  This is the constructor.
	    \param  callable  This is the CallableType we will be turning into a functor.
	*/
	Functor(CallableType callable): callable_(callable) { }


	/** \brief  This is the function that executes the Functor as if it were a function for functors that take no arguments.
	*/
	ReturnType operator()() const
	{
		return callable_();
	}


	/** \brief  This is the function that executes the Functor as if it were a function for functors that take 1 argument.
	 *  \param  arg1  This is the argument that is passed into the functor.
	 */
	template <typename Arg1> ReturnType operator()(const Arg1& arg1) const
	{
		return callable_(arg1);
	}


	/** \brief  This is the function that executes the Functor as if it were a function for functors that take 2 arguments.
	 *  \param  arg1  This is argument1 that is passed into the functor.
	 *  \param  arg2  This is argument2 that is passed into the functor.
	 */
	template <typename Arg1, typename Arg2> ReturnType operator()(const Arg1& arg1, const Arg2& arg2) const
	{
		return callable_(arg1, arg2);
	}


	/**
	 *  Follow the patterns above to implement functors that take more than 2 arguments.
	 */
};


/** \brief   Function which constructs a Functor based upon a c++ class type object passed in "callable_type". Using this you don't have to specify the Functor
 *           template argument explicitly.
 *  \param   callable_type  This specifies the function or callable type you want to turn into a Functor.
 *  \return  This function returns a Functor based upon callable_type.
*/
template <typename FunctorType> Functor<typename FunctorType::ReturnType, FunctorType> MakeFunctor(FunctorType callable_type)
{
	return callable_type;
}


/** \brief  Function which constructs a Functor based upon a function pointer that takes no arguments. Using this you don't have to specify the
            Functor template argument explicitly.
    \param  functor The function or callable type you want to turn into a Functor.
    \return A Functor based upon fun.
*/
template <typename ReturnType> Functor<ReturnType, ReturnType (*)()> MakeFunctor(ReturnType (*callable_type)())
{
	return callable_type;
}


/** \brief  Function which constructs a Functor based upon a function pointer that takes 1 argument. Using this you don't have to specify the
            Functor template argument explicitly.
    \param  fun The function or callable type you want to turn into a Functor.
    \return A Functor based upon FunctorType.
*/
template <typename ReturnType, typename Arg1> Functor<ReturnType, ReturnType (*)(Arg1)> MakeFunctor(ReturnType (*callable_type)(Arg1))
{
	return callable_type;
}


/** \brief   Create an STL container by many different constructors, and provide some member functions that the STL didn't include (like operator= across
 *           containers).
 *  \param   StlContainerBase  This specifies which STL container type to build the collection from, example Collection<std::map<int, double> > mycollection;
 *  \param   Converter         Functor class which provides operator() which converts items being inserted into the Collection from their type to the
 *                             Collection's type. It is applied to all elements being inserted into the Collection. It lets you transfer elements from
 *                             external containers into this Collection easily.  The Converter type should have an operator() which takes a
 *                             parameter of the type from the outside type and returns an element of the Collection type. So if you are making a
 *                             collection of ints and want to construct it from a collection of strings, like a text file, you could create your
 *                             collection like this: Collection<std::vector<int>, atoi> Numbers(listofstrings); This would create a Collection based
 *                             on an std::vector of ints, and construct it but as its constructed, convert each element in listofstrings to an int
 *                             before inserting it.  IMPORTANT: The class AutoConvert is the default here. It can automatically convert to and from most
 *                             built in types and std::string. You may never need to use any other.
 * \note     Examples: Create a collection of lines from a larger string by splitting on \\r or \\n
 *           CollectionByDelimitedText<std::vector<std::string> > > * avector(stringtosplit, "\\r\\n");
 * \note     This was designed mostly to convert from text strings into containers. In general, most of the classes below are constructed from a
 *           source string. The variation is in the way the string is decomposed into the collection. By splitting on words, by splitting on
 *           characters, by splitting on Regex expressions. If "split" or "delimit" is in the name the string is decomposed into elements
 *           that do not match delimiters, a sort of negative match, "If it isn't a divider, we want it". If "collect" is in the name, the
 *           string is decomposed by positive examples, parts of the string that match the Regular expression we are looking for.
 *
 *           You can use a Collection wherever you can use an std::container of the base type. A Collection IS-A std::list, std::map etc.
 */
template <typename StlContainerBase = std::vector<std::string>, typename Converter = AutoConvert>  class Collection: public StlContainerBase {
public:
	typedef typename StlContainerBase::value_type ElementType;


	// The instance of Converter which actually does the conversions.
	Converter converter_;


	/** \brief  This inserts an element into the Collection. We use this instead of inherited insert because conversion occurs during insertion and we
	 *          need to perform that conversion here. You can still use the insert that is inherited from the STL container but you won't get auto
	 *          conversion to the container type.
	 *  \param  insertee  This must be the element to insert.
	 *  \note   This bears explanation. We receive a string, insertee, but the container might not be a container of strings. The Convert class
	 *	    senses the type conversion needed, and does it. This is how for example, you can collect a text file full of numbers directly into
	 *	    a vector of ints. The conversion is performed by a functor you provide though there is a default one called AutoConvert.
	 */
	template <typename Element> void insertElement(const Element &insertee)
	{
		this->insert(this->end(), converter_(insertee));
	}


	/** \brief We need a special insertElement for pairs that want AutoConversion because pairs have two items to be converted and may need two
	 *         Converters.  Use the StlHelpers::PairFromString class which lets you set a Conversion class of its own, prior to inserting
	 *         into this Collection.
         *  \param insertee This must be the PairFromString object to insert.
	 */
	template <typename First, typename Second, typename PairConverter>
	void insertElement(const StlHelpers::PairFromString<First, Second, PairConverter> &insertee)
	{
		this->insert(this->end(), insertee);
	}


	/** \brief Construct by inserting multiple elements.
	 *  \param count How many items follow.
	 *  \param ...   The items which will be inserted into the new Collection
	 *  \note  Example: const Collection<std::vector<std::string> > > number_list(5, "first", "second", "third", "fourth", "fifth")
	 */
	template <typename ArgType> Collection(unsigned count, const ArgType first, ...)
	{
		va_list args;
		va_start(args, first);
		
		insertElement(first);
		--count;

		while(count-- > 0)
		        insertElement(va_arg(args, ArgType));

		va_end(args);
	}


	/** \brief  Set contents of this Collection from any other container that has iterators and compatible (or convertible) members.
	 *  \param  rhs    "Right Hand Side" of operator=. The container from which we will set our own contents.
	 *  \return This returns a non const reference to this container.
	 */
	template <typename OtherContainer> Collection &operator=(const OtherContainer &rhs)
	{
		this->clear();
		addContents(rhs.begin(), rhs.end());
		return *this;
	}


	/** \brief  Construct this container and fill it with items from another container, anything that has STL syntax iterators.
	 *  \param  other    The other container from which we will draw our contents.
	 */
	template <typename StlContainer> Collection(const StlContainer &other, Converter converter = Converter())
		: converter_(converter)
	{
		operator=(other);
	}


	/** \brief  Default constructor.
	 */
	Collection(Converter converter = Converter()) : converter_(converter) { }


	/** \brief  Create, populate and return a Collection by autodetecting the type of STL container to base it on.
	 *  \param  other  The source container used to create, and populate this one.
         *  \return A Collection of the same type and contents as other.
	 */
	template <typename StlContainer, typename ElementConverter> Collection<StlContainer, ElementConverter> MakeCollection(const StlContainer &other)
	{
		Collection<StlContainer, Converter> temp(other);
		return temp;
	}


	/** \brief  Construct a Collection from two source iterators.
	 *  \param  first   An iterator where to start collection from the source container.
	 *  \param  last    An iterator where to stop collection from the source container.
	 *  \param  max     Stop after this many items have been collected.
         *  \param  converter The
	 */
	template <typename Iterator> explicit Collection(const Iterator &first, const Iterator &last,
							 const int max = INT_MAX, Converter converter = Converter())
		: converter_(converter)
	{
		addContents(first, last, max);
	}


	/** \brief  operator[], various forms.
	 *  \param index   Which element to get. This function will only instantiate for non-integral indices like std::strings.
	 *  \note   If the index is a non integer, refer this the the standard container operator[]. If the value is an int we support negative indices,
	 *          meaning, offset from the back of the container. This is implemented using template specialization.
	 */
	template <typename IndexType> ElementType &operator[](const IndexType &index)
	{
		return StlContainerBase::operator[](index);
	}


        /** \brief operator[] for use with unsigned indices. This can never be negative so we need not check for "from the back" elements.
         *  \param index   Which element to get. 0 is the first one. If this is >= Collection::size(), throw an exception.
	 */
	ElementType &operator[](const unsigned index)
	{
		if (index >= this->size())
		       throw Exception(StringUtil::Format("Index %u invalid at: ", index) + ErrorLocation());

		return StlContainerBase::operator[](index);
	}


	/** \brief  This is like the normal operator[] but accepts negative values. A negative index means "from the back", so, my_collection[-1] would
	 *          return the last element in the list. Too bad there is no negative 0 to make this more consistent with forward indices but ce la
	 *          vie. If the index is out of range it throws an exception.
	 *  \param  index  Which element to access.
         *  \return A non-const reference to the item of interest.
	 */
	ElementType &operator[](int index)
	{
		if (index >= 0) {
			if (static_cast<unsigned>(index) >= this->size())
			        throw Exception(StringUtil::Format("Index %d invalid: ", index) + ErrorLocation());

			return StlContainerBase::operator[](index);
		}

		for (typename StlContainerBase::reverse_iterator item(this->rbegin()); item != this->rend(); ++item) {
			if (++index >= 0)
			        return *item;
		}

		throw Exception(StringUtil::Format("Index %d invalid: ", index) + ErrorLocation());
	}
private:
	typename Collection::const_reverse_iterator increment(typename Collection::const_reverse_iterator current_position, int movement) const
	{
		int step = MathUtil::Sign(movement);
		for(;current_position != this->rend() and movement != 0; movement -= step) {
			++current_position;
			if (current_position == this->rbegin())
			          break;
		}

		return current_position;
	}


	typename Collection::const_iterator increment(typename Collection::const_iterator current_position, int movement) const
	{
		int step = MathUtil::Sign(movement);
		for(;current_position != this->end() and movement != 0; movement -= step) {
			++current_position;
			if (current_position == this->begin())
			          break;
		}

		return current_position;
	}
public:
	/** \brief This function returns a range of elements permissively, meaning, if the range is invalid return whatever
	 *         portion is valid, don't consider this an error.
	 *  \param start      The position within the collection to start the subset. If the value is negative it means start at the back of
	 *                    the string. -1 being the end element and -2 being the next to end etc.
         *  \param max_count  How many elements to collect. If the value is negative, it means to collect in the reverse direction, from back
         *                    to front.
         *  \note  Example: Collection<std::vector<std::string> > test("this is a test"); std::list<std::string> > subset =
         *         test.subset<std::list<std::string> >(1, 2)
	 *         Because it uses iterators to construct the return value, return value optimization bypasses making an extra temporary making
	 *         return by value performance neutral.
	 * \return A container of the same type as this but containing the specified subset.
	 */
	StlContainerBase subset(int start, int max_count = INT_MAX) const
	{
		// No contents? Return an empty container of the same type as *this.
		if (this->empty())
		        return Collection();

		typename Collection::const_iterator first;

		/*
		  Decide which end to "start" at (negative starts from back). Trim start value to be valid for container size. Note
		  that since the last element is -1 (there is no -0) the negative indices are 1 based, not 0 based.
		*/
		if (unlikely(start < 0)) {
			if (static_cast<unsigned>(-start) > this->size())
				start = -this->size();

		        first = increment(this->rbegin(), -start).base();
		}
		else {
			if (static_cast<unsigned>(start) >= this->size())
			        start = this->size() - 1;

		        first = increment(this->begin(), start);
		}

		if (unlikely(max_count < 0)) {
			typename Collection::const_reverse_iterator rfirst(++first);
			typename Collection::const_reverse_iterator rlast = increment(rfirst, max_count);
			return Collection(rfirst, rlast);
		}
		else {
		        typename Collection::const_iterator last = increment(first, max_count);
			return Collection(first, last);
		}
	}


	/** \brief Another way to do subset but just takes indices in parenthesis.
	 *  \param start     The position within the collection to start the subset. If the value is negative it means start at the back of
	 *                   the string. -1 being the end element and -2 being the next to end etc.
         *  \param max_count How many elements to collect. If the value is negative, it means to collect in the reverse direction, from back
         *                   to front.
         *  \note  Example: Collection<std::vector<std::string> > test("this is a test"); std::list<std::string> > subset =
         *         test.subset<std::list<std::string> >(1, 2)
	 *         Because it uses iterators to construct the return value, return value optimization bypasses making an extra temporary making
	 *         return by value performance neutral.
	 *  \return
	 */
	StlContainerBase operator()(int start, int max_count = INT_MAX) const
	{
		return subset(start, max_count);
	}


	/** \brief  Add contents from a source iterator range and return reference to self. Limit to max number of
	 *	            items. Negative max puts items into container in reverse. Accomodate first being later than last.
	 *  \param   first  Iterator of first source element.
	 *  \param   last   Iterator of last source element.
	 *  \param   max    Stop inserting after this many items. Defaults to INT_MAX (some ungodly huge number).
	 *  \return  Reference to self, *this.
	 */
	template <typename Iterator> Collection &addContents(Iterator first, Iterator last, signed max = INT_MAX)
	{
		int step = MathUtil::Sign(max);

		for (Iterator item(first); max != 0 and item != last; ++item, max -= step)
		        insertElement(*item);

		return *this;
	}


	/** \brief  Add entire contents of a source container.
	 *  \param  container  The other container from where to get the items.
	 *  \return Reference to self, *this.
	 */
	template <typename OtherContainer> Collection &addContents(const OtherContainer &container)
	{
		return addContents(container.begin(), container.end());
	}


	/** \brief  Add entire contents of a source container.
	 *  \param   container The other container from where to get the items.
	 *  \return  Reference to self, *this.
	 */
	template <typename SourceContainer> Collection &operator+=(const SourceContainer &rhs)
	{
		addContents(rhs.begin(), rhs.end());
		return *this;
	}


	/** \brief  Add entire contents of a source container to this container and return a third container of the result.
	 *  \param  container he other container from where to add items.
	 *  \return A container containing both this and the rhs container.
	 */
	template <typename SourceContainer> Collection operator+(const SourceContainer &rhs) const
	{
		Collection<StlContainerBase> temp = *this;
		temp.addContents(rhs.begin(), rhs.end());
		return temp;
	}


	/** \brief  Add contents by splitting a string on delimiter characters starting from the back working forward.
	 *  \param  const_source        This be the string we be decomposing. Arrr.
	 *  \param  delimited_by_chars  These characters are delimiters and where one or more appear in the text will be where
	 *                              the text is split and they themselves will be thrown away.
	 *  \param  max                 Only split this many times. Any remaining text not split is included in the last element that
	 *                              is split off.
	 *  \return Reference to self, *this.
	 */
	Collection &addByReverseSplit(const std::string &const_source, const std::string &delimited_by_chars, int max = INT_MAX)
	{
		// We need a mutable copy of the string because the process of extraction is destructive of the original string
		const char *source(const_source.c_str());
		const char *last(source + std::strlen(source));
		const char *start = last;
		const char *delimiters = delimited_by_chars.c_str();
		for (/* initialized outside of loop */; start >= source and max <= -1; start--) {
			if (*start and ::strchr(delimiters, *start)){
				if(not ::strchr(delimiters, *(start + 1))) {
					this->insertElement(std::string(start + 1, last - start - 1));
					++max;
				}
				last = start;
			}
		}
		if (last != source and start != last)
    		        this->insertElement(std::string(start + 1, last - start - 1));

		return *this;
	}


	/** \brief  Add contents by splitting a string on delimiter characters.
	 *  \param  const_source        This be the string we be decomposing. Arrr.
	 *  \param  delimited_by_chars  These characters are delimiters and where one or more appear in the text will be where
	 *                              the text is split and they themselves will be thrown away.
	 *  \param  max                 Only split this many times. Any remaining text not split is included in the last element that
	 *                              is split off.
	 *  \return Reference to self, *this.
	 */
	Collection &addByForwardSplit(const std::string &const_source, const std::string &delimited_by_chars, int max = INT_MAX)
	{
		// We will alter but reconstitute this string so its ok to cast away const
		const char *start(const_source.c_str());
		const char *current = start;
		const char *delimiters = delimited_by_chars.c_str();
		for (/* initialized outside */; *current and max != 0; ++current) {
			if (::strchr(delimiters, *current)) {
				if (current > start and *(current - 1)) { // Not a terminator?
					this->insertElement(std::string(start, current - start));
					--max;
				}
				start = current + 1;
			}
		}

		if (start != current)
		        this->insertElement(std::string(start, current - start));

		return *this;
	}


	/** \brief  This is a constructor helper function. It determines whether to split the string in forward
	 *          or reverse, based on whether "max" is positive or negative respectively.
	 *  \param  source This is the string we will extract elements from.
         *  \param  delimited_by_chars Which characters will determine where to split the string.
	 *  \max    Stop after this many splits. If max is negative, we extract from the string starting at the back working forward.
	 *  \return A reference to *this.
	 */
	Collection &init(const std::string &source, const std::string &delimited_by_chars, int max = INT_MAX)
	{
		if (unlikely(max < 0))
			this->addByReverseSplit(source, delimited_by_chars, max);
		else
			this->addByForwardSplit(source, delimited_by_chars, max);
		return *this;
	}


	/** \brief  Add strings to the container by splitting a source string on a regular expression. The regular expression determines where the
	 *          string is split and any text that matches the regular expression is thrown away. So this basically splits the string into sections
	 *          that do not match the "regex".
	 *  \param  source  This is the string to be split.
	 *  \param  regex   This is the regular expression that determines where to split the string.
	 *  \param  max     Limit the number of splits to this. Any text remaining after the last split is include with the last element extracted.
	 *  \return Returns a refrence to *this.
	 *  \note   Unlike the other container splits, a negative max will not split in reverse. The regex library has no provision for reverse searching.
	 */
	Collection &addByForwardSplitRegex(const std::string &source, const PerlCompatRegExp &regex, unsigned max = UINT_MAX)
	{
		size_t offset(0);
		while (max-- != 0) {
			size_t found_offset(0);
			size_t found_length(0);
			if (not regex.match(source, offset, &found_offset, &found_length))
				break;
			// If there is a regex right at the beginning, don't store a string.
			if (found_offset != 0)
				this->insertElement(source.substr(offset, found_offset - offset));
			offset = found_offset + found_length;
		}
		if (offset <= source.size())
		        this->insertElement(source.substr(offset)); // String after last delimiter.
		return *this;
	}


	/** \brief  Add strings to the container by collecting matching strings from the source strings. The regular expression match is repeated until
	 *          no more matches are found or the number of matches reaches "max". Any text that does not match the regex is not extracted.
	 *  \param  source    This is the string to be matched.
	 *  \param  regex     This is the regular expression that determines which substrings to extract.
         *  \param  recombine If the regex contains substring matches, this text instructs how to recombine those substrings to create an actual
         *                    extracted element.
	 *  \param  max       Limit the number of extractions to this. Any text remaining after the last split is simply discarded.
	 *  \return Returns a reference to *this.
	 *  \note   Unlike the other container splits, a negative max will not match in reverse. The regex library has no provision for reverse searching.
	 */
	Collection &addByForwardCollectRegex(const std::string &source, const PerlCompatRegExp &regex, const std::string &recombine, unsigned max = UINT_MAX)
	{
		size_t offset(0);
		while (max-- != 0) {
			size_t found_offset(0);
			size_t found_length(0);
			if (not regex.match(source, offset, &found_offset, &found_length))
				break;
			unsigned substring_count(regex.getSubstringMatchCount());
			std::string element;
			if (substring_count == 0) { // Caller did not specify substrings
				if (found_length == 0)
					return *this;
				element = source.substr(found_offset, found_length);
			}
			else {
				for (unsigned index = 1; index <= substring_count; ++index)
					element = PerlCompatRegExp::GenerateReplacementText(regex, recombine);
			}
			this->insertElement(element);
			// Search the string again starting AFTER our current match
			offset = found_offset + found_length;
		}
		return *this;
	}


	/** \brief  Add strings to the container by a single regex match. The "elements" to be extracted are the substrings allowed in a regex
         *          using the () operators.
	 *  \param  source    This is the string to be matched.
	 *  \param  regex     This is the regular expression that determines which substrings to extract. It must contain at least one () expression.
	 *  \return Returns a reference to *this.
	 *  \note   Unlike the other container splits, a negative max will not match in reverse. The regex library has no provision for reverse searching.
	 */
	Collection &addByForwardCollectRegexSubstrings(const std::string &source, const PerlCompatRegExp &regex)
	{
		if (not regex.match(source))
			return *this;
		unsigned substring_count(regex.getSubstringMatchCount());
		for (unsigned index(1); index <= substring_count; ++index) {
			this->insertElement(regex.getMatchedSubstring(index));
		}
		return *this;
	}


	/** \brief  Add pairs to a container by "double splitting".
	 *  \param  source                 This is the string to split.
	 *  \param  record_splitter_chars  These characters do the larger split, they split the string into records.
	 *  \param  field_splitter_chars   These characters do the smaller split. They split the records into first and second for the pair.
	 *  \param  max                    Stop the splitting after this many splits are done.
	 *  \return Returns a reference to *this.
	 */
	Collection &forwardDoubleSplit(const char *const_source, const std::string &record_splitter_chars = "\r\n",
				      const std::string &field_splitter_chars = " \t", int max = INT_MAX)
	{
		typedef typename ElementType::first_type FirstType;
		typedef typename ElementType::second_type SecondType;

		char *source = Strdupa(const_source);
		const char *start(source);
		char* current = source;
		for (/* initialized outside */; *current and max != 1; ++current) {
			if (::strchr(record_splitter_chars.c_str(), *current)) {
				*current = '\0';
				if (current > start and *(current - 1)) { // Not a terminator?
					// WARNING: This will fail if the ElementType is not a pair. This function is only usable
					// if the ElementType is and std::pair
					this->insertElement(StlHelpers::PairFromString<FirstType, SecondType, Converter>(start, field_splitter_chars));
					--max;
				}
				start = current + 1;
			}
		}

		if (*start)
			this->insertElement(StlHelpers::PairFromString<FirstType, SecondType, Converter>(start, field_splitter_chars));

		return *this;
	}
};


template <typename StlContainerBase = std::vector<std::string>, typename Converter = AutoConvert>
class CollectionByDelimitedText: public Collection<StlContainerBase, Converter> {
	typedef Collection<StlContainerBase, Converter> Base;
public:
	/**
	 * \brief    Construct the container from a string
	 * \param    source              an std::string OR const char * which is the string to be split
	 * \param    delimited_by_chars  List of characters which are considered delimiters. Splits of the
	 *				 string will consider any continuous sequence of these characters as a
	 *				 single delimiter ala the standard c function strtok.
	 * \param    max                 The maximum number of splits to perform. Use INT_MAX (which is the
	 *                               default anyway) to mean no limit. If a NEGATIVE max is used it means
	 *                               the split will be performed in reverse with delimited sections of
	 *                               the string taken from the back of source working forward. Extracted
	 *                               sections will be ordered in the new container in the order they are
	 *                               extracted, that is, the first sections taken will be the first
	 *                               elements of the container.
	 * \param    converter           A Converter object that understands how to translate from the provided source string into a usable format.
         * \note                         Example: Collection<std::vector<std::string> >("this is a test", " ", -2);
	 *				 would create an std::vector<std::string> that looks like this:
         *                               "test", "a", "this is"
	 *                               This Collection class and descendents class are actually meant to be expanded as needed, providing methods
	 *                               to make working with STL containers easier and more powerful.
         *                               This class is needed because of the following incapabilities in the StringUtil Split functions: 1)
	 *                               They don't allow partial splitting, that is, to split only n times. This is needed for example to
         *                               just get the protocol portion of a URL or only the filename from a complete path without wasting
         *                               time chopping up the entire string.  2) They don't allow splitting on complex patterns aspects. The
         *                               CollectionByRegex functions let you do that.  3) They don't have reverse splitting where the split
         *                               is done from the back. This is important for decomposing paths into filename and path components or
         *                               decomposing domain names where you want to process the most significant component first (the .com)
         *                               4) They can only split into strings. These function will allow splitting a string into a collection
         *                               of numbers also.
	 *
	 *                               String processing is probably the single most used and important function in any computer program
	 *                               that talks to people. The available functions to process strings should be powerful, as easy to use
	 *                               as possible and complete.
	 */
	explicit CollectionByDelimitedText(const std::string &source, const std::string &delimited_by_chars = "\r\t\n ", const int max = INT_MAX,
					       Converter converter = Converter())
		: Base(converter)
        {
		this->init(source, delimited_by_chars, max);
	}


	/**  \brief  Same as above but saves construction of an std::string if not necessary.
	 */
	explicit CollectionByDelimitedText(const char *source, const char *delimited_by_chars = "\r\t\n ", const int max = INT_MAX,
					       Converter converter = Converter())
		: Base(converter)
	{
		this->init(source, delimited_by_chars, max);
	}


	/** \brief Construct the container from a string delimited by a regular expression.
	 *  \param source   The string used to initialize the container.
	 *  \param regex    The regular expression to be used to split the string. The pattern found in the string will be discarded. It is not
	 *                  implied that multiple instances of the regular expression will be considered a single instance. You'll need to do
	 *                  something like (expression)+ if you want one or more instances of the expression to be considered a single
	 *                  delimiter.
	 * \param max       The maximum number of splits to perform. Use INT_MAX (which is the default anyway) to mean no limit. If a NEGATIVE max is
	 *                  used it means the split will be performed in reverse with delimited sections of the string taken from the back of source
	 *                  working forward. Extracted sections will be ordered in the new container in the order they are extracted, that is, the
	 *                  first sections taken will be the first elements of the container.
	 * \param converter A Converter object that understands how to translate from the provided source string into a usable format.
	 *  \note           Reverse splitting isn't supported since pcre doesn't support reverse searching.
	 */
	explicit CollectionByDelimitedText(const std::string &source, const PerlCompatRegExp &regex, const unsigned max = UINT_MAX,
					       Converter converter = Converter())
		: Base(converter)
	{
		this->addByForwardSplitRegex(source, regex, max);
	}
};


template <typename StlContainerBase = std::vector<std::string>, typename Converter = AutoConvert>
class CollectionByRegex: public Collection<StlContainerBase, Converter> {
	typedef Collection<StlContainerBase, Converter> Base;
public:
	/** \brief Construct the container from a string by extracting matching regexes.
	 *  \param source   The string used to initialize the container.
	 *  \param regex    The regular expression to be used to extract elements from the source string that match. Matching strings will be
	 *                  added to the container. The string that is put into the container will be composed of the substrings (if you used
	 *                  the \\1 \\2 etc special symbols in your regex), concatenated or the entire substring that was matched if you don't.
	 *  \param max      The maximum number of splits to perform. Use INT_MAX (which is the default anyway) to mean no limit. If a negative max is
	 *                  used it means the split will be performed in reverse with delimited sections of the string taken from the back of source
         *                  working forward. Extracted sections will be ordered in the new container in the order they are extracted, that is, the
         *                  first sections taken will be the first elements of the container.
         * \param converter A Converter object that understands.
         *                  how to translate from the provided source string into a usable format.
         * \param recombine If substring extraction are used in the regular expression, each match will be recombined with the recombine string to
                            create the actual elements that will be inserted. The default string will just concatenate all the substrings.
	 */
	explicit CollectionByRegex(const std::string &source, const PerlCompatRegExp &regex, const std::string &recombine = "",
				   const unsigned max = UINT_MAX, Converter converter = Converter())
		: Base(converter)
	{
		this->addByForwardCollectRegex(source, regex, recombine, max);
	}

	/**
	 *  \brief Construct the container from substrings of a single regular expression.
	 *  \param regex       The regular expression to be used to extract elements from the source string that match. You must have substrings
         *                     extracted from your regular expression (the parenthesis operator () does this).
	 *  \param source      The string used to extract substrings from.
	 *  \param converter   A Converter object that understands how to translate from the provided source string into a usable format.
	 *  \note  example: Collection<std::string> path_breakout("https://yahoo.com/newpages/newfonts/index3.html", "(https?)://(.+)/(.+)/(.+$)")
	 *         would put the strings "https", "yahoo.com", "newpages/newfonts", "index3.html" in the container "path_breakout"
	 */
	explicit CollectionByRegex(const PerlCompatRegExp &regex, const std::string &source, Converter converter = Converter())
		: Base(converter)
	{
		this->addByForwardCollectRegexSubstrings(source, regex);
	}
};


/** \class CollectionByDoubleSplit
 */
template <typename StlContainerBase = std::map<std::string, std::string>, typename PairConverter = AutoConvertPair<> >
class CollectionByDoubleSplit: public Collection<StlContainerBase, PairConverter> {
	typedef Collection<StlContainerBase, PairConverter> Base;
public:
	explicit CollectionByDoubleSplit(const std::string &source, const std::string &record_delimiter_chars = "\r\n",
					 const std::string &field_delimiter_chars = " \t", const int max = INT_MAX,
					 PairConverter converter = PairConverter())
		: Base(converter)
        {
		this->forwardDoubleSplit(source.c_str(), record_delimiter_chars, field_delimiter_chars, max);
	}
};


/** \brief  Compares two containers for equality.
 *  \param  container1  The first container to compare.
 *  \param  container2  The second container to compare.  (Must have the same value_type as the first container!)
 *  \return If both containers are of the same size and all corresponding elements are equal (==), this function returns true, otherwise it
 *          returns false.
 */
template<typename ContainerType1, typename ContainerType2>
inline bool ContainerCompare(const ContainerType1 &container1, const ContainerType2 &container2)
{
	typename ContainerType1::const_iterator iter1(container1.begin());
	typename ContainerType2::const_iterator iter2(container2.begin());
	for (/* Empty. */; iter1 != container1.end() and iter2 != container2.end(); ++iter1, ++iter2) {
		if (*iter1 != *iter2)
			return false;
	}

	return iter1 == container1.end() and iter2 == container2.end();
}


/** \brief  Compares two containers.
 *  \param  container1  The first container to compare.
 *  \param  container2  The second container to compare.  (Must have the same value_type as the first container!)
 *  \param  predicate   The binary comparison functor that will be applied to corresponding elements from both containers.
 *  \return If both containers are of the same size and all corresponding elements pass the "predicate" test, this function returns true, otherwise it
 *          returns false.
 */
template<typename ContainerType1, typename ContainerType2, typename Predicate>
inline bool ContainerCompare(const ContainerType1 &container1, const ContainerType2 &container2, const Predicate &predicate)
{
	typename ContainerType1::const_iterator iter1(container1.begin());
	typename ContainerType2::const_iterator iter2(container2.begin());
	for (/* Empty. */; iter1 != container1.end() and iter2 != container2.end(); ++iter1, ++iter2) {
		if (not predicate(*iter1, *iter2))
			return false;
	}

	return iter1 == container1.end() and iter2 == container2.end();
}


#endif // COLLECTION_H
