CXX         = g++-mp-4.4
CXXFLAGS    = -W -Wall -Werror -Iinclude -DGDB='"/Developer/usr/bin/gdb"'
SRCS        = src/*.cc
TEMP        = $(addprefix objs/,$(notdir $(wildcard src/*.cc)))
OBJS        = $(TEMP:.cc=.o)


all: lib/libiViaCoreSubset.a

lib/libiViaCoreSubset.a: $(OBJS)
	@echo "Linking $@..."
	@ar cqs $@ $^

objs/%.o: src/%.cc
	@echo "Compiling $<..."
	@$(CXX) $(CXXFLAGS) $< -c -o $@

clean:
	rm -f objs/* lib/*.a

