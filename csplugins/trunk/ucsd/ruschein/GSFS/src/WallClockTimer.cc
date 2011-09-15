/** \file    WallClockTimer.cc
 *  \brief   Implementation of class WallClockTimer.
 *  \author  Dr. Johannes Ruscheinski
 *  \author  Wagner Truppel
 */

/*
 *  Copyright 2003-2008 Project iVia.
 *  Copyright 2003-2008 The Regents of The University of California.
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

#include <WallClockTimer.h>
#include <cerrno>
#include <ctime>
#include <Compiler.h>
#include <Exception.h>


#ifdef __MACH__
#      include <stdlib.h>
#      include <sys/time.h>
#      include <mach/mach.h>
#      include <mach/mach_types.h>
#      include <mach/clock.h>
#endif


WallClockTimer::WallClockTimer(const WallClockTimerType timer_type, const std::string &name)
	: is_running_(false), time_(0.0), name_(name), timer_type_(timer_type)
{
#if USE_CLOCK_GETTIME
	time_start_.tv_sec = time_start_.tv_nsec = 0;
#else
	timerclear(&time_start_);
#endif
	if (timer_type & AUTO_START_FLAG)
		start();
}


WallClockTimer::~WallClockTimer()
{
	// Ignore all potential error conditions if we have an uncaught exception:
	if (std::uncaught_exception())
		return;

	if ((timer_type_ & AUTO_STOP_FLAG) and is_running_) {
		stop();
		return;
	}

	if (unlikely(is_running_)) {
		if (not name_.empty())
			throw Exception("in WallClockTimer::~WallClockTimer: timer \"" + name_ + "\" is running!");
		else
			throw Exception("in WallClockTimer::~WallClockTimer: timer is running!");
	}
}


void WallClockTimer::start()
{
	if (unlikely(is_running_)) {
		if (not name_.empty())
			throw Exception("in WallClockTimer::start: \"" + name_ + "\" is running!");
		else
			throw Exception("in WallClockTimer::start: timer is running!");
	}

#if USE_CLOCK_GETTIME
#    ifdef __MACH__
	clock_serv_t cclock;
	mach_timespec_t mts;
	::host_get_clock_service(mach_host_self(), CALENDAR_CLOCK, &cclock);
	clock_get_time(cclock, &mts);
	::mach_port_deallocate(mach_task_self(), cclock);
	time_start_.tv_sec  = mts.tv_sec;
	time_start_.tv_nsec = mts.tv_nsec;
#    else
	if (unlikely(::clock_gettime(CLOCK_REALTIME, &time_start_) == -1))
		throw Exception("in WallClockTimer::start: clock_gettime(2) failed (" + MsgUtil::ErrnoToString() + ")!");
#    endif
#else
	struct timezone dummy;
	if (unlikely(::gettimeofday(&time_start_, &dummy) == -1))
		throw Exception("in WallClockTimer::start: gettimeofday(2) failed (" + MsgUtil::ErrnoToString() + ")!");
#endif

	is_running_ = true;
}


void WallClockTimer::stop()
{
	if (unlikely(not is_running_)) {
		if (not name_.empty())
			throw Exception("in WallClockTimer::stop: timer \"" + name_ + "\" is not running!");
		else
			throw Exception("in WallClockTimer::stop: timer is not running!");
	}

#if USE_CLOCK_GETTIME
	struct timespec time_end;
#    ifdef __MACH__
	clock_serv_t cclock;
	mach_timespec_t mts;
	::host_get_clock_service(mach_host_self(), CALENDAR_CLOCK, &cclock);
	clock_get_time(cclock, &mts);
	::mach_port_deallocate(mach_task_self(), cclock);
	time_end.tv_sec  = mts.tv_sec;
	time_end.tv_nsec = mts.tv_nsec;
#    else
	if (unlikely(::clock_gettime(CLOCK_REALTIME, &time_end) == -1))
		throw Exception("in WallClockTimer::stop: clock_gettime(2) failed (" + MsgUtil::ErrnoToString() + ")!");
#endif

	if (timer_type_ & CUMULATIVE_FLAG)
		time_ += TimespecToDouble(time_end) - TimespecToDouble(time_start_);
	else // Assume noncumulative timing.
		time_ = TimespecToDouble(time_end) - TimespecToDouble(time_start_);
#else
	struct timeval time_end;
	struct timezone dummy;
	if (unlikely(::gettimeofday(&time_end, &dummy) == -1))
		throw Exception("in WallClockTimer::stop: gettimeofday(2) failed (" + MsgUtil::ErrnoToString() + ")!");

	if (timer_type_ & CUMULATIVE_FLAG)
		time_ += TimevalToDouble(time_end) - TimevalToDouble(time_start_);
	else // Assume noncumulative timing.
		time_ = TimevalToDouble(time_end) - TimevalToDouble(time_start_);
#endif

	is_running_ = false;
}


double WallClockTimer::getTime() const
{
	if (unlikely(is_running_)) {
		if (not name_.empty())
			throw Exception("in WallClockTimer::getSystemTime: \"" + name_ + "\" is still running!");
		else
			throw Exception("in WallClockTimer::getSystemTime: timer is still running!");
	}

	return time_;
}
