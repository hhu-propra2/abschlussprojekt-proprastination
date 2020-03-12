
package mops.controllers;

import mops.model.Account;
import mops.services.ApplicantService;
import mops.services.CSVService;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;

@Controller
@SessionScope
@RequestMapping("/bewerbung2/bewerber")
public class ApplicationController {

    private Account createAccountFromPrincipal(final KeycloakAuthenticationToken token) {
        KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
        return new Account(
                principal.getName(),
                principal.getKeycloakSecurityContext().getIdToken().getEmail(),
                null,
                token.getAccount().getRoles());
    }

    /**
     * GetMapping for the main board
     *
     * @param token The KeycloakAuthentication
     * @param model The Website model
     * @return The HTML file rendered as a String
     */

    @GetMapping("/")
    @Secured("ROLE_studentin")
    public String main(final KeycloakAuthenticationToken token, final Model model) {
        if (token != null) {
            model.addAttribute("account", createAccountFromPrincipal(token));
        }
        return "applicantMain";
    }

    /**
     * The GetMapping for a new application
     *
     * @param token The KeycloakAuthentication
     * @param model The Website model
     * @return The HTML file rendered as a String
     */

    @GetMapping("/neueBewerbung")
    @Secured("ROLE_studentin")
    public String newAppl(final KeycloakAuthenticationToken token, final Model model) {
        if (token != null) {
            model.addAttribute("account", createAccountFromPrincipal(token));
            model.addAttribute("countries", CSVService.getCountries());
        }
        return "applicationPersonal";
    }

    /**
     * The GetMapping for the open applications
     *
     * @param token The KeycloakAuthentication
     * @param model The Website model
     * @return The HTML file rendered as a String
     */

    @GetMapping("/offeneBewerbungen")
    public String openAppl(final KeycloakAuthenticationToken token, final Model model) {
        if (token != null) {
            model.addAttribute("account", createAccountFromPrincipal(token));
        }
        return "openAppl";
    }

    /**
     * The GetMapping for the personal data page
     *
     * @param token The KeycloakAuthentication
     * @param model The Website model
     * @return The HTML file rendered as a String
     */

    @GetMapping("/profil")
    public String personal(final KeycloakAuthenticationToken token, final Model model) {
        if (token != null) {
            model.addAttribute("account", createAccountFromPrincipal(token));
        }
        return "personal";
    }


    /**
     * The GetMapping for the module page
     *
     * @param token The KeycloakAuthentication
     * @param model The Website model
     * @return The HTML file rendered as a String
     */

    @GetMapping("/modul")
    public String module(final KeycloakAuthenticationToken token, final Model model) {
        if (token != null) {
            model.addAttribute("account", createAccountFromPrincipal(token));
        }
        return "applicationModule";
    }

    /**
     * Post Mapping after Pers Data
     * @param token
     * @param model
     * @param street
     * @param place
     * @param plz
     * @param birthplace
     * @param nationality
     * @param birthday
     * @param gender
     * @param subject
     * @param status
     * @param graduation
     * @param diverse
     * @param module
     * @return
     */
    @PostMapping(value = "/modul")
    public String postModule(final KeycloakAuthenticationToken token, final Model model,
                             @RequestParam("street") final String street,
                             @RequestParam("place") final String place,
                             @RequestParam("plz") final String plz,
                             @RequestParam("placeofbirth") final String birthplace,
                             @RequestParam("nationality") final String nationality,
                             @RequestParam("birthday") final String birthday,
                             @RequestParam("gender") final String gender,
                             @RequestParam("subject") final String subject,
                             @RequestParam("status") final String status,
                             @RequestParam("graduation") final String graduation,
                             @RequestParam("diverse") final String diverse,
                             @RequestParam("module") final String module) {
        ApplicantService.
        return "applicationModule";
    }

    /**
     * The GetMapping for the overview
     *
     * @param token The KeycloakAuthentication
     * @param model The Website model
     * @return The HTML file rendered as a String
     */

    @GetMapping("/uebersicht")
    public String overview(final KeycloakAuthenticationToken token, final Model model) {
        if (token != null) {
            model.addAttribute("account", createAccountFromPrincipal(token));
        }
        return "applicationOverview";
    }

    /**
     * The GetMapping for the edit form fot personal data
     *
     * @param token The KeycloakAuthentication
     * @param model The Website model
     * @return The HTML file rendered as a String
     */

    @GetMapping("/bearbeitePersoenlicheDaten")
    public String editPersonalData(final KeycloakAuthenticationToken token, final Model model) {
        if (token != null) {
            model.addAttribute("account", createAccountFromPrincipal(token));
        }
        return "applicationEditPersonal";
    }

    /**
     * The GetMapping for the edit form for modules
     *
     * @param token The KeycloakAuthentication
     * @param model The Website model
     * @return The HTML file rendered as a String
     */

    @GetMapping("/bearbeiteModulDaten")
    public String editModuleData(final KeycloakAuthenticationToken token, final Model model) {
        if (token != null) {
            model.addAttribute("account", createAccountFromPrincipal(token));
        }
        return "applicationEditPersonal";
    }
}
