package mops.services;

import mops.model.classes.Module;
import mops.repositories.ModuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;

    /**
     * Injects The Repository
     *
     * @param moduleRepository the injected repository
     */
    @SuppressWarnings("checkstyle:HiddenField")
    public ModuleService(final ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    /**
     * Finds all Modules
     *
     * @return List of all Modules
     */
    public List<Module> getModules() {
        return moduleRepository.findAll();
    }
}
