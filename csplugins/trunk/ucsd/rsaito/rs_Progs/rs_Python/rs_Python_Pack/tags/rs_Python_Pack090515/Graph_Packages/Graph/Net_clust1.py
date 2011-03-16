#!/usr/bin/env python

import string

class Net_Clust:
    def __init__(self):

        self.belong = {}
        self.member = {}
        self.counter = 0

    def incorp_pair(self, item1, item2):
        
        if not item1 in self.belong:
            self.belong[ item1 ] = self.counter
            self.member[ self.counter ] = [ item1 ]
            self.counter += 1

        if not item2 in self.belong:
            self.belong[ item2 ] = self.counter
            self.member[ self.counter ] = [ item2 ]
            self.counter += 1
            
        cluster1 = self.belong[ item1 ]
        cluster2 = self.belong[ item2 ]

        member1 = self.member[ cluster1 ]
        member2 = self.member[ cluster2 ]

        if member1 != member2:
            if cluster1 < cluster2:
                member1 += member2
                for item in member2:
                    self.belong[ item ] = cluster1
                del self.member[ cluster2 ]
            else:
                member2 += member1
                for item in member1:
                    self.belong[ item ] = cluster2
                del self.member[ cluster1 ]
                

    def display_clust_info(self):

        for cluster in self.member:
            print "Cluster #" + `cluster`
            print string.join(self.member[ cluster ], ",")
            print
        print

        for item in self.belong:
            print item, "belongs to cluster #" + `self.belong[ item ]`
        print

    def get_members(self):
        return self.belong.keys()

    def get_cluster(self, item):
        return self.belong[ item ]


if __name__ == "__main__":
    import sys
    
    clust = Net_Clust()
    """
    clust.incorp_pair("A", "B")
    clust.incorp_pair("C", "D")
    clust.incorp_pair("H", "I")
    clust.incorp_pair("J", "K")
    clust.incorp_pair("J", "J")
    clust.incorp_pair("J", "L")
    clust.incorp_pair("B", "C")
    clust.incorp_pair("D", "E")
    clust.incorp_pair("F", "A")
    clust.display_clust_info()

    """
    filename   = sys.argv[1]
    filename_w = sys.argv[2]

    fh = open(filename, "r")
    header = fh.readline()

    line_count = 0
    for line in fh:
        r = line.split("\t")
        item1, item2 = r[:2]
        # print item1, item2
        clust.incorp_pair(item1, item2)
        line_count += 1
        if line_count % 10000 == 0:
            fh_w = open(filename_w, "w")
            fh_w.write("# Reading line #" + `line_count` + "\n")
            for member in clust.get_members():
                fh_w.write(
                    string.join((member, `clust.get_cluster(member)`), "\t")
                    + "\n")
            fh_w.close()

    fh.close()

    fh_w = open(filename_w, "w")
    fw_w.write("# Reading line #" + `line_count` + "\n")
    for member in clust.get_members():
        fh_w.write(
            string.join((member, `clust.get_cluster(member)`), "\t")
            + "\n")
    fh_w.close()

