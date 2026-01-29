package Index;
import DataStructeure.HashMap;
import Shared.*;
import Storage.StudentRecord;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Hash_Index extends IndexBase implements IIndex{

    private HashMap data;

    public Hash_Index(FiledsType filed) {
        super(filed);
        data = new HashMap();
    }

    @Override
    public boolean Insert(Object record) {
        StudentRecord studentRecord = (StudentRecord) record;
        if(filed.toString().equals("gpa")){
            data.put(studentRecord.getGpa(),record);
        }else if(filed.toString().equals("name")){
            data.put(studentRecord.getName(),record);
        }else if(filed.toString().equals("id")){
            data.put(studentRecord.getId(),record);
        }
        return true;
    }

    @Override
    public boolean Delete(Object record) {
        StudentRecord studentRecord = (StudentRecord) record;
        if(filed.toString().equals("gpa")){
            data.remove(studentRecord.getGpa());
        }else if (filed.toString().equals("name")){
            data.remove(studentRecord.getName());
        }else if (filed.toString().equals("id")){
            data.remove(studentRecord.getId());
        }
        return true;
    }


    @Override
    public SearchResult Search(Comparable record) {
        long start = System.nanoTime();
        SearchResult res = data.get(record);
        long end = System.nanoTime();
        double time = (end - start) / 1000000.0;
        BigDecimal round = BigDecimal.valueOf(time);
        double result = round.setScale(3, RoundingMode.HALF_UP).doubleValue();
        res.setTime(result);
        return res;
    }
}
