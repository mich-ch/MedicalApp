package com.pfizer.sacchon.team3.representation;

import com.pfizer.sacchon.team3.model.Doctors;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class CreatedDoctorRepresentation {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Date lastActive;
    private boolean isDeleted;
    private String uri;

    public CreatedDoctorRepresentation(Doctors doctor) {
        if (doctor != null) {
            firstName = doctor.getFirstName();
            lastName = doctor.getLastName();
            email = doctor.getEmail();
            password = doctor.getPassword();
            lastActive = doctor.getLastActive();
            isDeleted = doctor.isDeleted();
            uri = "http://localhost:9000/v1/doctor/" + doctor.getId();
        }
    }
}