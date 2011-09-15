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

#include <Serialize.h>


namespace Serialization {


PointerToPointer pointer_map_;
const File *last_file=NULL;
const char **last_memory=NULL;

void ClearGlobals(File &, void * const)
{
	last_file = NULL;
	last_memory = NULL;
	pointer_map_.clear();
}



/**
 * \brief   The "SerializePointer" function serializes a function without attempting to dereference the
 *          referent. Sometimes you only want to store the pointer.
*/
void SerializePointer(File &file, const Direction direction, void * const &pointer)
{
	/**
	 * Because there are differences in pointer size between 32 and 64 bit platforms, we have to perform
	 * some translation here. We cannot serialized directly into pointer because our
	 * SerializedPointerFormat is always 64 bits long which would overflow a 32 bit pointer value if
	 * assigned directly.
	 */
	SerializedPointerFormat temp;

	if (direction == OUT)
		temp = reinterpret_cast<SerializedPointerFormat>(pointer);

	SerializePrimitive(file, direction, temp);

	if (direction == IN) {
		void *temp2 = reinterpret_cast<void *>(temp);
		void *& pointer2 = const_cast<void *&>(pointer);
		pointer2 = temp2;
	}
}


void SerializePointer(char ** const memory, const Direction direction, void * const &pointer)
{
	/**
	 * Because there are differences in pointer size between 32 and 64 bit platforms, we have to perform
	 * some translation here. We cannot serialized directly into pointer because our
	 * SerializedPointerFormat is always 64 bits long which would overflow a 32 bit pointer value if
	 * assigned directly.
	 */
	SerializedPointerFormat temp;

	if (direction == OUT)
		temp = reinterpret_cast<SerializedPointerFormat>(pointer);

	SerializePrimitive(memory, direction, temp);

	if (direction == IN) {
		void *temp2 = reinterpret_cast<void *>(temp);
		void *& pointer2 = const_cast<void *&>(pointer);
		pointer2 = temp2;
	}
}


std::string SerializationErrorContext(File &file, const off_t error_offset)
{
	off_t file_offset(std::max(0, static_cast<int32_t>(error_offset) - 512));
	file.seek(file_offset);
	char buffer[1024];
	file.read(buffer, sizeof(buffer));
	return StringUtil::Format("At file offset:%ld (0x%lx):\n", static_cast<long int>(error_offset),
		static_cast<long unsigned>(error_offset)) +  MiscUtil::HexDump(buffer, sizeof(buffer), file_offset, 16);
}


int32_t SerializeClassNameAndVersion(File &file, const Direction direction, const char *class_name, const int32_t current_version)
{
	if (direction == Serialization::OUT) {
		const std::string class_name_and_version(StringUtil::Format("%s:%6.6u", class_name, current_version));
		Serialize(file, Serialization::OUT, class_name_and_version.data(), class_name_and_version.length());

		return current_version;
	}

	//
	// If we make it here, direction == Serialization::IN
	//

	const size_t class_name_length(std::strlen(class_name));
	char class_name_buf[class_name_length]; // No room for a trailing zero byte!
	if (unlikely(file.read(class_name_buf, class_name_length) != class_name_length)
	    or std::strncmp(class_name_buf, class_name, class_name_length) != 0)
		ThrowSerializationException(file, direction,
					    StringUtil::Format("Wrong class name in file \"%s\", while looking for \"%s\"!",
							       StringUtil::CStyleEscape(std::string(class_name_buf, class_name_length)).c_str(),
							       class_name));

	// Now try to get a colon followed by a six digit version code.  If can't find that, we assume we have an old archive without versioning header:
	char colon_and_version[1 + 6 + 1]; // Includes room for a trailing zero byte.
	if (file.read(colon_and_version, sizeof(colon_and_version) - 1) != sizeof(colon_and_version) - 1 or colon_and_version[0] != ':') {
		file.seek(-7, SEEK_CUR);
		return 0;
	}

	colon_and_version[sizeof(colon_and_version) - 1] = '\0'; // Add a terminating NUL.
	int32_t version;
	if (unlikely(not StringUtil::ToNumber(colon_and_version + 1, &version, 10))) {
		file.seek(-7, SEEK_CUR);
		return 0;
	}

	return version;
}


int32_t SerializeClassNameAndVersion(char ** const memory, const Direction direction, const char *class_name, const int32_t current_version)
{
	if (direction == Serialization::OUT) {
		const std::string class_name_and_version(StringUtil::Format("%s:%6.6u", class_name, current_version));
		Serialize(memory, Serialization::OUT, class_name_and_version.data(), class_name_and_version.length());

		return current_version;
	}

	//
	// If we make it here, direction == Serialization::IN
	//

	const size_t class_name_length(std::strlen(class_name));
	if (unlikely(std::strncmp(*memory, class_name, class_name_length) != 0))
		throw Exception(StringUtil::Format("in SerializeClassNameAndVersion: wrong class name \"%s\", while looking for \"%s\"!",
						   StringUtil::CStyleEscape(std::string(*memory, class_name_length)).c_str(), class_name));
	*memory += class_name_length;

	// Now try to get a colon followed by a six digit version code.  If can't find that, we assume we have an old archive without versioning header:
	if (**memory != ':')
		return 0;
	++*memory;

	int32_t version;
	if (unlikely(not StringUtil::ToNumber(std::string(*memory, 6), &version, 10))) {
		--*memory;
		return 0;
	}
	*memory += 6;

	return version;
}


} // namespace Serialization
