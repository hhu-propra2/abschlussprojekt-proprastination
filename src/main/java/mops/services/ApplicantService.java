package mops.services;

import mops.model.classes.Address;
import mops.model.classes.Applicant;
import mops.model.classes.Application;
import mops.model.classes.webclasses.WebAddress;
import mops.model.classes.webclasses.WebApplicant;
import mops.repositories.ApplicantRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@EnableAutoConfiguration
public class ApplicantService {

    private final ApplicantRepository applicantRepository;

    /**
     * Lets Spring inject the Repository
     *
     * @param applicantRepository the injected Repository
     */
    @SuppressWarnings("checkstyle:HiddenField")
    public ApplicantService(final ApplicantRepository applicantRepository) {
        this.applicantRepository = applicantRepository;
    }

    /**
     *
     * @param webAddress
     * @return
     */
    public Address buildAddress(final WebAddress webAddress) {
        String street = webAddress.getStreet();
        Address address = Address.builder()
                .street(street.substring(0, street.indexOf(' ')))
                .houseNumber(street.substring(street.indexOf(' ') + 1))
                .city(webAddress.getCity())
                .zipcode(webAddress.getZipcode())
                .build();
        return address;
    }

    /**
     * @param uniserial
     * @param webApplicant
     * @param address
     * @return
     */
    public Applicant buildApplicant(final String uniserial, final WebApplicant webApplicant, final Address address) {
        Set<Application> applications = new HashSet<>();
        Applicant applicant = Applicant.builder()
                .uniserial(uniserial)
                .address(address)
                .birthday(webApplicant.getBirthday())
                .birthplace(webApplicant.getBirthplace())
                .gender(webApplicant.getGender())
                .nationality(webApplicant.getNationality())
                .course(webApplicant.getCourse())
                .status(webApplicant.getStatus())
                .comment(webApplicant.getComment())
                .applications(applications)
                .build();
        return applicant;
    }

    /**
     * Saves or Updates Applicant to Repository
     *
     * @param applicant the new Applicant
     */
    public void saveApplicant(final Applicant applicant) {
        applicantRepository.save(applicant);
    }

    /**
     * Finds all Applicants
     *
     * @return List of Applicants
     */
    public List<Applicant> findAll() {
        return applicantRepository.findAll();
    }

    /**
     * Finds first applicant with give uniserial
     * uniserial should be unique, so only the
     * first result is returned
     *
     * @param uniserial Unikennung
     * @return the Applicant found
     */
    public Applicant findByUniserial(final String uniserial) {
        return applicantRepository.findByUniserial(uniserial);
    }

    /**
     * Updates Applicant without changing his applications
     *
     * @param newApplicant The Object containing the new information
     */
    public void updateApplicantWithoutChangingApplications(final Applicant newApplicant) {
        Applicant oldApplicant = findByUniserial(newApplicant.getUniserial());
        saveApplicant(Applicant.builder()
                .applications(oldApplicant.getApplications())
                .uniserial(newApplicant.getUniserial())
                .certs(newApplicant.getCerts())
                .status(newApplicant.getStatus())
                .course(newApplicant.getCourse())
                .nationality(newApplicant.getComment())
                .birthday(newApplicant.getBirthday())
                .address(newApplicant.getAddress())
                .birthplace(newApplicant.getBirthplace())
                .comment(newApplicant.getComment())
                .surname(newApplicant.getSurname())
                .firstName(newApplicant.getFirstName())
                .gender(newApplicant.getGender())
                .build());
    }
}
