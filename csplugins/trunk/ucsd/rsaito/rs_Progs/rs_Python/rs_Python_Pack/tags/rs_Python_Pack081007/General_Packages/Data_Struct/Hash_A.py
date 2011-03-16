#!/usr/bin/python

import sys
import string

class Hash_A:
    """ Reads tab-delimited file and make dictionary using items
    specified columns as key and another specified column as value.
    Basically, keys and values are tab-delimited and they are the main
    inputs and outputs to the member functions respectively.
    """

    def __init__(self):
	self.__set_data("", "", initialize = True)
	self.verbose = False

    # Only the following 2 functions performs the direct manipulation of "h".
    def __set_data(self, key, val, initialize = False):
	if initialize:
	    self.__h = {}
	else:
	    self.__h[ key ] = val

    def __push_data(self, key, val): # Redundancies of "val"s are allowed.
	if self.has_key(key):
	    self.__h[ key ].append(val)
	else:
	    self.__h[ key ] = [ val ]

    ### Only the following 5 functions look "h" directly. ###
    def val(self, *keylist):
	""" Each element can be tab-delimited before
	calling this function, i.e., self.val("a", "b") and
	self.val("a\tb") produce the same outputs """
        key = string.join(keylist, "\t")
	return self.__h[ key ]

    def val_force(self, *keylist):
	""" Each element can be tab-delimited before
	calling this function, i.e., self.val("a", "b") and
	self.val("a\tb") produce the same outputs """
        key = string.join(keylist, "\t")
        return self.__h.get(key, [])

    def keys(self):
        return self.__h.keys()

    def has_key(self, *keylist):
	""" Each element can be tab-delimited before
	calling this function, i.e., self.val("a", "b") and
	self.val("a\tb") produce the same outputs """
        key = string.join(keylist, "\t")
        return self.__h.has_key(key)

    def all_data(self):
	return self.__h

    #########################################################

    def __getitem__(self, key):
        return self.val(key)

    def val_list(self, *key_list):
        key = string.join(key_list, "\t")
	return self.val(key).split("\t")

    def vals(self, *keys):
	values = []
	for k in keys:
	    values.append(self.val(k))
	return values

    def has_pair(self, key1, key2):
	if self.has_key(key1, key2):
	    return key1 + "\t" + key2
	elif self.has_key(key2, key1):
	    return key2 + "\t" + key1
	else:
	    return False

    def pair_val(self, key1, key2):
	if self.has_key(key1, key2):
	    return self.val(key1, key2)
	elif self.has_key(key2, key1):
	    return self.val(key2, key1)
	else:
	    return []

    def filt_key(self, *filters):
        """ Gets keys having certain key patterns """
        filt_keys = []
        for key in self.keys():
            hit = 1
            for filter in filters:
                [ col, pat ] = filter
                r = key.split("\t")
                if col >= len(r) or r[col] <> pat:
                    hit = 0
                    break
            if hit == 1:
                filt_keys.append(key)
        return filt_keys

    def get_val_type(self):
	return self.val_type

    def verbose_mode(self):
	self.verbose = True

    def filt_line(self, r):
	return False

    def read_file(self, filename, Key_cols = [0], Val_cols = [1]):

        fh = open(filename, 'r')
	count = 0
        for line in fh.readlines():
            if line[0] == "#": continue
            linec = string.rstrip(line)
            r = linec.split("\t")
	    if self.filt_line(r): continue
	    if self.verbose: print "Reading line #" + `count`, linec
            keys = []
            for col in Key_cols: keys.append(r[col])
            key = string.join(keys, "\t")
	    vals = []
	    for col in Val_cols: vals.append(r[col])
	    val = string.join(vals, "\t")
            self.__push_data(key, val)
	    count = count + 1

        fh.close()

    def add_hash(self, hash):
	if self.get_val_type() <> hash.get_val_type():
	    raise "Type mismatch", (self.get_val_type(),
				     hash.get_val_type())
	count = 0
	for key in hash.keys():
	    self.__set_data(key, hash.val(key))
	    if self.verbose:
		print "Reading key #" + `count`, key, hash.val(key)
	    count = count + 1

    def reverse_Hash(self, hash):

	count = 0
	for key in hash.keys():
	    val = hash[key]
	    if self.verbose:
		print "Reading key #" + `count`, key, hash.val(key)
            self.__push_data(val, key)
	    count = count + 1


class Hash_headf_A(Hash_A):

    def header_key(self):
        return self.hd_key

    def header_val(self):
        return self.hd_val

    def val_accord_hd(self, key, val_term):
        """ self.header_val() = [ "Sho", "Chu", "Dai" ],
        Small --> "Medium\tLarge"
        Few   --> "Medium\tMany"
        Never --> "Often\tAlways"

        self.val_accord_hd("Few", "Dai") --> "Many"
        """

        col_val_hd = self.header_val().index(val_term)
        val = self.val_force(key)
        if val:
            ret = []
            for v in val:
                ret.append(v.split("\t")[ col_val_hd ])
            return ret
        else:
            return []

    def read_file(self, filename, Key_cols_hd, Val_cols_hd):

        self.hd_key = Key_cols_hd
        self.hd_val = Val_cols_hd

        fh = open(filename, 'r')

	header_line = fh.readline()
	header_c = string.rstrip(header_line)
	header = header_c.split("\t");

	Key_cols = []
	for Kch in Key_cols_hd:
	    Key_cols.append(header.index(Kch))
	Val_cols = []
	for Vch in Val_cols_hd:
	    Val_cols.append(header.index(Vch))

	count = 0
        for line in fh.readlines():
            if line[0] == "#": continue
            linec = line[:-1]
            # If string.rstrip(line), multiple blanks will be erased.
            r = linec.split("\t")
	    if self.filt_line(r): continue
	    if self.verbose: print "Reading line #" + `count`, linec
            keys = []
            for col in Key_cols: keys.append(r[col])
            key = string.join(keys, "\t")
	    vals = []
	    for col in Val_cols: vals.append(r[col])
	    val = string.join(vals, "\t")
            self._Hash_A__push_data(key, val)
	    count = count + 1

        fh.close()

class Hash_filt_A(Hash_A):

    def __init__(self):
        self.filts = []
	self.filts_OR_dict = {}
        Hash_A.__init__(self)

    def set_filt(self, *filts):
	self.filts = filts

    def set_filt_OR(self, *filts):
	for filt in filts:
	    [ col, filt_key ] = filt
	    if not (col in self.filts_OR_dict):
		self.filts_OR_dict[ col ] = {}
	    self.filts_OR_dict[ col ][ filt_key ] = True


    def filt_line(self, r):

        filter = False
	for filt in self.filts:
	    [ col, filt_key ] = filt
	    if col < len(r) and r[col] <> filt_key:
                """ If any one of the condition is NOT met,
                filter is activated. """
		filter = True
                break

        if not filter and self.filts_OR_dict != {}:
            filter = True
	    for col in range(0,len(r)):
		val = r[col]
		if (col in self.filts_OR_dict and
		    val in self.filts_OR_dict[col]):
                    """ If any one of the condition is MET,
                    filter is inactivated. """
                    filter = False
                    break

	return filter


if __name__ == "__main__":
    import TmpFile
    tmp_obj = TmpFile.TmpFile_III("""

Rintaro   Saito      SFC   male
Takayuki  Saito      SFC   male
Yuji      Kagayama   Law   male
Hiroshi   Mochizuki  Mer   male
Masaru    Mitsuhashi Law   male
Yoshihisa Wada       Eco   male
Sotaro    Yamazaki   Eco   male
Nobuo     Saito      SFC   male
Mayumi    Hasegawa   SFC   female
Miki      Matsumura  SFC   female
Ryosuke   Suzuki     SFC   male
Takashi   Suzuki     SFC   male
Dosan     Saito      His   male

""")

    print "Temporary file:"
    print tmp_obj.filename()

    print
    print "*** Hash Class ***"

    h_test = Hash_A()
#    h_test.verbose_mode()
    h_test.read_file(filename = tmp_obj.filename(),
		     Key_cols = [2,3],
		     Val_cols = [0,1])
    print "All data:"
    print h_test.all_data()
    print "Keys:"
    print h_test.keys()
    print h_test.has_key("SFC", "male")
    print h_test.has_pair("male", "SFC")
    print h_test.pair_val("male", "SFC")
    print h_test.filt_key([1, "male"])

    print
    print "*** Hash_filt Class ***"

    h_test2 = Hash_filt_A()
    h_test2.set_filt([2, "SFC"])
    h_test2.set_filt_OR([1, "Saito"], [1, "Suzuki"])
    h_test2.verbose_mode()
    h_test2.read_file(filename = tmp_obj.filename(),
                      Key_cols = [0],
                      Val_cols = [1])
    print h_test2.all_data()

    print
    print "*** Hash_headf Class ***"

    tmp_obj2 = TmpFile.TmpFile_III("""

Given     Family     Faculty Gender

Rintaro   Saito      SFC      male
Takayuki  Saito      SFC      male
Yuji      Kagayama   Law      male
Hiroshi   Mochizuki  Mer      male
Masaru    Mitsuhashi Law      male
Yoshihisa Wada       Eco      male
Sotaro    Yamazaki   Eco      male
Nobuo     Saito      SFC      male
Mayumi    Hasegawa   SFC      female
Miki      Matsumura  SFC      female
Ryosuke   Suzuki     SFC      male
Takashi   Suzuki     SFC      male
Dosan     Saito      His      male

""")

    hhf = Hash_headf_A()
    hhf.verbose_mode()
    hhf.read_file(filename = tmp_obj2.filename(),
                  Key_cols_hd = ["Faculty"],
                  Val_cols_hd = ["Given", "Family"])
    print hhf.keys()
    print hhf.val_accord_hd("Law", "Given")
    print hhf.val_accord_hd("Law", "Family")
