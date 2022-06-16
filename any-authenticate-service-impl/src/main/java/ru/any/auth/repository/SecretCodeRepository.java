package ru.any.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.any.auth.model.SecretCode;

import java.util.Optional;

public interface SecretCodeRepository extends JpaRepository<SecretCode, Long>, JpaSpecificationExecutor<SecretCode> {

    Optional<SecretCode> findByPhone(String phone);
}
