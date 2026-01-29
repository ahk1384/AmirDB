package Engine.Commands;

import Index.FiledsType;
import Index.IndexType;
import Optimizer.IIndexOptimizer;
import java.io.IOException;

public class CreateIndexCommand {
    private final IIndexOptimizer indexOptimizer;

    public CreateIndexCommand(IIndexOptimizer indexOptimizer) {
        this.indexOptimizer = indexOptimizer;
    }

    public  Boolean execute(IndexType indexType, FiledsType filedsType) throws IOException {
        return indexOptimizer.addIndex(indexType, filedsType);
    }
}
