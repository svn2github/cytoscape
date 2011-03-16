.data
xdef GRAPHW
xdef SCROLLER
xdef CHFIL
xdef PUTEN
.text
*move.l #$b3,d0
*move.b #$01,d1
*move.w SCROLL,d2
*clr.l d3
*trap #$0f
SCROLLER:
bsr GRAPHW        *write graphic one line
move.w SCROLL,d0  *add scroll register
addi.l #1,d0
cmpi.l #512,d0
bcs SKIPD
move.l #0,d0
SKIPD:
move.w d0,SCROLL
VDISPL:          *wait for VDISP High*
move.b $e88001,d1                    *
btst.l #4,d1                         *
beq VDISPL                           *
VDISPL2:         *wait for VDISP Low *
move.b $e88001,d1                    *
btst.l #4,d1                         *
bne VDISPL2                          *
move.w d0,$e80018 *write on scroll I/O
move.l BLOCK,d0
cmpi.l #528,d0    ***
bcs RETURN
move.l #0,BLOCK   ***
RETURN:
rts

GRAPHW:   *write graphic one line
lea MAPDATA,a2
move.l BLOCK,d1
***lsl.l #2,d1
adda.l d1,a2
clr.l d7
LOOP0:
lea SECDATA0,a1
clr.l d1
move.b (a2),d1 ***move.w
lsl.l #8,d1
move.l LOCAT,d0
add.l d0,d1
adda.l d1,a1
clr.l d6
LOOP1:
move.l d7,d0
lsl.l #4,d0
add.l d6,d0
lsl.l #8,d0
lsl.l #2,d0
add.l START,d0
movea.l d0,a3
clr.l d0
move.b (a1),d0
move.w d0,(a3)
adda.l #16,a1
addi.l #1,d6
cmpi.l #16,d6
bcs LOOP1
adda.l #1,a2 ***(#2)
addi.l #1,d7
cmpi.l #16,d7
bcs LOOP0
move.l START,d0
addi.l #2,d0
cmpi.l #$c00400,d0
bcs SKIP0
move.l #$c00000,d0
SKIP0:
move.l d0,START
move.l LOCAT,d0
addi.l #1,d0
cmpi.l #16,d0
bcs SKIP1
move.l BLOCK,d0
addi.l #16,d0
move.l d0,BLOCK
move.l #0,d0
SKIP1:
move.l d0,LOCAT
*return
rts
*
*
CHFIL:  *write a square on text screen
movem.l d0-d7/a0-a6,-(sp)
clr.l d2
movea.l d0,a1 
CHFILL:
move.w d1,(a1)
adda.l #$80,a1
addi.l #1,d2
cmpi.l #8,d2 *
bcs CHFILL
movem.l (sp)+,d0-d7/a0-a6
rts
*
PUTEN: *indicate gage on text screen
movem.l d0-d7/a0-a6,-(sp)
move.l d0,d5 *
move.l d0,d1
lsr.l #4,d1
move.l d1,d2
move.l #$e41000,d0 *
FILBO:
subi.l #1,d2
bmi N1FIL
move.l #$ffff,d1
bsr CHFIL
addi.l #2,d0 *
bra FILBO
N1FIL:
move.l d5,d2
lsr.l #4,d2
lsl.l #4,d2
move.l d5,d1
sub.l d2,d1
move.l #$1_0000,d2
lsr.l d1,d2
sub.w #1,d2
eori.w #$ffff,d2
move.l d2,d1
bsr CHFIL
addi.l #2,d0
N2FIL:
cmpi.l #$e41020,d0
bcc N3FIL
clr.l d1
bsr CHFIL
addi.l #2,d0
bra N2FIL
N3FIL:
movem.l (sp)+,d0-d7/a0-a6
rts
*
*
*
.data
BLOCK:dc.l 0
LOCAT:dc.l 0
START:dc.l $c003fe
SCROLL:dc.w 0
include secd011.s
.bss
