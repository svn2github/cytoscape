**ŠyŠíƒf[ƒ^[İ’è
*/
*(ƒƒCƒ“‚Å)lea INSTC,a2
*(  V    )move.w #n,NFMDRV
*
.data
xdef TRAP15
xdef INSTSET
xref SLOT0
xref NFMDRV
.text
INSTSET:
movem.l d0-d7/a0-a6,-(sp)
cmpi.l #$1_0000,d7
bcs COMST
subi.l #$1_0000,d7
bra COMNST
COMST:
move.b #$1b,d1    *ƒEƒF[ƒuƒtƒH[ƒ€
bsr FMSET1
move.b #$18,d1    *ƒXƒs[ƒh
bsr FMSET1
move.b #$19,d1    *‚o‚l‚cC‚`‚l‚c
bsr FMSET1
bsr FMSET1
*/
*+move.l #$0,d7
COMNST:
lea SLOT0,a3
adda.l d7,a3
FSLOOP1:
move.b #$20,d1    *‚k,‚q‚o‚`‚m^ƒtƒB[ƒhƒAƒ‹ƒS
bsr FMSET2
move.b #$38,d1    *‚o‚l‚rC‚`‚l‚r
bsr FMSET2
move.b (a2)+,(a3)+ *ƒXƒƒbƒgƒ}ƒXƒN
*/
move.l #$0,d6
FSLOOP2:
move.b #$80,d1    *‚j‚r^‚`‚q
bsr FMSET3
move.b #$a0,d1    *‚`‚l‚ren^‚c‚P‚q
bsr FMSET3
move.b #$c0,d1    *‚c‚s‚Q^‚c‚Q‚q
bsr FMSET3
move.b #$e0,d1    *‚c‚P‚k^‚q‚q
bsr FMSET3
move.b #$60,d1    *‚s‚k
bsr FMSET3
move.b #$40,d1    *‚c‚s‚P^‚l‚t‚k
bsr FMSET3
move.b (a2)+,d0   *opdata endmarker
cmpi.b #$fe,d0
bne ERROR
addi.l #1,d6
cmpi.l #4,d6
bne FSLOOP2
move.b (a2)+,d0   *chdata endmarker
cmpi.b #$ff,d0
bne ERROR
*+addi.l #1,d7
*+move.w NFMDRV,d0
*+cmp.w d7,d0
*+bcc FSLOOP1
movem.l (sp)+,d0-d7/a0-a6
rts
*/
ERROR:
move.l #$21,d0
lea E_MSG,a1
bsr TRAP15
DDLOOP:
bra DDLOOP
*/
FMSET1:
move.l #$68,d0
move.b (a2)+,d2
bsr TRAP15
rts
FMSET2:
move.l #$68,d0
add.l d7,d1
move.b (a2)+,d2
bsr TRAP15
rts
FMSET3:
move.l #$68,d0
move.l d6,d3
lsl.l #3,d3
add.l d7,d3
add.l d3,d1
move.b (a2)+,d2
bsr TRAP15
rts
*/
TRAP15:
trap #$0f
rts
.data
E_MSG:dc.b 'ŠyŠíƒf[ƒ^[ƒGƒ‰[',0
