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




#include "kdNode.h"
#include "partition.cu"
#include "cudpp.h"
#include "complexDevice.h"
#include "common.h"


#include <stdio.h>


complexDevice * centerD;

void kdNodeInitD(kdNodeInt * n,kdNodeFloat * nf,int i,unsigned int a,float d,float u,float l,float r){
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
  (nf+i-1)->radius = sqrt(((nf+i-1)->left-(nf+i-1)->center.r)*((nf+i-1)->left-(nf+i-1)->center.r) + ((nf+i-1)->right-(nf+i-1)->center.i)*((nf+i-1)->right-(nf+i-1)->center.i));
	
  for(k = 0; k < 4; k++){
    (nf+i-1)->Outer[k]=Complex(0,0);
  }
}

void printD(kdNodeInt * n, kdNodeFloat * nf,int i){
  if(!n)
    return;
  if(!nf)
    return;
	
  if(((n+i-1)->child==0)){
    printf("%d, %f, %f, %f %f %f\n",(n+i-1)->numElems,(nf+i-1)->down,(nf+i-1)->up,(nf+i-1)->left,(nf+i-1)->right,(nf+i-1)->radius);
    printf("(");
    for(int j = 0; j < (n+i-1)->numElems; j++)
      printf("%d ",(n+i-1)->graphNodes[j]);
    printf(")\n");
    if((n+i-1)->next)
      printD(n,nf,(n+i-1)->next);
  }
  else{
    if((n+i-1)->child)
      printD(n,nf,(n+i-1)->child);
  }
}

void insertListD(kdNodeInt * n,int x,float * NodePos,float pos, int index,int limit){
  int i,j;
  
  for( i = 0; (i < (n+x-1)->numElems) && ((NodePos[2*(n+x-1)->graphNodes[i]+(n+x-1)->axis]) <= pos); i++)
    ;
  for(j = (n+x-1)->numElems; j > i; j--){
    (n+x-1)->graphNodes[j] = (n+x-1)->graphNodes[j-1];
  }
  (n+x-1)->graphNodes[i] = index; 
}

void updateElemsD(kdNodeInt * n, int i, int* start, int* end){
  int j;
  for(j=0;start <=end; start ++,j++){
    (n+i-1)->graphNodes[j] = *start;
  }
  (n+i-1)->numElems = j;
}


void InsertElemD(kdNodeInt * n, kdNodeFloat * nf,int i,float * NodePos,float x,float y, int index, int limit, int init){
  static int nIntAvail=2;
  if(init)
    nIntAvail=2;
  int k;
  float pos;
  
  fcomplex zi = Complex((x),(y));
  fcomplex z_v_minus_z_0_over_k = Csub(zi,((nf+i-1)->center));
  for(k = 0; k < 4; k++){
    fcomplex temp = Complex(-1/(float)(k+1),0);
    (nf+i-1)->Outer[k] = Cadd((nf+i-1)->Outer[k], Cmul(z_v_minus_z_0_over_k,temp));
    z_v_minus_z_0_over_k = Cmul(z_v_minus_z_0_over_k,Csub(zi,(nf+i-1)->center));
    //Outer[k]+=  ((float(-1))*z_v_minus_z_0_over_k)/float(k+1);
    //z_v_minus_z_0_over_k *= zi - (nf+i-1)->center;
  }
  
  if((n+i-1)->axis)
    pos = y;
  else
    pos = x;
  
  if((n+i-1)->child==0){
    insertListD((n),i,NodePos,pos,index,limit);
    if((n+i-1)->numElems >= limit){
      float u1,d1,l1,r1,u2,d2,l2,r2;
      int numElems = (n+i-1)->numElems+1;
      int splitting_node = (n+i-1)->graphNodes[((numElems)/2)-1];
      (nf+i-1)->split = NodePos[2*splitting_node+(n+i-1)->axis];
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
      (n+i-1)->child = nIntAvail; kdNodeInitD(n,nf,nIntAvail,1-(n+i-1)->axis,d1,u1,l1,r1);
      nIntAvail++;
      (n+nIntAvail-2)->next = nIntAvail; kdNodeInitD(n,nf,nIntAvail,1-(n+i-1)->axis,d2,u2,l2,r2);
      updateElemsD(n,nIntAvail-1,&((n+i-1)->graphNodes[0]),&((n+i-1)->graphNodes[(numElems/2)-1]));
      updateElemsD(n,nIntAvail,&((n+i-1)->graphNodes[(numElems/2)]),&((n+i-1)->graphNodes[limit]));
      (n+nIntAvail-1)->next = (n+i-1)->next;
      nIntAvail++;
    }
  }
  else {
    int c = (n+i-1)->child;
    if(pos <= (nf+i-1)->split)
      InsertElemD((n),nf,c,NodePos,x,y,index,limit,0);
    else
      InsertElemD((n),nf,(n+c-1)->next,NodePos,x,y,index,limit,0);
  }
  (n+i-1)->numElems++;
}

void swapD(float3 * greater,float3 * less){
  float3 temp = *greater;
  *greater = *less;
  *less    = temp;
}

float3 * partition(float3 * a, float3 * b, float pivot,int axis){
  
  int i = 0;
  int n = b-a+1;
  if(axis){
    while((i<n) && ((b-i)->y > pivot))
      i++;
    float3 * greater = b-i;
    i = 0;
    while((i<n) && ((a+i)->y <= pivot))
      i++;
    float3 * less = a+i; 
    for(; less < greater; ){
      swapD(greater,less);
      greater--;
      less++;
      while(less->y <= pivot)
	less++;
      while(greater->y > pivot)
	greater--;
    }
    return greater;
  }
  else{
    while((i<n) && ((b-i)->x > pivot))
      i++;
    float3 * greater = b-i;
    i = 0;
    while((i<n) && ((a+i)->x <= pivot))
      i++;
    float3 * less = a+i; 
    for(; less < greater; ){
      swapD(greater,less);
      greater--;
      less++;
      while(less->x <= pivot)
	less++;
      while(greater->x > pivot)
	greater--;
    }
    return greater;
  }
}

float3 * medianD(float3 * a, float3 * b, int axis,int k){
  
  if(!(a-b))
    return a;
  
  float pivot;
  if(axis){
    pivot = (a)->y;
  }
  else{
    pivot = (a)->x;
  }
  
  float3 * p = partition(a,b,pivot,axis);
  int i = int(p-a)+1;
  if(i==k)
    return p;
  else if(i < k)
    return medianD(p+1,b,axis,k-i);
  else
    return medianD(a,p-1,axis,k);
}

float3 * radixPartitionD(float3 * a, float3 * b, unsigned int bit, int axis){
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
      //less--;
    }
    for(; less < greater; ){
      swapD(greater,less);
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
    for( ; less < greater ; ){
      swapD(greater,less);
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

float3 * radixMedianD(float3* a, float3* b, int axis,int k, unsigned int bit,unsigned int* data_out, unsigned int* d_temp_addr_uint, 
		      float3* d_out,CUDPPHandle planHandle, unsigned int* nD)
{
  float3 * s;
  
  if(a==b)
    return a;
  else{
    int n = b - a + 1; 
    dim3  threads,blocks;
    unsigned m_chunks = n / maxThreadsThisBlock;
    unsigned m_leftovers = n % maxThreadsThisBlock;
    
    if ((m_chunks == 0) && (m_leftovers > 0)){
      // can't even fill a block
      blocks = dim3(1, 1, 1); 
      threads = dim3((m_leftovers), 1, 1);
    } 
    else {
      // normal case
      if (m_leftovers > 0){
	// not aligned, add an additional block for leftovers
	blocks = dim3(m_chunks + 1, 1, 1);
      }
      else{
	// aligned on block boundary
	blocks = dim3(m_chunks, 1, 1);
      }
      threads = dim3(maxThreadsThisBlock , 1, 1);
    }
    
    radixGlobalSetup_kernel<<<blocks, threads>>>(data_out, a, bit,axis);
    CUT_CHECK_ERROR("Kernel execution failed");

    // Call to CUDPP Scan function
    //cudppScan(d_temp_addr_uint, data_out, n, config);                                Deprecated
    CUDPPResult cudpp_result = cudppScan (planHandle, (void*)d_temp_addr_uint, (void*)data_out, n);

    CUT_CHECK_ERROR("Kernel execution failed");
    
    if (cudpp_result != CUDPP_SUCCESS)
      printf ("Error while performing CUDPP Scan\n");

    radixGlobalSplit_kernel<<<blocks, threads>>>(d_out, d_temp_addr_uint,a, bit,n- 1,axis,nD);
    
    int ns;
    cudaMemcpy(&ns,nD,sizeof(unsigned int),cudaMemcpyDeviceToHost);
    s = a + ns - 1 ;
    if(bit==0)
      return s;	
    if( ns > k )
      return radixMedianD(a,s,axis,k,bit>>1,data_out,d_temp_addr_uint,d_out,planHandle,nD);
    else
      return radixMedianD(s+1,b,axis,k-ns,bit>>1,data_out,d_temp_addr_uint,d_out,planHandle,nD);
  }
}

void constructD(float3 * NodePos1, float3 * NodePos2, kdNodeInt * n,kdNodeFloat * nf,
		int i,int a,float d,float u,float l,float r, int limit,
		unsigned int * data_out, unsigned int * d_temp_addr_uint, float3 * d_out,CUDPPHandle planHandle, unsigned int * nD,
		complexDevice * OuterD)
{
  static int avail = 2;
  if(i==1)
    avail = 2;
  (n+i-1)->numElems = int(NodePos2-NodePos1)+1;
  dim3 blocks, threads;
  
  unsigned m_chunkSize = maxThreadsThisBlock;
  unsigned m_chunks = (n+i-1)->numElems / m_chunkSize;
  unsigned m_leftovers = (n+i-1)->numElems % m_chunkSize;
  
  if ((m_chunks == 0) && (m_leftovers > 0)){
    // can't even fill a block
    blocks = dim3(1, 1, 1); 
    threads = dim3((m_chunkSize), 1, 1);
  } 
  else{
    // normal case
    if (m_leftovers > 0){
      // not aligned, add an additional block for leftovers
      blocks = dim3(m_chunks + 1, 1, 1);
    }
    else{
      // aligned on block boundary
      blocks = dim3(m_chunks, 1, 1);
    }
    threads = dim3(m_chunkSize, 1, 1);
  }
  
  fcomplex * Outer = (fcomplex *) malloc( 4*(m_chunks+1)*sizeof(fcomplex));
  
  // check if kernel execution generated and error
  CUT_CHECK_ERROR("Kernel execution failed");
  
  complexDevice center; center.x = ((nf+i-1)->center).r; center.y = ((nf+i-1)->center).i;
  cudaMalloc((void**)&OuterD, 4*(m_chunks+1)*sizeof(fcomplex));
  calcOuter<<<blocks, threads,4*sizeof(fcomplex)*threads.x>>>(NodePos1,OuterD,(n+i-1)->numElems,center);
  CUT_CHECK_ERROR("Kernel execution failed");
	
  cudaMemcpy(Outer, OuterD, 4*(m_chunks+1)*sizeof(fcomplex), cudaMemcpyDeviceToHost);
  for(int k = 0; k < m_chunks+1; k+=4)
    for(int j = 0; j < 4; j++)
      (nf+i-1)->Outer[j]= Cadd(Outer[k+j],(nf+i-1)->Outer[j]);
  
  cudaFree(OuterD);
  free(Outer);
  
  // check if kernel execution generated and error
  CUT_CHECK_ERROR("Kernel execution failed");
	
  if((n+i-1)->numElems <= limit){
    float3 *start = (float3*)malloc(((n+i-1)->numElems)*sizeof(float3));
    cudaMemcpy(start, NodePos1, ((n+i-1)->numElems)*sizeof(float3), cudaMemcpyDeviceToHost);
    for(int j = 0; j < (n+i-1)->numElems; j++,start++){
      (n+i-1)->graphNodes[j]= (int)start->z;
    }	
    return;
  }
	
  //CUT_CHECK_ERROR("Kernel execution failed");
  float l1,l2,r1,r2,u1,u2,d1,d2;
        	
  unsigned int bit = 1<<(sizeof(float)*8-1);

  float3 * mid = radixMedianD(NodePos1,NodePos2,a,(int)((NodePos2-NodePos1)/2)+1,bit,data_out,d_temp_addr_uint,d_out,planHandle,nD);
  
  float3 midElem;
  cudaError_t err = cudaMemcpy(&(midElem), mid, sizeof(float3),cudaMemcpyDeviceToHost);	
  CUT_CHECK_ERROR("Kernel execution failed");	
  if(a){
    (nf+i-1)->split = midElem.y;
    l1 =(nf+i-1)->left;  l2=(nf+i-1)->left;
    r1 =(nf+i-1)->right; r2 = (nf+i-1)->right;
    u1 =(nf+i-1)->split; u2 = (nf+i-1)->up;
    d1 =(nf+i-1)->down; d2 = (nf+i-1)->split;
  }
  else{
    (nf+i-1)->split = midElem.x;
    l1 =(nf+i-1)->left; l2=(nf+i-1)->split;
    r1 =(nf+i-1)->split; r2 = (nf+i-1)->right;
    u1 = (nf+i-1)->up; u2 = (nf+i-1)->up;
    d1 = (nf+i-1)->down; d2 = (nf+i-1)->down;
  }
  
  int c = avail++; 
  int c2 = avail++;
  
  kdNodeInitD(n,nf,c,1-a,d1,u1,l1,r1);
  kdNodeInitD(n,nf,c2,1-a,d2,u2,l2,r2);
  
  (n+i-1)->child = c;
  (n+c-1)->next = c2;
  (n+c2-1)->next = (n+i-1)->next;
  
  constructD(NodePos1,mid-1,n,nf,c,1-a,d1,u1,l1,r1,limit,data_out, d_temp_addr_uint, d_out,planHandle,nD,OuterD);
  constructD(mid,NodePos2 ,n,nf,c2,1-a,d2,u2,l2,r2,limit,data_out, d_temp_addr_uint, d_out,planHandle,nD,OuterD);	 
}
