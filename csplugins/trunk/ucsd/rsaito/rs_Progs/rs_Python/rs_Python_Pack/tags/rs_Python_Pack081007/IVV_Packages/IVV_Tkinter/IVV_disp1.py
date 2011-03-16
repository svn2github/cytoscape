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

class Frame(Tk.Frame):
    def __init__(self, width = 1024, height = 768, master=None):
        Tk.Frame.__init__(self, master)
        self.master.title("rsIVV Viewer Version 1")
        self.master.geometry('+64+64')

        self.rs_width = width
        self.rs_height = height


        """ Defines three widgets """

        subframe1 = Tk.Frame(self)
        self.canvas = Tk.Canvas(subframe1,
                                scrollregion=(0, 0,  width,  height),
                                width=512, height=384)
        xscroll = Tk.Scrollbar(subframe1, orient=Tk.HORIZONTAL,
                               command=self.canvas.xview)
        yscroll = Tk.Scrollbar(subframe1, orient=Tk.VERTICAL,
                               command=self.canvas.yview)

        subframe2 = Tk.Frame(self)
        self.entry = Tk.Entry(subframe2, width = 30)
        execButton = Tk.Button(subframe2, text="Execute",
                               command = self.func1)

        prevButton = Tk.Button(subframe2, text="Prev",
                               command = self.func_prev)
        nextButton = Tk.Button(subframe2, text="Next",
                               command = self.func_next)

        quitButton = Tk.Button(subframe2, text="Dismiss",
                               command = self.quit)


        """ Places and displays widgets """
        subframe1.grid(row=0, column=0, sticky=Tk.N+Tk.E+Tk.W+Tk.S)
        self.canvas.grid(row=0, column=0, sticky= Tk.N+Tk.E+Tk.W+Tk.S)
        xscroll.grid(row=1, column=0, sticky=Tk.E+Tk.W)
        yscroll.grid(row=0, column=1, sticky=Tk.N+Tk.S)

        subframe2.grid(row=2, column=0, sticky=Tk.N+Tk.E+Tk.W+Tk.S)
        self.entry.pack(side = Tk.LEFT)
        execButton.pack(side = Tk.LEFT)
        prevButton.pack(side = Tk.LEFT)
        nextButton.pack(side = Tk.LEFT)
        quitButton.pack(side = Tk.LEFT)

        self.canvas.config(xscrollcommand=xscroll.set,
                           yscrollcommand=yscroll.set)

        """ Enable manual scaling """
        self.grid_rowconfigure(0, weight=1, minsize=0)
        self.grid_columnconfigure(0, weight=1, minsize=0)
        subframe1.grid_rowconfigure(0, weight=1, minsize=0)
        subframe1.grid_columnconfigure(0, weight=1, minsize=0)

    def draw_test(self):
        self.canvas.create_oval(100, 200,
                                600, 400,
                                outline = "red", width = 5)

        self.canvas.create_line(100,200,300,400,
                                width = 5,
                                arrow = Tk.LAST,
                                fill = "Green")
        self.canvas.create_text(100, 200, text = "Hello",
                                font = tkFont.Font(family = "Times",
                                                   size = 24))
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

        self.refseqids = map_info.keys()
        self.refseqid_pointer = 0

    def draw_map(self, refseqid):

        self.canvas.create_rectangle(
            0, 0, self.rs_width, self.rs_height,
            outline = '#FFEFD5',
            fill = '#FFEFD5')

        mapping = self.map_info.val(refseqid)

        y_pos = 30
        y_pos_plus = 30
        label_offset = 50

        prev_type = False

        for m in mapping:
            seqtype, seqid, bait, pos1, pos2 = m.split("\t")

            if prev_type != "Motif" or seqtype != "Motif":
                y_pos += y_pos_plus

            line_color = self.map_color[ seqtype ]
            line_width = self.map_linew[ seqtype ]

            self.canvas.create_line(self.scale_x(pos1),
                                    self.scale_y(y_pos),
                                    self.scale_x(pos2),
                                    self.scale_y(y_pos),
                                    width = line_width,
                                    fill = line_color)

            if seqtype != "Motif":
                self.canvas.create_text(self.scale_x(int(pos1) - 50),
                                        self.scale_y(y_pos),
                                        text = seqid,
                                        font = tkFont.Font(family = "Times",
                                                           size = 14))

            else:
                self.canvas.create_text(self.scale_x((int(pos1)+int(pos2))/2),
                                        self.scale_y(y_pos),
                                        text = seqid,
                                        font = tkFont.Font(family = "Times",
                                                           size = 14))

            if seqtype == "Prey":
                self.canvas.create_text(self.scale_x(-100),
                                        self.scale_y(y_pos),
                                        text = bait,
                                        font = tkFont.Font(family = "Times",
                                                           size = 14))


            prev_type = seqtype

#            print seqtype, seqid, bait, pos1, pos2


    def scale_x(self, x):
        return int(x) + 150

    def scale_y(self, y):
        return y

    def func1(self):
        refseqid = self.entry.get()
        if self.map_info.has_key(refseqid):
            self.draw_map(refseqid)


    def func_prev(self):
        self.refseqid_pointer -= 1
        if self.refseqid_pointer < 0:
            self.refseqid_pointer = len(self.refseqids) - 1
        self.draw_map(self.refseqids[ self.refseqid_pointer ])

    def func_next(self):
        self.refseqid_pointer += 1
        if self.refseqid_pointer >= len(self.refseqids):
            self.refseqid_pointer = 0
        self.draw_map(self.refseqids[ self.refseqid_pointer ])


if __name__ == '__main__':

    mapfile = "../TestFile/RefSeq_based_map2_bf.res"
    map_info = Data_Struct.Hash.Hash("A")
    map_info.read_file(mapfile, Key_cols = [0], Val_cols = [1,2,3,4,5])

    f = Frame(8192,512)
    f.set_map_info(map_info)
    f.pack(fill=Tk.BOTH, expand=1)

    f.draw_map("NM_021156")

    f.mainloop()

