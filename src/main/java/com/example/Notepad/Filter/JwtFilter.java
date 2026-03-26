package com.example.Notepad.Filter;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.Notepad.TokenUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        
        //Step 1: Check header exist and starts with Bearer
        if (header != null && header.startsWith("Bearer"));{
        	String token = header.substring(7);
        	
        	//Step 2:Validate token 
        	Claims claims = tokenUtil.validateToken(token);
        	
        	if (claims != null) {
        		
        		//Step 3:Extract username and role 
        		String username = claims.getSubject();
        		String role = (String) claims.get("role");
        		
        		//Step 4:Convert role authorities
        		List<GrantedAuthority> authorities =
        				List.of(new SimpleGrantedAuthority("ROLE_" +role));
        		
        		//Step 5:Create Authentication object
        		UsernamePasswordAuthenticationToken auth =
        				new UsernamePasswordAuthenticationToken(
        						username,
        						null,
        						authorities
        				);
        		
        		//Step 6:Set authentication in context
        		SecurityContextHolder.getContext().setAuthentication(auth);
        	}
       }
        
        //Step 7:Continue request
        filterChain.doFilter(request,response);
    }
}

       