package org.armstrongonline.fileservice.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.armstrongonline.fileservice.model.UploadedFile;
import org.armstrongonline.fileservice.model.UploadedFileJDBC;
import org.armstrongonline.fileservice.service.FileService;
import org.armstrongonline.fileservice.service.PasswordElements;
import org.armstrongonline.fileservice.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class FileServiceController {

	@Autowired
	private UploadedFileJDBC jdbct;

	@Autowired
	private FileService fService;

	@RequestMapping(path = "/fileservice/file", method = RequestMethod.POST)
	public @ResponseBody String post(@RequestParam("file") MultipartFile file,
			@RequestParam(value = "password", required = false) String password, RedirectAttributes redirectAttributes,
			HttpServletResponse response) throws IOException {

		Long id;
		try {
			PasswordService psvc = new PasswordService();
			PasswordElements pw = psvc.encryptPassword(password);
			id = jdbct.create(pw.getPassword(), pw.getSalt());
			
		} catch (InvalidDataAccessApiUsageException | SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not create entry in DB");
			return "Could not upload " + file.getOriginalFilename();
		}

		String filename = null;
		if ((filename = fService.saveFile(file, id)) != null) {
			response.setStatus(HttpServletResponse.SC_OK);
			return "The URL for file: https://54.203.116.194:8443/fileservice/file/" + filename + "/";
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not save file");
			return "Could not upload " + file.getOriginalFilename();
		}

	}

	@RequestMapping(path = "/fileservice/file/{filename}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<ByteArrayResource> get(@PathVariable String filename,
			@RequestParam(value = "password", required = false) String password, HttpServletResponse response)
			throws IOException {
		System.out.println("In GET with filename of " + filename);
		
		String fileId = getId(filename);
		if (fileId == null) {
			System.out.println("Resource name is an incorrect format - " + filename);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Resource name is an incorrect format - " + filename);
			return new ResponseEntity<ByteArrayResource>(HttpStatus.BAD_REQUEST);
		}
		
		Long id = (long) 0;
		try {
			id = Long.valueOf(fileId);
		} catch (NumberFormatException ex) {
			System.out.println("fileId parameter is not a valid number - " + fileId);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"fileId parameter is not a valid number - " + fileId);
			return new ResponseEntity<ByteArrayResource>(HttpStatus.BAD_REQUEST);
		}

		UploadedFile fileRecord = null;
		try {
			fileRecord = jdbct.find(id);
		} catch (DataAccessException ex) {
			System.out.println("Could not find matching fileId in DB");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not find matching fileId in DB");
			return new ResponseEntity<ByteArrayResource>(HttpStatus.BAD_REQUEST);
		}

		Date expireDate = DateUtils.addHours(fileRecord.getCreateDate(), 24);
		Date now = new Date();
		if (now.after(expireDate)) {
			// The link has expired
			System.out.println("Link expired");
			response.sendError(HttpServletResponse.SC_GONE, "Link Expired");
			return new ResponseEntity<ByteArrayResource>(HttpStatus.GONE);
		}

		if (fileRecord.getPassword() != null && fileRecord.getPassword().length > 0) {
			PasswordService psvc = new PasswordService();
			PasswordElements pEle = new PasswordElements();
			pEle.setPassword(fileRecord.getPassword());
			pEle.setSalt(fileRecord.getSalt());
			if (psvc.comparePasswords(pEle, password) == false) {
				System.out.println("This file is protected by a password - trying to redirect...");
				HttpHeaders headers = new HttpHeaders();
				headers.add("Location", "/collectpw.html?filename=" + filename + "/");
				return new ResponseEntity<ByteArrayResource>(headers, HttpStatus.FOUND);
			}
			System.out.println("...you got the right password...");
		}

		File file = new File(fService.filePathFromId(filename));

		Path path = Paths.get(file.getAbsolutePath());
		ByteArrayResource resource;
		try {
			System.out.println("returning the content for file " + path.toString());
			resource = new ByteArrayResource(Files.readAllBytes(path));
			System.out.println("Setting header filename as " + filename);
			return ResponseEntity.ok()
					.contentLength(file.length())
					.contentType(MediaType.parseMediaType("application/octet-stream"))
					.header("content-disposition", "attachment; filename=" + filename)
					.body(resource);
		} catch (IOException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to read file");
			return new ResponseEntity<ByteArrayResource>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String getId( String data) {
		int ndx = data.indexOf('-');
		if (ndx == -1)
			return null;
		
		String result = data.substring(0, data.indexOf('-'));
		return result;
	}
}
