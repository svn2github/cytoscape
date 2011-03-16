#!/usr/bin/env python

""" This setting at the beginning is necessary to make
Entry widget functional. """

import os
os.putenv("LANG", "en_US.UTF-8")

""" *** """


""" Before the following imports (in anywhere, LANG enviroment
must be set. """

import Tkinter as Tk
import tkFont

import Data_Struct.Hash
from Usefuls.Usefuls2 import Revolver

class Frame(Tk.Frame):

    bait_label_offset = -60
    label_offset = -40

    def __init__(self, width = 1024, height = 768,
		 window_width = 512, window_height = 384,
		 master=None):
        Tk.Frame.__init__(self, master)
        self.master.title("rsIVV Viewer Version 1")
        self.master.geometry('+64+64')

        self.rs_width = width
        self.rs_height = height
	self.window_width = window_width
	self.window_height = window_height

        """ Defines three widgets """

        self.canvas = Tk.Canvas(self,
                                scrollregion=(0, 0,  width,  height),
                                width=window_width,
				height=window_height)

        xscroll = Tk.Scrollbar(self, orient=Tk.HORIZONTAL,
                               command=self.canvas.xview)
        yscroll = Tk.Scrollbar(self, orient=Tk.VERTICAL,
                               command=self.canvas.yview)

        subframe2 = Tk.Frame(self)
        label1 = Tk.Label(subframe2, text="ID:", justify=Tk.CENTER)
        self.entry = Tk.Entry(subframe2, width = 30)
        execButton = Tk.Button(subframe2, text="Search",
                               command = self.func1)
        prevButton = Tk.Button(subframe2, text="Prev",
                               command = self.func_prev)
        nextButton = Tk.Button(subframe2, text="Next",
                               command = self.func_next)

        subframe3 = Tk.Frame(self)
        self.scaler = Tk.Scale(subframe3, orient=Tk.HORIZONTAL,
                               length=200,from_=0,to=10,tickinterval=1)
        self.scaler.set(5)
        quitButton = Tk.Button(subframe3, text="Dismiss",
                               command = self.quit)


        """ Places and displays widgets """

        self.canvas.grid(row=0, column=0, sticky= Tk.N+Tk.E+Tk.W+Tk.S)
        xscroll.grid(row=1, column=0, sticky=Tk.E+Tk.W)
        yscroll.grid(row=0, column=1, sticky=Tk.N+Tk.S)

	subframe2.grid(row=2, column=0, sticky=Tk.N+Tk.E+Tk.W+Tk.S)
        label1.pack(side = Tk.LEFT)
        self.entry.pack(side = Tk.LEFT)
	execButton.pack(side = Tk.LEFT)
	prevButton.pack(side = Tk.RIGHT)
	nextButton.pack(side = Tk.RIGHT)

	subframe3.grid(row=3, column=0, sticky=Tk.N+Tk.E+Tk.W+Tk.S)
        self.scaler.pack(side = Tk.LEFT)
        quitButton.pack(side = Tk.RIGHT)


        self.canvas.config(xscrollcommand=xscroll.set,
                           yscrollcommand=yscroll.set)

        """ Enable manual scaling """
        self.grid_rowconfigure(0, weight=1, minsize=0)
        self.grid_columnconfigure(0, weight=1, minsize=0)

    def set_map_info(self, map_info):
        self.map_info = map_info

        self.map_color = { "RefSeq": "black",
                           "Protein": "green",
                           "Motif": "pink",
                           "Prey": "blue" }

        self.map_linew = { "RefSeq": 3,
                            "Protein": 4,
                            "Motif": 4,
                            "Prey": 2 }

        self.revolver = Revolver(map_info.keys())

    def clear(self):
        self.canvas.create_rectangle(
            0, 0, self.rs_width, self.rs_height,
            outline = '#FFEFD5',
            fill = '#FFEFD5')

    def draw_line(self, pos1, pos2, y_pos, width, color):
	self.canvas.create_line(self.scale_x(pos1),
				self.scale_y(y_pos),
				self.scale_x(pos2),
				self.scale_y(y_pos),
				width = width,
				fill  = color)

    def draw_line2(self, pos1, y_pos1, pos2, y_pos2, width, color):
        """ Two points are given to draw a line between them. """
	self.canvas.create_line(self.scale_x(pos1),
				self.scale_y(y_pos1),
				self.scale_x(pos2),
				self.scale_y(y_pos2),
				width = width,
				fill  = color)

    def draw_text(self, pos1, y_pos, text_pat, size):

	self.canvas.create_text(self.scale_x(pos1),
				self.scale_y(y_pos),
				text = text_pat,
				font = tkFont.Font(family = "Courier",
						   size = size))

    def graduate_sub(self, pos1, y_pos, num):
	self.draw_line2(pos1, y_pos-5, pos1, y_pos+5, 1, "black")
	self.draw_text(pos1, y_pos - 10, `num`, 14)


    def graduate(self, pos1, pos2, y_pos, step):
	c_pos = int(pos1)
	while c_pos < int(pos2):
	    if c_pos % step == 0:
		self.graduate_sub(c_pos, y_pos, c_pos)
	    c_pos += 1

    def draw_map(self, refseqid):

	self.clear()

        mapping = self.map_info.val(refseqid)

        y_pos = 30
        y_pos_plus = 30

        prev_type = False

        for m in mapping:
            seqtype, seqid, bait, pos1, pos2 = m.split("\t")

            if prev_type != "Motif" or seqtype != "Motif":
                y_pos += y_pos_plus


	    if seqtype == "RefSeq":
		self.graduate(pos1, pos2, y_pos, 50)

            line_color = self.map_color[ seqtype ]
            line_width = self.map_linew[ seqtype ]

            self.draw_line(pos1, pos2, y_pos, line_width, line_color)

            if seqtype != "Motif":
		self.draw_text(int(pos1) + Frame.label_offset, y_pos, seqid, 14)
            else:
		self.draw_text((int(pos1)+int(pos2))/2, y_pos, seqid, 14)

            if seqtype == "Prey":
		self.draw_text(Frame.bait_label_offset, y_pos, bait, 14)

            prev_type = seqtype

#            print seqtype, seqid, bait, pos1, pos2


    def scale_x(self, x):
        return int(int(x)*self.scaler.get()/5 + 150)

    def scale_y(self, y):
        return y

    def func1(self):
        refseqid = self.entry.get()
        if self.map_info.has_key(refseqid):
            self.draw_map(refseqid)


    def func_prev(self):
        refseqid = self.revolver.backward()
        self.draw_map(refseqid)

    def func_next(self):
        refseqid = self.revolver.forward()
        self.draw_map(refseqid)


if __name__ == '__main__':

    mapfile = "../TestFile/RefSeq_based_map2_bf.res"
    map_info = Data_Struct.Hash.Hash("A")
    map_info.read_file(mapfile, Key_cols = [0], Val_cols = [1,2,3,4,5])

    f = Frame(8192,512)
    f.set_map_info(map_info)
    f.pack(fill=Tk.BOTH, expand=1)

    f.draw_map("NM_021156")

    f.mainloop()

