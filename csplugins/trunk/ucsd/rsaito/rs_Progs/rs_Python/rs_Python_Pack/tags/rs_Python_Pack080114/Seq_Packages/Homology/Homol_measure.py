#!/usr/bin/env python

from Usefuls.Instance_check import instance_class_check

class HM:
    def __init__(self, eval = None,
                 identity = None,
                 positive = None,
                 overlap = None,
                 hit_len_ratio_query = None,
                 hit_len_ratio_subj = None):
        
        self.evalue = eval
        self.identity = identity
        self.identity_positive = positive
        self.overlap  = overlap
        self.hit_len_ratio_query = hit_len_ratio_query
        self.hit_len_ratio_subj  = hit_len_ratio_subj

    def set_evalue(self, eval):
        self.evalue = eval

    def set_identity(self, identity):
        self.identity = identity

    def set_positive(self, identity):
        self.identity_positive = identity

    def set_overlap(self, overlap):
        self.overlap = overlap

    def get_evalue(self):
        return self.evalue

    def get_identity(self):
        return self.identity

    def get_positive(self):
        return self.identity_positive

    def get_overlap(self):
        return self.overlap

    def get_hit_len_ratio_query(self):
        return self.hit_len_ratio_query

    def get_hit_len_ratio_subj(self):
        return self.hit_len_ratio_subj

    def eval(self, hm):
        """ Evaluates whether given hm satisfies threshold of this instance. """
        instance_class_check(hm, HM)

        if self.get_evalue() != None and hm.get_evalue() is None:
            return False
        if self.get_identity() != None and hm.get_identity() is None:
            return False
        if self.get_positive() != None and hm.get_positive() is None:
            return False
        if self.get_overlap() != None and hm.get_overlap() is None:
            return False
        if (self.get_hit_len_ratio_query() != None and
            hm.get_hit_len_ratio_query() is None):
            return False
        if (self.get_hit_len_ratio_subj() != None and
            hm.get_hit_len_ratio_subj() is None):
            return False

        if (self.get_evalue() != None and hm.get_evalue() != None and
            self.get_evalue() < hm.get_evalue()):
            return False

        if (self.get_identity() != None and hm.get_identity() != None and
            self.get_identity() > hm.get_identity()):
            return False

        if (self.get_positive() != None and hm.get_positive() != None and
            self.get_positive() > hm.get_positive()):
            return False

        if (self.get_overlap() != None and hm.get_overlap() != None and
            self.get_overlap() > hm.get_overlap()):
            return False

        if (self.get_hit_len_ratio_query() != None and
            hm.get_hit_len_ratio_query()   != None and
            self.get_hit_len_ratio_query() > hm.get_hit_len_ratio_query()):
            return False

        if (self.get_hit_len_ratio_subj() != None and
            hm.get_hit_len_ratio_subj()   != None and
            self.get_hit_len_ratio_subj() > hm.get_hit_len_ratio_subj()):
            return False

        return True


    def __gt__(self, val):
        return self.get_evalue() > val

    def __lt__(self, val):
        return self.get_evalue() < val

    def __ge__(self, val):
        return self.get_evalue() >= val

    def __le__(self, val):
        return self.get_evalue() <= val

    def __eq__(self, val):
        return self.get_evalue() == val

    def __repr__(self):

        if self.get_evalue() is None:
            eval = "-"
        else:
            eval = `self.get_evalue()`
        if self.get_identity() is None:
            ident = "-"
        else:
            ident = `self.get_identity()`
        if self.get_positive() is None:
            pos = "-"
        else:
            pos = `self.get_positive()`
        if self.get_overlap() is None:
            overl = "-"
        else:
            overl = `self.get_overlap()`
        if self.get_hit_len_ratio_query() is None:
            hlr_query = "-"
        else:
            hlr_subj = `self.get_hit_len_ratio_query()`
        if self.get_hit_len_ratio_subj() is None:
            hlr_subj = "-"
        else:
            hlr_subj = `self.get_hit_len_ratio_subj()`

        return("<Homology measure: E-value %s, Identity %s, Positive %s, Overlap %s, Hit_len_ratio_query %s, Hit_len_ratio_subject %s>" % 
               (`eval`, `ident`, `pos`, `overl`, `hlr_query`, `hlr_subj`))


if __name__ == "__main__":
    
    hm = HM(10.0)
    print hm.get_evalue()
    print hm < 12
    print hm

    print HM(0.2, 0.4, 0.8, 10, 0.9, 0.8).eval(HM(0.1, 0.5, 0.3, 10, 0.9, 0.7))
    print HM(0.2, 0.4, 0.8, 10, 0.9, 0.8).eval(HM(0.1, 0.5, 0.7, 10, 0.9, 0.7))
    print HM(0.2, 0.4, 0.8, 10, 0.9, 0.8).eval(HM(0.1, 0.5, 0.8, 10, 0.9, 0.8))
    print HM(0.2, 0.4, 0.8, 10, 0.9, 0.8).eval(HM(0.2, 0.5, 0.9, 11, 0.7, 0.9))
    print HM(0.2, 0.4, 0.8, 10, None, 0.8).eval(HM(0.2, 0.5, 0.9, 11, 0.9, 0.9))
    print HM(0.2, 0.4, 0.8, 10, 0.9, 0.8).eval(HM(0.2, 0.5, 0.9, 11, None, 0.9))
