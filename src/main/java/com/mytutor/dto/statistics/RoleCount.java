package com.mytutor.dto.statistics;

import com.mytutor.constants.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoleCount {
    private Role role;
    private Long count;
}
