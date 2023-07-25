package com.fw.utils;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;

@Component
public class LDAPDirContextUtil {

	private static Logger log = Logger.getLogger(LDAPDirContextUtil.class);

	private static final Hashtable<String, String> environment = new Hashtable<String, String>();
	private static final Properties props = new Properties();
	private static final String[] attributesArr = { "uidnumber:uniqueLDAPId",
			"employeenumber:employeeNumber", "givenname:firstName",
			"sn:lastName", "cn:fullName", "mail:email",
			"telephonenumber:contactNumber", "gidnumber:grid",
			"homepostaladdress:postalAddress", "uid:userName" };

	/**
	 * Create the user with given values as argument. Throw exception if user is
	 * not created.
	 * 
	 * @arguments: String firstName
	 * @arguments: String middleName
	 * @arguments: String lastName
	 * @arguments: String email
	 * @arguments: String userName
	 * @arguments: String postalAddress
	 * @arguments: String telephoneNo
	 * @arguments: String password
	 */
	public void createUser(String adminUsername, String adminPass,
			String firstName, String middleName, String lastName, String email,
			String userName, String postalAddress, String telephoneNo,
			String password) throws APIExceptions {

		Map<String, List<String>> lDapUsers = new LinkedHashMap<String, List<String>>();
		int maxEmployeeNumber = 0;
		int maxUIDNumber = 0;
		try {
			lDapUsers = getLDapUsers("all");
			// verifying if the user name exist or not in the LDap
			List<String> userNamelist = lDapUsers.get("uid");
			if (userNamelist.contains(userName)) {
				throw new APIExceptions("Username [" + userName
						+ "] already exist. Please choose another.");
			}

			// getting the max employee number
			List<String> employeeNumberlist = lDapUsers.get("employeeNumber");

			for (String empNo : employeeNumberlist) {
				if (Integer.parseInt(empNo) > maxEmployeeNumber) {
					maxEmployeeNumber = Integer.parseInt(empNo);
				}
			}

			// getting the max UID number
			List<String> UIDNumberlist = lDapUsers.get("uidNumber");

			for (String UIDNo : UIDNumberlist) {
				if (Integer.parseInt(UIDNo) > maxUIDNumber) {
					maxUIDNumber = Integer.parseInt(UIDNo);
				}
			}
		} catch (APIExceptions e) {
			throw new APIExceptions(
					"Some error occured while getting the user info : "
							+ e.getMessage());
		}

		StringBuffer sb = new StringBuffer(firstName);
		if (!(null == middleName || middleName.trim().equals(""))) {
			sb = sb.append(" ").append(middleName);
		}
		String commonName = sb.append(" ").append(lastName).toString();

		Attribute cn = new BasicAttribute("cn", commonName);
		Attribute mail = new BasicAttribute("mail", email);
		Attribute employeeNumber = new BasicAttribute("employeeNumber",
				"" + (maxEmployeeNumber++));
		Attribute gidNumber = new BasicAttribute("gidNumber", "500");
		Attribute givenName = new BasicAttribute("givenName", firstName);
		Attribute homeDirectory = new BasicAttribute("homeDirectory",
				"/home/users/" + userName);
		Attribute homePostalAddress = new BasicAttribute("homePostalAddress",
				postalAddress);
		Attribute userPassword = new BasicAttribute("userPassword", password);
		Attribute sn = new BasicAttribute("sn", lastName);
		Attribute telephoneNumber = new BasicAttribute("telephoneNumber",
				telephoneNo);
		Attribute uidNumber = new BasicAttribute("uidNumber",
				"" + (maxUIDNumber++));
		Attribute uid = new BasicAttribute("uid", userName);

		Attribute oc = new BasicAttribute("objectClass");
		oc.add("top");
		oc.add("posixAccount");
		oc.add("inetOrgPerson");
		// oc.add("user");
		Attributes entry = new BasicAttributes(true);
		entry.put(oc);
		// entry.put(sAMAccountName);
		entry.put(cn);
		entry.put(mail);
		entry.put(employeeNumber);
		entry.put(gidNumber);
		entry.put(givenName);
		entry.put(homeDirectory);
		entry.put(homePostalAddress);
		entry.put(userPassword);
		entry.put(sn);
		entry.put(telephoneNumber);
		entry.put(uidNumber);
		entry.put(uid);

		DirContext context = null;
		try {
			context = connectLDapServer(context, adminUsername, adminPass);
			String entryDN = "cn=" + commonName
					+ ",ou=People,DC=faberwork,DC=com";
			context.createSubcontext(entryDN, entry);
		} catch (Exception ex) {
			String e = "Error occured while creating new user in the system : "
					+ ex.getMessage();
			log.error(e);
			throw new APIExceptions(e);
		}
	}

	/**
	 * Verify the given user's credentials and return the attributes if the
	 * authentication is a success
	 * 
	 * @throws APIExceptions
	 * 
	 * @arguments: String username
	 * @arguments: String password
	 * @returns: Map<String, String> with all the attributes
	 */
	public Map<String, String> verifyUser(String username, String password)
			throws APIExceptions {
		DirContext context = null;
		// Validating username and password
		if (null == username || username.equals("")) {
			log.error("Username cannot be blank");
			return null;
		} else if (null == password || password.equals("")) {
			log.error("Password cannot be blank");
			return null;
		}
		try {
			context = connectLDapServer(context);
			// adding search criteria for the given username
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> res = context.search(
					props.getProperty("ldap.searchbase"), "uid=" + username,
					sc);
			if (res.hasMore()) {
				SearchResult s = res.next();
				String nameInNamespace = s.getNameInNamespace();
				// Verifying if the user has given the correct password
				if (null == verifyUserPassword(nameInNamespace, password)) {
					log.info("User password is incorrect");
					return null;
				}

				// Retrieve list of groups in which given user is assigned
				Map<String, String> userGroups = getUserGroups(context,
						username);
				Map<String, String> attributes = getUserAttributes(
						s.getAttributes());
				attributes.putAll(userGroups);
				// Returning the user attributes after the successful
				// authentication
				return attributes;
			}
			throw new APIExceptions(
					"User credentials are incorrect or server is down");
		} catch (AuthenticationNotSupportedException exception) {
			log.error("The authentication is not supported by the server");
			throw new APIExceptions(
					"The authentication is not supported by the server");
		} catch (AuthenticationException exception) {
			log.error("Incorrect password or username");
			throw new APIExceptions("Incorrect password or username");
		} catch (NamingException exception) {
			log.error("Error when trying to contact the authetication server : "
					+ exception.getMessage());
			throw new APIExceptions(
					"Error when trying to contact the authetication server");
		} catch (Exception e) {
			log.error(
					"Error occured in user authorization : " + e.getMessage());
			throw new APIExceptions(
					"Error occured in user authorization : " + e.getMessage());
		} finally {
			try {
				if (null != context) {
					context.close();
				}
			} catch (NamingException e) {
				log.info("Error occured while closing context");
			}
		}
	}

	public Map<String, String> getUserDetailsByUserName(String username) {
		DirContext context = null;
		// Validating username and password
		try {
			context = connectLDapServer(context);
			// adding search criteria for the given username
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> res = context.search(
					props.getProperty("ldap.searchbase"), "uid=" + username,
					sc);
			if (res.hasMore()) {
				SearchResult s = res.next();

				// Retrieve list of groups in which given user is assigned
				Map<String, String> userGroups = getUserGroups(context,
						username);
				Map<String, String> attributes = getUserAttributes(
						s.getAttributes());
				attributes.putAll(userGroups);

				// Returning the user attributes after the successful
				// authentication
				return attributes;
			}
		} catch (AuthenticationNotSupportedException exception) {
			log.error("The authentication is not supported by the server");
		} catch (AuthenticationException exception) {
			log.error("Incorrect username");
		} catch (NamingException exception) {
			log.error("Error when trying to create the context");
		} catch (Exception e) {
			log.error("Error occured while gettting user information."
					+ e.getMessage());
		} finally {
			try {
				if (null != context) {
					context.close();
				}
			} catch (NamingException e) {
				log.info("Error occured while closing context");
			}
		}
		return null;
	}

	/**
	 * Used to update the password on the LDap server.
	 * 
	 * @arguments: String username
	 * @arguments: String oldPassword
	 * @arguments: String newPassword
	 * 
	 * @return String based on the final result
	 */
	public String updatePassword(String username, String oldPassword,
			String newPassword) {
		DirContext context = null;
		InitialDirContext newContext = null;
		if (null == username || username.equals("")) {
			return "Username cannot be blank";
		} else if (null == oldPassword || oldPassword.equals("")) {
			return "Old password cannot be blank";
		} else if (null == newPassword || newPassword.equals("")) {
			return "New password cannot be blank";
		}

		if (oldPassword.equalsIgnoreCase(newPassword)) {
			return "Old and new password cannot be same";
		}
		try {
			// LdapContext ctx = new InitialLdapContext(environment, null);
			context = connectLDapServer(context);
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> res = context.search(
					props.getProperty("ldap.searchbase"), "uid=" + username,
					sc);
			if (res.hasMore()) {
				SearchResult s = res.next();
				String nameInNamespace = s.getNameInNamespace();
				newContext = verifyUserPassword(nameInNamespace, oldPassword);

				// Defining the modification rules
				ModificationItem[] mods = new ModificationItem[2];
				mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE,
						new BasicAttribute("userPassword",
								digestMd5(oldPassword)));
				mods[1] = new ModificationItem(DirContext.ADD_ATTRIBUTE,
						new BasicAttribute("userPassword",
								digestMd5(newPassword)));

				String theUserName = s.getName() + ","
						+ props.getProperty("ldap.searchbase");

				// Performing the update on LDap server
				newContext.modifyAttributes(theUserName, mods);
				return "Password updated successfully";
			} else {
				return "User [" + username
						+ "] is not found in the system. Please check the credentials";
			}
		} catch (AuthenticationNotSupportedException exception) {
			log.error("The authentication is not supported by the server");
			return "Unsupported authentication";
		} catch (AuthenticationException exception) {
			log.error("Incorrect password or username");
			return "Incorrect old password or username";
		} catch (NamingException exception) {
			log.error("Error when trying to create the context");
			return "Some error occured while connecting to the LDap server. Please try again later";
		} catch (Exception e) {
			log.error("Problem changing password: " + e.getMessage());
			return "Problem changing password: " + e.getMessage();
		} finally {

			try {
				if (null != context) {
					context.close();
				}
			} catch (NamingException e) {
				log.error(
						"Error while closing LDap context. This can cause memory leaks."
								+ e.getMessage());
			} finally {
				context = null;
			}

			try {
				if (null != newContext) {
					newContext.close();
				}
			} catch (NamingException e) {
				log.error(
						"Error while closing LDap context. This can cause memory leaks."
								+ e.getMessage());
			} finally {
				newContext = null;
			}
		}
	}

	/**
	 * Convert the given password string into MD5 hashing.
	 * 
	 * @arguments: String password
	 * @return: converted password
	 * @throws: Exception in case some error occured while converting the string
	 */
	private String digestMd5(final String password) throws Exception {
		byte[] base64;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(password.getBytes());
			base64 = Base64.getEncoder().encode(digest.digest());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		String pass = null;
		try {
			pass = new String(base64, "UTF8");
		} catch (UnsupportedEncodingException e) {
			throw new Exception("Error occured while hashing the password");
		}
		return "{MD5}" + pass;
	}

	private DirContext connectLDapServer(DirContext context) throws Exception {
		// Initializing properties file
		readResource(LDAPDirContextUtil.class, "authconfig.properties");

		// Setting up environment variable for LDap connection
		setEnv();

		// Connecting to the LDap server based on Environment variables
		context = new InitialDirContext(environment);
		log.debug("Connected..");

		return context;
	}

	private DirContext connectLDapServer(DirContext context,
			final String username, final String pass) throws Exception {
		// Initializing properties file
		readResource(LDAPDirContextUtil.class, "authconfig.properties");

		// Setting up environment variable for LDap connection
		setEnv(username, pass);

		// Connecting to the LDap server based on Environment variables
		context = new InitialDirContext(environment);
		log.info("Connected..");

		return context;
	}

	/**
	 * This will return the group information in which the given user is
	 * assigned
	 * 
	 * @argument: DirContext context : LDap server context
	 * @argument: String username : the user for which the assigned groups has
	 *            to be searched in the LDap
	 * @return: comma (',') separated groups name
	 * @throws Exception
	 */
	private Map<String, String> getUserGroups(final DirContext context,
			final String username) throws Exception {
		Map<String, String> groups = new LinkedHashMap<String, String>();
		SearchControls ctls = new SearchControls();
		String[] attrIDs = { "cn", "memberOf", "description" };
		ctls.setReturningAttributes(attrIDs);
		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration<SearchResult> answer = context.search(
				props.getProperty("ldap.searchbase"),
				"(&(cn=*)(memberUid=" + username + "))", ctls);
		String temp = null;
		boolean flg = true;
		while (answer.hasMore()) {
			SearchResult next = answer.next();
			if (next.getAttributes().size() == 0) {
				log.info("Group not found");
				return null;
			} else {

				if (next.getAttributes().get("cn").get().toString().trim()
						.equals("Inactive Users")) {
					throw new Exception(
							"User is inactive and cannot login inside application");
				}
				/*
				 * The description of the group is added in the group name
				 */
				Attribute groupType = next.getAttributes().get("description");
				if (flg) {
					temp = (groupType == null
							? next.getAttributes().get("cn").get().toString()
							: groupType.get().toString().toLowerCase()
									.concat("_").concat(next.getAttributes()
											.get("cn").get().toString()));
					flg = false;
				} else {
					temp += "," + (groupType == null
							? next.getAttributes().get("cn").get().toString()
							: groupType.get().toString().toLowerCase()
									.concat("_").concat(next.getAttributes()
											.get("cn").get().toString()));
				}
			}
		}
		if (null == temp) {
			throw new Exception("Group information for the user [" + username
					+ "] is not available.");
		}
		groups.put("groups", temp);
		return groups;
	}

	/**
	 * This will return all the available groups and their description in LDap
	 * 
	 * @argument: DirContext context : LDap server context
	 * @return: Map<String, String> with key as group cn and value as group
	 *          description
	 * @throws Exception
	 */
	public Map<String, String> getGroups() throws Exception {
		DirContext context = null;
		context = connectLDapServer(context);
		Map<String, String> groups = new LinkedHashMap<String, String>();
		SearchControls ctls = new SearchControls();
		String[] attrIDs = { "cn", "description" };
		ctls.setReturningAttributes(attrIDs);
		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration<SearchResult> answer = context.search(
				props.getProperty("ldap.searchbase"),
				"(&(cn=*)(objectclass=posixGroup))", ctls);
		String key = null;
		while (answer.hasMore()) {
			SearchResult next = answer.next();
			Attribute attribute = next.getAttributes().get("description");
			if (null != attribute) {
				key = attribute.get().toString();
				if (null == groups.get(key)) {
					groups.put(key,
							next.getAttributes().get("cn").get().toString());
				} else {
					groups.put(key, groups.get(key).concat(",").concat(
							next.getAttributes().get("cn").get().toString()));
				}
			} else {
				groups.put("Description Not available",
						groups.get("Description Not available") == null
								? next.getAttributes().get("cn").get()
										.toString()
								: groups.get("Description Not available")
										.concat(",").concat(next.getAttributes()
												.get("cn").get().toString()));
			}
		}
		return groups;
	}

	/**
	 * Read the properties file and convert it into properties object to fetch
	 * values return in given property file
	 * 
	 * @arguments: Class<? extends Object> className : class to get the relative
	 *             path of the resource String resource: Name of the properties
	 *             file
	 * 
	 * @return: Initialize the props instance variable
	 * @throws Exception
	 */
	private static void readResource(Class<? extends Object> className,
			String resource) throws Exception {
		InputStream input = className.getClassLoader()
				.getResourceAsStream(resource);
		try {
			if (input == null) {
				return;
			}
			props.load(input);
		} catch (Exception e) {
			throw new Exception("Error occured while reading the resource ["
					+ resource + "] file.");
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					log.error("Error occured while closing the resource : "
							+ e.getMessage());
				}
			}
		}
	}

	/**
	 * Method is for setting up the environment variables for Ldap connection
	 */
	private void setEnv(final String username, final String password) {
		environment.put(Context.INITIAL_CONTEXT_FACTORY,
				props.getProperty("ldap.factories.initctx"));

		environment.put(Context.SECURITY_AUTHENTICATION, "simple");
		environment.put(Context.SECURITY_PRINCIPAL,
				props.getProperty(username));
		environment.put(Context.SECURITY_CREDENTIALS,
				props.getProperty(password));

		environment.put(Context.PROVIDER_URL, props.getProperty("ldap.host"));
	}

	private void setEnv() {
		environment.put(Context.INITIAL_CONTEXT_FACTORY,
				props.getProperty("ldap.factories.initctx"));
		environment.put(Context.PROVIDER_URL, props.getProperty("ldap.host"));
	}

	/**
	 * Verify the user password after successful connection with LDap server
	 * based on environment variables. Here the connection is re-establish after
	 * getting the Context.SECURITY_PRINCIPAL value from the initial connection
	 * and password given by user
	 * 
	 * @arguments: String nameInNamespace : retrieved from initial LDap
	 *             connection and searching for the user with given username
	 * 
	 * @arguments: String password : password to verify given by the user during
	 *             login
	 * 
	 * @return true if password is correct else false is return
	 * @throws Exception
	 */
	private InitialDirContext verifyUserPassword(final String nameInNamespace,
			final String password) throws Exception {
		Properties authEnv = new Properties();
		authEnv.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		authEnv.put(Context.PROVIDER_URL, props.getProperty("ldap.host"));
		authEnv.put(Context.SECURITY_PRINCIPAL, nameInNamespace);
		authEnv.put(Context.SECURITY_CREDENTIALS, password);
		try {
			return new InitialDirContext(authEnv);
		} catch (NamingException e) {
			throw new Exception(
					"Password incorrect. Authorization cannot be done");
		}
	}

	/**
	 * Retrieve the use attributes after successful authentication and convert
	 * it into Map
	 * 
	 * @arguments: Attributes
	 * 
	 * @return: Map of string attributes
	 * @throws Exception
	 */
	private Map<String, String> getUserAttributes(final Attributes attributes)
			throws Exception {
		Map<String, String> userAttr = new LinkedHashMap<String, String>();
		try {
			for (String s : attributesArr) {
				String temp = null;
				boolean flg = true;
				if (attributes.get(s.split(":")[0]) == null) {
					log.error("Attribute [" + s.split(":")[1]
							+ "] is not defined");
					continue;
				}
				NamingEnumeration<?> all = attributes.get(s.split(":")[0])
						.getAll();
				while (all.hasMore()) {
					String next = (String) all.next();
					if (s.split(":")[0].equals("telephonenumber")
							&& next.contains("-")) {
						userAttr.put("emergencyContactName",
								next.split("-")[1].trim());
						userAttr.put("emergencyContactNumber",
								next.split("-")[0].trim());
						continue;
					}
					if (flg) {
						temp = next;
						flg = false;
					} else {
						temp += "," + next;
					}
				}
				if (null != temp) {
					userAttr.put(s.split(":")[1],
							temp.trim().replace("\n", "").replace("\r", ""));
				} else {
					log.info("Attribute [" + s
							+ "] not found in the current search");
				}
			}
		} catch (Exception e) {
			log.error(
					"Error occured while retriving the attributed for the given user."
							+ e.getMessage());
		}
		return userAttr;
	}

	/**
	 * This is used to get the users list assigned to a particular client i.e
	 * group. return all the users in the ldap when group all us passed as
	 * argument.
	 * 
	 * @arguments: String group: Name of the group
	 * @return: List<String> of groups
	 */

	public Map<String, List<String>> getLDapUsers(final String group)
			throws APIExceptions {
		DirContext context = null;
		Map<String, List<String>> result = new LinkedHashMap<String, List<String>>();
		try {
			context = connectLDapServer(context);
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

			NamingEnumeration<SearchResult> res = null;
			List<String> cn = new ArrayList<String>();
			List<String> uid = new ArrayList<String>();
			List<String> uidNumber = new ArrayList<String>();
			List<String> employeeNumber = new ArrayList<String>();
			if (group.toLowerCase().equals("all")) {
				res = context.search(props.getProperty("ldap.searchbase"),
						"(&(objectClass=inetOrgPerson)(gidNumber=500))", sc);
				try {
					while (res.hasMore()) {
						Attributes attributes = res.next().getAttributes();
						cn.add(attributes.get("cn").get().toString().trim());
						uid.add(attributes.get("uid").get().toString().trim());
						uidNumber.add(attributes.get("uidNumber").get()
								.toString().trim());
						employeeNumber.add(attributes.get("employeeNumber")
								.get().toString().trim());
					}
				} catch (NullPointerException ex) {
					String e = "One or more required attributes among "
							+ "[cn,uid,uidNumber,employeeNumber] is not "
							+ "available. Please setup required information "
							+ "for the existing users";
					log.error(e);
					throw new Exception(e);
				}
				result.put("cn", cn);
				result.put("uid", uid);
				result.put("uidNumber", uidNumber);
				result.put("employeeNumber", employeeNumber);
			} else {
				res = context.search(props.getProperty("ldap.searchbase"),
						"(&(objectClass=posixGroup)(cn=" + group + "))", sc);

				if (res.hasMore()) {
					Attributes attributes = res.next().getAttributes();
					NamingEnumeration<?> all = attributes.get("memberUid")
							.getAll();
					String username = null;
					NamingEnumeration<SearchResult> uids = null;
					List<String> users = new ArrayList<String>();
					while (all.hasMoreElements()) {
						username = all.nextElement().toString();
						sc.setReturningAttributes(new String[] { "cn", "uid",
								"uidNumber", "employeeNumber" });
						uids = context.search(
								props.getProperty("ldap.searchbase"),
								"uid=" + username, sc);
						if (uids.hasMore()) {
							Attributes attribute = uids.next().getAttributes();
							cn.add(attribute.get("cn").get().toString().trim());
							uid.add(attribute.get("uid").get().toString()
									.trim());
							uidNumber.add(attribute.get("uidNumber").get()
									.toString().trim());
							employeeNumber.add(attribute.get("employeeNumber")
									.get().toString().trim());
							users.add((username.concat(
									PintailerConstants.USER_DETAILS_SEPARATOR)
									.concat(attribute.get("cn").get()
											.toString())).trim());
						}
					}
					result.put("cn", cn);
					result.put("uid", uid);
					result.put("uidNumber", uidNumber);
					result.put("employeeNumber", employeeNumber);
					result.put("users", users);
				}
			}

			if (result.size() == 0) {
				throw new Exception(
						"Given group [" + group + "] does not exist in Ldap.");
			}
			return result;
		} catch (NamingException exception) {
			log.error("Error when trying to create the context");
			throw new APIExceptions("Error when trying to create the context"
					+ exception.getMessage());
		} catch (Exception e) {
			log.error("Error occured while getting the list of users for LDap."
					+ e);
			throw new APIExceptions(
					"Error occured while getting the list of users for LDap."
							+ e);
		} finally {
			try {
				if (null != context) {
					context.close();
				}
			} catch (NamingException e) {
				log.error("Error occured while closing context");
			}
		}
	}

}
