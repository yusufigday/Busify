package com.yusufgun.busify.mapper;

import com.yusufgun.busify.dto.UserResponse;
import com.yusufgun.busify.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toUserResponse(User user){
        if (user == null){
            return null;
        }

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());

        return response;
    }

}
