package br.com.cviana.security.jwt;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class JwtTokenFilter extends GenericFilterBean {
    @Autowired private JwtTokenProvider provider;

    public JwtTokenFilter(JwtTokenProvider provider) {
        this.provider = provider;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filters) throws IOException, ServletException {
        var token = provider.resolveToken((HttpServletRequest) req);
        if(StringUtils.isNotBlank(token) && provider.validateToken(token)) {
            Authentication auth = provider.getAuthentication(token);
            if(auth != null) SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filters.doFilter(req, res);
    }

}
