package com.example.cj.videoeditor.fileserversdk.setting;

public class SQL {
    public final static String CREATE_TABLE=
            "CREATE TABLE IF NOT EXISTS "+ Setting.MYSQL_TABLE_NAME+"(\n" +
                    "id VARCHAR(40) PRIMARY KEY,\n" +
                    "access_code VARCHAR(100) NOT NULL,\n" +
                    "content LONGBLOB,\n" +
                    "create_time BIGINT,\n" +
                    "size BIGINT,\n" +
                    "file_name VARCHAR(40) NOT NULL\n"+
                    ") DEFAULT CHARSET=utf8;";
    public final static String INSERT=
            "INSERT INTO "+ Setting.MYSQL_TABLE_NAME+"\n" +
                    "(id,access_code,content,create_time,size,file_name)\n" +
                    "VALUES\n" +
                    "(?,?,?,?,?,?);";
    public final static String SELECT_NO_CONTENT=
            "SELECT access_code,create_time,size,file_name FROM "+ Setting.MYSQL_TABLE_NAME+" WHERE id=?;";
    public final static String SELECT=
            "SELECT access_code,create_time,size,file_name,content FROM "+ Setting.MYSQL_TABLE_NAME+" WHERE id=?;";
    public final static String UPDATE_INFO=
            "UPDATE "+ Setting.MYSQL_TABLE_NAME+" SET access_code=?,create_time=?,size=?,file_name=? WHERE id=?;";
    public final static String UPDATE_CONTENT=
            "UPDATE "+ Setting.MYSQL_TABLE_NAME+" SET content=? WHERE id=?;";
    public final static String DELETE=
            "DELETE FROM "+ Setting.MYSQL_TABLE_NAME+" WHERE id=?;";
}
