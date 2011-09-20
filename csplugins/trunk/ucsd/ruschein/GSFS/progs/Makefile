CXX         = g++-mp-4.4
CXXFLAGS    = -W -Wall -Werror -I../include


FetchPage: FetchPage.o
	$(CXX) -o $@ $^ -lcurl -lpcre -lssl -lcrypto -L/opt/local/lib -lmagic -lz -L ../lib -liViaCoreSubset

FetchPage.o: FetchPage.cc
	$(CXX) $(CXXFLAGS) -c $<

clean:
	rm -f *.o *~
