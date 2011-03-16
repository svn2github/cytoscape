#!/usr/bin/env python

def control_bait(baitID):
    if baitID == "Initial_Initial":
        return "Initial"
    elif baitID == "Mock_Mock":
        return "Mock"
    else:
        return False

if __name__ == "__main__":
    print control_bait("Initial_Initial")
    print control_bait("Mock_Mock")
    print control_bait("XXX")
