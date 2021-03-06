package mops.services.webServices;

import mops.model.classes.Applicant;
import mops.model.classes.Application;
import mops.model.classes.Module;
import mops.model.classes.Organizer;
import mops.model.classes.webclasses.DownloadProgress;
import mops.services.EMailService;
import mops.services.PDFService;
import mops.services.ZIPService;
import mops.services.dbServices.ApplicantService;
import mops.services.dbServices.ModuleService;
import mops.services.dbServices.OrganizerService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

@Service
public class WebPdfService {

    private Logger logger = LoggerFactory.getLogger(WebPdfService.class);

    private final ModuleService moduleService;
    private final EMailService eMailService;
    private final ZIPService zipService;
    private final ApplicantService applicantService;
    private final PDFService pdfService;
    private final OrganizerService organizerService;

    /**
     * Constructor
     *
     * @param eMailService     Emailservice
     * @param zipService       zipservice
     * @param applicantService applicantservice
     * @param pdfService       pdfservice
     * @param moduleService    moduleservice
     * @param organizerService organizerservice
     */
    @SuppressWarnings("checkstyle:HiddenField")
    public WebPdfService(final EMailService eMailService, final ZIPService zipService,
                         final ApplicantService applicantService, final PDFService pdfService,
                         final ModuleService moduleService,
                         final OrganizerService organizerService) {
        this.eMailService = eMailService;
        this.zipService = zipService;
        this.applicantService = applicantService;
        this.pdfService = pdfService;
        this.moduleService = moduleService;
        this.organizerService = organizerService;
    }

    /**
     * -
     * @param module module
     * @param applicant applicant
     * @return -
     */
    public String getDownloadRedirectOfApplicantWithModule(final String module, final String applicant) {
        return "redirect:pdfDownload?student=" + applicant + "&module=" + module;
    }

    /**
     * -
     *
     * @param token            keycloak
     * @param attributes       redirect
     * @param eMail            email
     * @param downloadProgress Progress
     */
    public void sendEmail(final KeycloakAuthenticationToken token,
                          final RedirectAttributes attributes, final String eMail,
                          final DownloadProgress downloadProgress) {
        attributes.addFlashAttribute("account", AccountGenerator.createAccountFromPrincipal(token));
        String message;
        String type;
        try {
            File file = zipService.getZipFileForAllDistributions(downloadProgress);
            eMailService.sendEmailToRecipient(eMail, file);
            message = "Die Email wurde erfolgreich versendet";
            type = "success";
        } catch (MailSendException | IOException e) {
            message = e.getMessage();
            type = "danger";
        }
        downloadProgress.zero();
        attributes.addFlashAttribute("message", message);
        attributes.addFlashAttribute("type", type);
    }

    /**
     * -
     *
     * @param module   module
     * @param student  student
     * @param response HttpResponse
     * @throws IOException IO
     */
    public void generatePdfForDownload(final String module,
                                       final String student,
                                       final HttpServletResponse response) throws IOException {
        Applicant applicant;
        Optional<Application> application;
        Organizer organizer;
        try {
            if (module == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            applicant = applicantService.findByUniserial(student);
            if (applicant == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            application = applicant.getApplications().stream()
                    .filter(p -> p.getModule().getName().equals(module)).findFirst();
            if (application.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            organizer = organizerService.findByUniserial(application.get().getModule().getProfSerial());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        File file = pdfService.generatePDF(application.get(), applicant, organizer);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM.toString());
        response.setHeader("Content-disposition", "attachment; filename=Bewerbung_"
                + applicant.getFirstName() + "_" + applicant.getSurname() + ".pdf");
        OutputStream out = response.getOutputStream();
        FileInputStream in = new FileInputStream(file);
        IOUtils.copy(in, out);
        out.close();
        in.close();
        boolean deleted = file.delete();
        if (!deleted) {
            logger.warn("Could not delete: " + file.getName() + " on Path: " + file.getAbsolutePath());
        }
    }




    /**
     * -
     *
     * @param response         HttpResponse
     * @param downloadProgress Progress
     * @param module           module
     * @throws IOException IO
     */
    public void generateZipForModuleAssigned(final HttpServletResponse response, final String module,
                                             final DownloadProgress downloadProgress) throws IOException {
        Module modules = moduleService.findModuleByName(module);
        File file = zipService.getZipFileForModuleDistributions(modules, downloadProgress);
        loadFileToUser(response, file);
        downloadProgress.zero();
    }

    /**
     * -
     *
     * @param response         HttpResponse
     * @param downloadProgress Progress
     * @throws IOException IO
     */
    public void generateZipForAllAssigned(final HttpServletResponse response,
                                          final DownloadProgress downloadProgress) throws IOException {
        File file = zipService.getZipFileForAllDistributions(downloadProgress);
        loadFileToUser(response, file);
        downloadProgress.zero();
    }

    private void loadFileToUser(final HttpServletResponse response, final File file) throws IOException {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM.toString());
        response.setHeader("Content-disposition", "attachment; filename=\"download.zip\"");

        OutputStream out = response.getOutputStream();
        FileInputStream in = new FileInputStream(file);
        IOUtils.copy(in, out);
        out.close();
        in.close();
        boolean deleted = file.delete();
        if (!deleted) {
            logger.warn("Could not delete: " + file.getName() + " on Path: " + file.getAbsolutePath());
        } else {
            logger.info("Delteted temporary zip File to save storage space");
        }

    }

}
