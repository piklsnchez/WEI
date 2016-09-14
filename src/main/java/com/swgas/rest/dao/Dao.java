package com.swgas.rest.dao;

import java.util.logging.Logger;
import org.postgresql.ds.PGPoolingDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.IntStream;

public class Dao{
    private static final String CLASS = Dao.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
    private static Dao instance = new Dao();
    private final PGPoolingDataSource source;
    
    private Dao(){
        source = new PGPoolingDataSource();
        source.setDataSourceName("WEI datasource");
        source.setServerName("localhost");
        source.setDatabaseName("wei");
        source.setUser("postgres");
        source.setPassword("password");
        source.setInitialConnections(1);
        source.setMaxConnections(10);
    }
    
    public static Dao getInstance(){
        return instance;
    }
    
    public List<Map<String, String>> getAllUsers(){
        return query("select first_name, last_name from users");
    }
    
    public List<Map<String, String>> getAllUsersWithInfo(){
        return query("select first_name, last_name, description, location, phone, email from users u join user_info i on (u.uid = i.users_uid)");
    }
    
    public List<Map<String, String>> getAllSessions(){
        return query("select name, title from sessions");
    }
    
    public List<Map<String, String>> getAllSessionsWithFacilitators(){
        return query("select s.name session_name, s.title session_title, u.first_name first_name, u.last_name last_name from sessions s join session_facilitators sf on (s.uid = sf.sessions_uid) join users u on (u.uid = sf.users_uid)");
    }
    
    public List<Map<String, String>> getAllSessionsWithNotes(){
        return query("select s.name session_name, s.title session_title, n.uid note_id, n.notes note from sessions s join session_notes sn on (s.uid = sn.sessions_uid) join notes n on (n.uid = sn.sessions_uid)");
    }
    
    public List<Map<String, String>> getPlanningDates(){
        return query("select date, title from planning_dates order by date asc");
    }
    
    public void updateNote(int id, String note){
        String update = "update notes set notes = ? where uid = ?";
        try(Connection conn = source.getConnection();
            PreparedStatement state = conn.prepareStatement(update)){
            state.setString(1, note);
            state.setInt(2, id);
            state.executeUpdate();
        } catch(SQLException e){
            LOG.throwing(CLASS, "updateNotes", e);
            throw new RuntimeException(e.getCause());
        }
    }
    
    private List<Map<String, String>> query(String query){
        List<Map<String, String>> returnList = new LinkedList<>();
        try(Connection conn = source.getConnection();
            Statement state = conn.createStatement();
            ResultSet rs = state.executeQuery(query)){
                
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            String[] names = IntStream.rangeClosed(1, columnCount).mapToObj(i -> {
                try {
                    return rsmd.getColumnLabel(i);
                } catch(SQLException e){
                    throw new RuntimeException(e);
                }
            }).toArray(String[]::new);
            while(rs.next()){
                Map<String, String> map = new HashMap<>();
                Arrays.stream(names).forEach(name -> {
                    try{
                        map.put(name, rs.getString(name));
                    } catch(SQLException e){
                        throw new RuntimeException(e);
                    }
                });
                returnList.add(map);
            }
        } catch(Exception e){
            LOG.severe(""+e);
            throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
        }
        return returnList;
    }
}