.data
xdef FMRESET
xref TRAP15
xref NFMDRV
.text
*reset to channel d6
FMRESET:
movem.l d0-d7/a0-a6,-(sp)

FMRE:
move.b #$8,d1
move.l d6,d2
KONR:
bsr OPMSET
dbf.l d2,KONR

ifdef MCRRESET
move.l #0,d3
FMR0:
move.b #$20,d1
add.l d3,d1
move.b #0,d2
FMR:
bsr OPMSET
addi.b #8,d1
bcc FMR
addi.l #1,d3
move.w d6,d0
cmp.w d3,d0
bcc FMR0
endif
*
lea RADB,a1      *<
FMR1:
move.b (a1)+,d1
bmi RRMAX
bsr OPMSET
bra FMR1

RRMAX:
move.l #0,d3
RMA:
move.l #$e0,d1
add.l d3,d1
move.l #15,d2
RRMA:
bsr OPMSET
addi.b #8,d1
bcc RRMA
addi.w #1,d3
move.w d6,d0
cmp.w d3,d0
bcc RMA

movem.l (sp)+,d0-d7/a0-a6
rts

OPMSET:
move.l #$68,d0
bsr TRAP15
rts

.data
RADB:*dc.b 1,8,$f
     *dc.b $10,$11,$12,$14
     dc.b $18,$19,$1b,$ff
     even
