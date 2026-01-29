package Index;

import DataStructeure.AVLTree;
import Shared.*;
import Storage.StudentRecord;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AVLTree_Index extends IndexBase implements IIndex{
    private AVLTree data;

    public AVLTree_Index(FiledsType field) {
        super(field);
        if (data == null){
            data = new AVLTree();
        }
    }

    @Override
    public boolean Insert(Object record) {
        StudentRecord record2 = (StudentRecord) record;
        if(filed.toString().equals("gpa")){
            data.add(record2.getGpa());
        }
        else if(filed.toString().equals("id")){
            data.add(record2.getId());
        } else if (filed.toString().equals("name")) {
            data.add(record2.getName());
        }
        return true;
    }
    @Override
    public boolean Delete(Object record) {
        StudentRecord record2 = (StudentRecord) record;
        if(filed.toString().equals("gpa")){
            data.Remove(record2.getGpa());
        }
        else if(filed.toString().equals("id")){
            data.Remove(record2.getId());
        } else if (filed.toString().equals("name")) {
            data.Remove(record2.getName());
        }
        return true;
    }

    @Override
    public SearchResult Search(Comparable record) {
        long start = System.nanoTime();
        SearchResult res = data.find(record);
        long end = System.nanoTime();
        double time = (end - start) / 1000000.0;
        BigDecimal round = BigDecimal.valueOf(time);
        double result = round.setScale(3, RoundingMode.HALF_UP).doubleValue();
        res.setTime(result);
        return res;
    }
}
