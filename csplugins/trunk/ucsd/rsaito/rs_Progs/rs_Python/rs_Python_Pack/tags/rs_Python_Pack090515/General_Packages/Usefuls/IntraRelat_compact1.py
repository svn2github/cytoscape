#!/usr/bin/env python

import shelve
import TmpFile

NUM2KEY = "NUM2KEY"
KEY2NUM = "KEY2NUM"
RELAT   = "RELAT"

class IntraRelat_compact:

    def read_tab_file(self, filename, header_flag = True):
        self.key2num = {}
        self.num2key = {}
        self.relat = []
        self.count = 0

        fh = open(filename, "r")
        if header_flag:
            header = fh.readline()

        # line_count = 0
        for line_tmp in fh:
            line = line_tmp.rstrip()
            r = line.split("\t")
            item1, item2 = r[:2]
            if not self.key2num.has_key(item1):
                self.key2num[ item1 ] = self.count
                self.num2key[ self.count ] = item1
                self.count += 1
                # if self.count % 10000 == 0:
                    # print "Sequence:", self.count
            """
            line_count += 1
            if line_count % 10000 == 0:
                print "#1:", line_count
            """

        fh.seek(0, 0)
        if header_flag:
            header = fh.readline()

        # line_count = 0
        for line_tmp in fh:
            line = line_tmp.rstrip()
            r = line.split("\t")
            item1, item2 = r[:2]
            if not self.key2num.has_key(item2):
                self.key2num[ item2 ] = self.count
                self.num2key[ self.count ] = item2
                self.count += 1

            """
            line_count += 1
            if line_count % 10000 == 0:
                print "#2:", line_count
            """

        fh.seek(0, 0)
        if header_flag:
            header = fh.readline()

        num1_max = -1
        # line_count = 0
        for line_tmp in fh:
            line = line_tmp.rstrip()
            r = line.split("\t")
            item1, item2 = r[:2]
            val = self.treat_value(r[2])

            num1 = self.key2num[ item1 ]
            num2 = self.key2num[ item2 ]

            if num1 <= num1_max:
                self.relat[num1][num2] = val
                # print "Registering", item1, num1
            else:
                self.relat.append({ num2: val })
                num1_max += 1
                # print "Newly registering", item1, num1
                # print self.relat
                if num1 <> len(self.relat) - 1:
                    raise "Logical error..."
            """
            line_count += 1
            if line_count % 10000 == 0:
                print "#3:", line_count
            """

    def treat_value(self, value):
        return value

    def ret_value(self, item1, item2):
        if not self.key2num.has_key(item1):
            return False
        if not self.key2num.has_key(item2):
            return False
        num1 = self.key2num[ item1 ]
        num2 = self.key2num[ item2 ]

        if num1 >= len(self.relat):
            return False
        if not self.relat[num1].has_key(num2):
            return False
        return self.relat[num1][num2]

    def display(self):
        for num1 in range(len(self.relat)):
            item1 = self.num2key[ num1 ]
            num2_val = self.relat[num1]
            for num2 in num2_val:
                val = num2_val[num2]
                item2 = self.num2key[ num2 ]
                print item1, item2, val

    def save_shelve(self, shelveDB):

        d = shelve.open(shelveDB)
        d[ NUM2KEY ] = self.num2key
        d[ KEY2NUM ] = self.key2num
        d[ RELAT   ] = self.relat
        d.close()

    def load_shelve(self, shelveDB):

        d = shelve.open(shelveDB)
        self.num2key = d[ NUM2KEY ]
        self.key2num = d[ KEY2NUM ]
        self.relat   = d[ RELAT ]
        d.close()

if __name__ == "__main__":

    tmp_obj = TmpFile.TmpFile_III("""

Kazuya Yoshi    ??
Jack  Heihachi  NG
Heihachi Jack   OK
Kazuya Jack     OK
Jack   Kuni     OK
Kazuya Jun      OK
Alex   Jun      NG
Devil  Jun      OK
Devil  Kazuya   Son

""")

    irc = IntraRelat_compact()

    irc.read_tab_file(tmp_obj.filename(), False)
    print irc.key2num
    print irc.num2key
    print irc.count
    irc.save_shelve("TMPSHELVE")

    irc.load_shelve("TMPSHELVE")
    irc.display()
    print irc.ret_value("Devil", "Kazuya")
    print irc.ret_value("Devil", "Alex")
    print irc.ret_value("XXX", "XXX")
