package Index;

import Models.Student;
import Shared.*;

import java.util.List;

public interface IIndex {
    public boolean Insert(Object record);
    public boolean Delete(Object record);
    public SearchResult Search(Object record);
}
