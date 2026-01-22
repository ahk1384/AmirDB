package Index;

import Models.Student;
import Shared.*;

import java.util.List;

public interface IIndex {
     boolean Insert(Object record);
     boolean Delete(Object record);
     SearchResult Search(Comparable record);
}
