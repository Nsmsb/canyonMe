package com.canyoncorp.canyonme.web.rest;

import com.canyoncorp.canyonme.domain.Address;
import com.canyoncorp.canyonme.repository.AddressRepository;
import com.canyoncorp.canyonme.service.dto.AddressDTO;
import com.canyoncorp.canyonme.service.mapper.AddressMapper;
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
 * REST controller for managing {@link com.canyoncorp.canyonme.domain.Address}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AddressResource {

    private final Logger log = LoggerFactory.getLogger(AddressResource.class);

    private static final String ENTITY_NAME = "address";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AddressRepository addressRepository;

    private final AddressMapper addressMapper;

    public AddressResource(AddressRepository addressRepository, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
    }

    /**
     * {@code POST  /addresses} : Create a new address.
     *
     * @param addressDTO the addressDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new addressDTO, or with status {@code 400 (Bad Request)} if the address has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) throws URISyntaxException {
        log.debug("REST request to save Address : {}", addressDTO);
        if (addressDTO.getId() != null) {
            throw new BadRequestAlertException("A new address cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Address address = addressMapper.toEntity(addressDTO);
        address = addressRepository.save(address);
        AddressDTO result = addressMapper.toDto(address);
        return ResponseEntity
            .created(new URI("/api/addresses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /addresses/:id} : Updates an existing address.
     *
     * @param id the id of the addressDTO to save.
     * @param addressDTO the addressDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated addressDTO,
     * or with status {@code 400 (Bad Request)} if the addressDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the addressDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/addresses/{id}")
    public ResponseEntity<AddressDTO> updateAddress(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AddressDTO addressDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Address : {}, {}", id, addressDTO);
        if (addressDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, addressDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!addressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Address address = addressMapper.toEntity(addressDTO);
        address = addressRepository.save(address);
        AddressDTO result = addressMapper.toDto(address);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, addressDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /addresses/:id} : Partial updates given fields of an existing address, field will ignore if it is null
     *
     * @param id the id of the addressDTO to save.
     * @param addressDTO the addressDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated addressDTO,
     * or with status {@code 400 (Bad Request)} if the addressDTO is not valid,
     * or with status {@code 404 (Not Found)} if the addressDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the addressDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/addresses/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<AddressDTO> partialUpdateAddress(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AddressDTO addressDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Address partially : {}, {}", id, addressDTO);
        if (addressDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, addressDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!addressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AddressDTO> result = addressRepository
            .findById(addressDTO.getId())
            .map(
                existingAddress -> {
                    addressMapper.partialUpdate(existingAddress, addressDTO);

                    return existingAddress;
                }
            )
            .map(addressRepository::save)
            .map(addressMapper::toDto);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, addressDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /addresses} : get all the addresses.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of addresses in body.
     */
    @GetMapping("/addresses")
    public List<AddressDTO> getAllAddresses() {
        log.debug("REST request to get all Addresses");
        List<Address> addresses = addressRepository.findAll();
        return addressMapper.toDto(addresses);
    }

    /**
     * {@code GET  /addresses/:id} : get the "id" address.
     *
     * @param id the id of the addressDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the addressDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/addresses/{id}")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable Long id) {
        log.debug("REST request to get Address : {}", id);
        Optional<AddressDTO> addressDTO = addressRepository.findById(id).map(addressMapper::toDto);
        return ResponseUtil.wrapOrNotFound(addressDTO);
    }

    /**
     * {@code DELETE  /addresses/:id} : delete the "id" address.
     *
     * @param id the id of the addressDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        log.debug("REST request to delete Address : {}", id);
        addressRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
