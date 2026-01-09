package Index;

import DataStructeure.BST;
import Shared.*;

public class BST_Index implements IIndex{

    private BST data ;

    @Override
    public boolean Insert(Object record) {
        if (data == null ){
            data = new BST();
        }
        return data.Insert(record);
    }

    @Override
    public boolean Delete(Object record) {
        return false;
    }

    @Override
    public SearchResult Search(Object record) {
        return null;
    }
}
