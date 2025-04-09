package com.dw.NAMANSOLOJAVA.Repository;

import com.dw.NAMANSOLOJAVA.model.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ToDoRepository extends JpaRepository<ToDo, Long> {
    @Query("SELECT t FROM ToDo t WHERE t.user.username = :username AND t.type = :type")
    List<ToDo> findAllByUsernameAndType(String username, String type);
    Optional<ToDo> findByIdAndUserUsername(Long id, String username);

}
