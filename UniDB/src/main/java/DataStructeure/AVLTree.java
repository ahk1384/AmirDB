package DataStructeure;

class Node<T> {
    public T value;
    public int height;

    public Node<T> left, right;


    public Node(T d) {
        value = d;
        height = 1;
    }

}
class AVLTree<T>
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

        if (Comparer<T>.Default.Compare(value, node.value) < 0) {
            node.left = insert(node.left, value);
        } else if (Comparer<T>.Default.Compare(value, node.value) > 0) {
            node.right = insert(node.right, value);
        } else {
            return node;
        }

        node.height = 1 + max(height(node.left), height(node.right));

        int balance = getBalance(node);

        if (balance > 1 && Comparer<T>.Default.Compare(value, node.left.value) < 0) {
            return rightRotate(node);
        }

        if (balance < -1 && Comparer<T>.Default.Compare(value, node.right.value) > 0) {
            return leftRotate(node);
        }

        if (balance > 1 && Comparer<T>.Default.Compare(value, node.left.value) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && Comparer<T>.Default.Compare(value, node.right.value) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }
    public int depth(T value) {
        Node<T> node = root;
        int depth = 0;
        while (node != null) {
            if (Comparer<T>.Default.Compare(value, node.value) == 0) {
                return depth;
            } else if (Comparer<T>.Default.Compare(value, node.value) < 0) {
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
            if (Comparer<T>.Default.Compare(value, node.value) == 0) {
                return node;
            } else if (Comparer<T>.Default.Compare(value, node.value) < 0) {
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
            if (Comparer<T>.Default.Compare(value, node.value) == 0) {
                return node.value;
            } else if (Comparer<T>.Default.Compare(value, node.value) < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return default(T);
    }
    private List<T> preOrder(Node<T> node) {
        List<T> result = new List<T>();
        if (node != null) {
            result.Add(node.value);
            result.AddRange(preOrder(node.left));
            result.AddRange(preOrder(node.right));
        }
        return result;
    }
    public List<T> PreOrder() {
        return preOrder(root);
    }
    public List<T> InOrder() {
        return inOrder(root);
    }
    private List<T> inOrder(Node<T> node) {
        List<T> result = new List<T>();
        if (node != null) {
            result.AddRange(inOrder(node.left));
            result.Add(node.value);
            result.AddRange(inOrder(node.right));
        }
        return result;
    }
    public List<T> PostOrder() {
        return postOrder(root);
    }
    private List<T> postOrder(Node<T> node) {
        List<T> result = new List<T>();
        if (node != null) {
            result.AddRange(postOrder(node.left));
            result.AddRange(postOrder(node.right));
            result.Add(node.value);
        }
        return result;
    }
    public String traverse(string type) {
        if (type == "postorder") {
            return String.Join(" ", PostOrder());
        } else if (type == "inorder") {
            return String.Join(" ", InOrder());
        } else if (type == "preorder") {
            return String.Join(" ", PreOrder());
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

        if (Comparer<T>.Default.Compare(value, node.value) < 0) {
            node.left = RemoveNode(node.left, value);
        } else if (Comparer<T>.Default.Compare(value, node.value) > 0) {
            node.right = RemoveNode(node.right, value);
        } else {
            if (node.left == null || node.right == null) {
                Node<T> temp = node.left ?? node.right;

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
