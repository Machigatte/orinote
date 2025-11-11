package com.chalkim.orinote.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.chalkim.orinote.model.Note;
import com.chalkim.orinote.model.User;

public interface NoteRepository extends JpaRepository<Note, Long>, JpaSpecificationExecutor<Note> {

    boolean existsByIdAndUserAndIsDeletedFalse(Long id, User user);

    boolean existsByIdAndUserAndArchivedAtIsNotNull(Long id, User user);

    Optional<Note> findByIdAndUserAndIsDeletedFalse(Long id, User user);

    List<Note> findAllByUserAndIsDeletedFalseOrderByCreatedAtDesc(User user);

    List<Note> findAllByUserAndCreatedAtBetweenAndIsDeletedFalseOrderByCreatedAtDesc(User user, Instant from, Instant to);
}
