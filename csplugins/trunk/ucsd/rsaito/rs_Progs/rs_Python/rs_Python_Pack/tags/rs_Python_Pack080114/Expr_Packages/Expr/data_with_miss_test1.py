#!/usr/bin/env python

import Data_Struct.Data_with_Miss2
import Data_Struct.ListList

ll = Data_Struct.ListList.ListList()
ll.add_list([1,3,5,7,"X",9])
llsc = Data_Struct.Data_with_Miss2.ListList_Size_Conv(ll)
llsc.conv_idx_missing_to_numlistlist()

print llsc.get_listlist_with_missing().get_all_lists()
print llsc.get_num_listlist().get_all_lists()

ll2 = Data_Struct.ListList.ListList()
ll2.add_list([ 2, 4, 6, 8, 10 ])
llsc.import_num_listlist(ll2)

print llsc.get_num_listlist().get_all_lists()
print llsc.get_idx_numlistlist_to_missing().get_all_lists()



