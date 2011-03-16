#!/usr/bin/env python

class Instance_Type_Mismatch:
    pass

def instance_class_check(inst, cls):
    if not isinstance(inst, cls):
        # print "Instance:", inst
        # print "Class:", cls
        raise Instance_Type_Mismatch


if __name__ == "__main__":

    class class1:pass
    class class2:pass

    inst1 = class1()
    inst2 = class2()

    instance_class_check(inst1, class1)
    print "Check #1 OK."
    
    instance_class_check(inst2, class1)
    print "Check #2 OK."
              
