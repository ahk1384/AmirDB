package Test;

import Index.AVLTree_Index;
import Index.BST_Index;
import Index.FiledsType;
import Models.Student;
import Test.Benchmark;

import java.util.*;

public class BenchmarkIndexes {

    public void runComparison() {

        System.out.println("🔥 Starting Benchmark...");
        System.out.println("==================================================");

        System.out.println("\n⏱️  Testing BST Index...");
        long bstTime = benchmarkInsert("bst");
        System.out.println("BST Time: " + (bstTime / 1_000_000.0) + " ms");

        System.out.println("\n⏱️  Testing AVL Index...");
        long avlTime = benchmarkInsert("avl");
        System.out.println("AVL Time: " + (avlTime / 1_000_000.0) + " ms");

        System.out.println("\n==================================================");
        System.out.println("📊 Results:");
        System.out.printf("BST: %.6f ms\n", bstTime / 1_000_000.0);
        System.out.printf("AVL: %.6f ms\n", avlTime / 1_000_000.0);

        if (bstTime < avlTime) {
            double faster = ((avlTime - bstTime) / (double) avlTime * 100);
            System.out.printf("🏆 BST is faster by %.2f%%\n", faster);
        } else {
            double faster = ((bstTime - avlTime) / (double) bstTime * 100);
            System.out.printf("🏆 AVL is faster by %.2f%%\n", faster);
        }

        System.out.println("\n💡 Sorted data = BST gets rekt, AVL stays strong! ✨");
    }

    private long benchmarkInsert(String indexType) {
        Benchmark bench = new Benchmark();
        List<Student> students = bench.insertBenchmark(10000);
        AVLTree_Index avl = new AVLTree_Index(FiledsType.id);
        BST_Index bst = new BST_Index(FiledsType.id);
        long startTime = System.nanoTime();
        if(indexType == "avl"){
            for(Student student : students){
                avl.Insert(student.toStudentRecord());
            }
        }else if (indexType == "bst"){
            for(Student student : students){
                bst.Insert(student.toStudentRecord());
            }
        }
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    public static void main(String[] args) {
        BenchmarkIndexes benchmark = new BenchmarkIndexes();
        benchmark.runComparison();
    }
}