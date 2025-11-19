package com.moau.moau.accounting.common.repository;

import com.moau.moau.accounting.common.domain.Category;
import com.moau.moau.accounting.common.domain.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByTeamIdAndNameAndType(Long teamId, String name, TransactionType type);

    boolean existsByTeamIdAndNameAndTypeAndIdNot(Long teamId, String name, TransactionType type, Long id);

    List<Category> findByTeamId(Long teamId);

    List<Category> findByTeamIdAndType(Long teamId, TransactionType type);

    Optional<Category> findByIdAndTeamId(Long id, Long teamId);
}