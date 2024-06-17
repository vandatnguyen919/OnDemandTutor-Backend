/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.constants;

/**
 *
 * @author Nguyen Van Dat
 */
public enum DegreeType {
    ASSOCIATE(1),
    BACHELOR(2),
    MASTER(3),
    DOCTORAL(4);

    private final int ranking;

    DegreeType(int ranking) {
        this.ranking = ranking;
    }

    public int getRanking() {
        return ranking;
    }

    public static DegreeType getDegreeType(String degreeType) {
        if (degreeType == null) return null;
        if (degreeType.equalsIgnoreCase(ASSOCIATE.toString())) {
            return ASSOCIATE;
        } else if (degreeType.equalsIgnoreCase(BACHELOR.toString())) {
            return BACHELOR;
        } else if (degreeType.equalsIgnoreCase(MASTER.toString())) {
            return MASTER;
        } else if (degreeType.equalsIgnoreCase(DOCTORAL.toString())) {
            return DOCTORAL;
        }
        return null;
    }
}
