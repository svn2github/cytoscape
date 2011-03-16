from lpsolve55 import *

class LPMaker:
    def createLP(self,variables):
        
        lp = lpsolve('make_lp', 0, len(variables))
        lpsolve('set_maxim', lp)
        return lp
    def setObjectives(self,numPPI,variables,lp):
        params = []
        for i in range(len(variables)):
            if i < numPPI:
                params.append(1)
            else:
                params.append(0)
        lpsolve('set_obj_fn', lp, params)
    def setVarConstraint(self,lp):
        var =lpsolve('get_variables', lp)[0]
        
        for i in range(len(var)):
            lpsolve('set_binary', lp, i+1,True)
        
    def addDomainBasedConstraints(self,lp,params):
        lpsolve('add_constraint', lp, params, LE, 1)
    def addInteractionsBasedConstraints(self,lp,params):
        
        lpsolve('add_constraint', lp, params, EQ, 0)
    def addSos(self,lp,vars):
        weights=[]
        for i in range(len(vars)):
            weights.append(1.0)
        lpsolve('add_SOS', lp, "", 1,1,vars,weights)
    def writeLP(self,lp,fileName):
        lpsolve('write_lp', lp, fileName)