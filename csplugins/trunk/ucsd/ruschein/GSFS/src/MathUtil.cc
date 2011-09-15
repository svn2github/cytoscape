/** \file   MathUtil.cc
 *  \brief  Implementation for functions used in our classifiers.
 *  \author Wagner Truppel
 *  \author Dr. Johannes Ruscheinski
 *  \author Jason Scheirer
 */

/*
 *  Copyright 2004-2008 Project iVia.
 *  Copyright 2004-2008 The Regents of The University of California.
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

#include <MathUtil.h>
#include <Compiler.h>


#ifndef DIM
#       define DIM(array)  (sizeof(array)/sizeof(array[0]))
#endif


namespace MathUtil {


void ExtractExponentAndMantissa(const float f, int * const exponent, int32_t * const mantissa)
{
	float f1 = ::frexpf(f, exponent);
	*mantissa = static_cast<int32_t>(::scalbnf(f1, FLT_MANT_DIG));
}


void ExtractExponentAndMantissa(const double d, int * const exponent, int64_t * const mantissa)
{
	double d1 = ::frexp(d, exponent);
	*mantissa = static_cast<int64_t>(::scalbn(d1, DBL_MANT_DIG));
}


void ExponentAndMantissaToFloat(const int exponent, const int32_t mantissa, float * const f)
{
	float f1 = ::scalbnf(mantissa, -FLT_MANT_DIG);
	*f = ::ldexpf(f1, exponent);
}


void ExponentAndMantissaToDouble(const int exponent, const int64_t mantissa, double * const d)
{
	double d1 = ::scalbn(mantissa, -DBL_MANT_DIG);
	*d = ::ldexp(d1, exponent);
}


} // namespace MathUtil
