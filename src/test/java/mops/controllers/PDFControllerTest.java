package mops.controllers;

import com.c4_soft.springaddons.test.security.context.support.WithMockKeycloackAuth;
import mops.model.classes.Applicant;
import mops.model.classes.Application;
import mops.model.classes.Module;
import mops.model.classes.Organizer;
import mops.services.dbServices.ApplicantService;
import mops.services.PDFService;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
class PDFControllerTest {

    @Mock
    static PDFService service;
    @Mock
    static ApplicantService appService;
    @InjectMocks
    static PDFController controller;
    MockMvc mvc;
    @Autowired
    WebApplicationContext context;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockKeycloackAuth(name = "baum", roles = "studentin")
    void noSuchApplication() throws Exception {
        Module module = Module.builder()
                .applicantDeadline(LocalDateTime.ofEpochSecond(100, 0, ZoneOffset.UTC))
                .name("Info4")
                .build();
        Application application = Application.builder()
                .module(module)
                .build();
        Applicant applicant = Applicant.builder().application(application).build();

        File file = File.createTempFile("bbbb", ".tmp");
        file.deleteOnExit();

        when(appService.findByUniserial(any(String.class))).thenReturn(applicant);
        when(service.generatePDF(any(Application.class), any(Applicant.class), any(Organizer.class))).thenReturn(file);

        mvc.perform(get("/bewerbung2/pdf/pdfDownload?module=Baum?student=baum")).andExpect(status().isBadRequest());

    }

    @Test
    @WithMockKeycloackAuth(name = "name", roles = "studentin")
    void missingParam() throws Exception {
        mvc.perform(get("/bewerbung2/pdf/pdfDownload")).andExpect(status().isBadRequest());

    }


}