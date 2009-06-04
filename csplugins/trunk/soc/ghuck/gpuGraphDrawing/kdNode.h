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
