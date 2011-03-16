#!/usr/bin/env python

import socket
import datetime
import os

from General_Packages.Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsPack_Config")

logfile  = rsc.RSPP_LOGFILE
hostname = socket.gethostname()

headkey  = "rspp_"

def getNextIssue():
    if (not os.path.exists(logfile) or
        os.path.getsize(logfile) == 0):
        return 0
    
    fh = open(logfile, "r")
    header = fh.readline()
    
    max_issue = -1
    for line in fh: 
        tstamp_f, host_f, issue_f, log_f = line.split("\t")
        if host_f == hostname and int(issue_f) > max_issue:
            max_issue = int(issue_f)
        
    return max_issue + 1


def stamp(log):
    if (not os.path.exists(logfile) or
        os.path.getsize(logfile) == 0):
        fh = open(logfile, "w")
        fh.write("\t".join([ "Time Stamp",
                             "Hostname",
                             "Issue",
                             "Log"
                            ]) + "\n")
        fh.close()
        
    fh = open(logfile, "a")
    curr     = datetime.datetime.today()
    t_stamp = curr.strftime("%Y-%m-%d %H:%M:%S")
    issue = getNextIssue()
    fh.write("\t".join([ t_stamp,
                         hostname,
                         str(issue),
                         log
                        ]) + "\n")
    fh.close()
    return headkey + hostname + str(issue)
        
if __name__ == "__main__":
    import sys
    if len(sys.argv) <= 1:
        print stamp("This is a test.")
    else:
        print stamp(sys.argv[1])