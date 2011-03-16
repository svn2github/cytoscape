#!/usr/bin/python

import sys
import Hash2

def keylist_to_tuple(keylist):
    ret = []
    for key in keylist:
        if type(key) is tuple:
            # keylist: (("a", "b")), key: ("a", "b") 
            ret += list(key)
        elif (type(key) is str or 
              type(key) is int or 
              type(key) is float or
              type(key) is long):
            # keylist: ("a", "b"), key: "a"
            ret += [ key ]
        else:
            raise "Key type error."

    return tuple(ret)

class Hash_II(Hash2.Hash):
    """ Reads tab-delimited file and make dictionary using items
    specified columns as key and another specified column as value.
    Basically, keys and values are in tuple format and they are the main
    inputs and outputs to the member functions respectively.
    """

    #########################################################
    def val(self, *keylist):
        """ Each element can be tab-delimited before
        calling this function, i.e., self.val("a", "b") and
        self.val(("a", "b")) produce the same outputs
        keylist : ("apple",) -> ("apple",)
        keylist : ("apple", "banana") -> ("apple", "banana")
        keylist : (("apple", "banana")) -> ("apple", "banana")
        """
        # print "Searching for", keylist, "in", self.all_data()
        return Hash2.Hash.val_raw(self, keylist_to_tuple(keylist))

    def val_force(self, *keylist):
        """ self.val("a", "b") and
        self.val(("a", b")) will produce the same outputs """
        return Hash2.Hash.val_raw_force(self, keylist_to_tuple(keylist))

    def has_key(self, *keylist):
        """ self.val("a", "b") and
        self.val(("a", "b")) produce the same outputs """
        return Hash2.Hash.has_key_raw(self, keylist_to_tuple(keylist))

    def keys_n(self, n):
        return map(lambda x: x[n], self.keys())

    def keys_s(self):
        return map(lambda x: x[0], self.keys())

    #########################################################
    

    def val_list(self, *key_list):
        return self.val(keylist_to_tuple(key_list))

    def has_pair(self, key1, key2):
        if self.has_key(key1, key2):
            return (key1, key2)
        elif self.has_key(key2, key1):
            return (key2, key1)
        else:
            return False

    def pair_val(self, key1, key2):
        if self.has_key(key1, key2):
            return self.val(key1, key2)
        elif self.has_key(key2, key1):
            return self.val(key2, key1)
        else:
            return False

    def filt_key(self, *filters):
        """ Gets keys having certain key patterns """
        filt_keys = []
        for key in self.keys():
            hit = 1
            for filter in filters:
                [ col, pat ] = filter
                r = key
                if col >= len(r) or r[col] <> pat:
                    hit = 0
                    break
            if hit == 1:
                filt_keys.append(key)
        return filt_keys


    def recorder(self, Key_cols, Val_cols, typeconv):
        if typeconv is None:
            typeconv = {}

        for line in self.fh.readlines():
            if line[0] == "#": continue
            linec = line[:len(line)-1]
            """ string.rstrip(line) may also cut last tabs """

            r = linec.split("\t")
            for col in typeconv:
                dest_type = typeconv[ col ]
                r[ col ] = dest_type(r[ col ])

            if self.filt_line(r): continue
            # print linec, r
            self.record(r, Key_cols, Val_cols)


    def record(self, r, Key_cols, Val_cols):

        # print r, Key_cols, Val_cols

        if self.verbose: print "Reading line ---", r
        keys = []      
        for col in Key_cols:
            if col < len(r):
                keys.append(self.conv_key(r[col]))
            elif self.miscolumn_permit:
                keys.append(self.conv_key(""))
            else:
                raise "Key column missing ..."
        key = tuple(keys)
        
        vals = []
        for col in Val_cols:
            if col < len(r):
                vals.append(self.conv_val(r[col]))
            elif self.miscolumn_permit:
                vals.append(self.conv_val(""))
            else:
                raise "Val column missing ..."     
        
        val = tuple(vals)
        if self.get_val_type() == "S": # Scalar
            self._set_data(key, val)
        elif self.get_val_type() == "A": # Array
            self._push_data(key, val)
        elif self.get_val_type() == "L": # Scalar, row as value
            self._set_data(key, tuple(r[Val_cols[0]:]))
        elif self.get_val_type() == "N": # Scalar, null
            self._set_data(key, "")
        else:
            raise "Illegal option", val_type


    def read_file(self, filename, Key_cols = [0], Val_cols = [1],
                  typeconv = None):

        self.fh = open(filename, 'r')

        self.pre_read_file()
        self.recorder(Key_cols, Val_cols,
                      typeconv)

        self.fh.close()
        self.fh = None


    def ret_reversed_Hash(self, ret_val_type):
        """ Only value types "S" and "A" are supported. """

        ret_hash = Hash_II(ret_val_type)
        for k in self.keys():
            v = self.val(k)
            if self.get_val_type() == "S":
                if ret_val_type == "S":
                    ret_hash._set_data(v, k)
                elif ret_val_type == "A":
                    ret_hash._push_data(v, k)
                else:
                    raise "Return value type error..."
                
            elif self.get_val_type() == "A":
                for ev in v:
                    if ret_val_type == "S":
                        ret_hash._set_data(ev, k)
                    elif ret_val_type == "A":
                        ret_hash._push_data(ev, k)
                    else:
                        raise "Return value type error..."
            else:
                raise "Source value type error..."

        return ret_hash

    #
    # From here, methods for headers are defined.
    #

    def val_accord_hd(self, key, val_term):
        """ self.header_val() = [ "Sho", "Chu", "Dai" ],
        Small --> "Medium\tLarge"
        Few   --> "Medium\tMany"
        Never --> "Often\tAlways"

        self.val_accord_hd("Few", "Dai") --> "Many"
        """

        col_val_hd = self.header_val().index(val_term)
        if type(key) is tuple:
            val = self.val_force(key)
        else:
            val = self.val_force((key,))

        if val:
            if self.get_val_type() == "S":
                return val[ col_val_hd ]
            elif self.get_val_type() == "A":
                ret = []
                for v in val:
                    ret.append(v[ col_val_hd ])
                return ret
            else:
                raise "Illegal type:", self.get_val_type()
        else:
            return None

    def read_file_hd(self, filename, Key_cols_hd, Val_cols_hd,
                     typeconv = None):

        self.hd_key = Key_cols_hd
        self.hd_val = Val_cols_hd

        self.fh = open(filename, 'r')
        self.pre_read_file()

        header_line = self.fh.readline()
        header_c = header_line.rstrip()
        header = header_c.split("\t");

        Key_cols = []
        for Kch in Key_cols_hd:
            Key_cols.append(header.index(Kch))
        Val_cols = []
        for Vch in Val_cols_hd:
            Val_cols.append(header.index(Vch))

        if typeconv is None:
            typeconv_ncol = None
        else:
            typeconv_ncol = {}
            for col_key in typeconv:
                col_n = header.index(col_key)
                typeconv_ncol[ col_n ] = typeconv[ col_key ]

        self.recorder(Key_cols, Val_cols, typeconv_ncol)

        self.fh.close()
        self.fh = None

    #
    # From here, methods for filters are defined.
    #


if __name__ == "__main__":
    import Usefuls.TmpFile
    tmp_obj = Usefuls.TmpFile.TmpFile_III("""

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

    h_test = Hash_II("S")
#    h_test.verbose_mode()
    h_test.read_file(filename = tmp_obj.filename(),
		     Key_cols = [0,1],
		     Val_cols = [2])
    print "All data:"
    print h_test.all_data()
    print "Keys:"
    print h_test.keys()
    print h_test.has_key("Rintaro", "Saito")
    print h_test.has_pair("Saito", "Rintaro")
    print h_test.pair_val("Rintaro", "Saito")
    print h_test.filt_key([1, "Saito"])
    print

    print "All data reversed."
    h_test_reverse = h_test.ret_reversed_Hash("S")
    print h_test_reverse.all_data()
    h_test_reverse = h_test.ret_reversed_Hash("A")
    print h_test_reverse.all_data()

    print "Stage 2:"
    h_test = Hash_II("A")
    h_test.read_file(filename = tmp_obj.filename(),
		     Key_cols = [1],
		     Val_cols = [2])
    print h_test.all_data()
    print "All data reversed."
    h_test_reverse = h_test.ret_reversed_Hash("S")
    print h_test_reverse.all_data()
    h_test_reverse = h_test.ret_reversed_Hash("A")
    print h_test_reverse.all_data()    

    print
    print "*** Hash_filt Class ***"

    h_test2 = Hash_II("S")
    h_test2.set_filt([2, "SFC"])
    h_test2.set_filt_OR([1, "Saito"], [1, "Suzuki"])
    h_test2.verbose_mode()
    h_test2.read_file(filename = tmp_obj.filename(),
                      Key_cols = [0],
                      Val_cols = [1])
    print h_test2.all_data()

    print
    print "*** Hash_headf Class ***"

    tmp_obj2 = Usefuls.TmpFile.TmpFile_III("""

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

    hhf = Hash_II("A")
    hhf.verbose_mode()
    hhf.read_file_hd(filename = tmp_obj2.filename(),
                     Key_cols_hd = ["Faculty"],
                     Val_cols_hd = ["Given", "Family"])
    print hhf.keys()
    print hhf.val_accord_hd("Law", "Given")
    print hhf.val_accord_hd("Law", "Family")
    print

    disney_resort = Usefuls.TmpFile.TmpFile_III("""
Given     Family     Place   Date
Satomi    Nakazawa   Land    1995.3
Tomoko    Yamagishi  Land    1998.4
Reiko     Tomita     Sea     2006.9
Akiko     Sato       Land    2007.12
""")
    disney_with = Hash_II("A")
    disney_with.read_file_hd(filename = disney_resort.filename(),
                             Key_cols_hd = [ "Place" ],
                             Val_cols_hd = [ "Given" ])
    print disney_with.val_accord_hd("Land", "Given")
    print


    niagara_curry = Usefuls.TmpFile.TmpFile_III("""
Given     Family     Date
Tomoko    Yamagishi  1998.8
Akiko     Sato       2006.1
Akiko     Shiozawa   2006.3
Ayako     Kazamaki   2008.1
""")
    niagara_with = Hash_II("A")
    niagara_with.read_file_hd(filename = niagara_curry.filename(),
                         Key_cols_hd = [ "Family" ],
                         Val_cols_hd = [ "Given" ])
    print niagara_with.val_accord_hd("Kazamaki", "Given")
    print

    class Hash_skip(Hash_II):
        def pre_read_file(self):
            while True:
                line = self.fh.readline()
                if line[0:4] != "Skip":
                    break
                print "Skipping ..."
            self.fh.seek(-len(line), 1)

    print "*** Hash_skip Class ***"

    tmp_obj3 = Usefuls.TmpFile.TmpFile_III("""

Skip #1
Skip #2
Skip #3
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

    hhf = Hash_skip("A")
    hhf.verbose_mode()
    hhf.read_file_hd(filename = tmp_obj3.filename(),
                     Key_cols_hd = ["Faculty"],
                     Val_cols_hd = ["Given", "Family"])
    print hhf.keys()
    print hhf.val_accord_hd("Law", "Given")
    print hhf.val_accord_hd("Law", "Family")

    print "*** Hash numerical conversion ***"

    tmp_obj4_ = Usefuls.TmpFile.TmpFile_III("""
Galant  240   1993
180SX   205   1992
Silvia  140   1994
""")
    car = Hash_II("S")
    car.read_file(filename = tmp_obj4_.filename(),
                  Key_cols = [ 0 ],
                  Val_cols = [ 1 ],
                  typeconv = { 1 : float,
                               2 : int })
    print car.val("Galant")


    tmp_obj4 = Usefuls.TmpFile.TmpFile_III("""
Car     Power  Year
Galant  240    1993
180SX   205    1992
Silvia  140    1994
""")
    car = Hash_II("S")
    car.read_file_hd(filename = tmp_obj4.filename(),
                     Key_cols_hd = [ "Car" ],
                     Val_cols_hd = [ "Power", "Year" ],
                     typeconv = { "Power": float,
                                  "Year" : int })
    print car.val_accord_hd("Galant", "Power")
    print car.val_accord_hd(("Galant",), "Year")
    print car.val_accord_hd("Galant", "Year")

                     
