package com.fw.services;

import java.util.List;

import javax.naming.NamingException;

import com.fw.bean.AllGroups;
import com.fw.bean.ContactDetailUserInfo;
import com.fw.bean.GetAllUserBean;
/**
 * 
 * @author Sumit Srivastava
 *
 */
import com.fw.bean.LDAPUserResponseBean;
import com.fw.bean.UserBean;
import com.fw.bean.UserDetailInfo;
import com.fw.exceptions.APIExceptions;

public interface IUserService {

	LDAPUserResponseBean loginUser(UserBean userBean) throws APIExceptions;

	List<GetAllUserBean> getUserList(String groupValue) throws NamingException,
			Exception;

	List<AllGroups> getGroups() throws APIExceptions, Exception;

	List<UserDetailInfo> getUserDetailsByUserName(String userID);

	List<ContactDetailUserInfo> getUserContactDetailsByUserName(String userID);

	String updateUserPassword(String username, String oldPassword,
			String newPassword) throws APIExceptions;

	void createUser(String adminUsername, String adminPass, String firstName,
			String middleName, String lastName, String email, String userName,
			String postalAddress, String telephoneNo, String password)
			throws APIExceptions;
}
