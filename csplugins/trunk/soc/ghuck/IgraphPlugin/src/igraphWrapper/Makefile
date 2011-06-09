all: libigraphWrapper.dylib


libigraphWrapper.dylib: igraphJNA.cpp igraphJNA.h                                                                                                                                                                                                        
	g++ -fPIC -shared -soname,libigraphWrapper.dylib ${CFLAGS} ${LIB_FLAGS} -o libigraphWrapper.dylib igraphJNA.cpp

clean :
	rm libIgraphWrapper.dylib 


CFLAGS=-I${IGRAPH_PATH}/include
LIB_FLAGS=-L${IGRAPH_PATH}/src/.libs -ligraph 


IGRAPH_PATH := /Users/gerardohuck/igraph/

