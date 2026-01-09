package Optimizer;

import Index.IIndex;

import java.util.HashMap;
import java.util.Map;

public class IndexOptimizer {
    Map<String, IIndex> indexMap = new HashMap<>();

    public boolean addIndex(String filed ,IIndex index){
        indexMap.put(filed,index);
        return true;
    }
    public IIndex getIndex(String filed){
        if(indexMap.containsKey(filed)){
            return indexMap.get(filed);
        }
        else {
            return null;
        }
    }

}
