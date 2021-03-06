package com.csdg1t3.ryverbankapi;

import java.util.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import javax.annotation.PostConstruct;

import com.csdg1t3.ryverbankapi.content.*;
import com.csdg1t3.ryverbankapi.user.*;
import com.csdg1t3.ryverbankapi.security.*;
import com.csdg1t3.ryverbankapi.trade.*;

@SpringBootApplication
@EnableScheduling
public class RyverbankApiApplication {
    
    // Set the application timezone as GMT+8 (Singapore)
    @PostConstruct
    public void init(){
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
    }
    
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(RyverbankApiApplication.class, args);
        initApplicationData(ctx);

    }

    public static void initApplicationData(ApplicationContext ctx) {
        

        UserRepository users = ctx.getBean(UserRepository.class);
        ContentRepository contents = ctx.getBean(ContentRepository.class);
        BCryptPasswordEncoder encoder = ctx.getBean(BCryptPasswordEncoder.class);
        PortfolioRepository portfolios = ctx.getBean(PortfolioRepository.class);
        MarketMaker marketMaker = ctx.getBean(MarketMaker.class);
        

        User admin = new User(null, "admin", "S1234567G", "81756529", "Lalaland 10, Potato's Dream, 10200", "manager_1", encoder.encode("01_manager_01"), "ROLE_MANAGER", true);
        User tstark = new User(null, "Tony Stark", "S8732269I", "81437586", "10880 Malibu Point, 90265", "iamironman", encoder.encode("i<3carmen"), "ROLE_USER", true);
        User tholland = new User(null, "Tom Holland", "S9847385E", "93580378", "20 Ingram Street", "spiderman", encoder.encode("mrstark,Idontfeels0good"), "ROLE_USER", true);
        User analyst1 = new User(null, "analyst 1", "S1234567D", "91234567", "no address", "analyst_1", encoder.encode("01_analyst_01"), "ROLE_ANALYST", true);
        User analyst2 = new User(null, "analyst 2", "S2331492D", "95671234", "no address", "analyst_2", encoder.encode("02_analyst_02"), "ROLE_ANALYST", true);

        // Add users
        System.out.println("[Add manager]: " + users.save(admin));
        System.out.println("[Add customer]: " + users.save(tstark));
        System.out.println("[Add customer]: " + users.save(tholland));
        System.out.println("[Add analyst]: " + users.save(analyst1));
        System.out.println("[Add analyst]: " + users.save(analyst2));

        // Add portfolios
        System.out.println("Adding portfolio for stark");
        portfolios.save(new Portfolio(null, tstark.getId(),tstark, null, 0, 0));
        System.out.println("Adding portfolio for tholland");
        portfolios.save(new Portfolio(null, tholland.getId(), tholland, null, 0, 0));
        
        // Add content
        contents.save(new Content(null, "Title1", "Summary1", "Content1", "Link1", true));
        contents.save(new Content(null, "Title2", "Summary2", "Content2", "Link2", false));
        
        // Add stocks and market maker trades
        marketMaker.initMarket();
    }

    
}
