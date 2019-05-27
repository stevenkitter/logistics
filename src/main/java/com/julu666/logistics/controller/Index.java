package com.julu666.logistics.controller;


import com.google.gson.Gson;
import com.julu666.logistics.http.Service;
import com.julu666.logistics.jpa.Company;
import com.julu666.logistics.jpa.Package;
import com.julu666.logistics.jpa.Transit;
import com.julu666.logistics.json.CNApiJSON;
import com.julu666.logistics.json.CNCompanyInfo;
import com.julu666.logistics.json.MainJson;
import com.julu666.logistics.json.PackageStatus;
import com.julu666.logistics.repository.CompanyRepository;
import com.julu666.logistics.repository.PackageRepository;
import com.julu666.logistics.repository.TransitRepository;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;

import java.math.BigInteger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Controller
public class Index {

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private TransitRepository transitRepository;

    private static final String url  = "https://h5api.m.taobao.com/h5/mtop.cnwireless.cnlogisticdetailservice.wapquerylogisticpackagebymailno/1.0/?jsv=2.4.2&appKey=12574478&t=1558689823624&sign=a4a07c85486c6d429015818e430748e7&api=mtop.cnwireless.CNLogisticDetailService.wapqueryLogisticPackageByMailNo&AntiCreep=true&v=1.0&timeout=5000&type=originaljson&dataType=json&c=066b300b1b1d4cf8c081cc0e6de2c56e_1558693104754%3B1ba7e340e2a388bb022c62fe7568e10f&data=%7B%22mailNo%22%3A%22805979264794805297%22%2C%22cpCode%22%3A%22%22%7D";
    private static final String cookieUrl  = "https://h5api.m.taobao.com/h5/mtop.cnwireless.cncainiaoappservice.getlogisticscompanylist/1.0/?jsv=2.4.2&appKey=12574478&t=1558942991925&sign=e307e2e4d42a589720bda5113ba10aab&api=mtop.cnwireless.CNCainiaoAppService.getLogisticsCompanyList&v=1.0&AntiCreep=true&type=originaljson&dataType=json&c=984f66cd90ce2456af181ffe11915138_1558950171472%3B0c59af5606a0a1e3ff5d18a1649c1cd2&data=%7B%22version%22%3A0%2C%22cptype%22%3A%22all%22%7D";
    @GetMapping("/")
    public String index(HttpServletRequest request, Model model) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for(Cookie cookie : cookies) {
                if (cookie.getName().equals("code")){
                    String code = cookie.getValue();
                    model.addAttribute("code", code);
                }
            }
        }
        return "index";
    }


    @PostMapping("/query")
    public String query(HttpServletResponse response, @RequestParam("code")final String code, Model model) throws IOException {

        if (code.length() < 5) {
//            model.addAttribute("msg", "查询不到此物流信息");
            model.addAttribute("msg", "单号太短");
            return "index";
        }
        String newCode = code.replace(" ", "");
        Cookie cookie = new Cookie("code", newCode);
        response.addCookie(cookie);

        model.addAttribute("code", code);

        Optional<Package> optionalPackage = packageRepository.findByMailNo(code);
        if (optionalPackage.isPresent()) {
            //TODO 更新数据
            model.addAttribute("package", optionalPackage.get());
            new Thread(()->{
//                final String code1 = code;
                try {
                    CNApiJSON json = request(code);
                    saveToData(json);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
        } else {
            try {
                CNApiJSON
                        json = request(code);
                if (json.getData().getBuyerUserId() == null || json.getData().getBuyerUserId() < 0) {
                    model.addAttribute("msg", "爬虫数据异常");
                    return "index";
                }
                if (json.getRet().size() == 0) {
                    model.addAttribute("msg", "爬虫数据异常");
                    return "index";
                }
                String ret = json.getRet().get(0);
                String[] res = ret.split("::");
                if (res.length == 0) {
                    model.addAttribute("msg", "爬虫数据异常");
                    return "index";
                }
                if (!res[0].equals("SUCCESS")){
                    model.addAttribute("msg", res[1]);
                    return "index";
                } else {
                    //TODO 开线程干活 一边给页面值 一边写入数据库

                    Package p = jsonToPackage(json);
                    model.addAttribute("package", p);

                    new Thread(()->
                        saveToData(json)
                    ).start();
                }

            } catch (IOException ex){
                ex.printStackTrace();
            }
        }

        return  "index";
    }

    @GetMapping("/result")
    public String result() {
        return "index";
    }

    public CNApiJSON request(String code) throws IOException {

        String tokenStr = "";
        String tokenEnc = "";

        Pattern _m_h5_tk = Pattern.compile("\\b_m_h5_tk=([^;]+)");
        Matcher m =_m_h5_tk.matcher(Const.cookiess);
        if (m.find()) {
            tokenStr = m.group(0);
            String[] s = tokenStr.split("=");
            tokenStr = s[1];
        }

        Pattern _m_h5_tk_enc = Pattern.compile("\\b_m_h5_tk_enc=([^;]+)");
        Matcher mm =_m_h5_tk_enc.matcher(Const.cookiess);
        if (mm.find()) {
            tokenEnc = mm.group(0);
            String[] s = tokenEnc.split("=");
            tokenEnc = s[1];
        }

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://h5api.m.taobao.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        Service service = retrofit.create(Service.class);
        Map<String, String> param = new HashMap<>();

        String t = System.currentTimeMillis() + "";
        MainJson mainJson = new MainJson();
        mainJson.setMailNo(code);
        mainJson.setCpCode("");
        Gson j = new Gson();
        String mailJ = j.toJson(mainJson);

        String[] tks = tokenStr.split("_");

        String appKey = "12574478";
        String sign = sign(tks[0], t, appKey, mailJ);

        param.put("jsv", "2.4.2");
        param.put("appKey", appKey);
        param.put("t", t + "");
        param.put("sign", sign);
        param.put("api", "mtop.cnwireless.CNLogisticDetailService.wapqueryLogisticPackageByMailNo");
        param.put("AntiCreep", "true");
        param.put("v", "1.0");
        param.put("timeout", "5000");
        param.put("type", "originaljson");
        param.put("dataType", "json");
        param.put("c", tokenStr + ";" + tokenEnc);

        param.put("data", mailJ);
        Call<CNApiJSON> call = service.getRepo(param);
        CNApiJSON api = call.execute().body();
        return api;
    }

    // 抓包签名
    private String sign(String token, String now ,String appKey, String data) {
        String signStr = token + "&" + now + "&" + appKey + "&" + data;
        MessageDigest md = null;
        try{
            md = MessageDigest.getInstance("MD5");
            md.update(signStr.getBytes());
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return new BigInteger(1, md.digest()).toString(16);
    }

    @Transactional
    public void saveToData(CNApiJSON json) {
        CNCompanyInfo c = json.getData().getCpCompanyInfo();
        Company company = companyRepository.findByCompanyName(c.getCompanyName()).orElse(new Company());

        company.setCompanyCode(c.getCompanyCode());
        company.setCompanyName(c.getCompanyName());
        company.setIconUrl16x16(c.getIconUrl16x16());
        company.setIconUrl36x36(c.getIconUrl36x36());
        company.setIconUrl102x38(c.getIconUrl102x38());
        company.setPinyin(c.getPinyin());
        company.setServiceTel(c.getServiceTel());
        company.setWebUrl(c.getWebUrl());
        companyRepository.save(company);


        PackageStatus s = json.getData().getPackageStatus();

        Package p = packageRepository.findByMailNo(json.getData().getMailNo()).orElse(new Package());
        p.setDepartureName(s.getDepartureName());
        p.setDescription(s.getDesc());
        p.setDestinationName(s.getDestinationName());
        p.setNewStatusCode(s.getNewStatusCode());
        p.setNewStatusDesc(s.getNewStatusDesc());
        p.setProgressbar(s.getProgressbar());
        p.setStatus(s.getStatus());
        p.setStatusCode(s.getStatusCode());
        p.setMailNo(json.getData().getMailNo());
        p.setCompanyId(company.getId());
        packageRepository.save(p);



        for (com.julu666.logistics.json.Transit jsonTransit : json.getData().getTransitList()) {
            Transit t = transitRepository.findByTime(jsonTransit.getTime()).orElse(new Transit());
            t.setAction(jsonTransit.getAction());
            t.setMessage(jsonTransit.getMessage());
            t.setSectionIcon(jsonTransit.getSectionIcon());
            t.setSectionName(jsonTransit.getSectionName());
            t.setTime(jsonTransit.getTime());
            t.setMailId(p.getId());
            transitRepository.save(t);
        }
    }

    public Package jsonToPackage(CNApiJSON json) {
        Package p = new Package();
        p.setDepartureName(json.getData().getPackageStatus().getDepartureName());
        p.setDescription(json.getData().getPackageStatus().getDesc());
        p.setDestinationName(json.getData().getPackageStatus().getDestinationName());
        p.setNewStatusCode(json.getData().getPackageStatus().getNewStatusCode());
        p.setNewStatusDesc(json.getData().getPackageStatus().getNewStatusDesc());
        p.setProgressbar(json.getData().getPackageStatus().getProgressbar());
        p.setStatus(json.getData().getPackageStatus().getStatus());
        p.setStatusCode(json.getData().getPackageStatus().getStatusCode());

        Company company = new Company();
        company.setCompanyCode(json.getData().getCpCompanyInfo().getCompanyCode());
        company.setCompanyName(json.getData().getCpCompanyInfo().getCompanyName());
        company.setIconUrl16x16(json.getData().getCpCompanyInfo().getIconUrl16x16());
        company.setIconUrl36x36(json.getData().getCpCompanyInfo().getIconUrl36x36());
        company.setIconUrl102x38(json.getData().getCpCompanyInfo().getIconUrl102x38());
        company.setPinyin(json.getData().getCpCompanyInfo().getPinyin());
        company.setServiceTel(json.getData().getCpCompanyInfo().getServiceTel());
        company.setWebUrl(json.getData().getCpCompanyInfo().getWebUrl());

        p.setCompany(company);

        List<Transit> list = new ArrayList<>();
        for (com.julu666.logistics.json.Transit tr : json.getData().getTransitList()) {
            Transit t = new Transit();
            t.setAction(tr.getAction());
            t.setMessage(tr.getMessage());
            t.setSectionIcon(tr.getSectionIcon());
            t.setSectionName(tr.getSectionName());
            t.setTime(tr.getTime());
            list.add(t);
        }
        p.setTransits(list);
        return p;
    }


}

