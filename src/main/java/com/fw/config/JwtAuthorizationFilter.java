package com.fw.config;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fw.pintailer.constants.PintailerConstants;
import com.fw.utils.LocalUtils;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest req,
			HttpServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		String header = req.getHeader(PintailerConstants.HEADER_STRING);
		String userName = null;
		String authToken = null;
		String userOrganizationalRole = null;
		String email = null;
		String userOrganizations = null;
		String userProjectRole = null;
		String defaultOrg = null;
		String assignedProjectIds = null;
		try {
			if (header != null) {
				authToken = header.replace(PintailerConstants.TOKEN_PREFIX, "");
				userName = jwtTokenUtil.getUsernameFromToken(authToken);
				userOrganizationalRole = jwtTokenUtil
						.getUserOrganizationalRoleFromToken(authToken);
				email = jwtTokenUtil.getUserEmailFromToken(authToken);
				userOrganizations = jwtTokenUtil
						.getUserOrganizationsFromToken(authToken);
				userProjectRole = jwtTokenUtil
						.getUserProjectRoleFromToken(authToken);
				defaultOrg = jwtTokenUtil.getDefaultOrgFromToken(authToken);
				assignedProjectIds = jwtTokenUtil
						.getAssignedProjectFromToken(authToken);
				if (assignedProjectIds.startsWith("Error")) {
					throw new Exception(assignedProjectIds);
				}
			}

			if (!req.getMethod().equals("OPTIONS")
					&& (null == header || header.trim().equalsIgnoreCase("") || header
							.trim().equalsIgnoreCase("null"))) {
				if (req.getRequestURI().contains("/private/")
						&& null == SecurityContextHolder.getContext()
								.getAuthentication()) {
					throw new Exception("UnauthorizedProcess");
				}
			}

			if (userName != null
					&& SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = new CustomUser(
						userName,
						"",
						Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")),
						userOrganizationalRole, email, userOrganizations,
						userProjectRole, defaultOrg, assignedProjectIds,
						authToken);
				if (jwtTokenUtil.validateToken(authToken, userDetails)) {
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userDetails, null,
							Arrays.asList(new SimpleGrantedAuthority(
									"ROLE_ADMIN")));

					authentication
							.setDetails(new WebAuthenticationDetailsSource()
									.buildDetails(req));
					SecurityContextHolder.getContext().setAuthentication(
							authentication);
				} else {
					throw new Exception("AnauthorizedUser");
				}
			}
			chain.doFilter(req, res);
		} catch (SignatureException e) {
			sendError("InvalidToken", req, res, chain);
		} catch (IllegalArgumentException e) {
			sendError("InvalidToken", req, res, chain);
		} catch (ExpiredJwtException e) {
			sendError("TokenExpired", req, res, chain);
		} catch (Exception e) {
			sendError(e.getMessage(), req, res, chain);
		}
	}

	private void sendError(String errMessage, HttpServletRequest req,
			HttpServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		if (req.getRequestURI().contains("/private/")) {
			String message = LocalUtils.getStringLocale(
					PintailerConstants.LOCALE_EN, errMessage);
			res.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
			return;
		} else {
			chain.doFilter(req, res);
		}
	}
}
