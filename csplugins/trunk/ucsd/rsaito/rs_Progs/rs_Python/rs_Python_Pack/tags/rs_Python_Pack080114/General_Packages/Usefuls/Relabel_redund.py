#!/usr/bin/env python

import Counter
import Data_Struct.NonRedSet1 as NonRedSet1

REDU_SEP1 = "-"
REDU_SEP2 = "-RS-"

class Relabel_redund:
    def __init__(self, ilist):
        self.ilist = ilist

    def relabel(self):
        ct1 = Counter.Counter2()
        for elabel in self.ilist:
            ct1.count_up(elabel)

        new_labels = []
        ct2 = Counter.Counter2()
        for i in range(len(self.ilist)):
            elabel = self.ilist[i]
            ct2.count_up(elabel)
            if ct1.get_counter(elabel) < 2:
                new_label = elabel
            else:
                new_label = elabel + REDU_SEP1 + `ct2.get_counter(elabel)`
                if (new_label in self.ilist or
                    new_label in new_labels):
                    except_counter = 0
                    while (new_label in self.ilist or
                           new_label in new_labels):
                        new_label = elabel + REDU_SEP2 + `except_counter`
                        except_counter += 1

            new_labels.append(new_label)

        self.new_labels = new_labels
        
        self.old_to_new = NonRedSet1.NonRedSetDict()
        self.new_to_old = {}
        for i in range(len(new_labels)):
            old_label = self.ilist[i]
            new_label = self.new_labels[i]
            self.old_to_new.append_Dict(old_label, new_label)
            self.new_to_old[new_label] = old_label

    def get_old_labels(self):
        return self.ilist

    def get_new_labels(self):
        return self.new_labels

    def get_old_to_new(self, old_label):
        return self.old_to_new.ret_set_Dict(old_label)

    def get_new_to_old(self, old_label):
        return self.new_to_old[old_label]


if __name__ == "__main__":
    ilist = [ "A", "B", "C", "D", "C", "C", "C", "D"]
    rr = Relabel_redund(ilist)
    rr.relabel()
    print rr.get_new_labels()

    for old_label in rr.get_old_labels():
        print old_label, rr.get_old_to_new(old_label)

    print " * * * * * "

    for new_label in rr.get_new_labels():
        print new_label, rr.get_new_to_old(new_label)
