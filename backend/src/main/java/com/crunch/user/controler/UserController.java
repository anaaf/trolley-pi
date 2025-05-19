package com.crunch.user.controler;

import com.crunch.user.UserService;
import com.crunch.user.api.UserApi;
import com.crunch.user.model.UserRequestModel;
import com.crunch.user.model.UserResponseModel;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1")
public class UserController implements UserApi {


    @Autowired
    UserService userService;

    @Override
    public ResponseEntity<UserResponseModel> createUser(UserRequestModel userRequestModel) {
        return ResponseEntity.ok(userService.createUser(userRequestModel));
    }

    @Override
    public ResponseEntity<Object> getUser(String uuid) {
        // calling
        throw new NotImplementedException("getUser");
    }
}
