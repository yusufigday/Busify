package com.yusufgun.busify.dto.request;

import com.yusufgun.busify.enums.Role;

public record ChangeRoleRequest(
        String email,
        Role newRole
) {
}
