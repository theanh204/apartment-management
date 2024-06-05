package com.ou.repositories.impl;

import com.ou.exception.AppException;
import com.ou.exception.ErrorCode;
import com.ou.pojo.User;
import com.ou.repositories.UserRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.Objects;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User getUserByUsername(String username) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM User WHERE username = :username");
        q.setParameter("username", username);
        if (q.getResultList().isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return (User) q.getSingleResult();
    }

    @Override
    public void addUser(User user) {
        Session s = this.factory.getObject().getCurrentSession();
        s.save(user);
    }

    @Override
    public boolean authUser(String username, String password) {
        User  u = this.getUserByUsername(username);

        return this.bCryptPasswordEncoder.matches(password, u.getPassword());
    }

    @Override
    public boolean userExistsByUsername(String username) {
        try {
            this.getUserByUsername(username);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
