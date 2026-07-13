package com.yusufgun.busify.mapper;

import com.yusufgun.busify.dto.response.UserResponse;
import com.yusufgun.busify.entity.User;
import com.yusufgun.busify.enums.Role;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-13T16:57:14+0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse toUserResponseWithoutRole(User user) {
        if ( user == null ) {
            return null;
        }

        Long id = null;
        String firstName = null;
        String lastName = null;
        String email = null;
        String tcNo = null;

        id = user.getId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        email = user.getEmail();
        tcNo = user.getTcNo();

        Role role = null;

        UserResponse userResponse = new UserResponse( id, firstName, lastName, email, tcNo, role );

        return userResponse;
    }

    @Override
    public UserResponse toUserResponseWithRole(User user) {
        if ( user == null ) {
            return null;
        }

        Long id = null;
        String firstName = null;
        String lastName = null;
        String email = null;
        String tcNo = null;
        Role role = null;

        id = user.getId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        email = user.getEmail();
        tcNo = user.getTcNo();
        role = user.getRole();

        UserResponse userResponse = new UserResponse( id, firstName, lastName, email, tcNo, role );

        return userResponse;
    }
}
