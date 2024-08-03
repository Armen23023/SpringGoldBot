package am.relex.dao;

import am.relex.entity.AppUser;
import am.relex.entity.enums.UserState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserDAO extends JpaRepository<AppUser,Long> {

   Optional<AppUser> findByTelegramUserId(Long id);

   Optional<AppUser> findByEmail(String email);

}

