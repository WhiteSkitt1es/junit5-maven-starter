package com.junit;

import com.junit.service.UserService;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.io.PrintWriter;

public class TestLauncher {
    public static void main(String[] args) {
        Launcher launcher = LauncherFactory.create();

        SummaryGeneratingListener summaryGeneratingListener = new SummaryGeneratingListener();

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder
                .request()
//                .selectors(DiscoverySelectors.selectClass(UserService.class))
                .selectors(DiscoverySelectors.selectPackage("com.junit.service"))
                .build();
        launcher.execute(request, summaryGeneratingListener);

        try(PrintWriter writer = new PrintWriter(System.out)){
            summaryGeneratingListener.getSummary().printTo(writer);
        }

    }
}
