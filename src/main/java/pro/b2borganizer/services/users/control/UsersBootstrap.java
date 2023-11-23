package pro.b2borganizer.services.users.control;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import pro.b2borganizer.services.users.entity.User;

@Component
@RequiredArgsConstructor
public class UsersBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    private final UsersRepository usersRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        usersRepository.deleteAll();

        User user = new User();
        user.setUsername("admin");
        user.setPassword(bCryptPasswordEncoder.encode("admin"));

        usersRepository.save(user);
    }
}
