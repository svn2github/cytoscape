CXX         = g++-mp-4.4
CCFLAGS     = -W -Wall -Werror -Iinclude
SRCS        = src/*.cc
TEMP        = $(addprefix objs/,$(notdir $(wildcard src/*.cc)))
OBJS        = $(TEMP:.cc=.o)

all: $(OBJS)

objs/%.o: src/%.cc
	@echo "Compiling $<..."
	@$(CXX) $(CCFLAGS) $< -c -o $@

clean:
	rm -f objs/*
