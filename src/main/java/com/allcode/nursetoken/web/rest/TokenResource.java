package com.allcode.nursetoken.web.rest;

import com.allcode.nursetoken.security.AuthoritiesConstants;
import com.codahale.metrics.annotation.Timed;
import com.allcode.nursetoken.domain.Token;
import com.allcode.nursetoken.repository.TokenRepository;
import com.allcode.nursetoken.web.rest.errors.BadRequestAlertException;
import com.allcode.nursetoken.web.rest.util.HeaderUtil;
import com.allcode.nursetoken.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Token.
 */
@RestController
@RequestMapping("/api")
public class TokenResource {

    private final Logger log = LoggerFactory.getLogger(TokenResource.class);

    private static final String ENTITY_NAME = "token";

    private final TokenRepository tokenRepository;

    public TokenResource(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * POST  /tokens : Create a new token.
     *
     * @param token the token to create
     * @return the ResponseEntity with status 201 (Created) and with body the new token, or with status 400 (Bad Request) if the token has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tokens")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Token> createToken(@Valid @RequestBody Token token) throws URISyntaxException {
        log.debug("REST request to save Token : {}", token);
        if (token.getId() != null) {
            throw new BadRequestAlertException("A new token cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Token result = tokenRepository.save(token);
        return ResponseEntity.created(new URI("/api/tokens/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tokens : Updates an existing token.
     *
     * @param token the token to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated token,
     * or with status 400 (Bad Request) if the token is not valid,
     * or with status 500 (Internal Server Error) if the token couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/tokens")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Token> updateToken(@Valid @RequestBody Token token) throws URISyntaxException {
        log.debug("REST request to update Token : {}", token);
        if (token.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Token result = tokenRepository.save(token);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, token.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tokens : get all the tokens.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of tokens in body
     */
    @GetMapping("/tokens")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<Token>> getAllTokens(Pageable pageable) {
        log.debug("REST request to get a page of Tokens");
        Page<Token> page = tokenRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tokens");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /tokens/:id : get the "id" token.
     *
     * @param id the id of the token to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the token, or with status 404 (Not Found)
     */
    @GetMapping("/tokens/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Token> getToken(@PathVariable Long id) {
        log.debug("REST request to get Token : {}", id);
        Optional<Token> token = tokenRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(token);
    }

    /**
     * DELETE  /tokens/:id : delete the "id" token.
     *
     * @param id the id of the token to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/tokens/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteToken(@PathVariable Long id) {
        log.debug("REST request to delete Token : {}", id);

        tokenRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
