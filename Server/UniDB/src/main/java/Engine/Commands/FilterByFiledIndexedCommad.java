package Engine.Commands;

import Index.FiledsType;
import Index.IIndex;
import Models.Student;
import Optimizer.IIndexOptimizer;
import Shared.SearchResult;
import Storage.StorageManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FilterByFiledIndexedCommad {
    private IIndexOptimizer indexOptimizer;
    private IIndex index;
    public FilterByFiledIndexedCommad(IIndexOptimizer indexOptimizer) {
        this.indexOptimizer = indexOptimizer;
    }

    public SearchResult execute(FiledsType field, Comparable value) {
        index = indexOptimizer.getIndex(field);
        if (field.equals(FiledsType.id)){
            return index.Search(Long.parseLong(value.toString()));
        }else if (field.equals(FiledsType.name)){
            return index.Search(value.toString());
        }else if(field.equals(FiledsType.gpa)) {
            return index.Search(Double.parseDouble(value.toString()));
        }else{
            return index.Search(value.toString());
        }
    }

}
