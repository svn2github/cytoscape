import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.list.IntArrayList;

class Target2PathMap extends OpenIntObjectHashMap
{
    Target2PathMap(int numNodes)
    {
        super(numNodes);
    }

    void addPath(int targetNode, int path)
    {
        
        if(!this.containsKey(targetNode))
        {
            IntArrayList l = new IntArrayList();
            l.add(path);
            this.put(targetNode, l);
        }
        else
        {
            ((IntArrayList) this.get(targetNode)).add(path);
        }
    }

}
