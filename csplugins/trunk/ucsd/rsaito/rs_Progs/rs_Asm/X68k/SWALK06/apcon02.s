.data
xdef CAF
xdef CON1
xdef SCORE
xdef LIFE
xdef SMTCP
xdef VDISPWF
xref SPCTRL02  *spctrl02.s
xref SPCTRL02_2 *
xref SP015_1    *
xref SNASP      *
xref PCGSET02  *pcgset02.s
xref MSETUP    *mdrv02.s
.text
lea USERSP,sp
move.l #$81,d0
clr.l a1
trap #$0f
**reset ACON
lea CON0,a1
lea CON256,a2
ACRLOOP:
move.l #0,(a1)+
cmpa.l a2,a1
bcs ACRLOOP
**
***set SPCTRL
lea CON1,a1
lea SPCTRL02,a2
move.l a2,(a1)+
move.l #14,d0
CON1CL:
clr.l (a1)+
dbf d0,CON1CL
***
bsr PCGSET02 
bsr MSETUP
*//sets VDISP interrupt
move.l #$ffffffff,SMTC
clr.l d1
clr.l a1
move.l #$6c,d0
trap #$0f
move.l #$6c,d0
lea VDISPINT,a1
move.w #$00_01,d1
trap #$0f
*//
move.w #72,LIFE
move.w #0,SCORE
lea CON1,a0
move.l #$0000_3000,16(a0) *default x
move.l #$0000_af00,20(a0) *default y
move.l #$0201,d0
trap #$07

lea CON2,a1
lea SP015_1,a2
move.l a2,(a1)
lea CON3,a1
move.l a2,(a1)
lea CON4,a1
move.l a2,(a1)

LOOP:
*
move.l #0,d6            *basic part of apcon
bsr LOOP0               *basic part
*
move.w LIFE,d0          *life warning music
cmpi.w #32,d0
bcc LINDAN
move.l LDFLAG,d0
btst.l #0,d0
bne LINDAN
bset.l #0,d0
move.l d0,LDFLAG
move.l #$0402,d0
trap #$07
LINDAN:
  *activate tentoumushi 
move.w SCORE,d0
addi.w #2,d0
lea CON5,a1
lea SPCTRL02_2,a2
ATENTOUL:
move.l a2,(a1)
adda.l #64,a1
dbf.w d0,ATENTOUL



SKIPD:
move.l #$04,d0  *ESC key
move.w #$00,d1
trap #$0f
btst.l #1,d0
bne EXIT
move.w LIFE,d0  *life
tst.w d0
beq LIFEOUT
move.l #$3b,d0
move.w #1,d1
trap #$0f
btst.l #6,d0
bne LOOP
lea CON1,a0
move.l 28(a0),d0
bset.l #3,d0
move.l d0,28(a0)
move.l #$20,4(a0)
bra LOOP
LIFEOUT:
lea CON1,a1
move.l 28(a1),d0
bset.l #10,d0
move.l d0,28(a1)
move.l 32(a1),d0
cmpi.l #16,d0
bne LOOP
move.l #17,32(a1)
move.l #$0400,d0
trap #$07
bra LOOP

EXIT:
move.l #$0200,d0
trap #$07
move.l #$6c,d0
clr.l a1
clr.l d1
trap #$0f
move.l #$6a,d0
clr.l a1
trap #$0f
clr.w -(sp)
dc.w $ff0c
addq.l #2,sp
dc.w $ff00

**VDISP interrupt
VDISPINT:
movem.l d0-d7/a0-a6,-(sp)
move.w VDISPWF,d0
bne VDINT2
ori.w #$07_00,sr
movea.l SMTCP,a1
VDINT0:
movea.l (a1),a0
move.l a0,d0
bmi VDINT1
adda.l #4,a1
move.w (a1)+,d0
move.w d0,(a0)
bra VDINT0
VDINT1:
move.l a1,SMTCP
VDINT2:
movem.l (sp)+,d0-d7/a0-a6
rte


APSET:*input d0,d1(doesn't save registers)


***active controller
LOOP0:
lea CON0,a0
lea CON256,a1
move.l #0,d7
ACLOOP:
move.l (a0),d0
beq NCAA
move.l d7,d0
movea.l (a0),a2
movem.l d0-d7/a0-a6,-(sp)
jsr (a2)
movem.l (sp)+,d0-d7/a0-a6
NCAA:
addi.l #1,d7
adda.l #64,a0
cmpa.l a1,a0
bcs ACLOOP
rts

.data
CAF:dc.l %00000000_00000000_00000000_00000000
    dc.l %00000000_00000000_00000000_00000000
    dc.l 0,0,0,0,0,0


SCORE:dc.w 0
LIFE: dc.w 0
LDFLAG:dc.l 0 *life warning music turn on flag
SMTCP:dc.l SMTC

ACSUB0:dc.l SPCTRL02_2
       dc.l 0
       dc.l 0
       dc.l 0


.bss
CON0:dc.l 0
      ds.b 60
CON1:dc.l 0
      ds.b 60
CON2:dc.l 0
      ds.b 60
CON3:dc.l 0
      ds.b 60
CON4:dc.l 0
      ds.b 60
CON5:dc.l 0
      ds.b 60
ds.b 64*250
CON256:***ACON end

ds.l 4096
SMTC:dc.l 0 *write $ffff_ffff
VDISPWF:dc.w 0

ds.l 16634
USERSP:

