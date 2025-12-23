package Test;

import Models.Student;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Benchmark {
    List<String> names = List.of(
            "Alice", "Bob", "Charlie", "David", "Eve",
            "Frank", "Grace", "Hannah", "Ivy", "Jack",
            "Kathy", "Liam", "Mia", "Noah", "Olivia",
            "Paul", "Quinn", "Rachel", "Sam", "Tina"
    );
    List<Student> studentsCache = null;
    public Student generateStudent(long id) {
        String name = names.get((int) (id % names.size()));
        double gpa = ThreadLocalRandom.current().nextDouble(5.0, 20.0);
        return new Student(id, name, gpa);
    }

    public List<Student> generateStudents(int recordCount) {
        studentsCache = java.util.stream.LongStream.rangeClosed(1, recordCount)
                .mapToObj(this::generateStudent)
                .toList();
        return studentsCache;
    }

    public long insertBenchmark(int recordCount) {
        List<Student> students = generateStudents(recordCount);
        long startTime = System.nanoTime();
        for (Student student : students) {
            Engine.Commands.InsertOneCommand.execute(student);
        }
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    public Long deleteFirstBenchmark(int recordDeleteCount) {
        long startTime = System.nanoTime();
        for (int i = 0; i < recordDeleteCount; i++) {
            Engine.Commands.DeleteOneCommand.execute((long) i);
        }
        long endTime = System.nanoTime();
        return endTime - startTime;
    }
    public long deleteLastBenchmark(int recordDeleteCount) {
        int size = studentsCache.size();
        long startTime = System.nanoTime();
        for (int i = 0; i < recordDeleteCount; i++) {
            Engine.Commands.DeleteOneCommand.execute(size-i-1L);
        }
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    public long filterBenchmark(int recordDeleteCount) {
        Random random = new Random();
        long startTime = System.nanoTime();
        for (int i = 0; i < recordDeleteCount; i++) {
            long idToFind = random.nextInt() + 1;
            Engine.Commands.FindByIdCommand.execute(idToFind);
        }
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    public static void main(String[] args){
        Benchmark benchmark = new Benchmark();
        System.out.println("Inserted " + 50000 + " records in " + benchmark.insertBenchmark(50000) / 1_000_000 + " ms");
        System.out.println("Deleted " + 500 + " records from the first in " + benchmark.deleteFirstBenchmark(500) / 1_000_000 + " ms");
        System.out.println("Deleted " + 500 + " records from the last in " + benchmark.deleteLastBenchmark(500) / 1_000_000 + " ms");
        System.out.println("Filtered " + 500 + " records in " + benchmark.filterBenchmark(500) / 1_000_000 + " ms");
    }
}
