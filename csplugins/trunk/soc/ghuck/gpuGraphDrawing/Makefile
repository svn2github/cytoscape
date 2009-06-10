gpuGraphDrawing : simpleCUDPP.o
	g++ -o gpuGraphDrawing $(INC) $(LIB) simpleCUDPP.o -lglut -lcudart -lcudpp64

simpleCUDPP.o : simpleCUDPP.cu
	nvcc -c $(INC) -o simpleCUDPP.o simpleCUDPP.cu

clean :
	rm gpuGraphDrawing simpleCUDPP.o

CUDA := /usr/local/cuda

CUDPP := /home/gerardo/cudpp_1.0a

INC := 	-I$(CUDPP)/common/inc \
        -I$(CUDPP)/cudpp/include

LIB := -L$(CUDA)/lib \
       -L$(CUDPP)/lib



