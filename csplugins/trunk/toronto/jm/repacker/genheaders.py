#!/usr/bin/env python

import re
import sys
from zipfile import ZipFile

patterns = [
    re.compile('((.*)/)?lib(.+)[.](dylib|jnilib)'),
    re.compile('((.*)/)?lib(.+)[.]so'),
    re.compile('((.*)/)?(.+)[.]dll'),
]

all_processors = {
    'universal': ['x86', 'x86-64'],
    'i586': ['x86'],
    'amd64': ['x86-64'],
}

all_oses = {
    'macosx': 'mac os x',
    'windows': 'win32',
    'linux': 'linux',
}

def generate_headers(name, fragment_host):
    zip = ZipFile(name, 'r')
    platforms = {}
    for entry in zip.namelist():
        for pattern in patterns:
            matcher = pattern.match(entry)
            if matcher:
                platform = matcher.group(2)
                print platform, entry
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
    for platform, entries in platforms.items():
        os, processor_name = platform.split('-')
        os = all_oses[os]
        for processor in all_processors[processor_name]:
            platform_entries.append('%s;osname=%s;processor=%s' % (';'.join(entries), os, processor))
    
    properties = open('%s.properties' % name, 'w')
    print >> properties, 'Bundle-NativeCode=%s' % ','.join(platform_entries + ['*'])
    print >> properties, 'Fragment-Host=%s' % fragment_host
    properties.close()
    
if __name__ == '__main__':
    if len(sys.argv) > 2:
        name = sys.argv[1]
        fragment_host = sys.argv[2]    
        generate_headers(name, fragment_host)
