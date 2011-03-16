#!/usr/bin/env python

""" This setting at the beginning is necessary to make
Entry widget functional. """

import os
os.putenv("LANG", "en_US.UTF-8")

""" *** """

import Tkinter as Tk
import tkFont
     
class Frame(Tk.Frame):
    def __init__(self, width = 768, height = 512, master=None):
        Tk.Frame.__init__(self, master)
        self.master.title("Scrolling Canvas")
        self.master.geometry('+64+64')
        
        """ Defines three widgets """

        subframe1 = Tk.Frame(self)
        self.canvas = Tk.Canvas(subframe1,
                                scrollregion=(0, 0,  width,  height),
                                width=384, height=256)
        xscroll = Tk.Scrollbar(subframe1, orient=Tk.HORIZONTAL,
                               command=self.canvas.xview)
        yscroll = Tk.Scrollbar(subframe1, orient=Tk.VERTICAL,
                               command=self.canvas.yview)
        
        subframe2 = Tk.Frame(self)
        self.entry = Tk.Entry(subframe2, width = 30)
        execButton = Tk.Button(subframe2, text="Execute",
                               command = self.func1)
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
        quitButton.pack(side = Tk.RIGHT)
        
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


    def func1(self):
        print self.entry.get()
        

if __name__ == '__main__':
    f = Frame()
    f.pack(fill=Tk.BOTH, expand=1)
    f.draw_test()
    f.mainloop()   
