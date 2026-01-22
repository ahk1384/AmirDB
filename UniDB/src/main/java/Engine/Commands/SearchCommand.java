package Engine.Commands;

import Index.FiledsType;
import Index.IIndex;
import Index.Inverted_Index;
import Optimizer.IIndexOptimizer;
import Shared.SearchResult;

public class SearchCommand {
    private IIndexOptimizer indexOptimizer;
    private IIndex index;
    public SearchCommand(IIndexOptimizer indexOptimizer) {
        this.indexOptimizer = indexOptimizer;

    }
    public SearchResult execute(FiledsType filed,Comparable value) {
        index = indexOptimizer.getIndex(filed);
        if (index instanceof Inverted_Index) {
            return index.Search(value);
        }
        return null;
    }
}
