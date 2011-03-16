INCLUDE = /usr/X11R6/include
LIB = /usr/X11R6/lib

kmeans: kmeans.o point.o plot_field.o rsgraph3.o initialize.o
	g++ -L${LIB} kmeans.o point.o plot_field.o \
	initialize.o rsgraph3.o -o kmeans -lm -lX11

kmeans.o:kmeans_MAC.c point.h plot_field.h rsgraph3.h
	g++ -I${INCLUDE} -c kmeans_MAC.c -o kmeans.o

point.o:point.c point.h
	g++ -c point.c

plot_field.o: plot_field.c rsgraph3.h
	g++ -c plot_field.c

initialize.o: initialize.c
	g++ -c initialize.c

rsgraph3.o:rsgraph3.c
	g++ -I${INCLUDE} -c rsgraph3.c

clean:
	rm *.o