#!/usr/bin/env python

class ListList:
    def __init__(self, ilists = None):
        if ilists is None:
            self.slists = []
        else:
            self.slists = ilists

    def add_list(self, ilist):
	self.slists.append(ilist)
    
    def add_lists(self, ilists):
	self.slists += ilists

    def empty_lists(self, num):
        self.slists = []
        for ct in range(num):
            self.slists.append([])

    def nth_elems(self, nth):
	ret = []
	for slist in self.slists:
	    if nth < len(slist):
		ret.append(slist[nth])
	return ret

    def nth_nums(self, nth):
	ret = []
	for slist in self.slists:
	    if (nth < len(slist) and
                (type(slist[nth]) == int or type(slist[nth]) == float)):
		ret.append(slist[nth])
	return ret
    
    def nth_nums_all_valid(self, nth):
	ret = []
	for slist in self.slists:
	    if (nth >= len(slist) or
                (type(slist[nth]) != int and type(slist[nth]) != float)):
                return False
            else:
                ret.append(slist[nth])
	return ret

    def num_lists(self):
        return len(self.slists)

    def max_length(self):
	max = 0
	for slist in self.slists:
	    if max < len(slist):
		max = len(slist)
	return max

    def get_all_lists(self):
        return self.slists

    def to_single_list(self):
	single_list = []
	for i in range(self.max_length()):
	    nth = self.nth_nums(i)
	    if nth:
		single_list.append(1.0*sum(nth)/len(nth))
	    else:
		single_list.append(None)
	return single_list

    def append_all(self, ilist):

        if len(ilist) != len(self.slists):
            raise "List size length mismatch"
        
        for ct in range(len(ilist)):
            self.slists[ct].append(ilist[ct])

    def append_all_same(self, elem):
       
        for elist in self.slists:
            elist.append(elem)

    def change_every(self, ilist, nth):
        if len(ilist) != len(self.slists):
            raise "List size length mismatch"

        for ct in range(len(ilist)):
            self.slists[ct][nth] = ilist[ct]       

if __name__ == "__main__":
    ll = ListList([[2,9,8]])
    ll.add_list([1,3,5,"Rin",9])
    ll.add_list([4,6,8])
    ll.add_list([4,5,7,4,7,8])

    print ll.get_all_lists()
    print ll.max_length()
    print ll.nth_nums(2)
    print ll.nth_nums_all_valid(2)
    print ll.to_single_list()
    print ll.num_lists()

    ll = ListList()
    print ll.slists
    ll.empty_lists(3)
    print ll.get_all_lists()
    ll.append_all(["A", "B", "C"])
    ll.append_all_same("XXX")
    print ll.get_all_lists()

    ll = ListList()
    ll.add_lists([[1,2,3],[4,5,6],[7,8,9]])
    ll.change_every(["A", "B", "C"], 1)
    print ll.get_all_lists()
