package com.manager_account.repositories;

import com.manager_account.entities.Account;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
    Optional<Account> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void deleteByHaibazoAccountId(Long haibazoAccountId);
    Optional<Account> findByHaibazoAccountId(Long haibazoAccountId);   
}
