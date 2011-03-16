
mscale1000 <- function(vec){
   return(vec * 1000.0 / mean(vec))
}

Human44K.dT.File = "./Data_44K/Preliminary/Human44K_dT_mod2"

id.list <- read.table("onc_afas_info1")
probe.ids <- id.list[,1]

sense.rows = grep("^ORIG", id.list[,3])
afas.rows  = grep("^AFAS", id.list[,3])

probe.ids.sense = id.list[ sense.rows, 1 ]
probe.ids.afas = id.list[ afas.rows, 1 ]

tb = read.table(Human44K.dT.File,
	header = TRUE, sep = "\t",
	row.names = 1, as.is = TRUE)

tbs = apply(tb, 2, mscale1000)

colon_normal_cols = grep("Colon.*Normal", dimnames(tbs)[[2]])
colon_cancer_cols = grep("Colon.*Disease", dimnames(tbs)[[2]])

sense_colon_normal_exp = tbs[ row.names(tbs) %in% probe.ids.sense,
                              colon_normal_cols ]

hist(log10(sense_colon_normal_exp))
