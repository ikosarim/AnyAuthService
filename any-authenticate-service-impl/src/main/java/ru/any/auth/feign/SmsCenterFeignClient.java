package ru.any.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "sms-center-feign", url = "${sms.center.url}")
public interface SmsCenterFeignClient {

    @GetMapping
    ResponseEntity<String> sendSms(
            @RequestParam("login") String login,
            @RequestParam("psw") String psw,
            @RequestParam("phones") String phones,
            @RequestParam("mes") String mes
    );

    @GetMapping
    ResponseEntity<String> getSmsCost(
            @RequestParam("login") String login,
            @RequestParam("psw") String psw,
            @RequestParam("phones") String phones,
            @RequestParam("mes") String mes,
            @RequestParam("cost") String cost
    );
}


//https://smsc.ru/sys/send.php?login=<login>&psw=<password>&phones=<phones>&mes=<message>
//https://smsc.ru/sys/send.php?login=<login>&psw=<password>&phones=<phones>&mes=<message>&cost=1