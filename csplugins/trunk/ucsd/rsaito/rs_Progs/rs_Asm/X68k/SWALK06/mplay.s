***演奏サブルーチン***
.data
xdef SLOT0
xdef COM
xdef PDMUSIC0
xdef NFMDRV
xdef MPLAY
xref TRAP15
xdef ALPHA *外部からの呼び出しは危険
xdef BETA  *          〃
xdef KEYOFFW
xdef PMUSIC0
xdef SPDD
xdef SPDC
xdef WMUSIC0
xdef MFLAG0
xdef CHENA
xdef SPMSC0
xdef STKBUF0
xdef MFLAG0
.text
MPLAY:
movem.l d0-d7/a0-a6,-(sp)
move.w COM,d0
cmpi.w #$02,d0
bne SKIP1
move.w NFMDRV,d2
move.b #$08,d1
LOOP1:
bsr OPMSET
dbf d2,LOOP1
bra RETURN
SKIP1:
cmpi.w #$03,d0
bne SKIP2
move.w #$02,COM
move.w NFMDRV,d5
lea PDMUSIC0,a3
lea PMUSIC0,a2
lea SPMSC0,a0
lea STKBUF0,a1
LOOP2:
move.l (a3)+,(a2)+
move.l a1,(a0)+
adda.l #80,a1
dbf d5,LOOP2
move.w NFMDRV,d5
lea WMUSIC0,a2
LOOP3:
move.l #0,(a2)+
dbf d5,LOOP3
*move.w SPDD,SPDC***
bra RETURN
SKIP2:
cmpi.w #$01,d0
beq SKIP3
cmpi.w #$04,d0
beq SKIP3
bra RETURN
SKIP3:
subi.w #1,SPDC
beq SKIP4
bra RETURN
SKIP4:
move.w SPDD,SPDC
move.w COM,d0
cmpi.w #4,d0
bne SSKIP0
move.w NFMDRV,d7
andi.l #$ffff,d7
bra ALPHA
SSKIP0:
move.l #0,d7  *<<
SKIP5:
lea WMUSIC0,a2
bsr SADRC
adda.l d0,a2
move.l (a2),d0
move.l KEYOFFW,d1
cmp.l d0,d1
bne SKIP7
move.b #8,d1
move.b d7,d2
bsr OPMSET
SKIP6:
move.l (a2),d0
subi.l #1,d0
move.l d0,(a2)
ALPHA:
addi.l #1,d7
move.w NFMDRV,d0
cmp.w d7,d0
bcc SKIP5
move.l CHENA,d0
btst.l d7,d0
bne SKIP5
cmpi.w #8,d7
bne ALPHA
bra RETURN
SKIP7:
tst.l d0
bne SKIP6
BETA:
clr.l d0
clr.l d1
clr.l d2
lea PMUSIC0,a2
bsr SADRC
adda.l d0,a2  *<<
movea.l (a2),a3
move.b (a3),d1
bmi SKIP8
BSECT:
move.b d1,d2
move.b #$28,d1
add.b d7,d1
bsr OPMSET
move.b 1(a3),d1
subi.b #1,d1
andi.l #$ff,d1
lea WMUSIC0,a3
bsr SADRC
adda.l d0,a3
move.l d1,(a3)
addi.l #2,(a2)
lea SLOT0,a2
adda.l d7,a2
move.b (a2),d2
lsl.l #3,d2
add.l d7,d2
move.b #8,d1
bsr OPMSET
bra ALPHA
SKIP8:
cmpi.b #$ff,d1
bne SKIP9
bra ALPHA
SKIP9:
cmpi.b #$f8,d1
bne SKIP10
lea PMUSIC0,a2
bsr SADRC
adda.l d0,a2
addi.l #1,(a2)
bra BETA
SKIP10:
cmpi.b #$8e,d1
bne SKIP11
move.b 1(a3),d1
move.b 2(a3),d2
bsr OPMSET
addi.l #3,(a2)
bra BETA
SKIP11:
cmpi.b #$ec,d1
bne SKIP12
clr.l d1
move.b 1(a3),d1
subi.l #1,d1
addi.l #2,(a2)
lea WMUSIC0,a2
bsr SADRC
adda.l d0,a2
move.l d1,(a2)
bra ALPHA
SKIP12:
cmpi.b #$c0,d1
bne SKIP13
lea SPMSC0,a2
bsr SADRC
adda.l d0,a2
movea.l (a2),a1
adda.l #2,a3
move.l a3,-(a1)
suba.l #1,a3
move.b (a3),d0
andi.w #$00ff,d0
move.w d0,-(a1)
subi.l #6,(a2)
lea PMUSIC0,a2
bsr SADRC
adda.l d0,a2
addi.l #2,(a2)
bra BETA
SKIP13:
cmpi.b #$a0,d1
bne SKIP14  *拡張用
lea SPMSC0,a0
bsr SADRC
adda.l d0,a0
movea.l (a0),a1
move.w (a1)+,d1
move.l (a1)+,d2
cmpi.w #$ff,d1
beq TESEC
subi.w #1,d1
bne KISEC
addi.l #6,(a0)
addi.l #1,(a2)
bra BETA
KISEC:
move.l d2,-(a1)
move.w d1,-(a1)
TESEC:
move.l d2,(a2)
bra BETA
SKIP14:
cmpi.b #$c1,d1
bne SKIP15
move.b 1(a3),d1
andi.l #$ff,d1
lea MFLAG0,a3
bsr SADRC
adda.l d0,a3
move.l d1,(a3)
addi.l #2,(a2)
bra BETA
SKIP15:
move.l #$21,d0
lea ERRM,a1
bsr TRAP15
DLOOP:
bra DLOOP
*
RETURN:
movem.l (sp)+,d0-d7/a0-a6
rts *(rte)
*
SADRC:
move.l d7,d0
lsl.l #2,d0
rts
OPMSET:
move.l #$68,d0
bsr TRAP15
rts
*
.data
ERRM:dc.b '未定義楽譜コードを使用しました',0
even
SPDD:dc.w 1 *<<<<<<<<
SPDC:dc.w 1
NFMDRV:dc.w 0
KEYOFFW:dc.l 3 *<<<<<<<<
SLOT0:ds.b 8
MFLAG0:ds.l 8
even
CHENA:dc.l 0
COM:dc.w 3
.bss
PMUSIC0:ds.l 8
PDMUSIC0:ds.l 8
WMUSIC0:ds.l 8
SPMSC0:ds.l 8
ds.l 20 *第一スタックバッファ
STKBUF0:
ds.l 20*7 *残りのバッファ
