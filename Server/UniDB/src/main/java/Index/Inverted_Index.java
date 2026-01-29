package Index;
import Models.Student;
import Optimizer.IndexOptimizer;
import Shared.*;
import Storage.StudentRecord;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
public class Inverted_Index extends IndexBase implements IIndex{

    private HashMap<String,List<Long>> data;

    public Inverted_Index(FiledsType filed) {
        super(filed);
        data = new HashMap<String,List<Long>>();
    }

    @Override
    public boolean Insert(Object record) {
        StudentRecord studentRecord = (StudentRecord) record;
        String[] str = studentRecord.getName().split(" ");
        for(String s : str){
            if(data.containsKey(s)){
                data.get(s).add(studentRecord.getId());
            }
            else {
                List<Long> l = new ArrayList<Long>();
                l.add(studentRecord.getId());
                data.put(s, l);
            }
        }
        return true;

    }

    @Override
    public boolean Delete(Object record) {
        StudentRecord studentRecord = (StudentRecord) record;
        Long id = studentRecord.getId();
        for (List<Long> list: data.values()){
            if (list.contains(id)){
                list.remove(id);
            }
        }
        return true;
    }

    @Override
    public SearchResult Search(Comparable record) {
        int count = 0;
        double time = 0;
        if (data.get(record) != null) {
            long start = System.nanoTime();
            count = data.get(record).size();
            long end = System.nanoTime();
            time = (end - start) / 1000000.0;
            BigDecimal round = BigDecimal.valueOf(time);
            time = round.setScale(3, RoundingMode.HALF_UP).doubleValue();
        }
        return new SearchResult(count,time,-1);
    }
}
