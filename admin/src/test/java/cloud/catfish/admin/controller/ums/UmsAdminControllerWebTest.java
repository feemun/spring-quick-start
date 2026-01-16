package cloud.catfish.admin.controller.ums;

import cloud.catfish.admin.dto.TokenPair;
import cloud.catfish.admin.service.UmsAdminService;
import cloud.catfish.admin.service.UmsRoleService;
import cloud.catfish.api.exception.GlobalExceptionHandler;
import cloud.catfish.security.config.SecurityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UmsAdminControllerWebTest {

    private MockMvc mockMvc;

    private UmsAdminService adminService;

    private UmsRoleService roleService;

    private SecurityProperties.JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        this.adminService = mock(UmsAdminService.class);
        this.roleService = mock(UmsRoleService.class);
        this.jwtProperties = new SecurityProperties.JwtProperties("Authorization", "Bearer ", "s", 1L, 1L);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        this.mockMvc = MockMvcBuilders.standaloneSetup(new UmsAdminController(jwtProperties, adminService, roleService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void login_whenBadCredentials_returnsUnauthorizedProblemDetail() throws Exception {
        when(adminService.login(anyString(), anyString())).thenReturn(null);

        mockMvc.perform(post("/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"u\",\"password\":\"bad\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.detail").isString());
    }

    @Test
    void login_whenOk_returnsTokenPairPayload() throws Exception {
        when(adminService.login(anyString(), anyString())).thenReturn(new TokenPair("a", "r"));

        mockMvc.perform(post("/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"u\",\"password\":\"p\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("a"))
                .andExpect(jsonPath("$.refreshToken").value("r"))
                .andExpect(jsonPath("$.tokenHead").value("Bearer "));
    }

    @Test
    void info_withoutPrincipal_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/admin/info"))
                .andExpect(status().isUnauthorized());
    }
}
