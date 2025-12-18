// java
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
            file = null;
        }

        indexMap = new HashMap<>();
        nextPosition = 0;
        loadExistingRecords();
    }

    private void ensureFileOpen() {
        if (file == null) {
            throw new IllegalStateException("RandomAccessFile is not open");
        }
    }

    private void loadExistingRecords() {
        if (file == null) return;
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

            nextPosition = (fileLen / StudentRecord.RECORD_SIZE) * StudentRecord.RECORD_SIZE;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean writeRecord(StudentRecord record) {
        ensureFileOpen();
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
            file.writeChars(completeString(record.getName(), StudentRecord.NAME_SIZE));
            file.writeDouble(record.getGpa());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        indexMap.put(record.getId(), position);
        return true;
    }

    public List<StudentRecord> readAllRecord() {
        ensureFileOpen();
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
        ensureFileOpen();
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
        ensureFileOpen();
        Long position = indexMap.get(studentId);
        if (position == null) {
            return false;
        }
        try {
            file.seek(position);
            file.writeLong(-1L);
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

    public long getRecordCount() {
        return indexMap.size();
    }

    public double sumOfFiled(String fieldName) {
        ensureFileOpen();
        double sum = 0.0;
        try {
            long fileLen = file.length();
            long pos = 0;
            while (pos + StudentRecord.RECORD_SIZE <= fileLen) {
                file.seek(pos);
                long id = file.readLong();
                String name = readString(file, StudentRecord.NAME_SIZE);
                double gpa = file.readDouble();
                if (id != -1L) {
                    if ("gpa".equals(fieldName)) {
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

    public double averageOfFiled(String fieldName) {
        ensureFileOpen();
        double sum = 0.0;
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
                    if ("gpa".equals(fieldName)) {
                        sum += gpa;
                        count++;
                    }
                }
                pos += StudentRecord.RECORD_SIZE;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return count == 0 ? 0.0 : sum / count;
    }

    public List<StudentRecord> filterByFiled(String fieldName, String value) {
        ensureFileOpen();
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
                    if ("name".equals(fieldName) && name.equals(value)) {
                        results.add(new StudentRecord(id, name, gpa));
                    } else if ("gpa".equals(fieldName)) {
                        try {
                            double v = Double.parseDouble(value);
                            if (Double.compare(gpa, v) == 0) {
                                results.add(new StudentRecord(id, name, gpa));
                            }
                        } catch (NumberFormatException ignore) {
                        }
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

    private String completeString(String str, int size) {
        StringBuilder sb = new StringBuilder(size);
        if (str != null) {
            if (str.length() > size) {
                sb.append(str, 0, size);
            } else {
                sb.append(str);
            }
        }
        while (sb.length() < size) {
            sb.append(' ');
        }
        return sb.toString();
    }

    public void flush() throws IOException {
        ensureFileOpen();
        if (file != null) {
            file.getFD().sync();
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
