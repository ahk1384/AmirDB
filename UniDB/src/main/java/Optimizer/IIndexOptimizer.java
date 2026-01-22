package Optimizer;

import Index.FiledsType;
import Index.IIndex;
import Index.IndexType;

import java.util.List;

public interface IIndexOptimizer {
    boolean addIndex(IndexType indexType, FiledsType filedsType);

    IIndex getIndex(FiledsType filed);

    boolean isIndexed(FiledsType filedsType);

}
