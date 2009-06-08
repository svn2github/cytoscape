

gpuGraphDrawing : simpleCUDPP.cu
	nvcc -o gpuGraphDrawing $(INC) $(LIB) simpleCUDPP.cu  

clean :
	rm simpleGraphDrawing simpleGraphDrawing.o

CUDA_SDK := /home/gerardo/NVIDIA_CUDA_SDK

INC := 	-I$(CUDA_SDK)/common/inc \
        -I$(CUDA_SDK)/common/inc/cudpp 

LIB := -L$(CUDA_SDK)/lib 








#nvcc -I/home/gerardo/NVIDIA_CUDA_SDK/common/inc/ -I/home/gerardo/NVIDIA_CUDA_SDK/common/inc/cudpp/ -L/home/gerardo/NVIDIA_CUDA_SDK/common/lib/  simpleCUDPP.cu




#TARGET := gpuGraphDrawing 

