package com.hotel.booking.controller;

import com.hotel.booking.dtos.request.CreateUser;
import com.hotel.booking.entity.User;
import com.hotel.booking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Users")
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * This function is used for registering a new user in a Java application.
     *
     * @param createUser The `createUser` parameter in the `registerUser` method is of type
     *                   `CreateUser`, which is a class representing the data needed to create a new user. This parameter
     *                   is annotated with `@RequestBody` to indicate that the data will be obtained from the request
     *                   body, and `@Valid
     * @return The method `registerUser` is returning a `ResponseEntity` object with the HTTP status
     * code `201 Created` and the newly registered `User` object in the response body.
     */
    @PostMapping
    @Operation(summary = "This API is used for registering new user.", description = "Register User")
    public ResponseEntity<User> registerUser(@RequestBody @Valid CreateUser createUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(createUser));
    }
}
