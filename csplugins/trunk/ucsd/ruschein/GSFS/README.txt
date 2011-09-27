GSFS is a mountable GenomeSpace file system based on FUSE (File system in USEr space).
The Mac OS implementation will run as a module under OSXFUSE, the successor to MacFUSE.
You can download OSXFUSE here: https://github.com/osxfuse/osxfuse/downloads
Ports that I installed included "file", "zlib", "fuse4x", "sshfs" and "pcre".

Various programs in here and libiViaCoreSubset use "ini" files that are located in
an "etc" directory.  I have hacked libiViaCoreSubset to look for these files in
~/etc/.  You need to create this directory and copy everything that is under etc here
into it.
