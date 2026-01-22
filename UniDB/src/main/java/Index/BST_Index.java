package Index;

import DataStructeure.BST;
import Shared.*;
import Storage.StudentRecord;

import java.math.RoundingMode;
import java.math.BigDecimal;
public class BST_Index extends IndexBase implements IIndex{

    private BST data ;

    public BST_Index(FiledsType filed) {
        super(filed);
        if (data == null) {
            data = new BST();
        }
    }

    @Override
    public boolean Insert(Object record) {
        StudentRecord studentRecord = (StudentRecord) record;
        if(filed.toString().equals("gpa")){
            data.Insert((int)studentRecord.getId());
        }else if(filed.toString().equals("name")){
            data.Insert(studentRecord.getName());
        }else if(filed.toString().equals("id")){
            data.Insert(studentRecord.getId());
        }
        return true;
    }

    @Override
    public boolean Delete(Object record) {
        StudentRecord studentRecord = (StudentRecord) record;
        if(filed.toString().equals("gpa")){
            data.Delete((int)studentRecord.getId());
        }else if(filed.toString().equals("name")){
            data.Delete(studentRecord.getName());
        }else if(filed.toString().equals("id")){
            data.Delete(studentRecord.getId());
        }
        return true;
    }

    @Override
    public SearchResult Search(Comparable record) {

        long start = System.nanoTime();
        SearchResult res = data.Search(record);
        long end = System.nanoTime();
        double time = (end - start) / 1000000.0;
        BigDecimal round = BigDecimal.valueOf(time);
        double result = round.setScale(3, RoundingMode.HALF_UP).doubleValue();
        res.setTime(result);
        return res;
    }
}
