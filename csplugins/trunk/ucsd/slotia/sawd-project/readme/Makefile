all: readme.pdf

readme.pdf: readme.dvi
	dvipdf readme.dvi

readme.dvi: readme.latex
	latex readme.latex
	latex readme.latex

clean:
	rm -f readme.dvi
	rm -f readme.pdf
	rm -f readme.aux
	rm -f readme.log
	rm -f readme.toc

view: readme.pdf
	ggv readme.pdf
