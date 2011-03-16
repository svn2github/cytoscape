#!/usr/bin/env python

class MultiCol_Output:
    def __init__(self, join_char = "\t", null_char = ""):
        self.multicol_lines = []
        self.current_col = 0
        self.current_row = 0
        self.join_char = join_char
        self.null_char = null_char

    def next_col(self):
        self.current_row = 0
        self.current_col += 1

    def append(self, line):

        if self.current_row == len(self.multicol_lines):
            self.multicol_lines.append([])
        current_line = self.multicol_lines[ self.current_row ]

        i = len(current_line)
        while i < self.current_col:
            current_line.append(self.null_char)
            i += 1
        current_line.append(line)
        self.current_row += 1

    def output(self, rectangle_mode = False):
        max_len = 0
        for line in self.multicol_lines:
            if max_len < len(line):
                max_len = len(line)

        for line in self.multicol_lines:
            if rectangle_mode:
                additional = [ self.null_char ] * (max_len - len(line))
            else:
                additional = []

            print self.join_char.join(line + additional)


if __name__ == "__main__":
    mco = MultiCol_Output()
    mco.append("Yobi")
    mco.append("Getsu")
    mco.append("Ka")
    mco.next_col()
    mco.append("Week")
    mco.append("Mon")
    mco.append("Tue")
    mco.append("Wed")
    mco.append("Thr")
    mco.next_col()
    mco.append("Franc")
    mco.append("Lundi")
    mco.append("Mardi")
    mco.output(True)
    
