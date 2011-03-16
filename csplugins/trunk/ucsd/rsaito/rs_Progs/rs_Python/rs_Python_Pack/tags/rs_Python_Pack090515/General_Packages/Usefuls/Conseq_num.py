#!/usr/bin/env python

def conseq2segm(nums):
    nums_sorted = nums[:]
    nums_sorted.sort()
    
    c_start = None
    prev = None
    ret = []
    for num in nums_sorted:
        if prev is None:
            c_start = num
        elif num == prev or num == prev + 1:
            pass
        else:
            ret.append((c_start, prev))
            c_start = num
        prev = num
        
    ret.append((c_start, prev))

    return ret

if __name__ == "__main__":
    print conseq2segm([1,3,4,5,6,8,10,11,12,13,14,20])
    print conseq2segm([1,3,4,5,6,8,10,11,12,13,14,15])
    print conseq2segm([1])