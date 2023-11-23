package pro.b2borganizer.services.users.control;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pro.b2borganizer.services.users.entity.User;

@Repository
public interface UsersRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}
