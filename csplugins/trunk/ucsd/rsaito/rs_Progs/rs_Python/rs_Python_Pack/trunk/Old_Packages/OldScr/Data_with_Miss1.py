#!/usr/bin/env python

import ListList

class Data_with_Miss:
    def __init__(self, ilist):
        self.list_with_missing = ilist
        self.num_list = []
        self.missing_to_num_list = []
        self.num_list_to_missing = []
        
    def discard_missing(self):
        
         ptr1 = 0
         ptr2 = 0

         for edata in self.list_with_missing:
             if (type(edata) == int or 
                 type(edata) == float):
                 self.num_list.append(edata)
                 self.missing_to_num_list.append(ptr2)
                 self.num_list_to_missing.append(ptr1)
                 ptr2 += 1
             else:
                 self.missing_to_num_list.append(False)
             ptr1 += 1

    def reflect_to_missing(self):
         
         ct = 0
         for indx in self.num_list_to_missing:
             self.list_with_missing[indx] = self.num_list[ct]
             ct += 1
         
    def get_num_list(self):
         return self.num_list
     
    def get_list_with_missing(self):
         return self.list_with_missing


class List_Size_Conv:
    def __init__(self, ilist):
        self.list_with_missing = ilist
        self.num_list = []
        self.missing_to_num_list = []
        self.num_list_to_missing = []
     
    def make_conv_index(self):
        
        ptr1 = 0
        ptr2 = 0
        
        for edata in self.list_with_missing:
            if (type(edata) == int or 
                type(edata) == float):
                 self.missing_to_num_list.append(ptr2)
                 self.num_list_to_missing.append(ptr1)
                 ptr2 += 1
            else:
                 self.missing_to_num_list.append(False)
            ptr1 += 1
        
        
    def conv_idx_missing_to_numlist(self):
        
         self.num_list = []
         
         for ct in range(0,len(self.missing_to_num_list)):
             if not (self.missing_to_num_list[ct] is False):
                 self.num_list.append(self.list_with_missing[ct])
              
    def conv_idx_numlist_to_missing(self):
         """ Original non-numbers will not be erased. """
         
         ct = 0
         for indx in self.num_list_to_missing:
             self.list_with_missing[indx] = self.num_list[ct]
             ct += 1
             
    def get_idx_numlist_to_missing(self):
        
        ret = []

        for from_idx in self.missing_to_num_list:
            if from_idx is False:
                ret.append(False)
            else:
                ret.append(self.num_list[from_idx])

        return ret
        
         
    def get_num_list(self):
         return self.num_list
     
    def get_list_with_missing(self):
         return self.list_with_missing

class ListList_Size_Conv:
    def __init__(self, listlist):

        if not isinstance(listlist, ListList.ListList):
            raise "Instance type mismatch: ListList expected."
        self.listlist_with_missing = listlist

        self.num_listlist = []
        self.missing_to_num_listlist = []
        self.num_listlist_to_missing = []
     
    def make_conv_index(self):
        
        ptr1 = 0
        ptr2 = 0
        
        for idx in range(0, self.listlist_with_missing.max_length()):
            if self.listlist_with_missing.nth_nums_all_valid(idx):
                 self.missing_to_num_listlist.append(ptr2)
                 self.num_listlist_to_missing.append(ptr1)
                 ptr2 += 1
            else:
                 self.missing_to_num_list.append(False)
            ptr1 += 1
        
        
    def conv_idx_missing_to_numlistlist(self):
        
         self.num_listlist = []
         
         for ct in range(0,len(self.missing_to_num_listlist)):
             if not (self.missing_to_num_listlist[ct] is False):
                 self.num_listlist.append(self.listlist_with_missing[ct])
              
    def conv_idx_numlistlist_to_missing(self):
         """ Original non-numbers will not be erased. """
         
         ct = 0
         for indx in self.num_listlist_to_missing:
             self.listlist_with_missing[indx] = self.num_listlist[ct]
             ct += 1
             
    def get_idx_numlistlist_to_missing(self):
        
        ret = []

        for from_idx in self.missing_to_num_listlist:
            if from_idx is False:
                ret.append(False)
            else:
                ret.append(self.num_listlist[from_idx])

        return ret
        
         
    def get_num_listlist(self):
         return self.num_listlist
     
    def get_listlist_with_missing(self):
         return self.listlist_with_missing


if __name__ == "__main__":
    """
    dm = Data_with_Miss([3,5,None, 6.3,10.3])
    print dm.get_list_with_missing()
    dm.discard_missing()
    print dm.get_num_list()
    dm.get_num_list()[2] = 1000
    dm.reflect_to_missing()
    print dm.get_list_with_missing()
    """
    
    lsc = List_Size_Conv(["Fukuzawa", 3,4.1, "Rintaro", 3, "Yukichi", 2])
    lsc.make_conv_index()
    lsc.conv_idx_missing_to_numlist()
    print lsc.get_list_with_missing()
    print lsc.get_num_list()
    lsc.get_num_list()[2] = "XXXXX"
    lsc.conv_idx_numlist_to_missing()
    print lsc.get_list_with_missing()
    print lsc.get_idx_numlist_to_missing()
    
