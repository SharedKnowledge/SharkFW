/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.wasp;

/**
 *
 * @author micha
 */
public enum Direction {
    IN,
    OUT,
    INOUT,
    NO;

    @Override
    public String toString() {
        return name();
    }
}
