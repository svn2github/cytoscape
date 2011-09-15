/** \file    BinaryIO.cc
 *  \brief   Implementation of binary I/O utility functions.
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

#include <BinaryIO.h>
#include <netinet/in.h>
#include <File.h>
#include <MathUtil.h>


namespace BinaryIO {


bool Write(std::ostream &output, const std::string &s)
{
	uint32_t size(static_cast<uint32_t>(s.size()));
	size = htonl(size);
	output.write(reinterpret_cast<const char *>(&size), sizeof size);
	if (output.fail())
		return false;

	output.write(s.c_str(), s.size());
	if (output.fail())
		return false;

	return true;
}


bool Write(char ** const memory, const std::string &s)
{
	uint32_t size(static_cast<uint32_t>(s.size()));
	size = htonl(size);
	std::memcpy(*memory, &size, sizeof size);
	*memory += sizeof size;

	std::memcpy(*memory, s.c_str(), s.size());
	*memory += s.size();

	return true;
}


bool Read(std::istream &input, std::string * const s)
{
	uint32_t size;
	input.read(reinterpret_cast<char *>(&size), sizeof size);
	if (input.fail())
		return false;

	size = ntohl(size);
	char buf[size];
	input.read(buf, size);
	if (input.fail())
		return false;

	*s = std::string(buf, size);

	return true;
}


bool Read(const char ** const memory, std::string * const s)
{
	uint32_t size;
	std::memcpy(reinterpret_cast<char *>(&size), *memory, sizeof size);
	*memory += sizeof size;

	size = ntohl(size);
	char buf[size];
	std::memcpy(buf, *memory, size);
	*memory += size;

	*s = std::string(buf, size);

	return true;
}


bool Write(std::ostream &output, const char ch)
{
	output.write(&ch, sizeof ch);
	return not output.fail();
}


bool Write(char ** const memory, const char ch)
{
	**memory = ch;
	++*memory;

	return true;
}


bool Read(std::istream &input, char * const ch)
{
	input.read(ch, sizeof *ch);
	return not input.fail();
}


bool Read(const char ** const memory, char * const ch)
{
	*ch = **memory;
	++*memory;

	return true;
}


bool Write(std::ostream &output, const bool b)
{
	const int8_t i8(b);
	output.write(reinterpret_cast<const char *>(&i8), sizeof i8);
	return not output.fail();
}


bool Write(char ** const memory, const bool b)
{
	const int8_t i8(b);
	std::memcpy(*memory, &i8, sizeof i8);
	++*memory;

	return true;
}


bool Read(std::istream &input, bool * const b)
{
	int8_t i8;
	input.read(reinterpret_cast<char *>(&i8), sizeof i8);
	*b = i8;
	return not input.fail();
}


bool Read(const char ** const memory, bool * const b)
{
	int8_t i8;
	std::memcpy(&i8, *memory, sizeof i8);
	++*memory;
	*b = i8;

	return true;
}


bool Write(std::ostream &output, const uint8_t u8)
{
	output.write(reinterpret_cast<const char *>(&u8), sizeof u8);
	return not output.fail();
}


bool Write(char ** const memory, const uint8_t u8)
{
	std::memcpy(*memory, &u8, sizeof u8);
	*memory += sizeof u8;
	return true;
}


bool Read(std::istream &input, uint8_t * const u8)
{
	input.read(reinterpret_cast<char *>(u8), sizeof *u8);
	return not input.fail();
}


bool Read(const char ** const memory, uint8_t * const u8)
{
	std::memcpy(u8, *memory, sizeof *u8);
	*memory += sizeof *u8;

	return true;
}


bool Write(std::ostream &output, const int8_t i8)
{
	output.write(reinterpret_cast<const char *>(&i8), sizeof i8);
	return not output.fail();
}


bool Write(char ** const memory, const int8_t i8)
{
	std::memcpy(*memory, &i8, sizeof i8);
	*memory += sizeof i8;
	return true;
}


bool Read(std::istream &input, int8_t * const i8)
{
	input.read(reinterpret_cast<char *>(i8), sizeof *i8);
	return not input.fail();
}


bool Read(const char ** const memory, int8_t * const i8)
{
	std::memcpy(i8, *memory, sizeof *i8);
	*memory += sizeof *i8;

	return true;
}


bool Write(std::ostream &output, const uint16_t u16)
{
	uint16_t be_u16;
	be_u16 = htons(u16);
	output.write(reinterpret_cast<const char *>(&be_u16), sizeof be_u16);
	return not output.fail();
}


bool Write(char ** const memory, const uint16_t u16)
{
	uint16_t be_u16;
	be_u16 = htons(u16);
	std::memcpy(*memory, &be_u16, sizeof be_u16);
	*memory += sizeof be_u16;
	return true;
}


bool Read(std::istream &input, uint16_t * const u16)
{
	uint16_t be_u16;
	input.read(reinterpret_cast<char *>(&be_u16), sizeof be_u16);
	*u16 = ntohs(be_u16);
	return not input.fail();
}


bool Read(const char ** const memory, uint16_t * const u16)
{
	uint16_t be_u16;
	std::memcpy(&be_u16, *memory, sizeof be_u16);
	*memory += sizeof be_u16;
	*u16 = ntohs(be_u16);

	return true;
}


bool Write(std::ostream &output, const int16_t i16)
{
	uint16_t be_u16;
	be_u16 = htons(i16);
	output.write(reinterpret_cast<const char *>(&be_u16), sizeof be_u16);
	return not output.fail();
}


bool Write(char ** const memory, const int16_t i16)
{
	int16_t be_i16;
	be_i16 = htons(i16);
	std::memcpy(*memory, &be_i16, sizeof be_i16);
	*memory += sizeof be_i16;
	return true;
}


bool Read(std::istream &input, int16_t * const i16)
{
	uint16_t be_u16;
	input.read(reinterpret_cast<char *>(&be_u16), sizeof be_u16);
	*i16 = ntohs(be_u16);
	return not input.fail();
}


bool Read(const char ** const memory, int16_t * const i16)
{
	int16_t be_i16;
	std::memcpy(&be_i16, *memory, sizeof be_i16);
	*memory += sizeof be_i16;
	*i16 = ntohs(be_i16);

	return true;
}


bool Write(std::ostream &output, const uint32_t u32)
{
	uint32_t be_u32;
	be_u32 = htonl(u32);
	output.write(reinterpret_cast<const char *>(&be_u32), sizeof be_u32);
	return not output.fail();
}


bool Write(char ** const memory, const uint32_t u32)
{
	uint32_t be_u32;
	be_u32 = htonl(u32);
	std::memcpy(*memory, &be_u32, sizeof be_u32);
	*memory += sizeof be_u32;
	return true;
}


bool Read(std::istream &input, uint32_t * const u32)
{
	uint32_t be_u32;
	input.read(reinterpret_cast<char *>(&be_u32), sizeof be_u32);
	*u32 = ntohl(be_u32);
	return not input.fail();
}


bool Read(const char ** const memory, uint32_t * const u32)
{
	uint32_t be_u32;
	std::memcpy(reinterpret_cast<char *>(&be_u32), *memory, sizeof be_u32);
	*memory += sizeof be_u32;
	*u32 = ntohl(be_u32);

	return true;
}


bool Write(std::ostream &output, const int32_t i32)
{
	uint32_t be_u32;
	be_u32 = htonl(i32);
	output.write(reinterpret_cast<const char *>(&be_u32), sizeof be_u32);
	return not output.fail();
}


bool Write(char ** const memory, const int32_t i32)
{
	int32_t be_i32;
	be_i32 = htonl(i32);
	std::memcpy(*memory, &be_i32, sizeof be_i32);
	*memory += sizeof be_i32;
	return true;
}


bool Read(std::istream &input, int32_t * const i32)
{
	uint32_t be_u32;
	input.read(reinterpret_cast<char *>(&be_u32), sizeof be_u32);
	*i32 = ntohl(be_u32);
	return not input.fail();
}


bool Read(const char ** const memory, int32_t * const i32)
{
	int32_t be_i32;
	std::memcpy(reinterpret_cast<char *>(&be_i32), *memory, sizeof be_i32);
	*memory += sizeof be_i32;
	*i32 = ntohl(be_i32);

	return true;
}


bool Write(std::ostream &output, const uint64_t u64)
{
	const uint32_t lower(static_cast<uint32_t>(u64 & 0xFFFFFFFFu));
	if (not Write(output, lower))
		return false;

	const uint32_t upper(static_cast<uint32_t>(u64 >> 32u));
	return Write(output, upper);
}


bool Write(char ** const memory, const uint64_t u64)
{
	const uint32_t lower(static_cast<uint32_t>(u64 & 0xFFFFFFFFu));
	if (not Write(memory, lower))
		return false;

	const uint32_t upper(static_cast<uint32_t>(u64 >> 32u));
	return Write(memory, upper);
}


bool Read(std::istream &input, uint64_t * const u64)
{
	uint32_t lower;
	if (not Read(input, &lower))
		return false;

	uint32_t upper;
	if (not Read(input, &upper))
		return false;

	*u64 = (static_cast<uint64_t>(upper) << 32u) | lower;
	return true;
}


bool Read(const char ** const memory, uint64_t * const u64)
{
	uint32_t lower;
	if (not Read(memory, &lower))
		return false;

	uint32_t upper;
	if (not Read(memory, &upper))
		return false;

	*u64 = (static_cast<uint64_t>(upper) << 32u) | lower;
	return true;
}


bool Write(std::ostream &output, const int64_t i64)
{
	const uint32_t lower(static_cast<uint32_t>(i64 & 0xFFFFFFFFu));
	if (not Write(output, lower))
		return false;

	const uint32_t upper(static_cast<uint32_t>(i64 >> 32u));
	return Write(output, upper);
}


bool Write(char ** const memory, const int64_t i64)
{
	const uint32_t lower(static_cast<uint32_t>(i64 & 0xFFFFFFFFu));
	if (not Write(memory, lower))
		return false;

	const uint32_t upper(static_cast<uint32_t>(i64 >> 32u));
	return Write(memory, upper);
}


bool Read(std::istream &input, int64_t * const i64)
{
	uint32_t lower;
	if (not Read(input, &lower))
		return false;

	uint32_t upper;
	if (not Read(input, &upper))
		return false;

	*i64 = (static_cast<uint64_t>(upper) << 32u) | lower;
	return true;
}


bool Read(const char ** const memory, int64_t * const i64)
{
	uint32_t lower;
	if (not Read(memory, &lower))
		return false;

	uint32_t upper;
	if (not Read(memory, &upper))
		return false;

	*i64 = (static_cast<int64_t>(upper) << 32u) | lower;
	return true;
}


bool Write(std::ostream &output, const float f)
{
	int exponent;
	int32_t mantissa;
	MathUtil::ExtractExponentAndMantissa(f, &exponent, &mantissa);
	if (not Write(output, static_cast<int32_t>(exponent)))
		return false;
	return Write(output, mantissa);
}


bool Write(char ** const memory, const float f)
{
	int exponent;
	int32_t mantissa;
	MathUtil::ExtractExponentAndMantissa(f, &exponent, &mantissa);
	if (not Write(memory, static_cast<int32_t>(exponent)))
		return false;
	return Write(memory, mantissa);
}


bool Read(std::istream &input, float * const f)
{
	int32_t exponent;
	if (not Read(input, &exponent))
		return false;

	int32_t mantissa;
	if (not Read(input, &mantissa))
		return false;

	MathUtil::ExponentAndMantissaToFloat(exponent, mantissa, f);
	return true;
}


bool Read(const char ** const memory, float * const f)
{
	int32_t exponent;
	if (not Read(memory, &exponent))
		return false;

	int32_t mantissa;
	if (not Read(memory, &mantissa))
		return false;

	MathUtil::ExponentAndMantissaToFloat(exponent, mantissa, f);
	return true;
}


bool Write(std::ostream &output, const double d)
{
	int exponent;
	int64_t mantissa;
	MathUtil::ExtractExponentAndMantissa(d, &exponent, &mantissa);
	if (not Write(output, static_cast<int32_t>(exponent)))
		return false;
	return Write(output, mantissa);
}


bool Write(char ** const memory, const double d)
{
	int exponent;
	int64_t mantissa;
	MathUtil::ExtractExponentAndMantissa(d, &exponent, &mantissa);
	if (not Write(memory, static_cast<int32_t>(exponent)))
		return false;
	return Write(memory, mantissa);
}


bool Read(std::istream &input, double * const d)
{
	int32_t exponent;
	if (not Read(input, &exponent))
		return false;

	int64_t mantissa;
	if (not Read(input, &mantissa))
		return false;

	MathUtil::ExponentAndMantissaToDouble(exponent, mantissa, d);
	return true;
}


bool Read(const char ** const memory, double * const d)
{
	int32_t exponent;
	if (not Read(memory, &exponent))
		return false;

	int64_t mantissa;
	if (not Read(memory, &mantissa))
		return false;

	MathUtil::ExponentAndMantissaToDouble(exponent, mantissa, d);
	return true;
}


bool Write(File &output, const std::string &s)
{
	uint32_t size(static_cast<uint32_t>(s.size()));
	size = htonl(size);
	output.write(reinterpret_cast<const char *>(&size), sizeof size);
	if (output.fail())
		return false;

	output.write(s.c_str(), s.size());
	if (output.fail())
		return false;

	return true;
}


bool Read(File &input, std::string * const s)
{
	uint32_t size;
	input.read(reinterpret_cast<char *>(&size), sizeof size);
	if (input.fail())
		return false;

	size = ntohl(size);
	char buf[size];
	input.read(buf, size);
	if (input.fail())
		return false;

	*s = std::string(buf, size);

	return true;
}


bool Write(File &output, const char ch)
{
	output.write(&ch, sizeof ch);
	return not output.fail();
}


bool Read(File &input, char * const ch)
{
	input.read(ch, sizeof *ch);
	return not input.fail();
}


bool Write(File &output, const bool b)
{
	const int8_t i8(b);
	output.write(reinterpret_cast<const char *>(&i8), sizeof i8);
	return not output.fail();
}


bool Read(File &input, bool * const b)
{
	int8_t i8;
	input.read(reinterpret_cast<char *>(&i8), sizeof i8);
	*b = i8;
	return not input.fail();
}


bool Write(File &output, const uint8_t u8)
{
	output.write(reinterpret_cast<const char *>(&u8), sizeof u8);
	return not output.fail();
}


bool Read(File &input, uint8_t * const u8)
{
	input.read(reinterpret_cast<char *>(u8), sizeof *u8);
	return not input.fail();
}


bool Write(File &output, const int8_t i8)
{
	output.write(reinterpret_cast<const char *>(&i8), sizeof i8);
	return not output.fail();
}


bool Read(File &input, int8_t * const i8)
{
	input.read(reinterpret_cast<char *>(i8), sizeof *i8);
	return not input.fail();
}


bool Write(File &output, const uint16_t u16)
{
	uint16_t be_u16;
	be_u16 = htons(u16);
	output.write(reinterpret_cast<const char *>(&be_u16), sizeof be_u16);
	return not output.fail();
}


bool Read(File &input, uint16_t * const u16)
{
	uint16_t be_u16;
	input.read(reinterpret_cast<char *>(&be_u16), sizeof be_u16);
	*u16 = ntohs(be_u16);
	return not input.fail();
}


bool Write(File &output, const int16_t i16)
{
	uint16_t be_u16;
	be_u16 = htons(i16);
	output.write(reinterpret_cast<const char *>(&be_u16), sizeof be_u16);
	return not output.fail();
}


bool Read(File &input, int16_t * const i16)
{
	uint16_t be_u16;
	input.read(reinterpret_cast<char *>(&be_u16), sizeof be_u16);
	*i16 = ntohs(be_u16);
	return not input.fail();
}


bool Write(File &output, const uint32_t u32)
{
	uint32_t be_u32;
	be_u32 = htonl(u32);
	output.write(reinterpret_cast<const char *>(&be_u32), sizeof be_u32);
	return not output.fail();
}


bool Read(File &input, uint32_t * const u32)
{
	uint32_t be_u32;
	input.read(reinterpret_cast<char *>(&be_u32), sizeof be_u32);
	*u32 = ntohl(be_u32);
	return not input.fail();
}


bool Write(File &output, const int32_t i32)
{
	uint32_t be_u32;
	be_u32 = htonl(i32);
	output.write(reinterpret_cast<const char *>(&be_u32), sizeof be_u32);
	return not output.fail();
}


bool Read(File &input, int32_t * const i32)
{
	uint32_t be_u32;
	input.read(reinterpret_cast<char *>(&be_u32), sizeof be_u32);
	*i32 = ntohl(be_u32);
	return not input.fail();
}


bool Write(File &output, const uint64_t u64)
{
	const uint32_t lower(static_cast<uint32_t>(u64 & 0xFFFFFFFFu));
	if (not Write(output, lower))
		return false;

	const uint32_t upper(static_cast<uint32_t>(u64 >> 32u));
	return Write(output, upper);
}


bool Read(File &input, uint64_t * const u64)
{
	uint32_t lower;
	if (not Read(input, &lower))
		return false;

	uint32_t upper;
	if (not Read(input, &upper))
		return false;

	*u64 = (static_cast<uint64_t>(upper) << 32u) | lower;
	return true;
}


bool Write(File &output, const int64_t i64)
{
	const uint32_t lower(static_cast<uint32_t>(i64 & 0xFFFFFFFFu));
	if (not Write(output, lower))
		return false;

	const uint32_t upper(static_cast<uint32_t>(static_cast<uint64_t>(i64) >> 32u));
	return Write(output, upper);
}


bool Read(File &input, int64_t * const i64)
{
	uint32_t lower;
	if (not Read(input, &lower))
		return false;

	uint32_t upper;
	if (not Read(input, &upper))
		return false;

	*i64 = (static_cast<uint64_t>(upper) << 32u) | lower;
	return true;
}


bool Write(File &output, const float f)
{
	int exponent;
	int32_t mantissa;
	MathUtil::ExtractExponentAndMantissa(f, &exponent, &mantissa);
	if (not Write(output, static_cast<int32_t>(exponent)))
		return false;
	return Write(output, mantissa);
}


bool Read(File &input, float * const f)
{
	int32_t exponent;
	if (not Read(input, &exponent))
		return false;

	int32_t mantissa;
	if (not Read(input, &mantissa))
		return false;

	MathUtil::ExponentAndMantissaToFloat(exponent, mantissa, f);
	return true;
}


bool Write(File &output, const double d)
{
	int exponent;
	int64_t mantissa;
	MathUtil::ExtractExponentAndMantissa(d, &exponent, &mantissa);
	if (not Write(output, static_cast<int32_t>(exponent)))
		return false;
	return Write(output, mantissa);
}


bool Read(File &input, double * const d)
{
	int32_t exponent;
	if (not Read(input, &exponent))
		return false;

	int64_t mantissa;
	if (not Read(input, &mantissa))
		return false;

	MathUtil::ExponentAndMantissaToDouble(exponent, mantissa, d);
	return true;
}


} // namespace BinaryIO
