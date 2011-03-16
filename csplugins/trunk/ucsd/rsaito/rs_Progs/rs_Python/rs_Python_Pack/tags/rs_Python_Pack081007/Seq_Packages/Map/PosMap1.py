#!/usr/bin/env python

from Usefuls.Instance_check import instance_class_check

class PosMap_Obj:
    def __init__(self, ID = None):
        self.ID = ID
        self.mapped_to = None
        self.mapped_to_in_pos_supr = None
        self.mapped_to_in_pos_self = None
        self.contains = []
        self.contains_in_pos_in_self = []
        self.contains_in_pos_in_inst = []

    def get_ID(self):
        return self.ID

    def get_containing_obj(self):
        return self.contains

    def map(self, super_posmap_obj, pos1, pos2, pos3, pos4,
            position_check = True):
        """
        ---------pos1--------------pos2--------- (super_posmap_obj)
               --pos3--------------pos4---       (self)
               """

        instance_class_check(super_posmap_obj, PosMap_Obj)
        if position_check:
            self.pos_check(pos1, pos2, pos3, pos4)
        
        if self.mapped_to is None:
            self.mapped_to = super_posmap_obj
            self.mapped_to_in_pos_supr = (pos1, pos2)
            self.mapped_to_in_pos_self = (pos3, pos4)
        else:
            raise "The object is mapped to multiple objects."

    
    def take(self, posmap_obj, pos1, pos2, pos3, pos4,
             position_check = True):        
        """
        ---------pos1--------------pos2--------- (self)
               --pos3--------------pos4---       (posmap_obj)
               """

        instance_class_check(posmap_obj, PosMap_Obj)
        if position_check:
            posmap_obj.pos_check(pos1, pos2, pos3, pos4)
        
        self.contains.append(posmap_obj)
        self.contains_in_pos_in_self.append((pos1, pos2))
        self.contains_in_pos_in_inst.append((pos3, pos4))

        posmap_obj.map(self, pos1, pos2, pos3, pos4, position_check)
        

    def pos_check(self, pos1, pos2, pos3, pos4):
        if pos2 - pos1 != pos4 - pos3:
            raise "Position error..."
    
    def pos_transfer_to_super(self, pos):
        return (self.mapped_to_in_pos_supr[0] -
                self.mapped_to_in_pos_self[0]) + pos

    def pos_transfer_to_super_end(self, pos):
        return (self.mapped_to_in_pos_supr[0] -
                self.mapped_to_in_pos_self[0]) + pos

    def region_transfer_to_super(self, pos_start, pos_end):
        return (self.pos_transfer_to_super(pos_start),
                self.pos_transfer_to_super_end(pos_end))
                

    def mapped_positions(self):

        pos_info = []
        for obj in self.contains:
            pos_info.append((obj,
                             obj.mapped_to_in_pos_supr[0],
                             obj.mapped_to_in_pos_supr[1]))
            pos_info_sub = obj.mapped_positions()
            for pinfos in pos_info_sub:
                so, ss, se = pinfos
                r_start, r_end = obj.region_transfer_to_super(ss, se)
                pos_info.append((so, r_start, r_end))

        return pos_info


    def mapped_positions_dict(self):

        pos_info = {}
        for obj in self.contains:
            pos_info[ obj.get_ID() ] = (
                             obj.mapped_to_in_pos_supr[0],
                             obj.mapped_to_in_pos_supr[1])
            pos_info_sub = obj.mapped_positions()
            for pinfos in pos_info_sub:
                so, ss, se = pinfos
                r_start, r_end = obj.region_transfer_to_super(ss, se)
                pos_info[ so.get_ID() ] = (r_start, r_end)
                
        return pos_info
    

    def __repr__(self):
        return "PosMap_Obj: " + self.ID

    
    def display_all_info(self):
        print self
        print "ID:", self.ID
        print "mapped_to:", self.mapped_to
        print "mapped_to_in_pos_supr:", self.mapped_to_in_pos_supr
        print "mapped_to_in_pos_self:", self.mapped_to_in_pos_self
        print "contains:", self.get_containing_obj()
        print "contains_in_pos_in_self", self.contains_in_pos_in_self
        print "contains_in_pos_in_inst", self.contains_in_pos_in_inst
        print 


class PosMap_Obj_Prot_to_DNA(PosMap_Obj):
    """ This instance: Protein, Super instance: DNA """
    def pos_check(self, pos1, pos2, pos3, pos4):
        if pos2 - pos1 + 1 != (pos4 - pos3 + 1) * 3:
            raise "Position error..."

    def pos_transfer_to_super(self, pos_start):
        return (pos_start - self.mapped_to_in_pos_self[0]) * 3 + \
               self.mapped_to_in_pos_supr[0]

    def pos_transfer_to_super_end(self, pos_end):
        return (pos_end - self.mapped_to_in_pos_self[0]) * 3 + \
               self.mapped_to_in_pos_supr[0] + 2


if __name__ == "__main__":
    
    obj1 = PosMap_Obj("Main")
    obj2 = PosMap_Obj("Sub1")
    obj3 = PosMap_Obj("Sub2")
    obj4 = PosMap_Obj("SubSub1")
    obj5 = PosMap_Obj("SubSubSub1")

    obj1.take(obj2, 10, 20, 5, 15)
    obj1.take(obj3, 20, 40, 100, 120)
    obj2.take(obj4, 10, 12, 3, 5)
    obj4.take(obj5, 4, 5, 1, 2)
    
    obj1.display_all_info()
    obj2.display_all_info()
    obj3.display_all_info()
    obj4.display_all_info()
    obj5.display_all_info()

    objp1 = PosMap_Obj_Prot_to_DNA("Subp1")
    objp2 = PosMap_Obj("Subsubp1")
    obj1.take(objp1, 30, 47, 10, 15)
    objp1.take(objp2, 11, 14, 111, 114)

    objp1.display_all_info()
    objp2.display_all_info()

    print obj1.mapped_positions()
    print obj1.mapped_positions_dict()

