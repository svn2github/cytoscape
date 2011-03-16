#!/usr/bin/env python

class Hist:
    def __init__(self, lower, upper, n):
	self.lower = lower
	self.upper = upper
	self.n = n
	self.width = 1.0 * (upper - lower) / n
	self.count_under = 0
	self.count_over = 0
	self.count = []
	for i in range(n): self.count.append(0)
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
