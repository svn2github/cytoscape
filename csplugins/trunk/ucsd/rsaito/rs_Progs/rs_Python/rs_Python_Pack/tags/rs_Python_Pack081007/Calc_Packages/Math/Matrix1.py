#!/usr/bin/env python

import Usefuls.DictKeyIterator

class Matrix1:
    def __init__(self):
	self.matrix = []
	self.label_row = []
	self.label_column = []

    def input_from_dict_Square(self, i_dict):
	dki = Usefuls.DictKeyIterator.DictKey_Iterator(i_dict)
	d_key = {}
	while dki.next():
	    e1, e2 = dki.current_key()
	    d_key[ e1 ] = ""
	    d_key[ e2 ] = ""
	self.label_row = d_key.keys()
	self.label_column = d_key.keys()
	for i in range(len(self.label_row)):
	    self.matrix.append([0] * len(self.label_row))
	dki.__init__(i_dict)
	while dki.next():
	    e1, e2 = dki.current_key()
	    i = self.label_row.index(e1)
	    j = self.label_row.index(e2)
	    self.matrix[i][j] = dki.current_value()

    def input_from_dict_Rectangle(self, i_dict):
	dki = Usefuls.DictKeyIterator.DictKey_Iterator(i_dict)
	d_key_row = {}
	d_key_column = {}
	while dki.next():
	    e1, e2 = dki.current_key()
	    d_key_row[ e1 ] = ""
	    d_key_column[ e2 ] = ""
	self.label_row = d_key_row.keys()
	self.label_column = d_key_column.keys()
	for i in range(len(self.label_row)):
	    self.matrix.append([0] * len(self.label_column))
	dki.__init__(i_dict)
	while dki.next():
	    e1, e2 = dki.current_key()
	    i = self.label_row.index(e1)
	    j = self.label_column.index(e2)
	    self.matrix[i][j] = dki.current_value()

    def get_matrix(self):
	return self.matrix

    def get_label(self):
	return self.label_row

    def get_label_row(self):
	return self.label_row

    def get_label_column(self):
	return self.label_column

if __name__ == "__main__":
    h = { "Rin": { "Chiho": 4, "Kiyomi": 3 }, 
	  "Chiho": { "Kiyomi" : 5, "HideM": 3, "Rin": 4 },
	  "HideM": { "Chiho": 4, "Kiyomi": 4 }}
    sma = Matrix1()
    sma.input_from_dict_Rectangle(h)
    print sma.get_label_row()
    print sma.get_label_column()
    print sma.get_matrix()
