#!/usr/bin/env python

def dv(x, y):
    if x == 0 and y == 0:
        return 0
    else:
        return 1.0 * x / y

class Hist:
    def __init__(self, lower, upper, n):
	self.lower = lower
	self.upper = upper
	self.n = n
	self.width = 1.0 * (upper - lower) / n
	self.count_under = 0
	self.count_over = 0
	self.count = []
	for i in range(n):
            self.count.append(0)
        self.name = "No Name"

    def add(self, x):
	if x < self.lower:
	    self.count_under += 1
	elif x >= self.upper:
	    self.count_over += 1
	else:
	    pos = int((x - self.lower) / self.width)
#	    print "Adding rank", pos
	    self.count[ pos ] += 1

    def within(self, pos):
	return self.count[pos]

    def under(self):
	return self.count_under
    
    def over(self):
	return self.count_over

    def total_samples(self):
	return sum([ self.count_under, ] + self.count +
		   [ self.count_over,  ])

    def display(self):
	print "Under_" + `self.lower` + "\t" + `self.under()`
	for i in range(self.n):
	    print `self.lower + self.width * i` +"\t" + `self.within(i)`
	print `self.upper` + "_or_Over\t" + `self.over()`

    def display_rate(self):
	tot = self.total_samples()
	print "Under_" + `self.lower` + "\t" + `1.0*self.under()/tot`
	for i in range(self.n):
	    print `self.lower + self.width * i` +"\t" + `1.0*self.within(i)/tot`
	print `self.upper` + "_or_Over\t" + `1.0*self.over()/tot`


    def get_display(self):
        """ "Under" and "Over" on X-axis will be a little bit isolated """
        ret = ""
        ret += `self.lower - self.width*2` + "\t" + `self.under()` + "\n"
	for i in range(self.n):
	    ret += `self.lower + self.width * i` +"\t" + `self.within(i)` + "\n"
	ret += `self.upper + self.width` + "\t" + `self.over()`
        return ret

    def get_display_rate(self):
        """ "Under" and "Over" on X-axis will be a little bit isolated """

	tot = self.total_samples()
        ret = ""
        ret += `self.lower - self.width*2` + "\t" + `1.0*self.under()/tot` + "\n"
	for i in range(self.n):
	    ret += `self.lower + self.width * i` +"\t" + `1.0*self.within(i)/tot` + "\n"
	ret += `self.upper + self.width` + "\t" + `1.0*self.over()/tot`
        return ret

    def set_name(self, name):
        self.name = name

    def get_name(self):
        return self.name


class Hists:
    def __init__(self, lower, upper, n):
        self.lower = lower
	self.upper = upper
	self.n = n
	self.width = 1.0 * (upper - lower) / n
        self.hists = {}
        self.hist_names = []

    def add_data(self, name, x):
        self.add_hist(name)
        self.hists[name].add(x)

    def add_hist(self, name):
        if name not in self.hists:
            self.hists[name] = Hist(self.lower, self.upper, self.n)
            self.hists[name].set_name(name)
            self.hist_names.append(name)
            return True
        else:
            return False

    def add_hists(self, *names):
        for name in names:
            self.add_hist(name)
    
    def get_display(self):
        """ "Under" and "Over" on X-axis will be a little bit isolated """

        header = [ "" ] + self.hist_names
        under  = [ self.lower - self.width*2 ]
        for name in self.hist_names:
            under.append(self.hists[name].under())
        within = []
        for i in range(self.n):
            within_each = [ self.lower + self.width * i ]
            for name in self.hist_names:
                within_each.append(self.hists[name].within(i))
            within.append(within_each)
        over = [ self.upper + self.width ]
        for name in self.hist_names:
            over.append(self.hists[name].over())

        return header, under, within, over

    def get_display_rate(self):
        """ "Under" and "Over" on X-axis will be a little bit isolated """

        header = [ "" ] + self.hist_names
        under  = [ self.lower - self.width*2 ]
        for name in self.hist_names:
            under.append(dv(self.hists[name].under(), self.hists[name].total_samples()))
        within = []
        for i in range(self.n):
            within_each = [ self.lower + self.width * i ]
            for name in self.hist_names:
                within_each.append(dv(self.hists[name].within(i), self.hists[name].total_samples()))
            within.append(within_each)
        over = [ self.upper + self.width ]
        for name in self.hist_names:
            over.append(dv(self.hists[name].over(), self.hists[name].total_samples()))

        return header, under, within, over


    def display(self, xaxismode = False):
        header, under, within, over = self.get_display()
        print "\t".join(header)
        if xaxismode:
            under_label = `under[0]`
        else:
            under_label = "Under_" + `self.lower`
        under_str  = [ under_label ] + map(lambda x:`x`, under[1:])
        print "\t".join(under_str)
        for each in within:
            each_str = map(lambda x:`x`, each)
            print "\t".join(each_str)
        if xaxismode:
            over_label = `over[0]`
        else:
            over_label = `self.upper` + "_or_over"
        over_str   = [ over_label ] + map(lambda x:`x`, over[1:])
        print "\t".join(over_str)

    def display_rate(self, xaxismode = False):
        header, under, within, over = self.get_display_rate()
        print "\t".join(header)
        if xaxismode:
            under_label = `under[0]`
        else:
            under_label = "Under_" + `self.lower`
        under_str  = [ under_label ] + map(lambda x:"%.3f" % x, under[1:])
        print "\t".join(under_str)
        for each in within:
            each_str = [`each[0]`] + map(lambda x:"%.3f" % x, each[1:])
            print "\t".join(each_str)
        if xaxismode:
            over_label = `over[0]`
        else:
            over_label = `self.upper` + "_or_over"
        over_str   = [ over_label ] + map(lambda x:"%.3f" % x, over[1:])
        print "\t".join(over_str)

if __name__ == "__main__":
    hs = Hist(20, 40, 4)
    hs.add(6)
    hs.add(12)
    hs.add(23)
    hs.add(24)
    hs.add(25)
    hs.add(25)
    hs.add(25)
    hs.add(35.9)
    hs.add(40)
    hs.add(200)
    hs.display()
    hs.display_rate()
    hs.set_name("Test")
    print hs.total_samples()
    print hs.get_name()
    print hs.get_display()
    print hs.get_display_rate()

    hss = Hists(20, 40, 4)
    hss.add_data("Test1", 15)
    hss.add_data("Test1", 15)
    hss.add_data("Test1", 20)
    hss.add_data("Test2", 25)
    hss.add_data("Test2", 15)
    hss.add_data("Test2", 15)
    hss.add_data("Test2", 15)
    hss.add_data("Test2", 37)
    hss.add_data("Test3", 35)
    hss.add_data("Test3", 37)
    hss.add_hist("TestX")
    hss.display()
    print
    hss.display_rate(True)
