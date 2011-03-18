%%%
%% 数理科学別冊用クラスファイル msse.cls for pLaTeX2e
%%  by Motoaki Sato (ULS and company), E-mail: teppei@uls.co.jp
\NeedsTeXFormat{pLaTeX2e}
\ProvidesClass{msse}
  [2001/05/25 v1.0 for 数理科学別冊]

\newdimen\@JQ
 \@JQ=0.7392507pt
\newdimen\@Q
 \@Q=0.71144pt
\newdimen\@EM
 \@EM=.03125em

\newif\if@Proof
%	\@Prooftrue
	\@Prooffalse
\newif\if@cameraready
%	\@camerareadytrue
	\@camerareadyfalse
\DeclareOption{Proof}{\@Prooftrue}
\DeclareOption{cameraready}{\@camerareadytrue}

\newcounter{@paper}
%% \newif\if@landscape \@landscapefalse
\newcommand{\@ptsize}{}
\newif\if@restonecol
%% \newif\if@titlepage
%% \@titlepagetrue
\newif\if@openright
\newif\if@mainmatter \@mainmattertrue
\hour\time \divide\hour by 60\relax
 \@tempcnta\hour \multiply\@tempcnta 60\relax
 \minute\time \advance\minute-\@tempcnta
%\newif\if@stysize \@stysizefalse
\newif\if@enablejfam \@enablejfamtrue
\DeclareOption{b5paper}{\setcounter{@paper}{4}%
  \setlength\paperheight {257mm}
  \setlength\paperwidth  {182mm}}
\DeclareOption{b5j}{\setcounter{@paper}{4}%\@stysizetrue
  \setlength\paperheight {257mm}
  \setlength\paperwidth  {182mm}}
\DeclareOption{10pt}{\renewcommand{\@ptsize}{0}}
\DeclareOption{11pt}{\renewcommand{\@ptsize}{1}}
\DeclareOption{12pt}{\renewcommand{\@ptsize}{2}}
\DeclareOption{tombow}{%
  \tombowtrue \tombowdatetrue
  \setlength{\@tombowwidth}{.1mm}%
  \@bannertoken{%
     \jobname\space:\space\number\year/\number\month/\number\day
      (\number\hour:\number\minute)}
  \maketombowbox}
\DeclareOption{tombo}{%
  \tombowtrue \tombowdatefalse
  \setlength{\@tombowwidth}{.1mm}%
  \maketombowbox}
\DeclareOption{mentuke}{%
  \tombowtrue \tombowdatefalse
  \setlength{\@tombowwidth}{\z@}%
  \maketombowbox}
\DeclareOption{tate}{%
  \AtBeginDocument{\tate\message{《縦組モード》}%
                   \adjustbaseline}%
}%
\DeclareOption{oneside}{\@twosidefalse}
\DeclareOption{twoside}{\@twosidetrue}
\DeclareOption{onecolumn}{\@twocolumnfalse}
\DeclareOption{twocolumn}{\@twocolumntrue}
%%\DeclareOption{titlepage}{\@titlepagetrue}
%%\DeclareOption{notitlepage}{\@titlepagefalse}

\DeclareOption{openright}{\@openrighttrue}
\DeclareOption{openany}{\@openrightfalse}
%% \DeclareOption{leqno}{\input{leqno.clo}}
%% \DeclareOption{fleqn}{\input{fleqn.clo}}
\DeclareOption{openbib}{%
  \AtEndOfPackage{%
   \renewcommand\@openbib@code{%
      \advance\leftmargin\bibindent
      \itemindent -\bibindent
      \listparindent \itemindent
      \parsep \z@
      }%
   \renewcommand\newblock{\par}}}
\DeclareOption{disablejfam}{\@enablejfamfalse}
\DeclareOption{draft}{\setlength\overfullrule{5pt}}
\DeclareOption{final}{\setlength\overfullrule{0pt}}

%%\ExecuteOptions{a4paper,10pt,twoside,onecolumn,final,openright}
\ExecuteOptions{b5paper,twoside,onecolumn,final,openright}
\ProcessOptions\relax

\if@cameraready
 \input pskanji-sci-book.fd
\else
\DeclareFontShape{JY1}{mc}{m}{n}{%
  <5>     sfixed * [5.1748] min5  %  7Q
  <6>     sfixed * [5.9141] min6  %  8Q
  <7>     sfixed * [7.3926] min7  % 10Q
  <8>     sfixed * [8.1318] min8  % 11Q
  <9>     sfixed * [8.8711] min9  % 12Q
  <10>    sfixed * [9.6104] min10 % 13Q
  <10.95> sfixed * [11.089] min10 % 15Q
  <12>    sfixed * [13.307] min10 % 18Q
  <14.4>  sfixed * [14.785] min10 % 20Q
  <17.28> sfixed * [17.742] min10 % 24Q
  <20.74> sfixed * [20.699] min10 % 28Q
  <24.88> sfixed * [23.656] min10 % 32Q
  <->                       min10 % 14Q, 16Q, 36Q, and so on
}{}
\DeclareFontShape{JY1}{mc}{bx}{n}{%
  <5>     sfixed * [5.1748] goth5
  <6>     sfixed * [5.9141] goth6
  <7>     sfixed * [7.3926] goth7
  <8>     sfixed * [8.1318] goth8
  <9>     sfixed * [8.8711] goth9
  <10>    sfixed * [9.6104] goth10
  <10.95> sfixed * [11.089] goth10
  <12>    sfixed * [13.307] goth10
  <14.4>  sfixed * [14.785] goth10
  <17.28> sfixed * [17.742] goth10
  <20.74> sfixed * [20.699] goth10
  <24.88> sfixed * [23.656] goth10
  <->                       goth10
}{}
\DeclareFontShape{JY1}{gt}{m}{n}{%
  <5>     sfixed * [5.1748] goth5
  <6>     sfixed * [5.9141] goth6
  <7>     sfixed * [7.3926] goth7
  <8>     sfixed * [8.1318] goth8
  <9>     sfixed * [8.8711] goth9
  <10>    sfixed * [9.6104] goth10
  <10.95> sfixed * [11.089] goth10
  <12>    sfixed * [13.307] goth10
  <14.4>  sfixed * [14.785] goth10
  <17.28> sfixed * [17.742] goth10
  <20.74> sfixed * [20.699] goth10
  <24.88> sfixed * [23.656] goth10
  <->                       goth10
}{}
\DeclareFontShape{JY1}{gt}{bx}{n}{%
  <5>     sfixed * [5.1748] goth5
  <6>     sfixed * [5.9141] goth6
  <7>     sfixed * [7.3926] goth7
  <8>     sfixed * [8.1318] goth8
  <9>     sfixed * [8.8711] goth9
  <10>    sfixed * [9.6104] goth10
  <10.95> sfixed * [11.089] goth10
  <12>    sfixed * [13.307] goth10
  <14.4>  sfixed * [14.785] goth10
  <17.28> sfixed * [17.742] goth10
  <20.74> sfixed * [20.699] goth10
  <24.88> sfixed * [23.656] goth10
  <->                       goth10
}{}
\DeclareFontShape{JY1}{mc}{m}{it}{<->ssub*mc/m/n}{}
\DeclareFontShape{JY1}{mc}{bx}{it}{<->ssub*gt/m/n}{}
\DeclareFontShape{JY1}{gt}{m}{it}{<->ssub*gt/m/n}{}
\DeclareFontShape{JY1}{gt}{bx}{it}{<->ssub*gt/m/n}{}
\DeclareFontShape{JT1}{mc}{m}{it}{<->ssub*mc/m/n}{}
\DeclareFontShape{JT1}{mc}{bx}{it}{<->ssub*gt/m/n}{}
\DeclareFontShape{JT1}{gt}{m}{it}{<->ssub*gt/m/n}{}
\DeclareFontShape{JT1}{gt}{bx}{it}{<->ssub*gt/m/n}{}
\DeclareFontShape{JY1}{mc}{m}{sc}{<->ssub*mc/m/n}{}
\DeclareFontShape{JT1}{mc}{m}{sc}{<->ssub*mc/m/n}{}
\fi

\renewcommand{\normalsize}{%
  \@setfontsize\normalsize\@xpt{23H}%
  \abovedisplayskip .5\Cvs \@plus .25\Cvs \@minus .15\Cvs
  \abovedisplayshortskip \z@ \@plus 1\p@
  \belowdisplayshortskip .3\Cvs \@plus .15\Cvs \@minus .1\Cvs
  \belowdisplayskip \abovedisplayskip
  \let\@listi\@listI}

\normalsize

\setbox0\hbox{\char\euc"A1A1}%
\setlength\Cht{\ht0}
\setlength\Cdp{\dp0}
\setlength\Cwd{\wd0}
\setlength\Cvs{\baselineskip}
\setlength\Chs{\wd0}

\newcommand{\small}{%
  \@setfontsize\small\@ixpt{20H}%
  \abovedisplayskip .5\baselineskip
   \@plus .25\baselineskip \@minus .15\baselineskip
  \abovedisplayshortskip \z@ \@plus 1\p@
  \belowdisplayshortskip .3\baselineskip
   \@plus .15\baselineskip \@minus .1\baselineskip
  \def\@listi{\leftmargin\leftmargini
              \topsep .5\baselineskip
               \@plus .25\baselineskip \@minus .15\baselineskip
              \parsep \z@
              \itemsep \parsep}%
  \belowdisplayskip \abovedisplayskip}

\newcommand{\footnotesize}{%
  \@setfontsize\footnotesize\@viiipt{16H}%
  \abovedisplayskip .5\baselineskip
   \@plus .25\baselineskip \@minus .15\baselineskip
  \abovedisplayshortskip \z@ \@plus 1\p@
  \belowdisplayshortskip .3\baselineskip
   \@plus .15\baselineskip \@minus .1\baselineskip
  \def\@listi{\leftmargin\leftmargini
              \topsep .5\baselineskip
               \@plus .25\baselineskip \@minus .15\baselineskip
              \parsep \z@
              \itemsep \parsep}%
  \belowdisplayskip \abovedisplayskip}

\newcommand{\scriptsize}{\@setfontsize\scriptsize\@viipt\@viiipt}
\newcommand{\tiny}{\@setfontsize\tiny\@vpt\@vipt}
\newcommand{\large}{\@setfontsize\large\@xiipt{17}}
\newcommand{\Large}{\@setfontsize\Large\@xivpt{21}}
\newcommand{\LARGE}{\@setfontsize\LARGE\@xviipt{31H}}
\newcommand{\huge}{\@setfontsize\huge\@xxpt{28}}
\newcommand{\Huge}{\@setfontsize\Huge\@xxvpt{33}}

\headheight\z@
\headsep\z@
\topskip1\Cht
\footskip8mm
\maxdepth.5\topskip
\textwidth113.75mm %% 35w x 13Q / 44w (143mm)
%\textwidth143mm
\@settopoint\textwidth
\textheight 37\Cvs
\advance \textheight \topskip
\advance \textheight .1mm %% .07mm
\@settopoint\textheight
\if@twocolumn
 \setlength\marginparsep{10\p@}
\else
 \setlength\marginparsep{10\p@}
\fi
\setlength\marginparpush{5\p@}

\iftombow
 \topmargin\paperheight
 \advance \topmargin -\textheight
 \advance \topmargin -\headheight
 \advance \topmargin -\headsep
 \advance \topmargin -\footskip
 \divide \topmargin \tw@
 \advance \topmargin -1in
 \@settopoint\topmargin
%
 \oddsidemargin\paperwidth
 \advance \oddsidemargin -\textwidth
 \divide \oddsidemargin \tw@
 \advance \oddsidemargin 14.625mm
 \advance \oddsidemargin -1in
 \@settopoint\oddsidemargin
 \evensidemargin\oddsidemargin
 \@settopoint\evensidemargin
\else
 \topmargin \z@
 \@settopoint\topmargin
 \oddsidemargin 29.25mm
 \@settopoint\oddsidemargin
 \evensidemargin\oddsidemargin
 \@settopoint\evensidemargin
\fi

\marginparwidth 2in
\@settopoint\marginparwidth

%%\footnotesep 6.65\p@ %% .7\baselineskip of footnotesize
%%\skip\footins 9\p@ \@plus 4\p@ \@minus 2\p@
%%\floatsep 12\p@ \@plus 2\p@ \@minus 2\p@
%%\textfloatsep 20\p@ \@plus 2\p@ \@minus 4\p@
%%\intextsep 12\p@ \@plus 2\p@ \@minus 2\p@
%%\dblfloatsep 12\p@ \@plus 2\p@ \@minus 2\p@
%%\dbltextfloatsep 20\p@ \@plus 2\p@ \@minus 4\p@
\footnotesep 2.8mm
\skip\footins .75\Cvs \@plus 4\p@ \@minus 2\p@
\floatsep 1\Cvs \@plus 2\p@ \@minus 2\p@
\dblfloatsep 1\Cvs \@plus 2\p@ \@minus 2\p@
\textfloatsep 1.5\Cvs \@plus 2\p@ \@minus 4\p@
\dbltextfloatsep 1.5\Cvs \@plus 2\p@ \@minus 4\p@
\intextsep 1.5\Cvs \@plus 2\p@ \@minus 2\p@

\@fptop 0\p@ \@plus 1fil
\@fpsep 8\p@ \@plus 2fil
\@fpbot 0\p@ \@plus 1fil
\@dblfptop 0\p@ \@plus 1fil
\@dblfpsep 8\p@ \@plus 2fil
\@dblfpbot 0\p@ \@plus 1fil
%% \partopsep 2\p@ \@plus 1\p@ \@minus 1\p@
\partopsep\z@

\def\@listi{\leftmargin\leftmargini
% \topsep  8\p@ \@plus2\p@ \@minus4\p@
% \parsep  4\p@ \@plus2\p@ \@minus\p@
% \itemsep 4\p@ \@plus2\p@ \@minus\p@
  \topsep.5\Cvs \@plus .25\Cvs \@minus .15\Cvs
  \parsep\z@ %% 2\p@ \@plus\p@ \@minus\p@
  \itemsep\parsep
}

\let\@listI\@listi
\@listi
\def\@listii{\leftmargin\leftmarginii
   \labelwidth\leftmarginii \advance\labelwidth-\labelsep
   \topsep 4\p@ \@plus2\p@ \@minus\p@
   \parsep 2\p@ \@plus\p@  \@minus\p@
   \itemsep\parsep}
\def\@listiii{\leftmargin\leftmarginiii
   \labelwidth\leftmarginiii \advance\labelwidth-\labelsep
   \topsep   2\p@ \@plus\p@\@minus\p@
   \parsep   \z@
   \partopsep\p@ \@plus\z@ \@minus\p@
   \itemsep\topsep}
\def\@listiv {\leftmargin\leftmarginiv
              \labelwidth\leftmarginiv
              \advance\labelwidth-\labelsep}
\def\@listv  {\leftmargin\leftmarginv
              \labelwidth\leftmarginv
              \advance\labelwidth-\labelsep}
\def\@listvi {\leftmargin\leftmarginvi
              \labelwidth\leftmarginvi
              \advance\labelwidth-\labelsep}
%% \endinput
%%
%% End of file `jbk10.clo'.
%% </from jbk10.clo>

%%\if@stysize
%%  \setlength\columnsep{2\Cwd}
%%\else
%%  \setlength\columnsep{10\p@}
%%\fi
\columnsep 2zw
\columnseprule 0\p@
\lineskip 1\p@ 
\normallineskip 1\p@
\renewcommand{\baselinestretch}{}
\parskip \z@ %% 0\p@ \@plus \p@
\parindent 1zw %% 1\Cwd

\@lowpenalty   51
\@medpenalty  151
\@highpenalty 301
\setcounter{topnumber}{5}
\setcounter{bottomnumber}{5}
\setcounter{totalnumber}{10}
\setcounter{dbltopnumber}{5}
\renewcommand{\topfraction}{.95}
\renewcommand{\bottomfraction}{.95}
\renewcommand{\textfraction}{.05}
\renewcommand{\floatpagefraction}{.05}
\renewcommand{\dbltopfraction}{.95}
\renewcommand{\dblfloatpagefraction}{.05}

\if@Proof
 \def\@Rtop{\rlap{\smash{\rule[-\headsep]{0.05mm}{10mm}%
  \rule[-\headsep]{10mm}{0.05mm}}}}
 \def\@Ltop{\llap{\smash{\rule[-\headsep]{10mm}{0.05mm}%
  \rule[-\headsep]{0.05mm}{10mm}}}}
 \def\@Rbot{\rlap{\smash{\raisebox{1\footskip}
  {\rule[-10mm]{0.05mm}{10mm}\rule{10mm}{0.05mm}}}}}
 \def\@Lbot{\llap{\smash{\raisebox{1\footskip}
  {\rule{10mm}{0.05mm}\rule[-10mm]{0.05mm}{10mm}}}}}
\else
 \let\@Rtop\relax\let\@Ltop\relax\let\@Rbot\relax\let\@Lbot\relax
\fi

\def\foot@font{\small}
\def\nombre@font{\small\bfseries}

\def\ps@empty{\let\ps@jpl@in\ps@plain%
 \def\@oddhead{\hbox to \textwidth{\hss\hbox to 143mm{\@Ltop\hfil\@Rtop}}}%
 \def\@oddfoot{\hbox to \textwidth{\hss\hbox to 143mm{\@Lbot\hfil\@Rbot}}}%
 \let\@evenhead\@oddhead
 \let\@evenfoot\@oddfoot
 \let\@mkboth\@gobbletwo
 \let\chaptermark\@gobble
 \let\sectionmark\@gobble
}

\def\ps@plain{%
 \let\@mkboth\@gobbletwo
 \let\ps@jpl@in\ps@plain
 \def\@oddhead{\hbox to \textwidth{\hss\hbox to 143mm{\@Ltop\hfil\@Rtop}}}%
 \let\@evenhead\@oddhead
 \def\@oddfoot{%
  \hbox to \textwidth{\hss\hbox to 143mm{\@Lbot
   \hfil{\nombre@font\thepage}\@Rbot}}%
 }%
 \def\@evenfoot{%
  \hbox to \textwidth{\hss\hbox to 143mm{\@Lbot
   {\nombre@font\thepage}\hfil\@Rbot}}%
 }%
}
\let\ps@jpl@in\ps@plain

%\def\ps@headnombre{\let\@mkboth\@gobbletwo
%    \let\ps@jpl@in\ps@headnombre
%  \def\@evenhead{\@Ltop{\nombre@font\thepage}\hfil\@Rtop}%
%  \def\@oddhead{\@Ltop\hfil{\nombre@font\thepage}\@Rtop}%
%  \def\@oddfoot{\@Lbot\hfil\@Rbot}%
%  \let\@evenfoot\@oddfoot
%}

%\def\ps@footnombre{\let\@mkboth\@gobbletwo
%    \let\ps@jpl@in\ps@footnombre
%  \def\@evenfoot{\@Lbot{\nombre@font\thepage}\hfil\@Rbot}%
%  \def\@oddfoot{\@Lbot\hfil{\nombre@font\thepage}\@Rbot}%
%  \def\@oddhead{\@Ltop\hfil\@Rtop}%
%  \let\@evenhead\@oddhead}

%\def\rightmark{\expandafter\@rightmark\botmark{}{}}

%\def\ps@headings{\let\@mkboth\markboth
% \def\@oddhead{\@Ltop\hfill\@Rtop}%
% \def\@oddfoot{\@Lbot\hfill{\foot@font\rightmark}\hskip3mm
%   {\nombre@font\thepage}\@Rbot}%
% \def\@evenhead{\@Ltop\hfill\@Rtop}%
% \def\@evenfoot{\@Lbot{\nombre@font\thepage}\hskip3mm
%   {\foot@font\leftmark}\hfill\@Rbot}%
%  \def\chaptermark##1{\markboth {\uppercase{\ifnum \c@secnumdepth >\m@ne
%   \@chapapp{\thechapter}\hskip1zw\fi ##1}}{}}
%  \def\sectionmark##1{\markright{\uppercase{\ifnum \c@secnumdepth >\z@
%   \thesection\hskip1zw \fi ##1}}}}

\def\ps@headings{\let\ps@jpl@in\ps@plain
 \def\@oddhead{\hbox to \textwidth{\hss\hbox to 143mm{\@Ltop\hfil\@Rtop}}}%
 \let\@evenhead\@oddhead
 \def\@oddfoot{%
  \hbox to \textwidth{\hss\hbox to 143mm{\@Lbot
   \hfil{\foot@font\rightmark}\hskip3mm
    {\nombre@font\thepage}\@Rbot}}%
 }%
 \def\@evenfoot{%
  \hbox to \textwidth{\hss\hbox to 143mm{\@Lbot
   {\nombre@font\thepage}\hskip3mm
    {\foot@font\leftmark}\hfil\@Rbot}}%
 }%
 \let\@mkboth\markboth
 \def\chaptermark##1{\markboth{%
  \ifnum \c@secnumdepth >\m@ne
   \if@mainmatter
    \@chapapp\thechapter\@chappos\hskip1zw
   \fi
  \fi
   ##1}{}}%
 \def\sectionmark##1{\markright{%
  \ifnum \c@secnumdepth >\z@ \thesection\hskip1zw\fi
   ##1}}%
}

%  \def\ps@bothstyle{\let\ps@jpl@in\ps@footnombre
%    \def\@evenhead{\@Ltop{\head@font\leftmark}\hfil\@Rtop}% right page
%    \def\@evenfoot{\@Lbot{\nombre@font\thepage}\hfil\@Rbot}% right page
%    \def\@oddhead{\@Ltop\hfil{\head@font\rightmark}\@Rtop}% left page
%    \def\@oddfoot{\@Lbot\hfil{\nombre@font\thepage}\@Rbot}% left page
%  \let\@mkboth\markboth
%  \def\chaptermark##1{\markboth{%
%     \ifnum \c@secnumdepth >\m@ne
%         \if@mainmatter
%         \@chapapp\thechapter\@chappos\hskip1zw
%         \fi
%     \fi
%     ##1}{}}%
%  \def\sectionmark##1{\markright{%
%     \ifnum \c@secnumdepth >\z@ \thesection\hskip1zw\fi
%     ##1}}%
%  }

\def\ps@myheadings{\let\ps@jpl@in\ps@plain%
 \def\@oddhead{\hbox to \textwidth{\hss\hbox to 143mm{\@Ltop\hfil\@Rtop}}}%
 \let\@evenhead\@oddhead
 \def\@oddfoot{%
  \hbox to \textwidth{\hss\hbox to 143mm{\@Lbot
   \hfil{\foot@font\rightmark}\hskip3mm
    {\nombre@font\thepage}\@Rbot}}%
 }%
 \def\@evenfoot{%
  \hbox to \textwidth{\hss\hbox to 143mm{\@Lbot
   {\nombre@font\thepage}\hskip3mm
    {\foot@font\leftmark}\hfil\@Rbot}}%
 }%
  \let\@mkboth\@gobbletwo
  \let\chaptermark\@gobble
  \let\sectionmark\@gobble
}

%%\if@compatibility
%%\newenvironment{titlepage}
%%    {%
%%      \cleardoublepage
%%     \if@twocolumn\@restonecoltrue\onecolumn
%%     \else\@restonecolfalse\newpage\fi
%%     \thispagestyle{empty}%
%%     \setcounter{page}\z@
%%    }%
%%    {\if@restonecol\twocolumn\else\newpage\fi
%%    }
%%\else
%%\newenvironment{titlepage}
%%    {%
%%      \cleardoublepage
%%      \if@twocolumn
%%        \@restonecoltrue\onecolumn
%%      \else
%%        \@restonecolfalse\newpage
%%      \fi
%%      \thispagestyle{empty}%
%%      \setcounter{page}\@ne
%%    }%
%%    {\if@restonecol\twocolumn \else \newpage \fi
%%     \if@twoside\else
%%        \setcounter{page}\@ne
%%     \fi
%%    }
%%\fi

%%\def\p@thanks#1{\footnotemark
%%  \protected@xdef\@thanks{\@thanks
%%    \protect{\noindent$\m@th^\thefootnote$~#1\protect\par}}}

%%\if@titlepage
%%  \newcommand{\maketitle}{\begin{titlepage}%
%%  \let\footnotesize\small
%%  \let\footnoterule\relax
%%  \let\footnote\thanks
%%  \null\vfil
%%  \vskip 60\p@
%%  \begin{center}%
%%    {\LARGE \@title \par}%
%%    \vskip 3em%
%%    {\Large
%%     \lineskip .75em%
%%      \begin{tabular}[t]{c}%
%%        \@author
%%      \end{tabular}\par}%
%%      \vskip 1.5em%
%%    {\large \@date \par}%       % Set date in \large size.
%%  \end{center}\par
%%  \@thanks\vfil\null
%%  \end{titlepage}%
%%  \setcounter{footnote}{0}%
%%  \global\let\thanks\relax
%%  \global\let\maketitle\relax
%%  \global\let\p@thanks\relax
%%  \global\let\@thanks\@empty
%%  \global\let\@author\@empty
%%  \global\let\@date\@empty
%%  \global\let\@title\@empty
%%  \global\let\title\relax
%%  \global\let\author\relax
%%  \global\let\date\relax
%%  \global\let\and\relax
%%  }%
%%\else
%%  \newcommand{\maketitle}{\par
%%  \begingroup
%%    \renewcommand{\thefootnote}{\fnsymbol{footnote}}%
%%    \def\@makefnmark{\hbox{\ifydir $\m@th^{\@thefnmark}$
%%      \else\hbox{\yoko$\m@th^{\@thefnmark}$}\fi}}%
%%     \long\def\@makefntext##1{\parindent 1em\noindent
%%       \hbox to1.8em{\hss$\m@th^{\@thefnmark}$}##1}%
%%    \if@twocolumn
%%      \ifnum \col@number=\@ne \@maketitle
%%      \else \twocolumn[\@maketitle]%
%%      \fi
%%    \else
%%      \newpage
%%      \global\@topnum\z@   % Prevents figures from going at top of page.
%%      \@maketitle
%%    \fi
%%     \thispagestyle{jpl@in}\@thanks
%%  \endgroup
%%  \setcounter{footnote}{0}%
%%  \global\let\thanks\relax
%%  \global\let\maketitle\relax
%%  \global\let\p@thanks\relax
%%  \global\let\@thanks\@empty
%%  \global\let\@author\@empty
%%  \global\let\@date\@empty
%%  \global\let\@title\@empty
%%  \global\let\title\relax
%%  \global\let\author\relax
%%  \global\let\date\relax
%%  \global\let\and\relax
%%  }
%%  \def\@maketitle{%
%%  \newpage\null
%%  \vskip 2em%
%%  \begin{center}%
%%  \let\footnote\thanks
%%    {\LARGE \@title \par}%
%%    \vskip 1.5em%
%%    {\large
%%      \lineskip .5em%
%%      \begin{tabular}[t]{c}%
%%        \@author
%%      \end{tabular}\par}%
%%    \vskip 1em%
%%    {\large \@date}%
%%  \end{center}%
%%  \par\vskip 1.5em}
%%\fi

\newcommand*{\chaptermark}[1]{}
\setcounter{secnumdepth}{3}

\newcounter{part}
\newcounter{chapter}
\newcounter{section}[chapter]
\newcounter{subsection}[section]
\newcounter{subsubsection}[subsection]
\newcounter{paragraph}[subsubsection]
\newcounter{subparagraph}[paragraph]

\renewcommand{\thepart}{\@Roman\c@part}
\renewcommand{\thechapter}{\@arabic\c@chapter}
\renewcommand{\thesection}{\thechapter.\@arabic\c@section}
\renewcommand{\thesubsection}{\thesection.\@arabic\c@subsection}
\renewcommand{\thesubsubsection}{%
   \thesubsection.\@arabic\c@subsubsection}
\renewcommand{\theparagraph}{%
   \thesubsubsection.\@arabic\c@paragraph}
\renewcommand{\thesubparagraph}{%
   \theparagraph.\@arabic\c@subparagraph}

\newcommand{\@chapapp}{\prechaptername}
\newcommand{\@chappos}{\postchaptername}

\newif\if@frontmatter \@frontmatterfalse
\newcommand\frontmatter{%
 \@frontmattertrue
%%
 \textwidth143mm
 \@settopoint\textwidth
 \iftombow
  \oddsidemargin\paperwidth
  \advance \oddsidemargin -\textwidth
  \divide \oddsidemargin \tw@
  \advance \oddsidemargin -1in
  \@settopoint\oddsidemargin
  \evensidemargin\oddsidemargin
  \@settopoint\evensidemargin
 \else
  \oddsidemargin\z@
  \@settopoint\oddsidemargin
  \evensidemargin\oddsidemargin
  \@settopoint\evensidemargin
 \fi
 \linewidth\textwidth \hsize\textwidth
%%
 \if@openright \cleardoublepage \else \clearpage \fi
 \@mainmatterfalse\pagenumbering{roman}%
}
\newcommand{\mainmatter}{%
 \@frontmatterfalse
 \clearpage
%% 
 \textwidth 113.75mm
 \@settopoint\textwidth
 \iftombow
  \oddsidemargin\paperwidth
  \advance \oddsidemargin -\textwidth
  \divide \oddsidemargin \tw@
  \advance \oddsidemargin 14.625mm
  \advance \oddsidemargin -1in
  \@settopoint\oddsidemargin
  \evensidemargin\oddsidemargin
  \@settopoint\evensidemargin
 \else
  \oddsidemargin 29.25mm
  \@settopoint\oddsidemargin
  \evensidemargin\oddsidemargin
  \@settopoint\evensidemargin
 \fi
 \linewidth\textwidth \hsize\textwidth
%%
 \if@openright \cleardoublepage \else \clearpage \fi
 \@mainmattertrue\pagenumbering{arabic}%
}
\newcommand{\backmatter}{%
 \@frontmatterfalse
 \clearpage
%%
 \textwidth143mm
 \@settopoint\textwidth
 \iftombow
  \oddsidemargin\paperwidth
  \advance \oddsidemargin -\textwidth
  \divide \oddsidemargin \tw@
  \advance \oddsidemargin -1in
  \@settopoint\oddsidemargin
  \evensidemargin\oddsidemargin
  \@settopoint\evensidemargin
 \else
  \oddsidemargin\z@
  \@settopoint\oddsidemargin
  \evensidemargin\oddsidemargin
  \@settopoint\evensidemargin
 \fi
 \linewidth\textwidth \hsize\textwidth
%%
 \if@openright \cleardoublepage \else \clearpage \fi
 \@mainmatterfalse
}

\if@Proof
 \def\cleardoublepage{\clearpage
  \if@twoside
   \ifodd\c@page
   \else
    \clearpage
    \addtocounter{page}{1}%
   \fi
  \fi}
\else
 \def\cleardoublepage{\clearpage
  \if@twoside
   \ifodd\c@page
   \else
    \hbox{}\thispagestyle{empty}\newpage
    \if@twocolumn\hbox{}\newpage\fi
    \clearpage
   \fi
  \fi}
\fi

\def\part{% }{
  \if@openright \cleardoublepage \else \clearpage \fi
  \thispagestyle{empty}%
  \if@twocolumn\onecolumn\@tempswatrue\else\@tempswafalse\fi
  \null\vfil
  \secdef\@part\@spart}
\def\@part[#1]#2{%
  \ifnum \c@secnumdepth >-2\relax
    \refstepcounter{part}%
    \addcontentsline{toc}{part}{%
       \prepartname\thepart\postpartname\hspace{1em}#1}%
  \else
    \addcontentsline{toc}{part}{#1}%
  \fi
  \markboth{}{}%
  {\centering
   \interlinepenalty\@M\reset@font
   \ifnum \c@secnumdepth >-2\relax
     \huge\bfseries\prepartname\thepart\postpartname
     \par\vskip20\p@
   \fi
   \Huge\bfseries#2\par}%
   \@endpart}
\def\@spart#1{{%
  \centering
  \interlinepenalty\M@\reset@font
  \Huge\bfseries#1\par}%
  \@endpart}
\def\@endpart{\vfil\newpage
   \if@twoside\null\thispagestyle{empty}\newpage\fi
   \if@tempswa\twocolumn\fi}

\def\chapter{% }{
  \if@openright\cleardoublepage\else\clearpage\fi
  \thispagestyle{jpl@in}%
  \global\@topnum\z@
  \@afterindenttrue
  \secdef\@chapter\@schapter}

\def\@chapter[#1]#2{%
 \ifnum \c@secnumdepth >\m@ne
  \if@mainmatter
   \refstepcounter{chapter}%
   \typeout{\@chapapp\space\the\c@chapter\space\@chappos}%
   \addcontentsline{toc}{chapter}%
    {\protect\numberline{\@chapapp\thechapter\@chappos}#1}%
   \else
    \addcontentsline{toc}{chapter}{#1}%
   \fi
 \else
  \addcontentsline{toc}{chapter}{#1}%
 \fi
 \chaptermark{#1}%
% \addtocontents{lof}{\protect\addvspace{10\p@}}%
% \addtocontents{lot}{\protect\addvspace{10\p@}}%
 \@makechapterhead{#2}%
 \@afterheading
}

\DeclareFixedFont{\KFchapnum}{JY1}{gt}{m}{n}{17.28}
\def\chapnum@font{\Huge\sffamily\KFchapnum\ybaselineshift2\@EM}

\def\chap@font{%
 \Huge\bfseries\mathversion{bold}\ybaselineshift2\@EM
}

\def\@makechapterhead#1{\hbox{}%
 {\parindent\z@
  \boxmaxdepth\z@
  \lineskiplimit\z@
  \lineskip\z@
  \vbox to 11\baselineskip{%
   \leftskip-9zw
   \ifnum \c@secnumdepth >\m@ne 
    \vskip\baselineskip
    \vbox to \baselineskip{\vss
     \hskip8mm{\reset@font\chapnum@font\@chapapp\ \thechapter\ \@chappos}%
    \vss}%
   \fi
   \vskip\baselineskip
   \hskip8mm %% 8+135=143
   \parbox[t]{134.75mm}{\raggedright\reset@font\chap@font #1}%
   \vss
  }\par
 }
}

\def\@schapter#1{%
 \@makeschapterhead{#1}\@afterheading
}

\DeclareFixedFont{\preface@font}{JY1}{mc}{m}{n}{24.88}% 32\@JQ
\def\reference@font{\LARGE\bfseries}

\def\@makeschapterhead#1{\null
 {\parindent\z@
  \boxmaxdepth\z@
  \lineskiplimit\z@
  \lineskip\z@
  \if@frontmatter %% まえがき
   \vbox to 6\Cvs{%
    \vss\vss
    \vbox to \Cvs{\vss
     \hfil
     {\preface@font #1}%
    \vss}%
    \vss\vss\vss
   }\par
  \else
   \if@mainmatter
   \else %% 参考文献
    \vbox to 4\Cvs{%
     \vss
     \vbox to \Cvs{\vss
      \hskip6mm
      {\reference@font #1}
     \vss}%
     \vss\vss
    }\par
   \fi
  \fi
  \nobreak
 }%
}

\def\abstract{%
 \leftskip-4.5zw % 9-4.5
 \rightskip4.5zw
}
\def\endabstract{\par}

\def\@afterheading{%
 \global\@nobreaktrue
 \everypar{%
  \if@nobreak
   \global\@nobreakfalse
   \clubpenalty \z@
    \if@afterindent
    \else
     {\setbox\z@=\lastbox}%
    \fi
  \else
   \clubpenalty\@clubpenalty
   \everypar{}%
  \fi}%
}

% \@startsection{NAME}{LEVEL}{INDENT}{BEFORESKIP}{AFTERSKIP}{STYLE}
\def\@startsection#1#2#3#4#5#6{\if@noskipsec \leavevmode \fi
   \par \@tempskipa #4\relax
   \@afterindenttrue
   \ifdim \@tempskipa <\z@ \@tempskipa -\@tempskipa \@afterindentfalse\fi
   \if@nobreak
    \everypar{}%
   \else
    \addpenalty\@secpenalty
     \ifnum#2=\@ne
      \vskip-\prevdepth \prevdepth\z@ \vskip\Cvs
      \advance\@tempskipa-\Cvs
      \advance\@tempskipa-\Cvs
      \vspace{\Cvs}
      \vspace*{\@tempskipa}
     \else
      \ifnum#2=\tw@
       \vskip-\prevdepth \prevdepth\z@ \vskip\Cvs
       \advance\@tempskipa-\Cvs
       \advance\@tempskipa-\Cvs
       \vspace{\Cvs}
       \vspace*{\@tempskipa}
      \else
       \vskip-\prevdepth \prevdepth\z@ \vskip\Cvs
       \advance\@tempskipa-\Cvs
       \vspace*{\@tempskipa}
%%     \addvspace\@tempskipa
      \fi
     \fi
   \fi
 \@ifstar{\@ssect{#3}{#4}{#5}{#6}}{\@dblarg{\@sect{#1}{#2}{#3}{#4}{#5}{#6}}}}

% \@sect{NAME}{LEVEL}{INDENT}{BEFORESKIP}{AFTERSKIP}{STYLE}[ARG1]{ARG2}
\def\@sect#1#2#3#4#5#6[#7]#8{%
 \ifnum #2>\c@secnumdepth
  \let\@svsec\@empty
 \else
  \refstepcounter{#1}%
  \protected@edef\@svsec{\@seccntformat{#1}\relax}%
 \fi
  \@tempskipa #5\relax
  \ifdim \@tempskipa>\z@
   \begingroup
    #6{%
      \ifnum#2=\@ne
       \@hangfrom{\llap{\sectionnum@font \thesection}\hskip #3\relax}%
        \interlinepenalty \@M #8\@@par
      \else
       \@hangfrom{\hskip #3\relax
        {\csname #1num@font\endcsname\@svsec}}%
         \interlinepenalty \@M #8\@@par
      \fi
      }%
   \endgroup
   \csname #1mark\endcsname{#7}%
   \addcontentsline{toc}{#1}{%
    \ifnum #2>\c@secnumdepth \else
     \protect\numberline{\csname the#1\endcsname}%
    \fi
    #7}%
  \else
   \def\@svsechd{%
     #6{\hskip #3\relax
     \@svsec #8}%
    \csname #1mark\endcsname{#7}%
    \addcontentsline{toc}{#1}{%
     \ifnum #2>\c@secnumdepth \else
       \protect\numberline{\csname the#1\endcsname}%
     \fi
     #7}}%
  \fi
  \@xsect{#5}%
}

\def\@seccntformat#1{\csname the#1\endcsname\hskip1zw}

\def\sectionnum@font{\Large\sffamily}
\def\susectionnum@font{\normalsize\sffamily}

\def\sec@font{%
 \large\bfseries\baselineskip\Cvs\mathversion{bold}%
}
\def\subsec@font{%
 \normalsize\bfseries\mathversion{bold}%
}
\def\subsubsec@font{%
 \normalsize\bfseries\mathversion{bold}%
}

\def\section{\@startsection{section}{1}{1zw}% }{
   {1.5\Cvs \@plus.5\Cdp \@minus.2\Cdp}%
   {.5\Cvs \@plus.3\Cdp \@minus.15\Cdp}%
   {\reset@font\sec@font}}
\def\subsection{\@startsection{subsection}{2}{\z@}% }{
   {.99999\Cvs \@plus.5\Cdp \@minus.2\Cdp}%
   {.00001\Cvs \@plus.3\Cdp \@minus .1\Cdp}%
   {\reset@font\subsec@font}}
\def\subsubsection{\@startsection{subsubsection}{3}{\z@}% }{
   {.99999\Cvs \@plus.5\Cdp \@minus.2\Cdp}%
   {.00001\Cvs \@plus.3\Cdp \@minus .1\Cdp}%
   {\reset@font\subsubsec@font}}
\def\paragraph{\@startsection{paragraph}{4}{\z@}% }{
   {.00001\Cvs \@plus.3\Cdp \@minus .1\Cdp}%
   {.00001\Cvs \@plus.3\Cdp \@minus .1\Cdp}%
   {\reset@font\normalsize\bfseries\mathversion{bold}}}
\def\subparagraph{\@startsection{subparagraph}{5}{\z@}% }{
   {.00001\Cvs \@plus.3\Cdp \@minus .1\Cdp}%
   {.00001\Cvs \@plus.3\Cdp \@minus .1\Cdp}%
   {\reset@font\normalsize\bfseries\mathversion{bold}}}

\def\appendix{\par
  \setcounter{chapter}{0}%
  \setcounter{section}{0}%
  \renewcommand{\@chapapp}{\appendixname}%
  \renewcommand{\@chappos}\space%
  \renewcommand{\thechapter}{\@Alph\c@chapter}}

\if@twocolumn
  \setlength\leftmargini {2zw}% 2em
\else
  \setlength\leftmargini {2zw}% 2.5em
\fi
\setlength\leftmarginii  {2zw}% 2.2em
\setlength\leftmarginiii {2zw}% 1.87em
\setlength\leftmarginiv  {2zw}% 1.7em
\if@twocolumn
  \setlength\leftmarginv {1zw}% .5em
  \setlength\leftmarginvi{1zw}% .5em
\else
  \setlength\leftmarginv {1zw}% 1em
  \setlength\leftmarginvi{1zw}% 1em
\fi
\setlength  \labelsep  {.5zw}% .5em
\setlength  \labelwidth{\leftmargini}
\addtolength\labelwidth{-\labelsep}
\@beginparpenalty -\@lowpenalty
\@endparpenalty   -\@lowpenalty
\@itempenalty     -\@lowpenalty

\renewcommand{\theenumi}{\@arabic\c@enumi}
\renewcommand{\theenumii}{\@alph\c@enumii}
\renewcommand{\theenumiii}{\@roman\c@enumiii}
\renewcommand{\theenumiv}{\@Alph\c@enumiv}
\newcommand{\labelenumi}{\theenumi.}
\newcommand{\labelenumii}{(\theenumii)}
\newcommand{\labelenumiii}{\theenumiii.}
\newcommand{\labelenumiv}{\theenumiv.}
\renewcommand{\p@enumii}{\theenumi}
\renewcommand{\p@enumiii}{\theenumi(\theenumii)}
\renewcommand{\p@enumiv}{\p@enumiii\theenumiii}

\renewenvironment{enumerate}
  {\ifnum \@enumdepth >3\relax\@toodeep\else
   \advance\@enumdepth\@ne
   \edef\@enumctr{enum\romannumeral\the\@enumdepth}%
   \list{\csname label\@enumctr\endcsname}{%
         \topsep\z@\parsep\z@\partopsep\z@\itemsep\z@
%         \labelwidth1zw
%         \labelsep.3zw
%         \ifnum \@enumdepth=\@ne \leftmargin1zw\relax
%           \else\leftmargin\leftskip\fi
%         \advance\leftmargin 1zw
         \listparindent1zw
         \usecounter{\@enumctr}%
         \def\makelabel##1{\hss\llap{##1}}}%
   \fi}{\endlist}

\newcommand{\labelitemi}{\leavevmode
 \raise.1zh\hbox to 1zw{\hss \textbullet\hss}}
\newcommand{\labelitemii}{\raise.1zh\hbox to 1zw{\hss $\circ$\hss}}
\newcommand{\labelitemiii}{\textasteriskcentered}
\newcommand{\labelitemiv}{\textperiodcentered}

\renewenvironment{itemize}
  {\ifnum \@itemdepth >3\relax\@toodeep\else
   \advance\@itemdepth\@ne
   \edef\@itemitem{labelitem\romannumeral\the\@itemdepth}%
   \expandafter
   \list{\csname \@itemitem\endcsname}{%
%
        \labelwidth 1zw
        \labelsep \z@
        \itemindent \z@
        \listparindent 1zw
        \topsep\z@\parsep\z@\partopsep\z@\itemsep\z@
        \def\makelabel##1{\hss\llap{##1}}}%
   \fi}{\endlist}

\newenvironment{description}
  {\list{}{\labelwidth\z@ \itemindent-\leftmargin
   \iftdir
     \leftmargin\leftskip \advance\leftmargin3\Cwd
     \rightmargin\rightskip
     \labelsep1zw \itemsep\z@
     \listparindent\z@
     \topsep\z@\parsep\z@\partopsep\z@\itemsep\z@
   \fi
           \let\makelabel\descriptionlabel}}{\endlist}
\newcommand{\descriptionlabel}[1]{%
   \hspace\labelsep\normalfont\bfseries #1}

\newenvironment{verse}
  {\let\\\@centercr
   \list{}{%
           \topsep\z@\parsep\z@\partopsep\z@\itemsep\z@
           \itemsep\z@ \itemindent -2zw
           \listparindent\itemindent
           \rightmargin\leftmargin \advance\leftmargin 2zw}%
           \item\relax}{\endlist}

\newenvironment{quotation}
  {\list{}{%
           \topsep\z@\parsep\z@\partopsep\z@\itemsep\z@
           \listparindent 2zw
           \itemindent\listparindent
           \rightmargin\leftmargin
          }%
           \item\relax}{\endlist}

\newenvironment{quote}
  {\list{}{\rightmargin\leftmargin
           \topsep\z@\parsep\z@\partopsep\z@\itemsep\z@
          }%
           \item\relax}{\endlist}

\newskip\@hlskip
\@hlskip=.5\baselineskip \@plus 1mm \@minus .5mm

\gdef\tabbing{\lineskip \z@skip\let\>\@rtab\let\<\@ltab\let\=\@settab
     \let\+\@tabplus\let\-\@tabminus\let\`\@tabrj\let\'\@tablab
     \let\\=\@tabcr
     \@hightab\@firsttab
     \global\@nxttabmar\@firsttab
     \dimen\@firsttab\@totalleftmargin
     \global\@tabpush\z@ \global\@rjfieldfalse
     \topsep\@hlskip \parsep\z@\partopsep\z@
     \trivlist \item\relax
     \if@minipage\else\vskip\parskip\fi
     \setbox\@tabfbox\hbox{%
       \rlap{\hskip\@totalleftmargin\indent\the\everypar}}%
     \def\@itemfudge{\box\@tabfbox}%
     \@startline\ignorespaces}

\newdimen\verbatimleftmargin
  \verbatimleftmargin\z@
\newdimen\verbatimbaselineskip
  \verbatimbaselineskip\baselineskip
\def\verbatimsize{\normalsize}

\def\@verbatim{%
 \topsep \z@ % \@hlskip
 \partopsep\z@\parsep\z@\itemsep\z@
 \trivlist \item\relax
  \if@minipage\else
   \vskip\baselineskip
   \vskip-\verbatimbaselineskip
%  \vskip\parskip
  \fi
  \leftskip\@totalleftmargin
  \if@minipage\else
   \advance \leftskip by \verbatimleftmargin
  \fi
  \rightskip\z@skip
  \parindent\z@\parfillskip\@flushglue\parskip\z@skip
  \@@par
  \@tempswafalse
  \def\par{%
    \if@tempswa
      \leavevmode \null \@@par\penalty\interlinepenalty
    \else
      \@tempswatrue
      \ifhmode\@@par\penalty\interlinepenalty\fi
    \fi}%
  \let\do\@makeother \dospecials
  \obeylines 
   \verbatimsize \baselineskip\verbatimbaselineskip
   \verbatim@font \@noligs
  \everypar \expandafter{\the\everypar \unpenalty}%
}
%% \def\verbatim@font{\normalfont\ttfamily}

\newcounter{figure}[chapter]
\renewcommand{\thefigure}{%
  \ifnum\c@chapter>\z@\thechapter.\fi\@arabic\c@figure}
\def\fps@figure{tbp}
\def\ftype@figure{1}
%% \def\ext@figure{lof}
\def\fnum@figure{\figurename~\thefigure}
\newenvironment{figure}
               {\floatenv\capwidth113.75mm
                \abovecaptionskip\z@\belowcaptionskip\z@\@float{figure}}
               {\end@float}
\newenvironment{figure*}
               {\floatenv\capwidth113.75mm
                \abovecaptionskip\z@\belowcaptionskip\z@\@dblfloat{figure}}
               {\end@dblfloat}

\newcounter{table}[chapter]
\renewcommand{\thetable}{\thechapter.\@arabic\c@table}
\def\fps@table{tbp}
\def\ftype@table{2}
%% \def\ext@table{lot}
\def\fnum@table{\tablename~\thetable}
\newenvironment{table}
               {\floatenv\capwidth113.75mm
                \abovecaptionskip\z@\belowcaptionskip6H\@float{table}}
               {\end@float}
\newenvironment{table*}
               {\floatenv\capwidth113.75mm
                \abovecaptionskip\z@\belowcaptionskip6H\@dblfloat{table}}
               {\end@dblfloat}

\newlength\abovecaptionskip
\newlength\belowcaptionskip

\def\@floatboxreset {%
        \reset@font
        \small\tabcolsep.5zw
        \@setminipage
}

\def\floatenv{\let\center\Center}
\def\Center{\topsep\z@\parsep\z@\partopsep\z@\itemsep\z@
 \trivlist \centering\item\relax}
\def\endCenter{\endtrivlist}

\long\def\@caption#1[#2]#3{\par
% \addcontentsline{\csname ext@#1\endcsname}{#1}%
%  {\protect\numberline{\csname the#1\endcsname}{\ignorespaces #2}}%
 \begingroup
  \@parboxrestore
  \if@minipage
   \@setminipage
  \fi
  \small
  \@makecaption{\csname fnum@#1\endcsname}{\ignorespaces #3}\par
 \endgroup}

\newdimen\capwidth
\newdimen\abovecaptionskip
\newdimen\belowcaptionskip
\newbox\@tempboxb

\def\capnum@font{\small\gtfamily\sffamily}

\long\def\@makecaption#1#2{%
 \vskip\abovecaptionskip
 \setbox\@tempboxa\hbox{{\capnum@font #1}\hskip1zw\mbox{}}%
 \setbox\@tempboxb\hbox{\shortstack[l]{#2\inhibitglue}}%
 \@tempdimb\wd\@tempboxb
 \@tempdima\wd\@tempboxa
 \ifdim \@tempdimb=\z@
  \hbox to \hsize{\capnum@font\hss #1\inhibitglue\hss}\par
 \else
  \advance\@tempdima \@tempdimb % <
   \ifdim \@tempdima > \capwidth
    \hfil
    \begin{minipage}{.99\capwidth}%
     \hangindent=\wd\@tempboxa
     \leavevmode\box\@tempboxa\kern0mm #2\inhibitglue
    \end{minipage}\par
   \else
    \hbox to\hsize{\hss\box\@tempboxa
     \parbox[t]{\@tempdimb}{#2\inhibitglue}\hss}\par
   \fi
 \fi
 \vspace{\belowcaptionskip}%
}

\arraycolsep 5\p@     %% 5\p@
\tabcolsep .5zw       %% 6\p@
\arrayrulewidth .12mm %% .4\p@
\doublerulesep 2\p@
\tabbingsep \labelsep

\skip\@mpfootins = \skip\footins
\fboxsep .5zw         %% 3\p@
\fboxrule .12mm       %% .4\p@

\@addtoreset{equation}{chapter}
\renewcommand{\theequation}{%
  \ifnum\c@chapter>\z@\thechapter.\fi \@arabic\c@equation}
\def\@eqnnum{{\normalfont \normalcolor (\theequation)\hskip1zw\mbox{}}}

\if@enablejfam
  \DeclareSymbolFont{mincho}{JY1}{mc}{m}{n}
  \DeclareSymbolFontAlphabet{\mathmc}{mincho}
  \SetSymbolFont{mincho}{bold}{JY1}{gt}{m}{n}
  \DeclareMathAlphabet\mathgt{JY1}{gt}{m}{n}
  \reDeclareMathAlphabet{\mathrm}{\@mathrm}{\@mathmc}
  \reDeclareMathAlphabet{\mathbf}{\@mathbf}{\@mathgt}
  \jfam\symmincho
\else
  \DeclareRobustCommand{\mathmc}{%
    \@latex@error{Command \noexpand\mathmc invalid with\space
       `disablejfam' class option.}\@eha
  }
  \DeclareRobustCommand{\mathgt}{%
    \@latex@error{Command \noexpand\mathgt invalid with\space
       `disablejfam' class option.}\@eha
  }
\fi

\DeclareOldFontCommand{\mc}{\normalfont\mcfamily}{\mathmc}
\DeclareOldFontCommand{\gt}{\normalfont\gtfamily}{\mathgt}
\DeclareOldFontCommand{\rm}{\normalfont\rmfamily}{\mathrm}
\DeclareOldFontCommand{\sf}{\normalfont\sffamily}{\mathsf}
\DeclareOldFontCommand{\tt}{\normalfont\ttfamily}{\mathtt}
\DeclareOldFontCommand{\bf}{\normalfont\bfseries}{\mathbf}
\DeclareOldFontCommand{\it}{\normalfont\itshape}{\mathit}
\DeclareOldFontCommand{\sl}{\normalfont\slshape}{\@nomath\sl}
\DeclareOldFontCommand{\sc}{\normalfont\scshape}{\@nomath\sc}
\DeclareRobustCommand*{\cal}{\@fontswitch\relax\mathcal}
\DeclareRobustCommand*{\mit}{\@fontswitch\relax\mathnormal}

\setcounter{tocdepth}{2}
\newcommand{\@pnumwidth}{1.55em}
\newcommand{\@tocrmarg}{2.55em}
\newcommand{\@dotsep}{4.5}
\newdimen\toclineskip
\setlength\toclineskip{\z@}
\newdimen\@lnumwidth

\def\numberline#1{\hbox to\@lnumwidth{#1\hfil}}

\def\@dottedtocline#1#2#3#4#5{\ifnum #1>\c@tocdepth \else
  \vskip\toclineskip \@plus.2\p@
  {\leftskip #2\relax \rightskip \@tocrmarg \parfillskip -\rightskip
    \parindent #2\relax\@afterindenttrue
   \interlinepenalty\@M
   \leavevmode
   \@lnumwidth #3\relax
   \advance\leftskip \@lnumwidth \hbox{}\hskip -\leftskip
    {#4}\nobreak\leaders\hbox{$\m@th \mkern \@dotsep mu.\mkern \@dotsep
       mu$}\hfill \nobreak\hbox to\@pnumwidth{%
         \hss\reset@font\rmfamily \normalcolor #5}\par}\fi}

\def\addcontentsline#1#2#3{%
  \protected@write\@auxout
    {\let\label\@gobble \let\index\@gobble \let\glossary\@gobble
  \@temptokena{\thepage}}%
    {\string\@writefile{#1}%
       {\protect\contentsline{#2}{#3}{\the\@temptokena}}}%
}

\DeclareFixedFont{\tochead@font}{JY1}{gt}{m}{n}{24.88}% 32\@JQ

\def\@maketochead#1{\null %% 目次
 {\parindent\z@
  \boxmaxdepth\z@
  \lineskiplimit\z@
  \lineskip\z@
  \vbox to 4\Cvs{%
   \vss
   \vbox to \Cvs{\vss
    \hskip8mm
    {\tochead@font #1}%
   \vss}%
   \vss\vss\vss
  }\par
  \nobreak
 }%
}

\newcommand{\tableofcontents}{%
  \if@twocolumn\@restonecoltrue\onecolumn
  \else\@restonecolfalse\fi
  \@maketochead{\contentsname}\par
  \thispagestyle{plain}
  \@mkboth{\contentsname}{}%
  \@starttoc{toc}%
  \if@restonecol\twocolumn\fi
}

%%   \def\numberline##1{%
%%    \setbox\@tempboxb\hbox{##1}%
%%    \@lnumwidth\wd\@tempboxb
%%    \advance \@lnumwidth 1zw
%%    \hbox to \@lnumwidth{##1\hss}%
%%   }

\newcommand*{\l@part}[2]{%
  \ifnum \c@tocdepth >-2\relax
    \addpenalty{-\@highpenalty}%
    \addvspace{2.25em \@plus\p@}%
    \begingroup
    \parindent\z@\rightskip\@pnumwidth
    \parfillskip-\@pnumwidth
    {\leavevmode\large\bfseries
     \setlength\@lnumwidth{4zw}%
     #1\hfil\nobreak
     \hbox to\@pnumwidth{\hss#2}}\par
    \nobreak
    \global\@nobreaktrue
    \everypar{\global\@nobreakfalse\everypar{}}%
     \endgroup
  \fi}

\newcommand*{\l@chapter}[2]{%
 \ifnum \c@tocdepth >\m@ne
  \addpenalty{-\@highpenalty}%
  \addvspace{\Cvs}%
  \begingroup
   \parindent\z@
   \rightskip\@pnumwidth \parfillskip-\rightskip
   \leavevmode\bfseries
   \setlength\@lnumwidth{4zw}%
   \advance\leftskip\@lnumwidth \hskip-\leftskip
    #1\nobreak\hfil\nobreak\hbox to\@pnumwidth{\hss #2}\par
   \penalty\@highpenalty
  \endgroup
 \fi}

\newcommand*{\l@Chapter}[2]{%
% \addvspace{\Cvs}%
  \begingroup
   \parindent\z@
   \rightskip\@pnumwidth \parfillskip-\rightskip
   \leavevmode\bfseries
   \setlength\@lnumwidth{4zw}%
   \advance\leftskip\@lnumwidth \hskip-\leftskip
    #1\nobreak\hfil\nobreak\hbox to\@pnumwidth{\hss #2}\par
   \penalty\@highpenalty
  \endgroup
}

\newcommand*{\l@section}{\@dottedtocline{1}{1.5em}{2.3em}}
\newcommand*{\l@subsection}   {\@dottedtocline{2}{3.8em}{3.2em}}
\newcommand*{\l@subsubsection}{\@dottedtocline{3}{7.0em}{4.1em}}
\newcommand*{\l@paragraph}    {\@dottedtocline{4}{10em}{5em}}
\newcommand*{\l@subparagraph} {\@dottedtocline{5}{12em}{6em}}

%\newcommand{\listoffigures}{%
%  \if@twocolumn\@restonecoltrue\onecolumn
%  \else\@restonecolfalse\fi
%  \chapter*{\listfigurename
%  \@mkboth{\listfigurename}{\listfigurename}}%
%  \@starttoc{lof}%
%  \if@restonecol\twocolumn\fi
%}
%\newcommand*{\l@figure}{\@dottedtocline{1}{1.5em}{2.3em}}
%\newcommand{\listoftables}{%
%  \if@twocolumn\@restonecoltrue\onecolumn
%  \else\@restonecolfalse\fi
%  \chapter*{\listtablename
%  \@mkboth{\listtablename}{\listtablename}}%
%  \@starttoc{lot}%
%  \if@restonecol\twocolumn\fi
%}
%\let\l@table\l@figure

\newdimen\bibindent
\setlength\bibindent{1.5em}
\newcommand{\newblock}{\hskip .11em\@plus.33em\@minus.07em}

\newenvironment{thebibliography}[1]
 {\chapter*{\bibname}%
  \@mkboth{\bibname}{}%
  \addcontentsline{toc}{chapter}{\bibname}%
   \list{\@biblabel{\@arabic\c@enumiv}}%
        {\settowidth\labelwidth{\@biblabel{#1}}%
         \leftmargin\labelwidth
         \advance\leftmargin\labelsep
         \topsep\z@\parsep\z@\partopsep\z@\itemsep\z@
         \@openbib@code
         \usecounter{enumiv}%
         \let\p@enumiv\@empty
         \renewcommand\theenumiv{\@arabic\c@enumiv}}%
   \sloppy
   \clubpenalty\z@
   \@clubpenalty\clubpenalty
   \widowpenalty\z@
   \sfcode`\.\@m}
  {\def\@noitemerr
    {\@latex@warning{Empty `thebibliography' environment}}%
   \endlist}
\let\@openbib@code\@empty

\def\@biblabel#1{[#1]}

\def\@makeindexhead{\null %% 索引
 {\parindent\z@
  \boxmaxdepth\z@
  \lineskiplimit\z@
  \lineskip\z@
  \vbox to 4\Cvs{%
   \vss
   \hfil
   {\reference@font \kintou{4zw}{索引}}%
   \vss
  }\par
  \nobreak
 }%
}

\newenvironment{theindex}
 {\if@twocolumn\@restonecolfalse\else\@restonecoltrue\fi
  \columnseprule\z@
  \small
  \columnsep2zw
  \raggedbottom
  \twocolumn[\@makeindexhead]%
  \addcontentsline{toc}{Chapter}{\indexname}
  \@mkboth{\indexname}{}%
  \thispagestyle{jpl@in}%
  \parindent\z@
  \parskip\z@ 
  \let\item\@idxitem
 }
 {\if@restonecol\onecolumn\else\clearpage\fi}

\newcounter{KANA}
\def\theKANA{\KANAsymbol{KANA}}%
\def\KANAsymbol#1{\@KANAsymbol{\@nameuse{c@#1}}}
\def\@KANAsymbol#1{\ifcase#1\or ア\or カ\or サ\or タ\or ナ\or 
 ハ\or マ\or ヤ\or ラ\or ワ\else\@ctrerr\fi\relax}

\def\GYO{%
 \refstepcounter{KANA}%
 \pagebreak[2]
 \@tempskipa.5\baselineskip
 \vskip-\prevdepth
 \prevdepth\z@
 \vskip\baselineskip
 \advance\@tempskipa-\baselineskip
 \vspace*{\@tempskipa}\par
 \hskip3mm\smash{\lower1mm\hbox{\LARGE\bfseries \theKANA}}%
 \halflineskip
 \nopagebreak[4]%
}

\def\EGYO{% 
 \def\indexspace{\par\vskip\baselineskip}%
 \pagebreak[2]
 \@tempskipa.5\baselineskip
 \vskip-\prevdepth
 \prevdepth\z@
 \vskip\baselineskip
 \advance\@tempskipa-\baselineskip
 \vspace{1\baselineskip}
 \vspace*{\@tempskipa}\par
 \hskip3mm\smash{\lower1mm\hbox{\LARGE\bfseries 欧字}}
 \halflineskip
 \nopagebreak
}

\newcommand{\@idxitem}{\par\leavevmode\hangindent1zw}
\newcommand{\subitem}{\par\leavevmode\hangindent3zw\hspace*{2zw}}
\newcommand{\subsubitem}{\par\leavevmode\hangindent4zw\hspace*{3zw}}
%\newcommand{\indexspace}{\par \vskip 10\p@ \@plus5\p@ \@minus3\p@\relax}
\let\indexspace\relax

\renewcommand{\footnoterule}{%
  \kern-3\p@
  \hrule width 42.25mm % .4\columnwidth
  \kern 2.6\p@}

\@addtoreset{footnote}{chapter}

\def\@makefnmark{\penalty\@M
 \hbox{\@textsuperscript{\normalfont $\ast$\@thefnmark\mbox{\tiny ）}}}}

\newcommand\@makefntext[1]{%
 \everypar={\hangafter=\@ne \hangindent=2zw}%
 \mathindent4zw
 \parindent3zw
 \noindent\hbox to 2zw{\hss{\ttfamily *}\@thefnmark{}）}\hskip1zw
  #1}

%\def\thefootnote{\fnsymbol{footnote}}
%\def\@fnsymbol#1{\ensuremath{\ifcase#1\or *\or \dagger\or \ddagger\or
%   \mathchar "278\or \mathchar "27B\or \|\or **\or \dagger\dagger
%   \or \ddagger\ddagger \else\@ctrerr\fi}}

%%%\ProvidesFile{fleqn.clo}
%%%        [1998/08/17 v1.1c Standard LaTeX option
%%%                                   (flush left equations)]
\newdimen\mathindent
\AtEndOfClass{\mathindent2zw}
% \mathindent2zw
\renewcommand\[{\relax
                \ifmmode\@badmath
                \else
                  \@InListEnv
                  \begin{trivlist}%
                    \@beginparpenalty\predisplaypenalty
                    \@endparpenalty\postdisplaypenalty
                    \item[]\leavevmode
                    \hb@xt@\linewidth\bgroup $\m@th\displaystyle %$
                      \hskip\mathindent\bgroup
                \fi}
\renewcommand\]{\relax
                \ifmmode
                      \egroup $\hfil% $
                    \egroup
                  \end{trivlist}%
                \else \@badmath
                \fi}
\renewenvironment{equation}%
    {\@beginparpenalty\predisplaypenalty
     \@endparpenalty\postdisplaypenalty
     \refstepcounter{equation}%
     \@InListEnv
     \trivlist \item[]\leavevmode
       \hb@xt@\linewidth\bgroup $\m@th% $
         \displaystyle
         \hskip\mathindent}%
        {$\hfil % $
         \displaywidth\linewidth\hbox{\@eqnnum}%
       \egroup
     \endtrivlist}
\renewenvironment{eqnarray}{%
    \stepcounter{equation}%
    \def\@currentlabel{\p@equation\theequation}%
    \global\@eqnswtrue\m@th
    \global\@eqcnt\z@
    \tabskip\mathindent
    \let\\=\@eqncr
    \@InListEnv
    \setlength\abovedisplayskip{\topsep}%
    \ifvmode
      \addtolength\abovedisplayskip{\partopsep}%
    \fi
    \addtolength\abovedisplayskip{\parskip}%
    \setlength\belowdisplayskip{\abovedisplayskip}%
    \setlength\belowdisplayshortskip{\abovedisplayskip}%
    \setlength\abovedisplayshortskip{\abovedisplayskip}%
    $$\everycr{}\halign to\linewidth% $$
    \bgroup
      \hskip\@centering
      $\displaystyle\tabskip\z@skip{##}$\@eqnsel&%
      \global\@eqcnt\@ne \hskip .8\arraycolsep \hfil${##}$\hfil&%
      \global\@eqcnt\tw@ \hskip .8\arraycolsep
        $\displaystyle{##}$\hfil \tabskip\@centering&%
      \global\@eqcnt\thr@@
        \hb@xt@\z@\bgroup\hss##\egroup\tabskip\z@skip\cr}%
      {\@@eqncr
    \egroup
    \global\advance\c@equation\m@ne$$% $$
    \@ignoretrue
    }
%%\endinput
%%
%% End of file `fleqn.clo'.

\def\@InListEnv{%
 \ifdim\topsep=\z@
  \@listi
 \fi
}

\def\@begintheorem#1#2{%
 \topsep\z@\parsep\z@\partopsep\z@\itemsep\z@
 \labelsep1zw
 \trivlist
   \item[\hskip \labelsep{\bfseries #1\ #2}]}% \itshape
\def\@opargbegintheorem#1#2#3{%
 \topsep\z@\parsep\z@\partopsep\z@\itemsep\z@
 \labelsep1zw
 \trivlist
   \item[\hskip \labelsep{\bfseries #1\ #2（#3）\inhibitglue}]}% \itshape
\def\@endtheorem{\endtrivlist}
%%\def\the(theorem){\unskip}% 番号を除くとき

\newif\if西暦 \西暦false
\def\西暦{\西暦true}
\def\和暦{\西暦false}
\def\today{{%
  \iftdir
    \if西暦
      \kansuji\number\year 年
      \kansuji\number\month 月
      \kansuji\number\day 日
    \else \@tempcnta\year
      \advance\@tempcnta-1988 平成
      \ifnum \@tempcnta =1 元\else\kansuji\number\@tempcnta\fi 年
      \kansuji\number\month 月\kansuji\number\day 日
    \fi
  \else
    \if西暦
      \number\year\, 年\hspace{.5em}\nobreak
      \number\month\, 月\hspace{.5em}\nobreak
      \number\day\, 日
    \else \@tempcnta\year
      \advance\@tempcnta-1988 平成
      \ifnum \@tempcnta =1 元\else\number\@tempcnta\fi
        \, 年\hspace{.5em}\nobreak
      \number\month\, 月\hspace{.5em}\nobreak\number\day\, 日
    \fi
  \fi}}

\newcommand{\prepartname}{第}
\newcommand{\postpartname}{部}
\newcommand{\prechaptername}{第}
\newcommand{\postchaptername}{章}
\newcommand{\contentsname}{目\hskip1zw 次}
%% \newcommand{\listfigurename}{図 目 次}
%% \newcommand{\listtablename}{表 目 次}
\newcommand{\bibname}{参考文献}% 関連図書
\newcommand{\indexname}{索\hskip1zw 引}
\newcommand{\figurename}{図}
\newcommand{\tablename}{表}
\newcommand{\appendixname}{付録}

\newcount\@minsofar
\newcount\@min
\newcount\@cite@temp
\def\@citex[#1]#2{%
\if@filesw \immediate \write \@auxout {\string \citation {#2}}\fi
\@tempcntb\m@ne \let\@h@ld\relax \def\@citea{}%
\@min\m@ne%
\@cite{%
  \@for \@citeb:=#2\do {\@ifundefined {b@\@citeb}%
    {\@h@ld\@citea\@tempcntb\m@ne{\bfseries ?}%
    \@warning {Citation `\@citeb ' on page \thepage \space undefined}}%
{\@minsofar\z@ \@for \@scan@cites:=#2\do {%
  \@ifundefined{b@\@scan@cites}%
    {\@cite@temp\m@ne}
    {\@cite@temp\number\csname b@\@scan@cites \endcsname \relax}%
\ifnum\@cite@temp > \@min% select the next one to list
    \ifnum\@minsofar = \z@
      \@minsofar\number\@cite@temp
      \edef\@scan@copy{\@scan@cites}\else
    \ifnum\@cite@temp < \@minsofar
      \@minsofar\number\@cite@temp
      \edef\@scan@copy{\@scan@cites}\fi\fi\fi}\@tempcnta\@min
  \ifnum\@minsofar > \z@ % some more
    \advance\@tempcnta\@ne
    \@min\@minsofar
    \ifnum\@tempcnta=\@minsofar %   Number follows previous--hold on to it
      \ifx\@h@ld\relax
        \edef \@h@ld{\@citea\csname b@\@scan@copy\endcsname}%
      \else \edef\@h@ld{\ifmmode{]～[}\else
       ]～[\fi\csname b@\@scan@copy\endcsname}%
      \fi
    \else \@h@ld\@citea\csname b@\@scan@copy\endcsname
          \let\@h@ld\relax
  \fi % no more
\fi}%
\def\@citea{],\penalty\@highpenalty\,[}}\@h@ld}{#1}}

%% from "cite.sty", with a little modified
\def\@cite#1#2{\leavevmode\unskip
  \ifnum\lastpenalty=\z@\penalty\@highpenalty\fi% highpenalty before
  \,$^{\mbox{\tiny [{\multiply\@highpenalty 3 #1% % triple-highpenalties within
      \if@tempswa,\penalty\@highpenalty\ #2\fi % and before note.
    }]}}$\spacefactor\@m}
%% end of cite.sty

\let\org@cite\@cite
\def\cite{\@ifnextchar[% ]
 {\@tempswatrue\let\@cite\org@cite\@citex}
 {\@tempswafalse\let\@cite\org@cite\@citex[]}}
\def\Cite{\@ifnextchar[% ]
 {\@tempswatrue\let\@cite\@Cite\@citex}
 {\@tempswafalse\let\@cite\@Cite\@citex[]}}

\def\@Cite#1#2{\leavevmode
  \ifnum\lastpenalty=\z@\penalty\@highpenalty\fi% highpenalty before
  [{\multiply\@highpenalty 3 #1%             % triple-highpenalties within
      \if@tempswa,\penalty\@highpenalty\ #2\fi % and before note.
    }]\spacefactor\@m}

\def\kintou#1#2{\leavevmode\hbox to #1{%
 \kanjiskip\z@ plus 1fill minus 1fill\xkanjiskip\kanjiskip #2\hfil}%
}

\AtBeginDocument{%
 \@ifundefined{maketag@@@}{}
  {%
   \@mathmargin\mathindent
   \multlinegap2zw
   \def\maketag@@@#1{\hbox{\m@th\normalfont #1}\hskip1zw}%
   \def\eqref#1{\textup{\tagform@{\ref{#1}}\unskip}}%
   %% from /usr/local/share/texmf/tex/latex/amslatex/amsmath.sty (v1.2c)
   \renewenvironment{equation*}{%
    \equation \nonumber
   }{%
    % \nonumber
    \endequation
   }%
  }%
}

\postbreakpenalty`（=10000
\prebreakpenalty`）=10000
\postbreakpenalty`［=10000
\prebreakpenalty`］=10000
\postbreakpenalty`〈=10000
\prebreakpenalty`〉=10000
\prebreakpenalty`"=10000
\prebreakpenalty`℃=10000
\prebreakpenalty`　=10000
\prebreakpenalty`・=10000
\prebreakpenalty`～=10000
\prebreakpenalty`ー=10000
\prebreakpenalty`？=10000
\prebreakpenalty`！=10000
\postbreakpenalty"5C=10000

\def\sloppy{\tolerance9999\hbadness9999\hfuzz.5\p@ \vfuzz.5\p@}
\hbadness4000
% \vbadness10001
\tolerance4000\pretolerance\m@ne
\clubpenalty\z@
\widowpenalty\z@
\displaywidowpenalty\z@
% \interdisplaylinepenalty\z@ % 100 
\predisplaypenalty\z@       % 1000

\pagestyle{headings}
\pagenumbering{arabic}
\raggedbottom
\if@twocolumn
  \twocolumn
  \sloppy
  \flushbottom
\else
  \onecolumn
\fi
\if@twoside
  \@mparswitchtrue
\else
  \@mparswitchfalse
\fi

\endinput
