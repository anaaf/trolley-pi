package com.crunch.user;

import com.crunch.user.entity.User;
import com.crunch.user.model.UserRequestModel;
import com.crunch.user.model.UserResponseModel;
import com.crunch.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;

    public UserResponseModel createUser(UserRequestModel userRequestModel) {

        User user = userRepository.save(transformToEntity(userRequestModel));
        return transformToResponseModel(user);
    }


    private static User transformToEntity(UserRequestModel requestModel) {

        User user = new User();
        user.setUserName(requestModel.getName());
        user.setEmail(requestModel.getEmail());
        user.setPhoneNo(requestModel.getPhoneNo());
        user.setPassword(requestModel.getPassword());

        return user;
    }

    private static UserResponseModel transformToResponseModel(User user) {

        UserResponseModel responseModel = new UserResponseModel();

        responseModel.setUuid(user.getUuid()); // Assuming you have UUID in the entity
        responseModel.setName(user.getUserName());
        responseModel.setEmail(user.getEmail());
        responseModel.setPhoneNo(user.getPhoneNo());

        // Assuming you want the creationDate to be converted to a string (e.g., in ISO format)
        if (user.getCreationDate() != null) {
            responseModel.setCreatedAt(user.getCreationDate().toString());
        }
        return responseModel;
    }

}
