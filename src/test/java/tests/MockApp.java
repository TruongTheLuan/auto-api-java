package tests;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class MockApp {
    public static void main(String[] args){
        WireMockServer refDataServer = new WireMockServer(options()
                .port(7777).notifier(new ConsoleNotifier(true))); //No-args constructor will start on port 8080, no HTTPS
        refDataServer.start();

        WireMockServer cardDataServer = new WireMockServer(options()
                .port(7778).notifier(new ConsoleNotifier(true))); //No-args constructor will start on port 8080, no HTTPS
        cardDataServer.start();
    }
}
