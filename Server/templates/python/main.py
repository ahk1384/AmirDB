from abc import ABC, abstractmethod
from typing import List, Optional

# ==========================================
# Domain Models
# ==========================================
class Student:
    def __init__(self, id: int, name: str, gpa: float):
        self.id = id
        self.name = name
        self.gpa = gpa

    def __repr__(self):
        return f"{{id: {self.id}, name: '{self.name}', gpa: {self.gpa}}}"

# ==========================================
# Layer 3: Storage Engine (Interface)
# ==========================================
class Collection(ABC):
    @abstractmethod
    def insert_one(self, student: Student):
        pass

    @abstractmethod
    def delete_one(self, id: int):
        pass

    @abstractmethod
    def find_by_id(self, id: int) -> Optional[Student]:
        pass

    @abstractmethod
    def find_all(self) -> List[Student]:
        pass

class ArrayCollection(Collection):
    def __init__(self):
        self.data = []  # Python list is a dynamic array

    def insert_one(self, student: Student):
        # TODO: Implement
        pass

    def delete_one(self, id: int):
        # TODO: Implement
        pass

    def find_by_id(self, id: int) -> Optional[Student]:
        # TODO: Implement
        return None

    def find_all(self) -> List[Student]:
        return []

class LinkedListCollection(Collection):
    class Node:
        def __init__(self, data):
            self.data = data
            self.next = None
            self.prev = None

    def __init__(self):
        self.head = None
        self.tail = None

    def insert_one(self, student: Student):
        # TODO: Implement
        pass

    def delete_one(self, id: int):
        # TODO: Implement
        pass

    def find_by_id(self, id: int) -> Optional[Student]:
        # TODO: Implement
        return None

    def find_all(self) -> List[Student]:
        return []

# ==========================================
# Layer 2: Execution Engine
# ==========================================
class ExecutionEngine:
    def __init__(self):
        self.current_collection = ArrayCollection()
        # TODO: self.transaction_stack = []
        # TODO: self.batch_queue = []

    def execute_command(self, command_type: str, args: List[str]):
        if command_type == "insertOne":
            print("Executing insertOne...")
            # TODO: Parse args
        elif command_type == "findByID":
            pass
        # TODO: Handle others

# ==========================================
# Layer 1: Query Parser
# ==========================================
class QueryParser:
    @staticmethod
    def parse_and_execute(input_str: str, engine: ExecutionEngine):
        tokens = input_str.split()
        if not tokens:
            return

        # TODO: Improve parsing logic
        # Example: db.students.insertOne({...}) -> extract 'insertOne' and json
        cmd = tokens[0]
        engine.execute_command(cmd, tokens[1:])

# ==========================================
# Main Loop
# ==========================================
if __name__ == "__main__":
    engine = ExecutionEngine()
    print("UniDB Shell (Python)\nType 'exit' to quit.")

    while True:
        try:
            user_input = input("unidb> ")
            if user_input == "exit":
                break
            QueryParser.parse_and_execute(user_input, engine)
        except KeyboardInterrupt:
            break

