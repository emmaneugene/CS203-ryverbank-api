package com.csdg1t3.ryverbankapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import com.csdg1t3.ryverbankapi.account.*;
import com.csdg1t3.ryverbankapi.user.*;
import com.csdg1t3.ryverbankapi.security.*;
import com.csdg1t3.ryverbankapi.trade.TradeRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.annotation.Id;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {
    @Mock 
    private AccountRepository accountRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private TransferRepository transferRepo;

    @Mock
    private UserAuthenticator uAuth;

    @InjectMocks
    private AccountController accountController;

    private User user = new User((long) 1, "Test User", "S9926201Z", "92307743", "23 Hume Rd", "testUser", "testing", "ROLE_USER", true);

    @Test
    void addAccount_ValidUserId_ReturnsSavedAccount(){ 
        //Arrange
        Account newAccount = new Account(Long.valueOf(1), user, user.getId(), 1000.0, 1000.0);
            
        //mock userRepo behaviour
        when(userRepo.findById(any(Long.class))).thenReturn(Optional.of(user));

        //mock accountRepo behaviour
        when(accountRepo.save(newAccount)).thenReturn(newAccount);

        //act
        Account savedAccount = accountController.addAccount(newAccount);

        //assert result
        assertNotNull(savedAccount);
        verify(userRepo).findById(newAccount.getCustomer_id());
        verify(accountRepo).save(newAccount);

    }
    
    @Test
    void addAccount_InvalidUserID_ThrowsAccountNotValidException(){
        // Arrange
        user.setId(Long.valueOf(100));
        Account newAccount = new Account(Long.valueOf(100), user, user.getId(), 1000.0, 1000.0);

        // mock userRepo behaviour
        when(userRepo.findById(any(Long.class))).thenReturn(Optional.empty());

        // Act and assert result
        assertThrows(AccountNotValidException.class, () -> accountController.addAccount(newAccount));
        verify(userRepo).findById(newAccount.getCustomer_id());
    }

    
    //assert that account balance is valid
    @Test
    void addAccount_ValidAccountBalance_ReturnsSavedAccount(){
        //Arrange
        Account newAccount = new Account(Long.valueOf(1), user, user.getId(), 1000.0, 1000.0);
            
        //mock userRepo behaviour
        when(userRepo.findById(any(Long.class))).thenReturn(Optional.of(user));

        //mock accountRepo behaviour
        when(accountRepo.save(newAccount)).thenReturn(newAccount);

        //act
        Account savedAccount = accountController.addAccount(newAccount);

        //assert result
        assertNotNull(savedAccount);
        assertEquals(savedAccount, newAccount);
        verify(userRepo).findById(newAccount.getCustomer_id());
        verify(accountRepo).save(newAccount);
    }

    @Test
    void addAccount_InvalidAccountBalance_ThrowsAccountNotValidException() { 
        Account newAccount = new Account(Long.valueOf(100), user, user.getId(), -1000.0, -1000.0);

        // mock userRepo behaviour
        when(userRepo.findById(any(Long.class))).thenReturn(Optional.empty());

        // Act and assert result
        assertThrows(AccountNotValidException.class, () -> accountController.addAccount(newAccount));
        verify(userRepo).findById(newAccount.getCustomer_id());
    }

    @Test
    void getAccount_isOwnAccount_ReturnAccount() {
        Long id = Long.valueOf(1);
        Account newAccount = new Account(id, user, user.getId(), 1000.0, 1000.0);

        Optional<Account> found = Optional.of(newAccount);
        when(accountRepo.findById((any(Long.class)))).thenReturn(found);
        when(uAuth.getAuthenticatedUser()).thenReturn(user);

        Account returned = accountController.getAccount(id);

        // Assert result
        assertEquals(returned, newAccount);
        verify(accountRepo).findById(id);
        verify(uAuth).getAuthenticatedUser();
    }

    @Test
    void getAccount_AccountNotFound_ThrowAccountNotFoundException() {
        Long id = Long.valueOf(10000);
        Account newAccount = new Account(id, user, user.getId(), 1000.0, 1000.0);

        when(accountRepo.findById((any(Long.class)))).thenReturn(Optional.empty());
        // Assert result
        assertThrows(AccountNotFoundException.class, () -> accountController.getAccount(id), "Could not find account 10000");
        verify(accountRepo).findById(id);
    }


    @Test
    void getAccount_isOtherUser_ThrowsRoleNotAuthorisedException() {
       //Arrange
       Long id1 = Long.valueOf(1);
       Long id2 = Long.valueOf(2);
       Account newAccount = new Account(id1, user, user.getId(), 1000.0, 1000.0);
       User user2 = new User(id2, "Test User", "S9926201Z", "92307743", "23 Hume Rd", "testUser", "testing", "ROLE_USER", true);

       Optional<Account> found = Optional.of(newAccount);
       when(accountRepo.findById(any(Long.class))).thenReturn(found);
       when(uAuth.getAuthenticatedUser()).thenReturn(user2);

       //assert result
       assertThrows(RoleNotAuthorisedException.class, () -> accountController.getAccount(id1), "You cannot view another customer's accounts");
       verify(accountRepo).findById(id1);
       verify(uAuth).getAuthenticatedUser();
    }

    @Test
    void createTransfer_isOwnAccount_ReturnTransfer() {
        Long id1 = Long.valueOf(1);
        Long id2 = Long.valueOf(2);
        Account newAccount = new Account(id1, user, user.getId(), 1000.0, 1000.0);
        User user2 = new User(id2, "Test User", "S9926201Z", "92307743", "23 Hume Rd", "testUser", "testing", "ROLE_USER", true);
        Account account2 = new Account(id2, user2, user2.getId(), 1100.0, 1100.0);
        Transfer transfer = new Transfer(id1, newAccount, account2, id1, id2, 50.0);

        when(transferRepo.save(transfer)).thenReturn(transfer);
        when(accountRepo.findById(id1)).thenReturn(Optional.of(newAccount));
        when(accountRepo.findById(id2)).thenReturn(Optional.of(account2));
        when(uAuth.getAuthenticatedUser()).thenReturn(user);

        Transfer savedTransfer = accountController.createTransfer(id1, transfer);

        assertNotNull(savedTransfer);
        verify(accountRepo).findById(id1);
        verify(accountRepo).findById(id2);
        verify(transferRepo).save(transfer);
        verify(uAuth).getAuthenticatedUser();
    }

    @Test
    void createTransfer_sameSenderAndReceivierAccount_ThrowsTransferNotValidException() {
        Long id1 = Long.valueOf(1);
        Account newAccount = new Account(id1, user, user.getId(), 1000.0, 1000.0);
        Transfer transfer = new Transfer(id1, newAccount, newAccount, id1, id1, 50.0);

        assertThrows(TransferNotValidException.class, () -> accountController.createTransfer(id1, transfer), "Sender and receiver fields cannot be identical");
    }

    @Test
    void createTransfer_sentFromAnotherAccount_ThrowsAccountNotValidException() {
        Long id1 = Long.valueOf(1);
        Long id2 = Long.valueOf(2);
        Account newAccount = new Account(id1, user, user.getId(), 1000.0, 1000.0);
        User user2 = new User(id2, "Test User", "S9926201Z", "92307743", "23 Hume Rd", "testUser", "testing", "ROLE_USER", true);
        Account account2 = new Account(id2, user2, user2.getId(), 1100.0, 1100.0);
        Transfer transfer = new Transfer(id1, newAccount, account2, id1, id2, 50.0);

        assertThrows(AccountNotValidException.class, () -> accountController.createTransfer(id2, transfer), "'From' field must match account ID in URL");
    }

    @Test
    void createTransfer_invalidSender_ThrowsAccountNotValidException() {
        Long id1 = Long.valueOf(1);
        Long id2 = Long.valueOf(2);
        Account newAccount = new Account(id1, user, user.getId(), 1000.0, 1000.0);
        User user2 = new User(id2, "Test User", "S9926201Z", "92307743", "23 Hume Rd", "testUser", "testing", "ROLE_USER", true);
        Account account2 = new Account(id2, user2, user2.getId(), 1100.0, 1100.0);
        Transfer transfer = new Transfer(id1, newAccount, account2, id1, id2, 50.0);

        when(accountRepo.findById(id1)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountController.createTransfer(id1, transfer), transfer.getFrom() + "");
        verify(accountRepo).findById(id1);
    }

    @Test
    void createTransfer_notAuthenticated_ThrowsAccountNotValidException() {
        Long id1 = Long.valueOf(1);
        Long id2 = Long.valueOf(2);
        Account newAccount = new Account(id1, user, user.getId(), 1000.0, 1000.0);
        User user2 = new User(id2, "Test User", "S9926201Z", "92307743", "23 Hume Rd", "testUser", "testing", "ROLE_USER", true);
        Account account2 = new Account(id2, user2, user2.getId(), 1100.0, 1100.0);
        Transfer transfer = new Transfer(id1, newAccount, account2, id1, id2, 50.0);

        when(accountRepo.findById(id1)).thenReturn(Optional.of(newAccount));
        when(uAuth.getAuthenticatedUser()).thenReturn(user2);

        assertThrows(RoleNotAuthorisedException.class, () -> accountController.createTransfer(id1, transfer), "You cannot transfer funds from another person's account");
        verify(accountRepo).findById(id1);
        verify(uAuth).getAuthenticatedUser();
    }

    @Test
    void createTransfer_insufficientBalance_ThrowsAccountNotValidException() {
        Long id1 = Long.valueOf(1);
        Long id2 = Long.valueOf(2);
        Account newAccount = new Account(id1, user, user.getId(), 1000.0, 1000.0);
        User user2 = new User(id2, "Test User", "S9926201Z", "92307743", "23 Hume Rd", "testUser", "testing", "ROLE_USER", true);
        Account account2 = new Account(id2, user2, user2.getId(), 1100.0, 1100.0);
        Transfer transfer = new Transfer(id1, newAccount, account2, id1, id2, 1100.0);

        when(accountRepo.findById(id1)).thenReturn(Optional.of(newAccount));
        when(uAuth.getAuthenticatedUser()).thenReturn(user);

        assertThrows(TransferNotValidException.class, () -> accountController.createTransfer(id1, transfer), "Insufficient funds in account for transfer");
        verify(accountRepo).findById(id1);
        verify(uAuth).getAuthenticatedUser();
    }

    @Test
    void createTransfer_invalidReceiver_ThrowsAccountNotValidException() {
        Long id1 = Long.valueOf(1);
        Long id2 = Long.valueOf(2);
        Account newAccount = new Account(id1, user, user.getId(), 1000.0, 1000.0);
        User user2 = new User(id2, "Test User", "S9926201Z", "92307743", "23 Hume Rd", "testUser", "testing", "ROLE_USER", true);
        Account account2 = new Account(id2, user2, user2.getId(), 1100.0, 1100.0);
        Transfer transfer = new Transfer(id1, newAccount, account2, id1, id2, 50.0);

        when(accountRepo.findById(id1)).thenReturn(Optional.of(newAccount));
        when(uAuth.getAuthenticatedUser()).thenReturn(user);
        when(accountRepo.findById(id2)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountController.createTransfer(id1, transfer), transfer.getTo() + "");
        verify(accountRepo).findById(id1);
        verify(uAuth).getAuthenticatedUser();
        verify(accountRepo).findById(id2);
    }

    @Test
    void getTransaction_isInvolvedAccount_ReturnTransaction() {
        Long id1 = Long.valueOf(1);
        Long id2 = Long.valueOf(2);
        Account newAccount = new Account(id1, user, user.getId(), 1000.0, 1000.0);
        User user2 = new User(id2, "Test User", "S9926201Z", "92307743", "23 Hume Rd", "testUser", "testing", "ROLE_USER", true);
        Account account2 = new Account(id2, user2, user2.getId(), 1100.0, 1100.0);
        Transfer transfer = new Transfer(id1, newAccount, account2, id1, id2, 50.0);
        List<Transfer> foundSender = new ArrayList<Transfer>();
        foundSender.add(transfer);

        when(transferRepo.findBySenderIdOrReceiverId(id1, id1)).thenReturn(foundSender);
        when(accountRepo.findById(id1)).thenReturn(Optional.of(newAccount));
        when(uAuth.getAuthenticatedUser()).thenReturn(user);

        List<Transfer> savedTransfers = accountController.getTransfers(id1);

        assertEquals(savedTransfers, foundSender);
        verify(accountRepo).findById(id1);
        verify(transferRepo).findBySenderIdOrReceiverId(id1, id1);
        verify(uAuth).getAuthenticatedUser();
    }

    @Test
    void getTransaction_isUninvolvedAccount_ThrowsRoleNotAuthorisedException() {
        Long id1 = Long.valueOf(1);
        Long id2 = Long.valueOf(2);
        Account newAccount = new Account(id1, user, user.getId(), 1000.0, 1000.0);
        User user2 = new User(id2, "Test User", "S9926201Z", "92307743", "23 Hume Rd", "testUser", "testing", "ROLE_USER", true);
        Account account2 = new Account(id2, user, user.getId(), 1100.0, 1100.0);
        Transfer transfer = new Transfer(id1, newAccount, account2, id1, id2, 50.0);
        List<Transfer> foundSender = new ArrayList<Transfer>();
        foundSender.add(transfer);

        when(accountRepo.findById(id1)).thenReturn(Optional.of(newAccount));
        when(uAuth.getAuthenticatedUser()).thenReturn(user2);

        assertThrows(RoleNotAuthorisedException.class, () -> accountController.getTransfers(id1), "You cannot view another customer's accounts");
        verify(accountRepo).findById(id1);
        verify(uAuth).getAuthenticatedUser();
    }

    @Test
    void getAccounts_ReturnAllAccounts(){
        List<Account> accounts = new ArrayList<Account>();
        when(uAuth.getAuthenticatedUser()).thenReturn(user);
        when(accountRepo.findByCustId(any(Long.class))).thenReturn(accounts);
        

        List<Account> returned = accountController.getAccounts();
        assertEquals(returned, accounts);
        verify(uAuth).getAuthenticatedUser();
        verify(accountRepo).findByCustId((long) 1);
    }
}
