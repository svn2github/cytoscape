#!/usr/bin/env python

import Tkinter as Tk
     
class Frame(Tk.Frame):
    def __init__(self, width = 768, height = 512, master=None):
        Tk.Frame.__init__(self, master)
        self.master.title("Scrolling Canvas")

        """ Defines three widgets """
        self.canvas = Tk.Canvas(self,
                                scrollregion=(0, 0,  width,  height),
                                width=384, height=256)
        xscroll = Tk.Scrollbar(self, orient=Tk.HORIZONTAL,
                               command=self.canvas.xview)
        yscroll = Tk.Scrollbar(self, orient=Tk.VERTICAL,
                               command=self.canvas.yview)

        """ Places and displays widgets """
        self.canvas.grid(row=0, column=0, sticky= Tk.N+Tk.E+Tk.W+Tk.S)
        xscroll.grid(row=1, column=0, sticky=Tk.E+Tk.W)
        yscroll.grid(row=0, column=1, sticky=Tk.N+Tk.S)

        self.canvas.config(xscrollcommand=xscroll.set,
                        yscrollcommand=yscroll.set)

        """ Enable manual scaling """
        self.grid_rowconfigure(0, weight=1, minsize=0)     
        self.grid_columnconfigure(0, weight=1, minsize=0)

    def draw_test(self):
        self.canvas.create_oval(100, 200, 
                                700, 300,
                                outline = "red", width = 5)


if __name__ == '__main__':
    f = Frame()
    f.pack(fill=Tk.BOTH, expand=1)
    f.draw_test()
    f.mainloop()   
