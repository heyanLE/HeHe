package com.heyanle.hehehe.helper;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by HeYanLe on 2021/5/30 20:54.
 * https://github.com/heyanLE
 */
@Service
public class JWTHelperImpl implements JWTHelper{

    private final String secret = "3E8419CBE8419CB71CA2A76371CA2A76";
    private final String issuer = "hehehe_server";

    @Override
    public String getUsername(String token) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("username").asString();
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }

    }

    @Override
    public String sign(String username, Long time) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        Date now = new Date();
        Date expires = new Date(now.getTime() + EXPIRATION_DATA);
        return JWT.create()
                .withIssuer(issuer)
                .withIssuedAt(new Date())
                .withExpiresAt(expires)
                .withClaim("username", username)
                .sign(algorithm);
    }

    @Override
    public Boolean verity(String token, String username) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("username").asString().equals(username);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
