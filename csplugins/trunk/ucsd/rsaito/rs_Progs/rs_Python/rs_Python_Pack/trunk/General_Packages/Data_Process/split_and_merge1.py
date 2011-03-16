#!/usr/bin/env python

class Split_and_Merge:
    def __init__(self, obj_to_split, param, mode):
        self.obj_to_split = obj_to_split
        self.param = param
        self.mode = mode
        self.init()
        self.main()
        self.finish()
    
    def init(self):
        pass
    
    def main(self):
        self.split()
        self.process()
        self.merge()
    
    def split(self):
        self.split_ok = []

    def process(self):
        self.split_processed = []
    
    def merge(self):
        self.merged = None

    def finish(self):
        pass


if __name__ == "__main___":
    SaM = Split_and_Merge(None, None, None)
    SaM.main()
    
