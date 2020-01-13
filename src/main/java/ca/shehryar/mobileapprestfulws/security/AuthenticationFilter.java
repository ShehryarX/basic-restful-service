package ca.shehryar.mobileapprestfulws.security;

import ca.shehryar.mobileapprestfulws.SpringApplicationContext;
import ca.shehryar.mobileapprestfulws.service.UserService;
import ca.shehryar.mobileapprestfulws.service.impl.UserServiceImpl;
import ca.shehryar.mobileapprestfulws.shared.dto.UserDto;
import ca.shehryar.mobileapprestfulws.ui.model.request.UserLoginRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            UserLoginRequestModel creds = new ObjectMapper().readValue(request.getInputStream(), UserLoginRequestModel.class);
            return authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                        creds.getEmail(),
                        creds.getPassword(),
                        new ArrayList<>()
                    ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        String username = ((User) authResult.getPrincipal()).getUsername();

        String token = Jwts.builder()
                           .setSubject(username)
                           .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_DATE))
                           .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                           .compact();

        UserService userService = (UserServiceImpl)SpringApplicationContext.getBean("userServiceImpl");
        UserDto userDto = userService.getUser(username);

        response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        response.addHeader("User ID", userDto.getUserId());
    }
}
