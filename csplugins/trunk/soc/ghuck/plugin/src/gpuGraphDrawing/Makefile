GpuLayout : GpuLayout.cu 	
	cp -r ../../build/GpuLayout/ ./
	javah -d ../ GpuLayout.ForceDirected
	rm -r ./GpuLayout/
	nvcc --shared --compiler-options '-fPIC' -o libGpuLayout.so $(INC) $(LIB) GpuLayout.cu -lglut -lcudart -lcudpp64

	#nvcc --compiler-options '-fPIC' -c -o GpuLayout.o $(INC) $(LIB) GpuLayout.cu -lglut -lcudart -lcudpp64

	cp libGpuLayout.so ../../

clean :
	rm libGpuLayout.so 
	rm *.linkinfo
	rm ../*.h
	rm ../../libGpuLayout.so

CUDA := /usr/local/cuda

CUDPP := /home/gerardo/cudpp_1.0a

INC := 	-I$(CUDPP)/common/inc \
        -I$(CUDPP)/cudpp/include

LIB := -L$(CUDA)/lib \
       -L$(CUDPP)/lib



