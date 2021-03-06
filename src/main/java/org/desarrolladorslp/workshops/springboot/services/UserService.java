package org.desarrolladorslp.workshops.springboot.services;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.desarrolladorslp.workshops.springboot.forms.RegistrationForm;
import org.desarrolladorslp.workshops.springboot.models.User;


public interface UserService {
    User saveUser(User user);

    List<User> getAll();

    User findByEmail(String email);

    User findById(Long id) throws EntityNotFoundException;

    User findByUsername(String username);

    void deleteById(Long id) throws EntityNotFoundException;

    User updateUser(User user);

    User createUser(RegistrationForm registrationForm);
}
