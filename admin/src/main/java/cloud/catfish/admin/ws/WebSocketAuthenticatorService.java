package cloud.catfish.admin.ws;

import cloud.catfish.security.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebSocketAuthenticatorService {

    private final String BEARER = "Bearer";

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // This method MUST return a UsernamePasswordAuthenticationToken instance, the spring security chain is testing it with 'instanceof' later on. So don't use a subclass of it or any other class
    public UsernamePasswordAuthenticationToken getAuthenticatedOrFail(String authHeader) throws AuthenticationException {
        if (authHeader == null || authHeader.trim().isEmpty()) {
            throw new AuthenticationCredentialsNotFoundException("Authorization header was null or empty.");
        }
        
        if (!authHeader.startsWith(BEARER)) {
            throw new AuthenticationCredentialsNotFoundException("Authorization header must start with 'Bearer '.");
        }
        
        try {
            String authToken = authHeader.substring(BEARER.length()).trim();
            if (authToken.isEmpty()) {
                throw new AuthenticationCredentialsNotFoundException("JWT token was empty.");
            }
            
            String username = jwtTokenUtil.getUserNameFromToken(authToken);
            log.info("Checking username: {}", username);

            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    log.info("Successfully authenticated user: {}", username);
                    return authentication;
                } else {
                    throw new AuthenticationCredentialsNotFoundException("JWT token validation failed for user: " + username);
                }
            } else {
                throw new AuthenticationCredentialsNotFoundException("Unable to extract username from JWT token.");
            }
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage());
            if (e instanceof AuthenticationException) {
                throw e;
            }
            throw new AuthenticationCredentialsNotFoundException("Authentication processing failed: " + e.getMessage());
        }
    }
}