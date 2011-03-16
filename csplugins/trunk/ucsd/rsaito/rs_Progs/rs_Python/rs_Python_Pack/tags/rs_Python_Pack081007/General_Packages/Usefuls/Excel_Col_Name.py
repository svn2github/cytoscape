#!/usr/bin/env python

ALPHA = ("A","B","C","D","E",
         "F","G","H","I","J",
         "K","L","M","N","O",
         "P","Q","R","S","T",
         "U","V","W","X","Y",
         "Z") 

ALPHA0 = ("","A","B","C","D","E",
          "F","G","H","I","J",
          "K","L","M","N","O",
          "P","Q","R","S","T",
          "U","V","W","X","Y",
          "Z")

def col_num_to_alphabet_dead(col_num):
    """ col_num starts from 0 """
    
    ret_alpha = ""
    denominator = len(ALPHA)   
    col_num_remain = col_num
    idx = col_num_remain % denominator
    ret_alpha += ALPHA[ idx ]
    col_num_remain -= col_num_remain % denominator
    col_num_remain /= denominator
    
    denominator = len(ALPHA0)
    while col_num_remain > 0:
        idx = col_num_remain % denominator
        # print idx, col_num_remain, denominator
        ret_alpha += ALPHA0[ idx ]
        col_num_remain -= col_num_remain % denominator
        col_num_remain /= denominator
    
    return ret_alpha[::-1]

def col_num_to_alphabet(col_num):
    """ col_num starts from 1 """
    
    ret_alpha = ""
    col_num_remain = col_num
    
    while col_num_remain > 0:
        idx = (col_num_remain - 1) % len(ALPHA)
        # print idx, col_num_remain, denominator
        ret_alpha += ALPHA[ idx ]
        col_num_remain -= (col_num_remain - 1) % len(ALPHA)
        col_num_remain /= len(ALPHA)
    
    return ret_alpha[::-1]


if __name__ == "__main__":
    
    for i in range(0,300):
        print i, col_num_to_alphabet(i)

    