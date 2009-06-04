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
