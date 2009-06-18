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

See license.h for more information.
**************************************************************************************/
#include "license.h"




__device__ void setFlagBit(unsigned int * cmpflags, const float3 * idata, 
                           unsigned int bit,int axis)
{
    const int addr0 =  threadIdx.x;
    cmpflags[addr0] = axis?((((unsigned int)(idata[addr0].y) & bit) == 0) ? 1 : 0):((((unsigned int)(idata[addr0].x) & bit) == 0) ? 1 : 0);
}

__global__ void radixGlobalSetup_kernel(unsigned int * g_odata, 
                                        const float3 * g_idata, 
                                        unsigned int bit,
                                        int axis)
{
    const float3 * idata = g_idata + blockIdx.x * blockDim.x;
    unsigned int * odata = g_odata + blockIdx.x * blockDim.x;
    // deposit the correct bit into d_out
    setFlagBit(odata,idata, bit,axis);
}



__global__ void radixGlobalSplit_kernel(float3 * g_data_out, 
                                        unsigned int * g_addr, 
                                        float3 * g_data_in, 
                                        unsigned int bit, unsigned int last, int axis,unsigned int * nD)
{
#ifdef __DEVICE_EMULATION__
    bool debug = false;
#endif
    const int n = blockDim.x;
    const int bid = blockIdx.x;
    const int offset = __mul24(bid, n);
    const unsigned int * addr = g_addr + offset;
    const float3       * data_in = g_data_in + offset;
    const int thid = threadIdx.x;
    const int myid0 = offset + thid;
    
    __shared__ unsigned int totalFalses;
    // g_addr contains output of enumerate
    // g_temp contains input to enumerate (0/1 per element)

#ifdef __DEVICE_EMULATION__
    if (debug)
    {
        __syncthreads();
        if (thid == 0) { printf("last: %d, blockid: %d, myid0: %d, myid1: %d\n", last, bid, myid0, myid1); fflush(stdout); }
        __syncthreads();
    }
#endif

    const int addr0 = (myid0 > last) ? last : thid;
    
    const int g_addr0 = addr0 + offset;
    
    // flag is 0 if false, 1 if true
    unsigned int flag0;
    flag0 = axis? ((((unsigned int)data_in[addr0].y & bit) == 0) ? 1 : 0): ((((unsigned int)data_in[addr0].x & bit) == 0) ? 1 : 0); // store in reg
    if (thid == 0)
    {
        totalFalses = 
             axis? (((((unsigned int)g_data_in[last].y & bit) == 0) ? 1 : 0) + g_addr[last]):(((((unsigned int)g_data_in[last].x & bit) == 0) ? 1 : 0) + g_addr[last]);
		if(bid==0)
			*nD = totalFalses;
    }
    __syncthreads();
        
    // read in addr into laddr (local addr)
    unsigned int laddr0;
    laddr0 = addr[addr0];
        
    // addr is correct for falses (flag == 0), not for trues (flag == 1)
        
       
    if (!flag0)
    {
        laddr0 = g_addr0 - laddr0 + totalFalses;
    }
        
       
        
    // scatter those bad boys into output data array g_data
    // possible problem - running many blocks might mean we have to
    // have different input/output buffers here
    float3 data0;
    data0 = data_in[addr0];
   
    if (myid0 <= last)
    {
        g_data_out[laddr0] = data0;
    }
}

