package com.example.services.hibernate;

import com.example.db.HibernateConfig;
import com.example.models.User;
import com.example.repositories.UserRepository;
import com.example.repositories.impl.hibernate.UserHibernateRepository;
import com.example.services.AuthService;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class HibernateAuthService implements AuthService {
    private final UserHibernateRepository userRepo;

    public HibernateAuthService(UserHibernateRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public Optional<User> login(String login, String rawPassword) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            userRepo.setSession(session);
            return userRepo.findByLogin(login)
                    .filter(user -> BCrypt.checkpw(rawPassword, user.getPassword()));
        }
    }

    @Override
    public boolean register(String login, String rawPassword, String role) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            userRepo.setSession(session);

            if(userRepo.findByLogin(login).isPresent()) {
                return false;
            }

            String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

            User user = User.builder()
                    .login(login)
                    .password(hashed)
                    .role(role)
                    .build();

            userRepo.save(user);
            tx.commit();
            return true;
        }
    }
}
