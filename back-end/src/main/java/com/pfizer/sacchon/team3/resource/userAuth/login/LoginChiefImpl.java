package com.pfizer.sacchon.team3.resource.userAuth.login;

import com.pfizer.sacchon.team3.exception.NotFoundException;
import com.pfizer.sacchon.team3.exception.WrongCredentials;
import com.pfizer.sacchon.team3.model.Chiefs;
import com.pfizer.sacchon.team3.repository.ChiefRepository;
import com.pfizer.sacchon.team3.repository.util.JpaUtil;
import com.pfizer.sacchon.team3.representation.ChiefRepresentation;
import com.pfizer.sacchon.team3.representation.LoginRepresentation;
import org.restlet.engine.Engine;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import java.util.Optional;
import java.util.logging.Logger;

public class LoginChiefImpl extends ServerResource implements LoginChief {
    public static final Logger LOGGER = Engine.getLogger(LoginChiefImpl.class);
    private ChiefRepository chiefRepository;

    @Override
    protected void doInit() {
        LOGGER.info("Initialising chief resource starts");
        try {
            chiefRepository = new ChiefRepository(JpaUtil.getEntityManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("Initialising chief resource ends");
    }

    @Override
    public ChiefRepresentation loginChief(LoginRepresentation loginRepresentation) throws NotFoundException, WrongCredentials {
        LOGGER.info("Login chief");
        // Initialize the persistence layer
        Chiefs chief;
        try {
            Optional<Chiefs> opChief = chiefRepository.findByEmailAndPass(loginRepresentation.getEmail(), loginRepresentation.getPassword());
            setExisting(opChief.isPresent());
            if (!isExisting()) {
                LOGGER.config("email does not exist:" + loginRepresentation.getEmail());
                throw new NotFoundException("No chief with that email : " + loginRepresentation.getEmail());
            } else {
                chief = opChief.get();
                ChiefRepresentation result = new ChiefRepresentation(chief);
                LOGGER.finer("chief successfully logged in");

                return result;
            }
        } catch (Exception ex) {
            throw new ResourceException(ex);
        }
    }
}
