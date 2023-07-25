package com.fw.services.impl;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.naming.NamingException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fw.bean.AllGroups;
import com.fw.bean.ContactDetailUserInfo;
import com.fw.bean.GetAllUserBean;
import com.fw.bean.LDAPUserResponseBean;
import com.fw.bean.UserBean;
import com.fw.bean.UserDetailInfo;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IUserService;
import com.fw.utils.LDAPDirContextUtil;

@Service
public class UserServiceImpl implements IUserService {

	// private Logger log = Logger.getLogger(UserServiceImpl.class);

	LDAPDirContextUtil ldapUtil = new LDAPDirContextUtil();
	final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper

	@Override
	public LDAPUserResponseBean loginUser(UserBean userBean)
			throws APIExceptions {
		String username = userBean.getUsername();
		String password = userBean.getPassword();

		Map<String, String> userAttr = ldapUtil.verifyUser(username, password);
		LDAPUserResponseBean lDAPUserResponseBean = mapper.convertValue(
				userAttr, LDAPUserResponseBean.class);
		return lDAPUserResponseBean;
	}

	@Override
	public List<GetAllUserBean> getUserList(String groupValue)
			throws NamingException, Exception {

		GetAllUserBean getAllUserBean;
		List<GetAllUserBean> allUserList = new ArrayList<>();
		Map<String, List<String>> lDapUsersList = ldapUtil
				.getLDapUsers(groupValue);
		for (int i = 0; i < lDapUsersList.get("cn").size(); i++) {
			Map<String, String> userMap = new HashMap<>();
			userMap.put("userName", lDapUsersList.get("uid").get(i));
			userMap.put("fullName", lDapUsersList.get("cn").get(i));
			getAllUserBean = mapper.convertValue(userMap, GetAllUserBean.class);
			allUserList.add(getAllUserBean);
		}
		return allUserList;
	}

	@Override
	public List<AllGroups> getGroups() throws Exception {

		Map<String, String> groups = ldapUtil.getGroups();
		AllGroups getAllGroupsBean;
		List<AllGroups> allUserList = new ArrayList<>();
		List<String> values = new ArrayList<String>(groups.values());
		List<String> keySet = new ArrayList<String>(groups.keySet());
		for (int i = 0; i < values.size(); i++) {
			Map<String, String> groupsMap = new HashMap<>();
			groupsMap.put("des", keySet.get(i));
			groupsMap.put("names", values.get(i));
			getAllGroupsBean = mapper.convertValue(groupsMap, AllGroups.class);
			allUserList.add(getAllGroupsBean);
		}
		return allUserList;
	}

	@Override
	public List<UserDetailInfo> getUserDetailsByUserName(String userID) {
		Map<String, String> groups = ldapUtil.getUserDetailsByUserName(userID);
		UserDetailInfo userInfo;
		List<UserDetailInfo> allUserDetail = new ArrayList<>();
		userInfo = mapper.convertValue(groups, UserDetailInfo.class);
		allUserDetail.add(userInfo);
		return allUserDetail;
	}

	@Override
	public List<ContactDetailUserInfo> getUserContactDetailsByUserName(
			String userID) {
		Map<String, String> groups = ldapUtil.getUserDetailsByUserName(userID);
		ContactDetailUserInfo userInfo;
		List<ContactDetailUserInfo> allUserDetail = new ArrayList<>();
		userInfo = mapper.convertValue(groups, ContactDetailUserInfo.class);
		allUserDetail.add(userInfo);
		return allUserDetail;
	}

	@Override
	public String updateUserPassword(String username, String oldPassword,
			String newPassword) throws APIExceptions {
		return ldapUtil.updatePassword(username, oldPassword, newPassword);
	}

	@Override
	public void createUser(final String adminUsername, final String adminPass,
			final String firstName, final String middleName,
			final String lastName, final String email, final String userName,
			final String postalAddress, final String telephoneNo,
			final String password) throws APIExceptions {
		ldapUtil.createUser(adminUsername, adminPass, firstName, middleName,
				lastName, email, userName, postalAddress, telephoneNo, password);
	}

}
