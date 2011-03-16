#!/usr/bin/env python

import ListList

class ListList_Size_Conv:
    def __init__(self, listlist):

        if not isinstance(listlist, ListList.ListList):
            self.listlist_with_missing = ListList.ListList(listlist)
        else:
            self.listlist_with_missing = listlist

        self.num_listlist = ListList.ListList() 
        self.missing_to_num_listlist = []
        self.num_listlist_to_missing = []
        self.make_conv_index()
        self.conv_idx_missing_to_numlistlist()

    def add_labels(self, labels):
        if len(labels) != self.listlist_with_missing.max_length():
            raise "Data length and label size mismatch"

        self.labels = labels

    def make_conv_index(self):
        
        ptr1 = 0
        ptr2 = 0
        
        for idx in range(0, self.listlist_with_missing.max_length()):
            if self.listlist_with_missing.nth_nums_all_valid(idx):
                 self.missing_to_num_listlist.append(ptr2)
                 self.num_listlist_to_missing.append(ptr1)
                 ptr2 += 1
            else:
                 self.missing_to_num_listlist.append(False)
            ptr1 += 1
        
    def valid_labels(self):

        ret = []
        for i in range(len(self.labels)):
            if not (self.missing_to_num_listlist[i] is False):
                ret.append(self.labels[i])
        return ret

    def conv_idx_missing_to_numlistlist(self):

        self.num_listlist.empty_lists(self.listlist_with_missing.num_lists())
        
        for ct in range(0,len(self.missing_to_num_listlist)):
             if not (self.missing_to_num_listlist[ct] is False):
                 elems = self.listlist_with_missing.nth_nums_all_valid(ct)
                 self.num_listlist.append_all(elems)

    def conv_idx_numlistlist_to_missing(self):
         """ Original non-numbers will not be erased. """
         
         ct = 0
         for indx in self.num_listlist_to_missing:
             elems = self.num_listlist.nth_nums_all_valid(ct)
             self.listlist_with_missing.change_every(elems, indx)
             ct += 1
             
    def get_idx_numlistlist_to_missing(self, listoption = False):
        
        ret = ListList.ListList()
        ret.empty_lists(self.listlist_with_missing.num_lists())

        for from_idx in self.missing_to_num_listlist:
            if from_idx is False:
                ret.append_all_same(False)
            else:
                ret.append_all(self.num_listlist.nth_nums_all_valid(from_idx))

        if listoption:
            return ret.get_all_lists()
        else:
            return ret
         
    def get_num_listlist(self, listoption = False):
        
        if listoption:
            return self.num_listlist.get_all_lists()
        else:
            return self.num_listlist
     
    def get_listlist_with_missing(self, listoption = False):

        if listoption:
            return self.listlist_with_missing.get_all_lists()
        else:
            return self.listlist_with_missing

    def import_num_listlist(self, ill):

        if not isinstance(ill, ListList.ListList):
            ll = ListList.ListList(ill)
        else:
            ll = ill

        if (self.num_listlist.num_lists() != ll.num_lists()):
            raise "Number of lists mismatches ... " + \
                  `self.num_listlist.num_lists()` + " --- " + `ll.num_lists()`
        
        for i in range(ll.num_lists()):
            if (len(self.num_listlist.get_all_lists()[i])
                != len(ll.get_all_lists()[i])):
                raise "List size mismatches ... "
                      
        self.num_listlist = ll
            

if __name__ == "__main__":

    ll = ListList.ListList()
    ll.add_lists((["W",1,2,3, 4, 5,"X",7, 8],
                  [ 0, 1,2,3,"Y",5, 6, 7, 8.1],
                  [ 0, 1,2,3, 4, 5, 6,"Z",8]))

    print "Orininal List:"
    print ll.get_all_lists()

    llsc = ListList_Size_Conv(ll)
    llsc.add_labels(["Zero", "One", "Two", "Three", "Four",
                     "Five", "Six", "Seven", "Eight"])

    print "Missing to Number list conversion:"
    print llsc.missing_to_num_listlist

    print "Number list to Missing conversion:"
    print llsc.num_listlist_to_missing

    print "Number list:"
    print llsc.num_listlist.get_all_lists()

    ll2 = ListList.ListList()
    ll2.add_lists([[10, 2, 3, 5, 8],
                   [15, 2, 3, 5, 8.1],
                   [19, 2, 30, 5, 8]])
    llsc.import_num_listlist(ll2)

    print "Current missing list:"
    print llsc.get_listlist_with_missing(True)   #.get_all_lists()

    print "Imported number list:"
    print llsc.get_num_listlist(True)

    print "Modified missing list:"
    print llsc.get_idx_numlistlist_to_missing(True) # .get_all_lists()

    print "Valid labels:"
    print llsc.valid_labels()
