/** \file    Serialize.h
 *  \brief   Serialization Template Functions
 *  \author  Mr. Walter Howard Junior
 */

/*
 *  Copyright 2006-2008 Project iVia.
 *  Copyright 2006-2008 The Regents of The University of California.
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

#ifndef SERIALIZE_H
#define SERIALIZE_H


#include <deque>
#include <list>
#include <map>
#include <set>
#include <vector>
#include <BinaryIO.h>
#include <File.h>
#include <GnuHash.h>
#include <Collection.h>
#include <MiscUtil.h>
#include <StlHelpers.h>


/**
   Serialization Primitives

   These templates help you serialize and deserialize your classes. Because they work recursively, all you need to do is
   define three member functions:

private:

   void serializeAndDeserialize(File&, const Serialize::Direction) const;

public:

   void serialize(File &output) const;
   void deserialize(File &output);

   Example:

   class BongoBongo: public BaseClass
   {
      int integer;
      std::string astring;
      std::list<std::string> alistofstrings;

      protected:

      // Step 1) You need to define this function for your class:
      // Note, using same function for both IN and OUT enforces the same order of items being serialized.
      // This makes it impossible for programmers to get the sequence wrong.

      void serializeAndDeserialize(File &file, const Serialization::Direction direction) const // <<== You need to define this
      {
           // First serialize base class information:
           Serialization::SerializeBase<BaseClass>(file, direction, *this);

           // Then each element of your class:
           Serialization::Serialize(file, direction, integer);

           // std::string is automatically supported though not strictly a primitive
           Serialization::Serialize(file, direction, astring);

           // Even STL containers are supported
           Serialization::Serialize(file, direction, alistofstrings);
      }

      public:

      // Step 2) You need to have these functions also. They are usually exactly the same for every
      // class since all they do is call serializeAndDeserialize which is unique for each class.

      void serialize(File &file) const { serializeAndDeserialize(file, Serialization::OUT); }
      void deserialize(File &file) { serializeAndDeserialize(file, Serialization::IN); }
   };

   BongoBongo myBongo;

   // This would save a bongo
   File output_file("FileWhereToSaveBongo.bin", "w");
   myBongo.serialize(output_file);

   // This would "reconstitute" that BongoBongo that was saved.
   File input_file("FileWhereToSaveBongo.bin", "r");
   myBongo.deserialize(input);

   Additional Factors:

   NOTE: Serializing a class means base class information must be serialized also. Due to the fact that this
   won't compile: Serialize(file, OUT, static_cast<BaseClass *>(this));

   We have a special function to serialize base classes:

   Serialization::SerializeBase<BaseClassTypeName>(file, direction);

   Your base class of course has to have it's own functions serializeAndDeserialize(), serialize() and deserialize().

   Serialization::Serialize(file, direction, dynamic_cast<const BaseClass &>(this));

   Serialization will apply itself recursively to any serializable data structure no matter how deep, as long
   as all the classes in the chain have the serialization members.

   The general idea here is that the Serialize template functions automatically instantiate to functions of the proper
   type to Serialize just about any type but note there is no provision here for Serializing pointer referents.

   Why is Serializing both in and out collapsed into the same function instead of having two functions like Serialize()
   and DeSerialize()?

   It is critical that data is Deserialized (read in) in the exact same order it was Serialized (written out). By using
   the same function for both reading and writing, there is no way the order can be inadvertantly swapped. Debugging a
   problem with non-human-readable binary data can be hell and everything should be done to prevent that situation
   happening.

   The Serialize functions have to do a few const_cast operations. This is because both serializing and deserializing
   are done in the same function. You want to be able to Serialize (Stream OUT) a const instance of a class so its
   Serialize function must be declared const. However, when used to Deserialize (Stream IN) data for form the instance
   of the class, it must be able to change the classes member variables.

   These functions use the BinaryIO functions at their lowest level so your class will be serialized as binary data, not
   very human readable.
 */


/** \brief        The following functions return a string that is the class's type. Since these are templates, there is a unique copy of the
 *                function for each type, thus, the name needs to be calculated only once and so that name can be a static local variable.
 *  \param Type   The variable whose type needs to be derived.
 *  \return       Returns an std::string of the type. This will be in the same form the computer issues messages in, so for example, STL
 *                containers and such will be long, obtuse strings.
 *  \notes        Example: std::string bubba; std::cout << ClassName(bubba) << std::endl;
 */
static const PerlCompatRegExp pretty_extractor("= +([^\\]]+)");
template <typename Type> std::string ClassName(const Type &)
{
	static std::string class_name(CollectionByRegex<>(__PRETTY_FUNCTION__, pretty_extractor, "$1")[-1]);
	return class_name;
}


/** Like the above, but returns the name prepended with a newline and a ':' appended */
template <typename Type> std::string ClassNameLabel(const Type &)
{
	static std::string class_name = ("\n" + CollectionByRegex<>(__PRETTY_FUNCTION__, pretty_extractor, "$1")[-1] + ':');
	return class_name;
}


/**
 *   Defining this will put the "typename:" in front of each data element in the serialization stream. And double check during
 *   deserialization that what is being read is the same type as what was written. This is useful for debugging serialization.
 */
#undef ANNOTATE_SERIALIZATION


namespace Serialization {


enum Direction { IN, OUT };


/** \brief  Use this when serializing a base class. This function helps avoid a bit of mess when seralizing base
 *          classes, Example:
 *          Serialization::SerializeBase<YourBaseClassType>(file, direction, *this);
 *          Of course, the base class itself must also have the 3 serialization functions
 *  \param  file       The libiViaCore File object being streamed to/from.
 *  \param  direction  Specifies whether this is a serialization or deserialization.
 *  \param  item       The item being Serialized.
*/
template <typename Class> inline void SerializeBase(File &file, const Serialization::Direction direction, const Class &item)
{
	Serialize(file, direction, item);
}

template <typename Class> inline void SerializeBase(char ** const memory, const Serialization::Direction direction, const Class &item)
{
	Serialize(memory, direction, item);
}


/** \brief  Throws exception giving a lot of information related to serializing.
 *  \param  file       The libiViaCore File object being streamed to/from.
 *  \param  direction  Direction of information flow, IN = deserializing, OUT = serializing.
 *  \param  message    The error message composed by the programmer
*/
inline void ThrowSerializationException(const File& file, const Serialization::Direction direction, const std::string &message)
{
	throw Exception(ErrorLocation() + ' ' + message + " while " + (direction == IN ? "deserializing" : "serializing") + " file " + file.getPath()
			+ " at offset " + StringUtil::ToString(file.tell()));
}


/**
 * \brief PointerOrReference - Homogenizes pointers and references.
 *        From dictionary.com: 3.to make uniform or similar, as in composition or function.
 *	  Constructs with either a pointer or reference but acts like a reference to "Class"
 * \note  C++ template functions are unable to broadly specialize between "any class" and
 *        "that class's pointer". Template classes can. This class helps the general Serialize
 *        function work for both pointers to object and references to objects.
*/
typedef uint64_t SerializedPointerFormat; /** we will save pointers as uint64_t */


template <typename Class> class PointerOrReference {
	const Class &item_;
public:
	explicit PointerOrReference(const Class &item): item_(item) { }
	void serialize(File &file) const { item_.serialize(file); }
	void deserialize(File &file) { const_cast<Class &>(item_).deserialize(file); }
};


template <typename Class> class MemPointerOrReference {
	const Class &item_;
public:
	explicit MemPointerOrReference(const Class &item): item_(item) { }
	void serialize(char ** const memory) const { item_.serialize(memory); }
	void deserialize(const char ** const memory) { const_cast<Class &>(item_).deserialize(memory); }
};


void SerializePointer(File &file, const Direction direction, void * const &pointer);
void SerializePointer(char ** const memory, const Direction direction, void * const &pointer);
std::string SerializationErrorContext(File &file, const off_t file_offset);


/**
 * This object keeps track of pointer values while serializing and deserializing.  The idea here is, when
 * serializing pointers to objects, the actual pointer values become unusable as they point to actual
 * locations in memory. However, when we serialize out a pointer we do keep track of its value. The absolute
 * value of the pointer is irrelevant, but if another pointer has the same value, we know that when we
 * deserialize things, we can set pointers that were identical originally, back to pointing at the identical
 * address. For example, reference counted pointers need this capability if they are to be restored in the
 * same state. This pointer map will grow as its used to it should be cleared between serializations using the
 * clear function.
*/

/**
 * \brief  Serializing a pointer is very different from serializing an actual object. This class
 *         is a specialization of the default PointerOrReference. This will construct if a pointer data
 *         type is attempting serialization.
 * \note   This is somewhat complicated because the pointer in the deserialize call must sometimes be modified
 *         in the caller's space. That is why we use a reference to a pointer. It also understands if pointers point
 *         to the same object and only stores or restores one copy of the common object.
 */

/**
 * This map keeps track of the pointers which have been serialized. If the same pointer is serialized more than once, it means that pointer
 * was pointing to the same object as a previous pointer. We need to keep track of this so when we deserialize we can restore the fact that
 * these pointers were pointing to the same object. We keep track of the File being written. If that changes, we reset the pointer map.
 */
typedef std::map<SerializedPointerFormat, void *> PointerToPointer;
extern PointerToPointer pointer_map_;
extern const File *last_file;
extern const char **last_memory;

void ClearGlobals(File &, void * const);


/** This needs to be called between top level serialization operations */
inline void PrepareNewSerialization(const File &file)
{

	const_cast<File &>(file).registerOnCloseCallback(&Serialization::ClearGlobals, NULL);
	if (&file != last_file or file.getPath() != last_file->getPath()) {
		pointer_map_.clear();
		last_file = &file;
	}
}


/** This needs to be called between top level serialization operations */
inline void PrepareNewSerialization(const char ** const memory)
{
	if (memory != last_memory) {
		pointer_map_.clear();
		last_memory = memory;
	}
}


/** This needs to be called between top level serialization operations */
inline void PrepareNewSerialization()
{
	pointer_map_.clear();
}


template <typename Class> class PointerOrReference<Class *> {
	typedef Class *ClassPtr;
	const ClassPtr &item_;
public:
	PointerOrReference(const ClassPtr &item) : item_(item) { }

	/**
	 * Pointers never have their own serializers. They must rely on the serialization of their referent
	 * class. This will recurse back through the "Serialize" functions now looking for Class & instead of
	 * Class *. We save the pointer value also so we can save the fact that we had a NULL pointer, and we
	 * can also deserialize shared pointers to point to the same object if their original pointer values
	 * were the same.
	 */
	void serialize(File &output) const
	{
		/**
		 * pointer_map_ needs to be cleared between serializations. The following code detects if the
		 * File being used has changed. If so, we know we are doing a different serialization so we
		 * clear pointer_map_
		 */
		PrepareNewSerialization(output);

		// If pointer is NULL, save it but NOT the object it doesn't point to.
		SerializePointer(output, OUT, item_);

		// No need to save referents of NULL pointers
		if (item_ == NULL)
			return;

		// See if we have already saved this pointer
		if (pointer_map_.find(reinterpret_cast<SerializedPointerFormat>(item_)) != pointer_map_.end())
			return;

		Serialize(output, Serialization::OUT, *item_);

		// store this pointer now.
		pointer_map_[reinterpret_cast<SerializedPointerFormat>(item_)] = item_;
	}

	void deserialize(File &input)
	{
		PrepareNewSerialization(input);

		/**
		 * Warning: There is a prerequisite here. The pointer being reconstituted must be either NULL,
		 * or pointer to a valid instance of Class. It must not be an uninitialized pointer. If you
		 * get a crash here it is probably because you are trying to deserialize into an uninitialized
		 * pointer. Make sure when you stream OUT (serialize) this thing, it wasn't serialized with an
                 * uninitialized pointer.
		 */
		// First read in the pointer that was saved.
		void *restored_pointer;
		SerializePointer(input, IN, restored_pointer);

		// Source and target both NULL? Nothing to do.
		if (restored_pointer == NULL and item_ == NULL)
			return;

		/**
		 *  Now, determine what to read in next.
		 */
		ClassPtr &target_item = const_cast<ClassPtr &>(item_);

		/**
		 * Is the source NULL, but the target is not? Delete the target and set
		 * its pointer to NULL. It means the saved object had this pointer set to NULL
		 * when it was serialized and we want to restore that saved state.
		 */
		if (restored_pointer == NULL) {
			delete target_item;
			target_item = NULL;
			return;
		}

		/** Has this pointer referent already been restored? */
		typename PointerToPointer::const_iterator ptr_iter =
			pointer_map_.find(reinterpret_cast<SerializedPointerFormat>(restored_pointer));

		// If we've already restored this object
		if (ptr_iter != pointer_map_.end()) {
			target_item = reinterpret_cast<ClassPtr>(ptr_iter->second);
			return;
		}

		/**
		 * If the target pointer is NULL but we need to deserialize an actual object,
		 * construct a new default object up in there into which we will deserialize. We
		 * don't know how to deserialize anything. We must construct something and then
		 * have that something deserialize itself.
		 */
		if (target_item == NULL)
			target_item = new Class();

		Serialize(input, IN, *target_item);

		// We have now reconstituted this, add it to the pointer map
		pointer_map_[reinterpret_cast<SerializedPointerFormat>(restored_pointer)] = target_item;
	}
};


template <typename Class> class MemPointerOrReference<Class *> {
	typedef Class *ClassPtr;
	const ClassPtr &item_;
public:
	MemPointerOrReference(const ClassPtr &item) : item_(item) { }

	/**
	 * Pointers never have their own serializers. They must rely on the serialization of their referent
	 * class. This will recurse back through the "Serialize" functions now looking for Class & instead of
	 * Class *. We save the pointer value also so we can save the fact that we had a NULL pointer, and we
	 * can also deserialize shared pointers to point to the same object if their original pointer values
	 * were the same.
	 */
	void serialize(char ** const memory) const
	{
		/**
		 * pointer_map_ needs to be cleared between serializations. The following code detects if the
		 * File being used has changed. If so, we know we are doing a different serialization so we
		 * clear pointer_map_
		 */
		PrepareNewSerialization(memory);

		// If pointer is NULL, save it but NOT the object it doesn't point to.
		SerializePointer(memory, OUT, item_);

		// No need to save referents of NULL pointers
		if (item_ == NULL)
			return;

		// See if we have already saved this pointer
		if (pointer_map_.find(reinterpret_cast<SerializedPointerFormat>(item_)) != pointer_map_.end())
			return;

		Serialize(memory, OUT, *item_);

		// store this pointer now.
		pointer_map_[reinterpret_cast<SerializedPointerFormat>(item_)] = item_;
	}

	void deserialize(const char ** const memory)
	{
		PrepareNewSerialization(memory);

		/**
		 * Warning: There is a prerequisite here. The pointer being reconstituted must be either NULL,
		 * or pointer to a valid instance of Class. It must not be an uninitialized pointer. If you
		 * get a crash here it is probably because you are trying to deserialize into an uninitialized
		 * pointer. Make sure when you stream OUT (serialize) this thing, it wasn't serialized with an
                 * uninitialized pointer.
		 */
		// First read in the pointer that was saved.
		void *restored_pointer;
		SerializePointer(memory, IN, restored_pointer);

		// Source and target both NULL? Nothing to do.
		if (restored_pointer == NULL and item_ == NULL)
			return;

		/**
		 *  Now, determine what to read in next.
		 */
		ClassPtr &target_item = const_cast<ClassPtr &>(item_);

		/**
		 * Is the source NULL, but the target is not? Delete the target and set
		 * its pointer to NULL. It means the saved object had this pointer set to NULL
		 * when it was serialized and we want to restore that saved state.
		 */
		if (restored_pointer == NULL) {
			delete target_item;
			target_item = NULL;
			return;
		}

		/** Has this pointer referent already been restored? */
		typename PointerToPointer::const_iterator ptr_iter(pointer_map_.find(reinterpret_cast<SerializedPointerFormat>(restored_pointer)));

		// If we've already restored this object
		if (ptr_iter != pointer_map_.end()) {
			target_item = reinterpret_cast<ClassPtr>(ptr_iter->second);
			return;
		}

		/**
		 * If the target pointer is NULL but we need to deserialize an actual object,
		 * construct a new default object up in there into which we will deserialize. We
		 * don't know how to deserialize anything. We must construct something and then
		 * have that something deserialize itself.
		 */
		if (target_item == NULL)
			target_item = new Class();

		Serialize(memory, IN, *target_item);

		// We have now reconstituted this, add it to the pointer map
		pointer_map_[reinterpret_cast<SerializedPointerFormat>(restored_pointer)] = target_item;
	}
};


inline void Serialize(File &file, const Direction direction, char *address, const size_t length)
{
	if (direction == IN)
		file.read(address, length);
	else
		file.write(address, length);
}


/** \brief  Used to deserialize from or serialize to memory.
 *  \note   Advances memory to point just past the written/read section.
 */
inline void Serialize(char ** const memory, const Direction direction, char *address, const size_t length)
{
	if (direction == IN)
		std::memcpy(address, *memory, length);
	else
		std::memcpy(*memory, address, length);

	*memory += length;
}


/** used where const char* is serialized out but cannot be serialized back in, it's const! */
inline void Serialize(File &file, const Direction direction, const char *address, const size_t length)
{
	if (direction == IN) {
		/** Since "address" is const we assume it's always set so we cannot deserialize into a memory location however
		    we must read it somehow to clear it from the input stream */
		char *throw_away = static_cast<char *>(alloca(length));
		file.read(throw_away, length);
	}
	else
		file.write(address, length);
}


/** used where const char* is serialized out but cannot be serialized back in, it's const! */
inline void Serialize(char ** const memory, const Direction direction, const char *address, const size_t length)
{
	if (direction == OUT)
		std::memcpy(*memory, address, length);

	*memory += length;
}


/**
 * \brief   The general "Serialize" function which gets called if your class is not one of the built in primitives or
 *          STL containers. It will expect your class to have 2 functions:
 *          "void serialize(File &) const"
 *          "void deserialize(File &)"
 * \param   file                   The libiViaCore File object being streamed to/from.
 * \param   direction              The direction of the serialization process.
 * \param   pointer_or_reference   The item being serialized.
*/
template <typename Class> inline void Serialize(File &file, const Direction direction, const Class &pointer_or_reference)
{
	if (direction > OUT or direction < IN)
		ThrowSerializationException(file, direction, "Invalid direction: " + ErrorLocation());

	PointerOrReference<Class> item(pointer_or_reference);

#ifdef ANNOTATE_SERIALIZATION
	off_t file_position = file.tell();
	char marker[2048] = "";
	std::strncpy(marker, ClassNameLabel(pointer_or_reference).c_str(), sizeof(marker) / sizeof(marker[0]));
	Serialize(file, direction, marker, std::strlen(marker) + 1);
#endif
	if (direction == OUT)
		item.serialize(file);
	else {
#ifdef ANNOTATE_SERIALIZATION
		if (ClassNameLabel(pointer_or_reference) != marker)
			ThrowSerializationException(file, direction, "Serialize in:" + ClassNameLabel(pointer_or_reference) +
						    " does not match what was serialized out: " + marker +
						    SerializationErrorContext(file, file_position));
#endif
		item.deserialize(file);
	}
}


/**
 * \brief   The general "Serialize" function which gets called if your class is not one of the built in primitives or
 *          STL containers. It will expect your class to have 2 functions:
 *          "void serialize(File &) const"
 *          "void deserialize(File &)"
 * \param   memory                 The address of the memory location to which we'd liek to serialize or from which we'd like to deserialize.
 * \param   direction              The direction of the serialization process.
 * \param   pointer_or_reference   The item being serialized.
*/
template <typename Class> inline void Serialize(char ** const memory, const Direction direction, const Class &pointer_or_reference)
{
	if (direction != OUT and direction != IN)
		throw Exception("in Serialization::Serialize: direction must be IN or OUT!");

	MemPointerOrReference<Class> item(pointer_or_reference);

	if (direction == OUT)
		item.serialize(memory);
	else
		item.deserialize(const_cast<const char ** const>(memory));
}


/**
 * \brief   If during template expansion, the value being streamed is recognized as a built in primitive or "known"
 *          type, this function will end up being called to use the BinaryIO functions. Since all structs and classes are
 *          composed of these primitives all Serialization eventually ends up calling this.
 * \param   file      The libiViaCore File object being streamed to/from.
 * \param   direction Specifies whether this is a serialization or a deserialization.
 * \param   item      The primitive (int, float, unsigned, std::string etc.) being serialized.
*/
template <typename Primitive> inline void SerializePrimitive(File &file, const Direction direction, const Primitive &item)
{
	if (unlikely(direction != OUT and direction != IN))
		ThrowSerializationException(file, direction, "Invalid direction: " + ErrorLocation());

#ifdef ANNOTATE_SERIALIZATION
	off_t file_position = file.tell();
	char marker[2048] = "";
	std::strncpy(marker, ClassNameLabel(item).c_str(), sizeof(marker)/sizeof(marker[0]));
	Serialize(file, direction, marker, std::strlen(marker) + 1);
#endif

	if (direction == OUT) {
		if (unlikely(not BinaryIO::Write(file, item)))
			ThrowSerializationException(file, direction, StringUtil::Format("Writing %s to serialization stream: ", ClassName(item).c_str())
					    + ErrorInfo());
	}
	else {
#ifdef ANNOTATE_SERIALIZATION
		if (unlikely(ClassNameLabel(item) != marker))
			ThrowSerializationException(file, direction, "Serialize in: %s" + ClassNameLabel(item) +
						    " does not match what was serialized out: " + marker +
						    SerializationErrorContext(file, file_position));
#endif
		if (unlikely(not BinaryIO::Read(file, const_cast<Primitive *>(&item))))
			ThrowSerializationException(file, direction, StringUtil::Format("Reading %s from serialization stream: ", ClassName(item).c_str())
						    + ErrorInfo());
	}
}


/**
 * \brief   If during template expansion, the value being streamed is recognized as a built in primitive or "known"
 *          type, this function will end up being called to use the BinaryIO functions. Since all structs and classes are
 *          composed of these primitives all Serialization eventually ends up calling this.
 * \param   memory    The memory being serizlized to or deserialized from.
 * \param   direction Specifies whether this is a serialization or a deserialization.
 * \param   item      The primitive (int, float, unsigned, std::string etc.) being serialized.
*/
template <typename Primitive> inline void SerializePrimitive(char ** const memory, const Direction direction, const Primitive &item)
{
	if (unlikely(direction != OUT and direction != IN))
		throw Exception("in Serialization::SerializePrimitive: invalid direction (must be IN or OUT)!");

	if (direction == OUT) {
		if (unlikely(not BinaryIO::Write(memory, item)))
			throw Exception("in Serialization::SerializePrimitive: BinaryIO::Write() to memory of \"" + ClassNameLabel(item) + "\" failed!");
	}
	else if (unlikely(not BinaryIO::Read(const_cast<const char ** const>(memory), const_cast<Primitive *>(&item))))
		throw Exception("in Serialization::SerializePrimitive: BinaryIO::Read() of memory of \"" + ClassNameLabel(item) + "\" failed!");
}


/**
 * \brief  These are "specialized" templates that are instantiated instead of the general "void Serialize(File&,
 *         Class&, Direction) when the item being Serialized is one of the basic c++ types.
*/
template <> inline void Serialize<bool>(File &file, const Direction direction, const bool &item)
{
	SerializePrimitive(file, direction, item);
}


template <> inline void Serialize<bool>(char ** const memory, const Direction direction, const bool &item)
{
	SerializePrimitive(memory, direction, item);
}


template <> inline void Serialize<double>(File &file, const Direction direction, const double &item){

	SerializePrimitive(file, direction, item);
}


template <> inline void Serialize<double>(char ** const memory, const Direction direction, const double &item){

	SerializePrimitive(memory, direction, item);
}


template <> inline void Serialize<float>(File &file, const Direction direction, const float &item)
{
	SerializePrimitive(file, direction, item);
}


template <> inline void Serialize<float>(char ** const memory, const Direction direction, const float &item)
{
	SerializePrimitive(memory, direction, item);
}


template <> inline void Serialize<int8_t>(File &file, const Direction direction, const int8_t &item)
{
	SerializePrimitive(file, direction, item);
}


template <> inline void Serialize<int8_t>(char ** const memory, const Direction direction, const int8_t &item)
{
	SerializePrimitive(memory, direction, item);
}


template <> inline void Serialize<int16_t>(File &file, const Direction direction, const int16_t &item)
{
	SerializePrimitive(file, direction, item);
}


template <> inline void Serialize<int16_t>(char ** const memory, const Direction direction, const int16_t &item)
{
	SerializePrimitive(memory, direction, item);
}


template <> inline void Serialize<int32_t>(File &file, const Direction direction, const int32_t &item)
{
	SerializePrimitive(file, direction, item);
}


template <> inline void Serialize<int32_t>(char ** const memory, const Direction direction, const int32_t &item)
{
	SerializePrimitive(memory, direction, item);
}


template <> inline void Serialize<int64_t>(File &file, const Direction direction, const int64_t &item)
{
	SerializePrimitive(file, direction, item);
}


template <> inline void Serialize<int64_t>(char ** const memory, const Direction direction, const int64_t &item)
{
	SerializePrimitive(memory, direction, item);
}


template <> inline void Serialize<uint8_t>(File &file, const Direction direction, const uint8_t &item)
{
	SerializePrimitive(file, direction, item);
}


template <> inline void Serialize<uint8_t>(char ** const memory, const Direction direction, const uint8_t &item)
{
	SerializePrimitive(memory, direction, item);
}


template <> inline void Serialize<uint16_t>(File &file, const Direction direction, const uint16_t &item)
{
	SerializePrimitive(file, direction, item);
}


template <> inline void Serialize<uint16_t>(char ** const memory, const Direction direction, const uint16_t &item)
{
	SerializePrimitive(memory, direction, item);
}


template <> inline void Serialize<uint32_t>(File &file, const Direction direction, const uint32_t &item)
{
	SerializePrimitive(file, direction, item);
}


template <> inline void Serialize<uint32_t>(char ** const memory, const Direction direction, const uint32_t &item)
{
	SerializePrimitive(memory, direction, item);
}


template <> inline void Serialize<uint64_t>(File &file, const Direction direction, const uint64_t &item)
{
	SerializePrimitive(file, direction, item);
}


template <> inline void Serialize<uint64_t>(char ** const memory, const Direction direction, const uint64_t &item)
{
	SerializePrimitive(memory, direction, item);
}


template <> inline void Serialize<std::string>(File &file, const Direction direction, const std::string &item)
{
	SerializePrimitive(file, direction, item);
}


template <> inline void Serialize<std::string>(char ** const memory, const Direction direction, const std::string &item)
{
	SerializePrimitive(memory, direction, item);
}


/** Enums need special handling. */
template <typename EnumType> inline void SerializeEnum(File &file, const Direction direction, const EnumType &item)
{
	// Conversion FROM enum TO int is ok
	int32_t temp(item);

	SerializePrimitive(file, direction, temp);

	// Conversion FROM int TO enum needs to be forced with a static_cast
	if (direction == IN) {
		EnumType enum_value = static_cast<EnumType>(temp);
		const_cast<EnumType &>(item) = enum_value;
	}
}


/** Enums need special handling. */
template <typename EnumType> inline void SerializeEnum(char ** const memory, const Direction direction, const EnumType &item)
{
	// Conversion FROM enum TO int is ok
	int32_t temp(item);

	SerializePrimitive(memory, direction, temp);

	// Conversion FROM int TO enum needs to be forced with a static_cast
	if (direction == IN) {
		EnumType enum_value = static_cast<EnumType>(temp);
		const_cast<EnumType &>(item) = enum_value;
	}
}


/**  \brief             Save a class name and a version of that class.
 *   \param class_name  The name of the class we are saving.
 *   \param version     A number.
 *   \note              The purpose of the version number is to have objects that have been modified in later versions, to be able to
 *                      read in streamed objects from a previous version. This function will return the version number of what was streamed in and you
 *                      can then branch and accomodate older versions of streams in newer code.
 */
int32_t SerializeClassNameAndVersion(File &file, const Direction direction, const char *class_name, const int32_t current_version);


/**  \brief             Save a class name and a version of that class.
 *   \param class_name  The name of the class we are saving.
 *   \param version     A number.
 *   \note              The purpose of the version number is to have objects that have been modified in later versions, to be able to
 *                      read in streamed objects from a previous version. This function will return the version number of what was streamed in and you
 *                      can then branch and accomodate older versions of streams in newer code.
 */
int32_t SerializeClassNameAndVersion(char ** const memory, const Direction direction, const char *class_name, const int32_t current_version);


/**
   \note   It helps greatly to write serialization template functions for some standard types that are used everywhere,
   specifically, std::pair and all the STL containers.
*/
template <typename First, typename Second> inline void Serialize(File &file, const Direction direction, const std::pair<First, Second> &item)
{
#ifdef ANNOTATE_SERIALIZATION
	char marker[2048] = "";
	std::strncpy(marker, ClassNameLabel(item).c_str(), sizeof(marker) / sizeof(marker[0]));
	Serialize(file, direction, marker, strlen(marker) + 1);
#endif
	Serialize(file, direction, item.first);
	Serialize(file, direction, item.second);
}


/**
   \note   It helps greatly to write serialization template functions for some standard types that are used everywhere,
   specifically, std::pair and all the STL containers.
*/
template <typename First, typename Second> inline void Serialize(char ** const memory, const Direction direction, const std::pair<First, Second> &item)
{
	Serialize(memory, direction, item.first);
	Serialize(memory, direction, item.second);
}


template <typename StlContainer> void SerializeContainer(File &file, const Direction direction, const StlContainer &cont)
{
#ifdef ANNOTATE_SERIALIZATION
	char marker[2048] = "";
	std::strncpy(marker, ClassNameLabel(cont).c_str(), sizeof(marker)/sizeof(marker[0]));
	Serialize(file, direction, marker, strlen(marker) + 1);
#endif
	StlContainer &container = const_cast<StlContainer &>(cont);
	uint64_t element_count;
	if (direction == IN) {
		Serialize(file, IN, element_count);
		container.clear(); // Clean out any pre-existing values
		for (uint64_t i = 0; i < element_count; ++i) {
			// Redundant? No. This assignment is necessary to make sure objects
			// especially pointers are set to default state. Simple types are not initialized
			// to default by simply declaring them. They must be assigned like int i = int();
			typename StlContainer::value_type item = typename StlContainer::value_type();
			Serialize(file, IN, item);
			container.insert(container.end(), item);
		}
	}
	else {
		element_count = container.size();
		Serialize(file, OUT, element_count);
		for (typename StlContainer::iterator item(container.begin()); item != container.end(); ++item)
			Serialize(file, OUT, *item);
	}
}


template <typename StlContainer> void SerializeContainer(char ** const memory, const Direction direction, const StlContainer &cont)
{
	StlContainer &container = const_cast<StlContainer &>(cont);
	uint64_t element_count;
	if (direction == IN) {
		Serialize(memory, IN, element_count);
		container.clear(); // Clean out any pre-existing values
		for (uint64_t i = 0; i < element_count; ++i) {
			// Redundant? No. This assignment is necessary to make sure objects
			// especially pointers are set to default state. Simple types are not initialized
			// to default by simply declaring them. They must be assigned like int i = int();
			typename StlContainer::value_type item = typename StlContainer::value_type();
			Serialize(memory, IN, item);
			container.insert(container.end(), item);
		}
	}
	else {
		element_count = container.size();
		Serialize(memory, OUT, element_count);
		for (typename StlContainer::iterator item(container.begin()); item != container.end(); ++item)
			Serialize(memory, OUT, *item);
	}
}


// GNU_HASH_MAP does not support insert(iterator, value_type).  It is not a drop in replacement for std::map. Most heinous.
template <typename StlContainer> void SerializeBrokenContainer(File &file, const Direction direction, const StlContainer &cont)
{
#ifdef ANNOTATE_SERIALIZATION
	char marker[2048] = "";
	std::strncpy(marker, ClassNameLabel(cont).c_str(), sizeof(marker)/sizeof(marker[0]));
	Serialize(file, direction, marker, strlen(marker) + 1);
#endif
	StlContainer &container = const_cast<StlContainer &>(cont);
	uint64_t element_count;
	if (direction == IN) {
		container.clear(); // Clean out any pre-existing values.
		Serialize(file, IN, element_count);
		for (uint64_t i = 0; i < element_count; ++i) {
			typename StlContainer::value_type item = typename StlContainer::value_type();
			Serialize(file, IN, item);
			container.insert(item); // <== Different handling than std::map is taken care of here.
		}
	}
	else {
		element_count = container.size();
		Serialize(file, OUT, element_count);
		for (typename StlContainer::iterator item(container.begin()); item != container.end(); ++item)
			Serialize(file, OUT, *item);
	}
}


// GNU_HASH_MAP does not support insert(iterator, value_type).  It is not a drop in replacement for std::map. Most heinous.
template <typename StlContainer> void SerializeBrokenContainer(char ** const memory, const Direction direction, const StlContainer &cont)
{
	StlContainer &container = const_cast<StlContainer &>(cont);
	uint64_t element_count;
	if (direction == IN) {
		container.clear(); // Clean out any pre-existing values.
		Serialize(memory, IN, element_count);
		for (uint64_t i = 0; i < element_count; ++i) {
			typename StlContainer::value_type item = typename StlContainer::value_type();
			Serialize(memory, IN, item);
			container.insert(item); // <== Different handling than std::map is taken care of here.
		}
	}
	else {
		element_count = container.size();
		Serialize(memory, OUT, element_count);
		for (typename StlContainer::iterator item(container.begin()); item != container.end(); ++item)
			Serialize(memory, OUT, *item);
	}
}


template <typename Element> inline void Serialize(File &file, const Direction direction, const std::vector<Element> &container)
{
	SerializeContainer(file, direction, container);
}


template <typename Element> inline void Serialize(char ** const memory, const Direction direction, const std::vector<Element> &container)
{
	SerializeContainer(memory, direction, container);
}


template <typename Key, typename Value> inline void Serialize(File &file, const Direction direction, const std::map<Key, Value> &container)
{
	SerializeContainer(file, direction, container);
}


template <typename Key, typename Value> inline void Serialize(char ** const memory, const Direction direction, const std::map<Key, Value> &container)
{
	SerializeContainer(memory, direction, container);
}


template <typename Key, typename Value> inline void Serialize(File &file, const Direction direction, const std::multimap<Key, Value> &container)
{
	SerializeContainer(file, direction, container);
}


template <typename Key, typename Value> inline void Serialize(char ** const memory, const Direction direction, const std::multimap<Key, Value> &container)
{
	SerializeContainer(memory, direction, container);
}


template <typename Element> inline void Serialize(File &file, const Direction direction, const std::set<Element> &container)
{
	SerializeContainer(file, direction, container);
}


template <typename Element> inline void Serialize(char ** const memory, const Direction direction, const std::set<Element> &container)
{
	SerializeContainer(memory, direction, container);
}


template <typename Element> inline void Serialize(File &file, const Direction direction, const std::multiset<Element> &container)
{
	SerializeContainer(file, direction, container);
}


template <typename Element> inline void Serialize(char ** const memory, const Direction direction, const std::multiset<Element> &container)
{
	SerializeContainer(memory, direction, container);
}


template <typename Key, typename Value> inline void Serialize(File &file, const Direction direction, const GNU_HASH_MAP<Key, Value> &container)
{
	SerializeBrokenContainer(file, direction, container);
}


template <typename Key, typename Value> inline void Serialize(char ** const memory, const Direction direction, const GNU_HASH_MAP<Key, Value> &container)
{
	SerializeBrokenContainer(memory, direction, container);
}


template <typename Element> inline void Serialize(File &file, const Direction direction, const std::list<Element> &container)
{
	SerializeContainer(file, direction, container);
}


template <typename Element> inline void Serialize(char ** const memory, const Direction direction, const std::list<Element> &container)
{
	SerializeContainer(memory, direction, container);
}


template <typename Element> inline void Serialize(File &file, const Direction direction, const std::deque<Element> &container)
{
	SerializeContainer(file, direction, container);
}


template <typename Element> inline void Serialize(char ** const memory, const Direction direction, const std::deque<Element> &container)
{
	SerializeContainer(memory, direction, container);
}


} // namespace Serialize


#endif // SERIALIZE_H
