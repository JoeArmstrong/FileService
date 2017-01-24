package org.armstrongonline.fileservice.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class UploadedFileJDBC implements UploadedFileDAO {
	private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;

	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	@Override
	public Long create(final byte[] password, final byte[] salt) throws SQLException, InvalidDataAccessApiUsageException {
		final String SQL = "insert into fileservicedata (password, salt, create_date) values (?, ?, ?)";
		
		java.util.Date date = new Date();
		final Timestamp timestamp = new Timestamp(date.getTime());
		
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplateObject.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(SQL, new String[] {"id"});
                ps.setBytes(1, password);
                ps.setBytes(2,  salt);
                ps.setTimestamp(3, timestamp);
                return ps;
            }

        }, keyHolder);
        Long lastId = (Long) keyHolder.getKey();
		return lastId;
	}

	@Override
	public UploadedFile find(Long id ) throws DataAccessException {
		String SQL = "select * from fileservicedata where id = ?";
		
		
		UploadedFile file = jdbcTemplateObject.queryForObject(SQL, 
                      new Object[]{id}, new UploadedFileMapper());
        
		return file;
		
	}

}
