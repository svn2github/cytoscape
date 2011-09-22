CXX         = g++-mp-4.4
CXXFLAGS    = -W -Wall -Werror -I../include
LIBS        = -lcurl -lpcre -lssl -lcrypto -L/opt/local/lib -lmagic -lz -L ../lib -liViaCoreSubset
PROGS       = FetchPage GSAuth GsSessionTest ParseDirectory JSONScannerTest


all: $(PROGS)

FetchPage: FetchPage.o
	$(CXX) -o $@ $^ $(LIBS)

FetchPage.o: FetchPage.cc
	$(CXX) $(CXXFLAGS) -c $<

GSAuth: GSAuth.o
	$(CXX) -o $@ $^ $(LIBS)

GSAuth.o: GSAuth.cc
	$(CXX) $(CXXFLAGS) -c $<

GsSession.o: GsSession.cc GsSession.h
	$(CXX) $(CXXFLAGS) -I. -c $<

GsSessionTest.o: GsSessionTest.cc GsSession.h
	$(CXX) $(CXXFLAGS) -I. -c $<

GsSessionTest: GsSessionTest.o GsSession.o
	$(CXX) -o $@ $^ $(LIBS)

ParseDirectory.o: ParseDirectory.cc
	$(CXX) $(CXXFLAGS) -I. -c $<

ParseDirectory: ParseDirectory.o
	$(CXX) -o $@ $^ $(LIBS)

JSONScanner.o: JSONScanner.cc JSONScanner.h
	$(CXX) $(CXXFLAGS) -I. -c $<

JSONScannerTest.o: JSONScannerTest.cc JSONScanner.h
	$(CXX) $(CXXFLAGS) -I. -c $<

JSONScannerTest: JSONScannerTest.o JSONScanner.o
	$(CXX) -o $@ $^ $(LIBS)

clean:
	rm -f *.o *~ $(PROGS)
