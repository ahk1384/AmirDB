use std::io::{self, Write};

// ==========================================
// Domain Models
// ==========================================
#[derive(Debug, Clone)]
struct Student {
    id: i32,
    name: String,
    gpa: f64,
}

// ==========================================
// Layer 3: Storage Engine (Trait)
// ==========================================
trait Collection {
    fn insert_one(&mut self, student: Student);
    fn delete_one(&mut self, id: i32);
    fn find_by_id(&self, id: i32) -> Option<&Student>;
    fn find_all(&self) -> Vec<&Student>;
}

struct ArrayCollection {
    data: Vec<Student>,
}

impl ArrayCollection {
    fn new() -> Self {
        ArrayCollection { data: Vec::new() }
    }
}

impl Collection for ArrayCollection {
    fn insert_one(&mut self, student: Student) {
        // TODO: Implement
    }
    fn delete_one(&mut self, id: i32) {
        // TODO: Implement
    }
    fn find_by_id(&self, id: i32) -> Option<&Student> {
        // TODO: Implement
        None
    }
    fn find_all(&self) -> Vec<&Student> {
        Vec::new()
    }
}

struct LinkedListCollection {
    // TODO: Implement logic (Rust LinkedList is tricky, maybe use std::collections::LinkedList or custom unsafe)
    // For students, maybe suggest using standard library for this part unless they are advanced
}

// ==========================================
// Layer 2: Execution Engine
// ==========================================
struct ExecutionEngine {
    current_collection: Box<dyn Collection>,
    // TODO: transaction_stack
    // TODO: batch_queue
}

impl ExecutionEngine {
    fn new() -> Self {
        ExecutionEngine {
            current_collection: Box::new(ArrayCollection::new()),
        }
    }

    fn execute_command(&mut self, command_type: &str, args: Vec<&str>) {
        match command_type {
            "insertOne" => println!("Executing insertOne..."),
            "findByID" => println!("Executing findByID..."),
            _ => println!("Unknown command"),
        }
    }
}

// ==========================================
// Layer 1: Query Parser
// ==========================================
struct QueryParser;

impl QueryParser {
    fn parse_and_execute(input: &str, engine: &mut ExecutionEngine) {
        let tokens: Vec<&str> = input.split_whitespace().collect();
        if tokens.is_empty() {
            return;
        }

        // TODO: Proper parsing
        let cmd = tokens[0];
        engine.execute_command(cmd, tokens);
    }
}

// ==========================================
// Main Loop
// ==========================================
fn main() {
    let mut engine = ExecutionEngine::new();
    println!("UniDB Shell (Rust)\nType 'exit' to quit.");

    loop {
        print!("unidb> ");
        io::stdout().flush().unwrap();

        let mut input = String::new();
        match io::stdin().read_line(&mut input) {
            Ok(_) => {
                let input = input.trim();
                if input == "exit" {
                    break;
                }
                QueryParser::parse_and_execute(input, &mut engine);
            }
            Err(error) => println!("Error: {}", error),
        }
    }
}

