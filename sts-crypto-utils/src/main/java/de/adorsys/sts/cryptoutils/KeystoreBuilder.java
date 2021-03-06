package de.adorsys.sts.cryptoutils;

import javax.security.auth.callback.CallbackHandler;
import java.io.IOException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

public class KeystoreBuilder {
    private KeyStoreType storeType;
	private String storeId;
	private Map<String, KeyEntry> keyEntries = new HashMap<>();
	
	public KeystoreBuilder withStoreType(KeyStoreType storeType) {
		this.storeType = storeType;
		return this;
	}
	public KeystoreBuilder withStoreId(String storeId) {
		this.storeId = storeId;
		return this;
	}
	public KeystoreBuilder withKeyEntry(KeyEntry keyEntry) {
		this.keyEntries.put(keyEntry.getAlias(), keyEntry);
		return this;
	}
	
	public byte[] build(CallbackHandler storePassSrc) throws IOException, NoSuchAlgorithmException, CertificateException {
		KeyStore ks = KeyStoreService.newKeyStore(storeType);
		KeyStoreService.fillKeyStore(ks, keyEntries.values());

		return KeyStoreService.toByteArray(ks, storeId, storePassSrc);
	}

	public KeyStore build() throws IOException, NoSuchAlgorithmException, CertificateException{
		KeyStore ks = KeyStoreService.newKeyStore(storeType);
		KeyStoreService.fillKeyStore(ks, keyEntries.values());

		return ks;
	}
}
