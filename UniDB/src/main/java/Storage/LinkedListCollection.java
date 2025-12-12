package Storage;

import Models.Student;

import java.util.ArrayList;
import java.util.List;

public class LinkedListCollection implements Collection {
    private class Node {
        Student data;
        Node next;
        Node prev;
    }
    private Node head;
    private Node tail;


    public boolean insertOne(Student student) {
        Node newNode = new Node();
        newNode.data = student;
        if (head == null) {
            head = newNode;
            tail = newNode;
            return true;
        }
        tail.next = newNode;
        newNode.prev = tail;
        tail = newNode;
        return true;
    }
    public boolean deleteOne(int id) {
        Node current = head;
        while (current != null) {
            if (current.data.getId() == id) {
                if (current.prev != null) {
                    current.prev.next = current.next;
                } else {
                    head = current.next;
                }
                if (current.next != null) {
                    current.next.prev = current.prev;
                } else {
                    tail = current.prev;
                }
                return true;
            }
            current = current.next;
        }
        return false;
    }
    public Student findByID(int id) {
        Node current = head;
        while (current != null) {
            if (current.data.getId() == id) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }
    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        Node current = head;
        while (current != null) {
            students.add(current.data);
            current = current.next;
        }
        return students;
    }
}