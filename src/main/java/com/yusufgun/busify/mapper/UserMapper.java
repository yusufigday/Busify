package com.yusufgun.busify.mapper;

import com.yusufgun.busify.dto.response.UserResponse;
import com.yusufgun.busify.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "role", ignore = true)
    UserResponse toUserResponseWithoutRole(User user);
    
    UserResponse toUserResponseWithRole(User user);

    default UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return isAdmin ? toUserResponseWithRole(user) : toUserResponseWithoutRole(user);
    }
}