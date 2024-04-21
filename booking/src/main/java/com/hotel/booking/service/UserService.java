package com.hotel.booking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.booking.dtos.request.CreateUser;
import com.hotel.booking.entity.User;
import com.hotel.booking.exception.CustomException;
import com.hotel.booking.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepo userRepo;

    /**
     * The function registers a new user by converting the input data to a User object and saving it to
     * the user repository.
     *
     * @param createUser The `createUser` parameter is likely an object that contains the data needed to
     *                   create a new user. It could include fields such as username, email, password, and any other
     *                   information required to register a user. This data is then converted into a `User` object using an
     *                   `objectMapper`
     * @return The `registerUser` method returns a `User` object after saving it to the user repository.
     */
    public User registerUser(CreateUser createUser) {
        User user = objectMapper.convertValue(createUser, User.class);
        return userRepo.save(user);
    }

}
