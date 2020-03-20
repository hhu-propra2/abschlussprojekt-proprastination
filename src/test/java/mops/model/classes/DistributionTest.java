package mops.model.classes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class DistributionTest {
    Distribution dist;
    List<Applicant> emps;

    @BeforeEach
    void init() {
        Application application = Application.builder().module("Divination").build();
        Set<Application> applicationList = new HashSet<>();
        applicationList.add(application);

        Certificate cert = Certificate.builder()
                .name("Bachelor")
                .course("Harvard")
                .build();
        Address address = Address.builder()
                .street("Baker Street")
                .houseNumber("21B")
                .city("London")
                .country("England")
                .zipcode(20394)
                .build();

        Applicant applicant = Applicant.builder()
                .surname("J")
                .address(address)
                .birthday("01.01.2001")
                .birthplace("Wakanda")
                .course("Arts")
                .nationality("English")
                .status("New")
                .certs(cert)
                .applications(applicationList)
                .build();
        emps = Arrays.asList(applicant);
        dist = Distribution.builder()
                .employees(emps)
                .module("ProPra")
                .build();
    }

    @Test
    void testBuilder() {
        assertThat(dist)
                .hasFieldOrPropertyWithValue("module", "ProPra")
                .hasFieldOrPropertyWithValue("employees", emps);
    }

    @Test
    void testToString() {
        assertThat(dist.toString()).isEqualTo("Distribution(stunden=0, module=ProPra, employees=[Applicant(uniserial=null, birthplace=Wakanda," +
                " title=null, firstName=null, surname=J, address=Address(street=Baker Street, houseNumber=21B, city=London, country=England," +
                " zipcode=20394), gender=null, birthday=01.01.2001, nationality=English, course=Arts, status=New, comment=null," +
                " certs=Certificate(name=Bachelor, course=Harvard), applications=[Application(hours=0, module=Divination, priority=0," +
                " grade=0.0, lecturer=null, semester=null, role=null, comment=null, applicant=null)])])");
    }
}