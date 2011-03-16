***ＦＭ音源コントローラー***
.data
xdef PMDATA0
xdef MDATA0
xdef MCTRL
xdef MCC
xdef DPSYS
xref FMRESET
xref NFMDRV
xref INSTSET
xref PDMUSIC0
xref COM
xref MPLAY
xref SPDD
xref SPDC
xref MFLAG0
.text
MCTRL:
movem.l d1-d7/a0-a6,-(sp)
***
move.w DPSYS,d0
beq NONDP
movea.l REGISSP,a6
movem.l (a6)+,d0-d7/a0-a5
move.w #0,DPSYS
bra DPSKIP
***
NONDP:
move.w MCC,d0
btst.l #10,d0
beq SKIP2
move.w SPDC,d1
cmp.w SPDD,d1
bne SKIP1
move.w MTLF,d1
add.w d1,MTL
move.w NFMDRV,d3
LOOP1:
move.l #$78,d1
add.l d3,d1
move.b MTL,d2
move.l #$68,d0
trap #$0f
dbf d3,LOOP1
cmpi.b #48,d2 *<<
bcs SKIP1
move.w MCC,d0
bclr.l #10,d0
bset.l #09,d0
move.w d0,MCC
move.w #0,MTL
SKIP1:
bra PLAY
SKIP2:
btst.l #9,d0
beq SKIP3
move.w NFMDRV,d6
bsr FMRESET
move.w MCC,d0
andi.l #$ff,d0
lea PMDATA0,a0
lsl.l #2,d0
adda.l d0,a0
movea.l (a0),a1
move.w (a1)+,d6
bsr FMRESET *
move.w d6,NFMDRV
move.l #0,d7
LOOP2:
move.w (a1)+,d0
lsl.l #2,d0
lea PINST0,a2
adda.l d0,a2
movea.l (a2),a2
bsr INSTSET
***
move.w #$ffff,DPSYS
lea REGISS,a6
movem.l d0-d7/a0-a5,-(a6)
move.l a6,REGISSP
bra RETURNDP
***
DPSKIP:
addi.l #1,d7
cmp.l d7,d6
bcc LOOP2
move.l #0,d7
lea PDMUSIC0,a0
LOOP3:
move.l (a1)+,(a0)+
addi.l #1,d7
cmp.l d7,d6
bcc LOOP3
move.b #1,MCC
move.w #3,COM
bsr MPLAY
bra RETURN
SKIP3:
btst.l #8,d0
beq SKIP4
PLAY:
move.w MCC,d0
bclr.l #11,d0
move.w d0,MCC
move.w #1,COM
bsr MPLAY
bra RETURN
SKIP4:
move.w MCC,d0
btst.l #11,d0
bne MPOTHER
move.w #2,COM
bsr MPLAY
bset.l #11,d0
move.w d0,MCC
MPOTHER:
move.w #4,COM
bsr MPLAY
RETURN:
lea MFLAG0,a1
move.l a1,d0
RETURNDP:
movem.l (sp)+,d1-d7/a0-a6
rts     *(rte)

.data
include MDATA5.s
even
MCC:dc.w 0
MTL:dc.w 0
MTLF:dc.w 32
DPSYS:dc.w 0
.bss
ds.l 20
REGISS:
REGISSP:dc.l 0
