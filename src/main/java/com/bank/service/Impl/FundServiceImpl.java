package com.bank.service.Impl;

import com.bank.mapper.BankAccountMapper;
import com.bank.mapper.BankCustomerMapper;
import com.bank.mapper.BankFundLogMapper;
import com.bank.mapper.BankFundProductMapper;
import com.bank.pojo.*;
import com.bank.service.FundService;
import com.bank.utils.BankResult;
import com.bank.utils.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FundServiceImpl implements FundService {

    @Autowired
    private BankFundProductMapper bankFundProductMapper;
    @Autowired
    private BankFundLogMapper bankFundLogMapper;
    @Autowired
    private BankCustomerMapper bankCustomerMapper;
    @Autowired
    private BankAccountMapper bankAccountMapper;

    private long datacenterId = 6L;  //数据中心
    private long machineId ;     //机器标识

    @Override
    public String createFundProduct(String type, double purchase_rate, double net_asset_value, double redemption_rate) {
        machineId = 1L;
        SnowFlake snowFlake = new SnowFlake(datacenterId,machineId);
        String snowFlakeId = String.valueOf(snowFlake.nextId());
        String fundId = snowFlakeId.substring(snowFlakeId.length() - 9, snowFlakeId.length() - 1);
        BankFundProduct bankFundProduct = new BankFundProduct();
        bankFundProduct.setFundId(fundId);
        bankFundProduct.setType(type);
        bankFundProduct.setPurchaseRate(purchase_rate);
        bankFundProduct.setNetAssetValue(net_asset_value);
        bankFundProduct.setRedemptionRate(redemption_rate);
        bankFundProduct.setPurchaseDate(String.valueOf(System.currentTimeMillis()));
        bankFundProductMapper.insert(bankFundProduct);
        return fundId;
    }

    @Override
    public List<BankFundProduct> getFundProducts() {
        BankFundProductExample bankFundProductExample = new BankFundProductExample();
        BankFundProductExample.Criteria criteria = bankFundProductExample.createCriteria();
        criteria.andFundIdIsNotNull();
        List<BankFundProduct> bankFundProductList = bankFundProductMapper.selectByExample(bankFundProductExample);
        return bankFundProductList;
    }

    @Override
    public BankResult createFundTx(String custId, String account, String fundId, String type, double amount) {
        machineId = 2L;

        BankCustomer bankCustomer = bankCustomerMapper.selectByPrimaryKey(custId);
        if (bankCustomer == null) return BankResult.build(200, "Request Failed", "Customer not exist!");

        BankAccount bankAccount = bankAccountMapper.selectByPrimaryKey(account);
        if (bankAccount == null) return BankResult.build(200, "Request Failed", "Account not exist!");

        if (!bankAccount.getCustId().equals(custId)) return BankResult.build(200, "Request Failed", "Account not correspond to customer!");

        if (bankAccount.getBalances() < amount) return BankResult.build(200, "Request Failed", "Insufficient balance!");

        SnowFlake snowFlake = new SnowFlake(datacenterId, machineId);
        long fundTxId = snowFlake.nextId();

        BankFundProductExample bankFundProductExample = new BankFundProductExample();
        BankFundProductExample.Criteria criteria = bankFundProductExample.createCriteria();
        criteria.andFundIdEqualTo(fundId);
        List<BankFundProduct> bankFundProductList = bankFundProductMapper.selectByExample(bankFundProductExample);

        BankFundLog bankFundLog = new BankFundLog();
        bankFundLog.setFundTxId(fundTxId);
        bankFundLog.setCustId(custId);
        bankFundLog.setAccount(account);
        bankFundLog.setFundId(fundId);
        bankFundLog.setType(type);
        bankFundLog.setAmount(amount);
        // TODO:填写份额计算公式
        double share = 1000;
        bankFundLog.setShare(share);
        bankFundLog.setTxDate(String.valueOf(System.currentTimeMillis()));
        bankFundLog.setReviewId("Carrie");

        bankFundLogMapper.insert(bankFundLog);
        return BankResult.ok();
    }
}
