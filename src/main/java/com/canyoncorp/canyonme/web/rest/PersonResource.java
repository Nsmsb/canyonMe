package com.canyoncorp.canyonme.web.rest;

import com.canyoncorp.canyonme.domain.Person;
import com.canyoncorp.canyonme.repository.PersonRepository;
import com.canyoncorp.canyonme.service.dto.PersonDTO;
import com.canyoncorp.canyonme.service.mapper.PersonMapper;
import com.canyoncorp.canyonme.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.canyoncorp.canyonme.domain.Person}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PersonResource {

    private final Logger log = LoggerFactory.getLogger(PersonResource.class);

    private static final String ENTITY_NAME = "person";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PersonRepository personRepository;

    private final PersonMapper personMapper;

    public PersonResource(PersonRepository personRepository, PersonMapper personMapper) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
    }

    /**
     * {@code POST  /people} : Create a new person.
     *
     * @param personDTO the personDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new personDTO, or with status {@code 400 (Bad Request)} if the person has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/people")
    public ResponseEntity<PersonDTO> createPerson(@Valid @RequestBody PersonDTO personDTO) throws URISyntaxException {
        log.debug("REST request to save Person : {}", personDTO);
        if (personDTO.getId() != null) {
            throw new BadRequestAlertException("A new person cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Person person = personMapper.toEntity(personDTO);
        person = personRepository.save(person);
        PersonDTO result = personMapper.toDto(person);
        return ResponseEntity
            .created(new URI("/api/people/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /people/:id} : Updates an existing person.
     *
     * @param id the id of the personDTO to save.
     * @param personDTO the personDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated personDTO,
     * or with status {@code 400 (Bad Request)} if the personDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the personDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/people/{id}")
    public ResponseEntity<PersonDTO> updatePerson(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PersonDTO personDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Person : {}, {}", id, personDTO);
        if (personDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, personDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!personRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Person person = personMapper.toEntity(personDTO);
        person = personRepository.save(person);
        PersonDTO result = personMapper.toDto(person);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, personDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /people/:id} : Partial updates given fields of an existing person, field will ignore if it is null
     *
     * @param id the id of the personDTO to save.
     * @param personDTO the personDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated personDTO,
     * or with status {@code 400 (Bad Request)} if the personDTO is not valid,
     * or with status {@code 404 (Not Found)} if the personDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the personDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/people/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<PersonDTO> partialUpdatePerson(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PersonDTO personDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Person partially : {}, {}", id, personDTO);
        if (personDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, personDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!personRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PersonDTO> result = personRepository
            .findById(personDTO.getId())
            .map(
                existingPerson -> {
                    personMapper.partialUpdate(existingPerson, personDTO);

                    return existingPerson;
                }
            )
            .map(personRepository::save)
            .map(personMapper::toDto);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, personDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /people} : get all the people.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of people in body.
     */
    @GetMapping("/people")
    public List<PersonDTO> getAllPeople() {
        log.debug("REST request to get all People");
        List<Person> people = personRepository.findAll();
        return personMapper.toDto(people);
    }

    /**
     * {@code GET  /people/:id} : get the "id" person.
     *
     * @param id the id of the personDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the personDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/people/{id}")
    public ResponseEntity<PersonDTO> getPerson(@PathVariable Long id) {
        log.debug("REST request to get Person : {}", id);
        Optional<PersonDTO> personDTO = personRepository.findById(id).map(personMapper::toDto);
        return ResponseUtil.wrapOrNotFound(personDTO);
    }

    /**
     * {@code DELETE  /people/:id} : delete the "id" person.
     *
     * @param id the id of the personDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/people/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        log.debug("REST request to delete Person : {}", id);
        personRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
