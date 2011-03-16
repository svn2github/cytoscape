#!/usr/bin/env python

from optparse import OptionParser
from Analyze_Sheet import Analyze_Sheet
from Usefuls.rsConfig import RSC_II
    
usage = "Usage: %prog options"
parser = OptionParser(usage)
parser.add_option(
    "-s", "--sheet", dest = "sheet_t", # dest = "sheet"
    help = "Answer sheet file") 
parser.add_option(
    "-l", "--students", dest = "students",
    help = "Students list file") 
parser.add_option(
    "-a", "--correctA", dest = "correctA",
    help = "Correct answers file A")
parser.add_option(
    "-b", "--correctB", dest = "correctB",
    help = "Correct answers file B")
parser.add_option(
    "-e", "--extra", dest = "extra",
    help = "Extra information file")

parser.add_option("-c", "--autoconfigno",
                  dest = "autoconfigno",
                  help = "Automatic configuration number")

options, args = parser.parse_args()

if options.autoconfigno == "1":
    options = RSC_II("rsLec_MCB_Config1")
    # Remember that options are not OptionParser object any more.
    if 'extra' not in vars(options):
        options.extra = None
elif options.autoconfigno == "2":
    options = RSC_II("rsLec_MCB_Config2")
    # Remember that options are not OptionParser object any more.
    if 'extra' not in vars(options):
        options.extra = None
else:
    if not options.sheet_t: # options.sheet
        parser.error("Answer sheet file not specified.")
    if not options.students:
        parser.error("Students list file not specified.")
    if not options.correctA:
        parser.error("Correct answer file A not specified.")
    if not options.correctB:
        parser.error("Correct answer file B not specified.")

analyze_sheet = Analyze_Sheet(options.sheet_t, # options.sheet
                              options.correctA,
                              options.correctB,
                              options.students,
                              options.extra)

analyze_sheet.output()


