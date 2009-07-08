gpuGraphDrawing : gpuGraphDrawing.o
	g++ -o gpuGraphDrawing $(INC) $(LIB) gpuGraphDrawing.o -lglut -lcudart -lcudpp64

gpuGraphDrawing.o : gpuGraphDrawing.cu
	nvcc -c $(INC) -o gpuGraphDrawing.o gpuGraphDrawing.cu

clean :
	rm gpuGraphDrawing gpuGraphDrawing.o

CUDA := /usr/local/cuda

CUDPP := /home/gerardo/cudpp_1.0a

INC := 	-I$(CUDPP)/common/inc \
        -I$(CUDPP)/cudpp/include

LIB := -L$(CUDA)/lib \
       -L$(CUDPP)/lib



