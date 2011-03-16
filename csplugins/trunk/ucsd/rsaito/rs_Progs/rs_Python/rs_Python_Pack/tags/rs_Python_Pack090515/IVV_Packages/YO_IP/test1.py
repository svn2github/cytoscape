from lpsolve55 import *

lp = lpsolve('make_lp', 0, 5)
lpsolve('set_obj_fn', lp, [1, 1, 0, 0,0])
lpsolve('set_binary', lp, 1,True)
lpsolve('set_binary', lp, 2,True)
lpsolve('set_binary', lp, 3,True)
lpsolve('set_binary', lp, 4,True)
lpsolve('set_binary', lp, 5,True)

lpsolve('set_col_name', lp, 1, 'y1')
lpsolve('set_col_name', lp, 2, 'y2')
lpsolve('set_col_name', lp, 3, 'x1')
lpsolve('set_col_name', lp, 4, 'x2')
lpsolve('set_col_name', lp, 5, 'x3')

lpsolve('add_constraint', lp, [-1, 0, 1, 1,0], EQ, 0)
lpsolve('add_constraint', lp, [0, -1, 0, 0,1], EQ, 0)
lpsolve('add_constraint', lp, [0, 0, 1, 1,0], LE, 1)
lpsolve('add_constraint', lp, [0, 0, 0, 0,1], LE, 1)

lpsolve('add_SOS', lp, "for_y1", 1,1,[3,4],[1.0,1.0])
lpsolve('add_SOS', lp, "for_y2", 1,1,[5],[1.0])
 
lpsolve('set_maxim', lp)
lpsolve('write_lp', lp, 'a.lp')

print lpsolve('get_mat', lp, 1, 2)
lpsolve('solve', lp)
print lpsolve('get_objective', lp)
print lpsolve('get_variables', lp)[0]
print lpsolve('get_constraints', lp)[0]
print lpsolve('get_col_name', lp)

lpsolve('delete_lp', lp)