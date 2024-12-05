package project.kafka.cpf_validator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.kafka.cpf_validator.entities.UserEntity;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    List<UserEntity> findByCpf(String cpf);
}
