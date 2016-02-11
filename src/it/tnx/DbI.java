/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.tnx;

import java.sql.Connection;

/**
 *
 * @author mceccarelli
 */
public interface DbI {

    public Connection getDbConn();

}
