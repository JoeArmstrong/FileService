package org.armstrongonline.fileservice.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

public class UploadedFileMapper implements RowMapper<UploadedFile> {
   public UploadedFile mapRow(ResultSet rs, int rowNum) throws SQLException {
     
      UploadedFile file = new UploadedFile();
      file.setId(rs.getInt("id"));
      file.setPassword(rs.getBytes("password"));
      file.setSalt(rs.getBytes("salt"));
      Timestamp timestamp = rs.getTimestamp("create_date");
      Date date = timestamp;
      file.setCreateDate(date);
      return file;
   }
}