package com.bettercloud.vault.api;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

import com.bettercloud.vault.response.VaultResponse;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;

/**
 * <p>Integration tests for the basic (i.e. "sys") Vault API operations.</p>
 *
 * <p>These tests require a Vault server to be up and running.  See the setup notes in
 * "src/test-integration/README.md".</p>
 *
 * <p>Unfortunately, it's not really possible to fully test these endpoints with the current integration testing
 * strategy.  The "generic" backend, used by the dev server, does not issue leases at all.  You CAN obtain leases
 * by using the PKI backend, but those aren't renewable:</p>
 *
 * <p>https://github.com/hashicorp/vault/issues/877</p>
 *
 * <p>Therefore, these revocation tests are basically just testing that the Vault server returns a 204 status
 * code.  Which isn't much of a test, since Vault routinely returns 204 even if you pass a non-existent lease ID.</p>
 *
 * <p>In the future, we may be shifting to an integration testing approach that uses a "real" Vault server
 * instance, running in a Docker container (see: https://github.com/BetterCloud/vault-java-driver/pull/25).  At
 * that time, these tests should be re-visited and better implemented.</p>
 */
public class LeasesTests {

    private static final String address = System.getProperty("VAULT_ADDR");
    private static final String token = System.getProperty("VAULT_TOKEN");

    private Vault vault;

    @BeforeClass
    public static void verifyEnv() {
        assertNotNull(address);
        assertNotNull(token);
    }

    @Before
    public void setup() throws VaultException {
        final VaultConfig config = new VaultConfig(address, token);
        vault = new Vault(config);
    }

    @Test
    public void testRevoke() throws VaultException {
        final VaultResponse response = vault.leases().revoke("sys/revoke-prefix/dummy");
        assertEquals(204, response.getRestResponse().getStatus());
    }

    @Test
    public void testRevokePrefix() throws VaultException {
        final VaultResponse response = vault.leases().revokePrefix("sys/revoke-prefix/dummy");
        assertEquals(204, response.getRestResponse().getStatus());
    }

    @Test
    public void testRevokeForce() throws VaultException {
        final VaultResponse response = vault.leases().revokeForce("sys/revoke-prefix/dummy");
        assertEquals(204, response.getRestResponse().getStatus());
    }

}