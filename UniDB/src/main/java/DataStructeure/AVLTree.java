package DataStructeure;

import java.util.ArrayList;

class Node<T> {
    public T value;
    public int height;

    public Node<T> left, right;


    public Node(T d) {
        value = d;
        height = 1;
    }
    public T getDate(){
        return value;
    }

}
class AVLTree<T extends Comparable<T>>
{


    private Node<T> root;
    public AVLTree() {
        root = null;
    }

    int height(Node<T> N) {
        if (N == null)
            return 0;
        return N.height;
    }

    int max(int a, int b) {
        return (a > b) ? a : b;
    }

    public Node<T> rightRotate(Node<T> y) {
        Node<T> x = y.left;
        Node<T> T2 = x.right;
        x.right = y;
        y.left = T2;
        y.height = max(height(y.left), height(y.right)) + 1;
        x.height = max(height(x.left), height(x.right)) + 1;
        return x;
    }
    public Node<T> leftRotate(Node<T> x) {
        Node<T> y = x.right;
        Node<T> T2 = y.left;
        y.left = x;
        x.right = T2;
        x.height = max(height(x.left), height(x.right)) + 1;
        y.height = max(height(y.left), height(y.right)) + 1;
        return y;
    }
    public int getBalance(Node<T> N) {
        if (N == null)
            return 0;
        return height(N.left) - height(N.right);
    }
    public Node<T> add(T value) {
        root = insert(root, value);
        return root;
    }
    private Node<T> insert(Node<T> node, T value) {
        if (node == null) {
            return new Node<T>(value);
        }

        if (value.compareTo(node.value)< 0) {
            node.left = insert(node.left, value);
        } else if (value.compareTo(node.value) > 0) {
            node.right = insert(node.right, value);
        } else {
            return node;
        }

        node.height = 1 + max(height(node.left), height(node.right));

        int balance = getBalance(node);

        if (balance > 1 && value.compareTo(node.value) < 0) {
            return rightRotate(node);
        }

        if (balance < -1 && value.compareTo(node.value) > 0) {
            return leftRotate(node);
        }

        if (balance > 1 && value.compareTo(node.value) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && value.compareTo(node.value) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }




    public int depth(T value) {
        Node<T> node = root;
        int depth = 0;
        while (node != null) {
            if (value.compareTo(node.value) == 0) {
                return depth;
            } else if (value.compareTo(node.value) < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
            depth++;
        }
        return -1;
    }
    public Node<T> ceil(T value) {
        Node<T> node = root;
        Node<T> ceilNode = null;

        while (node != null) {
            if (value.compareTo(node.value) == 0) {
                return node;
            } else if (value.compareTo(node.value) < 0) {
                ceilNode = node;
                node = node.left;
            } else {
                node = node.right;
            }
        }

        return ceilNode;
    }
    public T find(T value) {
        Node<T> node = root;
        while (node != null) {
            if (value.compareTo(node.value)== 0) {
                return node.value;
            } else if (value.compareTo(node.value)< 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return null;
    }
    private ArrayList<T> preOrder(Node<T> node) {
        ArrayList<T> result = new ArrayList<T>();
        if (node != null) {
            result.add(node.value);
            result.addAll(preOrder(node.left));
            result.addAll(preOrder(node.right));
        }
        return result;
    }
    public ArrayList<T> PreOrder() {
        return preOrder(root);
    }
    public ArrayList<T> InOrder() {
        return inOrder(root);
    }
    private ArrayList<T> inOrder(Node<T> node) {
        ArrayList<T> result = new ArrayList<T>();
        if (node != null) {
            result.addAll(inOrder(node.left));
            result.add(node.value);
            result.addAll(inOrder(node.right));
        }
        return result;
    }
    public ArrayList<T> PostOrder() {
        return postOrder(root);
    }
    private ArrayList<T> postOrder(Node<T> node) {
        ArrayList<T> result = new ArrayList<T>();
        if (node != null) {
            result.addAll(postOrder(node.left));
            result.addAll(postOrder(node.right));
            result.add(node.value);
        }
        return result;
    }
    public String traverse(String type) {
        if (type == "postorder") {
            return PostOrder().toString();
        } else if (type == "inorder") {
            return InOrder().toString();
        } else if (type == "preorder") {
            return PreOrder().toString();
        }
        return "";
    }
    public Node<T> Remove(T value) {
        root = RemoveNode(root, value);
        return root;
    }
    private Node<T> RemoveNode(Node<T> node, T value) {
        if (node == null) {
            return node;
        }

        if (value.compareTo(node.value) < 0) {
            node.left = RemoveNode(node.left, value);
        } else if (value.compareTo(node.value) > 0) {
            node.right = RemoveNode(node.right, value);
        } else {
            if (node.left == null || node.right == null) {
                Node<T> temp ;
                if (node.left != null){
                    temp = node.left;
                }
                else{
                    temp = node.right;
                }

                if (temp == null) {
                    node = null;
                } else {
                    node = temp;
                }
            } else {
                Node<T> temp = GetMinValueNode(node.right);

                node.value = temp.value;

                node.right = RemoveNode(node.right, temp.value);
            }
        }

        if (node == null) {
            return node;
        }

        node.height = 1 + max(height(node.left), height(node.right));

        int balance = getBalance(node);

        if (balance > 1 && getBalance(node.left) >= 0) {
            return rightRotate(node);
        }

        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && getBalance(node.right) <= 0) {
            return leftRotate(node);
        }

        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private Node<T> GetMinValueNode(Node<T> node) {
        Node<T> current = node;

        while (current.left != null) {
            current = current.left;
        }

        return current;
    }
}
