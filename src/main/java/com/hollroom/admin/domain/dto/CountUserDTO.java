package com.hollroom.admin.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CountUserDTO {

    private long newUsers;

    private long deactivatedUsers;

    private long totalUsers;

}
