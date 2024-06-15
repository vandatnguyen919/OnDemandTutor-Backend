/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.mytutor.constants;

/**
 *
 * @author HIEU
 */
public enum Role {
    STUDENT("student"),
    TUTOR("tutor"),
    ADMIN("admin"),
    MODERATOR("moderator");
    
    Role(String role) {
    }

    public static Role getRole(String role) {
        if (role == null) return null;
        if (role.equalsIgnoreCase(STUDENT.toString())) {
            return STUDENT;
        } else if (role.equalsIgnoreCase(TUTOR.toString())) {
            return TUTOR;
        } else if (role.equalsIgnoreCase(ADMIN.toString())) {
            return ADMIN;
        } else if (role.equalsIgnoreCase(MODERATOR.toString())) {
            return MODERATOR;
        }
        return null;
    }
}
