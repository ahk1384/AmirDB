package Optimizer;

import Engine.Commands.FindAllCommand;
import Index.*;
import Models.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexOptimizer implements IIndexOptimizer {
    private static IndexOptimizer indexOptimizer;
    Map<FiledsType, IIndex> indexMap;

    private IndexOptimizer() {
        indexMap = new HashMap<>();
    }

    public static IndexOptimizer getInstance() {
        if (indexOptimizer == null) {
            indexOptimizer = new IndexOptimizer();
        }
        return indexOptimizer;
    }

    public boolean addIndex(IndexType indexType, FiledsType filedsType) {
        if (indexMap.containsKey(filedsType)) {
            indexMap.remove(filedsType);
        }
        Adder(indexType, filedsType);
        return true;
    }

    private void Adder(IndexType indexType, FiledsType filedsType) {
        IIndex index;
        if (indexType.equals(IndexType.BST)) {
            index = new BST_Index(filedsType);
            Indexer(indexType, filedsType, index);
        } else if (indexType.equals(IndexType.AVL)) {
            index = new AVLTree_Index(filedsType);
            Indexer(indexType, filedsType, index);
        } else if (indexType.equals(IndexType.HASH)) {
            index = new Hash_Index(filedsType);
            Indexer(indexType, filedsType, index);
        }else if (indexType.equals(IndexType.INVERTED)){
            index = new Inverted_Index(filedsType);
            Indexer(indexType, filedsType, index);
        }
    }


    @Override
    public IIndex getIndex(FiledsType filed) {
        if (isIndexed(filed)) {
            return indexMap.get(filed);
        } else {
            return null;
        }
    }

    @Override
    public boolean isIndexed(FiledsType filedsType) {
        return indexMap.containsKey(filedsType);
    }

    private void Indexer(IndexType indexType, FiledsType filedsType, IIndex index) {
        for (Student record : FindAllCommand.execute()) {
            index.Insert(record.toStudentRecord());
        }
        indexMap.put(filedsType,index);
    }

    public boolean UpdateInsertIndex(Object record) {
        try {
            Student recordToUpdate = (Student) record;
            for (IIndex index : indexMap.values()) {
                index.Insert(recordToUpdate.toStudentRecord());
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean UpdateDeleteIndex(Object record) {
        try {
            Student recordToUpdate = (Student) record;
            for (IIndex index : indexMap.values()) {
                index.Delete(recordToUpdate.toStudentRecord());
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
