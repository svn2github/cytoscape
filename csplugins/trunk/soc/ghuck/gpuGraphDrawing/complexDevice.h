/**************************************************************************************
Copyright (C) Apeksha Godiyal, 2008
Copyright (C) Gerardo Huck, 2009


This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

See licence.h for more information.
**************************************************************************************/
#include "license.h"



#ifndef COMPLEX_DEVICE_H
#define COMPLEX_DEVICE_H

#include <math.h>

typedef float2 complexDevice;

__device__ void Cadd(complexDevice a, complexDevice b, complexDevice * c)
{
	(*c).x=a.x+b.x;
	(*c).y=a.y+b.y;
}

__device__ void Csub(complexDevice a, complexDevice b, complexDevice * c)
{
	(*c).x=a.x-b.x;
	(*c).y=a.y-b.y;
}

__device__ void Cmul(complexDevice a, complexDevice b, complexDevice * c)
{
	(*c).x=a.x*b.x-a.y*b.y;
	(*c).y=a.y*b.x+a.x*b.y;
}

__device__ void Complex(float re, float im, complexDevice * c)
{
	(*c).x=re;
	(*c).y=im;
}


__device__ void Cdiv(complexDevice a, complexDevice b, complexDevice * c)
{
	float r,den;
	if (fabs(b.x) >= fabs(b.y)) {
		r=b.y/b.x;
		den=b.x+r*b.y;
		(*c).x=(a.x+r*a.y)/den;
		(*c).y=(a.y-r*a.x)/den;
	} else {
		r=b.x/b.y;
		den=b.y+r*b.x;
		(*c).x=(a.x*r+a.y)/den;
		(*c).y=(a.y*r-a.x)/den;
	}
}

#endif
