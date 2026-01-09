package DataStructeure;
class BSTNode<T>
{
    public T Value;
    public BSTNode<T> Left;
    public BSTNode<T> Right;
    public BSTNode<T> Top;
    public BSTNode(BSTNode<T> top, T value)
    {
        Value = value;
        Left = null;
        Right = null;
        Top = top;
    }
}

public class BST<T>
{
    public BSTNode<T> Root;
    public BST()
    {
        Root = null;
    }
    public boolean Insert(T value)
    {
        if (Root == null)
        {
            Root = new BSTNode<T>(null, value);
            return true;
        }
        InsertRec(Root,Root, value);
        return true;
    }
    private BSTNode<T> InsertRec(BSTNode<T> node,BSTNode<T> top, T value)
    {
        if (node == null)
        {
            return new BSTNode<T>(top, value);
        }
        if (value < node.Value)
        {
            node.Left = InsertRec(node.Left, node, value);
        }
        else
        {
            node.Right = InsertRec(node.Right, node, value);
        }
        return node;
    }

}
