#
#                                   Makefile
#
# To make the tutorial, issue the following command:
#
#    make tutorial
#

all: tutorial

tutorial: TUTORIAL.pdf

TUTORIAL.pdf: TUTORIAL.dvi
	dvipdf TUTORIAL.dvi

TUTORIAL.dvi: TUTORIAL.latex tank.eps
	latex TUTORIAL.latex

clean:
	rm -f TUTORIAL.aux
	rm -f TUTORIAL.dvi
	rm -f TUTORIAL.pdf
	rm -f TUTORIAL.log
