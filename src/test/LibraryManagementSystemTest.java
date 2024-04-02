package test;
import main.LibraryManagementSystem;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryManagementSystemTest {


    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    @Test
    public void testConstructor() {
        LibraryManagementSystem l = new LibraryManagementSystem();
        assertEquals(LibraryManagementSystem.State.IDLE, l.getCurrentState());
        assertEquals(0, l.getLibraryBooks().size());
    }

    @Test
    public void testAddBook(){
        LibraryManagementSystem l = new LibraryManagementSystem();
        LibraryManagementSystem.Book book1 = new LibraryManagementSystem.Book("Cool Book", 100, false);
        assertFalse(l.getLibraryBooks().contains(book1));
        l.addBook(book1);
        assertTrue(l.getLibraryBooks().contains(book1));
    }

    @Test
    public void testLoanContainedBook(){
        LibraryManagementSystem l = new LibraryManagementSystem();
        LibraryManagementSystem.Book book1 = new LibraryManagementSystem.Book("Cool Book", 100, false);
        LibraryManagementSystem.Patron patron = new LibraryManagementSystem.Patron("John", "Doe", "fake street", "000 000 00 00", 0);
        l.addBook(book1);
        l.loanBook(book1, patron);
        assertEquals("Book '" + book1.getTitle() + "' loaned to patron '" + patron.getName() + "'.\n", outputStreamCaptor.toString());
        assertEquals(LibraryManagementSystem.State.IDLE, l.getCurrentState());
        assertFalse(l.getLibraryBooks().contains(book1));

    }

    @Test
    public void testLoanNotContainedBook(){
        LibraryManagementSystem l = new LibraryManagementSystem();
        LibraryManagementSystem.Book book1 = new LibraryManagementSystem.Book("Cool Book", 100, false);
        LibraryManagementSystem.Patron patron = new LibraryManagementSystem.Patron("John", "Doe", "fake street", "000 000 00 00", 0);
        l.loanBook(book1, patron);
        assertEquals("Book '" + book1.getTitle() + "' is not available in the library.\n", outputStreamCaptor.toString());
        assertEquals(LibraryManagementSystem.State.IDLE, l.getCurrentState());
        assertFalse(l.getLibraryBooks().contains(book1));
    }

    @Test
    public void testReturnBook(){
        LibraryManagementSystem l = new LibraryManagementSystem();
        LibraryManagementSystem.Book book1 = new LibraryManagementSystem.Book("Cool Book", 10, false);
        LibraryManagementSystem.Patron patron = new LibraryManagementSystem.Patron("John", "Doe", "fake street", "000 000 00 00", 0);
        l.addBook(book1);
        l.returnBook(book1, patron);
        assertEquals("Book '" + book1.getTitle() + "' returned to the library by patron '" + patron.getID() + ".\n", outputStreamCaptor.toString());
        assertEquals(LibraryManagementSystem.State.IDLE, l.getCurrentState());
    }

    @Test
    public void testReturnDamagedBook(){
        LibraryManagementSystem l = new LibraryManagementSystem();
        LibraryManagementSystem.Book book1 = new LibraryManagementSystem.Book("Cool Book", 10, true);
        LibraryManagementSystem.Patron patron = new LibraryManagementSystem.Patron("John", "Doe", "fake street", "000 000 00 00", 0);
        l.addBook(book1);
        l.returnBook(book1, patron);
        assertEquals("Book '" + book1.getTitle() + "' is damaged. Patron '" + patron.getID() + "'' will be charged 15$ for the damage.\n", outputStreamCaptor.toString());
        assertEquals(LibraryManagementSystem.State.IDLE, l.getCurrentState());
    }

    @Test
    public void testReturnLateBook(){
        LibraryManagementSystem l = new LibraryManagementSystem();
        LibraryManagementSystem.Book book1 = new LibraryManagementSystem.Book("Cool Book", 100, false);
        LibraryManagementSystem.Patron patron = new LibraryManagementSystem.Patron("John", "Doe", "fake street", "000 000 00 00", 0);
        l.addBook(book1);
        l.returnBook(book1, patron);
        String output = "Late fees for overdue books managed successfully.\n";
        output += "Book '" + book1.getTitle() + "' is overdue by '" + book1.getDays() + "'' days with a fee of '" + 50.0 + "'' dollars for the patron '" + patron.getID() + ".\n";
        assertEquals(output, outputStreamCaptor.toString());
        assertEquals(LibraryManagementSystem.State.IDLE, l.getCurrentState());
    }

    @Test
    public void testHandleLostBook(){
        LibraryManagementSystem l = new LibraryManagementSystem();
        LibraryManagementSystem.Book book1 = new LibraryManagementSystem.Book("Cool Book", 100, false);
        LibraryManagementSystem.Patron patron = new LibraryManagementSystem.Patron("John", "Doe", "fake street", "000 000 00 00", 0);
        l.handleLostBook(book1, patron);
        String output = "Lost book '" + book1.getTitle() + "' handled for patron '" + patron.getID() + ", who will be charged a fee of 20$.\n";
        assertEquals(output, outputStreamCaptor.toString());
        assertEquals(LibraryManagementSystem.State.IDLE, l.getCurrentState());
        assertFalse(l.getLibraryBooks().contains(book1));
    }

    @Test
    public void  testPerformAdministrativeTasks(){
        LibraryManagementSystem l = new LibraryManagementSystem();
        l.performAdministrativeTasks();
        assertEquals("Administrative tasks performed successfully.\n", outputStreamCaptor.toString());
        assertEquals(LibraryManagementSystem.State.IDLE, l.getCurrentState());
    }

    @Test
    public void testLoanBookWrongInitialState(){
        LibraryManagementSystem l = new LibraryManagementSystem();
        l.libraryManagementError();
        assertEquals(LibraryManagementSystem.State.ERROR, l.getCurrentState());
        LibraryManagementSystem.Book book1 = new LibraryManagementSystem.Book("Cool Book", 100, false);
        LibraryManagementSystem.Patron patron = new LibraryManagementSystem.Patron("John", "Doe", "fake street", "000 000 00 00", 0);
        l.loanBook(book1, patron);
        assertEquals(LibraryManagementSystem.State.ERROR, l.getCurrentState());
        String output = "Error: Operation failed due to unexpected state.\n";
        output += "Cannot loan a book in the current state.\n";
        output += "Error: Operation failed due to unexpected state.\n";
        assertEquals(output, outputStreamCaptor.toString());
    }

    @Test
    public void testReturnBookWrongInitialState(){
        LibraryManagementSystem l = new LibraryManagementSystem();
        l.libraryManagementError();
        assertEquals(LibraryManagementSystem.State.ERROR, l.getCurrentState());
        LibraryManagementSystem.Book book1 = new LibraryManagementSystem.Book("Cool Book", 100, false);
        LibraryManagementSystem.Patron patron = new LibraryManagementSystem.Patron("John", "Doe", "fake street", "000 000 00 00", 0);
        l.returnBook(book1, patron);
        assertEquals(LibraryManagementSystem.State.ERROR, l.getCurrentState());
        String output = "Error: Operation failed due to unexpected state.\n";
        output += "Cannot return a book in the current state.\n";
        output += "Error: Operation failed due to unexpected state.\n";
        assertEquals(output, outputStreamCaptor.toString());
    }

    @Test
    public void testManageLateFeeWrongInitialState(){
        LibraryManagementSystem l = new LibraryManagementSystem();
        l.libraryManagementError();
        assertEquals(LibraryManagementSystem.State.ERROR, l.getCurrentState());
        float res = l.manageLateFees(1);
        assertEquals(LibraryManagementSystem.State.ERROR, l.getCurrentState());
        assertEquals(0.0, res);
        String output = "Error: Operation failed due to unexpected state.\n";
        output += "Cannot manage late fees in the current state.\n";
        output += "Error: Operation failed due to unexpected state.\n";
        assertEquals(output, outputStreamCaptor.toString());
    }

    @Test
    public void testHandleLostBookWrongInitialState(){
        LibraryManagementSystem l = new LibraryManagementSystem();
        l.libraryManagementError();
        assertEquals(LibraryManagementSystem.State.ERROR, l.getCurrentState());
        LibraryManagementSystem.Book book1 = new LibraryManagementSystem.Book("Cool Book", 100, false);
        LibraryManagementSystem.Patron patron = new LibraryManagementSystem.Patron("John", "Doe", "fake street", "000 000 00 00", 0);
        l.handleLostBook(book1, patron);
        assertEquals(LibraryManagementSystem.State.ERROR, l.getCurrentState());
        String output = "Error: Operation failed due to unexpected state.\n";
        output += "Cannot handle lost book in the current state.\n";
        output += "Error: Operation failed due to unexpected state.\n";
        assertEquals(output, outputStreamCaptor.toString());
    }

    @Test
    public void testAdministrativeTaskInitialState(){
        LibraryManagementSystem l = new LibraryManagementSystem();
        l.libraryManagementError();
        assertEquals(LibraryManagementSystem.State.ERROR, l.getCurrentState());
        l.performAdministrativeTasks();
        assertEquals(LibraryManagementSystem.State.ERROR, l.getCurrentState());
        String output = "Error: Operation failed due to unexpected state.\n";
        output += "Cannot perform administrative tasks in the current state.\n";
        output += "Error: Operation failed due to unexpected state.\n";
        assertEquals(output, outputStreamCaptor.toString());
    }


    @Test
    public void testPatron(){
        String name = "John";
        String surname = "Doe";
        String address = "fake street";
        String phone = "000 000 00 00";
        int id = 0;
        LibraryManagementSystem.Patron p = new LibraryManagementSystem.Patron(name, surname, address, phone, id);
        assertEquals(name, p.getName());
        assertEquals(surname, p.getSurname());
        assertEquals(address, p.getAddress());
        assertEquals(phone, p.getPhone());
        assertEquals(id, p.getID());
    }

    @Test
    public void testBookUndamaged(){
        String title = "Cool Book";
        int days = 100;
        boolean damaged = false;
        LibraryManagementSystem.Book b = new LibraryManagementSystem.Book(title, days, damaged);
        assertEquals(title, b.getTitle());
        assertEquals(days, b.getDays());
        assertFalse(b.getDamage());
    }

    @Test
    public void testBookDamaged(){
        String title = "Cool Book";
        int days = 100;
        boolean damaged = true;
        LibraryManagementSystem.Book b = new LibraryManagementSystem.Book(title, days, damaged);
        assertEquals(title, b.getTitle());
        assertEquals(days, b.getDays());
        assertTrue(b.getDamage());
    }
}
