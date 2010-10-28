biocyc list databases
biocyc list pathways database="ECOLI"
biocyc list pathways database="META"
biocyc load pathway database="META" pathway="PWY-5025"

