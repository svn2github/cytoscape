/**************************************************************************************
This code is released under GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007. 
Copyright (C) Apeksha Godiyal, 2008
Copyright (C) Gerardo Huck, 2009

See licence.h and copyright.h for more information.
**************************************************************************************/

#include "licence.h"
#include "copyright.h"




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
