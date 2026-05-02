package cl.duoc.worksi.security;

import cl.duoc.worksi.entity.User;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final WorksiUserDetailsService userDetailsService;

  public JwtAuthenticationFilter(JwtService jwtService, WorksiUserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return isPublicPath(request);
  }

  private static boolean isPublicPath(HttpServletRequest request) {
    String path = request.getRequestURI();
    String method = request.getMethod();
    if ("/health".equals(path)) {
      return true;
    }
    if (path.startsWith("/api/v1/catalogs/")) {
      return true;
    }
    if ("/api/v1/auth/login".equals(path) && "POST".equals(method)) {
      return true;
    }
    if ("/api/v1/auth/register/candidate".equals(path) && "POST".equals(method)) {
      return true;
    }
    if (path.startsWith("/api/v1/auth/password-recovery/")) {
      return true;
    }
    return false;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header == null || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    String token = header.substring(7).trim();
    try {
      Claims claims = jwtService.parseAndValidate(token);
      String sub = claims.getSubject();
      if (sub == null || sub.isBlank()) {
        filterChain.doFilter(request, response);
        return;
      }
      long userId = Long.parseLong(sub);
      UserDetails userDetails = userDetailsService.loadUserById(userId);
      User u = ((UserPrincipal) userDetails).getUser();
      if (u.getDeletedAt() != null || !u.isActive()) {
        filterChain.doFilter(request, response);
        return;
      }
      UsernamePasswordAuthenticationToken auth =
          new UsernamePasswordAuthenticationToken(
              userDetails, null, userDetails.getAuthorities());
      auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(auth);
    } catch (Exception ignored) {
    }
    filterChain.doFilter(request, response);
  }
}
