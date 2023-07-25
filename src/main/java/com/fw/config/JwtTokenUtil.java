package com.fw.config;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.fw.bean.LoginResultBean;
import com.fw.pintailer.constants.PintailerConstants;

@Service
public class JwtTokenUtil {
	private final String DATA_SEPARATOR = "####";

	public String getUsernameFromToken(String token) throws Exception {
		return getClaimFromToken(token, Claims::getAudience);
	}

	public String getUserOrganizationalRoleFromToken(String token)
			throws Exception {
		return getClaimFromToken(token, Claims::getIssuer)
				.split(DATA_SEPARATOR)[0];
	}

	public String getUserProjectRoleFromToken(String token) throws Exception {
		return getClaimFromToken(token, Claims::getIssuer)
				.split(DATA_SEPARATOR)[1];
	}

	public String getUserEmailFromToken(String token) throws Exception {
		return getClaimFromToken(token, Claims::getSubject).split(
				DATA_SEPARATOR)[0];
	}

	public String getUserOrganizationsFromToken(String token) throws Exception {
		return getClaimFromToken(token, Claims::getId);
	}

	public Date getExpirationDateFromToken(String token) throws Exception {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public String getDefaultOrgFromToken(String token) throws Exception {
		return getClaimFromToken(token, Claims::getSubject).split(
				DATA_SEPARATOR)[1];
	}

	public String getAssignedProjectFromToken(String token) throws Exception {
		String projectIds = getClaimFromToken(token, Claims::getSubject);
		String[] ids = projectIds.split(DATA_SEPARATOR);
		if(ids.length<=2){
			return "";
		}
		return getClaimFromToken(token, Claims::getSubject).split(
				DATA_SEPARATOR)[2];
	}

	public <T> T getClaimFromToken(String token,
			Function<Claims, T> claimsResolver) throws Exception {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) throws Exception {
		try {
			return Jwts.parser().setSigningKey(PintailerConstants.SIGNING_KEY)
					.parseClaimsJws(token).getBody();
		} catch (MalformedJwtException m) {
			throw new Exception("Invalid token structure. : " + m.getMessage());
		}
	}

	private Boolean isTokenExpired(String token) throws Exception {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	public String generateToken(LoginResultBean user) {
		return doGenerateToken(user.getUserName(), user.getEmail(), user
				.getUserOrganizations().toString(), user
				.getUserOrganizationalRole().toString(), user
				.getUserApplicationsAndRoles().toString(),
				user.getDefaultOrganization(), user.getAssignedProjectIds());
	}

	private String doGenerateToken(String userName, String email,
			String clients, String orgRole, String projectRole,
			String defaultOrg, String assignedProjects) {
		return Jwts
				.builder()
				.setAudience(userName)
				.setSubject(
						email.concat(DATA_SEPARATOR).concat(defaultOrg)
								.concat(DATA_SEPARATOR)
								.concat(assignedProjects))
				.setId(clients)
				.setIssuer(orgRole.concat(DATA_SEPARATOR).concat(projectRole))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(
						new Date(
								System.currentTimeMillis()
										+ PintailerConstants.ACCESS_TOKEN_VALIDITY_SECONDS
										* 1000))
				.signWith(SignatureAlgorithm.HS256,
						PintailerConstants.SIGNING_KEY).compact();
	}

	public Boolean validateToken(String token, UserDetails userDetails)
			throws Exception {
		final String username = getUsernameFromToken(token);
		final String userOrganizationRole = getUserOrganizationalRoleFromToken(token);
		final String email = getUserEmailFromToken(token);
		final String userOrganizations = getUserOrganizationsFromToken(token);
		final String userProjectRole = getUserProjectRoleFromToken(token);
		final String defaultOrg = getDefaultOrgFromToken(token);

		boolean result = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
		if (userDetails instanceof CustomUser) {
			CustomUser user = (CustomUser) userDetails;
			result = result
					&& (userOrganizationRole.equals(user
							.getUserOrganizationalRole())
							&& !isTokenExpired(token)
							&& email.equals(user.getEmail())
							&& userOrganizations.equals(user
									.getUserOrganizations())
							&& userProjectRole
									.equals(user.getUserProjectRole()) && defaultOrg
								.equals(user.getDefaultOrg()));
		}
		return result;
	}
}
