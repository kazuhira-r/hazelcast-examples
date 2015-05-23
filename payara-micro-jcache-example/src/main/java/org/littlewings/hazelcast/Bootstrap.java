package org.littlewings.hazelcast;

import java.io.File;

import fish.payara.micro.BootstrapException;
import fish.payara.micro.PayaraMicro;

public class Bootstrap {
    public static void main(String... args) throws BootstrapException {
        PayaraMicro
                .getInstance()
                .addDeployment("target/payara-micro-jcache-example.war")
                .bootStrap();
    }
}
