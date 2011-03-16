#!/usr/bin/env python

print [(x, y) for x in range(1, 4) for y in range(3, 0, -1) if x + y == 5]

print [x for x in ("a", 10.0, "b", 7.0, "c", "d", 5) if type(x) == int or type(x) == float ]