package com.gabriel.plannerapi.task;

import com.gabriel.plannerapi.user.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByUser(UserAccount user);
    Optional<Task> findByIdAndUser(UUID id, UserAccount user);
}
