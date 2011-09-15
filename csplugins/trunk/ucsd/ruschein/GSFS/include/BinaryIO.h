/** \file    BinaryIO.h
 *  \brief   Declaration of binary I/O utility functions.
 *  \author  Dr. Johannes Ruscheinski
 */

/*
 *  Copyright 2004-2008 Project iVia.
 *  Copyright 2004-2008 The Regents of The University of California.
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

#ifndef BINARY_IO_H
#define BINARY_IO_H


#include <fstream>
#include <map>
#include <string>
#include <inttypes.h>
#include <Compiler.h>
#include <GnuHash.h>


// Forward declaration:
class File;


/** \namespace BinaryIO
 *  \brief     Contains binary I/O functions that allow portable (between different hardware platforms) serialization and
 *             deserialisation of std::strings and numeric types.
 */
namespace BinaryIO {


/** \brief  Serializes a std::string to a binary stream.
 *  \param  output  The stream to write to.
 *  \param  s       The string to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(std::ostream &output, const std::string &s);


/** \brief  Serializes a std::string to memory.
 *  \param  memory  The address of the memory location to write to.
 *  \param  s       The string to write.
 *  \note   "*memory" will point just past the serialised string after a successful call to this function.
 *  \return Always returns true!
 */
bool Write(char ** const memory, const std::string &s);


/** \brief  Deserializes a std::string from a binary stream.
 *  \param  input  The stream to read from.
 *  \param  s      The string to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(std::istream &input, std::string * const s);


/** \brief  Deserializes a std::string from memory.
 *  \param  memory  The address of the memory location to read from.
 *  \param  s       The string to read.
 *  \note   "*memory" will point just past the serialised string after a successful call to this function.
 *  \return Always returns true!
 */
bool Read(const char ** const memory, std::string * const s);


/** \brief  Serializes a character variable to a binary stream.
 *  \param  output  The stream to write to.
 *  \param  ch      The character variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(std::ostream &output, const char ch);


/** \brief  Serializes a character variable to memory.
 *  \param  memory  The address of the memory location to write to.
 *  \param  ch      The character variable to write.
 *  \note   "*memory" will point just past the serialised character after a successful call to this function.
 *  \return Always returns true!
 */
bool Write(char ** const memory, const char ch);


/** \brief  Deserializes a character variable from a binary stream.
 *  \param  input  The stream to read from.
 *  \param  ch     The character variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(std::istream &input, char * const ch);


/** \brief  Deserializes a character variable from memory.
 *  \param  memory  The address of the memory location to read from.
 *  \param  ch      The character variable to read.
 *  \note   "*memory" will point just past the deserialised character after a successful call to this function.
 *  \return Always returns true!
 */
bool Read(const char ** const memory, char * const ch);


/** \brief  Serializes a boolean variable to a binary stream.
 *  \param  output  The stream to write to.
 *  \param  b       The boolean variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(std::ostream &output, const bool b);


/** \brief  Serializes a boolean variable to memory.
 *  \param  memory  The address of the memory location to write to.
 *  \param  b       The boolean variable to write.
 *  \note   "*memory" will point just past the serialised boolean after a successful call to this function.
 *  \return Always returns true!
 */
bool Write(char ** const memory, const bool b);


/** \brief  Deserializes a boolean variable from a binary stream.
 *  \param  input  The stream to read from.
 *  \param  b      The boolean variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(std::istream &input, bool * const b);


/** \brief  Deserializes a boolean variable from memory.
 *  \param  memory  The address of the memory location to read from.
 *  \param  b       The boolean variable to read.
 *  \note   "*memory" will point just past the deserialised boolean after a successful call to this function.
 *  \return Always returns true!
 */
bool Read(const char ** const memory, bool * const b);


/** \brief  Serializes a uint8_t variable to a binary stream.
 *  \param  output  The stream to write to.
 *  \param  u8      The variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(std::ostream &output, const uint8_t u8);


/** \brief  Serializes a uint8_t variable to memory.
 *  \param  memory  The address of the memory location to write to.
 *  \param  u8      The variable to write.
 *  \note   "*memory" will point just past the serialised variable after a successful call to this function.
 *  \return Always returns true!
 */
bool Write(char ** const memory, const uint8_t u8);


/** \brief  Deserializes a uint8_t variable from a binary stream.
 *  \param  input  The stream to read from.
 *  \param  u8     The variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(std::istream &input, uint8_t * const u8);


/** \brief  Deserializes a uint8_t variable from memory.
 *  \param  memory  The address of the memory location to read from.
 *  \param  u8      The variable to read.
 *  \note   "*memory" will point just past the deserialised variable after a successful call to this function.
 *  \return Always returns true!
 */
bool Read(const char ** const memory, uint8_t * const u8);


/** \brief  Serializes an int8_t variable to a binary stream.
 *  \param  output  The stream to write to.
 *  \param  i8      The variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(std::ostream &output, const int8_t i8);


/** \brief  Serializes an int8_t variable to memory.
 *  \param  memory  The address of the memory location to write to.
 *  \param  i8      The variable to write.
 *  \note   "*memory" will point just past the serialised variable after a successful call to this function.
 *  \return Always returns true!
 */
bool Write(char ** const memory, const int8_t i8);


/** \brief  Deserializes an int8_t variable from a binary stream.
 *  \param  input  The stream to read from.
 *  \param  i8     The variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(std::istream &input, int8_t * const i8);


/** \brief  Deserializes an int8_t variable from memory.
 *  \param  memory  The address of the memory location to read from.
 *  \param  i8      The variable to read.
 *  \note   "*memory" will point just past the deserialised variable after a successful call to this function.
 *  \return Always returns true!
 */
bool Read(const char ** const memory, int8_t * const i8);


/** \brief  Serializes a uint16_t variable to a binary stream.
 *  \param  output  The stream to write to.
 *  \param  u16     The variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(std::ostream &output, const uint16_t u16);


/** \brief  Serializes a uint16_t variable to memory.
 *  \param  memory  The address of the memory location to write to.
 *  \param  u16     The variable to write.
 *  \note   "*memory" will point just past the serialised variable after a successful call to this function.
 *  \return Always returns true!
 */
bool Write(char ** const memory, const uint16_t u16);


/** \brief  Deserializes a uint16_t variable from a binary stream.
 *  \param  input  The stream to read from.
 *  \param  u16    The variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(std::istream &input, uint16_t * const u16);


/** \brief  Deserializes a uint16_t variable from memory.
 *  \param  memory  The address of the memory location to read from.
 *  \param  u16     The variable to read.
 *  \note   "*memory" will point just past the deserialised variable after a successful call to this function.
 *  \return Always returns true!
 */
bool Read(const char ** const memory, uint16_t * const u16);


/** \brief  Serializes an int16_t variable to a binary stream.
 *  \param  output  The stream to write to.
 *  \param  i16     The variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(std::ostream &output, const int16_t i16);


/** \brief  Deserializes an int16_t variable from a binary stream.
 *  \param  input  The stream to read from.
 *  \param  i16    The variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(std::istream &input, int16_t * const i16);


/** \brief  Deserializes an int16_t variable from memory.
 *  \param  memory  The address of the memory location to read from.
 *  \param  i16     The variable to read.
 *  \note   "*memory" will point just past the deserialised variable after a successful call to this function.
 *  \return Always returns true!
 */
bool Read(const char ** const memory, int16_t * const i16);


/** \brief  Serializes a uint32_t variable to a binary stream.
 *  \param  output  The stream to write to.
 *  \param  u32     The variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(std::ostream &output, const uint32_t u32);


/** \brief  Serializes a uint32_t variable to memory.
 *  \param  memory  The address of the memory location to write to.
 *  \param  u32     The variable to write.
 *  \note   "*memory" will point just past the serialised variable after a successful call to this function.
 *  \return Always returns true!
 */
bool Write(char ** const memory, const uint32_t u32);


/** \brief  Deserializes a uint32_t variable from a binary stream.
 *  \param  input  The stream to read from.
 *  \param  u32    The variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(std::istream &input, uint32_t * const u32);


/** \brief  Deserializes a uint32_t variable from memory.
 *  \param  memory  The address of the memory location to read from.
 *  \param  u32     The variable to read.
 *  \note   "*memory" will point just past the deserialised variable after a successful call to this function.
 *  \return Always returns true!
 */
bool Read(const char ** const memory, uint32_t * const u32);


/** \brief  Serializes an int32_t variable to a binary stream.
 *  \param  output  The stream to write to.
 *  \param  i32     The variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(std::ostream &output, const int32_t i32);


/** \brief  Serializes an int32_t variable to memory.
 *  \param  memory  The address of the memory location to write to.
 *  \param  i32     The variable to write.
 *  \note   "*memory" will point just past the serialised variable after a successful call to this function.
 *  \return Always returns true!
 */
bool Write(char ** const memory, const int32_t i32);


/** \brief  Deserializes an int32_t variable from a binary stream.
 *  \param  input  The stream to read from.
 *  \param  i32    The variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(std::istream &input, int32_t * const i32);


/** \brief  Deserializes an int32_t variable from memory.
 *  \param  memory  The address of the memory location to read from.
 *  \param  i32     The variable to read.
 *  \note   "*memory" will point just past the deserialised variable after a successful call to this function.
 *  \return Always returns true!
 */
bool Read(const char ** const memory, int32_t * const i32);


/** \brief  Serializes a uint64_t variable to a binary stream.
 *  \param  output  The stream to write to.
 *  \param  u64     The variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(std::ostream &output, const uint64_t u64);


/** \brief  Serializes a uint64_t variable to memory.
 *  \param  memory  The address of the memory location to write to.
 *  \param  u64     The variable to write.
 *  \note   "*memory" will point just past the serialised variable after a successful call to this function.
 *  \return Always returns true!
 */
bool Write(char ** const memory, const uint64_t u64);


/** \brief  Deserializes a uint64_t variable from a binary stream.
 *  \param  input  The stream to read from.
 *  \param  u64    The variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(std::istream &input, uint64_t * const u64);


/** \brief  Deserializes a uint64_t variable from memory.
 *  \param  memory  The address of the memory location to read from.
 *  \param  u64     The variable to read.
 *  \note   "*memory" will point just past the deserialised variable after a successful call to this function.
 *  \return Always returns true!
 */
bool Read(const char ** const memory, uint64_t * const u64);


/** \brief  Serializes an int64_t variable to a binary stream.
 *  \param  output  The stream to write to.
 *  \param  i64     The variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(std::ostream &output, const int64_t i64);


/** \brief  Serializes an int64_t variable to memory.
 *  \param  memory  The address of the memory location to write to.
 *  \param  i64     The variable to write.
 *  \note   "*memory" will point just past the serialised variable after a successful call to this function.
 *  \return Always returns true!
 */
bool Write(char ** const memory, const int64_t i64);


/** \brief  Deserializes an int64_t variable from a binary stream.
 *  \param  input  The stream to read from.
 *  \param  i64    The variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(std::istream &input, int64_t * const i64);


/** \brief  Deserializes an int64_t variable from memory.
 *  \param  memory  The address of the memory location to read from.
 *  \param  i64     The variable to read.
 *  \note   "*memory" will point just past the deserialised variable after a successful call to this function.
 *  \return Always returns true!
 */
bool Read(const char ** const memory, int64_t * const i64);


/** \brief  Serializes a float variable to a binary stream.
 *  \param  output  The stream to write to.
 *  \param  f       The variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(std::ostream &output, const float f);


/** \brief  Serializes a float variable to memory.
 *  \param  memory  The address of the memory location to write to.
 *  \param  f       The variable to write.
 *  \note   "*memory" will point just past the serialised variable after a successful call to this function.
 *  \return Always returns true!
 */
bool Write(char ** const memory, const float f);


/** \brief  Deserializes a float variable from a binary stream.
 *  \param  input  The stream to read from.
 *  \param  f      The variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(std::istream &input, float * const f);


/** \brief  Deserializes a float variable from memory.
 *  \param  memory  The address of the memory location to read from.
 *  \param  f       The variable to read.
 *  \note   "*memory" will point just past the deserialised variable after a successful call to this function.
 *  \return Always returns true!
 */
bool Read(const char ** const memory, float * const f);


/** \brief  Serializes a double variable to a binary stream.
 *  \param  output  The stream to write to.
 *  \param  d       The variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(std::ostream &output, const double d);


/** \brief  Serializes a double variable to memory.
 *  \param  memory  The address of the memory location to write to.
 *  \param  d       The variable to write.
 *  \note   "*memory" will point just past the serialised variable after a successful call to this function.
 *  \return Always returns true!
 */
bool Write(char ** const memory, const double d);


/** \brief  Deserializes a double variable from a binary stream.
 *  \param  input  The stream to read from.
 *  \param  d      The variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(std::istream &input, double * const d);


/** \brief  Deserializes a double variable from memory.
 *  \param  memory  The address of the memory location to read from.
 *  \param  d       The variable to read.
 *  \note   "*memory" will point just past the deserialised variable after a successful call to this function.
 *  \return Always returns true!
 */
bool Read(const char ** const memory, double * const d);


/** \brief  Serializes some sequence container variable to a binary file.
 *  \param  output     The file to write to.
 *  \param  container  The sequence container to serialize.
 *  \return True if the operation succeeded and false if there was some I/O error.
 *  \note   This only works if the sequence container contains a type for which a corresponding BinaryIO::Write() exists.
 */
template <typename SequenceContainer> bool Write(std::ostream &output, const SequenceContainer &container)
{
	const uint32_t size(container.size());
	if (unlikely(not Write(output, size)))
		return false;

	for (typename SequenceContainer::const_iterator entry(container.begin()); entry != container.end(); ++entry) {
		if (unlikely(not Write(output, *entry)))
			return false;
	}

	return true;
}


/** \brief  Deserializes some sequence container variable from a binary file.
 *  \param  input      The file to read from.
 *  \param  container  The sequence container to deserialize.
 *  \return True if the operation succeeded and false if there was some I/O error.
 *  \note   This only works if the sequence container contains a type for which a corresponding BinaryIO::Read() exists.
 */
template <typename SequenceContainer> bool Read(std::istream &input, SequenceContainer * const container)
{
	container->clear();

	uint32_t size;
	if (unlikely(not Read(input, &size)))
		return false;

	for (unsigned entry_no(0); entry_no < size; ++entry_no) {
		typename SequenceContainer::value_type entry;
		if (unlikely(not Read(input, &entry)))
			return false;

		container->insert(container->end(), entry);
	}

	return true;
}


/** \brief  Serializes a std::string to a binary file.
 *  \param  output  The file to write to.
 *  \param  s       The string to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(File &output, const std::string &s);


/** \brief  Deserializes a std::string from a binary file.
 *  \param  input  The file to read from.
 *  \param  s      The string to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(File &input, std::string * const s);


/** \brief  Serializes a character variable to a binary file.
 *  \param  output  The file to write to.
 *  \param  ch      The character variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(File &output, const char ch);


/** \brief  Deserializes a character variable from a binary file.
 *  \param  input  The file to read from.
 *  \param  ch     The character variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(File &input, char * const ch);


/** \brief  Serializes a boolean variable to a binary file.
 *  \param  output  The file to write to.
 *  \param  b       The boolean variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(File &output, const bool b);


/** \brief  Deserializes a boolean variable from a binary file.
 *  \param  input  The file to read from.
 *  \param  b      The boolean variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(File &input, bool * const b);


/** \brief  Serializes a uint8_t variable to a binary file.
 *  \param  output  The file to write to.
 *  \param  u8      The variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(File &output, const uint8_t u8);


/** \brief  Deserializes a uint8_t variable from a binary file.
 *  \param  input  The file to read from.
 *  \param  u8     The variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(File &input, uint8_t * const u8);


/** \brief  Serializes an int8_t variable to a binary file.
 *  \param  output  The file to write to.
 *  \param  i8      The variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(File &output, const int8_t i8);


/** \brief  Deserializes an int8_t variable from a binary file.
 *  \param  input  The file to read from.
 *  \param  i8     The variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(File &input, int8_t * const i8);


/** \brief  Serializes a uint16_t variable to a binary file.
 *  \param  output  The file to write to.
 *  \param  u16     The variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(File &output, const uint16_t u16);


/** \brief  Deserializes a uint16_t variable from a binary file.
 *  \param  input  The file to read from.
 *  \param  u16    The variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(File &input, uint16_t * const u16);


/** \brief  Serializes an int16_t variable to a binary file.
 *  \param  output  The file to write to.
 *  \param  i16     The variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(File &output, const int16_t i16);


/** \brief  Deserializes an int16_t variable from a binary file.
 *  \param  input  The file to read from.
 *  \param  i16    The variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(File &input, int16_t * const i16);


/** \brief  Serializes a uint32_t variable to a binary file.
 *  \param  output  The file to write to.
 *  \param  u32     The variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(File &output, const uint32_t u32);


/** \brief  Deserializes a uint32_t variable from a binary file.
 *  \param  input  The file to read from.
 *  \param  u32    The variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(File &input, uint32_t * const u32);


/** \brief  Serializes an int32_t variable to a binary file.
 *  \param  output  The file to write to.
 *  \param  i32     The variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(File &output, const int32_t i32);


/** \brief  Deserializes an int32_t variable from a binary file.
 *  \param  input  The file to read from.
 *  \param  i32    The variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(File &input, int32_t * const i32);


/** \brief  Serializes a uint64_t variable to a binary file.
 *  \param  output  The file to write to.
 *  \param  u64     The variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(File &output, const uint64_t u64);


/** \brief  Deserializes a uint64_t variable from a binary file.
 *  \param  input  The file to read from.
 *  \param  u64    The variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(File &input, uint64_t * const u64);


/** \brief  Serializes an int64_t variable to a binary file.
 *  \param  output  The file to write to.
 *  \param  i64     The variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(File &output, const int64_t i64);


/** \brief  Deserializes an int64_t variable from a binary file.
 *  \param  input  The file to read from.
 *  \param  i64    The variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(File &input, int64_t * const i64);


/** \brief  Serializes a float variable to a binary file.
 *  \param  output  The file to write to.
 *  \param  f       The variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(File &output, const float f);


/** \brief  Deserializes a float variable from a binary file.
 *  \param  input  The file to read from.
 *  \param  f      The variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(File &input, float * const f);


/** \brief  Serializes a double variable to a binary file.
 *  \param  output  The file to write to.
 *  \param  d       The variable to write.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Write(File &output, const double d);


/** \brief  Deserializes a double variable from a binary file.
 *  \param  input  The file to read from.
 *  \param  d      The variable to read.
 *  \return True if the operation succeeded and false if there was some I/O error.
 */
bool Read(File &input, double * const d);


/** \brief  Serializes some sequence container variable to a binary file.
 *  \param  output     The file to write to.
 *  \param  container  The sequence container to serialize.
 *  \return True if the operation succeeded and false if there was some I/O error.
 *  \note   This only works if the sequence container contains a type for which a corresponding BinaryIO::Write() exists.
 */
template <typename SequenceContainer> bool Write(File &output, const SequenceContainer &container)
{
	const uint32_t size(static_cast<uint32_t>(container.size()));
	if (unlikely(not Write(output, size)))
		return false;

	for (typename SequenceContainer::const_iterator entry(container.begin()); entry != container.end(); ++entry) {
		if (unlikely(not Write(output, *entry)))
			return false;
	}

	return true;
}


/** \brief  Deserializes some sequence container variable from a binary file.
 *  \param  input      The file to read from.
 *  \param  container  The sequence container to deserialize.
 *  \return True if the operation succeeded and false if there was some I/O error.
 *  \note   This only works if the sequence container contains a type for which a corresponding BinaryIO::Read() exists.
 */
template <typename SequenceContainer> bool Read(File &input, SequenceContainer * const container)
{
	container->clear();

	uint32_t size;
	if (unlikely(not Read(input, &size)))
		return false;

	for (unsigned entry_no(0); entry_no < size; ++entry_no) {
		typename SequenceContainer::value_type entry;
		if (unlikely(not Read(input, &entry)))
			return false;

		container->insert(container->end(), entry);
	}

	return true;
}


/** \brief  Deserializes a GNU_HASH_SET from a binary file.
 *  \param  input  The file to read from.
 *  \param  set    The set to deserialize.
 *  \return True if the operation succeeded and false if there was some I/O error.
 *  \note   This only works if the GNU_HASH_SET contains a type for which a corresponding BinaryIO::Read() exists.
 */
template <typename Entry> bool Read(File &input, GNU_HASH_SET<Entry> * const set)
{
	set->clear();

	uint32_t size;
	if (unlikely(not Read(input, &size)))
		return false;

	for (unsigned entry_no(0); entry_no < size; ++entry_no) {
		Entry entry;
		if (unlikely(not Read(input, &entry)))
			return false;

		set->insert(entry);
	}

	return true;
}


/** \brief  Serializes some map variable to a binary file.
 *  \param  output  The file to write to.
 *  \param  map     The map to serialize.
 *  \return True if the operation succeeded and false if there was some I/O error.
 *  \note   This only works if the map contains a type for which a corresponding BinaryIO::Write() exists.
 */
template <typename Key, typename Value> bool Write(File &output, const std::map<Key, Value> &map)
{
	const uint32_t size(static_cast<uint32_t>(map.size()));
	if (unlikely(not Write(output, size)))
		return false;

	for (typename std::map<Key, Value>::const_iterator pair(map.begin()); pair != map.end(); ++pair) {
		if (unlikely(not Write(output, pair->first)))
			return false;
		if (unlikely(not Write(output, pair->second)))
			return false;
	}

	return true;
}


/** \brief  Deserializes some map variable from a binary file.
 *  \param  input  The file to read from.
 *  \param  map    The map to deserialize.
 *  \return True if the operation succeeded and false if there was some I/O error.
 *  \note   This only works if the map contains a type for which a corresponding BinaryIO::Read() exists.
 */
template <typename Key, typename Value> bool Read(File &input, std::map<Key, Value> * const map)
{
	map->clear();

	uint32_t size;
	if (unlikely(not Read(input, &size)))
		return false;

	for (unsigned pair_no(0); pair_no < size; ++pair_no) {
		Key key;
		if (unlikely(not Read(input, &key)))
			return false;
		Value value;
		if (unlikely(not Read(input, &value)))
			return false;

		(*map)[key] = value;
	}

	return true;
}


/** \brief  Serializes a map variable to a binary file.
 *  \param  output  The file to write to.
 *  \param  map     The map to serialize.
 *  \return True if the operation succeeded and false if there was some I/O error.
 *  \note   This only works if the map contains a type for which a corresponding BinaryIO::Write() exists.
 */
template <typename Key, typename Value> bool Write(File &output, const GNU_HASH_MAP<Key, Value> &map)
{
	const uint32_t size(map.size());
	if (unlikely(not Write(output, size)))
		return false;

	for (typename GNU_HASH_MAP<Key, Value>::const_iterator pair(map.begin()); pair != map.end(); ++pair) {
		if (unlikely(not Write(output, pair->first)))
			return false;
		if (unlikely(not Write(output, pair->second)))
			return false;
	}

	return true;
}


/** \brief  Deserializes a map variable from a binary file.
 *  \param  input  The file to read from.
 *  \param  map    The map to deserialize.
 *  \return True if the operation succeeded and false if there was some I/O error.
 *  \note   This only works if the map contains a type for which a corresponding BinaryIO::Read() exists.
 */
template <typename Key, typename Value> bool Read(File &input, GNU_HASH_MAP<Key, Value> * const map)
{
	map->clear();

	uint32_t size;
	if (unlikely(not Read(input, &size)))
		return false;

	for (unsigned pair_no(0); pair_no < size; ++pair_no) {
		Key key;
		if (unlikely(not Read(input, &key)))
			return false;
		Value value;
		if (unlikely(not Read(input, &value)))
			return false;

		(*map)[key] = value;
	}

	return true;
}


} // namespace BinaryIO


#endif // ifndef BINARY_IO_H
