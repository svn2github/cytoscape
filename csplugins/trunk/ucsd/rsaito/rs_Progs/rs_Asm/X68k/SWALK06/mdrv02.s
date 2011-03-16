.data
xdef MSETUP
xref MCC
xref CHENA
xref INSTSET
xref SPMSC0
xref STKBUF0
xref PDMUSIC0
xref PMUSIC0
xref MCTRL
xref DPSYS

.text
***set up music driver
MSETUP:
***set FM interrupt***
move.w #0,MCC
move.l #$6a,d0
clr.l a1
trap #$0f

move.l #$14,d1
move.l #$15,d2
bsr OPMSET
clr.l d2
move.l #$10,d1
bsr OPMSET
move.l #$11,d1
bsr OPMSET
move.l #$6a,d0
lea MDRV,a1
trap #$0f

***set trap #$07   ***
move.l #$80,d0
move.w #$27,d1
lea MDRVINT,a1
trap #$0f
rts
*****
**FM interrupt
MDRV:
movem.l d0-d7/a0-a6,-(sp)
ori.w #$0700,sr
bsr MCTRL
move.l #$14,d1
move.l #$15,d2
bsr OPMSET
movem.l (sp)+,d0-d7/a0-a6
rte
**trap #$07
MDRVINT: *input d0
movem.l d1-d7/a0-a6,-(sp)
ori.w #$0700,sr
cmpi.l #$1_0000,d0
bcc EFFSC
move.w DPSYS,d1
beq DPNONB
move.l #$ffff_fff1,d0
bra MDRVIE
DPNONB:
move.w d0,MCC
clr.l d0
bra MDRVIE
EFFSC:
subi.l #$1_0000,d0
move.l d0,d1
lsr.l #8,d1
addi.l #$e0,d1
move.l #15,d2
RRMAL:
bsr OPMSET
addi.b #8,d1
bcc RRMAL  **Release Rate up to max
**key off
move.l d0,d2
lsr.l #8,d2
move.w #8,d1
bsr OPMSET
*set instrument data
lea EFFSD0,a2
move.l d0,d1
andi.l #$ff,d1
lsl.l #3,d1
adda.l d1,a2
movea.l (a2),a2
move.l d0,d7
lsr.l #8,d7
addi.l #$1_0000,d7
bsr INSTSET
*set stack buffer for music driver
lea SPMSC0,a0
move.l d0,d1
lsr.l #6,d1
adda.l d1,a0
lea STKBUF0,a1
move.l d0,d1
lsr.l #8,d1
move.l d1,d2
lsl.l #4,d1
lsl.l #6,d2
add.l d2,d1
adda.l d1,a1
move.l a1,(a0)
**
move.l d0,d1
andi.l #$ff,d1
lsl.l #3,d1
lea EFFSD0,a1
adda.l d1,a1
move.l d0,d2
lsr.l #8,d2
lsl.l #2,d2
lea PDMUSIC0,a2
move.l 4(a1),(a2,d2)
lea PMUSIC0,a2
move.l 4(a1),(a2,d2)
move.l d0,d1
lsr.l #8,d1
move.l CHENA,d2
bset.l d1,d2
move.l d2,CHENA
MDRVIE:
movem.l (sp)+,d1-d7/a0-a6
rte

OPMSET:
move.l d0,-(sp)
move.l #$68,d0
trap #$0f
move.l (sp)+,d0
rts

.data
include temd102.s
