package pro.b2borganizer.services.templates.control;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pro.b2borganizer.services.templates.entity.Template;

@Repository
public interface TemplateRepository extends MongoRepository<Template, String> {

    Optional<Template> findByCode(String code);
}
