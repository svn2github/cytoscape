all: libigraphWrapper.dylib


igraphJNA.o : igraphJNA.cpp igraphJNA.h                                                                                                                                                                                                        
	g++ -c -fPIC ${CFLAGS} ${STATIC_LIBS} -o igraphJNA.o igraphJNA.cpp

libigraphWrapper.dylib : igraphJNA.o
	g++ -shared -soname,libigraphWrapper.dylib ${LIB_FLAGS}-o libigraphWrapper.dylib igraphJNA.o

clean :
	rm igraphJNA.o 
	rm libIgraphWrapper.dylib 


CFLAGS=-I${IGRAPH_PATH}/include
LIB_FLAGS=-L${IGRAPH_PATH}/src/.libs -ligraph 
STATIC_LIBS=-L${IGRAPH_PATH}/src/libs/ 

IGRAPH_PATH=/Users/gerardohuck/igraph

