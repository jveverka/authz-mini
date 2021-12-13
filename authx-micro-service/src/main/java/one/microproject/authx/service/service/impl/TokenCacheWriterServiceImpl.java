package one.microproject.authx.service.service.impl;

import one.microproject.authx.service.model.CachedToken;
import one.microproject.authx.service.repository.CacheTokenRepository;
import one.microproject.authx.service.service.TokenCacheWriterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.cert.X509Certificate;

import static one.microproject.authx.common.utils.CryptoUtils.serializeX509Certificate;
import static one.microproject.authx.common.utils.ServiceUtils.createId;

@Service
@Transactional(readOnly = true)
public class TokenCacheWriterServiceImpl implements TokenCacheWriterService {

    private final CacheTokenRepository cacheTokenRepository;

    @Autowired
    public TokenCacheWriterServiceImpl(CacheTokenRepository cacheTokenRepository) {
        this.cacheTokenRepository = cacheTokenRepository;
    }

    @Override
    @Transactional
    public void saveIssuedToken(String projectId, String jti, String token, String kid, X509Certificate certificate) {
        String id = createId(projectId, jti);
        CachedToken cachedToken = new CachedToken(id, token, kid, serializeX509Certificate(certificate));
        cacheTokenRepository.save(cachedToken);
    }

    @Override
    @Transactional
    public void removeToken(String projectId, String jti) {
        String id = createId(projectId, jti);
        cacheTokenRepository.deleteById(id);
    }

}
