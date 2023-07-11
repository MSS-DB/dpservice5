package com.mbb.bts.config;

import static com.mbb.eclipse.common.util.CommonFunctions.isNullOrBlank;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mbb.eclipse.common.config.TokenAuthentication;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

	public static final String HEADER_STRING = "Authorization";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String header = request.getHeader(HEADER_STRING);
		if (!isNullOrBlank(header)) {
			UsernamePasswordAuthenticationToken authentication = new TokenAuthentication().validateToken(request,
					header);
			if (authentication != null) {
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
			
		} else {
			log.trace("Token not found");
		}

		chain.doFilter(request, response);
	}

}
