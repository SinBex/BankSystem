package com.bank.controller;


import com.bank.pojo.BankWithdraw;
import com.bank.service.BankWithdrawService;
import com.bank.utils.BankResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BankWithdrawController {

    @Autowired
    private BankWithdrawService bankWithdrawService;

    @RequestMapping(value = "/insert.do")
    @ResponseBody
    public BankResult insert(BankWithdraw bankWithdraw){
        return bankWithdrawService.insert(bankWithdraw);
    }

    @RequestMapping(value = "/{account}")
    @ResponseBody
    public BankResult getBankDepositFromAccount(@PathVariable("account") String account){
        return bankWithdrawService.getBankWithdrawByAccount(account);
    }
}