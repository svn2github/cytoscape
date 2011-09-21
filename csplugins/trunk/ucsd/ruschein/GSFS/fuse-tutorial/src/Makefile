CC=gcc-mp-4.4

bbfs : bbfs.o log.o
	$(CC) -g `pkg-config fuse --libs` -o bbfs bbfs.o log.o

bbfs.o : bbfs.c log.h params.h
	$(CC) -g -Wall `pkg-config fuse --cflags` -c bbfs.c

log.o : log.c log.h params.h
	$(CC) -g -Wall `pkg-config fuse --cflags` -c log.c

clean:
	rm -f bbfs *.o

dist:
	rm -rf fuse-tutorial/
	mkdir fuse-tutorial/
	cp ../*.html fuse-tutorial/
	mkdir fuse-tutorial/example/
	mkdir fuse-tutorial/example/mountdir/
	mkdir fuse-tutorial/example/rootdir/
	echo "a bogus file" > fuse-tutorial/example/rootdir/bogus.txt
	mkdir fuse-tutorial/src
	cp Makefile *.c *.h fuse-tutorial/src/
	tar cvzf ../../fuse-tutorial.tgz fuse-tutorial/
	rm -rf fuse-tutorial/
