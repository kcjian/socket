package com.kcj.db;

import java.sql.SQLException;


public class DBUtil {
    
	private static DBUtil dbUtil;
    private DBUtil(){
    	
    }

    public static DBUtil getInstances(){
    	if(dbUtil==null){
    		dbUtil=new DBUtil();
    	}
    	return dbUtil;
    }
    //测试用三台手机存
    public void insertAMessage(String message)
    {
    	DBHelp dbHelp=new DBHelp("insert into a values(NULL,?)");
    	try {
    		dbHelp.pst.setString(1, message);
			dbHelp.pst.executeUpdate();
			dbHelp.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void insertBMessage(String message)
    {
    	DBHelp dbHelp=new DBHelp("insert into b values(NULL,?)");
    	try {
    		dbHelp.pst.setString(1, message);
			dbHelp.pst.executeUpdate();
			dbHelp.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void insertCMessage(String message)
    {
    	DBHelp dbHelp=new DBHelp("insert into c values(NULL,?)");
    	try {
    		dbHelp.pst.setString(1, message);
			dbHelp.pst.executeUpdate();
			dbHelp.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
}
