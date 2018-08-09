package com.allcode.nursetoken.web.rest;

import com.allcode.nursetoken.NursebackApp;

import com.allcode.nursetoken.domain.Wallet;
import com.allcode.nursetoken.domain.User;
import com.allcode.nursetoken.repository.WalletRepository;
import com.allcode.nursetoken.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;


import static com.allcode.nursetoken.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the WalletResource REST controller.
 *
 * @see WalletResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NursebackApp.class)
public class WalletResourceIntTest {

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_ENCRIPTED_PRIVATE_KEY = "AAAAAAAAAA";
    private static final String UPDATED_ENCRIPTED_PRIVATE_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_ENCRIPTED_PUBLIC_KEY = "AAAAAAAAAA";
    private static final String UPDATED_ENCRIPTED_PUBLIC_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_ENCRIPTED_PUBLIC_KEY_HASH = "AAAAAAAAAA";
    private static final String UPDATED_ENCRIPTED_PUBLIC_KEY_HASH = "BBBBBBBBBB";

    private static final String DEFAULT_ENCRIPTED_WIF = "AAAAAAAAAA";
    private static final String UPDATED_ENCRIPTED_WIF = "BBBBBBBBBB";

    @Autowired
    private WalletRepository walletRepository;


    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restWalletMockMvc;

    private Wallet wallet;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final WalletResource walletResource = new WalletResource(walletRepository);
        this.restWalletMockMvc = MockMvcBuilders.standaloneSetup(walletResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Wallet createEntity(EntityManager em) {
        Wallet wallet = new Wallet()
            .address(DEFAULT_ADDRESS)
            .encriptedPrivateKey(DEFAULT_ENCRIPTED_PRIVATE_KEY)
            .encriptedPublicKey(DEFAULT_ENCRIPTED_PUBLIC_KEY)
            .encriptedPublicKeyHash(DEFAULT_ENCRIPTED_PUBLIC_KEY_HASH)
            .encriptedWif(DEFAULT_ENCRIPTED_WIF);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        wallet.setOwner(user);
        return wallet;
    }

    @Before
    public void initTest() {
        wallet = createEntity(em);
    }

    @Test
    @Transactional
    public void createWallet() throws Exception {
        int databaseSizeBeforeCreate = walletRepository.findAll().size();

        // Create the Wallet
        restWalletMockMvc.perform(post("/api/wallets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(wallet)))
            .andExpect(status().isCreated());

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeCreate + 1);
        Wallet testWallet = walletList.get(walletList.size() - 1);
        assertThat(testWallet.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testWallet.getEncriptedPrivateKey()).isEqualTo(DEFAULT_ENCRIPTED_PRIVATE_KEY);
        assertThat(testWallet.getEncriptedPublicKey()).isEqualTo(DEFAULT_ENCRIPTED_PUBLIC_KEY);
        assertThat(testWallet.getEncriptedPublicKeyHash()).isEqualTo(DEFAULT_ENCRIPTED_PUBLIC_KEY_HASH);
        assertThat(testWallet.getEncriptedWif()).isEqualTo(DEFAULT_ENCRIPTED_WIF);
    }

    @Test
    @Transactional
    public void createWalletWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = walletRepository.findAll().size();

        // Create the Wallet with an existing ID
        wallet.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restWalletMockMvc.perform(post("/api/wallets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(wallet)))
            .andExpect(status().isBadRequest());

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkAddressIsRequired() throws Exception {
        int databaseSizeBeforeTest = walletRepository.findAll().size();
        // set the field null
        wallet.setAddress(null);

        // Create the Wallet, which fails.

        restWalletMockMvc.perform(post("/api/wallets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(wallet)))
            .andExpect(status().isBadRequest());

        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEncriptedPrivateKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = walletRepository.findAll().size();
        // set the field null
        wallet.setEncriptedPrivateKey(null);

        // Create the Wallet, which fails.

        restWalletMockMvc.perform(post("/api/wallets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(wallet)))
            .andExpect(status().isBadRequest());

        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEncriptedPublicKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = walletRepository.findAll().size();
        // set the field null
        wallet.setEncriptedPublicKey(null);

        // Create the Wallet, which fails.

        restWalletMockMvc.perform(post("/api/wallets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(wallet)))
            .andExpect(status().isBadRequest());

        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEncriptedPublicKeyHashIsRequired() throws Exception {
        int databaseSizeBeforeTest = walletRepository.findAll().size();
        // set the field null
        wallet.setEncriptedPublicKeyHash(null);

        // Create the Wallet, which fails.

        restWalletMockMvc.perform(post("/api/wallets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(wallet)))
            .andExpect(status().isBadRequest());

        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEncriptedWifIsRequired() throws Exception {
        int databaseSizeBeforeTest = walletRepository.findAll().size();
        // set the field null
        wallet.setEncriptedWif(null);

        // Create the Wallet, which fails.

        restWalletMockMvc.perform(post("/api/wallets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(wallet)))
            .andExpect(status().isBadRequest());

        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWallets() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList
        restWalletMockMvc.perform(get("/api/wallets?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wallet.getId().intValue())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].encriptedEncriptedPrivateKey").value(hasItem(DEFAULT_ENCRIPTED_PRIVATE_KEY.toString())))
            .andExpect(jsonPath("$.[*].encriptedEncriptedPublicKey").value(hasItem(DEFAULT_ENCRIPTED_PUBLIC_KEY.toString())))
            .andExpect(jsonPath("$.[*].encriptedEncriptedPublicKeyHash").value(hasItem(DEFAULT_ENCRIPTED_PUBLIC_KEY_HASH.toString())))
            .andExpect(jsonPath("$.[*].encriptedWif").value(hasItem(DEFAULT_ENCRIPTED_WIF.toString())));
    }


    @Test
    @Transactional
    public void getWallet() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get the wallet
        restWalletMockMvc.perform(get("/api/wallets/{id}", wallet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(wallet.getId().intValue()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS.toString()))
            .andExpect(jsonPath("$.encriptedEncriptedPrivateKey").value(DEFAULT_ENCRIPTED_PRIVATE_KEY.toString()))
            .andExpect(jsonPath("$.encriptedEncriptedPublicKey").value(DEFAULT_ENCRIPTED_PUBLIC_KEY.toString()))
            .andExpect(jsonPath("$.encriptedEncriptedPublicKeyHash").value(DEFAULT_ENCRIPTED_PUBLIC_KEY_HASH.toString()))
            .andExpect(jsonPath("$.encriptedWif").value(DEFAULT_ENCRIPTED_WIF.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingWallet() throws Exception {
        // Get the wallet
        restWalletMockMvc.perform(get("/api/wallets/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWallet() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        int databaseSizeBeforeUpdate = walletRepository.findAll().size();

        // Update the wallet
        Wallet updatedWallet = walletRepository.findById(wallet.getId()).get();
        // Disconnect from session so that the updates on updatedWallet are not directly saved in db
        em.detach(updatedWallet);
        updatedWallet
            .address(UPDATED_ADDRESS)
            .encriptedPrivateKey(UPDATED_ENCRIPTED_PRIVATE_KEY)
            .encriptedPublicKey(UPDATED_ENCRIPTED_PUBLIC_KEY)
            .encriptedPublicKeyHash(UPDATED_ENCRIPTED_PUBLIC_KEY_HASH)
            .encriptedWif(UPDATED_ENCRIPTED_WIF);

        restWalletMockMvc.perform(put("/api/wallets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedWallet)))
            .andExpect(status().isOk());

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
        Wallet testWallet = walletList.get(walletList.size() - 1);
        assertThat(testWallet.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testWallet.getEncriptedPrivateKey()).isEqualTo(UPDATED_ENCRIPTED_PRIVATE_KEY);
        assertThat(testWallet.getEncriptedPublicKey()).isEqualTo(UPDATED_ENCRIPTED_PUBLIC_KEY);
        assertThat(testWallet.getEncriptedPublicKeyHash()).isEqualTo(UPDATED_ENCRIPTED_PUBLIC_KEY_HASH);
        assertThat(testWallet.getEncriptedWif()).isEqualTo(UPDATED_ENCRIPTED_WIF);
    }

    @Test
    @Transactional
    public void updateNonExistingWallet() throws Exception {
        int databaseSizeBeforeUpdate = walletRepository.findAll().size();

        // Create the Wallet

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restWalletMockMvc.perform(put("/api/wallets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(wallet)))
            .andExpect(status().isBadRequest());

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteWallet() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        int databaseSizeBeforeDelete = walletRepository.findAll().size();

        // Get the wallet
        restWalletMockMvc.perform(delete("/api/wallets/{id}", wallet.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Wallet.class);
        Wallet wallet1 = new Wallet();
        wallet1.setId(1L);
        Wallet wallet2 = new Wallet();
        wallet2.setId(wallet1.getId());
        assertThat(wallet1).isEqualTo(wallet2);
        wallet2.setId(2L);
        assertThat(wallet1).isNotEqualTo(wallet2);
        wallet1.setId(null);
        assertThat(wallet1).isNotEqualTo(wallet2);
    }
}
