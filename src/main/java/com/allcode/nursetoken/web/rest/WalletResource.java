package com.allcode.nursetoken.web.rest;

import com.allcode.nursetoken.domain.*;
import com.allcode.nursetoken.repository.TokenRepository;
import com.allcode.nursetoken.security.AuthoritiesConstants;
import com.allcode.nursetoken.service.UserService;
import com.codahale.metrics.annotation.Timed;
import com.allcode.nursetoken.repository.WalletRepository;
import com.allcode.nursetoken.web.rest.errors.BadRequestAlertException;
import com.allcode.nursetoken.web.rest.util.HeaderUtil;
import com.allcode.nursetoken.web.rest.util.PaginationUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * REST controller for managing Wallet.
 */
@RestController
@RequestMapping("/api")
public class WalletResource {

    private final Logger log = LoggerFactory.getLogger(WalletResource.class);

    private static final String ENTITY_NAME = "wallet";

    private final WalletRepository walletRepository;

    @Autowired
    UserService userService;

    @Autowired
    TokenRepository tokenRepository;

    public WalletResource(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    /**
     * POST  /wallets : Create a new wallet.
     *
     * @return the ResponseEntity with status 201 (Created) and with body the new wallet, or with status 400 (Bad Request) if the wallet has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/wallets")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<Wallet> createWallet(
        @Valid @RequestBody UpdatableWallet updatableWallet
    ) throws URISyntaxException {
        User user = userService.getCurrentUser();
        Wallet wallet = Wallet.createFromApiNeo(user);

        if (wallet == null) {
            throw new BadRequestAlertException("Error when try to create wallet", ENTITY_NAME, "neoapierror");
        }

        wallet.setName(updatableWallet.getName());
        log.debug("REST request to save Wallet : {}", wallet);
        Wallet result = walletRepository.save(wallet);
        return ResponseEntity.created(new URI("/api/wallets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /wallets : Updates an existing wallet.
     *
     * @param updatableWallet to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated wallet,
     * or with status 400 (Bad Request) if the wallet is not valid,
     * or with status 500 (Internal Server Error) if the wallet couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/wallets")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<Wallet> updateWallet(
        @Valid @RequestBody UpdatableWallet updatableWallet
    ) throws URISyntaxException {

        if (updatableWallet.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        Optional<Wallet> optionalWallet = walletRepository.findById(updatableWallet.getId());

        if(!optionalWallet.isPresent()){
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        Wallet wallet = optionalWallet.get();

        if(!userService.getCurrentUser().getId().equals(wallet.getOwner().getId())){
            throw new BadRequestAlertException("You not have permission", ENTITY_NAME, "unauthorize");
        }

        wallet.setName(updatableWallet.getName());

        log.debug("REST request to update Wallet : {}", wallet);
        Wallet result = walletRepository.save(wallet);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, wallet.getId().toString()))
            .body(result);
    }

    /**
     * GET  /wallets : get all the wallets.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of wallets in body
     */
    @GetMapping("/wallets")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<List<Wallet>> getAllWallets(Pageable pageable) {
        log.debug("REST request to get a page of Wallets");
        Page<Wallet> page = walletRepository.findByOwnerIsCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/wallets");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * POST  /wallets/import .
     *
     * @return the ResponseEntity with status 200 (OK) and with body the wallet, or with status 404 (Not Found)
     * * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/wallets/import")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<Wallet> getWallet(
        @Valid  @RequestBody ImportableWallet importableWallet
    ) throws URISyntaxException{
        User currentUser = userService.getCurrentUser();

        /*Wallet repeatWallet = walletRepository.findByWif(importableWallet.getWif());

        if(repeatWallet != null){
            throw new BadRequestAlertException("Wif already exists", ENTITY_NAME, "wifexists");
        }*/

        Wallet newWallet = Wallet.importWifFromApiNeo(importableWallet, currentUser);

        if (newWallet == null) {
            throw new BadRequestAlertException("Invalid Wif", ENTITY_NAME, "invalidwif");
        }

        log.debug("REST request to save Wallet : {}", newWallet);
        Wallet result = walletRepository.save(newWallet);
        return ResponseEntity.created(new URI("/api/wallets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }
    /**
     * GET  /wallets/:id : get the "id" wallet.
     *
     * @param id the id of the wallet to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the wallet, or with status 404 (Not Found)
     */
    @GetMapping("/wallets/{id}")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<Wallet> getWallet(@PathVariable Long id) {
        log.debug("REST request to get Wallet : {}", id);
        Optional<Wallet> optionalWallet = walletRepository.findById(id);

        if(!optionalWallet.isPresent()){
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        Wallet wallet = optionalWallet.get();

        if(!userService.getCurrentUser().getId().equals(wallet.getOwner().getId())){
            throw new BadRequestAlertException("You not have permission", ENTITY_NAME, "unauthorize");
        }

        return ResponseUtil.wrapOrNotFound(optionalWallet);
    }

    /**
     * DELETE  /wallets/:id : delete the "id" wallet.
     *
     * @param id the id of the wallet to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/wallets/{id}")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<Void> deleteWallet(@PathVariable Long id) {
        log.debug("REST request to delete Wallet : {}", id);

        Optional<Wallet> optionalWallet = walletRepository.findById(id);

        if(!optionalWallet.isPresent()){
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        Wallet wallet = optionalWallet.get();

        if(!userService.getCurrentUser().getId().equals(wallet.getOwner().getId())){
            throw new BadRequestAlertException("You not have permission", ENTITY_NAME, "unauthorize");
        }

        List<Wallet> currentUserWallets = walletRepository.findByOwnerIsCurrentUser();
        if(currentUserWallets.size() == 1){
            throw new BadRequestAlertException("You have only one wallet", ENTITY_NAME, "onlyonewallet");
        }

        walletRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * POST  /wallets/balance/:id : the balance for wallet with "id".
     *
     * @param id the id of the wallet to get balance
     * @return the ResponseEntity with status 200 (OK)
     */
    @PostMapping("/wallets/balance/{id}")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<ObjectNode> balanceWallet(@PathVariable Long id) {
        log.debug("REST request to get balance for Wallet : {}", id);

        Optional<Wallet> optionalWallet = walletRepository.findById(id);

        if(!optionalWallet.isPresent()){
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        Wallet wallet = optionalWallet.get();

        if(!userService.getCurrentUser().getId().equals(wallet.getOwner().getId())){
            throw new BadRequestAlertException("You not have permission", ENTITY_NAME, "unauthorize");
        }


        ObjectNode balance = wallet.getBalance(tokenRepository.findAll());

        return ResponseEntity.ok().body(balance);
    }
}
