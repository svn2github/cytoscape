/**************************************************************************************
This code is released under GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007. 
Copyright (C) Apeksha Godiyal, 2008
Copyright (C) Gerardo Huck, 2009

See licence.h and copyright.h for more information.
**************************************************************************************/

#include "licence.h"
#include "copyright.h"




#include "kdNode.h"
#include "cudpp.h"
#include "complexDevice.h"

void kdNodeInit(kdNodeInt * n,kdNodeFloat * nf,int i,unsigned int a,float d,float u,float l,float r){
	int k;
	(n+i-1)->i=i;
	(n+i-1)->numElems=0;
	(n+i-1)->axis=(a);
	(nf+i-1)->up=(u);
	(nf+i-1)->down=(d);
	(nf+i-1)->left=(l);
	(nf+i-1)->right=(r);
	(nf+i-1)->center = Complex((l+r)/2,(u+d)/2); 
	(n+i-1)->child=0;
	(n+i-1)->next=0;
	(nf+i-1)->radius = sqrt(((nf+i-1)->left-(nf+i-1)->center.r)*((nf+i-1)->left-(nf+i-1)->center.r) + ((nf+i-1)->up-(nf+i-1)->center.i)*((nf+i-1)->up-(nf+i-1)->center.i));
	
	for(k = 0; k < 4; k++){
		(nf+i-1)->Outer[k]=Complex(0,0);
	}
}

void print(kdNodeInt * n, kdNodeFloat * nf,int i){
	if(!n)
		return;
	if(!nf)
		return;
	
	if(((n+i-1)->child==0)){
		printf("%d, %f, %f, %f %f %f\n",(n+i-1)->numElems,(nf+i-1)->down,(nf+i-1)->up,(nf+i-1)->left,(nf+i-1)->right,(nf+i-1)->radius);
		if((n+i-1)->next)
			print(n,nf,(n+i-1)->next);
	}
	else{
		if((n+i-1)->child)
			print(n,nf,(n+i-1)->child);
	}
}

void swap(float3 * greater,float3 * less){
	float3 temp = *greater;
	*greater = *less;
	*less    = temp;
}

float3 * radixPartition(float3 * a, float3 * b, unsigned int bit, int axis){
	// y axis
	unsigned int o = 1;
	o = o<<(bit);
	int n = (int)(b-a)+1;
	if(axis){
		int i=0;
		
		while((i<n) && (((unsigned int)(b-i)->y) & o) )
			i++;
		float3 * greater = b-i;
		if(i==n){
			return greater;
		}
		i = 0;
		while((i<n) && !(((unsigned int)(a+i)->y)&o))
			i++;
		float3 * less = a+i; 
		if(i==n){
			return b;
		}
		for(; less < greater; ){
				swap(greater,less);
				greater--;
				less++;
				while(!((unsigned int)(less->y)&o))
					less++;
				while(((unsigned int)(greater->y)&o))
					greater--;
		}
		return greater;
	}
	// x axis
	else{
		int i=0;
		while((i<n) && (((unsigned int)(b-i)->x)&o) )
			i++;
		float3 * greater = b-i;
		if(i==n)
			return greater;
		i = 0;
		while((i<n) && !(((unsigned int)(a+i)->x)&o))
			i++;
		float3 * less = a+i;
		if(i==n)
			return b;
		for(; less < greater; ){
				swap(greater,less);
				greater--;
				less++;
				while(!((unsigned int)(less->x)&o))
					less++;
				while(((unsigned int)(greater->x)&o))
					greater--;
		}
		return greater;
	}
}

float3 * radixMedian(float3 * a, float3 * b, int axis,int k, unsigned int bit){

	float3 * s;
	if((int)bit < 0)
		return (a+(int)((b-a)/2));
	
	if(a==b)
		return a;
	else{
		s = radixPartition(a,b,bit,axis);
		int ns = (int)(s - a) + 1;
		if( ns > k )
			return radixMedian(a,s,axis,k,bit-1);
		else
			return radixMedian(s+1,b,axis,k-ns,bit-1);
	}
}
void construct(float3 * NodePos1, float3 * NodePos2, kdNodeInt * n,kdNodeFloat * nf,
			   int i,int a,float d,float u,float l,float r, int limit){
	
	static int avail = 2;
	if(i==1)
		avail = 2;
	(n+i-1)->numElems = int(NodePos2-NodePos1)+1;
	for(float3 * start = NodePos1; start <= NodePos2; start++){
		fcomplex zi = Complex(start->x,start->y);
		fcomplex z_v_minus_z_0_over_k = Csub(zi,((nf+i-1)->center));
		for(int k = 0; k < 4; k++){
			fcomplex temp = Complex(-1/(float)(k+1),0);
			(nf+i-1)->Outer[k] = Cadd((nf+i-1)->Outer[k], Cmul(z_v_minus_z_0_over_k,temp));
			z_v_minus_z_0_over_k = Cmul(z_v_minus_z_0_over_k,Csub(zi,(nf+i-1)->center));
		}
	}

	if((n+i-1)->numElems <= limit){
		float3 * start = NodePos1;
		for(int j = 0; j < (n+i-1)->numElems; j++,start++)
			(n+i-1)->graphNodes[j]= start->z;
		return;
	}
	
	float l1,l2,r1,r2,u1,u2,d1,d2;
	float3 * mid = radixMedian(NodePos1,NodePos2,a,(int)((NodePos2-NodePos1)/2)+1, sizeof(float)*8-1);
	
	if(a){
		(nf+i-1)->split = mid->y;
		l1 =(nf+i-1)->left;  l2=(nf+i-1)->left;
		r1 =(nf+i-1)->right; r2 = (nf+i-1)->right;
		u1 =(nf+i-1)->split; u2 = (nf+i-1)->up;
		d1 =(nf+i-1)->down; d2 = (nf+i-1)->split;
	}
	else{
		(nf+i-1)->split = mid->x;
		l1 =(nf+i-1)->left; l2=(nf+i-1)->split;
		r1 =(nf+i-1)->split; r2 = (nf+i-1)->right;
		u1 = (nf+i-1)->up; u2 = (nf+i-1)->up;
		d1 = (nf+i-1)->down; d2 = (nf+i-1)->down;
	}
	
	int c = avail++; 
	int c2 = avail++;

	kdNodeInit(n,nf,c,1-a,d1,u1,l1,r1);
	kdNodeInit(n,nf,c2,1-a,d2,u2,l2,r2);

	(n+i-1)->child = c;
	(n+c-1)->next = c2;
	(n+c2-1)->next = (n+i-1)->next;
	
	construct(NodePos1,mid-1,n,nf,c,1-a,d1,u1,l1,r1,limit);
	construct(mid,NodePos2 ,n,nf,c2,1-a,d2,u2,l2,r2,limit);
	 
}
void insertList(kdNodeInt * n,int x,float3 * NodePos,float pos, int index,int limit){
	int i,j;

	if((n+x-1)->axis){
		for( i = 0; (i < (n+x-1)->numElems) && ((NodePos[(n+x-1)->graphNodes[i]].y) <= pos); i++);
		for(j = (n+x-1)->numElems; j > i; j--){
			(n+x-1)->graphNodes[j] = (n+x-1)->graphNodes[j-1];
		}
		(n+x-1)->graphNodes[i] = index;
	}
	else{
		for( i = 0; (i < (n+x-1)->numElems) && ((NodePos[(n+x-1)->graphNodes[i]].x) <= pos); i++);
		for(j = (n+x-1)->numElems; j > i; j--){
			(n+x-1)->graphNodes[j] = (n+x-1)->graphNodes[j-1];
		}
		(n+x-1)->graphNodes[i] = index;
	} 
}

void updateElems(kdNodeInt * n, int i, int* start, int* end){
	int j;
	for(j=0;start <=end; start ++,j++){
		(n+i-1)->graphNodes[j] = *start;
	}
	(n+i-1)->numElems = j;
}

void InsertElem(kdNodeInt * n, kdNodeFloat * nf,int i,float3 * NodePos,float x,float y, int index, int limit){
	static int nIntAvail=2;
	if((i==1)&& (index==0))
		nIntAvail=2;
	int k;
	float pos;

 	fcomplex zi = Complex((x),(y));
	fcomplex z_v_minus_z_0_over_k = Csub(zi,((nf+i-1)->center));
	for(k = 0; k < 4; k++){
		fcomplex temp = Complex(-1/(float)(k+1),0);
		(nf+i-1)->Outer[k] = Cadd((nf+i-1)->Outer[k], Cmul(z_v_minus_z_0_over_k,temp));
		z_v_minus_z_0_over_k = Cmul(z_v_minus_z_0_over_k,Csub(zi,(nf+i-1)->center));
	}

	if((n+i-1)->axis)
		pos = y;
	else
		pos = x;

	if((n+i-1)->child==0){
		insertList((n),i,NodePos,pos,index,limit);
		if((n+i-1)->numElems >= limit){
			float u1,d1,l1,r1,u2,d2,l2,r2;
			int numElems = (n+i-1)->numElems+1;
			int splitting_node = (n+i-1)->graphNodes[((numElems)/2)-1];
			(nf+i-1)->split = ((n+i-1)->axis)? NodePos[splitting_node].y:NodePos[splitting_node].x;
			if((n+i-1)->axis){
				l1 =(nf+i-1)->left;  l2=(nf+i-1)->left;
				r1 =(nf+i-1)->right; r2 = (nf+i-1)->right;
				u1 =(nf+i-1)->split; u2 = (nf+i-1)->up;
				d1 =(nf+i-1)->down; d2 = (nf+i-1)->split;
			}
			else{
				l1 =(nf+i-1)->left; l2=(nf+i-1)->split;
				r1 =(nf+i-1)->split; r2 = (nf+i-1)->right;
				u1 = (nf+i-1)->up; u2 = (nf+i-1)->up;
				d1 = (nf+i-1)->down; d2 = (nf+i-1)->down;
			}
			(n+i-1)->child = nIntAvail; kdNodeInit(n,nf,nIntAvail,1-(n+i-1)->axis,d1,u1,l1,r1);
			nIntAvail++;
			(n+nIntAvail-2)->next = nIntAvail; kdNodeInit(n,nf,nIntAvail,1-(n+i-1)->axis,d2,u2,l2,r2);
			updateElems(n,nIntAvail-1,&((n+i-1)->graphNodes[0]),&((n+i-1)->graphNodes[(numElems/2)-1]));
			updateElems(n,nIntAvail,&((n+i-1)->graphNodes[(numElems/2)]),&((n+i-1)->graphNodes[limit]));
			(n+nIntAvail-1)->next = (n+i-1)->next;
			nIntAvail++;
		}
	}
	else {
		int c = (n+i-1)->child;
		if(pos <= (nf+i-1)->split)
			InsertElem((n),nf,c,NodePos,x,y,index,limit);
		else
			InsertElem((n),nf,(n+c-1)->next,NodePos,x,y,index,limit);
	}
	(n+i-1)->numElems++;
}

