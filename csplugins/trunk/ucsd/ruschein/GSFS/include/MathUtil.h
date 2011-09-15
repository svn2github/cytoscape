/** \file   MathUtil.h
 *  \brief  Declarations for functions used in our classifiers.
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

#ifndef MATH_UTIL_H
#define MATH_UTIL_H


#include <cfloat>
#include <cmath>
#include <inttypes.h>


#ifdef Abs
#      undef Abs
#endif


namespace MathUtil {


/** \brief  Splits a single-precision floating-point number into an exponent and a mantissa.
 *  \param  f         The number to split.
 *  \param  exponent  The exponent of the split number.
 *  \param  mantissa  The biased mantissa of the number.  (The bias is FLT_RADIX ^ FLT_MANT_DIG).
 */
void ExtractExponentAndMantissa(const float f, int * const exponent, int32_t * const mantissa);


/** \brief  Splits a double-precision floating-point number into an exponent and a mantissa.
 *  \param  d         The number to split.
 *  \param  exponent  The exponent of the split number.
 *  \param  mantissa  The biased mantissa of the number.  (The bias is DBL_RADIX ^ DBL_MANT_DIG).
 */
void ExtractExponentAndMantissa(const double d, int * const exponent, int64_t * const mantissa);


/** Reconstructs a single-precision floating-point number from the exponent and "mantissa" as returned by
    ExtractExponentAndMantissa(). */
void ExponentAndMantissaToFloat(const int exponent, const int32_t mantissa, float * const f);


/** Reconstructs a double-precision floating-point number from the exponent and "mantissa" as returned by
    ExtractExponentAndMantissa(). */
void ExponentAndMantissaToDouble(const int exponent, const int64_t mantissa, double * const d);


inline float Abs(const float x) { return ::fabsf(x); }
inline double Abs(const double x) { return std::fabs(x); }
inline long double Abs(const long double x) { return ::fabsl(x); }


inline float Sqrt(const float x) { return ::sqrtf(x); }
inline double Sqrt(const double x) { return std::sqrt(x); }
inline long double Sqrt(const long double x) { return ::sqrtl(x); }


inline float Exp(const float x) { return ::expf(x); }
inline double Exp(const double x) { return std::exp(x); }
inline long double Exp(const long double x) { return ::expl(x); }


template <typename Real> inline bool ApproximatelyEqual(const Real &x1, const Real &x2, const Real &epsilon = 1e-3)
{
	if (x1 != 0.0 and x2 != 0.0)
		return Abs(1.0 - Abs(x1 / x2)) < epsilon;
	else
		return Abs(x1 - x2) < epsilon;
}


template <typename NumericType> inline int Sign(const NumericType &x)
{
	if (x == 0.0)
		return 0;
	return (x > 0.0) ? 1 : -1;
}


} // namespace MathUtil


#endif // ifndef MATH_UTIL_H
