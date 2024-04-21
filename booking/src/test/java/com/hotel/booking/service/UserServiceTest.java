package com.hotel.booking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.booking.dtos.request.CreateUser;
import com.hotel.booking.entity.User;
import com.hotel.booking.repository.UserRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserService userService;

    @Test
    public void testRegisterUser() {
        CreateUser createUser = new CreateUser();
        createUser.setUsername("testUser");
        createUser.setEmail("test@example.com");
        User user = new User();
        user.setUsername(createUser.getUsername());
        user.setEmail(createUser.getEmail());

        when(objectMapper.convertValue(createUser, User.class)).thenReturn(user);
        when(userRepo.save(user)).thenReturn(user);

        User registeredUser = userService.registerUser(createUser);
        verify(objectMapper).convertValue(createUser, User.class);
        verify(userRepo).save(user);
        Assertions.assertEquals(user, registeredUser);
    }
}
