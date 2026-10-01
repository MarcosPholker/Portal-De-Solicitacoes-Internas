package com.portal.solicitacoes.internas;

import com.portal.solicitacoes.internas.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class SolicitacoesInternasApplicationTests {
	@Autowired
	private FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration;

	@Test
	void contextLoads() {
	}

	@Test
	void jwtFilterIsNotRegisteredOutsideSecurityChain() {
		assertFalse(jwtFilterRegistration.isEnabled());
	}

}
