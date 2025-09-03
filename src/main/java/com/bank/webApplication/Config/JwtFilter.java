package com.bank.webApplication.Config;

import com.bank.webApplication.Util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@AllArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private BankUserDetailService bankUserDetailService;
    @Autowired
    private final JWTUtil jwtUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader=request.getHeader("Authorization");
        String UserName=null;
        String token=null;
        if(authHeader!=null && authHeader.startsWith("Bearer ")) {
             token = authHeader.substring(7);
             UserName= jwtUtil.extractUsername(token);
        }
        if(UserName!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails= bankUserDetailService.loadUserByUsername(UserName);
            if(jwtUtil.validateToken(token,userDetails)){
                UsernamePasswordAuthenticationToken authtoken=
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authtoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authtoken);
            }
        }
        filterChain.doFilter(request,response);
    }
}
