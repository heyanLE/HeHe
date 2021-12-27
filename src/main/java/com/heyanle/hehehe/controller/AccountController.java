package com.heyanle.hehehe.controller;

import com.heyanle.hehehe.controller.info.LoginInfo;
import com.heyanle.hehehe.controller.info.RegisterInfo;
import com.heyanle.hehehe.entity.Account;
import com.heyanle.hehehe.entity.HttpResponse;
import com.heyanle.hehehe.helper.JWTHelper;
import com.heyanle.hehehe.helper.SHAHelper;
import com.heyanle.hehehe.repository.AccountRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Created by HeYanLe on 2021/5/30 20:35.
 * https://github.com/heyanLE
 */
@RestController
@RequestMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {

    private final AccountRepository accountRepository;
    private final JWTHelper jwtHelper;
    private final SHAHelper shaHelper;

    public AccountController(AccountRepository accountRepository, JWTHelper jwtHelper, SHAHelper shaHelper) {
        this.accountRepository = accountRepository;
        this.jwtHelper = jwtHelper;
        this.shaHelper = shaHelper;
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public HttpResponse<Account> login(
            @RequestBody LoginInfo loginInfo
    ){
        Optional<Account> o = accountRepository.findFirstByUsername(loginInfo.getUsername());
        if(o.isPresent()){
            Account account = o.get();
            String password = shaHelper.getSHA256Str(loginInfo.getPassword());
            if(password.equals(account.getPassword())){
                String token = jwtHelper.sign(loginInfo.getUsername(), JWTHelper.EXPIRATION_DATA);
                account.setToken(token);
                return HttpResponse.withData(200, "Login completely", account);
            }else{
                return HttpResponse.withoutData(400, "The username and the password do not match");
            }
        }else{
            return HttpResponse.withoutData(401, "The username and the password do not match");
        }
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseBody
    public HttpResponse<String> register(
            @RequestBody RegisterInfo registerInfo
    ){
        Optional<Account> o = accountRepository.findFirstByUsername(registerInfo.getUsername());
        if(o.isPresent()){
            return HttpResponse.withoutData(400, "username exist!");
        }
        Account account = new Account();
        account.setUsername(registerInfo.getUsername());
        account.setPassword(shaHelper.getSHA256Str(registerInfo.getPassword()));
        //account.setPassword("");
        account.setCreateTime(System.currentTimeMillis());
        accountRepository.save(account);
        return HttpResponse.withoutData(200, "register completely");
    }

}
