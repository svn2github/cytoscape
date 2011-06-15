#!/usr/bin/env python

import sys
import re
import os
import shutil

from os.path import walk, join

source_pattern = re.compile('.*-natives-(.+)[.]jar')

def process_natives(destination, directory, filenames):
    for filename in filenames:
        if filename.startswith('lib') or filename.endswith('.dll'):
            source = join(directory, filename)
            shutil.move(source, destination)

def extract_natives(source_file, root, destination):
    matcher = source_pattern.match(source_file)
    if not matcher:
        return
    
    platform = matcher.group(1)
    native_directory = join(destination, platform)
    try:
        os.makedirs(native_directory)
    except:
        pass
    walk(root, process_natives, native_directory)

if __name__ == '__main__':
    source_file = sys.argv[1]
    destination = sys.argv[2]
    root = '.'
    extract_natives(source_file, root, destination)