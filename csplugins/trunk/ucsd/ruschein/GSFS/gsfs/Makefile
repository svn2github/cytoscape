CCC         = g++-mp-4.4
LIBS        = -lcurl -lpcre -lssl -lcrypto -L/opt/local/lib -lmagic -lz -L ../lib -liViaCoreSubset


gsfs : gsfs.o GsSession.o log.o
	$(CCC) -std=gnu++0x -g `pkg-config fuse --libs` $(LIBS) -o gsfs $^

gsfs.o : gsfs.cc log.h params.h
	$(CCC) -std=gnu++0x -g -Wall -I../include `pkg-config fuse --cflags` -c gsfs.cc

GsSession.o : GsSession.cc GsSession.h log.h params.h
	$(CCC) -std=gnu++0x -g -Wall -I../include `pkg-config fuse --cflags` -c GsSession.cc

log.o : log.c log.h params.h
	$(CCC) -std=gnu++0x -g -Wall `pkg-config fuse --cflags` -c log.c

clean:
	rm -f gsfs *.o
