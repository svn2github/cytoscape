/** \file    MiscUtil.cc
 *  \brief   Declarations of miscellaneous utility functions.
 *  \author  Dr. Johannes Ruscheinski
 *  \author  Dr. Gordon W. Paynter
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

#include <MiscUtil.h>
#include <cctype>
#include <cerrno>
#include <climits>
#include <cstdlib>
#include <cstring>
#include <monetary.h>
#include <fcntl.h>
#include <signal.h>
#include <sys/wait.h>
#ifndef __linux__
#       include <sys/types.h>
#       include <sys/sysctl.h>
#endif
#include <unistd.h>
#include <DnsUtil.h>
#include <MsgUtil.h>
#include <NetUtil.h>
#include <ProcessUtil.h>
#include <StringUtil.h>
#include <ThreadUtil.h>


#define DIM(array)	(sizeof(array) / sizeof(array[0]))


namespace MiscUtil {


char HexDigit(const unsigned value)
{
	switch (value) {
	case 0:
		return '0';
	case 1:
		return '1';
	case 2:
		return '2';
	case 3:
		return '3';
	case 4:
		return '4';
	case 5:
		return '5';
	case 6:
		return '6';
	case 7:
		return '7';
	case 8:
		return '8';
	case 9:
		return '9';
	case 0xA:
		return 'A';
	case 0xB:
		return 'B';
	case 0xC:
		return 'C';
	case 0xD:
		return 'D';
	case 0xE:
		return 'E';
	case 0xF:
		return 'F';
	default:
		MsgUtil::Error("in HexDigit: invalid value %u!", value);
		return ' '; // Keep the compiler happy!
	}
}


std::string GetEnv(const char * const name)
{
	const char *value = ::getenv(name);
	if (value == NULL) {
		std::string err_msg("in MiscUtil::GetEnv: ::getenv(\"");
		err_msg += name;
		err_msg += "\") failed!";
		throw Exception(err_msg);
	}

	return value;
}


std::string SafeGetEnv(const char * const name)
{
	const char *value = ::getenv(name);
	return value == NULL ? "" : value;
}


void SetEnv(const std::string &name, const std::string &value, const bool overwrite)
{
	if (unlikely(::setenv(name.c_str(), value.c_str(), overwrite ? 1 : 0) != 0))
		throw Exception("in MiscUtil::SetEnv: setenv(3) failed!");
}


bool EnvironmentVariableExists(const std::string &name)
{
	const char *value = ::getenv(name.c_str());
	return value != NULL;
}


std::string CharToBitString(char ch)
{
	unsigned char uch = static_cast<unsigned char>(ch);
	std::string ret_val;
	const unsigned MASK = 1u << (CHAR_BIT - 1);
	for (unsigned i = 0; i < CHAR_BIT; ++i) {
		ret_val += (uch & MASK) ? '1' : '0';
		uch = static_cast<unsigned char>(uch << 1u);
	}

	return ret_val;
}


std::string StringToBitString(const std::string &s, const std::string &separator)
{
	std::string ret_val;
	for (std::string::const_iterator ch(s.begin()); ch != s.end(); ++ch) {
		if (ch != s.begin())
			ret_val += separator;
		ret_val += CharToBitString(*ch);
	}

	return ret_val;
}


void ByteSwap(int32_t * pointer, unsigned num_bytes)
{
	for (unsigned i = 0; i < num_bytes/sizeof(int); i++) {
		*pointer = ((*pointer & 0x000000ff) << 24) |
			   ((*pointer & 0x0000ff00) << 8) |
			   ((*pointer & 0x00ff0000) >> 8) |
			   ((*pointer & 0xff000000) >> 24);
		pointer++;
	}
}


std::string HexPrint(const int * const pointer, const unsigned num_rows) {

	std::string output;
	for (unsigned i = 0; i < 8*num_rows; i += 8) {

		// Do a byte swap.  i386-endianness specific.
		int p[32/4];
		std::memcpy(p, pointer + i, 32);
		ByteSwap((int *)p, 32);

		char hex_output[100];
		std::sprintf(hex_output, "%p\t%8.8x %8.8x %8.8x %8.8x %8.8x %8.8x %8.8x %8.8x   ",
			     pointer + i, p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7]);
		output += hex_output;

		for (unsigned j = 0; j < 8; ++j) {
			unsigned word = p[j];
			for (unsigned k = 0; k < sizeof(int *); ++k) {
				char ch = static_cast<char>(word >> 8*(3-k));
				if (ch >= 32 and ch <= 126)
					output += ch;
				else
					output += ".";
			}
		}
		output += "\n";
	}
	return output;
}


std::string MemInfo::toString() const
{
	std::string retval;
	retval.reserve(500);

	retval += "MemTotal:     ";
	retval += StringUtil::ToString(mem_total_, 10, 8);
	retval += " kB\n";

	retval += "MemFree:      ";
	retval += StringUtil::ToString(mem_free_, 10, 8);
	retval += " kB\n";

	retval += "Buffers:      ";
	retval += StringUtil::ToString(buffers_, 10, 8);
	retval += " kB\n";

	retval += "Cached:       ";
	retval += StringUtil::ToString(cached_, 10, 8);
	retval += " kB\n";

	retval += "SwapCached:   ";
	retval += StringUtil::ToString(swap_cached_, 10, 8);
	retval += " kB\n";

	retval += "Active:       ";
	retval += StringUtil::ToString(active_, 10, 8);
	retval += " kB\n";

	retval += "Inactive:     ";
	retval += StringUtil::ToString(inactive_, 10, 8);
	retval += " kB\n";

	return retval;
}


bool GetMemInfo(MemInfo * const mem_info)
{
	std::ifstream input("/proc/meminfo");
	if (input.fail())
		return false;

	std::memset(mem_info, '\0', sizeof *mem_info);

	std::string line;
	while (std::getline(input, line)) {
		const std::string::size_type colon_pos(line.find(':'));
		if (unlikely(colon_pos == std::string::npos))
			continue;
		const std::string label(line.substr(0, colon_pos));
		if (label == "MemTotal")
			MSG_UTIL_ASSERT(std::sscanf(line.c_str() + colon_pos + 1, "%lu", &mem_info->mem_total_) == 1);
		else if (label == "MemFree")
			MSG_UTIL_ASSERT(std::sscanf(line.c_str() + colon_pos + 1, "%lu", &mem_info->mem_free_) == 1);
		else if (label == "Buffers")
			MSG_UTIL_ASSERT(std::sscanf(line.c_str() + colon_pos + 1, "%lu", &mem_info->buffers_) == 1);
		else if (label == "Cached")
			MSG_UTIL_ASSERT(std::sscanf(line.c_str() + colon_pos + 1, "%lu", &mem_info->cached_) == 1);
		else if (label == "SwapCached")
			MSG_UTIL_ASSERT(std::sscanf(line.c_str() + colon_pos + 1, "%lu", &mem_info->swap_cached_) == 1);
		else if (label == "Active")
			MSG_UTIL_ASSERT(std::sscanf(line.c_str() + colon_pos + 1, "%lu", &mem_info->active_) == 1);
		else if (label == "Inactive")
			MSG_UTIL_ASSERT(std::sscanf(line.c_str() + colon_pos + 1, "%lu", &mem_info->inactive_) == 1);
	}

	return true;
}


std::string GetLocallyUniqueID()
{
	static unsigned generation_number(1);

        return StringUtil::ToString(getpid()) + ":" + StringUtil::ToString(generation_number++) + ":"
               + StringUtil::ToString(std::time(NULL));
}


std::string GetGloballyUniqueID()
{
	static std::string dotted_quad_as_string;
	if (dotted_quad_as_string.empty()) {
		std::list<in_addr_t> ip_addresses;
		NetUtil::GetLocalIPv4Addrs(&ip_addresses);

		const in_addr_t LOCALHOST(htonl((127u << 24u) + 1u)); // We want to skip 127.0.0.1
		bool found_at_least_one(false);
		for (std::list<in_addr_t>::const_iterator ip_address(ip_addresses.begin());
		     ip_address != ip_addresses.end(); ++ip_address)
		{
			if (*ip_address == LOCALHOST)
				continue;

			found_at_least_one = true;
			NetUtil::NetworkAddressToString(*ip_address, &dotted_quad_as_string);
			break;
		}

		if (not found_at_least_one)
			throw Exception("in MiscUtil::GetGloballyUniqueID: could not find a useful IP "
						 "address!");
	}

	return dotted_quad_as_string + ":" + GetLocallyUniqueID();
}


void BackTrace(const std::string &message)
{
	const pid_t process_id(::getpid());
	const std::string trace_filename("/tmp/" + MsgUtil::GetProgName() + "." + StringUtil::ToString(process_id)
					 + ".trace");
	errno = 0;
	const int trace_fd = ::open(trace_filename.c_str(), O_CREAT | O_APPEND | O_WRONLY, 0600);
	if (unlikely(trace_fd == -1))
		throw Exception("in MiscUtil::BackTrace: can't open \"" + trace_filename + "\" for writing ("
					 + MsgUtil::ErrnoToString() + ")!");

	if (::write(trace_fd, "BackTrace: ", 11) == -1)
		throw Exception("in MiscUtil::BackTrace: write(2) failed (" + MsgUtil::ErrnoToString()
					 + ")!");
	if (::write(trace_fd, message.c_str(), message.size()) == -1)
		throw Exception("in MiscUtil::BackTrace: write(2) failed (" + MsgUtil::ErrnoToString()
					 + ")!");
	if (::write(trace_fd, "\n", 1) == -1)
		throw Exception("in MiscUtil::BackTrace: write(2) failed (" + MsgUtil::ErrnoToString()
					 + ")!");

	std::string slave_pty_name;
	int pty_master_fd;
	const pid_t pid(ProcessUtil::PtyFork(&pty_master_fd, &slave_pty_name));
	if (pid < 0) {
		/* We weren't able to create the new process, very likely due to the system being out of resources! */
		::close(pty_master_fd);
		throw Exception("in MiscUtil::BackTrace: ProcessUtil::PtyFork() failed ("
					 + MsgUtil::ErrnoToString() + ")!");
	}
	else if (pid > 0) { // We're the parent process.
		::sleep(5);

		if (::write(pty_master_fd, "backtrace\n", 10) == -1) {
			::close(pty_master_fd);
			::kill(pid, SIGKILL);
			int status;
			::waitpid(pid, &status, 0);
			throw Exception("in MiscUtil::BackTrace: write(2) to child failed (1) ("
						 + MsgUtil::ErrnoToString() + ")!");
		}

		::sleep(5);

		char buf[512];
		ssize_t count;
		while ((count = ::read(pty_master_fd, buf, sizeof buf)) > 0)
			::write(trace_fd, buf, count);

		if (::write(pty_master_fd, "quit\nyes\n", 9) == -1) {
			::close(pty_master_fd);
			::kill(pid, SIGKILL);
			int status;
			::waitpid(pid, &status, 0);
			throw Exception("in MiscUtil::BackTrace: write(2) to child failed (2) ("
						 + MsgUtil::ErrnoToString() + ")!");
		}

		int status;
		if (::waitpid(pid, &status, 0) == -1) {
			throw Exception("in MiscUtil::BackTrace: waitpid(2) to child failed ("
						 + MsgUtil::ErrnoToString() + ")!");
			::close(pty_master_fd);
		}

		::close(pty_master_fd);
	}
	else { // We're the child process.
		::close(pty_master_fd);
		::execl(GDB, "gdb", MsgUtil::GetProgName().c_str(), StringUtil::ToString(process_id).c_str(), reinterpret_cast<char *>(NULL));
	}
}


unsigned CountBits(const unsigned word)
{
	unsigned mask(1), count(0);
	for (unsigned bit = 0; bit < sizeof(word) * 8; ++bit) {
		if (word & mask)
			++count;
		mask <<= 1u;
	}

	return count;
}


std::string GetCurrencyString(const double amount)
{
	char buf[30 + 1];
	errno = 0;
	::strfmon(buf, sizeof buf, "%n", amount);
	if (unlikely(errno != 0))
		throw Exception("in MiscUtil::GetCurrencyString: strfmon(3) failed ("
				+ MsgUtil::ErrnoToString() + ")!");


	return buf;
}


unsigned GetCpuCount()
{
#ifndef __linux__
	std::ifstream proc_cpuinfo("/proc/cpuinfo");
	if (unlikely(proc_cpuinfo.fail()))
		throw Exception("in MiscUtil::GetCpuCount: can't open \"/proc/cpuinfo\" for reading!");

	unsigned cpu_count(0);
	while (proc_cpuinfo) {
		std::string line;
		std::getline(proc_cpuinfo, line);
		size_t pos;
		if (not line.empty()) {
			pos = line.find("processor");
			if (pos != std::string::npos)
				++cpu_count;
		}
	}

	return cpu_count;
#else
	int mib[4];
	size_t len = sizeof(numCPU); 

	/* set the mib for hw.ncpu */
	mib[0] = CTL_HW;
	mib[1] = HW_AVAILCPU;  // alternatively, try HW_NCPU;

	/* get the number of CPUs from the system */
	::sysctl(mib, 2, &numCPU, &len, NULL, 0);

	if (numCPU < 1) {
		mib[1] = HW_NCPU;
		::sysctl(mib, 2, &numCPU, &len, NULL, 0);

		if (numCPU < 1)
			numCPU = 1;
	}

	return numCPU;
#endif
}


double GetLoadAverage(const LoadAveragingInterval load_averaging_interval)
{
	double averages[3];
	const int no_of_averages_returned = ::getloadavg(averages, DIM(averages));
	if (unlikely(no_of_averages_returned != DIM(averages)))
		throw Exception("in MiscUtil::GetLoadAverage: getloadavg(3) failed!");
	switch (load_averaging_interval) {
	case ONE_MINUTE:
		return averages[0];
	case FIVE_MINUTES:
		return averages[1];
	case FIFTEEN_MINUTES:
		return averages[2];
	default:
		throw Exception("in MiscUtil::GetLoadAverage: unexpected load averaging interval "
				+ StringUtil::ToString(load_averaging_interval) + "!");
	}
}


bool IsValidReadMemory(const void * const address, const size_t length)
{
	void *junk = const_cast<void*>(alloca(length));
	std::memcpy(junk, address, length);
	return true;
}


std::string HexDump(const char * const buffer, const unsigned amount, const unsigned outer_offset, const unsigned width)
{
	// Make room for outputting the char, 3 characters for hexvalue and space plus line specific data like
	// newline characters, leading offset numbers and a bit of whitespace
	char *return_value = static_cast<char *>(alloca(amount * 5));
	char *output = return_value;

	char *text = static_cast<char *>(alloca(width + 1)); // A line of text "width" bytes wide
	char *hex = static_cast<char *>(alloca(width * 3 + 1)); // the same text as 2 digit hex values separated by ' '

	unsigned text_offset(0);
	unsigned inner_offset(0);

	for (const char *pos = buffer; pos < buffer + amount; ++pos) {
		if (std::isgraph(*pos) and ::isascii(*pos))
			text[text_offset] = *pos;
		else
			text[text_offset] = '.';

		std::sprintf(hex + (3 * text_offset), "%2.2x ", int(static_cast<unsigned char>(*pos)));

		if (++text_offset >= width) {
			text[text_offset] = '\0';
			output += sprintf(output, "%8.8x ", static_cast<unsigned int>(outer_offset + inner_offset));
			output += sprintf(output, "%s %s\n", text, hex);
			text_offset = 0;
			inner_offset += width;
		}
	}

	return return_value;
}


std::string UnixCrypt(const std::string &unencrypted_password, const std::string &salt)
{
	const std::string actual_salt(salt.empty()
				      ? StringUtil::GenerateRandomString(2, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789./")
				      : salt);
	return ::crypt(unencrypted_password.c_str(), actual_salt.c_str());
}


char *GetNearestAddressInIncrementsOfPageSize(char * const address, const AddressRoundingMode address_rounding_mode)
{
	static const ptrdiff_t page_size(::sysconf(_SC_PAGESIZE));
	if (address_rounding_mode == UP)
		return reinterpret_cast<char *>((reinterpret_cast<ptrdiff_t>(address + page_size - 1) / page_size) * page_size);
	else // Round down.
		return reinterpret_cast<char *>((reinterpret_cast<ptrdiff_t>(address) / page_size) * page_size);
}


off_t GetNearestFileOffsetInIncrementsOfPageSize(const off_t offset, const AddressRoundingMode address_rounding_mode)
{
	static const ptrdiff_t page_size(::sysconf(_SC_PAGESIZE));
	if (address_rounding_mode == UP)
		return ((offset + page_size - 1) / page_size) * page_size;
	else // Round down.
		return (offset / page_size) * page_size;
}


unsigned long StackUsage() __attribute__((constructor));
unsigned long StackUsage()
{
	// Whenever we come into the function, stack_top sits at the top of the stack. By subtracting his address, from
	// the address of stack_base, we get the stack size
	unsigned long stack_top = 1;
	(void) stack_top;

	// The first time this function is called, it initializes stack_base.
	static unsigned long *stack_base = &stack_top;
	unsigned long rval = sizeof(unsigned long) * (stack_base - &stack_top);

	return rval;
}


namespace {


inline bool CompareOnStringLength(const std::pair<std::string, unsigned> &string_and_rank1, const std::pair<std::string, unsigned> &string_and_rank2)
{
	return string_and_rank1.first.length() < string_and_rank2.first.length();
}


class SubstringMatch {
        std::string string_to_match_;
	bool case_sensitive_;
public:
        SubstringMatch(const std::string &string_to_match, const bool case_sensitive)
		: string_to_match_(string_to_match), case_sensitive_(case_sensitive) { }
	bool operator()(const std::pair<std::string, unsigned> &string_and_rank) const
		{ return case_sensitive_ ? string_and_rank.first.find(string_to_match_) != std::string::npos
		                         : ::strcasestr(string_and_rank.first.c_str(), string_to_match_.c_str()) != NULL; }
};


inline bool CompareOnRank(const std::pair<std::string, unsigned> &string_and_rank1, const std::pair<std::string, unsigned> &string_and_rank2)
{
	return string_and_rank1.second < string_and_rank2.second;
}


} // unnamed namespace


void SubstringMerge(std::vector<std::string> * const strings, const bool case_sensitive)
{
	// Nothing to be done?
	if (unlikely(strings->empty()))
		return;

	// Create an array of strings and their associated rank (= input order) and the sort this array in increasing string-length order:
	GNU_HASH_SET<std::string> already_seen;
	std::vector< std::pair<std::string, unsigned> > strings_and_ranks;
	strings_and_ranks.reserve(strings->size());
	unsigned rank(1);
	for (std::vector<std::string>::const_iterator string(strings->begin()); string != strings->end(); ++string, ++rank) {
		// Avoid duplicates:
		if (already_seen.find(*string) != already_seen.end())
			continue;
		already_seen.insert(*string);

		// In order to only match entire words we all a single space to each end of the string.  This prevents "formation" to be considered a
		// substring of "information" and similar cases:
		strings_and_ranks.push_back(std::pair<std::string, unsigned>(" " + *string + " ", rank));
	}
	std::sort(strings_and_ranks.begin(), strings_and_ranks.end(), CompareOnStringLength);

	// Determined the merged strings and save them in "results" for later sorting.  We do this by going through "strings_and_ranks" which is now in order of
	// increasing string length and take one string at a time and compare it against all longer strings.  If we find that our string is a substring of a longer
	// string, we just ignore it.  If the string is no substring of any other string we store it in "results."
	std::vector< std::pair<std::string, unsigned> > results;
	results.reserve(strings->size());
	for (std::vector< std::pair<std::string, unsigned> >::const_iterator string_and_rank(strings_and_ranks.begin());
	     string_and_rank != strings_and_ranks.end(); ++string_and_rank)
	{
		bool substring_match_found(false);
		for (std::vector< std::pair<std::string, unsigned> >::const_iterator string_and_rank2(string_and_rank + 1);
		     string_and_rank2 != strings_and_ranks.end(); ++string_and_rank2)
		{
			// Skip comparisons against strings that are no longer than the string that is currently under consideration:
			if (string_and_rank2->first.length() == string_and_rank->first.length())
				continue;

			// Check to see if the current string under consideration ("string_and_rank") is a substring of a longer string:
			if (std::find_if(string_and_rank2, static_cast<std::vector< std::pair<std::string, unsigned> >::const_iterator>(strings_and_ranks.end()),
					 SubstringMatch(string_and_rank->first, case_sensitive)) != strings_and_ranks.end())
			{
				substring_match_found = true;
				break;
			}
		}

		// If the current string under consideration was not a substring of a longer string, we keep it:
		if (not substring_match_found)
			results.push_back(*string_and_rank);
	}

	// Restore the input order (rank) and return the merged strings in "strings":
	strings->clear();
	std::sort(results.begin(), results.end(), CompareOnRank);
	for (std::vector< std::pair<std::string, unsigned> >::iterator result(results.begin()); result != results.end(); ++result) {
		result->first = result->first.substr(1, result->first.length() - 2); // Remove the spaces we added at both ends.
		strings->push_back(result->first);
	}
}


const std::set<char> &GetWhiteSpaceSet()
{
	static std::set<char> white_space_set;
	if (white_space_set.empty()) {
		static ThreadUtil::Mutex mutex;
		ThreadUtil::MutexLocker locker(&mutex);
		if (white_space_set.empty()) {
			for (std::string::const_iterator ch(StringUtil::WHITE_SPACE.begin()); ch != StringUtil::WHITE_SPACE.end(); ++ch)
				white_space_set.insert(*ch);
		}
	}

	return white_space_set;
}


void FdSetToFdVector(const fd_set &set, std::vector<int> * const vector)
{
	for (int fd(0); static_cast<unsigned>(fd) < ProcessUtil::GetMaxOpenFileDescriptorCount(); ++fd) {
		if (FD_ISSET(fd, &set))
			vector->push_back(fd);
	}
}


} // namespace MiscUtil
