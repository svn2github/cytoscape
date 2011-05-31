#!/usr/bin/env python

import re
import sys
from zipfile import ZipFile

patterns = [
    [re.compile('lib(.+)[.](dylib|jnilib)'), 'mac os x'],
    [re.compile('lib(.+)[.]so'), 'linux'],
    [re.compile('lib(.+)[.]dll'), 'win32'],
]

def generate_headers(name, fragment_host):
    zip = ZipFile(name, 'r')
    processors = ['x86-64', 'x86']
    platforms = {}
    for entry in zip.namelist():
        for pattern, platform in patterns:
            matcher = pattern.match(entry)
            if matcher:
                if platform not in platforms:
                    entries = []
                    platforms[platform] = entries
                else:
                    entries = platforms[platform]
                entries.append(entry)
                break
    if len(platforms) == 0:
        return
    
    platform_entries = []
    for processor in processors:
        for platform, entries in platforms.items():
            platform_entries.append('%s;osname=%s;processor=%s' % (';'.join(entries), platform, processor))
    
    properties = open('%s.properties' % name, 'w')
    print >> properties, 'Bundle-NativeCode=%s' % ','.join(platform_entries + ['*'])
    print >> properties, 'Fragment-Host=%s' % fragment_host
    properties.close()
    
if __name__ == '__main__':
    if len(sys.argv) > 2:
        name = sys.argv[1]
        fragment_host = sys.argv[2]    
        generate_headers(name, fragment_host)
