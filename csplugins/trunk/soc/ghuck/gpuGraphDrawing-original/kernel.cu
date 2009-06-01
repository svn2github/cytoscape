#ifndef _HELLOWORLD_H_
#define _HELLOWORLD_H_

#include <stdio.h>
#include "complexDevice.h"
#include "graph.h"
#include "common.h"

enum intMembers{axis = 0, child=1, next=2, numElems=3, graphNodes,i=9};
enum floatMembers{radius=0,center=1,split=3,up=4,down=5,left=6,right=7,Outer=8};

texture<int> texInt;
texture<int> texAdjMatD;
texture<float> texFloat;
texture<int> texAdjMatValsD;
texture<int> texEdgeLenD;
texture<float2> texNodePosD;

__constant__ graph gd[1];
int nIntAvail=2;

__device__ float f_rep_scalar (float d)
	{
		if (d > 0)
		{
			return 1/d;
		}
		else
		{
			//printf("Error FruchtermanReingold:: f_rep_scalar nodes at same position\n");
			return 0;
		}
	}

__device__ void calcExactRepulsion(float3 NodePosS, float2* DispS, int j){
 int i;
 float scalar;          
 for(i = 0; i<tex1Dfetch(texInt,__umul24(j,10)+numElems); i++)
 {
	 if(NodePosS.z != tex1Dfetch(texInt,__umul24(j,10)+graphNodes+i)){
		int k = (tex1Dfetch(texInt,__umul24(j,10)+graphNodes+i));
		float2 NodePos = tex1Dfetch(texNodePosD,k);
		float xDelta = NodePosS.x- NodePos.x;
		float yDelta = NodePosS.y- NodePos.y;
 		float deltaLength = max(gd[0].EPSILON, sqrtf((xDelta * xDelta) + (yDelta * yDelta)));
		scalar = f_rep_scalar(deltaLength)/deltaLength;
		float f_rep_x = scalar * xDelta;
		float f_rep_y = scalar * yDelta;
		
		DispS->x+= f_rep_x;
		DispS->y+= f_rep_y;
	 }
 }
}

__device__ void calcFMMRepulsion(complexDevice z, float dist, float2* Disp, int j){
	complexDevice one; Complex(1,0,&one);
	complexDevice numElements; Complex((float)(tex1Dfetch(texInt,__umul24(j,10)+numElems)),0,&numElements);
	complexDevice z_v_minus_z_0_over_minus_k_minus_1; Cdiv(one,(z),&z_v_minus_z_0_over_minus_k_minus_1);
    complexDevice sum; Cmul(numElements,z_v_minus_z_0_over_minus_k_minus_1,&sum);
          
	  for(int k=0;k<4;k++)
	    {
              Cdiv(z_v_minus_z_0_over_minus_k_minus_1,z,&z_v_minus_z_0_over_minus_k_minus_1);
              complexDevice O; Complex(tex1Dfetch(texFloat,__umul24(j,16)+Outer+2*k),tex1Dfetch(texFloat,__umul24(j,16)+Outer+2*k+1),&O);
			  complexDevice temp1; Complex((float)k+1,0,&temp1);
			  complexDevice temp2; Cmul(O,z_v_minus_z_0_over_minus_k_minus_1,&temp2);
			  complexDevice Outerk;Cmul((temp1),(temp2),&Outerk);
              Csub(sum,Outerk,&sum);
	    }
		
	  Disp->x+=sum.x;
	  Disp->y+=-1*sum.y;
}

__device__ void calcRepulsion(float3 NodePos, float2* Disp){
	int j = 1;
	complexDevice z; Complex(NodePos.x,NodePos.y,&z);
	while(j){
		j--;
		complexDevice Center; Complex(tex1Dfetch(texFloat,__umul24(j,16)+center),tex1Dfetch(texFloat,__umul24(j,16)+center+1),&Center);
		complexDevice z_z0; Csub(z,Center,&z_z0);
		float dist = sqrt(z_z0.x*z_z0.x+z_z0.y*z_z0.y);
		float rad = tex1Dfetch(texFloat,__umul24(j,16)+radius); 
		if( dist > rad){
			calcFMMRepulsion(z_z0,dist,Disp,j);
			j = tex1Dfetch(texInt,__umul24(j,10)+next);
	}
	else{
		if(tex1Dfetch(texInt,__umul24(j,10)+child)==0){
			(calcExactRepulsion(NodePos,Disp,j));
			j = tex1Dfetch(texInt,__umul24(j,10)+next);
		}
		else{
			if(tex1Dfetch(texInt,__umul24(j,10)+child))
				j = tex1Dfetch(texInt,__umul24(j,10)+child);
			else
				j = tex1Dfetch(texInt,__umul24(j,10)+next);
			}
		}
	}
}

__device__ float f_attr_scalar (float d,float ind_ideal_edge_length)
{
  float s;
  float c =  log(d/ind_ideal_edge_length)/log(2.0);
      if (d > 0) 
	s =  c * d * d /
   	    (ind_ideal_edge_length * ind_ideal_edge_length * ind_ideal_edge_length); 
      else 
	s = -1e10;   
 return s;
} 

__device__ void calcAttraction(int j, float2* DispS, float3 NodePosS, float edgeLength) {
	float scalar;
	float xDelta, yDelta;
	float2 NodePos = tex1Dfetch(texNodePosD,j);
    xDelta = NodePosS.x - NodePos.x;
    yDelta = NodePosS.y - NodePos.y;
 
    float deltaLength = max(gd[0].EPSILON, sqrtf((xDelta * xDelta)+ (yDelta * yDelta)));
    
    scalar = f_attr_scalar(deltaLength,edgeLength)/deltaLength;
	float dx = scalar * xDelta;
	float dy = scalar * yDelta; 
        
    DispS->x-=2*dx;
	DispS->y-=2*dy;
}

__global__ void calculateForces(int numNodes, float2 * DispD, int * AdjMatIndexD)
{       
		const int globalId =  __umul24(blockIdx.x,blockDim.x) + threadIdx.x;
        
        if(globalId < numNodes){
			float2 NodePosTemp; 
			float2 Disp= make_float2(0,0); 
			float3 NodePos;
			
			NodePosTemp = tex1Dfetch(texNodePosD,globalId);
			NodePos.x = NodePosTemp.x;
			NodePos.y = NodePosTemp.y;
			NodePos.z = globalId;
			calcRepulsion(NodePos,&Disp);
			int end = AdjMatIndexD[globalId+1]; 
			for(int i = AdjMatIndexD[globalId]; i< end; i++)
					calcAttraction(tex1Dfetch(texAdjMatValsD,i),&Disp,NodePos,tex1Dfetch(texEdgeLenD,i));
			DispD[globalId] = Disp;
		}	
}

__global__ void 
calcOuter(float3 * NodePos, complexDevice * OuterD, int numNodes,complexDevice c){
	extern __shared__ complexDevice OuterS[];
	float3 * idata = NodePos + blockIdx.x * blockDim.x;
	unsigned int thid  = threadIdx.x;
	unsigned int gid   = blockIdx.x * blockDim.x + thid;
	
	complexDevice zi;  
	if(gid < numNodes)
		Complex(idata[thid].x,idata[thid].y,&zi);
	else
		Complex(0,0,&zi);
		
	complexDevice z_v_minus_z_0_over_k; Csub(zi,c,&z_v_minus_z_0_over_k);
	complexDevice temp = z_v_minus_z_0_over_k;
	for(int k = 0; k < 4; k++){
		if(gid < numNodes){
			complexDevice temp2; Complex(-1/(float)(k+1),0,&temp2);
			Cmul(z_v_minus_z_0_over_k,temp2,&OuterS[thid+blockDim.x*k] );
		}
		else
			Complex(0,0,&OuterS[thid+blockDim.x*k]);
		Cmul(z_v_minus_z_0_over_k,temp,&z_v_minus_z_0_over_k);
	}
	
	int offset = 1;
	for (int d = blockDim.x>>1; d > 0; d >>= 1) // build sum in place up the tree
	{
		__syncthreads();
		if (thid < d)
		{
			int ai = offset*(2*thid+1)-1;
			int bi = offset*(2*thid+2)-1;
			Cadd(OuterS[bi],OuterS[ai],&OuterS[bi]);
			ai = ai+blockDim.x;
			bi = bi+blockDim.x;
			Cadd(OuterS[bi],OuterS[ai],&OuterS[bi]);
			ai = ai+blockDim.x;
			bi = bi+blockDim.x;
			Cadd(OuterS[bi],OuterS[ai],&OuterS[bi]);
			ai = ai+blockDim.x;
			bi = bi+blockDim.x;
			Cadd(OuterS[bi],OuterS[ai],&OuterS[bi]);
		}
			offset *= 2;
	}
	if(thid<4){
		OuterD[blockIdx.x*4+thid]=OuterS[(blockDim.x-1)+thid*blockDim.x];
	}
}

#endif
