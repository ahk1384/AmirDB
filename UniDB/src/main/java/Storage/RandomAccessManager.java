package Storage;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RandomAccessManager {

    private static final String FILE_NAME = "student_records.dat";
    private RandomAccessFile file;
    private Map<Long, Long> indexMap;
    private long nextPosition;

    public RandomAccessManager() {
        try {
            file = new RandomAccessFile(FILE_NAME, "rw");
        } catch (IOException e) {
            e.printStackTrace();
        }

        indexMap = new HashMap<>();
        nextPosition = 0;
        loadExistingRecords();
    }

    private void loadExistingRecords() {
        try {
            long fileLen = file.length();
            long pos = 0;
            while (pos + StudentRecord.RECORD_SIZE <= fileLen) {
                file.seek(pos);
                long id = file.readLong();
                if (id != -1L) {
                    indexMap.put(id, pos);
                }
                pos += StudentRecord.RECORD_SIZE;
            }
            nextPosition = fileLen; // append at the end
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean writeRecord(StudentRecord record) {
        long position;
        if (indexMap.containsKey(record.getId())) {
            position = indexMap.get(record.getId());
        } else {
            position = nextPosition;
            nextPosition += StudentRecord.RECORD_SIZE;
        }
        try {
            file.seek(position);
            file.writeLong(record.getId());
            file.writeChars(CompleteString(record.getName(), StudentRecord.NAME_SIZE));
            file.writeDouble(record.getGpa());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        indexMap.put(record.getId(), position);
        return true;
    }

    public List<StudentRecord> readAllRecord() {
        List<StudentRecord> records = new ArrayList<>();
        try {
            long fileLen = file.length();
            long pos = 0;
            while (pos + StudentRecord.RECORD_SIZE <= fileLen) {
                file.seek(pos);
                long id = file.readLong();
                String name = readString(file, StudentRecord.NAME_SIZE);
                double gpa = file.readDouble();
                if (id != -1L) {
                    records.add(new StudentRecord(id, name, gpa));
                }
                pos += StudentRecord.RECORD_SIZE;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return records;
    }

    public StudentRecord readRecordByID(Long recordId) {
        Long position = indexMap.get(recordId);
        if (position == null) {
            return null;
        }
        try {
            file.seek(position);
            long id = file.readLong();
            String name = readString(file, StudentRecord.NAME_SIZE);
            double gpa = file.readDouble();
            return new StudentRecord(id, name, gpa);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteRecordById(Long studentId) {
        Long position = indexMap.get(studentId);
        if (position == null) {
            return false;
        }
        try {
            file.seek(position);
            file.writeLong(-1L); // mark as deleted using long
            indexMap.remove(studentId);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean exists(Long studentId) {
        return indexMap.containsKey(studentId);
    }

//    public boolean close() throws IOException {
//        file.close();
//        return true;
//    }

    public long getRecordCount() {
        return indexMap.size();
    }

    public long sumOfFiled(String fieldName) {
        long sum = 0;
        try {
            long fileLen = file.length();
            long pos = 0;
            while (pos + StudentRecord.RECORD_SIZE <= fileLen) {
                file.seek(pos);
                long id = file.readLong();
                String name = readString(file, StudentRecord.NAME_SIZE);
                double gpa = file.readDouble();
                if (id != -1L) {
                    if (fieldName.equals("gpa")) {
                        sum += gpa;
                    }
                }
                pos += StudentRecord.RECORD_SIZE;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return sum;
    }

    public long averageOfFiled(String fieldName) {
        long sum = 0;
        long count = 0;
        try {
            long fileLen = file.length();
            long pos = 0;
            while (pos + StudentRecord.RECORD_SIZE <= fileLen) {
                file.seek(pos);
                long id = file.readLong();
                String name = readString(file, StudentRecord.NAME_SIZE);
                double gpa = file.readDouble();
                if (id != -1L) {
                    if (fieldName.equals("gpa")) {
                        sum += gpa;
                        count++;
                    }
                }
                pos += StudentRecord.RECORD_SIZE;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return count == 0 ? 0 : sum / count;
    }
    public List<StudentRecord> filterByFiled(String fieldName, String value) {
        List<StudentRecord> results = new ArrayList<>();
        try {
            long fileLen = file.length();
            long pos = 0;
            while (pos + StudentRecord.RECORD_SIZE <= fileLen) {
                file.seek(pos);
                long id = file.readLong();
                String name = readString(file, StudentRecord.NAME_SIZE);
                double gpa = file.readDouble();
                if (id != -1L) {
                    if (fieldName.equals("name") && name.equals(value)) {
                        results.add(new StudentRecord(id, name, gpa));
                    } else if (fieldName.equals("gpa") && Double.toString(gpa).equals(value)) {
                        results.add(new StudentRecord(id, name, gpa));
                    }
                }
                pos += StudentRecord.RECORD_SIZE;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return results;
    }
    private String readString(RandomAccessFile file, int size) {
        StringBuilder sb = new StringBuilder(size);
        try {
            for (int i = 0; i < size; i++) {
                sb.append(file.readChar());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString().trim();
    }

    private String CompleteString(String str, int size) {
        StringBuilder sb = new StringBuilder(size);
        if (str != null) {
            sb.append(str);
        }
        while (sb.length() < size) {
            sb.append(' ');
        }
        return sb.toString();
    }
    public void flush() throws IOException {
        if (file != null) {
            file.getFD().sync(); // force OS to flush buffers to disk
        }
    }

    public void close() throws IOException {
        if (file != null) {
            try {
                flush();
            } finally {
                file.close();
                file = null;
            }
        }
    }
}
