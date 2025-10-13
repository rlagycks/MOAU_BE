package com.moau.moau.jwt.config;

import com.moau.moau.jwt.ports.JwtIssuerPort;
import com.moau.moau.jwt.ports.JwtParserPort;
import com.moau.moau.jwt.infra.JwtIssuerAdapter;
import com.moau.moau.jwt.infra.JwtParserAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtBeanConfig {

    @Bean
    public JwtIssuerPort jwtIssuerPort(JwtProps props) {
        return new JwtIssuerAdapter(props);
    }

    @Bean
    public JwtParserPort jwtParserPort(JwtProps props) {
        return new JwtParserAdapter(props);
    }
}
