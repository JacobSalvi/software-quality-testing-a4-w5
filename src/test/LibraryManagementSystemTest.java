package test;
import main.LibraryManagementSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryManagementSystemTest {

    @Test
    public void testConstructor() {
        LibraryManagementSystem l = new LibraryManagementSystem();
        LibraryManagementSystem.Book b = new LibraryManagementSystem.Book("a", 1, false);
    }
}
