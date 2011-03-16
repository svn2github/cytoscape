#!/usr/bin/env python

""" This setting at the beginning is necessary to make
Entry widget functional. """

import os
os.putenv("LANG", "en_US.UTF-8")

""" *** """


""" Before the following imports (in anywhere, LANG enviroment
must be set. """

import string
import Tkinter as Tk
import tkFont

from Data_Struct.Hash2 import Hash
from Data_Struct.Neighb_Red1 import Neighb_Red
from Usefuls.Usefuls_II import Revolver

class Halt: pass

class IVV_disp(Tk.Frame):

    bait_label_offset = -60
    label_offset = -50

    def __init__(self, width = 1024, height = 768,
		 window_width = 512, window_height = 384,
		 master=None):

	self.func_search = self.func_pass
	self.func_refr = self.func_pass
	self.func_prev = self.func_pass
	self.func_next = self.func_pass

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
                               command = self.func_search_transit)
        refrButton = Tk.Button(subframe2, text="Refr",
                               command = self.func_refr_transit)
        prevButton = Tk.Button(subframe2, text="Prev",
                               command = self.func_prev_transit)
        nextButton = Tk.Button(subframe2, text="Next",
                               command = self.func_next_transit)

        subframe3 = Tk.Frame(self)
        label2 = Tk.Label(subframe3, text="Scale:", justify=Tk.CENTER)
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
	nextButton.pack(side = Tk.RIGHT)
	prevButton.pack(side = Tk.RIGHT)
        refrButton.pack(side = Tk.RIGHT)

	subframe3.grid(row=3, column=0, sticky=Tk.N+Tk.E+Tk.W+Tk.S)
        label2.pack(side = Tk.LEFT)
        self.scaler.pack(side = Tk.LEFT)
        quitButton.pack(side = Tk.RIGHT)


        self.canvas.config(xscrollcommand=xscroll.set,
                           yscrollcommand=yscroll.set)

        """ Enable manual scaling """
        self.grid_rowconfigure(0, weight=1, minsize=0)
        self.grid_columnconfigure(0, weight=1, minsize=0)


    def clear(self):
        self.canvas.create_rectangle(
            0, 0, self.rs_width, self.rs_height,
            outline = '#FFEFD5',
            fill = '#FFEFD5')

    def scale_x(self, x):
        return int(int(x)*self.scaler.get()/5 + 150)

    def scale_y(self, y):
        return y

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

    def func_search_transit(self):
        input_id = self.entry.get()
	self.func_search(input_id)

    def func_refr_transit(self):
	self.func_refr()

    def func_prev_transit(self):
	self.func_prev()

    def func_next_transit(self):
	self.func_next()

    def set_func_search(self, func):
	self.func_search = func

    def set_func_refr(self, func):
	self.func_refr = func

    def set_func_prev(self, func):
	self.func_prev = func

    def set_func_next(self, func):
	self.func_next = func

    def func_pass(self):
	pass

class IVV_Info_for_Frame:
    def __init__(self, map_info):

	self.map_info = Hash("A")
	self.map_info.read_file(mapfile,
				Key_cols = [0],
				Val_cols = [1,2,3,4,5])

        self.map_color = { "RefSeq": "black",
                           "Protein": "green",
                           "Motif": "pink",
                           "Prey": "blue" }

        self.map_linew = { "RefSeq": 3,
                            "Protein": 4,
                            "Motif": 4,
                            "Prey": 2 }

        self.revolver = Revolver(self.map_info.keys())

	self.f = IVV_disp(8192,4096)
	self.f.pack(fill=Tk.BOTH, expand=1)
	self.f.set_func_search(self.func_search)
	self.f.set_func_refr(self.func_refr)
	self.f.set_func_prev(self.func_prev)
	self.f.set_func_next(self.func_next)
	self.draw_map(self.map_info.keys()[0])
	self.f.mainloop()

    def func_search(self, input_id_param):
        input_id = input_id_param.replace(" ", "")
        if self.map_info.has_key(input_id):
            refseqid = input_id
            self.draw_map(refseqid)
            self.revolver.search(refseqid)
        else:
            try:
                for refseqid in self.map_info.keys():
                    for id_info in self.map_info.val(refseqid):
                        id_type  = id_info.split("\t")[0]
                        id       = id_info.split("\t")[1]
                        if id_type == "Prey" and id == input_id:
                            self.draw_map(refseqid)
                            raise Halt
            except Halt:
                pass

	self.revolver.search(refseqid)

    def func_refr(self):
        refseqid = self.revolver.current()
        self.draw_map(refseqid)

    def func_prev(self):
        refseqid = self.revolver.backward()
        self.draw_map(refseqid)

    def func_next(self):
        refseqid = self.revolver.forward()
        self.draw_map(refseqid)

    def draw_map(self, refseqid):

	self.f.clear()

        mapping = self.map_info.val(refseqid)
        map_descr = Neighb_Red()
        for m in mapping:
            seqtype, seqid, pos1, pos2, cmnt = m.split("\t")
            map_descr.append(seqtype, (seqid, pos1, pos2, cmnt))

        y_pos = 60
        y_pos_plus = 30

        for md in map_descr.get_Single_KeyVals_set():
            seqtype, map_info = md.get_key(), md.get_vals()
            
            line_color = self.map_color[ seqtype ]
            line_width = self.map_linew[ seqtype ]
            
            if seqtype == "Motif":
                for m in map_info:
                    seqid, pos1, pos2, cmnt = m
                    self.f.draw_line(pos1, pos2, y_pos,
                                     line_width, line_color)                 
                    self.f.draw_text((int(pos1)+int(pos2))/2,
                                     y_pos, seqid, 14)
                y_pos += y_pos_plus

            else:
                for m in map_info:
                    seqid, pos1, pos2, cmnt = m
                    if seqtype == "RefSeq":
                        print seqid, pos1, pos2, cmnt
                        self.f.graduate(pos1, pos2, y_pos, 100)
                        cds_start, cds_end = (
                                   string.atoi(cmnt.split("-")[0]),
                                   string.atoi(cmnt.split("-")[1]))
                        self.f.draw_line(cds_start, cds_end, y_pos,
                                         line_width + 2, line_color)  
                                                
                    self.f.draw_line(pos1, pos2, y_pos,
                                     line_width, line_color)
                    self.f.draw_text(int(pos1) +
                                     IVV_disp.label_offset,
                                     y_pos, seqid, 14)
                    if seqtype == "Prey":
                        self.f.draw_text(IVV_disp.bait_label_offset,
                                         y_pos, cmnt, 14)
                    y_pos += y_pos_plus

            # print seqtype, seqid, bait, pos1, pos2
                  
              

if __name__ == '__main__':

    from Usefuls.rsConfig import RSC_II
    rsc = RSC_II("rsIVV_Config")

    mapfile = rsc.IVV_disp_map_GNP
    ivv_info_for_frame = IVV_Info_for_Frame(mapfile)

    """
    f = IVV_disp(8192,512)
    f.pack(fill=Tk.BOTH, expand=1)
    f.mainloop()
    """

