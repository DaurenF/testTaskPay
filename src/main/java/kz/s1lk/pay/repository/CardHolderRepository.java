package kz.s1lk.pay.repository;

import kz.s1lk.pay.model.entity.CardHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardHolderRepository extends JpaRepository<CardHolder, Long> {
    Optional<CardHolder> findByEmail(String email);
}
