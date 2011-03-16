#!/usr/bin/env python

""" This system is a part of PUNI, Potentially Universal and Narcissistic Infrastructure. """

from os import system, path
import pickle
from datetime import datetime

from Data_Struct.Hash2 import Hash
from random import randrange
from Usefuls.rsConfig import RSC_II
from Usefuls.TmpFile import TmpFile_II
from Usefuls.Replace_by_dict import replace_by_dict
from WEB.CGI_BasicI import cgi_html_out


def upper_first_char(s):
    return s[0].upper() + s[1:]

rsc = RSC_II("rsLec_MCB_Sys_Config")

hDB = Hash("S")
hDB.set_miscolumn_permit()
hDB.read_file_hd(filename = rsc.BioBasic3_DB,
                 Key_cols_hd = ["Student ID"],
                 Val_cols_hd = ["Department",
                                "Grade",
                                "Account",
                                "E-Mail",
                                "Family Name",
                                "Given Name",
                                "Score 1A",
                                "Score 1B",
                                "Score 1A Rank",
                                "Score 1B Rank",
                                "DS 1A",
                                "DS 1B",
                                "DS 1A Mod",
                                "DS 1B Mod",
                                "Score 2A",
                                "Score 2B",
                                "Score 2A Rank",
                                "Score 2B Rank",
                                "DS 2A",
                                "DS 2B",
                                "DS 2A Mod",
                                "DS 2B Mod",
                                "Validity 1A",
                                "Validity 1B",
                                "Validity 2A",
                                "Validity 2B",
                                "Presentation Score A",
                                "Presentation Score B",
                                "Presentation Days A",
                                "Presentation Days B",
                                "DS Total"
                                ])

ACCESS_LIMIT = 5

m_replace_SID   = "[[SID]]"
m_replace_NAME  = "[[Name]]"
m_replace_Mail  = "[[Mail]]"
m_replace_NACC  = "[[NACC]]"
m_replace_RNDM  = "[[RNDM]]"
m_replace_1A    = "[[1A]]"
m_replace_1ARNK = "[[1ARNK]]"
m_replace_1ADS  = "[[1ADS]]"
m_replace_1ADSM = "[[1ADSM]]"
m_replace_1B    = "[[1B]]"
m_replace_1BRNK = "[[1BRNK]]"
m_replace_1BDS  = "[[1BDS]]"
m_replace_1BDSM = "[[1BDSM]]"
m_replace_2A    = "[[2A]]"
m_replace_2ARNK = "[[2ARNK]]"
m_replace_2ADS  = "[[2ADS]]"
m_replace_2ADSM = "[[2ADSM]]"
m_replace_2B    = "[[2B]]"
m_replace_2BRNK = "[[2BRNK]]"
m_replace_2BDS  = "[[2BDS]]"
m_replace_2BDSM = "[[2BDSM]]"
m_replace_GNAM  = "[[GName]]"
m_replace_VLD1A = "[[VLD1A]]"
m_replace_VLD1B = "[[VLD1B]]"
m_replace_VLD2A = "[[VLD2A]]"
m_replace_VLD2B = "[[VLD2B]]"
m_replace_PRSTA = "[[PRSTA]]"
m_replace_PRSTB = "[[PRSTB]]"
m_replace_DSTOT = "[[DSTOT]]"

rd_messages = open(rsc.BioBasic3_rnd_m, "r").readlines()

MAIL_Command = "mail"
MAIL_Subject = "MCB3-2010: Result of your exam (Ver. 20101209)"
MAIL_From    = "rsaito@sfc.keio.ac.jp"

def set_MAIL_Command(mail_command):
    global MAIL_Command
    MAIL_Command = mail_command


def auto_email(input_ID, send_email = True):

    name = "(Unknown)"
    access_time = datetime.now().strftime("%Y/%m/%d %H:%M:%S")

    alog = {}
    if path.isfile(rsc.BioBasic3_Log):
        alog = pickle.load(open(rsc.BioBasic3_Log, "r"))
        
    if alog.get(input_ID, 0) >= ACCESS_LIMIT:

        print cgi_html_out(open(rsc.BioBasic3_html_ACLM, "r").read())

    elif hDB.has_key(input_ID):
        name     = upper_first_char(hDB.val_accord_hd(input_ID, "Given Name")) + ' ' + upper_first_char(hDB.val_accord_hd(input_ID, "Family Name"))
        email    = hDB.val_accord_hd(input_ID, "E-Mail")

        if hDB.val_accord_hd(input_ID, "Score 1A"):
            test1a    = "%2d"  % int(hDB.val_accord_hd(input_ID, "Score 1A"))
            test1arnk = hDB.val_accord_hd(input_ID, "Score 1A Rank")
            test1ads  = "%.1f" % float(hDB.val_accord_hd(input_ID, "DS 1A"))
            # test1adsm = "%.1f" % float(hDB.val_accord_hd(input_ID, "DS 1A Mod"))
            test1adsm = hDB.val_accord_hd(input_ID, "DS 1A Mod")
        else:
            test1a    = ""
            test1arnk = ""
            test1ads  = ""
            test1adsm = ""
            
        if hDB.val_accord_hd(input_ID, "Score 1B"):
            test1b    = "%2d"  % int(hDB.val_accord_hd(input_ID, "Score 1B"))
            test1brnk = hDB.val_accord_hd(input_ID, "Score 1B Rank")
            test1bds  = "%.1f" % float(hDB.val_accord_hd(input_ID, "DS 1B"))
            # test1bdsm = "%.1f" % float(hDB.val_accord_hd(input_ID, "DS 1B Mod"))
            test1bdsm = hDB.val_accord_hd(input_ID, "DS 1B Mod")
        else:
            test1b    = ""
            test1brnk = ""
            test1bds  = "" 
            test1bdsm = ""

        if hDB.val_accord_hd(input_ID, "Score 2A"):
            test2a    = "%2d"  % int(hDB.val_accord_hd(input_ID, "Score 2A"))
            test2arnk = hDB.val_accord_hd(input_ID, "Score 2A Rank")
            test2ads  = "%.1f" % float(hDB.val_accord_hd(input_ID, "DS 2A"))
            # test2adsm = "%.1f" % float(hDB.val_accord_hd(input_ID, "DS 2A Mod"))
            test2adsm = hDB.val_accord_hd(input_ID, "DS 2A Mod")
        else:
            test2a    = ""
            test2arnk = ""
            test2ads  = ""
            test2adsm = ""

        if hDB.val_accord_hd(input_ID, "Score 2B"):
            test2b    = "%2d"  % int(hDB.val_accord_hd(input_ID, "Score 2B"))
            test2brnk = hDB.val_accord_hd(input_ID, "Score 2B Rank")
            test2bds  = "%.1f" % float(hDB.val_accord_hd(input_ID, "DS 2B"))
            # test2bdsm = "%.1f" % float(hDB.val_accord_hd(input_ID, "DS 2B Mod"))
            test2bdsm = hDB.val_accord_hd(input_ID, "DS 2B Mod")
        else:
            test2b    = ""
            test2brnk = ""
            test2bds  = ""
            test2bdsm = "" 

        if hDB.val_accord_hd(input_ID, "Validity 1A") == "Invalid":
            vld1A = "Invalid"
        elif test1adsm == "":
            vld1A = ""
        else:
            vld1A = "OK"
        if hDB.val_accord_hd(input_ID, "Validity 1B") == "Invalid":
            vld1B = "Invalid"
        elif test1bdsm == "":
            vld1B = ""
        else:
            vld1B = "OK"            
        if hDB.val_accord_hd(input_ID, "Validity 2A") == "Invalid":
            vld2A = "Invalid"
        elif test2adsm == "":
            vld2A = ""
        else:
            vld2A = "OK"
        if hDB.val_accord_hd(input_ID, "Validity 2B") == "Invalid":
            vld2B = "Invalid"
        elif test2bdsm == "":
            vld2B = ""
        else:
            vld2B = "OK"    

        if hDB.val_accord_hd(input_ID, "Presentation Days A"):
            presA = hDB.val_accord_hd(input_ID, "Presentation Days A")
        else:
            presA = "None"

        if hDB.val_accord_hd(input_ID, "Presentation Days B"):
            presB = hDB.val_accord_hd(input_ID, "Presentation Days B")
        else:
            presB = "None"

        if hDB.val_accord_hd(input_ID, "DS Total"):
            total = hDB.val_accord_hd(input_ID, "DS Total")
        else:
            total = ""

        m_replace = {
                     m_replace_SID   : input_ID, 
                     m_replace_NAME  : name,
                     m_replace_GNAM  : upper_first_char(hDB.val_accord_hd(input_ID, "Given Name")),
                     m_replace_Mail  : email,
                     m_replace_NACC  : " ".join(['*'] * (alog.get(input_ID, 0) + 1)),
                     m_replace_RNDM  : rd_messages[ randrange(0, len(rd_messages)) ].rstrip(),
                     m_replace_1A    : test1a,
                     m_replace_1ARNK : test1arnk,
                     m_replace_1ADS  : test1ads,
                     m_replace_1ADSM : test1adsm,
                     m_replace_1B    : test1b,
                     m_replace_1BRNK : test1brnk,
                     m_replace_1BDS  : test1bds,
                     m_replace_1BDSM : test1bdsm,
                     m_replace_2A    : test2a,
                     m_replace_2ARNK : test2arnk,
                     m_replace_2ADS  : test2ads,
                     m_replace_2ADSM : test2adsm,
                     m_replace_2B    : test2b,
                     m_replace_2BRNK : test2brnk,
                     m_replace_2BDS  : test2bds,
                     m_replace_2BDSM : test2bdsm,
                     m_replace_VLD1A : vld1A,
                     m_replace_VLD1B : vld1B,
                     m_replace_VLD2A : vld2A,
                     m_replace_VLD2B : vld2B,
                     m_replace_PRSTA : presA,                   
                     m_replace_PRSTB : presB,
                     m_replace_DSTOT : total
                    }
        
        message = open(rsc.BioBasic3_mail_IDOK).read()
        message = replace_by_dict(message, m_replace)
        
        tmp_obj = TmpFile_II(message)
        if send_email:
            system("%s -s '%s' %s -f %s < %s" %
                   (MAIL_Command,
                    MAIL_Subject,
                    email,
                    MAIL_From,
                    tmp_obj.filename()))
        else:
            system("cat " + tmp_obj.filename())
        html_out = open(rsc.BioBasic3_html_IDOK, "r").read()
        html_out = replace_by_dict(html_out, m_replace)
        print cgi_html_out(html_out)

    else:
        m_replace = {
             m_replace_SID  : input_ID 
        }
        html_out = open(rsc.BioBasic3_html_IDNG, "r").read()
        html_out = replace_by_dict(html_out, m_replace)
        print cgi_html_out(html_out)

    alog[input_ID] = alog.get(input_ID, 0) + 1
    pickle.dump(alog, open(rsc.BioBasic3_Log, "w"))
    open(rsc.BioBasic3_LogTime, "a").write("\t".join((access_time, input_ID, name)) + '\n')

if __name__ == "__main__":
    
    auto_email(input_ID = "79151932", send_email = False)
    

