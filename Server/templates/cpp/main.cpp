#include <iostream>
#include <string>
#include <vector>
#include <sstream>

using namespace std;

// ==========================================
// Domain Models
// ==========================================
struct Student {
    int id;
    string name;
    double gpa;
};

// ==========================================
// Layer 3: Storage Engine (Interface)
// ==========================================
class Collection {
public:
    virtual void insertOne(const Student& student) = 0;
    virtual void deleteOne(int id) = 0;
    virtual Student* findByID(int id) = 0;
    virtual vector<Student> findAll() = 0;
    virtual ~Collection() {}
};

class ArrayCollection : public Collection {
    // TODO: Use a dynamic array (e.g. std::vector or raw pointer with resizing)
public:
    void insertOne(const Student& student) override {
        // TODO: Implement O(1) insert
    }
    void deleteOne(int id) override {
        // TODO: Implement O(n) delete with shift
    }
    Student* findByID(int id) override {
        // TODO: Implement O(n) search
        return nullptr;
    }
    vector<Student> findAll() override {
        return {};
    }
};

class LinkedListCollection : public Collection {
    struct Node {
        Student data;
        Node* next;
        Node* prev;
    };
    // TODO: Manage head and tail
public:
    void insertOne(const Student& student) override {
        // TODO: Implement linked list insert
    }
    void deleteOne(int id) override {
        // TODO: Implement linked list delete
    }
    Student* findByID(int id) override {
        // TODO: Implement linked list search
        return nullptr;
    }
    vector<Student> findAll() override {
        return {};
    }
};

// ==========================================
// Layer 2: Execution Engine
// ==========================================
class ExecutionEngine {
    Collection* currentCollection;
    // TODO: Add Stack for Transactions
    // TODO: Add Queue for Batch Processing

public:
    ExecutionEngine() {
        // Default to ArrayCollection for starter
        currentCollection = new ArrayCollection();
    }

    void executeCommand(string commandType, vector<string> args) {
        if (commandType == "insertOne") {
            // Parse args and call currentCollection->insertOne(...)
            cout << "Executing insertOne..." << endl;
        } else if (commandType == "findByID") {
            // Call currentCollection->findByID(...)
        }
        // TODO: Handle other commands
    }
};

// ==========================================
// Layer 1: Query Parser
// ==========================================
class QueryParser {
public:
    static void parseAndExecute(string input, ExecutionEngine& engine) {
        // Simple tokenizer
        stringstream ss(input);
        string segment;
        vector<string> tokens;
        while (getline(ss, segment, ' ')) {
            tokens.push_back(segment);
        }

        if (tokens.empty()) return;

        // Example parsing logic (needs to be improved to handle JSON-like syntax)
        // This is just a placeholder
        string cmd = tokens[0]; 
        vector<string> args;
        for(size_t i=1; i<tokens.size(); i++) args.push_back(tokens[i]);
        
        engine.executeCommand(cmd, args);
    }
};

// ==========================================
// Main Loop (Client Simulation)
// ==========================================
int main() {
    ExecutionEngine engine;
    string input;

    cout << "UniDB Shell (C++)\nType 'exit' to quit.\n";

    while (true) {
        cout << "unidb> ";
        getline(cin, input);
        if (input == "exit") break;

        QueryParser::parseAndExecute(input, engine);
    }

    return 0;
}

