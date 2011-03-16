** works under original active process controller
.data
WLKSPD = $100  *$100
JUMPWR = $600  *$600
JUMPKK = $080  *$080
OGRAVT = $040  *$040
STEPPI = $010  *$010
PUNCHT = $005
CODEZERO = $20

pcgnc macro data,addr
move.w addr,d0
move.b data,d0
move.w d0,addr
endm
xdef SPCTRL02
xdef SPCTRL02_2
xdef SP015_1
xdef SCOREPUT
xdef PRTBGC
xdef SNASP
xref SCROLLER  *SCROLL02.s
xref PUTEN     *SCROLL02.s
xref CAF       *apcon02.s
xref CON1      *apcon02.s
xref LIFE      *apcon02.s
xref SCORE    *apcon02.s
xref SMTCP    *apcon02.s
xref VDISPWF  *apcon02.s
***PCG data is set in PCGSET02.s
.text
SPCTRL02:
*
move.l 28(a0),d1
btst.l #10,d1
bne B_SEC  *checks whether life is out or not
btst.l #3,d1
beq SKIP1
bclr.l #1,d1
move.l d1,28(a0)
move.l 4(a0),d0
subi.l #1,d0
move.l d0,4(a0)
bne B_SEC
move.l d1,d0
bclr.l #3,d0
move.l d0,28(a0)
bra B_SEC
SKIP1:
btst.l #1,d1
beq SKIP2
move.l 8(a0),d0
subi.l #1,d0
move.l d0,8(a0)
bne B_SEC
move.l d1,d0
bclr.l #1,d0
move.l d0,28(a0)
bra B_SEC
SKIP2:
move.l #$3b,d0
move.w #0,d1
trap #$0f
btst.l #6,d0
beq SKIP3
move.l 28(a0),d0
bclr.l #7,d0
move.l d0,28(a0)
bra SKIP4
SKIP3:
move.l 28(a0),d0
btst.l #7,d0
bne SKIP4
bset.l #7,d0
move.l d0,28(a0)
move.l #PUNCHT,8(a0)  **<<
bset.l #1,d0
move.l d0,28(a0)
bra B_SEC
SKIP4:
move.l 28(a0),d3
btst.l #2,d3
bne C_SEC
move.l #$3b,d0
move.w #0,d1
trap #$0f
btst.l #5,d0
bne A_SEC
bset.l #2,d3
bchg.l #0,d3
btst.l #2,d0
bne SKIP5
bset.l #6,d3
bclr.l #5,d3
bset.l #4,d3
bra SKIP7
SKIP5:
btst.l #3,d0
bne SKIP6
bclr.l #6,d3
bset.l #5,d3
bclr.l #4,d3
bra SKIP7
SKIP6:
andi.l #$ffff_ff9f,d3
SKIP7:
move.l d3,28(a0)
move.l #JUMPWR,12(a0)  **<<
bra B_SEC
A_SEC:
btst.l #2,d0
bne SKIP9
move.l 28(a0),d0
bset.l #4,d0
move.l d0,28(a0)
move.l 16(a0),d0
subi.l #WLKSPD,d0 **<<
move.l d0,16(a0)
lsr.l #8,d0
andi.l #$0000_03ff,d0
cmpi.l #17,d0
bcc SKIP8
move.l #17,d0
lsl.l #8,d0
move.l d0,16(a0)
bra D_SEC
SKIP8:
move.l 24(a0),d0
addi.l #1,d0
move.l d0,24(a0)
cmpi.l #STEPPI,d0  **<<
bcs D_SEC
move.l #0,24(a0)
move.l 28(a0),d0
bchg.l #0,d0
move.l d0,28(a0)
bra D_SEC
SKIP9:
btst.l #3,d0
bne D_SEC
move.l 28(a0),d0
bclr.l #4,d0
move.l d0,28(a0)
move.l 16(a0),d0
addi.l #WLKSPD,d0  **<<
move.l d0,16(a0)
lsr.l #8,d0
cmpi.l #128,d0
bcs SKIP8
move.l #127,d0
lsl.l #8,d0
move.l d0,16(a0)
move.w #1,SCROLA
bsr SCROLLER
bsr SPRS
bra SKIP8
B_SEC:
move.l 28(a0),d0
btst.l #2,d0
beq D_SEC
C_SEC:
move.l 20(a0),d0
move.l 12(a0),d1
sub.l d1,d0
subi.l #OGRAVT,d1
move.l d1,12(a0)
move.l d0,20(a0)
lsr.l #8,d0
cmpi.l #176,d0
bcs SKIP09
move.l #175,d0
lsl.l #8,d0
move.l d0,20(a0)
move.l 28(a0),d0
bclr.l #2,d0
move.l d0,28(a0)
bra D_SEC
SKIP09:
move.l 28(a0),d3
btst.l #6,d3
beq SKIP10
move.l 16(a0),d0
subi.l #JUMPKK,d0
move.l d0,16(a0)
lsr.l #8,d0
cmpi.l #17,d0
bcc D_SEC
move.l #17,d0
lsl.l #8,d0
move.l d0,16(a0)
SKIP10:
btst.l #5,d3
beq D_SEC
move.l 16(a0),d0
addi.l #JUMPKK,d0
move.l d0,16(a0)
lsr.l #8,d0
cmpi.l #128,d0
bcs D_SEC
move.l #127,d0
lsl.l #8,d0
move.l d0,16(a0)
move.w #1,SCROLA
bsr SCROLLER
bsr SPRS
D_SEC:
move.w SCROLA,d0 * time adjustment for non-scroll
move.w #0,SCROLA *
tst.w d0         *
bne SKIPSLA      *
bsr VDISPL       *
SKIPSLA:         
move.l 28(a0),d0
btst.l #10,d0  *life out check 
bne E_SEC
move.l #$3b,d0
clr.l d1
trap #$0f
ori.b #$f0,d0
cmpi.b #$fd,d0
bne D_SEC0
move.l 28(a0),d0
btst.l #1,d0
beq D_SEC2
move.l 28(a0),d0
btst.l #8,d0
beq D_SEC0
D_SEC2:
move.l 28(a0),d0
btst.l #2,d0
bne D_SEC0
btst.l #3,d0
bne D_SEC0
bset.l #8,d0
move.l d0,28(a0)
move.l 20(a0),d0
lsr.l #8,d0
addi.l #8,d0
bra D_SEC1
D_SEC0:
move.l 28(a0),d0
bclr.l #8,d0
move.l d0,28(a0)

move.l 20(a0),d0
lsr.l #8,d0
D_SEC1:
move.w d0,$eb0002
addi.l #16,d0
move.w d0,$eb000a
move.w d0,$eb001a
addi.l #16,d0
move.w d0,$eb0012

move.l 16(a0),d0
lsr.l #8,d0
move.w d0,$eb0000
move.w d0,$eb0008
move.w d0,$eb0010

move.l 28(a0),d0
btst.l #4,d0
beq SKIP11
ori.w #$4000,$eb0004
ori.w #$4000,$eb000c
ori.w #$4000,$eb0014
ori.w #$4000,$eb001c
move.l 16(a0),d0
lsr.l #8,d0
subi.l #16,d0
move.w d0,$eb0018
bra SKIP12
SKIP11:
andi.w #$3fff,$eb0004
andi.w #$3fff,$eb000c
andi.w #$3fff,$eb0014
andi.w #$3fff,$eb001c
move.l 16(a0),d0
lsr.l #8,d0
addi.l #16,d0
move.w d0,$eb0018
SKIP12:
move.l 28(a0),d0
btst.l #3,d0
beq SKIP13
bclr.l #1,d0
move.l d0,28(a0)
pcgnc #$0006,$eb000c
pcgnc #$0007,$eb0014
andi.w #$fffc,$eb001e
bra SKIP17
SKIP13:
btst.l #1,d0
beq SKIP14
pcgnc #$0003,$eb000c
ori.w #$0002,$eb001e
bra SKIP15
SKIP14:
andi.w #$fffc,$eb001e
pcgnc #$0001,$eb000c
SKIP15:
move.l 28(a0),d0
btst.l #8,d0
beq SKIP15_0
pcgnc #$0016,$eb0014
bra SKIP17
SKIP15_0:
move.l 28(a0),d0
btst.l #0,d0
bne SKIP16
pcgnc #$0002,$eb0014
bra SKIP17
SKIP16:
pcgnc #$0005,$eb0014
SKIP17:
**bsr VDISPL2
*move.w SCROLA,d0
*tst.w d0
*beq SKIP18
*bsr SCROLLER
*bsr SPRS
*move.w #0,SCROLA
*bra SKIP19
SKIP18:
*bsr VDISPL
SKIP19:
rts

E_SEC:
move.l 28(a0),d0
bclr.l #1,d0
move.l d0,28(a0)
move.w #511,$eb0000
move.w #511,$eb0002
move.w #2,$eb001e  **SP3 ON
move.l 32(a0),d0
cmpi.l #16,d0
bcc E_SEC_FALL
addi.l #1,d0
move.l d0,32(a0) *increase FALL
pcgnc #32,$eb000c
pcgnc #31,$eb0014
pcgnc #34,$eb001c
move.w #$0121,$eb0024 ***color & code
move.w #2,$eb0026
move.l 16(a0),d0
lsr.l #8,d0
move.w d0,$eb0008
move.w d0,$eb0010
move.l 20(a0),d0
lsr.l #8,d0
move.w d0,$eb001a
addi.w #16,d0
move.w d0,$eb000a
move.w d0,$eb0022
addi.w #16,d0
move.w d0,$eb0012
move.l 28(a0),d0
btst.l #4,d0
bne E_SEC2
move.l 16(a0),d0
lsr.l #8,d0
addi.w #16,d0
move.w d0,$eb0018
move.w d0,$eb0020
E_SEC1:
andi.w #$3fff,$eb000c
andi.w #$3fff,$eb0014
andi.w #$3fff,$eb001c
andi.w #$3fff,$eb0024
bra SKIP19
E_SEC2:
move.l 16(a0),d0
lsr.l #8,d0
subi.w #16,d0
move.w d0,$eb0018
move.w d0,$eb0020
E_SEC3:
ori.w #$4000,$eb000c
ori.w #$4000,$eb0014
ori.w #$4000,$eb001c
ori.w #$4000,$eb0024
bra SKIP19
E_SEC_FALL:
***move.l #0,32(a0)
pcgnc #29,$eb000c
pcgnc #28,$eb0014
pcgnc #30,$eb001c
move.w #0,$eb0026
move.l 16(a0),d0
lsr.l #8,d0
move.w d0,$eb0010
move.l 20(a0),d0
lsr.l #8,d0
addi.w #32,d0
move.w d0,$eb000a
move.w d0,$eb0012
move.w d0,$eb001a
move.l 28(a0),d0
btst.l #4,d0
bne E_SEC4
move.l 16(a0),d0
lsr.l #8,d0
addi.w #16,d0
move.w d0,$eb0008
addi.w #16,d0
move.w d0,$eb0018
bra E_SEC1
E_SEC4:
move.l 16(a0),d0
lsr.l #8,d0
subi.w #16,d0
move.w d0,$eb0008
subi.w #16,d0
move.w d0,$eb0018
bra E_SEC3


VDISPL:
move.b $e88001,d0
btst.l #4,d0
beq VDISPL
VDISPL2:
move.b $e88001,d0
btst.l #4,d0
bne VDISPL2
rts

SPRS:
movea.l #$eb0040,a1
SLOOP:
subq.w #1,(a1)
bcc SKIP1_0
move.w #0,(a1)
SKIP1_0:
adda.l #8,a1
cmpa.l #$eb0200,a1
bcs SLOOP
rts


***other moves of sprite
*tentoumushi
SPCTRL02_2:
move.l d0,d7    ***
move.l 4(a0),d0
btst.l #0,d0
bne SKIP1_2
move.l #128,d5
move.l #8,d6
lea SPACT,a1
bsr SNASP
cmpi.w #$ffff,d6
beq SKIP5_2
move.w d6,12(a0)
bset.l d1,d2
move.l d2,(a1,d0)  *sp-active on
move.l #$7f,d0     *creates random Y
trap #$0f
andi.l #$ff,d0
mulu #17,d0
andi.l #$b0,d0
move.l d7,d1
lsl.l #5,d1
add.l d1,d0
andi.l #$ff,d0
move.l d0,d3  *Y
move.l #$c6,d0
move.l d6,d1
bset.l #31,d1
move.l #272,d2 *X
lsl.l #3,d7 **<
add.l d7,d2
lsr.l #3,d7 **<
move.w #$130,d4 *color&code
move.l #2,d5
trap #$0f
move.l 4(a0),d0
bset.l #0,d0
move.l d0,4(a0)
SKIP1_2:

move.l 4(a0),d0   *テントウムシはやられたか？
btst.l #1,d0      *b1-b3はやられた後の動作進行ぐあい
beq SKIP1__2
subi.l #1,8(a0)
bne RETURN1
move.l 4(a0),d0
btst.l #2,d0
bne SKIP1__3
bset.l #2,d0
move.l d0,4(a0)
move.l #3,8(a0)
clr.l d0
move.w 12(a0),d0
lsl.l #3,d0
addi.l #$eb0000,d0
movea.l d0,a1
pcgnc #25,4(a1)
bra RETURN1
SKIP1__3:
btst.l #3,d0
bne SKIP3_2
bset.l #3,d0
move.l d0,4(a0)
move.l #3,8(a0)
clr.l d0
move.w 12(a0),d0
lsl.l #3,d0
addi.l #$eb0000,d0
movea.l d0,a1
pcgnc #26,4(a1)
bra RETURN1


SKIP1__2:
clr.l d0
move.w 12(a0),d0   ***
lsl.l #3,d0
addi.l #$eb0000,d0
movea.l d0,a1
move.w (a1),d0
subi.w #1,d0
bcs SKIP3_2
*
move.w #1,VDISPWF *write on I/O using VDISPINT
movea.l SMTCP,a2
move.w d0,-(a2)
move.l a1,-(a2)
move.l a2,SMTCP
move.w #0,VDISPWF
*
move.w 12(a0),d0
lsl.l #8,d0
move.b #3,d0
move.l #$0008_fff0,d1
move.l #$0004_fff0,d2
bsr COLLD
tst.w d0
beq SKIP2_2
lea CON1,a1  ***
move.l 28(a1),d0
btst.l #1,d0
beq SKIP2_2  *bne SKIP3_2
move.l #$1_0502,d0
trap #$07
clr.l d1
move.w SCORE,d1
addi.w #1,d1
move.w d1,SCORE
move.l #$e080,d0
clr.l d1
move.w SCORE,d1
bsr SCOREPUT

move.l 4(a0),d0 *やられ信号
bset.l #1,d0
move.l d0,4(a0)
move.l #3,8(a0)
clr.l d0
move.w 12(a0),d0
lsl.l #3,d0
addi.l #$eb0000,d0
movea.l d0,a1
pcgnc #24,4(a1)

bra RETURN1
   ***bra SKIP3_2
SKIP2_2:
lea CON1,a1
move.w 12(a0),d0
lsl.l #8,d0
move.b #0,d0
move.l #$0010_fff0,d1
move.l #$0030_fff0,d2
move.l 28(a1),d3
btst.l #8,d3
beq SKIP_2_2
move.l #$0028_fff0,d2
SKIP_2_2:
bsr COLLD
tst.w d0
beq RETURN1
move.l 28(a1),d0
btst.l #3,d0 *
bne RETURN1  *
bset.l #3,d0
move.l d0,28(a1)
move.l #$30,4(a1) *INJ
move.l #$1_0603,d0
trap #$07
move.w LIFE,d0
subi.w #9,d0
bpl SKIP__2
clr.l d0
SKIP__2:
move.w d0,LIFE
bsr PUTEN
bra RETURN1
SKIP3_2:
move.l #0,(a0) *active off
SKIP4_2:
clr.l d0
move.w 12(a0),d0
bsr GETABIT
lea SPACT,a1
move.l (a1,d1),d2
bclr.l d0,d2
move.l d2,(a1,d1)
move.l #0,4(a0)
move.w 12(a0),d0
lsl.l #3,d0
addi.l #$eb0000,d0
movea.l d0,a1
move.w #0,6(a1)
SKIP5_2:
move.l #0,(a0) *active off
RETURN1:
rts
*****
*tobimushi
SP015_1:
move.l d0,d7 *
move.l #128,d5
move.l #8,d6
lea SPACT,a1
bsr SNASP
cmpi.w #$ffff,d6 *
bne SP015_1_1
clr.l (a0)
rts
SP015_1_1:
move.w d6,12(a0)
bset.l d1,d2
move.l d2,(a1,d0)
move.l d6,d5 *
lsl.l #3,d6
addi.l #$eb0000,d6
move.l d6,24(a0)
**make random Y
move.l #$7f,d0
trap #$0f
andi.l #$ff,d0
lsl.l #4,d0
move.l d7,d1
lsl.l #5,d1
add.l d1,d0
andi.l #$e0,d0
**
move.l d0,d3 *Y
move.l #$c6,d0
move.l d5,d1
bset.l #31,d1
move.l #272,d2
lsl.l #3,d7 *
add.l d7,d2
lsr.l #3,d7 *
move.w #$01_31,d4
move.l #2,d5
trap #$0f
lea SP015_2,a1
move.l a1,(a0)
move.l #0,4(a0)
move.w #0,16(a0)
move.w #0,18(a0)
SP015_2:
movea.l 24(a0),a1 *
move.w (a1),d4
subi.w #2,d4 *
bcs SP015_SPAOFF
move.w 16(a0),d1 *
btst.l #8,d1 *up-down check
bne SP015_2_1
move.w 2(a1),d3
addi.w #1,d3 *
addi.b #1,d1
move.w d1,16(a0)
cmpi.b #12,d1
bcs SP015_2_2
move.w #$100,16(a0)
pcgnc #49,4(a1)
bra SP015_2_2
SP015_2_1:
move.w 2(a1),d3
subi.w #1,d3 *
addi.b #1,d1
move.w d1,16(a0)
cmpi.b #12,d1
bcs SP015_2_2
move.w #0,16(a0)
pcgnc #50,4(a1)
SP015_2_2:
move.w #1,VDISPWF
movea.l SMTCP,a2
move.w d4,-(a2)
move.l a1,-(a2)
adda.l #2,a1
move.w d3,-(a2)
move.l a1,-(a2)
move.l a2,SMTCP
move.w #0,VDISPWF
*punch hit decision
move.w 12(a0),d0
lsl.l #8,d0
move.b #3,d0
move.l #$0008_fff0,d1
move.l #$0004_fff0,d2
bsr COLLD
tst.w d0
beq SP015_2_3
lea CON1,a1
move.l 28(a1),d0
btst.l #1,d0
beq SP015_2_3
*punch hit
move.l #$1_0502,d0
trap #$07
clr.l d1
move.w SCORE,d1
addi.w #1,d1
move.w d1,SCORE
move.l #$e080,d0
bsr SCOREPUT
move.l #3,8(a0)
movea.l 24(a0),a1
pcgnc #24,4(a1)
lea SP015_3,a1
move.l a1,(a0)
bra SP015_RET
*main chara damage decision
SP015_2_3:
lea CON1,a1
move.w 12(a0),d0
lsl.l #8,d0
move.b #0,d0
move.l #$0010_fff0,d1
move.l #$0030_fff0,d2
move.l 28(a1),d3
btst.l #8,d3
beq SP015_2_4
move.l #$0028_fff0,d2
SP015_2_4:
bsr COLLD
tst.w d0
beq SP015_RET
move.l 28(a1),d0
btst.l #3,d0
bne SP015_RET
*main chara damaged
bset.l #3,d0
move.l d0,28(a1)
move.l #$30,4(a1)
move.l #$1_0603,d0
trap #$07
move.w LIFE,d0
subi.w #3,d0
bpl SP015_2_5
clr.l d0
SP015_2_5:
move.w d0,LIFE
bsr PUTEN
bra SP015_RET

SP015_SPAOFF:
clr.l d0
move.w 12(a0),d0
bsr GETABIT
lea SPACT,a1
move.l (a1,d1),d2
bclr.l d0,d2
move.l d2,(a1,d1)
movea.l 24(a0),a1
move.w #0,6(a1)

SP015_AOFF:
move.l #SP015_1,(a0)
move.l #0,4(a0)

SP015_RET:
rts

SP015_3:
subi.l #1,8(a0)
bne SP015_RET
move.l 4(a0),d0
btst.l #2,d0
bne SP015_3_1
bset.l #2,d0
move.l d0,4(a0)
move.l #3,8(a0)
movea.l 24(a0),a1
pcgnc #25,4(a1)
bra SP015_RET
SP015_3_1:
btst.l #3,d0
bne SP015_SPAOFF
bset.l #3,d0
move.l d0,4(a0)
move.l #3,8(a0)
movea.l 24(a0),a1
pcgnc #26,4(a1)
bra SP015_RET

*
GETABIT:  *input d0-number output d0-bit,d1-?th*4
move.l d0,d6
lsr.l #5,d0
move.l d0,d1
lsl.l #5,d0
move.l d6,d2
sub.l d0,d2
move.l d2,d0
lsl.l #2,d1
rts
*
COLLD:
movem.l d1-d7/a0-a6,-(sp)
clr.l d3
move.b d0,d3
lsl.l #3,d3
addi.l #$eb0000,d3
clr.l d4
lsr.l #8,d0
move.b d0,d4
lsl.l #3,d4
addi.l #$eb0000,d4
movea.l d3,a3
movea.l d4,a4
move.w (a3),d3
move.w (a4),d4
sub.w d3,d4
cmp.w d1,d4
bmi COLLDN
swap d1
cmp.w d4,d1
bmi COLLDN
move.w 2(a3),d3
move.w 2(a4),d4
sub.w d3,d4
cmp.w d2,d4
bmi COLLDN
swap d2
cmp.w d4,d2
bmi COLLDN
move.l #1,d0
bra COLLDE
COLLDN:
clr.l d0
COLLDE:
movem.l (sp)+,d1-d7/a0-a6
rts
*
*gets nonactive
*input d5,d6,a1 
*output d6-free sprite no. d0-bytes (*4) d1-bit d2-s.active flag
SNASP:
move.l d6,d0
lsr.l #5,d0
move.l d0,d1
lsl.l #5,d1
move.l d6,d2
sub.l d1,d2
move.l d2,d1
lsl.l #2,d0
move.l (a1,d0),d2
btst.l d1,d2
beq RETURN2
addi.l #1,d6
cmp.l d5,d6
bcs SNASP
move.w #$ffff,d6
RETURN2:
rts

PUTBGC:
movem.l d0-d7/a0-a6,-(sp)
move.l d0,d2 *
andi.l #$ffff,d0
addi.l #$eb0000,d0
movea.l d0,a2
subi.l #$30,d1
addi.l #CODEZERO,d1
addi.l #$100,d1
move.w d1,(a2)
move.l d2,d0
movem.l (sp)+,d0-d7/a0-a6
rts
*
PRTBGC:
movem.l d0-d7/a0-a6,-(sp)
PRTBGCL:
clr.l d1
move.b (a1)+,d1
beq PRTE
cmpi.b #' ',d1
bne NPUTSP
move.b #$5b,d1
NPUTSP:
bsr PUTBGC
addq.l #2,d0
bra PRTBGCL
PRTE:
movem.l (sp)+,d0-d7/a0-a6
rts
*
SCOREPUT:
movem.l d0-d7/a0-a6,-(sp)
move.l #10000,d2
SCOREL:
divu d2,d1
addi.l #$30,d1
bsr PUTBGC
addi.l #2,d0
swap d1
andi.l #$ffff,d1
divu #10,d2
bne SCOREL
movem.l (sp)+,d0-d7/a0-a6
rts

.data
SPACT:dc.l $000000ff,0,0,0
SCROLA:dc.w 0
.bss

