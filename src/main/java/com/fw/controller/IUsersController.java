package com.fw.controller;

import java.util.List;

import javax.naming.NamingException;

import com.fw.bean.LoginResultBean;
import com.fw.bean.UserBean;
import com.fw.exceptions.APIExceptions;
/**
 * 
 * @author Sumit Srivastava
 *
 */
import org.springframework.http.ResponseEntity;

public interface IUsersController {

	ResponseEntity<?> loginUser(UserBean userBean, String gcaptha);

	boolean isUserLoggedIn();

	ResponseEntity<List<?>> getUserList(String groupValue)
			throws APIExceptions, NamingException, Exception;

	ResponseEntity<List<?>> getGroups() throws APIExceptions, NamingException,
			Exception;

	ResponseEntity<List<?>> getUserDetail(String userID)
			throws NamingException, APIExceptions, Exception;

	ResponseEntity<List<?>> getContactDetail(String userID)
			throws NamingException, APIExceptions, Exception;

	ResponseEntity<String> updateUserPassword(String username,
			String oldPassword, String newPassword, String gcaptha)
			throws APIExceptions;

	void createUser(String adminUsername, String adminPass, String firstName,
			String middleName, String lastName, String email, String userName,
			String postalAddress, String telephoneNo, String password)
			throws APIExceptions;

	LoginResultBean login(UserBean userBean) throws APIExceptions;
}
