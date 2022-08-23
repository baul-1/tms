package com.icode.toonmanger;

import ch.vorburger.mariadb4j.springframework.MariaDB4jSpringService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.net.ConnectException;
import java.net.Socket;

@SpringBootApplication
public class ToonmangerApplication {

    @Autowired
    Environment env;

    private static final Logger log = LogManager.getLogger(ToonmangerApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(ToonmangerApplication.class, args);

    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx){

        return  new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                String isMysqlActive = env.getProperty("mariaDB4j.isMysqlActive");
                String initSchema    = env.getProperty("mariaDB4j.initSchema");
                String initData      = env.getProperty("mariaDB4j.initData");
                String host          = env.getProperty("mariaDB4j.host");
                String dataDir       = env.getProperty("mariaDB4j.dataDir");
                int port             = Integer.parseInt(env.getProperty("mariaDB4j.port").trim()) ;

                log.info("isMysqlActive : " + isMysqlActive);
                log.info("initSchema : " + initSchema);
                log.info("initData : " + initData);
                log.info("host : " + host);
                log.info("dataDir : " + dataDir);


                if("Y".equals(isMysqlActive)) {

                    MariaDB4jSpringService DB ;

                    try {
                        (new Socket(host, port)).close();

                    }catch(ConnectException e) {

                        DB = new MariaDB4jSpringService();

                        DB.setDefaultDataDir(dataDir);
                        DB.setDefaultPort(port);
                        DB.start();

                        DB.getDB().source(initSchema);
                        DB.getDB().source(initData);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }


            }
        };
    }



}
