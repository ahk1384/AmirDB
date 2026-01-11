package DataStructeure;

import java.util.ArrayList;
import java.util.List;

class BSTNode<T> {
    public T Value;
    public BSTNode<T> Left;
    public BSTNode<T> Right;
    public BSTNode<T> Top;

    public BSTNode(BSTNode<T> top, T value) {
        Value = value;
        Left = null;
        Right = null;
        Top = top;
    }
}

public class BST<T extends Comparable<T>> {
    public BSTNode<T> Root;

    public BST() {
        Root = null;
    }

    public boolean Insert(T value) {
        if (Root == null) {
            Root = new BSTNode<T>(null, value);
            return true;
        }
        InsertRec(Root, null, value); // Pass null as initial top
        return true;
    }

    private BSTNode<T> InsertRec(BSTNode<T> node, BSTNode<T> top, T value) {
        if (node == null) {
            return new BSTNode<T>(top, value);
        }

        if (value.compareTo(node.Value) < 0) {
            node.Left = InsertRec(node.Left, node, value);
        } else if (value.compareTo(node.Value) > 0) {
            node.Right = InsertRec(node.Right, node, value);
        } else {
            // Value already exists, don't insert duplicates
            return node;
        }
        return node;
    }

    public boolean Search(T value) {
        return SearchRec(Root, value);
    }

    private boolean SearchRec(BSTNode<T> node, T value) {
        if (node == null) {
            return false;
        }

        if (value.compareTo(node.Value) == 0) {
            return true;
        } else if (value.compareTo(node.Value) < 0) {
            return SearchRec(node.Left, value);
        } else {
            return SearchRec(node.Right, value);
        }
    }

    public void InorderTraversal(BSTNode<T> node, List<T> result) {
        if (node != null) {
            InorderTraversal(node.Left, result);
            result.add(node.Value);
            InorderTraversal(node.Right, result);
        }
    }

    public List<T> InorderTraversal() {
        List<T> result = new ArrayList<>();
        InorderTraversal(Root, result);
        return result;
    }

    public void PreorderTraversal(BSTNode<T> node, List<T> result) {
        if (node != null) {
            result.add(node.Value);
            PreorderTraversal(node.Left, result);
            PreorderTraversal(node.Right, result);
        }
    }

    public List<T> PreorderTraversal() {
        List<T> result = new ArrayList<>();
        PreorderTraversal(Root, result);
        return result;
    }

    public void PostorderTraversal(BSTNode<T> node, List<T> result) {
        if (node != null) {
            PostorderTraversal(node.Left, result);
            PostorderTraversal(node.Right, result);
            result.add(node.Value);
        }
    }

    public List<T> PostorderTraversal() {
        List<T> result = new ArrayList<>();
        PostorderTraversal(Root, result);
        return result;
    }

    public boolean Delete(T value) {
        Root = DeleteRec(Root, value);
        return true;
    }

    private BSTNode<T> DeleteRec(BSTNode<T> node, T value) {
        if (node == null) {
            return null;
        }

        if (value.compareTo(node.Value) < 0) {
            node.Left = DeleteRec(node.Left, value);
        } else if (value.compareTo(node.Value) > 0) {
            node.Right = DeleteRec(node.Right, value);
        } else {
            if (node.Left == null) {
                return node.Right;
            } else if (node.Right == null) {
                return node.Left;
            }

            BSTNode<T> temp = new BSTNode<>(node.Top,node.Right.Value);
            node.Value = temp.Value;
            node.Right = DeleteRec(node.Right, node.Value);
        }
        return node;
    }

    public int Size() {
        return SizeRec(Root);
    }

    private int SizeRec(BSTNode<T> node) {
        if (node == null) {
            return 0;
        }
        return 1 + SizeRec(node.Left) + SizeRec(node.Right);
    }

    public int Height() {
        return HeightRec(Root);
    }

    private int HeightRec(BSTNode<T> node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(HeightRec(node.Left), HeightRec(node.Right));
    }

    public boolean IsEmpty() {
        return Root == null;
    }
}
