package com.yusufgun.busify.mapper;

import com.yusufgun.busify.dto.response.UserResponse;
import com.yusufgun.busify.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);
}