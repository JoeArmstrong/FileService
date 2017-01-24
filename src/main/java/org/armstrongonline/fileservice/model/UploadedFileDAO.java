package org.armstrongonline.fileservice.model;

import java.sql.SQLException;
import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

public interface UploadedFileDAO {
	/**
	 * This is the method to be used to initialize database resources ie.
	 * connection.
	 */
	public void setDataSource(DataSource ds);

	/**
	 * This is the method to be used to create a record in the fileservice table.
	 * @throws InvalidDataAccessApiUsageException 
	 * @throws SQLException 
	 */
	public Long create(byte[] password, byte[] salt) throws SQLException, InvalidDataAccessApiUsageException;
	
	/*
	 * Method to query the fileservice table to get the entry
	 * @throws DataAccessException
	 */
	public UploadedFile find( Long id ) throws DataAccessException;
}
