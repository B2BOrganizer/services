package pro.b2borganizer.services.tokens.control;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pro.b2borganizer.services.tokens.entity.APIToken;
import pro.b2borganizer.services.users.entity.User;

@Repository
public interface APITokensRepository extends MongoRepository<APIToken, String> {
    Optional<APIToken> findByUsernameAndActiveTrue(String username);

    Optional<APIToken> findByTokenAndActiveTrue(String token);
}
