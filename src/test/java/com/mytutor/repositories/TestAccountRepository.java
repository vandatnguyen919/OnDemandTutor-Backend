package com.mytutor.repositories;

import com.mytutor.entities.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TestAccountRepository {

    @Autowired
    private AccountRepository accountRepository;

    private Account testAccount;

    @BeforeEach
    public void setUp() {
        testAccount = new Account();
        testAccount.setEmail("test@example.com");
        testAccount.setPassword("Password123."); // Ensure password is encoded if needed
        testAccount.setPhoneNumber("1234567890");

        accountRepository.save(testAccount);
    }

    @Test
    public void testFindByEmail() {
        Optional<Account> foundAccount = accountRepository.findByEmail("test@example.com");
        assertTrue(foundAccount.isPresent());
        assertEquals(testAccount.getEmail(), foundAccount.get().getEmail());
    }

    @Test
    public void testExistsByEmail() {
        boolean exists = accountRepository.existsByEmail("test@example.com");
        assertTrue(exists);
    }

    @Test
    public void testExistsByPhoneNumber() {
        boolean exists = accountRepository.existsByPhoneNumber("1234567890");
        assertTrue(exists);
    }

    @Test
    public void testFindByEmail_NotFound() {
        Optional<Account> foundAccount = accountRepository.findByEmail("nonexistent@example.com");
        assertFalse(foundAccount.isPresent());
    }

    @Test
    public void testExistsByEmail_NotFound() {
        boolean exists = accountRepository.existsByEmail("nonexistent@example.com");
        assertFalse(exists);
    }

    @Test
    public void testExistsByPhoneNumber_NotFound() {
        boolean exists = accountRepository.existsByPhoneNumber("0987654321");
        assertFalse(exists);
    }
}
