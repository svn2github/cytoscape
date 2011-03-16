#!/usr/bin/env python

import Obj_Oriented.Obj_Factory1
import Expr_Packages.Expr_II.Transcript1 as Transcript
import SAT_gnuplot1
import Data_Struct.Hash2
import Data_Struct.DictSet1

class SAT:
    def __init__(self, satid):
        self.satid = satid
        self.transcript1 = None
        self.transcript2 = None
        self.info = {}

    def get_satid(self):
        return self.satid

    def set_transcript1(self, transcript):
        self.transcript1 = transcript

    def set_transcript2(self, transcript):
        self.transcript2 = transcript

    def get_transcripts(self):
        return self.transcript1, self.transcript2

    def get_transcript1(self):
        return self.transcript1

    def get_transcript2(self):
        return self.transcript2

    def get_antisense(self, transcript):
        if transcript == self.get_transcript1():
            return self.get_transcript2()
        elif transcript == self.get_transcript2():
            return self.get_transcript1()
        else:
            return None

    def set_info(self, key, val):
        self.info[ key ] = val

    def get_info(self, key):
        return self.info[ key ]


class SAT_Factory(Obj_Oriented.Obj_Factory1.Obj_Factory):
    def set_classobj(self):
        self.classobj = SAT


class SAT_Set:
    def __init__(self):
        self.sats = {}
        self._plus2minus = Data_Struct.DictSet1.DictSet()
        self._minus2plus = Data_Struct.DictSet1.DictSet()

    def add_sat(self, sat):
        """ sat must be constructed by factory. """
        
        self.sats[ sat.get_satid()] = sat
        t1, t2 = sat.get_transcripts()
        
        if t1 is not None and t2 is not None:
            self._plus2minus.append(t1, t2)  
            self._minus2plus.append(t2, t1)
            
    def plus2minus(self):
        return self._plus2minus
    
    def minus2plus(self):
        return self._minus2plus
    
    def get_transcripts(self):
        return self._plus2minus.keys() + self._minus2plus.keys()

    def get_sat(self, satid):
        return self.sats[ satid ]

    def get_satids(self):
        return self.sats.keys()

    def get_sats(self):
        return self.sats.values()


if __name__ == "__main__":
    sat = SAT("SAT001")
    sat.set_transcript1("T1")
    sat.set_transcript2("T2")
    print sat.get_satid()
    print sat.get_transcripts()
    print sat.get_transcript1()
    print sat.get_transcript2()
    print sat.get_antisense("T1")
    print sat.get_antisense("T2")
    print sat.get_antisense("T3")

    sat2 = SAT("SAT002")
    sat2.set_transcript1("T3")
    sat2.set_transcript2("T4")
    print sat2.get_transcripts()

    sat_set = SAT_Set()
    sat_set.add_sat(sat)
    sat_set.add_sat(sat2)

    print sat_set.get_transcripts()

    print sat_set.plus2minus()["T1"]
    print sat_set.plus2minus().keys()

    print sat_set.get_satids()
    print sat_set.get_sats()

    sat3 = SAT_Factory().make("SAT005")
    sat4 = SAT_Factory().make("SAT005")
    sat5 = SAT_Factory().make("SAT006")
    print id(sat3), id(sat4), id(sat5)
