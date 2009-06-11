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
#include "licence.h"




#ifndef __KD_NODE_H__
#define __KD_NODE_H__

#include "complex.h"

typedef struct {
	int axis;
	int child;
	int next;
	int numElems;
	int graphNodes[5];
	int i;
} kdNodeInt;

typedef struct {
	float radius;
	fcomplex center;
	float split;
	float up,down,left,right;
	fcomplex Outer[4];
} kdNodeFloat;

#endif
