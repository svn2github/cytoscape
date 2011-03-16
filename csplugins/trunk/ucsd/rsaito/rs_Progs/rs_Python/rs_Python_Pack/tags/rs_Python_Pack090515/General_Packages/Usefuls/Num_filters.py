#!/usr/bin/env python

def filter_floats(a1, a2):
    # String -> float conversion

    len_a = min(len(a1), len(a2))

    a1_filt = []
    a2_filt = []

    i = 0
    while i < len_a:
        try:
            e1 = float(a1[i])
            e2 = float(a2[i])
            a1_filt.append(e1)
            a2_filt.append(e2)
            
        except ValueError:
            pass

        i += 1

    return (a1_filt, a2_filt)

def filter_floats2(a1, a2):
    # Checking the object type

    len_a = min(len(a1), len(a2))

    a1_filt = []
    a2_filt = []

    i = 0
    while i < len_a:
	if ((type(a1[i]) == float or type(a1[i]) == int) and
	    (type(a2[i]) == float or type(a2[i]) == int)):
            a1_filt.append(a1[i])
            a2_filt.append(a2[i])
        i += 1

    return (a1_filt, a2_filt)


if __name__ == "__main__":

    a1 = (1,2,9,4,"-",6,7,8)
    a2 = (2,3,4,"-",6,7,0,9)

    a1_f, a2_f = filter_floats(a1, a2)
    print a1_f
    print a2_f

    a1_f, a2_f = filter_floats2(a1, a2)
    print a1_f
    print a2_f

