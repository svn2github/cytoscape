CXX         = g++-mp-4.4
CXXFLAGS    = -W -Wall -Werror -I../include
LIBS        = -lcurl -lpcre -lssl -lcrypto -L/opt/local/lib -lmagic -lz -L ../lib -liViaCoreSubset
PROGS       = FetchPage GSAuth


all: $(PROGS)

FetchPage: FetchPage.o
	$(CXX) -o $@ $^ $(LIBS)

FetchPage.o: FetchPage.cc
	$(CXX) $(CXXFLAGS) -c $<

GSAuth: GSAuth.o
	$(CXX) -o $@ $^ $(LIBS)

GSAuth.o: GSAuth.cc
	$(CXX) $(CXXFLAGS) -c $<

clean:
	rm -f *.o *~ $(PROGS)
